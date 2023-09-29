package dev.panwar.projectpulse.firebase

import android.app.Activity
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import dev.panwar.projectpulse.activities.MainActivity
import dev.panwar.projectpulse.activities.MyProfileActivity
import dev.panwar.projectpulse.activities.SignInActivity
import dev.panwar.projectpulse.activities.SignUpActivity
import dev.panwar.projectpulse.models.User
import dev.panwar.projectpulse.utils.Constants

class FireStoreClass {

// initially fireStore
    private val mFireStore=FirebaseFirestore.getInstance()

    fun registerUser(activity:SignUpActivity, userInfo: User){
//        Adding a new Collection of Data to FireStore DB...with name Users...Then we Giving current user ID to Store in Particular Collection...Then adding all info of Users to the Particular User i.e.(all the parameters of data Class User).
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
}