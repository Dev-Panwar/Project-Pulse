package dev.panwar.projectpulse.activities


import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.bumptech.glide.Glide
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import de.hdodenhof.circleimageview.CircleImageView
import dev.panwar.projectpulse.R
import dev.panwar.projectpulse.databinding.ActivityMainBinding
import dev.panwar.projectpulse.firebase.FireStoreClass
import dev.panwar.projectpulse.models.User


// Inherited BaseActivity to use it's functions and Navigation view to Access it's menu items
class MainActivity : BaseActivity(), NavigationView.OnNavigationItemSelectedListener {

    private var toolBarMain: Toolbar?=null
    private var nav_profile_img:CircleImageView?=null
    private var nav_username:TextView?=null
    private var drawerLayout:DrawerLayout?=null

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
        FireStoreClass().signInUser(this)
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

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.nav_my_profile -> {
                Toast.makeText(this, "My Profile Clicked", Toast.LENGTH_SHORT).show()

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


    fun updateNavigationUserDetails(user:User){

        Log.d("MainActivity", "updateNavigationUserDetails called with user: ${user.name}")
        Log.d("MainActivity", "User name: ${user.name}")
        Log.d("MainActivity", "User image URL: ${user.image}")
//        if no User image Found then use ic_user_place_holder
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

    }

}
