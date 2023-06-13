package si.uni_lj.fe.tnuv.umami_burger

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class BurgerPostAdapter(private val burgerPosts: List<BurgerPost>) : RecyclerView.Adapter<BurgerPostAdapter.BurgerPostViewHolder>() {

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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BurgerPostViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.image_post_layout, parent, false)
        return BurgerPostViewHolder(view)
    }

    override fun getItemCount() = burgerPosts.size

    override fun onBindViewHolder(holder: BurgerPostViewHolder, position: Int) {
        val burgerPost = burgerPosts[position]
        val user = burgerPost.user

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
        val userPostTime = format.format(date)
        holder.postTimeTextView.text = userPostTime
        Glide.with(holder.itemView)
            .load(burgerPost.imageUrl)
            .into(holder.postImageView)



        holder.descriptionTextView.text = burgerPost.burgerName
        holder.likeCountTextView.text = "${burgerPost.numberOfLikes} likes"
        holder.commentCountTextView.text = "${burgerPost.comments.size} comments"
        holder.ratingCountTextView.text = listOf(burgerPost.burgerOverallRating, burgerPost.burgerPattyRating, burgerPost.burgerVeggiesRating, burgerPost.burgerSauceRating).average().toString()
    }

}

