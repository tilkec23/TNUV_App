import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import androidx.fragment.app.Fragment
import si.uni_lj.fe.tnuv.umami_burger.R

class PostDetailsFragment : Fragment() {
    private lateinit var imageView: ImageView
    private lateinit var burgerNameEditText: EditText
    private lateinit var burgerPlaceEditText: EditText
    private lateinit var burgerPriceEditText: EditText
    private lateinit var burgerPattyRatingEditText: EditText
    private lateinit var burgerVeggiesRatingEditText: EditText
    private lateinit var burgerSauceRatingEditText: EditText
    private lateinit var burgerOverallRatingEditText: EditText

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_post_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        imageView = view.findViewById(R.id.imageView)
        burgerNameEditText = view.findViewById(R.id.burgerNameEditText)
        // ... Rest of your EditText initializations ...

        arguments?.getString("imageUri")?.let {
            val imageUri = Uri.parse(it)
            imageView.setImageURI(imageUri)
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = PostDetailsFragment()
    }
}
