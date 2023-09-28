package dev.panwar.projectpulse.activities
import android.os.Bundle
import dev.panwar.projectpulse.R
import dev.panwar.projectpulse.databinding.ActivityMyProfileBinding

class MyProfileActivity : BaseActivity() {

    private var binding:ActivityMyProfileBinding?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyProfileBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        setupActionBar()
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
}