import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import android.Manifest
import android.content.Context
import android.net.Uri
import android.widget.Button
import androidx.core.os.bundleOf
import si.uni_lj.fe.tnuv.umami_burger.R
import java.io.File
import java.io.FileOutputStream
import java.util.UUID

private var imageUri: Uri? = null

class NewPostFragment : Fragment() {
    private var param1: String? = null
    private var param2: String? = null

    private val REQUEST_CAMERA_PERMISSION = 101

    private val REQUEST_IMAGE_CAPTURE = 1
    private val SELECT_PICTURE = 2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_new_post, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<ImageView>(R.id.button_image1).setOnClickListener {
            button1Clicked(it)
        }
        view.findViewById<ImageView>(R.id.button_image2).setOnClickListener {
            button2Clicked(it)
        }
        view.findViewById<Button>(R.id.btnBack).setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }
    }

    fun button1Clicked(view: View) {
        // Check if the Camera permission is already available.
        if (ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            // Camera permission has not been granted.
            requestCameraPermission()
        } else {
            val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
        }
    }

    private fun requestCameraPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(), Manifest.permission.CAMERA)) {
            // Provide an additional rationale to the user
            Toast.makeText(context, "Camera access is required to display the camera preview.", Toast.LENGTH_SHORT).show()
        }
        // Request the permission.
        requestPermissions(arrayOf(Manifest.permission.CAMERA), REQUEST_CAMERA_PERMISSION)
    }

    fun button2Clicked(view: View) {
        val pickPhotoIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(pickPhotoIntent, SELECT_PICTURE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            val imageBitmap = data?.extras?.get("data") as Bitmap
            // Assuming you saved the image and got the Uri
            imageUri = saveImage(imageBitmap)
            navigateToPostDetailsFragment()
        } else if (requestCode == SELECT_PICTURE && resultCode == Activity.RESULT_OK) {
            imageUri = data?.data
            navigateToPostDetailsFragment()
        }
    }

    companion object {
        private const val ARG_PARAM1 = "param1"
        private const val ARG_PARAM2 = "param2"

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            NewPostFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
    private fun navigateToPostDetailsFragment() {
        val fragment = PostDetailsFragment.newInstance().apply {
            arguments = bundleOf("imageUri" to imageUri.toString())
        }
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.frame_layout, fragment, "PostDetailsFragment")
            .addToBackStack("PostDetailsFragment")
            .commit()
    }

    private fun saveImage(bitmap: Bitmap): Uri? {
        val imagesDir = requireActivity().applicationContext.getDir("images", Context.MODE_PRIVATE)
        val imageFile = File(imagesDir, UUID.randomUUID().toString() + ".jpg")

        val fos = FileOutputStream(imageFile)
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos)
        fos.close()

        return Uri.fromFile(imageFile)
    }
}
