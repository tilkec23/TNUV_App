package si.uni_lj.fe.tnuv.umami_burger

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.setMargins
import androidx.core.view.setPadding
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.progressindicator.CircularProgressIndicator
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.Query
import com.google.firebase.database.ValueEventListener

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class FeedFragment : Fragment() {

    fun dpToPx(dp: Int): Int {
        val density = resources.displayMetrics.density
        return (dp * density).toInt()
    }

    private var databaseReference: DatabaseReference? = null

    private val burgerPosts: MutableList<BurgerPost> = mutableListOf()
    private lateinit var recyclerView: RecyclerView
    private var allPostsFetched = false
    private lateinit var progressBar: CircularProgressIndicator

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.recycler_view_item, container, false)

        // Initialize Firebase Database reference
        databaseReference = FirebaseDatabase.getInstance().getReference("Posts")

        progressBar = view.findViewById(R.id.progress_bar)
        recyclerView = view.findViewById(R.id.feed_recyclerview)
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

        fetchPosts()



        return view
    }

    companion object {
        @JvmStatic
        fun newInstance() = FeedFragment()

        // item types for the RecyclerView
        const val VIEW_TYPE_POST = 0
        const val VIEW_TYPE_END = 1
    }
    // initialize the lastKey to the maximum value possible
    private var lastKey: String = Long.MAX_VALUE.toString()

    private fun fetchPosts() {
        progressBar.visibility = View.VISIBLE
        var query: Query? = databaseReference?.orderByChild("timestamp")?.limitToLast(10)

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
                progressBar.visibility = View.GONE
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
            fetchPosts()
        }
    }



}
