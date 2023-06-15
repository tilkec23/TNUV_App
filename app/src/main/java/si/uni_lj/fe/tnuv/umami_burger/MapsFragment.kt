import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.android.libraries.places.api.model.Place
import si.uni_lj.fe.tnuv.umami_burger.R
import java.util.*

class MapsFragment : Fragment(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var placesClient: PlacesClient
    private lateinit var lastLocation: Location

    private lateinit var requestPermissionLauncher: ActivityResultLauncher<String>


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_maps, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requestPermissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
                if (isGranted) {
                    enableLocationComponent()
                } else {
                    Toast.makeText(activity, "Location permission is necessary to use this feature", Toast.LENGTH_LONG).show()
                }
            }


        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        // Initialize the Places client
        Places.initialize(requireContext(), "AIzaSyA0g03W4-cIA1GdP5sGcZYPm7z1nQ-DcA4")
        placesClient = Places.createClient(requireContext())

        // Initialize the Fused Location client
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Check for location permissions
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
        ) {
            if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                return
            }


            return
        }

        mMap.isMyLocationEnabled = true

        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            // Get the last known location, if available
            if (location != null) {
                lastLocation = location

                val markerLatLng = LatLng(46.0446209, 14.4872043)
                mMap.addMarker(MarkerOptions().position(markerLatLng).title("Najlepši kraj v Ljubljani"))


                val currentLatLng = LatLng(location.latitude, location.longitude)
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 12f))

                // Use the Places API to find restaurants nearby
                val placeFields: List<Place.Field> = listOf(Place.Field.NAME, Place.Field.LAT_LNG)
                val request = FindCurrentPlaceRequest.newInstance(placeFields)
                val placeResponse = placesClient.findCurrentPlace(request)
                placeResponse.addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val response = task.result
                        for (placeLikelihood in response?.placeLikelihoods ?: emptyList()) {
                            val place = placeLikelihood.place
                            val placeTypes = place.types
                            if (placeTypes != null && placeTypes.contains(Place.Type.RESTAURANT)) {
                                val placeLocation = LatLng(place.latLng!!.latitude, place.latLng!!.longitude)
                                mMap.addMarker(MarkerOptions().position(placeLocation).title(place.name))
                            }

                        }
                    }
                }
            }
        }
    }
    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            LOCATION_PERMISSION_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission was granted, proceed with displaying Map.
                    if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                        ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    ) {
                        return
                    }
                    mMap.isMyLocationEnabled = true
                    fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                        // Your location handling code here...
                    }
                } else {
                    // Permission denied. Disable the functionality that depends on this permission.
                    Toast.makeText(activity, "Location permission is necessary to use this feature", Toast.LENGTH_LONG).show()
                }
                return
            }
            // Handle other permission results if any.
        }
    }
    private fun enableLocationComponent() {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        mMap.isMyLocationEnabled = true

        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            // Get the last known location, if available
            if (location != null) {
                lastLocation = location
                val currentLatLng = LatLng(location.latitude, location.longitude)
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 12f))

                // Use the Places API to find restaurants nearby
                val placeFields: List<Place.Field> = listOf(Place.Field.NAME, Place.Field.LAT_LNG)
                val request = FindCurrentPlaceRequest.newInstance(placeFields)
                val placeResponse = placesClient.findCurrentPlace(request)
                placeResponse.addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val response = task.result
                        for (placeLikelihood in response?.placeLikelihoods ?: emptyList()) {
                            val place = placeLikelihood.place
                            if (place.types.contains(Place.Type.RESTAURANT)) {
                                val placeLocation = LatLng(place.latLng!!.latitude, place.latLng!!.longitude)
                                mMap.addMarker(MarkerOptions().position(placeLocation).title(place.name))
                            }
                        }
                    }
                }
            }
        }
    }


}
