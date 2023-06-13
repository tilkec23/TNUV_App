package si.uni_lj.fe.tnuv.umami_burger

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import java.util.UUID

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"
private var filePath: Uri? = null


/**
 * A simple [Fragment] subclass.
 * Use the [ProfileEditFragment.newInstance] factory method to
 * create an instance of this fragment.
 */


class ProfileEditFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private lateinit var imageView: ImageView
    private lateinit var userNameEditText: EditText
    private lateinit var userDescriptionEditText: EditText
    private lateinit var btnEditProfile: Button



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        imageView = view.findViewById(R.id.avatar_image_view)
        userNameEditText = view.findViewById(R.id.display_name_edit_text)
        userDescriptionEditText = view.findViewById(R.id.description_edit_text)

        btnEditProfile = view.findViewById(R.id.btnEditProfile)

        btnEditProfile.setOnClickListener {
            uploadImage()
        }

        imageView.setOnClickListener {
            chooseImage()
        }
        arguments?.getString("imageUri")?.let {
            val imageUri = Uri.parse(it)
            imageView.setImageURI(imageUri)
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.data != null)
        {
            filePath = data.data
            imageView.setImageURI(filePath)
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile_edit, container, false)
    }

    companion object {

        private const val PICK_IMAGE_REQUEST = 1


        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ProfileEditFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    private fun chooseImage() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST)
    }

    private fun uploadImage() {
        if(filePath != null)
        {
            val ref = FirebaseStorage.getInstance().reference.child("images/" + UUID.randomUUID().toString())
            ref.putFile(filePath!!)
                .addOnSuccessListener { taskSnapshot ->
                    taskSnapshot.metadata?.reference?.downloadUrl?.addOnSuccessListener { uri ->
                        val imageUrl = uri.toString()
                        // Save the profile image URL to Firebase Database
                        saveUserDetails(imageUrl)
                    }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(context, "Failed " + e.message, Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun saveUserDetails(imageUrl: String) {
        val user = FirebaseAuth.getInstance().currentUser
        if (user == null) {
            Toast.makeText(context, "User not authenticated", Toast.LENGTH_SHORT).show()
            return
        }

        user?.let {
            val userId = user.uid
            val userName = userNameEditText.text.toString()
            val userDescription = userDescriptionEditText.text.toString()
            val userAccountCreationTime = user.metadata?.creationTimestamp.toString()

            if (userName.isBlank() || userDescription.isBlank()) {
                Toast.makeText(context, "Please fill out all the fields", Toast.LENGTH_SHORT).show()
                return
            }

            val userMap = mapOf<String, String>(
                "userId" to userId,
                "userName" to userName,
                "userDescription" to userDescription,
                "userAccountCreationTime" to userAccountCreationTime,
                "profileImage" to imageUrl
            )

            val dbRef = FirebaseDatabase.getInstance().getReference("Users/$userId")
            dbRef.setValue(userMap)
                .addOnSuccessListener {
                    Toast.makeText(context, "User details saved successfully!", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener {
                    Toast.makeText(context, "Failed to save user details!", Toast.LENGTH_SHORT).show()
                }
        }
    }




}