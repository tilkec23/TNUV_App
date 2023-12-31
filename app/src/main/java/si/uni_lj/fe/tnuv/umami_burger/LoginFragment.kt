package si.uni_lj.fe.tnuv.umami_burger

import android.graphics.Paint
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.material.progressindicator.CircularProgressIndicator
import com.google.android.material.textfield.TextInputEditText
import com.google.common.hash.Hashing
import com.google.firebase.auth.FirebaseUser
import si.uni_lj.fe.tnuv.umami_burger.MyApp.Companion.auth
import java.nio.charset.StandardCharsets

/**
 * A simple [Fragment] subclass.
 * Use the [RegisterFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class LoginFragment : Fragment() {

    private lateinit var textEmail: TextInputEditText
    private lateinit var textPassword: TextInputEditText
    private lateinit var loginButton: Button
    private lateinit var registerButton: Button

    private lateinit var progressCircular: CircularProgressIndicator


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            // param1 = it.getString(ARG_PARAM1)
            // param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_login, container, false)

        textEmail = view.findViewById(R.id.email)
        textPassword = view.findViewById(R.id.password)
        loginButton = view.findViewById(R.id.login_button)
        registerButton = view.findViewById(R.id.looking_for_register)
        progressCircular = view.findViewById(R.id.progress_bar)

        loginButton.setOnClickListener {
            progressCircular.visibility = View.VISIBLE
            val enteredText = textEmail.text.toString()
            val enteredPassword = textPassword.text.toString()
            // Perform actions with the entered text

            val containsAtAndDot = checkEmailFormat(enteredText)

            if(enteredText.length < 6){
                textEmail.error = "Please enter your email"
                progressCircular.visibility = View.GONE
                return@setOnClickListener
            }
            if(!containsAtAndDot){
                textEmail.error = "Please enter a valid email"
                progressCircular.visibility = View.GONE
                return@setOnClickListener
            }
            if(TextUtils.isEmpty(enteredText)){
                textEmail.error = "Please enter your email"
                progressCircular.visibility = View.GONE
                return@setOnClickListener
            }

            if(TextUtils.isEmpty(enteredPassword)){
                textPassword.error = "Please enter your password"
                progressCircular.visibility = View.GONE
                return@setOnClickListener
            }

            signInWithEmail(enteredText, hashPassword(enteredPassword))

        }


        registerButton.paintFlags = registerButton.paintFlags or Paint.UNDERLINE_TEXT_FLAG
        registerButton.setOnClickListener {
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            transaction.replace(R.id.frame_layout, RegisterFragment()) // replace 'fragment_container' with the id of your fragment container.
            transaction.commit()
        }


        return view
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment LoginFragment.
         */
        // TODO: Rename and change types and number of parameters
        private const val ARG_PARAM1 = "param1"
        private const val ARG_PARAM2 = "param2"
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
        RegisterFragment().apply {
          arguments = Bundle().apply {
            putString(ARG_PARAM1, param1)
          putString(ARG_PARAM2, param2)
        }
        }
    }
    fun checkEmailFormat(email: String): Boolean {
        val pattern = Regex("[^@]+@[^.]+\\..+")
        return pattern.matches(email)
    }
    fun signInWithEmail(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(requireActivity()) { task ->
                progressCircular.visibility = View.GONE
                if (task.isSuccessful) {
                    Log.d("TAG", "signInWithEmail:success")
                    val user = auth.currentUser
                    // Here you could use a method updateUI(user) as in your original function
                    // Switch to the profile fragment
                    val profileFragment = ProfileFragment.newInstance()  // replace with your parameters
                    parentFragmentManager.beginTransaction()
                        .replace(R.id.frame_layout, profileFragment)  // replace with your container view ID
                        .commit()
                } else {
                    Log.w("TAG", "signInWithEmail:failure", task.exception)
                    // Handle error and show it to user
                    var errorMessage = "Authentication failed."
                    if (task.exception != null) {
                        errorMessage = task.exception!!.message.toString()
                    }

                    Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show()
                    // updateUI(null)
                }
            }
    }
    private fun hashPassword(password: String): String {
        // no need to salt I guess
        return Hashing.sha256()
            .hashString(password, StandardCharsets.UTF_8)
            .toString()
    }


}