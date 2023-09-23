package dev.panwar.projectpulse.activities

import android.content.Intent
import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.WindowManager
import dev.panwar.projectpulse.databinding.ActivitySplashBinding
import dev.panwar.projectpulse.firebase.FireStoreClass

class SplashActivity : AppCompatActivity() {

    private var binding:ActivitySplashBinding?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding?.root)

//        To set this Screen as full screen covering complete display of phone and hiding status bar
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

//        for changing the Font of displayed title..i.e. APP name
        val typeFace:Typeface=Typeface.createFromAsset(assets,"Carnevalee Freakshow.ttf")
        binding?.tvAppName?.typeface=typeFace

//        to Move to Intro Activity automatically after 1.5 secs
        Handler().postDelayed({
//            For auto Log In of Previously LoggedIn User....This is feature of Firebase Authorization that it store the last User sign in or sign up...So we use fun of fireStore class to get user id from last User we have stored in firebase...if it's not empty we move ahead
            var currentUserId=FireStoreClass().getCurrentUserID()
            if (currentUserId.isNotEmpty()){
                startActivity(Intent(this,MainActivity::class.java))
            }else{
//                So that User sign in or sign up
                startActivity(Intent(this,IntroActivity::class.java))
            }
        finish()//finishes this activity
        },1500)

    }
}