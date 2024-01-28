package dev.panwar.projectpulse.activities

import android.app.Activity
import android.app.AlertDialog
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.CalendarContract.Colors
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import dev.panwar.projectpulse.R
import dev.panwar.projectpulse.adapters.CardMemberListItemsAdapter
import dev.panwar.projectpulse.databinding.ActivityCardDetailsBinding
import dev.panwar.projectpulse.dialogs.LabelColorListDialog
import dev.panwar.projectpulse.dialogs.MembersListDialog
import dev.panwar.projectpulse.firebase.FireStoreClass
import dev.panwar.projectpulse.models.*
import dev.panwar.projectpulse.utils.Constants

class CardDetailsActivity : BaseActivity() {

    private var binding:ActivityCardDetailsBinding?=null

    private lateinit var mBoardDetails:Board
    private var mTaskListPosition:Int=-1
    private var mCardPosition:Int=-1
    private var mSelectedColor=""
    private lateinit var mMembersDetailList:ArrayList<User>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityCardDetailsBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        getIntentData()
        setupActionBar()

        binding?.etNameCardDetails?.setText(mBoardDetails.taskList[mTaskListPosition].cards[mCardPosition].name)
//        to directly focus/ put cursor on etCardNameDetails when we open activity...
        binding?.etNameCardDetails?.setSelection(binding?.etNameCardDetails?.text?.length!!)

//        getting label color from DB
        mSelectedColor=mBoardDetails.taskList[mTaskListPosition].cards[mCardPosition].labelColor
        if (mSelectedColor.isNotEmpty()){
            setColor()
        }


        binding?.btnUpdateCardDetails?.setOnClickListener {
            if (binding?.etNameCardDetails?.text.toString().isNotEmpty()){
                updateCardDetails()
            }else{
                Toast.makeText(this,"Please enter card name",Toast.LENGTH_SHORT).show()
            }
        }

        binding?.tvSelectLabelColor?.setOnClickListener {
            labelColorsListDialog()
        }

        binding?.tvSelectMembers?.setOnClickListener {
            membersListDialog()
        }

        setupSelectedMembersList()
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

    if (intent.hasExtra(Constants.BOARD_MEMBERS_LIST)){
        mMembersDetailList=intent.getParcelableArrayListExtra((Constants.BOARD_MEMBERS_LIST))!!
    }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_delete_card,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.action_delete_card ->{
               alertDialogForDeleteCard(mBoardDetails.taskList[mTaskListPosition].cards[mCardPosition].name)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

//    called from fireStore Class
    fun addUpdateTaskListSuccess(){
       hideProgressDialogue()
        setResult(Activity.RESULT_OK)
        finish()
    }

//    called when we press update button
    private fun updateCardDetails(){
//        created a new card with updated details
        val card=Card(binding?.etNameCardDetails?.text.toString(),mBoardDetails.taskList[mTaskListPosition].cards[mCardPosition].createdBy,mBoardDetails.taskList[mTaskListPosition].cards[mCardPosition].assignedTo,mSelectedColor)

    // optimization...when we update our Task List...by default a new task is created at end with name addList due to our logic
    val taskList:ArrayList<Task> = mBoardDetails.taskList
    taskList.removeAt(taskList.size-1)

        mBoardDetails.taskList[mTaskListPosition].cards[mCardPosition]=card //putting updated card in mBoardDetails


//        updating in Database
        showProgressDialog(resources.getString(R.string.please_wait))
        FireStoreClass().addUpdateTaskList(this,mBoardDetails)

    }

    private fun deleteCard(){

        val cardList:ArrayList<Card> = mBoardDetails.taskList[mTaskListPosition].cards
        cardList.removeAt(mCardPosition)

//        optimization
        val taskList:ArrayList<Task> = mBoardDetails.taskList
        taskList.removeAt(taskList.size-1)

        mBoardDetails.taskList[mTaskListPosition].cards=cardList

        //        updating in Database
        showProgressDialog(resources.getString(R.string.please_wait))
        FireStoreClass().addUpdateTaskList(this,mBoardDetails)
    }

//     A function to show an alert dialog for the confirmation to delete the card.
    private fun alertDialogForDeleteCard(cardName: String) {
        val builder = AlertDialog.Builder(this)
        //set title for alert dialog
        builder.setTitle(resources.getString(R.string.alert))
        //set message for alert dialog
        builder.setMessage(
            resources.getString(
                R.string.confirmation_message_to_delete_card,
                cardName
            )
        )
        builder.setIcon(android.R.drawable.ic_dialog_alert)

        //performing positive action
        builder.setPositiveButton(resources.getString(R.string.yes)) { dialogInterface, which ->
            dialogInterface.dismiss() // Dialog will be dismissed
//            calling the function to Delete Card
            deleteCard()
            // END
        }
        //performing negative action
        builder.setNegativeButton(resources.getString(R.string.no)) { dialogInterface, which ->
            dialogInterface.dismiss() // Dialog will be dismissed
        }
        // Create the AlertDialog
        val alertDialog: AlertDialog = builder.create()
        // Set other dialog properties
        alertDialog.setCancelable(false) // Will not allow user to cancel after clicking on remaining screen area.
        alertDialog.show()  // show the dialog to UI
    }

//    function returns the colorList
    private fun colorsList(): ArrayList<String>{
        val colorsList:ArrayList<String> = ArrayList()
    colorsList.add("#43C86F")
    colorsList.add("#0C90F1")
    colorsList.add("#F72400")
    colorsList.add("#7A8089")
    colorsList.add("#D57C1D")
    colorsList.add("#770000")
    colorsList.add("#0022F8")

    return colorsList
    }

//    for setting the color of card..in this Activity view
    private fun setColor(){
        binding?.tvSelectLabelColor?.text=""
        binding?.tvSelectLabelColor?.setBackgroundColor(Color.parseColor(mSelectedColor))
    }

//    for initialising  select label color dialog
    private fun labelColorsListDialog(){
       val colorsList:ArrayList<String> = colorsList()

//    creating list dialog..Initialising
    val listDialog = object : LabelColorListDialog(
        this,colorsList,resources.getString(R.string.str_select_label_color),mSelectedColor){
        override fun onItemSelected(color: String) {
            mSelectedColor=color
            setColor()
//            updating in DB
            updateCardDetails()
        }

    }
    listDialog.show()

    }

    private fun membersListDialog(){
        var cardAssignedMembersList=mBoardDetails.taskList[mTaskListPosition].cards[mCardPosition].assignedTo


        if (cardAssignedMembersList.size>0){
//            mMemberDetailList contains Board Assigned Members Details
            for (i in mMembersDetailList.indices){
                for (j in cardAssignedMembersList){
//                    means out of boardMembers jin members ko card Assign hua hai..unko Selected=true kiya hai
                    if (mMembersDetailList[i].id==j){
                        mMembersDetailList[i].selected=true
                    }
                }
            }
        }else{
//            if no members Assigned to card till now then all members have selected = false
            for (i in mMembersDetailList.indices){
                mMembersDetailList[i].selected=false
            }
        }

        //        creating memberList Dialog...Initialising
        val listDialog = object : MembersListDialog (
            this,mMembersDetailList,resources.getString(R.string.str_select_member)
                ){
            override fun onItemSelected(user: User, action: String) {
                if (action == Constants.SELECT){
//                    if member already not assigned to card then
                    if (!mBoardDetails.taskList[mTaskListPosition].cards[mCardPosition].assignedTo.contains(user.id)){
                        mBoardDetails.taskList[mTaskListPosition].cards[mCardPosition].assignedTo.add(user.id)
                    }
                }else{
                    mBoardDetails.taskList[mTaskListPosition].cards[mCardPosition].assignedTo.remove(user.id)

                    for ( i in mMembersDetailList.indices){
                        if (mMembersDetailList[i].id==user.id){
                            mMembersDetailList[i].selected=false
                        }
                    }
                }
//                    setting up List Again..as changes made
                setupSelectedMembersList()
            }

        }
        listDialog.show()

    }

    private fun setupSelectedMembersList(){
//        Members assigned to card
        val cardAssignedMembersList=mBoardDetails.taskList[mTaskListPosition].cards[mCardPosition].assignedTo

//        list containing selected Members
        val selectedMembersList:ArrayList<SelectedMembers> = ArrayList()

        for (i in mMembersDetailList.indices){
            for (j in cardAssignedMembersList){
//                    means out of boardMembers jin members ko card Assign hua hai..unko Selected=true kiya hai
                if (mMembersDetailList[i].id==j){
                    val selectedMember=SelectedMembers(mMembersDetailList[i].id,mMembersDetailList[i].image)
                    selectedMembersList.add(selectedMember)
                }
            }
        }

        if (selectedMembersList.size>0){
//            Empty Member because InPlace of it we place iv_add_member
            selectedMembersList.add(SelectedMembers("",""))
//            hiding textView select Member
            binding?.tvSelectMembers?.visibility=View.GONE
//            showing RV to show profiles added
            binding?.rvSelectedMembersList?.visibility=View.VISIBLE

//            setting up RV
            binding?.rvSelectedMembersList?.layoutManager=GridLayoutManager(this,6)
            val adapter=CardMemberListItemsAdapter(this,selectedMembersList)
            binding?.rvSelectedMembersList?.adapter=adapter
//            implementing onClickListener interface
            adapter.setOnClickListener(object :CardMemberListItemsAdapter.OnClickListener{
                override fun onClick() {
                    membersListDialog()
                }

            })
        }else{
            binding?.tvSelectMembers?.visibility=View.VISIBLE
            binding?.rvSelectedMembersList?.visibility=View.GONE
        }

    }


    override fun onDestroy() {
        binding=null
        super.onDestroy()
    }
}