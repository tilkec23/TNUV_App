package si.uni_lj.fe.tnuv.umami_burger

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class ActualNotificationsFragment : Fragment() {

    private val notifications: MutableList<Notification> = mutableListOf()
    private lateinit var recyclerView: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.recycler_view_item, container, false)

        recyclerView = view.findViewById(R.id.feed_recyclerview)
        val linearLayoutManager = LinearLayoutManager(context)
        recyclerView.layoutManager = linearLayoutManager
        recyclerView.adapter = NotificationAdapter(notifications)

        fetchNotifications()

        return view
    }

    private fun fetchNotifications() {
        // TODO: Implement your logic for fetching notifications here
    }

    companion object {
        @JvmStatic
        fun newInstance() = ActualNotificationsFragment()
    }
}
