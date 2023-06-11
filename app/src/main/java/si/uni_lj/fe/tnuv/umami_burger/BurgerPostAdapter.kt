package si.uni_lj.fe.tnuv.umami_burger

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

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
        // Use Picasso or another image loading library to load images
        // Picasso.get().load(burgerPost.avatarUrl).into(holder.avatarImageView)
        holder.displayNameTextView.text = burgerPost.nameOfPerson
        holder.postTimeTextView.text = burgerPost.postTimestamp.toString()
        // Picasso.get().load(burgerPost.postImageUrl).into(holder.postImageView)
        holder.descriptionTextView.text = burgerPost.wordReview
        holder.likeCountTextView.text = "${burgerPost.numberOfLikes} likes"
        holder.commentCountTextView.text = "${burgerPost.numberOfComments} comments"
        holder.ratingCountTextView.text = burgerPost.ratingScore.calculatedRating.toString()
    }
}

