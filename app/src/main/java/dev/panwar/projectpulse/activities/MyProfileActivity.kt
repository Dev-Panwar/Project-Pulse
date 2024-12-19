package dev.panwar.projectpulse.activities
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
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

        binding?.ivProfileUserImage?.setOnClickListener {
            // Checking permissions dynamically based on Android version
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) { // Android 13 and above
                if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_MEDIA_IMAGES) == PackageManager.PERMISSION_GRANTED) {
                    Constants.showImageChooser(this)
                } else {
                    ActivityCompat.requestPermissions(
                        this,
                        arrayOf(android.Manifest.permission.READ_MEDIA_IMAGES),
                        Constants.READ_STORAGE_PERMISSION_CODE
                    )
                }
            } else { // Android 12 and below
                if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    Constants.showImageChooser(this)
                } else {
                    ActivityCompat.requestPermissions(
                        this,
                        arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
                        Constants.READ_STORAGE_PERMISSION_CODE
                    )
                }
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

        if (resultCode == Activity.RESULT_OK && requestCode == Constants.PICK_IMAGE_REQUEST_CODE && data!!.data !=null){
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
//        Storing the user in mUserDetails so that we can use it later in other functions without calling FireStore class again
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

    // This inbuilt function will be called automatically after the permission is granted/denied
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == Constants.READ_STORAGE_PERMISSION_CODE) {
            // Grant result stores the PERMISSION granted or denied information of all requested permissions
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Check the specific permission granted
                if ((Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
                            permissions.contains(android.Manifest.permission.READ_MEDIA_IMAGES)) ||
                    (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU &&
                            permissions.contains(android.Manifest.permission.READ_EXTERNAL_STORAGE))) {
                    Constants.showImageChooser(this)
                }
            } else {
                Toast.makeText(
                    this,
                    "Oops! You just denied the permission for storage. You can allow it from Settings.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }



//    function for storing our image in storage in firebase
    private fun uploadUserImage(){
        showProgressDialog(resources.getString(R.string.please_wait))

        if (mSelectedImageFileUri !=null){
//            this is our storage reference....inside child we write the name of the image we want to use to store in firebase storage
            val sref:StorageReference=FirebaseStorage.getInstance().reference.child("USER_IMAGE"+System.currentTimeMillis()+"."+Constants.getFileExtension(this,mSelectedImageFileUri))

            sref.putFile(mSelectedImageFileUri!!).addOnSuccessListener {
//                at this point our image is uploaded to Firebase Storage
                taskSnapshot ->
                    Log.i("Firebase Image URL", taskSnapshot.metadata!!.reference!!.downloadUrl.toString())

                taskSnapshot.metadata!!.reference!!.downloadUrl.addOnSuccessListener {
                    uri ->
                    Log.i("Downloadable Image URL",uri.toString())
//                    putting the url in mProfileImageUrl that we will use in updateUserProfile Data
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

   private fun updateUserProfileData(){
//       key will be Attributes like image,name,mobile used in FireStore DB
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
//    setting the result okay for the Activity which started this activity for Result i.e. Main Activity
        setResult(RESULT_OK)
        finish()
    }

}