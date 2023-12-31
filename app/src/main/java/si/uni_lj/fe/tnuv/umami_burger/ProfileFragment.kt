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
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.Query
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

    private var databaseReference: DatabaseReference? = null



    private lateinit var recyclerView: RecyclerView
    private val burgerPosts: MutableList<BurgerPost> = mutableListOf()
    private var allPostsFetched = false




    private val signInLauncher = registerForActivityResult(FirebaseAuthUIActivityResultContract()) { result ->
        // Handle the FirebaseAuthUIAuthenticationResult
        // ...
    }

    //override fun onCreate(savedInstanceState: Bundle?) {
      //  super.onCreate(savedInstanceState)
        //arguments?.let {

        //}
    //}

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {



        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        databaseReference = FirebaseDatabase.getInstance().getReference("Posts")

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



        recyclerView = view.findViewById(R.id.recycler_view_profile_posts)
        val linearLayoutManager = LinearLayoutManager(context)
        linearLayoutManager.reverseLayout = true
        linearLayoutManager.stackFromEnd = true
        recyclerView.layoutManager = linearLayoutManager
        recyclerView.adapter = BurgerPostAdapter(burgerPosts, allPostsFetched)

        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val totalItemCount = linearLayoutManager.itemCount
                val lastVisibleItemPosition = linearLayoutManager.findLastVisibleItemPosition()

                // load more posts if we've scrolled to the bottom of the list
                if (totalItemCount <= (lastVisibleItemPosition + 2)) {
                    loadMorePosts()
                }
            }
        })

        fetchUserInfo()
        fetchUserPosts()



        // Inflate the layout for this fragment
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = FirebaseAuth.getInstance().currentUser
        if(currentUser == null) {
            // not signed in, start sign in process
            // switch to log in fragment
            val loginFragment = LoginFragment.newInstance("", "")  // replace with your parameters
            parentFragmentManager.beginTransaction()
                .replace(R.id.frame_layout, loginFragment)  // replace with your container view ID
                .commit()
        } else {
            // user already signed in, update UI
            // ...


        }
    }



    companion object {
        @JvmStatic
        fun newInstance() = FeedFragment()

        // item types for the RecyclerView
        const val VIEW_TYPE_POST = 0
        const val VIEW_TYPE_END = 1
    }

    private var lastKey: String = Long.MAX_VALUE.toString()
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
                        val timestampString = snapshot.child("userAccountCreationTime").value.toString()
                        val timestamp = try {
                            timestampString.toLong()
                        } catch (e: NumberFormatException) {
                            Log.e("ProfileFragment", "Failed to convert $timestampString to Long", e)
                            0L
                        }
                        val date = Date(timestamp)
                        val format = SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault())
                        val userAccountCreationTime = format.format(date)
                        displayNameTextView.text = userName
                        descriptionTextView.text = userDescription
                        postTimeTextView.text = userAccountCreationTime


                        //handling images with glide:
                        context?.let { it1 -> Glide.with(it1).load(imageUrl).into(avatarImageView) }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(context, "Failed to fetch user details!", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }

    private fun fetchUserPosts() {
        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid

        var query: Query? = databaseReference?.orderByChild("userId")?.equalTo(currentUserId)?.limitToLast(10)

        // if lastKey is not the maximum value, a page of data has already been loaded, so get the next one
        if (lastKey != Long.MAX_VALUE.toString()) {
            query = databaseReference?.orderByChild("timestamp")?.endAt(lastKey)?.limitToLast(11)
        }

        query?.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val newPosts = mutableListOf<BurgerPost>()

                for (postSnapshot in dataSnapshot.children) {
                    val post = postSnapshot.getValue(BurgerPost::class.java)
                    if (post != null) {
                        // Get likes
                        for (likeSnapshot in postSnapshot.child("likes").children) {
                            val userId = likeSnapshot.key
                            val liked = likeSnapshot.getValue(Boolean::class.java) ?: false
                            if (userId != null && liked) {
                                post.likes[userId] = true
                            }
                        }

                        post.numberOfLikes = post.likes.size
                        newPosts.add(post)
                        fetchUserForPost(post, newPosts)
                    }
                }

                // remove the extra post if this isn't the first page of data
                if (lastKey != Long.MAX_VALUE.toString()) {
                    newPosts.removeAt(newPosts.size - 1)
                }

                // if the fetched list size is less than the limit, there are no more posts to load
                if (newPosts.size < 10) {
                    allPostsFetched = true
                }

                // get the key for the next page of data
                if (newPosts.size > 0) {
                    lastKey = newPosts[newPosts.size - 1].timestamp.toString()
                }

                // add newPosts to the start of the list because of Firebase's reverse ordering
                burgerPosts.addAll(0, newPosts)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Toast.makeText(context, "Failed to fetch posts!", Toast.LENGTH_SHORT).show()
            }
        })
    }



    private fun fetchUserForPost(post: BurgerPost, newPosts: MutableList<BurgerPost>) {
        val usersReference = FirebaseDatabase.getInstance().getReference("Users")
        usersReference.child(post.userId).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val userName = snapshot.child("userName").value.toString()
                    val userDescription = snapshot.child("userDescription").value.toString()
                    val imageUrl = snapshot.child("profileImage").value.toString()
                    val timestampString = snapshot.child("userAccountCreationTime").value.toString()
                    val timestamp = try {
                        timestampString.toLong()
                    } catch (e: NumberFormatException) {
                        Log.e("ProfileFragment", "Failed to convert $timestampString to Long", e)
                        0L
                    }

                    // Create a new user object with the fetched data
                    val user = User(post.userId, userName, userDescription, imageUrl, timestamp)

                    // Assign the new user to the post
                    post.user = user

                    // Only call notifyDataSetChanged() once, after all posts have been fetched
                    if (newPosts.size == burgerPosts.size) {
                        recyclerView.adapter?.notifyDataSetChanged()
                    }
                } else {
                    Toast.makeText(context, "Failed to fetch user for post!", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, "Failed to fetch user for post!", Toast.LENGTH_SHORT).show()
            }
        })
    }

    fun loadMorePosts() {
        if(!allPostsFetched){
            fetchUserPosts()
        }
    }

}
