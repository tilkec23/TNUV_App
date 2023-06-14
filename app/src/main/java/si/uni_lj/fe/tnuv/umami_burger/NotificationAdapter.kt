package si.uni_lj.fe.tnuv.umami_burger

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class NotificationsAdapter(private val notifications: List<Notification>) : RecyclerView.Adapter<NotificationsAdapter.NotificationViewHolder>() {

    class NotificationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleTextView: TextView = itemView.findViewById(R.id.notification_title)
        val contentTextView: TextView = itemView.findViewById(R.id.notification_content)
        val timeTextView: TextView = itemView.findViewById(R.id.notification_time)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.notification_post_layout, parent, false)
        return NotificationViewHolder(view)
    }

    override fun getItemCount() = notifications.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val notification = notifications[position]

        // Load user avatar
        Glide.with(holder.avatarImageView.context)
            .load(notification.avatarUrl)
            .into(holder.avatarImageView)

        // Set username
        holder.displayNameTextView.text = notification.displayName

        // Format and display time of the like event
        val formattedTime = formatTime(notification.likeEventTime)
        holder.postTimeTextView.text = formattedTime

        // Display who liked the post
        val notificationText = "${notification.likerName} liked your post"
        holder.notificationAlertTextView.text = notificationText

        // Load post image
        Glide.with(holder.postImageView.context)
            .load(notification.postImageUrl)
            .into(holder.postImageView)

        // Set like count
        holder.likeCountTextView.text = "${notification.likeCount} likes"

        // Set comment count
        holder.commentCountTextView.text = "${notification.commentCount} comments"

        // Set rating
        holder.ratingCountTextView.text = "${notification.rating}"
    }

    private fun formatTime(timeInMillis: Long): String {
        val date = Date(timeInMillis)
        val format = SimpleDateFormat("h:mm a, dd MMMM yyyy", Locale.getDefault())
        return format.format(date)
    }

}
