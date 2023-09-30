package dev.panwar.projectpulse.activities

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import dev.panwar.projectpulse.R
import dev.panwar.projectpulse.databinding.ActivityCreateBoardBinding
import dev.panwar.projectpulse.utils.Constants
import java.io.IOException

class CreateBoardActivity : AppCompatActivity() {

    private var binding:ActivityCreateBoardBinding?=null
    private var mSelectedImageFileUri: Uri?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityCreateBoardBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        setupActionBar()


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