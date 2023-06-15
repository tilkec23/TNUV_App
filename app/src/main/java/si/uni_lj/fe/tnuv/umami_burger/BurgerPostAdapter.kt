package si.uni_lj.fe.tnuv.umami_burger

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.progressindicator.CircularProgressIndicator
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class BurgerPostAdapter(private val burgerPosts: List<BurgerPost>, private val allPostsFetched: Boolean) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val VIEW_TYPE_ITEM = 0
    private val VIEW_TYPE_END_INDICATOR = 1

    private var databaseReference: DatabaseReference? = null



    class BurgerPostViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val avatarImageView: ImageView = itemView.findViewById(R.id.avatar_image_view)
        val displayNameTextView: TextView = itemView.findViewById(R.id.display_name_text_view)
        val postTimeTextView: TextView = itemView.findViewById(R.id.post_time_text_view)
        val postImageView: ImageView = itemView.findViewById(R.id.item_gallery_post_image_imageview)
        val descriptionTextView: TextView = itemView.findViewById(R.id.description_textview)
        val likeCountTextView: TextView = itemView.findViewById(R.id.like_count_textview)
        val commentCountTextView: TextView = itemView.findViewById(R.id.comment_count_textview)
        val ratingCountTextView: TextView = itemView.findViewById(R.id.rating_count_textview)
    }
    class EndIndicatorViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_ITEM) {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.image_post_layout, parent, false)
            BurgerPostViewHolder(view)
        } else {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.end_indicator_layout, parent, false)
            EndIndicatorViewHolder(view)
        }

    }

    override fun getItemCount() = if (allPostsFetched) burgerPosts.size + 1 else burgerPosts.size
    override fun getItemViewType(position: Int): Int {
        return if (position == burgerPosts.size && allPostsFetched) {
            VIEW_TYPE_END_INDICATOR
        } else {
            VIEW_TYPE_ITEM
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is BurgerPostViewHolder) {
            val burgerPost = burgerPosts[position]
            val user = burgerPost.user
            val burgerPlace = burgerPost.burgerPlace

            // User data can be accessed from the user object
            if (user != null) {
                holder.displayNameTextView.text = user.userName
                Glide.with(holder.itemView)
                    .load(user.profileImage)
                    .into(holder.avatarImageView)
            }

            val timestampString = burgerPost.timestamp.toString()
            val timestamp = try {
                timestampString.toLong()
            } catch (e: NumberFormatException) {
                Log.e("ProfileFragment", "Failed to convert $timestampString to Long", e)
                0L
            }
            val date = Date(timestamp)
            val format = SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault())
            val userPostTime = format.format(date) + "   at   " +burgerPlace
            holder.postTimeTextView.text = userPostTime
            Glide.with(holder.itemView)
                .load(burgerPost.imageUrl)
                .into(holder.postImageView)


            holder.descriptionTextView.text = burgerPost.burgerName
            holder.likeCountTextView.text = "${burgerPost.numberOfLikes} likes"
            holder.commentCountTextView.text = "Comments are disabled"
            val average = listOf(burgerPost.burgerOverallRating, burgerPost.burgerPattyRating, burgerPost.burgerVeggiesRating, burgerPost.burgerSauceRating).average()
            holder.ratingCountTextView.text = String.format("%.2f", average)


            // Set the like icon based on whether the user has liked the post or not
            val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
            if (burgerPost.likes.containsKey(userId)) {
                holder.likeCountTextView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.baseline_thumb_up_24, 0, 0, 0)
            } else {
                holder.likeCountTextView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.thumb_up_off_alt_24, 0, 0, 0)
            }

            holder.likeCountTextView.setOnClickListener {
                val postId = burgerPost.title
                val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
                val databaseReference = FirebaseDatabase.getInstance().getReference("Posts")

                if (burgerPost.likes.containsKey(userId)) {
                    // The post is already liked, so unlike it
                    burgerPost.likes.remove(userId)
                    databaseReference.child(postId).child("likes").child(userId).removeValue()
                } else {
                    // The post is not liked, so like it
                    burgerPost.likes[userId] = true
                    databaseReference.child(postId).child("likes").child(userId).setValue(true)
                }

                // Update the like count and UI
                burgerPost.numberOfLikes = burgerPost.likes.size
                holder.likeCountTextView.text = "${burgerPost.numberOfLikes} likes"
                if (burgerPost.likes.containsKey(userId)) {
                    holder.likeCountTextView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.baseline_thumb_up_24, 0, 0, 0)
                } else {
                    holder.likeCountTextView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.thumb_up_off_alt_24, 0, 0, 0)
                }
            }

        }

    }
}

