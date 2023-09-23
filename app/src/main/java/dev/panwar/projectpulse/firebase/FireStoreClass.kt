package dev.panwar.projectpulse.firebase

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
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

    fun signInUser(activity: SignInActivity){
//        same as above function just set change to get...refer to code explanation by above comments
        mFireStore.collection(Constants.USERS)
            .document(getCurrentUserID()).get().addOnSuccessListener { document->
//                we stored the currently Logged in User by converting Current User ID(document) to object of type User data class
                  val loggedInUser = document.toObject(User::class.java)
                if (loggedInUser!=null) {
//                calling function of signInactivity and passing current user
                    activity.signInSuccess(loggedInUser)
                }
            }.addOnFailureListener {
//                 activity.javaClass.name gives the name of Activity
                    e-> Log.e(activity.javaClass.name,"Error Writing Document")
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