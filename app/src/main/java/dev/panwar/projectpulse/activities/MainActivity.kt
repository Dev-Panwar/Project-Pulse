package dev.panwar.projectpulse.activities


import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import dev.panwar.projectpulse.R
import dev.panwar.projectpulse.databinding.ActivityMainBinding


// Inherited BaseActivity to use it's functions and Navigation view to Access it's menu items
class MainActivity : BaseActivity(), NavigationView.OnNavigationItemSelectedListener {

    private var toolBarMain: Toolbar?=null
    private var binding:ActivityMainBinding?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        toolBarMain=findViewById(R.id.toolbar_main_activity)
        setupActionBar()
//       For NavigationView.OnNavigationItemSelectedListener
        binding?.navView?.setNavigationItemSelectedListener(this)
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
        if (binding?.drawerLayout!!.isDrawerOpen(GravityCompat.START)){
            binding?.drawerLayout!!.closeDrawer(GravityCompat.START)
        }
        else{
            binding?.drawerLayout!!.openDrawer(GravityCompat.START)
        }
    }

    override fun onBackPressed() {
        if (binding?.drawerLayout!!.isDrawerOpen(GravityCompat.START)){
            binding?.drawerLayout!!.closeDrawer(GravityCompat.START)
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
        binding?.drawerLayout!!.closeDrawer(GravityCompat.START)
        return true
    }

}
