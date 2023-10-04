package dev.panwar.projectpulse.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import dev.panwar.projectpulse.R
import dev.panwar.projectpulse.databinding.ActivityTaskListBinding
import dev.panwar.projectpulse.firebase.FireStoreClass
import dev.panwar.projectpulse.models.Board
import dev.panwar.projectpulse.utils.Constants

class TaskListActivity : BaseActivity() {

    private var binding:ActivityTaskListBinding?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityTaskListBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        var boardDocumentId=""
        if (intent.hasExtra(Constants.DOCUMENT_ID)){
            boardDocumentId=intent.getStringExtra(Constants.DOCUMENT_ID)!!
        }
//      we jumped to this activity when we clicked a board
        showProgressDialog(resources.getString(R.string.please_wait))
//        for getting Board Details
        FireStoreClass().getBoardDetails(this,boardDocumentId)
    }

    private fun setupActionBar(title:String){
        setSupportActionBar(binding?.toolbarTaskListActivity)
        val actionBar= supportActionBar
        if (actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_profile_back)
            actionBar.title=title
        }

        binding?.toolbarTaskListActivity?.setNavigationOnClickListener {
            onBackPressed()
        }
    }

//    this function is called from getBoardDetails function in FireStoreClass
    fun boardDetails(board: Board){
        hideProgressDialogue()
//        setting up the action bar after getting the board details...as we need to set toolbar title as name of board
        setupActionBar(board.name)
    }
}