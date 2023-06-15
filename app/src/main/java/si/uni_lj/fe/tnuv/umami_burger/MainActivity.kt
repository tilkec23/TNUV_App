package si.uni_lj.fe.tnuv.umami_burger

import NewPostFragment
import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import si.uni_lj.fe.tnuv.umami_burger.MyApp.Companion.auth
import si.uni_lj.fe.tnuv.umami_burger.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {

    private lateinit var binding : ActivityMainBinding





    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)



        replaceFragment(Welcome(), "WelcomeFragment")

        // change the status bar color
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = ContextCompat.getColor(this, R.color.status_bar_color)






        binding.bottomNavigationView.setOnItemSelectedListener {
            when(it.itemId){
                R.id.nav_feed -> {
                    binding.bottomNavigationView.menu.setGroupCheckable(0, true, true)
                    replaceFragment(FeedFragment(),"FeedFragment")
                }
                R.id.nav_profile -> {
                    binding.bottomNavigationView.menu.setGroupCheckable(0, true, true)
                    val currentUser = auth.currentUser // retrieve the current user here
                    if (currentUser == null) {
                        replaceFragment(LoginFragment(), "LoginFragment")
                    } else
                    replaceFragment(ProfileFragment(), "ProfileFragment")
                }
                R.id.nav_nearby -> {
                    binding.bottomNavigationView.menu.setGroupCheckable(0, true, true)
                    replaceFragment(NearbyFragment(), "NearbyFragment")
                }
                R.id.nav_notifications -> {
                    binding.bottomNavigationView.menu.setGroupCheckable(0, true, true)
                    val currentUser = auth.currentUser // retrieve the current user here
                    if (currentUser == null) {
                        replaceFragment(LoginFragment(), "LoginFragment")
                    } else
                        replaceFragment(ActualNotificationsFragment(), "ActualNotificationsFragment")
                }

                else -> false
            }
            true
        }
        binding.fab.setOnClickListener {
            replaceFragment(NewPostFragment(), "NewPostFragment")
        }
        // Disable item selection at first
        binding.bottomNavigationView.menu.setGroupCheckable(0, false, true)
    }

    private fun replaceFragment(fragment : Fragment, tag: String) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frame_layout, fragment, tag)
        fragmentTransaction.addToBackStack(tag)
        fragmentTransaction.commit()
    }
    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount > 1) {
            supportFragmentManager.popBackStack()
            val fragmentTag = supportFragmentManager.getBackStackEntryAt(supportFragmentManager.backStackEntryCount - 2).name
            when (fragmentTag) {
                "FeedFragment" -> binding.bottomNavigationView.selectedItemId = R.id.nav_feed
                "ProfileFragment" -> binding.bottomNavigationView.selectedItemId = R.id.nav_profile
                "NearbyFragment" -> binding.bottomNavigationView.selectedItemId = R.id.nav_nearby
                "NotificationsFragment" -> binding.bottomNavigationView.selectedItemId = R.id.nav_notifications
            }
        } else {
            super.onBackPressed()
        }
    }
}
