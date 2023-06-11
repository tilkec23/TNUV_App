package si.uni_lj.fe.tnuv.umami_burger

import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.setMargins
import androidx.core.view.setPadding
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class FeedFragment : Fragment() {

    fun dpToPx(dp: Int): Int {
        val density = resources.displayMetrics.density
        return (dp * density).toInt()
    }

    private lateinit var burgerPosts: List<BurgerPost>
    private lateinit var recyclerView: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.recycler_view_item, container, false)

        // Define your burgerPosts here
        burgerPosts = listOf(
            BurgerPost(
                nameOfPerson = "John Doe",
                wordReview = "This burger was amazing!",
                numberOfLikes = 5,
                numberOfComments = 3,
                comments = listOf(
                    BurgerPost.Comment(
                        commenterName = "Jane Smith",
                        comment = "Delicious burger!",
                        commentTimestamp = System.currentTimeMillis() - 10_000
                    ),
                    BurgerPost.Comment(
                        commenterName = "Bob Baker",
                        comment = "Wow, looks amazing.",
                        commentTimestamp = System.currentTimeMillis() - 5_000
                    )
                ),
                location = BurgerPost.Location(
                    coordinates = Pair(48.8566, 2.3522),
                    placeId = "ChIJD7fiBh9u5kcRYJSMaMOCCwQ"
                ),
                nameOfBurger = "Classic Cheeseburger",
                priceOfBurger = 7.99f,
                postTimestamp = System.currentTimeMillis(),
                ratingScore = BurgerPost.RatingScore(
                    pattyRating = 4.2f,
                    veggieRating = 3.7f,
                    sauceRating = 4.5f,
                    overallRating = 4.0f,
                    calculatedRating = 4.1f
                ),
                reviewID = 123
            ),
            BurgerPost(
                nameOfPerson = "John Doe",
                wordReview = "This burger was amazing!",
                numberOfLikes = 5,
                numberOfComments = 3,
                comments = listOf(
                    BurgerPost.Comment(
                        commenterName = "Jane Smith",
                        comment = "Delicious burger!",
                        commentTimestamp = System.currentTimeMillis() - 10_000
                    ),
                    BurgerPost.Comment(
                        commenterName = "Bob Baker",
                        comment = "Wow, looks amazing.",
                        commentTimestamp = System.currentTimeMillis() - 5_000
                    )
                ),
                location = BurgerPost.Location(
                    coordinates = Pair(48.8566, 2.3522),
                    placeId = "ChIJD7fiBh9u5kcRYJSMaMOCCwQ"
                ),
                nameOfBurger = "Classic Cheeseburger",
                priceOfBurger = 7.99f,
                postTimestamp = System.currentTimeMillis(),
                ratingScore = BurgerPost.RatingScore(
                    pattyRating = 4.2f,
                    veggieRating = 3.7f,
                    sauceRating = 4.5f,
                    overallRating = 4.0f,
                    calculatedRating = 4.1f
                ),
                reviewID = 123
            )
            // add more BurgerPost instances here
        )

        recyclerView = view.findViewById(R.id.feed_recyclerview)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = BurgerPostAdapter(burgerPosts)

        return view
    }

    companion object {
        @JvmStatic
        fun newInstance() = FeedFragment()
    }
}
