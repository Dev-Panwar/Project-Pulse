package dev.panwar.projectpulse.activities
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import dev.panwar.projectpulse.R
import dev.panwar.projectpulse.databinding.ActivityMyProfileBinding
import dev.panwar.projectpulse.firebase.FireStoreClass
import dev.panwar.projectpulse.models.User
import dev.panwar.projectpulse.utils.Constants
import java.io.IOException

class MyProfileActivity : BaseActivity() {

    companion object{
        private const val READ_STORAGE_PERMISSION_CODE=1
        private const val PICK_IMAGE_REQUEST_CODE=2
    }

    private var binding:ActivityMyProfileBinding?=null
    private var mSelectedImageFileUri:Uri?=null
    private var mProfileImageUrl:String=""
    private lateinit var mUserDetails:User

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyProfileBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        setupActionBar()
        FireStoreClass().loadUserData(this)

//        implementing the
        binding?.ivProfileUserImage?.setOnClickListener {
//            first checking the permission to access external storage or not
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
                  showImageChooser()
            }
//            asking for storage permission...This time not using dexter library...using the default method
            else{
                ActivityCompat.requestPermissions(this,arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE), READ_STORAGE_PERMISSION_CODE)
            }
        }


        binding?.btnUpdate?.setOnClickListener {
            if (mSelectedImageFileUri!=null){
                uploadUserImage()
            }
            else{
                showProgressDialog(resources.getString(R.string.please_wait))
                updateUserProfileData()
            }
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK && requestCode == PICK_IMAGE_REQUEST_CODE && data!!.data !=null){
//            now we have Uri of image
            mSelectedImageFileUri = data.data

            try {
//                finally adding image to ivProfileUserImage
                binding?.ivProfileUserImage?.let {
                    Glide
                        .with(this)
                        .load(mSelectedImageFileUri)
                        .centerCrop()
                        .placeholder(R.drawable.ic_user_place_holder)
                        .into(it)
                }
            }catch (e: IOException){
                e.printStackTrace()
            }
        }
    }


    private fun setupActionBar(){
        setSupportActionBar(binding?.toolbarMyProfileActivity)
        val actionBar= supportActionBar
        if (actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_profile_back)
            actionBar.title=resources.getString(R.string.my_profile_title)
        }

        binding?.toolbarMyProfileActivity?.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    fun setUserDataInUI(user:User){
//        for setting up the image in MyProfile Ui
//        line 39 is additional line required as Glide requires Imageview in into() but we have circle imageview
//        Storing the user in mUserDetails
        mUserDetails=user
        binding?.ivProfileUserImage?.let {
            Glide
                .with(this)
                .load(user.image)
                .centerCrop()
                .placeholder(R.drawable.ic_user_place_holder)
                .into(it)
        }
        binding?.etName?.setText(user.name)
        binding?.etEmail?.setText(user.email)
//        if no mobile number added we have default mobile number as 0 in User data class.
        if (user.mobile !=0L){
//            if mobile no. is not equal to 0 add...the mobile number stored in FireStore Class
            binding?.etMobile?.setText(user.mobile.toString())
        }

    }

//    this inbuilt function will be called automatically after the Permission is granted/or denied...That's why we gave the REQUEST_CODE
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

    if (requestCode == READ_STORAGE_PERMISSION_CODE){
//        grant result store the PERMISSION granted or denied information of all requested permission
//        checking if the permission granted
        if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            showImageChooser()
        }else{
            Toast.makeText(this,"Oops You Just Denied the Permission for Storage. You can allow it from Settings",Toast.LENGTH_SHORT).show()
        }
    }

    }

//    for showing image chooser
    private fun showImageChooser(){
//        making a gallery intent with type Pick as we wanna pick something i.e. image
        val galleryIntent=Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(galleryIntent, PICK_IMAGE_REQUEST_CODE)
    }

//    function for storing our image in storage in firebase
    private fun uploadUserImage(){
        showProgressDialog(resources.getString(R.string.please_wait))

        if (mSelectedImageFileUri !=null){
//            this is our storage reference....inside child we write the name of the image we want to use to store in firebase storage
            val sref:StorageReference=FirebaseStorage.getInstance().reference.child("USER_IMAGE"+System.currentTimeMillis()+"."+getFileExtension(mSelectedImageFileUri))

            sref.putFile(mSelectedImageFileUri!!).addOnSuccessListener {
                taskSnapshot ->
                    Log.i("Firebase Image URL", taskSnapshot.metadata!!.reference!!.downloadUrl.toString())

                taskSnapshot.metadata!!.reference!!.downloadUrl.addOnSuccessListener {
                    uri ->
                    Log.i("Downloadable Image URL",uri.toString())
                    mProfileImageUrl=uri.toString()
                    updateUserProfileData()
                }
            }.addOnFailureListener {
                exception ->
                Toast.makeText(this, exception.message,Toast.LENGTH_SHORT).show()
                hideProgressDialogue()
            }
        }
    }

//    to get file extension from link...as we want only .png, .jpeg files to be stored in firebase storage
    private fun getFileExtension(uri: Uri?):String?{
        return MimeTypeMap.getSingleton().getExtensionFromMimeType(contentResolver.getType(uri!!))
    }

   private fun updateUserProfileData(){
        val userHashMap = HashMap<String,Any>()
        var anyChangesMade=false
//        if current image and Image in Database if different then only we update
            if (mProfileImageUrl.isNotEmpty() && mProfileImageUrl!=mUserDetails.image) {
//            this is the url we get when we upload image from  our device to firebase storage
            userHashMap[Constants.IMAGE] = mProfileImageUrl
                anyChangesMade = true
        }
            if (binding?.etName?.text.toString() != mUserDetails.name){
                userHashMap[Constants.NAME] = binding?.etName?.text.toString()
                anyChangesMade=true
            }

            if (binding?.etMobile?.text.toString() != mUserDetails.mobile.toString()){
                userHashMap[Constants.MOBILE] = binding?.etMobile?.text.toString().toLong()
                anyChangesMade=true
            }

            if (anyChangesMade){
                FireStoreClass().updateUserProfileData(this,userHashMap)
            }


    }


//    this will will called  profile Update Success after update button is clicked
     fun profileUpdateSuccess(){
        hideProgressDialogue()
        finish()
    }

}