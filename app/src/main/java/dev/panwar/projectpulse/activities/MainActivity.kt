package dev.panwar.projectpulse.activities


import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import de.hdodenhof.circleimageview.CircleImageView
import dev.panwar.projectpulse.R
import dev.panwar.projectpulse.adapters.BoardItemsAdapter
import dev.panwar.projectpulse.firebase.FireStoreClass
import dev.panwar.projectpulse.models.Board
import dev.panwar.projectpulse.models.User
import dev.panwar.projectpulse.utils.Constants


// Inherited BaseActivity to use it's functions and Navigation view to Access it's menu items
class MainActivity : BaseActivity(), NavigationView.OnNavigationItemSelectedListener {

    private var toolBarMain: Toolbar?=null
    private var nav_profile_img:CircleImageView?=null
    private var nav_username:TextView?=null
    private var drawerLayout:DrawerLayout?=null

    private lateinit var mUserName:String

    companion object {
//        request code for starting MyProfile activity for result
        const val MY_PROFILE_REQUEST_CODE:Int = 11
//        for reloading Board List when new board added
        const val CREATE_BOARD_REQUEST_CODE:Int=12
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        toolBarMain=findViewById(R.id.toolbar_main_activity)
        drawerLayout=findViewById(R.id.drawer_layout)
        setupActionBar()

//        As user name and user image are in different layout and in headerView of navigation view below is the way to access the properties
//        start
        val navView = findViewById<NavigationView>(R.id.nav_view)
      // Find the header view within NavigationView
        val headerView = navView.getHeaderView(0)

      // Find the ImageView and TextView in the header view
        nav_profile_img = headerView.findViewById(R.id.nav_user_image)
        nav_username = headerView.findViewById(R.id.nav_username)
//       For NavigationView.OnNavigationItemSelectedListener
        navView.setNavigationItemSelectedListener(this)
//        end

//        readBoardList=true is just an Optimization that when we updateNavigationDetails it does not load Board Data Also as updateNavigationDetails uses getBoardList function...it should only load User data to update in Navigation view header
//        true means board data will reload
        FireStoreClass().loadUserData(this,true)


//        For Adding a Board on clicking floating add button
        findViewById<FloatingActionButton>(R.id.fab_create_board).setOnClickListener {
            val intent=Intent(this,CreateBoardActivity::class.java)
//            sending Value UserName to CreateBoard Activity...
            intent.putExtra(Constants.NAME,mUserName)
            startActivityForResult(intent, CREATE_BOARD_REQUEST_CODE)
        }
    }

    private fun setupActionBar(){
        setSupportActionBar(toolBarMain)
        toolBarMain?.setNavigationIcon(R.drawable.ic_action_navigation_menu)
        toolBarMain?.setNavigationOnClickListener {
//            whenever we click on Navigation burger icon on toolbar
            toggleDrawer()
        }
    }

    private fun toggleDrawer() {
//        if drawer open then we close drawer else we open the drawer
        if (drawerLayout!!.isDrawerOpen(GravityCompat.START)){
            drawerLayout!!.closeDrawer(GravityCompat.START)
        }
        else{
            drawerLayout!!.openDrawer(GravityCompat.START)
        }
    }

    override fun onBackPressed() {
        if (drawerLayout!!.isDrawerOpen(GravityCompat.START)){
            drawerLayout!!.closeDrawer(GravityCompat.START)
        }
        else{
            doubleBackToExit()
        }
    }



//    for Populating the board List in UI using Recycler View
    fun populateBoardListToUI(boardList:ArrayList<Board>){
        hideProgressDialogue()

        if (boardList.size>0){
           val rvBoardList = findViewById<RecyclerView>(R.id.rv_boards_list)
//            making recycler view visible and no boards available text gone
            rvBoardList.visibility=View.VISIBLE
            findViewById<TextView>(R.id.tv_no_boards_available).visibility=View.GONE
            rvBoardList.layoutManager=LinearLayoutManager(this)
//            so that our board list has fixed list can adapter cannot change it
            rvBoardList.setHasFixedSize(true)

            val adapter=BoardItemsAdapter(this,boardList)
            rvBoardList.adapter=adapter

        }else{

            findViewById<RecyclerView>(R.id.rv_boards_list).visibility=View.GONE
            findViewById<TextView>(R.id.tv_no_boards_available).visibility=View.VISIBLE
        }
    }

//    this function is for if if any item is selected from the menu of Navigation view
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.nav_my_profile -> {
                val intent = Intent(this, MyProfileActivity::class.java)
//                started the Activity for result because we need to update User image and Data in Header of Navigation view like image and name after user Update Something in MyProfile activity
                startActivityForResult(intent, MY_PROFILE_REQUEST_CODE)

            }
            R.id.nav_sign_out -> {
              FirebaseAuth.getInstance().signOut()
//                moving back to the intro activity
                val intent=Intent(this,IntroActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                finish()
            }
        }
        drawerLayout!!.closeDrawer(GravityCompat.START)
        return true
    }


    fun updateNavigationUserDetails(user:User,readBoardsList:Boolean){

        Log.d("MainActivity", "updateNavigationUserDetails called with user: ${user.name}")
        Log.d("MainActivity", "User name: ${user.name}")
        Log.d("MainActivity", "User image URL: ${user.image}")
//        if no User image Found then use ic_user_place_holder

        mUserName=user.name

        nav_profile_img?.let {
            Glide
                .with(this)
                .load(user.image)
                .centerCrop()
                .placeholder(R.drawable.ic_user_place_holder)
                .into(it)
        }

        // Set the username text
        nav_username?.text = user.name

        if (readBoardsList){
            showProgressDialog(resources.getString(R.string.please_wait))
            FireStoreClass().getBoardList(this)
        }

    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

//        when we updated the profile details saying firebase to reload data and update in Navigation view header
        if (resultCode==Activity.RESULT_OK && requestCode== MY_PROFILE_REQUEST_CODE){
//            here we didn't passes true with this, as we only need to reload User data only and not board data again
            FireStoreClass().loadUserData(this)
        }
//        for updating the Boards in View as soon as new board Created
        else if (resultCode==Activity.RESULT_OK && requestCode== CREATE_BOARD_REQUEST_CODE){
            FireStoreClass().getBoardList(this)
        }
        else{
            Log.e("cancelled","Cancelled")
        }
    }

}
