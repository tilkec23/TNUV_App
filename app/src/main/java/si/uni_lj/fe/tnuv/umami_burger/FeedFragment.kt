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
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.recycler_view_item, container, false)

        // Initialize Firebase Database reference
        databaseReference = FirebaseDatabase.getInstance().getReference("Posts")


        recyclerView = view.findViewById(R.id.feed_recyclerview)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = BurgerPostAdapter(burgerPosts)

        fetchPosts()

        return view
    }

    companion object {
        @JvmStatic
        fun newInstance() = FeedFragment()
    }
    private fun fetchPosts() {
        databaseReference?.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                burgerPosts.clear()
                for (postSnapshot in snapshot.children) {
                    val post = postSnapshot.getValue(BurgerPost::class.java)
                    if (post != null) {
                        fetchUserForPost(post)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, "Failed to fetch posts!", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun fetchUserForPost(post: BurgerPost) {
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

                    burgerPosts.add(post)
                    recyclerView.adapter?.notifyDataSetChanged()
                } else {
                    Toast.makeText(context, "Failed to fetch user for post!", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, "Failed to fetch user for post!", Toast.LENGTH_SHORT).show()
            }
        })
    }



}
