package dev.panwar.projectpulse.activities

import android.app.Dialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import dev.panwar.projectpulse.R


//This activity will contain all the common functions that is used by several activities in this project...all activities using it's functions will
//inherit this class to use it's functions to make code look more clean

open class BaseActivity : AppCompatActivity() {

    private var doubleBackToExitPressedOnce=false
    private lateinit var mProgressDialog: Dialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_base)
    }

    fun showProgressDialog(text:String){
        mProgressDialog= Dialog(this)
        mProgressDialog.setContentView(R.layout.dialog_progress)
//        findViewById<TextView>(R.id.tv_progress_text).text= text
        mProgressDialog.show()
    }
// for hiding progress Dialogue
    fun hideProgressDialogue(){
        mProgressDialog.dismiss()
    }

//    TO get the current user id
    fun getCurrentUserID():String{
        return FirebaseAuth.getInstance().currentUser!!.uid
    }

//    if someone pressed back button twice....we close the Application
    fun doubleBackToExit(){
        if (doubleBackToExitPressedOnce){
            super.onBackPressed()
            return
        }
//    initially it is set as false....one pressing once it becomes true...on if we press again it enters the if condition
       doubleBackToExitPressedOnce=true
    Toast.makeText(this,resources.getString(R.string.please_click_back_again_to_exit),Toast.LENGTH_SHORT).show()

//    if User Does not press back button again before 2 secs we reset doubleBackToExitPressedOnce=false
    Handler().postDelayed({
           doubleBackToExitPressedOnce=false
      },2000)
    }

//    for showing snackBar in Case of any error
    fun showErrorSnackBar(message:String){
        val snackBar=Snackbar.make(findViewById(android.R.id.content),message,Snackbar.LENGTH_LONG)
//    for changing anything to the view of SnackBar
        val snackBarView=snackBar.view
        snackBarView.setBackgroundColor(ContextCompat.getColor(this,R.color.snackbar_error_color))
        snackBar.show()
    }
}