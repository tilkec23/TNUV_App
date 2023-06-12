package si.uni_lj.fe.tnuv.umami_burger

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



        replaceFragment(Welcome())

        // change the status bar color
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = ContextCompat.getColor(this, R.color.status_bar_color)






        binding.bottomNavigationView.setOnItemSelectedListener {
            when(it.itemId){
                R.id.nav_feed -> {
                    binding.bottomNavigationView.menu.setGroupCheckable(0, true, true)
                    replaceFragment(FeedFragment())
                }
                R.id.nav_profile -> {
                    binding.bottomNavigationView.menu.setGroupCheckable(0, true, true)
                    val currentUser = auth.currentUser // retrieve the current user here
                    if (currentUser == null) {
                        replaceFragment(LoginFragment())
                    } else
                    replaceFragment(ProfileFragment())
                }
                R.id.nav_nearby -> {
                    binding.bottomNavigationView.menu.setGroupCheckable(0, true, true)
                    replaceFragment(NearbyFragment())
                }
                R.id.nav_notifications -> {
                    binding.bottomNavigationView.menu.setGroupCheckable(0, true, true)
                    replaceFragment(NotificationsFragment())
                }
                else -> false
            }
            true
        }

        // Disable item selection at first
        binding.bottomNavigationView.menu.setGroupCheckable(0, false, true)
    }

    private fun replaceFragment(fragment : Fragment){

        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frame_layout, fragment)
        fragmentTransaction.commit()
    }
}
