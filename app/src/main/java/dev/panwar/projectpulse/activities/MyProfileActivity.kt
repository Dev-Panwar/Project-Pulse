package dev.panwar.projectpulse.activities
import android.os.Bundle
import com.bumptech.glide.Glide
import dev.panwar.projectpulse.R
import dev.panwar.projectpulse.databinding.ActivityMyProfileBinding
import dev.panwar.projectpulse.firebase.FireStoreClass
import dev.panwar.projectpulse.models.User

class MyProfileActivity : BaseActivity() {

    private var binding:ActivityMyProfileBinding?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyProfileBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        setupActionBar()
        FireStoreClass().loadUserData(this)
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
        binding?.ivUserImage?.let {
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
}