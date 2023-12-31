import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ServerValue
import com.google.firebase.storage.FirebaseStorage
import si.uni_lj.fe.tnuv.umami_burger.FeedFragment
import si.uni_lj.fe.tnuv.umami_burger.ProfileFragment
import si.uni_lj.fe.tnuv.umami_burger.R
import java.util.UUID

class PostDetailsFragment : Fragment() {
    private lateinit var imageView: ImageView
    private lateinit var burgerNameEditText: EditText
    private lateinit var burgerPlaceEditText: EditText
    private lateinit var burgerPriceEditText: EditText
    private lateinit var burgerPattyRatingEditText: EditText
    private lateinit var burgerVeggiesRatingEditText: EditText
    private lateinit var burgerSauceRatingEditText: EditText
    private lateinit var burgerOverallRatingEditText: EditText
    private lateinit var submitBurgerButton: Button
    private var filePath: Uri? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_post_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        imageView = view.findViewById(R.id.imageView)
        burgerNameEditText = view.findViewById(R.id.burgerNameEditText)
        burgerPlaceEditText = view.findViewById(R.id.burgerPlaceEditText)
        burgerPriceEditText = view.findViewById(R.id.burgerPriceEditText)
        burgerPattyRatingEditText = view.findViewById(R.id.burgerPattyRatingEditText)
        burgerVeggiesRatingEditText = view.findViewById(R.id.burgerVeggiesRatingEditText)
        burgerSauceRatingEditText = view.findViewById(R.id.burgerSauceRatingEditText)
        burgerOverallRatingEditText = view.findViewById(R.id.burgerOverallRatingEditText)
        submitBurgerButton = view.findViewById(R.id.submitBurgerButton)

        submitBurgerButton.setOnClickListener {
            uploadBurgerPost()
        }
        arguments?.getString("imageUri")?.let {
            val imageUri = Uri.parse(it)
            imageView.setImageURI(imageUri)
            filePath = imageUri
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = PostDetailsFragment()
    }

    private fun uploadBurgerPost() {
        if(filePath != null)
        {
            val user = FirebaseAuth.getInstance().currentUser
            if (user == null) {
                Toast.makeText(context, "User not authenticated", Toast.LENGTH_SHORT).show()
                return
            }
            val ref = FirebaseStorage.getInstance().reference.child("burger_images/" + UUID.randomUUID().toString())
            ref.putFile(filePath!!)
                .addOnSuccessListener { taskSnapshot ->
                    taskSnapshot.metadata?.reference?.downloadUrl?.addOnSuccessListener { uri ->
                        val imageUrl = uri.toString()
                        savePostDetails(imageUrl)
                    }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(context, "Failed to upload image: " + e.message, Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun savePostDetails(imageUrl: String) {
        val user = FirebaseAuth.getInstance().currentUser
        user?.let {
            val userId = user.uid

            val burgerName = burgerNameEditText.text.toString()
            val burgerPlace = burgerPlaceEditText.text.toString()
            val burgerPrice = isValidFloat(burgerPriceEditText.text.toString())
            val burgerPattyRating = isValidRating(burgerPattyRatingEditText.text.toString())
            val burgerVeggiesRating = isValidRating(burgerVeggiesRatingEditText.text.toString())
            val burgerSauceRating = isValidRating(burgerSauceRatingEditText.text.toString())
            val burgerOverallRating = isValidRating(burgerOverallRatingEditText.text.toString())

            if (burgerName.isBlank() || burgerPlace.isBlank() || burgerPrice == null || burgerPattyRating == null
                || burgerVeggiesRating == null || burgerSauceRating == null || burgerOverallRating == null) {
                Toast.makeText(context, "Please fill out all the fields and ensure ratings are between 1 and 10!", Toast.LENGTH_SHORT).show()
                return
            }
            if (userId == null) {
                Toast.makeText(context, "User not logged in", Toast.LENGTH_SHORT).show()
                return
            }
            val title = UUID.randomUUID().toString()


            val postMap = mapOf<String, Any>(
                "userId" to userId,
                "burgerName" to burgerName,
                "burgerPlace" to burgerPlace,
                "burgerPrice" to burgerPrice,
                "burgerPattyRating" to burgerPattyRating,
                "burgerVeggiesRating" to burgerVeggiesRating,
                "burgerSauceRating" to burgerSauceRating,
                "burgerOverallRating" to burgerOverallRating,
                "imageUrl" to imageUrl,
                "timestamp" to ServerValue.TIMESTAMP,
                "title" to title
            )

            val dbRef = FirebaseDatabase.getInstance().getReference("Posts/${title}")
            dbRef.setValue(postMap)
                .addOnSuccessListener {
                    Toast.makeText(context, "Burger post saved successfully!", Toast.LENGTH_SHORT).show()

                    val feedFragment = FeedFragment.newInstance()  // replace with your parameters
                    parentFragmentManager.beginTransaction()
                        .replace(R.id.frame_layout, feedFragment)  // replace with your container view ID
                        .commit()
                }
                .addOnFailureListener {
                    Toast.makeText(context, "Failed to save burger post!", Toast.LENGTH_SHORT).show()
                }
        }
    }
    private fun isValidFloat(input: String): Float? {
        return try {
            input.toFloat()
        } catch (e: NumberFormatException) {
            null
        }
    }
    private fun isValidRating(input: String): Float? {
        return try {
            val number = input.toFloat()
            if (number in 0.0..10.0) {
                number
            } else {
                null
            }
        } catch (e: NumberFormatException) {
            null
        }
    }

}
/*⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⣀⣠⡤⠶⠶⠚⠛⠛⠻⠿⢷⣶⡶⢾⣿⢿⣿⣷⣶⣶⣤⣤⣀⡀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀
⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⣀⣤⠶⠛⠉⠀⡀⠴⣏⡧⠐⠚⢛⡛⠓⠺⣿⣮⡿⠦⢤⡀⠀⠈⣭⠉⠛⠻⠷⣦⣄⡀⠀⠀⠀⠀⠀⠀⠀⠀
⠀⠀⠀⠀⠀⠀⠀⣠⣶⠟⠉⣤⡄⠀⣠⡛⠃⢠⣦⡄⠀⠀⡛⠛⠀⠰⠿⠟⢧⡀⠀⠻⠦⠀⠀⠀⠶⠀⢠⣦⡙⢿⣶⡄⠀⠀⠀⠀⠀⠀
⠀⠀⠀⠀⠀⣠⡾⠋⠑⠀⠀⠉⠀⠐⠟⠁⢀⣤⡉⠀⠀⠸⢿⠂⠀⣶⡦⠀⠘⠇⠀⠀⠰⠆⠀⣠⣤⡀⠀⠉⠀⠀⠙⣿⣆⠀⠀⠀⠀⠀
⠀⠀⠀⢀⣼⡟⠁⠀⠀⠰⠀⠀⠀⠀⠀⠀⠀⠉⢁⣤⡀⠀⣀⠀⠀⢀⣠⡀⠀⠀⢰⣶⠀⠀⠀⠈⠉⠀⠀⠀⠼⠏⠀⠈⠻⣦⠀⠀⠀⠀
⠀⠀⢀⣿⠏⡀⠀⠀⠀⠀⠀⠀⠀⠘⠛⠀⠀⣀⡈⠉⠀⠀⠋⠁⠀⠈⠉⠁⠀⠀⠀⠁⠀⠰⠟⠀⠀⠀⠰⠆⠀⠀⠀⠄⠀⠘⢧⠀⠀⠀
⠀⠀⣾⡟⢠⠇⡀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠉⠁⠀⠀⠘⠛⠃⠀⠀⠀⢰⡆⠀⠀⠀⠀⠀⠀⠠⣤⠄⠀⢀⠀⠀⠀⠀⠀⠀⠈⣧⠀⠀
⠀⠀⣿⣇⠈⣧⡟⣆⢘⢦⡀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠁⠀⠀⠀⠀⠀⠀⠈⠀⠀⠀⠀⠀⠀⠀⢹⡄⠀
⠀⠀⠘⣿⣦⣌⣁⠈⠚⠷⢽⣮⣦⣄⠀⢀⡀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢀⣼⠃⠀
⠀⢀⣤⠾⢋⠈⠉⠛⠳⠦⣤⣀⡉⠉⠉⠒⠛⠷⢶⡤⠤⠤⠤⠤⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⣀⣀⡀⠤⠴⠶⢾⣿⣅⠀⠀
⣴⣿⠿⢿⣿⠃⡀⠀⠀⠀⣀⠈⠉⠛⠒⠲⣦⣤⡤⠤⠤⠤⠤⠤⠤⠤⠤⠴⠶⠖⠲⠶⣶⣒⠛⠛⠋⠉⠉⠀⠀⠀⠀⠀⢀⣀⣈⣻⣿⣦
⠀⠀⠀⠚⠛⣻⡇⠀⣴⣏⣙⣷⠦⠶⠶⣟⠉⠀⠀⠀⠀⢠⡤⠤⣤⣀⠀⠀⠀⣤⢶⣄⣠⡭⠿⢶⣄⠀⢀⣤⠶⠶⢦⡤⠼⣿⡏⠉⠙⠻
⠀⠀⠀⢠⣴⣿⣷⣶⣿⣤⣉⡙⠛⠒⠒⠛⠛⣿⡄⢀⣤⠾⠤⠤⠖⠛⢷⣤⣼⣿⠶⣭⣄⣀⣀⢀⣸⣾⣋⣀⣀⣤⡤⣶⣿⣿⣅⠀⠀⠀
⠀⠀⠀⢿⣿⣿⣿⣿⣻⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣷⣦⣀⠀⠀⠀⠀⠀⠀⢀⣠⠴⢚⣽⣿⣿⣿⣿⣿⣿⡿⠻⠙⠋⠉⠘⣿⡄⠀⠀
⠀⠀⠀⣸⣿⣿⣿⣿⣿⣟⣿⣿⣿⡟⢻⣏⣹⢻⣿⢻⣻⣿⣿⣦⠀⣀⣀⡴⠞⣋⣤⣶⡿⣿⠋⠳⣞⢁⡠⠁⠀⠀⠀⠀⠀⢀⣿⡇⠀⠀
⠀⠀⠀⠘⣿⣿⣿⣿⣿⣿⣿⣿⣖⢻⣿⣿⣿⣿⣿⣻⣿⣿⣿⡿⣧⣭⣵⠖⠋⢩⣽⢁⣀⠻⠀⢤⣤⠿⠤⠖⣦⣷⣂⣠⣴⣾⣟⠃⠀⠀
⠀⠀⠀⣾⡟⢿⣄⣈⣽⡿⠿⠟⠛⠿⣿⣿⣿⣿⣿⣿⣹⣯⣿⣤⣾⣚⣿⣲⣿⣚⣧⣤⣿⣾⣷⣾⣿⣾⣟⣋⡉⠉⠉⣿⠀⠙⣿⡄⠀⠀
⠀⠀⠀⣿⡆⣆⠀⠀⠀⠀⠀⠀⠀⠀⠉⠙⢷⡄⠀⠉⠉⣉⡽⠛⠋⠙⠛⠿⣤⣤⠴⠟⠋⠁⠀⠀⠀⠀⠀⠈⠉⠛⠒⠛⠀⠀⢹⡇⠀⠀
⠀⠀⠀⢿⣷⢻⣿⣆⢠⡀⠀⡀⠀⠀⠀⠀⠀⠙⠳⠶⠞⠉⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⣸⡇⠀⠀
⠀⠀⠀⠘⢿⣦⣍⡛⠦⣷⣀⠳⣀⠈⠳⣄⠀⠀⣀⢀⣀⣀⣀⣀⣀⣀⣀⠀⠀⢀⣀⣀⣀⣀⡠⠤⠀⠀⠀⠀⠀⠀⠀⢀⣠⣴⠟⠀⠀⠀
⠀⠀⠀⠀⠀⠉⠙⠛⠻⠷⣶⣶⣾⣽⣷⣦⣤⣀⣈⣉⣉⣁⣀⣀⣀⣤⣤⣤⣤⣤⣤⣤⣤⣤⣤⣶⡶⠶⠶⠶⠒⠚⠋⠉⠁⠀⠀⠀⠀⠀*/