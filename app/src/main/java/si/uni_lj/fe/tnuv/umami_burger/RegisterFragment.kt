package si.uni_lj.fe.tnuv.umami_burger

import android.graphics.Paint
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import com.google.android.material.progressindicator.CircularProgressIndicator
import com.google.android.material.textfield.TextInputEditText
import com.google.common.hash.Hashing
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import java.nio.charset.StandardCharsets


/**
 * A simple [Fragment] subclass.
 * Use the [RegisterFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class RegisterFragment : Fragment() {
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var textEmail: TextInputEditText
    private lateinit var textPassword: TextInputEditText
    private lateinit var confirmTextPassword: TextInputEditText
    private lateinit var registerButton: Button
    private lateinit var loginButton: Button
    private lateinit var conditionLength: TextView
    private lateinit var conditionNumber: TextView
    private lateinit var conditionUppercase: TextView
    private lateinit var passwordConditions: TextView

    private lateinit var progressCircular: CircularProgressIndicator

    private lateinit var registerLayout: View

    private lateinit var auth: FirebaseAuth


    private val editTextFocusChangeListener = View.OnFocusChangeListener { view, hasFocus ->
        if (hasFocus) {
            addPaddingToRegisterLayout()
        } else if (!hasFocus && !hasAnyEditTextFocused()) {
            removePaddingFromRegisterLayout()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = MyApp.auth
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
        val view = inflater.inflate(R.layout.fragment_register, container, false)

        registerLayout = view.findViewById<View>(R.id.register_layout)

        textEmail = view.findViewById(R.id.email)
        textPassword = view.findViewById(R.id.password)
        confirmTextPassword = view.findViewById(R.id.confirm_password)
        registerButton = view.findViewById(R.id.register_button)
        loginButton = view.findViewById(R.id.looking_for_login)

        progressCircular = view.findViewById(R.id.progress_bar)

        textEmail.onFocusChangeListener = editTextFocusChangeListener
        textPassword.onFocusChangeListener = editTextFocusChangeListener
        confirmTextPassword.onFocusChangeListener = editTextFocusChangeListener


        conditionLength = view.findViewById(R.id.condition_length)
        conditionNumber = view.findViewById(R.id.condition_number)
        conditionUppercase = view.findViewById(R.id.condition_uppercase)
        passwordConditions = view.findViewById(R.id.password_conditions)

        textPassword.doOnTextChanged { text, _, _, _ ->
            updatePasswordConditions(text.toString())
        }



        registerButton.setOnClickListener {
            progressCircular.visibility = View.VISIBLE
            val enteredText = textEmail.text.toString()
            val enteredPassword = textPassword.text.toString()
            val enteredConfirmPassword = confirmTextPassword.text.toString()

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
            if (enteredPassword != enteredConfirmPassword) {
                //toast that the passwords do not match
                Toast.makeText(requireContext(), "Passwords do not match", Toast.LENGTH_LONG).show()
                progressCircular.visibility = View.GONE
                return@setOnClickListener
            }


            // If it passes all checks then we can create the user
            createAccount(enteredText,hashPassword(enteredConfirmPassword))


            // Perform actions with the entered text
        }

        loginButton.paintFlags = loginButton.paintFlags or Paint.UNDERLINE_TEXT_FLAG
        loginButton.setOnClickListener {
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            transaction.replace(R.id.frame_layout, LoginFragment()) // replace 'fragment_container' with the id of your fragment container.
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
        private const val ARG_PARAM1 = "param1"
        private const val ARG_PARAM2 = "param2"
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            RegisterFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
    private fun updatePasswordConditions(password: String) {
        // check password length
        conditionLength.setTextColor(ContextCompat.getColor(requireContext(), if (password.length >= 8) R.color.successful else R.color.unsuccessful))

        // check for at least one uppercase
        conditionUppercase.setTextColor(ContextCompat.getColor(requireContext(), if (password.any { it.isUpperCase() }) R.color.successful else R.color.unsuccessful))

        // check for at least one digit
        conditionNumber.setTextColor(ContextCompat.getColor(requireContext(), if (password.any { it.isDigit() }) R.color.successful else R.color.unsuccessful))
    }

    fun checkEmailFormat(email: String): Boolean {
        val pattern = Regex("[^@]+@[^.]+\\..+")
        return pattern.matches(email)
    }

    private fun addPaddingToRegisterLayout() {
        registerLayout.setPadding(
            registerLayout.paddingLeft,
            registerLayout.paddingTop,
            registerLayout.paddingRight,
            460
        )
    }

    private fun removePaddingFromRegisterLayout() {
        registerLayout.setPaddingRelative(
            registerLayout.paddingStart,
            registerLayout.paddingTop,
            registerLayout.paddingEnd,
            0
        )
    }
    private fun hasAnyEditTextFocused(): Boolean {
        return textEmail.hasFocus() || textPassword.hasFocus() || confirmTextPassword.hasFocus()
    }

    fun createAccount(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                progressCircular.visibility = View.GONE
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d("TAG", "createUserWithEmail:success")
                    val user = auth.currentUser
                    updateUI(user)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w("TAG", "createUserWithEmail:failure", task.exception)
                    Toast.makeText(
                        requireContext(),
                        "Authentication failed.",
                        Toast.LENGTH_SHORT
                    ).show()
                    updateUI(null)
                }
            }
    }
    fun updateUI(user: FirebaseUser?) {
        // Update your UI here based on user authentication status
    }

    private fun hashPassword(password: String): String {
        // no need to salt I guess
        return Hashing.sha256()
            .hashString(password, StandardCharsets.UTF_8)
            .toString()
    }
}

