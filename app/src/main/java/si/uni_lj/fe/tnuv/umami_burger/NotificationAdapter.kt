package si.uni_lj.fe.tnuv.umami_burger

import android.content.Context
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

class NotificationAdapter(
    private val context: Context,
    private val notifications: List<Notification>
) : RecyclerView.Adapter<NotificationAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val avatarImageView: ImageView = itemView.findViewById(R.id.avatar_image_view)
        val displayNameTextView: TextView = itemView.findViewById(R.id.display_name_text_view)
        val postTimeTextView: TextView = itemView.findViewById(R.id.post_time_text_view)
        val itemGalleryPostImageView: ImageView = itemView.findViewById(R.id.item_gallery_post_image_imageview)
        val notificationAlertTextView: TextView = itemView.findViewById(R.id.notification_alert)
        // ... other views ...
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recycler_view_item, parent, false)

        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return notifications.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val notification = notifications[position]


    }
}

