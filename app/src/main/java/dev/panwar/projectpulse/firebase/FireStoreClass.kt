package dev.panwar.projectpulse.firebase

import android.app.Activity
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.toObject
import dev.panwar.projectpulse.activities.*
import dev.panwar.projectpulse.models.Board
import dev.panwar.projectpulse.models.Task
import dev.panwar.projectpulse.models.User
import dev.panwar.projectpulse.utils.Constants

class FireStoreClass {

// initially fireStore
    private val mFireStore=FirebaseFirestore.getInstance()

    fun registerUser(activity:SignUpActivity, userInfo: User){
//        Adding a new Collection of Data to FireStore DB...with name Users...Then we Giving current user ID (id created of User when we SignUp via Firebase authentication) as the Document id to Store in Particular Collection...Then adding all info of Users to the Particular User i.e.(all the parameters of data Class User).
//        .Then after Success of task UserRegisteredSuccess fun execute

         mFireStore.collection(Constants.USERS)
             .document(getCurrentUserID()).set(userInfo, SetOptions.merge()).addOnSuccessListener {
//                 function of SignUp activity
                 activity.userRegisteredSuccess()
             }.addOnFailureListener {
//                 activity.javaClass.name gives the name of Activity
                 e-> Log.e(activity.javaClass.name,"Error Writing Document")
             }
    }


//    Creating a Board in FireStoreClass....Same code as RegisterUser
    fun createBoard(activity: CreateBoardActivity,board:Board){
//    no getCurrentUser id in document() as it auto generates a random id..as here we don't wanna create document with id of current User.....
        mFireStore.collection(Constants.BOARDS).document().set(board, SetOptions.merge()).addOnSuccessListener {
            Log.e(activity.javaClass.simpleName,"Board Created Successfully.")
            Toast.makeText(activity,"Board Created Successfully",Toast.LENGTH_SHORT).show()
            activity.boardCreatedSuccessfully()
        }.addOnFailureListener {
            exception->
            activity.hideProgressDialogue()
            Log.e(activity.javaClass.simpleName,"Error while Creating Board",exception)
        }
    }

//    this function is used to load User Data from Fire Store DB and send to Different Activity Function to perform their tasks
//    by default readBoardsList:Boolean will contain false if true is passed then i will use true
    fun loadUserData(activity: Activity, readBoardsList:Boolean=false){
//        same as above function just set change to get...refer to code explanation by above comments
        mFireStore.collection(Constants.USERS)
            .document(getCurrentUserID()).get().addOnSuccessListener { document->
//                we stored the currently Logged in User details by converting Current User ID(document) to object of type User data class
                  val loggedInUser = document.toObject(User::class.java)!!

                when(activity){
                    is SignInActivity ->{
//                       fir this fun will be called by signInActivity
//                       calling function of signInactivity and passing current user..where it will start Main Activity where this Function is called Again and Main Activity context is passed then isMainActivity code is executed

                        activity.signInSuccess(loggedInUser)
                    }
                    is MainActivity -> {
                        activity.updateNavigationUserDetails(loggedInUser, readBoardsList)
                    }

                    is MyProfileActivity ->{
                        activity.setUserDataInUI(loggedInUser)
                    }
                }
            }.addOnFailureListener {
//                 activity.javaClass.name gives the name of Activity
                    e->

                when(activity){
                    is SignInActivity ->{
//                        calling function of signInactivity and passing current user
                        activity.hideProgressDialogue()
                    }
                    is MainActivity -> {
                        activity.hideProgressDialogue()
                    }
                }
                Log.e(activity.javaClass.name,"Error Writing Document")
            }
    }

//    getting current user id from FireBase Auth
    fun getCurrentUserID():String{

//    getting the current User
        val currentUser=FirebaseAuth.getInstance().currentUser
//    initially current user id
        var currentUserId=""
        if (currentUser!=null){
//            if Current User not null then getting it's id
            currentUserId=currentUser.uid
        }
       return currentUserId
    }


//    to get list of Boards from FireStore DataBase
    fun getBoardList(activity: MainActivity){
//    this whereArrayContains Query gives Us the List of Board In fireStore DB that are assigned to Our Current User(i.e The list of board which has assigned to parameter set to current user id )
        mFireStore.collection(Constants.BOARDS).whereArrayContains(Constants.ASSIGNED_TO,getCurrentUserID()).get().addOnSuccessListener {
            document ->
//          this document contains the list of documents in firebase satisfying our Query
            Log.i(activity.javaClass.simpleName,document.documents.toString())
            val boardList:ArrayList<Board> = ArrayList()
            for (i in document.documents){
//                we are creating the Board Object from the Document()
                val board=i.toObject(Board::class.java)!!
//                as we have the id of document...setting documentId parameter of Board Data class with the document id stored in firebase(this id is autogenerated when we created)
                board.documentId=i.id
                boardList.add(board)
            }
            activity.populateBoardListToUI(boardList)
        }.addOnFailureListener {
            e ->
            activity.hideProgressDialogue()
            Log.e(activity.javaClass.simpleName,"Error While Creating a Board.",e)
        }
    }

// updating the info in Fire store database of current user Using Hashmap
    fun updateUserProfileData(activity: Activity, userHashmap:HashMap<String,Any>){
    mFireStore.collection(Constants.USERS)
        .document(getCurrentUserID()).update(userHashmap).addOnSuccessListener {
            Log.i(activity.javaClass.simpleName,"Profile data updated successfully")
            Toast.makeText(activity,"Profile details updated successfully",Toast.LENGTH_SHORT).show()
            when(activity){
                is MainActivity ->{
                    activity.tokenUpdateSuccess()
                }
                is MyProfileActivity ->{
                    activity.profileUpdateSuccess()
                }
            }

        }.addOnFailureListener {
            when(activity){
                is MainActivity ->{
                    activity.hideProgressDialogue()
                }
                is MyProfileActivity ->{
                    activity.hideProgressDialogue()
                }
            }

            Log.i(activity.javaClass.simpleName,"Error while creating a board")
            Toast.makeText(activity,"Error when updating the profile",Toast.LENGTH_SHORT).show()
        }
    }

    fun getBoardDetails(activity: TaskListActivity,documentId:String) {
        mFireStore.collection(Constants.BOARDS).document(documentId).get().addOnSuccessListener {
                document ->
//            we get the document(Board) with with the given document id
            Log.i(activity.javaClass.simpleName,document.toString())
            val board=document.toObject(Board::class.java)!!
            board.documentId=document.id
            activity.boardDetails(board)
        }.addOnFailureListener {
                e ->
            activity.hideProgressDialogue()
            Log.e(activity.javaClass.simpleName,"Error While Creating a Board.",e)
        }
    }

//    for updating TaskList attribute of Board in DATABASE
    fun addUpdateTaskList(activity: Activity,board: Board){
//        mapping the Data using Hashmap
        val taskListHashmap=HashMap<String,Any>()
        taskListHashmap[Constants.TASK_LIST]=board.taskList

//        storing in database...finding document/board with Boards document id
        mFireStore.collection(Constants.BOARDS).document(board.documentId).update(taskListHashmap).addOnSuccessListener {
            Log.e(activity.javaClass.simpleName,"TaskListUpdated Successfully.")
            if (activity is TaskListActivity) {
                activity.addUpdateTaskListSuccess()
            }
            else if (activity is CardDetailsActivity){
                activity.addUpdateTaskListSuccess()
            }
        }.addOnFailureListener {
            exception->
            if (activity is TaskListActivity) {
                activity.hideProgressDialogue()
            }else if(activity is CardDetailsActivity){
                activity.hideProgressDialogue()
            }
            Log.e(activity.javaClass.simpleName,"Error while creating a Board.",exception)
        }
    }

//    to get Assigned Members list/fetching members of the board...assignedTo contains ids of User assigned to a board
    fun getAssignedMembersListDetails(activity:Activity,assignedTo:ArrayList<String>){
//    checking User Collection, where user has id == AssignedTo (any entry of this arraylist)
        mFireStore.collection(Constants.USERS).whereIn(Constants.ID, assignedTo).get()
            .addOnSuccessListener { document ->
            Log.e(activity.javaClass.simpleName,document.documents.toString())
// we simply searched for all the documents/users where id==assignedTO
                val usersList: ArrayList<User> = ArrayList()

                for (i in document.documents){
                    val user=i.toObject(User::class.java)!!
                    usersList.add(user)
                }

                if (activity is MembersActivity) {
//                calling Setup UserList in MembersActivity
                    activity.setupMembersList(usersList)
                }
                else if (activity is TaskListActivity){
                    activity.boardMembersDetailList(usersList)
                }
        }
            .addOnFailureListener { e->
                if (activity is MembersActivity){
                    activity.hideProgressDialogue()
                }
                else if (activity is TaskListActivity){
                    activity.hideProgressDialogue()
                }
                Log.e(activity.javaClass.simpleName,"Error while creating a board",e)
            }
    }

//    when searching a member...to get User Details
    fun getMemberDetails(activity:MembersActivity, email:String){
//        searching user with given email from all users
        mFireStore.collection(Constants.USERS).whereEqualTo(Constants.EMAIL,email).get()
            .addOnSuccessListener { document ->
                 if (document.documents.size>0){
//                     got the user with given email..index 0 because we have only one user with a single email address
                     val user=document.documents[0].toObject(User::class.java)!!
                     activity.memberDetails(user)
                 }else{
                      activity.hideProgressDialogue()
                     activity.showErrorSnackBar("No such member found")
                 }
            }
            .addOnFailureListener {e->
                activity.hideProgressDialogue()
                Log.e(activity.javaClass.simpleName,"Error while getting user details",e)
            }
    }

//    function for assigning member to board...or updating in firebase....we got board with updated Assigned to parameter from Members Activity which we are updating in database
    fun assignMemberToBoard(activity: MembersActivity,board: Board,user:User){

        val assignedToHashMap = HashMap<String,Any>()
    assignedToHashMap[Constants.ASSIGNED_TO]=board.assignedTo
//    updating assigned to of a particular board
    mFireStore.collection(Constants.BOARDS).document(board.documentId).update(assignedToHashMap)
        .addOnSuccessListener {
            activity.memberAssignedSuccess(user)
        }
        .addOnFailureListener { e->
            activity.hideProgressDialogue()
            Log.e(activity.javaClass.simpleName,"Error while adding board member",e)
        }
    }
}