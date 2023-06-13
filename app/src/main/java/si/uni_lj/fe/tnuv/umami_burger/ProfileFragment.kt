package si.uni_lj.fe.tnuv.umami_burger

import android.os.Bundle
import android.text.format.DateUtils
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.google.android.material.imageview.ShapeableImageView
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import si.uni_lj.fe.tnuv.umami_burger.MyApp.Companion.auth
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"
private lateinit var signoutButton: Button
private lateinit var btnEditProfile: Button

private lateinit var avatarImageView: ShapeableImageView
private lateinit var displayNameTextView: TextView
private lateinit var postTimeTextView: TextView
private lateinit var descriptionTextView: TextView


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
        btnEditProfile = view.findViewById<Button>(R.id.btnEditProfile)

        avatarImageView = view.findViewById(R.id.avatar_image_view)
        displayNameTextView = view.findViewById(R.id.display_name_text_view)
        postTimeTextView = view.findViewById(R.id.post_time_text_view)
        descriptionTextView = view.findViewById(R.id.description_textview)


        signoutButton.setOnClickListener {
            auth.signOut()
            // After signing out, navigate the user back to the login screen
            val loginFragment = LoginFragment.newInstance("", "")  // replace with your parameters
            parentFragmentManager.beginTransaction()
                .replace(R.id.frame_layout, loginFragment)  // replace with your container view ID
                .commit()
        }
        btnEditProfile.setOnClickListener {
            val profileEditFragment = ProfileEditFragment.newInstance("","")  // replace with your parameters if any
            parentFragmentManager.beginTransaction()
                .replace(R.id.frame_layout, profileEditFragment, "ProfileEditFragment")  // replace with your container view ID
                .addToBackStack("ProfileEditFragment")
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

        avatarImageView = view.findViewById(R.id.avatar_image_view)
        displayNameTextView = view.findViewById(R.id.display_name_text_view)
        postTimeTextView = view.findViewById(R.id.post_time_text_view)
        descriptionTextView = view.findViewById(R.id.description_textview)

        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = FirebaseAuth.getInstance().currentUser
        if(currentUser == null) {
            // not signed in, start sign in process
            startSignIn()
        } else {
            // user already signed in, update UI
            // ...
            fetchUserInfo()
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
    private fun fetchUserInfo() {
        val user = FirebaseAuth.getInstance().currentUser
        user?.let {
            val userId = user.uid
            val dbRef = FirebaseDatabase.getInstance().getReference("Users/$userId")

            dbRef.addValueEventListener(object: ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if(snapshot.exists()) {
                        val userName = snapshot.child("userName").value.toString()
                        val userDescription = snapshot.child("userDescription").value.toString()
                        val imageUrl = snapshot.child("profileImage").value.toString()
                        val timestamp = snapshot.child("userAccountCreationTime").value.toString().toLong()
                        val date = Date(timestamp)
                        val format = SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault())
                        val userAccountCreationTime = format.format(date)
                        displayNameTextView.text = userName
                        descriptionTextView.text = userDescription
                        postTimeTextView.text = userAccountCreationTime
                        // Here you need to handle the image URL and display it on imageView
                        // I am not sure how you handle images, so I will leave this part empty

                        context?.let { it1 -> Glide.with(it1).load(imageUrl).into(avatarImageView) }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(context, "Failed to fetch user details!", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }
}
