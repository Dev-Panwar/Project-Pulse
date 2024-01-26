package dev.panwar.projectpulse.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import dev.panwar.projectpulse.R
import dev.panwar.projectpulse.adapters.MemberListItemsAdapter
import dev.panwar.projectpulse.databinding.ActivityMembersBinding
import dev.panwar.projectpulse.firebase.FireStoreClass
import dev.panwar.projectpulse.models.Board
import dev.panwar.projectpulse.models.User
import dev.panwar.projectpulse.utils.Constants

class MembersActivity : BaseActivity() {

    private var binding:ActivityMembersBinding?=null

    private lateinit var mBoardDetails:Board

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityMembersBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        if (intent.hasExtra(Constants.BOARD_DETAIL)){
            mBoardDetails=intent.getParcelableExtra<Board>(Constants.BOARD_DETAIL)!!
        }

        setupActionBar()
        showProgressDialog(resources.getString(R.string.please_wait))

        FireStoreClass().getAssignedMembersListDetails(this,mBoardDetails.assignedTo)
    }

//    setting up members list
    fun setupMembersList(list: ArrayList<User>){
        hideProgressDialogue()

        binding?.rvMembersList?.layoutManager=LinearLayoutManager(this)
        binding?.rvMembersList?.setHasFixedSize(true)

    val adapter=MemberListItemsAdapter(this,list)
    binding?.rvMembersList?.adapter=adapter
    }

    private fun setupActionBar(){
        setSupportActionBar(binding?.toolbarMembersActivity)
        val actionBar= supportActionBar
        if (actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_profile_back)
            actionBar.title=resources.getString(R.string.members )
        }

        binding?.toolbarMembersActivity?.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    override fun onDestroy() {
        binding=null
        super.onDestroy()
    }
}