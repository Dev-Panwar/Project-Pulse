package dev.panwar.projectpulse.activities

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import dev.panwar.projectpulse.R
import dev.panwar.projectpulse.databinding.ActivityCreateBoardBinding
import dev.panwar.projectpulse.firebase.FireStoreClass
import dev.panwar.projectpulse.models.Board
import dev.panwar.projectpulse.utils.Constants
import java.io.IOException

class CreateBoardActivity : BaseActivity() {

    private var binding:ActivityCreateBoardBinding?=null
    private var mSelectedImageFileUri: Uri?=null
    private lateinit var mUserName:String
    private var mBoardImageURL:String=""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityCreateBoardBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        setupActionBar()

        if (intent.hasExtra(Constants.NAME)){
            mUserName= intent.getStringExtra(Constants.NAME).toString()
        }


        binding?.ivBoardImage?.setOnClickListener {
            //   first checking the permission to access external storage or not
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
                Constants.showImageChooser(this)
            }
//            asking for storage permission...This time not using dexter library...using the default method
            else{
                ActivityCompat.requestPermissions(this,arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE), Constants.READ_STORAGE_PERMISSION_CODE)
            }
        }

        binding?.btnCreate?.setOnClickListener {
            if (mSelectedImageFileUri!=null){
                uploadBoardImage()
            }
            else{
                showProgressDialog(resources.getString(R.string.please_wait))
                createBoard()
            }
        }
    }

    private fun createBoard(){
        val assignedUsersArrayList:ArrayList<String> = ArrayList()
//        getCurrentUser id function from Base Activity
        assignedUsersArrayList.add(getCurrentUserID())

        var board=Board(binding?.etBoardName?.text.toString(),mBoardImageURL,mUserName,assignedUsersArrayList)

        FireStoreClass().createBoard(this,board)
    }

    private fun uploadBoardImage(){
        showProgressDialog(resources.getString(R.string.please_wait))

        if (mSelectedImageFileUri !=null){
//            this is our storage reference....inside child we write the name of the image we want to use to store in firebase storage
            val sref: StorageReference = FirebaseStorage.getInstance().reference.child("BOARD_IMAGE"+System.currentTimeMillis()+"."+Constants.getFileExtension(this,mSelectedImageFileUri))

            sref.putFile(mSelectedImageFileUri!!).addOnSuccessListener {
//                at this point our image is uploaded to Firebase Storage
                    taskSnapshot ->
                Log.i("Board Image URL", taskSnapshot.metadata!!.reference!!.downloadUrl.toString())
// below creates an Downloadable image URI...
                taskSnapshot.metadata!!.reference!!.downloadUrl.addOnSuccessListener {
                        uri ->
                    Log.i("Downloadable Image URL",uri.toString())
//                    putting the url in mProfileImageUrl that we will use in updateUserProfile Data
                    mBoardImageURL=uri.toString()
                    createBoard()
                }
            }.addOnFailureListener {
                    exception ->
                Toast.makeText(this, exception.message,Toast.LENGTH_SHORT).show()
                hideProgressDialogue()
            }
        }
    }


    fun boardCreatedSuccessfully(){
        hideProgressDialogue()
//        setting result okay for the activity which started this activity for creating board..i.e. Main Activity so that it can reload data in view
        setResult(Activity.RESULT_OK)
        finish()
    }

    private fun setupActionBar(){
        setSupportActionBar(binding?.toolbarCreateBoardActivity)
        val actionBar= supportActionBar
        if (actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_profile_back)
            actionBar.title=resources.getString(R.string.create_board_title)
        }

        binding?.toolbarCreateBoardActivity?.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK && requestCode == Constants.PICK_IMAGE_REQUEST_CODE && data!!.data !=null){
//            now we have Uri of image
            mSelectedImageFileUri = data.data

            try {
//                finally adding image to ivProfileUserImage
                binding?.ivBoardImage?.let {
                    Glide
                        .with(this)
                        .load(mSelectedImageFileUri)
                        .centerCrop()
                        .placeholder(R.drawable.ic_board_place_holder)
                        .into(it)
                }
            }catch (e: IOException){
                e.printStackTrace()
            }
        }
    }


    //    this inbuilt function will be called automatically after the Permission is granted/or denied...That's why we gave the REQUEST_CODE
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == Constants.READ_STORAGE_PERMISSION_CODE){
//        grant result store the PERMISSION granted or denied information of all requested permission
//        checking if the permission granted
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Constants.showImageChooser(this)
            }else{
                Toast.makeText(this,"Oops You Just Denied the Permission for Storage. You can allow it from Settings",
                    Toast.LENGTH_SHORT).show()
            }
        }

    }


}