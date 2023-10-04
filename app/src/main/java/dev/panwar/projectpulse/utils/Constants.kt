package dev.panwar.projectpulse.utils

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import android.webkit.MimeTypeMap
import androidx.core.app.ActivityCompat.startActivityForResult
import dev.panwar.projectpulse.activities.MyProfileActivity

object Constants {

//    Collection name for users in Firebase
    const val USERS:String= "Users"
//    Collection name for Boards in firebase
    const val BOARDS:String="boards"

//    Attributes of User data Class
    const val IMAGE:String="image"
    const val NAME:String="name"
    const val MOBILE:String="mobile"

//    for board Data Class
    const val ASSIGNED_TO:String="assignedTo"

//   for Start Activity for Result
    const val READ_STORAGE_PERMISSION_CODE=1
    const val PICK_IMAGE_REQUEST_CODE=2

    const val DOCUMENT_ID:String="documentID"

   //  the Common Function that we will use Several Times i.e. in MyProfileActivity and Create Board Activity
    //    for showing image chooser
    fun showImageChooser(activity: Activity){
//        making a gallery intent with type Pick as we wanna pick something i.e. image
        val galleryIntent= Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
//       activity.startStartActivity for result is here activity is from which activity we are starting galleryIntent for result
        activity.startActivityForResult(galleryIntent, PICK_IMAGE_REQUEST_CODE)
    }
    //    to get file extension from link...as we want only .png, .jpeg files to be stored in firebase storage
    fun getFileExtension(activity: Activity, uri: Uri?):String?{
        return MimeTypeMap.getSingleton().getExtensionFromMimeType(activity.contentResolver.getType(uri!!))
    }



}