package dev.panwar.projectpulse.activities

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.WindowManager
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dev.panwar.projectpulse.R
import dev.panwar.projectpulse.databinding.ActivitySignInBinding
import dev.panwar.projectpulse.firebase.FireStoreClass
import dev.panwar.projectpulse.models.User

class SignInActivity : BaseActivity() {

    private var binding:ActivitySignInBinding?=null
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        //  To set this Screen as full screen covering complete display of phone and hiding status bar
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        setupActionBar()

        // Initialize Firebase Auth
        auth = Firebase.auth

        binding?.btnSignIn?.setOnClickListener {
            signInRegisteredUser()
        }

    }

    private fun setupActionBar(){
        setSupportActionBar(binding?.toolbarSignInActivity)
        val actionBar=supportActionBar
        if(actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_black_color_back_24dp)

        }
        binding?.toolbarSignInActivity?.setNavigationOnClickListener {
            onBackPressed()
        }
    }

//    For Authenticating current user credentials using firebase
    private fun signInRegisteredUser(){
        val email:String=binding?.etEmailSignIn?.text.toString().trim{it<=' '}
        val password:String=binding?.etPasswordSignIn?.text.toString().trim{it<=' '}

        if (validateForm(email, password)){
            showProgressDialog(resources.getString(R.string.please_wait))

//            copied from Documentation https://firebase.google.com/docs/auth/android/password-auth
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d("Sign In", "signInWithEmail:success")
//                        val user = auth.currentUser
//                        after authenticating Signing In the User in FireStore to access it's data
                        FireStoreClass().loadUserData(this)
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w("Sign In", "signInWithEmail:failure", task.exception)
                        hideProgressDialogue()
                        Toast.makeText(
                            baseContext,
                            "Wrong Credentials, Try Again",
                            Toast.LENGTH_SHORT,
                        ).show()
                    }
                }
// copied till this line from documentation

        }

    }
// Function called in FireStore class which gave us loggedIn User data
    fun signInSuccess(user:User){
        hideProgressDialogue()
        startActivity(Intent(this,MainActivity::class.java))
        finish()
    }



    //    for validating the entered details while signing in
    private fun validateForm(email:String,password:String):Boolean{
        return when{
//             checking if details entered is empty
            TextUtils.isEmpty(email) -> {
                showErrorSnackBar("Please enter an email address")
                false
            }
            TextUtils.isEmpty(password) -> {
                showErrorSnackBar("Please enter an password")
                false
            }

            else -> true
        }
    }

}