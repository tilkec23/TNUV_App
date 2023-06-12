package si.uni_lj.fe.tnuv.umami_burger

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import si.uni_lj.fe.tnuv.umami_burger.MyApp.Companion.auth

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"
private lateinit var signoutButton: Button

/**
 * A simple [Fragment] subclass.
 * Use the [ProfileFragment.newInstance] factory method to
 * create an instance of this fragment.
 */




class ProfileFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var burgerPosts: List<BurgerPost>
    private lateinit var recyclerView: RecyclerView


    private val signInLauncher = registerForActivityResult(FirebaseAuthUIActivityResultContract()) { result ->
        // Handle the FirebaseAuthUIAuthenticationResult
        // ...
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        signoutButton = view.findViewById(R.id.btnSignout)

        signoutButton.setOnClickListener {
            auth.signOut()
            // After signing out, navigate the user back to the login screen
            val loginFragment = LoginFragment.newInstance("", "")  // replace with your parameters
            parentFragmentManager.beginTransaction()
                .replace(R.id.frame_layout, loginFragment)  // replace with your container view ID
                .commit()
        }

        // Define your burgerPosts here
        burgerPosts = listOf(
            BurgerPost(
                nameOfPerson = "John Doe",
                wordReview = "This burger was amazing!",
                numberOfLikes = 5,
                numberOfComments = 3,
                comments = listOf(
                    BurgerPost.Comment(
                        commenterName = "Jane Smith",
                        comment = "Delicious burger!",
                        commentTimestamp = System.currentTimeMillis() - 10_000
                    ),
                    BurgerPost.Comment(
                        commenterName = "Bob Baker",
                        comment = "Wow, looks amazing.",
                        commentTimestamp = System.currentTimeMillis() - 5_000
                    )
                ),
                location = BurgerPost.Location(
                    coordinates = Pair(48.8566, 2.3522),
                    placeId = "ChIJD7fiBh9u5kcRYJSMaMOCCwQ"
                ),
                nameOfBurger = "Classic Cheeseburger",
                priceOfBurger = 7.99f,
                postTimestamp = System.currentTimeMillis(),
                ratingScore = BurgerPost.RatingScore(
                    pattyRating = 4.2f,
                    veggieRating = 3.7f,
                    sauceRating = 4.5f,
                    overallRating = 4.0f,
                    calculatedRating = 4.1f
                ),
                reviewID = 123
            ),
            BurgerPost(
                nameOfPerson = "John Doe",
                wordReview = "This burger was amazing!",
                numberOfLikes = 5,
                numberOfComments = 3,
                comments = listOf(
                    BurgerPost.Comment(
                        commenterName = "Jane Smith",
                        comment = "Delicious burger!",
                        commentTimestamp = System.currentTimeMillis() - 10_000
                    ),
                    BurgerPost.Comment(
                        commenterName = "Bob Baker",
                        comment = "Wow, looks amazing.",
                        commentTimestamp = System.currentTimeMillis() - 5_000
                    )
                ),
                location = BurgerPost.Location(
                    coordinates = Pair(48.8566, 2.3522),
                    placeId = "ChIJD7fiBh9u5kcRYJSMaMOCCwQ"
                ),
                nameOfBurger = "Classic Cheeseburger",
                priceOfBurger = 7.99f,
                postTimestamp = System.currentTimeMillis(),
                ratingScore = BurgerPost.RatingScore(
                    pattyRating = 4.2f,
                    veggieRating = 3.7f,
                    sauceRating = 4.5f,
                    overallRating = 4.0f,
                    calculatedRating = 4.1f
                ),
                reviewID = 123
            )
            // add more BurgerPost instances here
        )

        recyclerView = view.findViewById(R.id.recycler_view_profile_posts)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = BurgerPostAdapter(burgerPosts)





        // Inflate the layout for this fragment
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = FirebaseAuth.getInstance().currentUser
        if(currentUser == null) {
            // not signed in, start sign in process
            startSignIn()
        } else {
            // user already signed in, update UI
            // ...
        }
    }

    private fun startSignIn() {
        val signInIntent = AuthUI.getInstance()
            .createSignInIntentBuilder()
            // ... options ...
            .build()

        signInLauncher.launch(signInIntent)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ProfileFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ProfileFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
