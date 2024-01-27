package dev.panwar.projectpulse.activities

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.recyclerview.widget.LinearLayoutManager
import dev.panwar.projectpulse.R
import dev.panwar.projectpulse.adapters.TaskListItemAdapter
import dev.panwar.projectpulse.databinding.ActivityTaskListBinding
import dev.panwar.projectpulse.firebase.FireStoreClass
import dev.panwar.projectpulse.models.Board
import dev.panwar.projectpulse.models.Card
import dev.panwar.projectpulse.models.Task
import dev.panwar.projectpulse.utils.Constants

class TaskListActivity : BaseActivity() {

    private var binding:ActivityTaskListBinding?=null
    private lateinit var mBoardDetails:Board
    private lateinit var mBoardDocumentId:String

    companion object{
        // request code when starting activity for result(MemberActivity)
        const val MEMBERS_REQUEST_CODE:Int=13
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityTaskListBinding.inflate(layoutInflater)
        setContentView(binding?.root)


        if (intent.hasExtra(Constants.DOCUMENT_ID)){
            mBoardDocumentId=intent.getStringExtra(Constants.DOCUMENT_ID)!!
        }
//      we jumped to this activity when we clicked a board
        showProgressDialog(resources.getString(R.string.please_wait))
//        for getting Board Details
        FireStoreClass().getBoardDetails(this,mBoardDocumentId)
    }

    private fun setupActionBar(){
        setSupportActionBar(binding?.toolbarTaskListActivity)
        val actionBar= supportActionBar
        if (actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_profile_back)
            actionBar.title=mBoardDetails.name
        }

        binding?.toolbarTaskListActivity?.setNavigationOnClickListener {
            onBackPressed()
        }
    }

//    inflating menu
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
    menuInflater.inflate(R.menu.menu_members,menu)
        return super.onCreateOptionsMenu(menu)
    }
//    implementing menu click
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
     when(item.itemId){
         R.id.action_members ->{
             val intent=Intent(this, MembersActivity::class.java)
             intent.putExtra(Constants.BOARD_DETAIL,mBoardDetails)
//             starting activity for result because if we add new member to board and press back button to go back to this activity. we have latest BoardDetails(as it contains assigned to variable)..See activity result
             startActivityForResult(intent, MEMBERS_REQUEST_CODE)
             return true

         }
     }
        return super.onOptionsItemSelected(item)
    }

//    this function is called from getBoardDetails function in FireStoreClass
    fun boardDetails(board: Board){
        mBoardDetails=board
        hideProgressDialogue()
//        setting up the action bar after getting the board details...as we need to set toolbar title as name of board
        setupActionBar()

//    inflating TaskList assigned to Board to our Recycler view adapter
//    adding initial item to Task list
    val addTaskList=Task(resources.getString(R.string.add_list))
    board.taskList.add(addTaskList)
// setting Layout as Horizontal for recycler view.showing different Task Lists
    binding?.rvTaskList?.layoutManager=LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false)
    binding?.rvTaskList?.setHasFixedSize(true)

//    giving the task list Stored in Board
    val adapter = TaskListItemAdapter(this, board.taskList)
    binding?.rvTaskList?.adapter=adapter


    }


//    when task list update Success
    fun addUpdateTaskListSuccess(){
//        hiding progressBar when update task list success
        hideProgressDialogue()
//        progressbar for getting board details
        showProgressDialog(resources.getString(R.string.please_wait))
//        specifying which board's detail we want by giving document id
        FireStoreClass().getBoardDetails(this,mBoardDetails.documentId)
    }

    fun createTaskList(taskListName:String){
        val task=Task(taskListName,FireStoreClass().getCurrentUserID())
//        the previous entry will move by one index
        mBoardDetails.taskList.add(0,task)
        mBoardDetails.taskList.removeAt(mBoardDetails.taskList.size-1)

        FireStoreClass().addUpdateTaskList(this, mBoardDetails)
    }

    fun updateTaskList(position:Int,listName:String,model:Task){
//        creating new task..
         val task=Task(listName,model.createdBy)
//        replacing at specified position
        mBoardDetails.taskList[position] = task
        mBoardDetails.taskList.removeAt(mBoardDetails.taskList.size-1)

        showProgressDialog(resources.getString(R.string.please_wait))
        FireStoreClass().addUpdateTaskList(this, mBoardDetails)
    }

    fun deleteTaskList(position: Int){
        mBoardDetails.taskList.removeAt(position)
        mBoardDetails.taskList.removeAt(mBoardDetails.taskList.size-1)

        showProgressDialog(resources.getString(R.string.please_wait))
        FireStoreClass().addUpdateTaskList(this, mBoardDetails)

    }

    fun addCardToTaskList(position: Int,cardName:String){
        mBoardDetails.taskList.removeAt(mBoardDetails.taskList.size-1)

        val cardAssignedUsersList:ArrayList<String> = ArrayList()
//        adding current user to cardAssigned user list
        cardAssignedUsersList.add(FireStoreClass().getCurrentUserID())
// finally creating a card
        val card= Card(cardName, FireStoreClass().getCurrentUserID(), cardAssignedUsersList)

//        getting cardList on a particular Task
        val cardList = mBoardDetails.taskList[position].cards
//        Now adding the card we created to this CardList
        cardList.add(card)

//        Now Creating a new task with same name and created by but with updated cardList
        val task=Task(mBoardDetails.taskList[position].title, mBoardDetails.taskList[position].createdBy, cardList)
// replacing old task with new one and updating in DB
        mBoardDetails.taskList[position]=task
        showProgressDialog(resources.getString(R.string.please_wait))
        FireStoreClass().addUpdateTaskList(this, mBoardDetails)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode==Activity.RESULT_OK && requestCode== MEMBERS_REQUEST_CODE){
           showProgressDialog(resources.getString(R.string.please_wait))
            FireStoreClass().getBoardDetails(this,mBoardDocumentId)
        }else{
            Log.e("Cancelled","Cancelled")
        }
    }

    fun cardDetails(taskListPosition:Int, cardPosition:Int){
        startActivity(Intent(this,CardDetailsActivity::class.java))
    }

}