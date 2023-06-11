package si.uni_lj.fe.tnuv.umami_burger

import android.app.Application
import com.google.firebase.auth.FirebaseAuth

class MyApp : Application() {
    companion object {
        lateinit var auth: FirebaseAuth
            private set
    }

    override fun onCreate() {
        super.onCreate()
        auth = FirebaseAuth.getInstance()
    }
}
