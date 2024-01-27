package dev.panwar.projectpulse.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import dev.panwar.projectpulse.R
import dev.panwar.projectpulse.databinding.ActivityCardDetailsBinding
import dev.panwar.projectpulse.models.Board
import dev.panwar.projectpulse.utils.Constants

class CardDetailsActivity : AppCompatActivity() {

    private var binding:ActivityCardDetailsBinding?=null

    private lateinit var mBoardDetails:Board
    private var mTaskListPosition:Int=-1
    private var mCardPosition:Int=-1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityCardDetailsBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        getIntentData()
        setupActionBar()

        binding?.etNameCardDetails?.setText(mBoardDetails.taskList[mTaskListPosition].cards[mCardPosition].name)
//        to directly focus/ put cursor on etCardNameDetails when we open activity...
        binding?.etNameCardDetails?.setSelection(binding?.etNameCardDetails?.text?.length!!)
    }

    private fun setupActionBar(){
        setSupportActionBar(binding?.toolbarCardDetailsActivity)
        val actionBar= supportActionBar
        if (actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_profile_back)
            actionBar.title=mBoardDetails.taskList[mTaskListPosition].cards[mCardPosition].name
        }

        binding?.toolbarCardDetailsActivity?.setNavigationOnClickListener {
            onBackPressed()
        }
    }

//    to get intent data
    private fun getIntentData(){
        if (intent.hasExtra(Constants.BOARD_DETAIL)){
            mBoardDetails=intent.getParcelableExtra(Constants.BOARD_DETAIL)!!
        }

    if (intent.hasExtra(Constants.TASK_LIST_ITEM_POSITION)){
        mTaskListPosition=intent.getIntExtra(Constants.TASK_LIST_ITEM_POSITION,-1)
    }

    if (intent.hasExtra(Constants.CARD_LIST_ITEM_POSITION)){
        mCardPosition=intent.getIntExtra(Constants.CARD_LIST_ITEM_POSITION,-1)
    }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_delete_card,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.action_delete_card ->{

            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onDestroy() {
        binding=null
        super.onDestroy()
    }
}