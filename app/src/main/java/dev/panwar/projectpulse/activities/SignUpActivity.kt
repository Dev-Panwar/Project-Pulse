package dev.panwar.projectpulse.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.WindowManager
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import dev.panwar.projectpulse.R
import dev.panwar.projectpulse.databinding.ActivitySignUpBinding
import dev.panwar.projectpulse.firebase.FireStoreClass
import dev.panwar.projectpulse.models.User

// inheriting BaseActivity because we are directly using using functions defined in it...see validateForm function to learn how
class SignUpActivity : BaseActivity() {

    private var binding:ActivitySignUpBinding?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        //  To set this Screen as full screen covering complete display of phone and hiding status bar
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        setupActionBar()

        binding?.btnSignUp?.setOnClickListener {
            registerUser()
        }
    }

    private fun setupActionBar(){
        setSupportActionBar(binding?.toolbarSignUpActivity)
        val actionBar=supportActionBar
        if(actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_black_color_back_24dp)

        }
        binding?.toolbarSignUpActivity?.setNavigationOnClickListener {
            onBackPressed()
        }
    }
// for registering user in Firebase and setting Authentication
    private fun registerUser(){
//        this will take name entered by user and also trim if user entered any space after name
        val name:String=binding?.etName?.text.toString().trim{it<=' '}
        val email:String=binding?.etEmail?.text.toString().trim{it<=' '}
        val password:String=binding?.etPassword?.text.toString().trim{it<=' '}

        if (validateForm(name, email, password)){
//            Toast.makeText(this,"Now we can register a new User",Toast.LENGTH_SHORT).show()
            showProgressDialog(resources.getString(R.string.please_wait))
//            when create user task is completed
            FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                if (task.isSuccessful) {
//                   Here the User is Created in Firebase Authorization
//                   getting the user
                    val firebaseUser: FirebaseUser = task.result!!.user!!
                    val registeredEmail = firebaseUser.email!!
//                    here we storing the user in fireStore
//                    Storing the User in Firebase Cloud FireStore DB to use it later...for this we call register user fun of  FireStore Class
                    val user=User(firebaseUser.uid,name,registeredEmail)//Other field of User data class will be Empty or with default values
//                    fireStore class created by us
                    FireStoreClass().registerUser(this,user)
                } else {
                    Toast.makeText(this, "Registration Failed!!", Toast.LENGTH_SHORT).show()
                }
            }

        }
    }

//    for validating the entered details while signing up
    private fun validateForm(name:String,email:String,password:String):Boolean{
         return when{
//             checking if name entered is empty
             TextUtils.isEmpty(name) -> {
                 showErrorSnackBar("Please enter a name")
                 false
             }
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

    fun userRegisteredSuccess(){
        hideProgressDialogue()
        Toast.makeText(this,"You have Successfully Registered",Toast.LENGTH_SHORT).show()
        FirebaseAuth.getInstance().signOut()
        finish()
    }
}