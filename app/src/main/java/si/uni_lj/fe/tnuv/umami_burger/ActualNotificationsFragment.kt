package si.uni_lj.fe.tnuv.umami_burger

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.Query
import com.google.firebase.database.ValueEventListener
import si.uni_lj.fe.tnuv.umami_burger.MyApp.Companion.auth








/**
 * A simple [Fragment] subclass.
 * Use the [ProfileFragment.newInstance] factory method to
 * create an instance of this fragment.
 */




class ActualNotificationsFragment : Fragment() {
    // TODO: Rename and change types of parameters

    private var databaseReference: DatabaseReference? = null
    private lateinit var signoutButton: Button


    private lateinit var recyclerView: RecyclerView
    private val burgerPosts: MutableList<BurgerPost> = mutableListOf()
    private var allPostsFetched = false





    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {



        val view = inflater.inflate(R.layout.fragment_actual_notifications, container, false)

        databaseReference = FirebaseDatabase.getInstance().getReference("Posts")

        signoutButton = view.findViewById(R.id.btnSignout)





        signoutButton.setOnClickListener {
            auth.signOut()
            // After signing out, navigate the user back to the login screen
            val loginFragment = LoginFragment.newInstance("", "")  // replace with your parameters
            parentFragmentManager.beginTransaction()
                .replace(R.id.frame_layout, loginFragment)  // replace with your container view ID
                .commit()
        }




        recyclerView = view.findViewById(R.id.recycler_view_notifications_posts)
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





    private var lastKey: String = Long.MAX_VALUE.toString()


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

                        // change the wordReview to string "Somebody liked your review"









                        // Check if the number of likes is greater than 0 before adding to the list
                        if (post.numberOfLikes > 0) {
                            newPosts.add(post)
                            Log.d("Test",post.toString())
                            fetchUserForPost(post, newPosts)
                        }
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

                if (burgerPosts.isEmpty()) {
                    parentFragmentManager.beginTransaction()
                        .replace(R.id.frame_layout, NotificationsFragment())
                        .commit()
                }
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
