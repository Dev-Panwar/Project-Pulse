package dev.panwar.projectpulse.firebase

import android.app.Activity
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import dev.panwar.projectpulse.activities.*
import dev.panwar.projectpulse.models.Board
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
//    no current id in Board() as it auto generates a random id..as here we don't wanna create document with id of current User...
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
    fun loadUserData(activity: Activity){
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
                        activity.updateNavigationUserDetails(loggedInUser)
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

// updating the info in Fire store database of current user Using Hashmap
    fun updateUserProfileData(activity: MyProfileActivity, userHashmap:HashMap<String,Any>){
    mFireStore.collection(Constants.USERS)
        .document(getCurrentUserID()).update(userHashmap).addOnSuccessListener {
            Log.i(activity.javaClass.simpleName,"Profile data updated successfully")
            Toast.makeText(activity,"Profile details updated successfully",Toast.LENGTH_SHORT).show()
            activity.profileUpdateSuccess()
        }.addOnFailureListener {
            activity.hideProgressDialogue()
            Log.i(activity.javaClass.simpleName,"Error while creating a board")
            Toast.makeText(activity,"Error when updating the profile",Toast.LENGTH_SHORT).show()
        }
    }
}