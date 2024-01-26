package dev.panwar.projectpulse.activities

import android.app.Activity
import android.app.Dialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
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
    private lateinit var mAssignedMembersList:ArrayList<User>
    private var anyChangesMade:Boolean=false

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
//    we got list of members to display in layout from firebase class(this function is called from firebase class) Storing this as local variable to use in some other logic
        mAssignedMembersList=list
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

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_add_member,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.action_add_member ->{
              dialogSearchMember()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

//    function for add member dialog
    private fun dialogSearchMember(){
        val dialog=Dialog(this)
    dialog.setContentView(R.layout.dialog_search_member)
    dialog.setCanceledOnTouchOutside(false)
//    add button functionality
    dialog.findViewById<TextView>(R.id.tv_add).setOnClickListener {
          val email = dialog.findViewById<EditText>(R.id.et_email_search_member).text.toString()
            if (email.isNotEmpty()){
                dialog.dismiss()
              showProgressDialog(resources.getString(R.string.please_wait))
                FireStoreClass().getMemberDetails(this,email)
          }else{
              Toast.makeText(this,"Please Enter members email Address",Toast.LENGTH_SHORT).show()
            }
    }
//    cancel button functionality
    dialog.findViewById<TextView>(R.id.tv_cancel).setOnClickListener {
       dialog.dismiss()
    }
    dialog.show()
    }

//    will be called from fireStore class with user details fetched..also there we validate if member with entered email exist or not
//    Now we have User Details...here we will add this User to AssignedTo of Board and update in database
    fun memberDetails(user:User){
      mBoardDetails.assignedTo.add(user.id)
//      now updating this is fireStore DB
    FireStoreClass().assignMemberToBoard(this,mBoardDetails,user)
    }

//    Setting up view(UI) with latest members list
    fun memberAssignedSuccess(user: User){
        hideProgressDialogue()
        mAssignedMembersList.add(user)
        setupMembersList(mAssignedMembersList)
        anyChangesMade=true
    }

    override fun onDestroy() {
        binding=null
        super.onDestroy()
    }

    override fun onBackPressed() {
        if (anyChangesMade){
            setResult(Activity.RESULT_OK)
        }
        super.onBackPressed()
    }
}