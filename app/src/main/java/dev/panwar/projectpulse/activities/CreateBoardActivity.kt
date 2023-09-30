package dev.panwar.projectpulse.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import dev.panwar.projectpulse.R
import dev.panwar.projectpulse.databinding.ActivityCreateBoardBinding

class CreateBoardActivity : AppCompatActivity() {

    private var binding:ActivityCreateBoardBinding?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityCreateBoardBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        setupActionBar()
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

}