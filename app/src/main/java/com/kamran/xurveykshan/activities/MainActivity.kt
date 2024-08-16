package com.kamran.xurveykshan.activities

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.Manifest.permission.CAMERA
import android.Manifest.permission.RECORD_AUDIO
import android.content.ContentResolver
import android.content.ContentValues
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.exifinterface.media.ExifInterface
import androidx.lifecycle.lifecycleScope
import cn.pedant.SweetAlert.SweetAlertDialog
import com.google.android.gms.location.LocationServices
import com.kamran.xurveykshan.R
import com.kamran.xurveykshan.data.DataEntity
import com.kamran.xurveykshan.databinding.ActivityMainBinding
import com.kamran.xurveykshan.utils.Constants.TAG
import com.kamran.xurveykshan.utils.openAppInfo
import com.kamran.xurveykshan.utils.showKeyboard
import com.kamran.xurveykshan.viewModels.MainViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.abs

class MainActivity : AppCompatActivity() {

    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModel<MainViewModel>()

    private var imageUri: Uri? = null


    companion object {
        private val requiredPermissions =
            arrayOf(ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION, CAMERA)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(_binding?.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


        setProfilePicture()
        bindObservers()


        binding.ageEdittext.addTextChangedListener {
            binding.startCameraBtn.isEnabled = it.toString().isNotEmpty()
            if (it?.isEmpty() == true) {
                binding.submitBtn.isEnabled = false
            } else if (it?.isEmpty() == false && imageUri != null) {
                binding.submitBtn.isEnabled = true
            }
        }

        binding.ageEdittext.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus && viewModel.firstFocusOfAgeEdittext) {
                if (checkPermissionIsGranted(RECORD_AUDIO).not()) {
                    recordAudioPermissionLauncher.launch(RECORD_AUDIO)
                } else {
                    v.showKeyboard(true)
                    viewModel.startRecording()
                    viewModel.firstFocusOfAgeEdittext = false
                }
            }
        }


        binding.submitBtn.setOnClickListener {

            binding.ageEdittext.showKeyboard(false)
            binding.ageEdittext.clearFocus()

            viewModel.stopRecording()

            val recordingTitle = viewModel.audioFilePath?.split("/")
                ?.get(viewModel.audioFilePath!!.split("/").size - 1)

            val submitTimeStamp =
                SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())

            val contactData = DataEntity(
                age = binding.ageEdittext.text.toString().toInt(),
                imageUri = getFileName(imageUri!!)!!,
                recording = recordingTitle!!,
                submitTime = submitTimeStamp
            )
            Log.d(TAG, contactData.toString())

            viewModel.insertContact(contactData)

            finish()  // finish the current activity


        }


    }

    private fun bindObservers() {


        viewModel.imageUri.observe(this) {
            imageUri = it
            binding.imageCardImage.setImageURI(imageUri)
            binding.imageCard.isVisible = imageUri != null
            binding.submitBtn.isEnabled = imageUri != null
            binding.startCameraBtn.isVisible = imageUri == null
        }

        viewModel.recordingStatus.observe(this) { isRecording ->
            if (isRecording && !viewModel.hasShownRecordingToast) {
                Toast.makeText(this, "Recording Started", Toast.LENGTH_SHORT).show()
                viewModel.hasShownRecordingToast = true
            } else if (!isRecording && !viewModel.hasShownSavedToast) {
                Toast.makeText(
                    this,
                    "Recording Saved\n${viewModel.audioFilePath}",
                    Toast.LENGTH_SHORT
                ).show()
                viewModel.hasShownSavedToast = true
            }
        }
    }


    private fun setProfilePicture() {

        binding.startCameraBtn.setOnClickListener {

            binding.ageEdittext.showKeyboard(false)
            binding.ageEdittext.clearFocus()

            val timeStamp = SimpleDateFormat("ddMMyyyy_HHmmss", Locale.ENGLISH).format(Date())
            val value = ContentValues()
            value.put(MediaStore.Images.Media.DISPLAY_NAME, timeStamp)
            value.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
            imageUri =
                contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, value)!!
            Log.d(TAG, imageUri.toString())

            takeAllPermissionAndOpenCamera()

        }


        binding.clear.setOnClickListener {
            viewModel.saveImageUri(null)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }


    private val recordAudioPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            if (it) {
                binding.ageEdittext.showKeyboard(true)
                viewModel.startRecording()
                viewModel.firstFocusOfAgeEdittext = false
            } else {
                binding.ageEdittext.clearFocus()
                val errorDialog = SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                errorDialog.contentText =
                    "Microphone / Record Audio Permission denied\nTo allow go to App Info."
                errorDialog.setOnDismissListener {
                    openAppInfo(this)
                }
                errorDialog.show()

            }

        }


    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { map ->
            val allPermissionGranted = map.values.all { it }
            Log.d(TAG, map.toString())

            val errorDialog = SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
            errorDialog.setOnDismissListener {
                openAppInfo(context = this)
            }

            when {
                allPermissionGranted -> {
                    // Launch the camera if all required permissions are granted
                    cameraLauncher.launch(imageUri!!)
                }

                checkPermissionIsGranted(CAMERA) && (checkPermissionIsGranted(ACCESS_FINE_LOCATION) || checkPermissionIsGranted(
                    ACCESS_COARSE_LOCATION
                )) -> {
                    // If the camera permission is granted and at least one of the location permissions is granted
                    cameraLauncher.launch(imageUri!!)
                }

                checkPermissionIsGranted(CAMERA) && (checkPermissionIsGranted(ACCESS_FINE_LOCATION).not() && checkPermissionIsGranted(
                    ACCESS_COARSE_LOCATION
                ).not()) -> {
                    // If the camera permission is granted but both location permissions are denied
                    errorDialog.contentText =
                        "Location Permission Denied\nTo allow permission, go to App Info."
                    errorDialog.show()
                }

                checkPermissionIsGranted(CAMERA).not() && (checkPermissionIsGranted(
                    ACCESS_FINE_LOCATION
                ) || checkPermissionIsGranted(ACCESS_COARSE_LOCATION)) -> {
                    // If the camera permission is denied
                    errorDialog.contentText =
                        "Camera Permission Denied\nTo allow permission, go to App Info."
                    errorDialog.show()
                }

                else -> {
                    // If none of the required permissions are granted
                    errorDialog.contentText =
                        "Camera & Location Permissions Denied\nTo allow permissions, go to App Info."
                    errorDialog.show()
                }
            }

        }

    private fun checkPermissionIsGranted(permission: String): Boolean {
        return (ActivityCompat.checkSelfPermission(
            this,
            permission
        ) == PackageManager.PERMISSION_GRANTED)
    }

    private val cameraLauncher =
        registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
            if (success) {
                imageUri?.let { it1 ->
                    viewModel.saveImageUri(it1)

                    lifecycleScope.launch {
                        // add Geo Tag to image
                        addGeoTagMetaDataToImage(contentResolver, it1)
                    }
                }
            }
        }


    private fun takeAllPermissionAndOpenCamera() {
        val permissionsToRequest = mutableListOf<String>()
        for (permission in requiredPermissions) {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    permission
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                permissionsToRequest.add(permission)
            }
        }

        if (permissionsToRequest.isNotEmpty()) {
            requestPermissionLauncher.launch(permissionsToRequest.toTypedArray())
        } else {
            cameraLauncher.launch(imageUri!!)
        }

    }


    private fun getFileName(fileUri: Uri): String? {
        val cursor = contentResolver.query(fileUri, null, null, null, null)
        cursor?.use {
            it.moveToFirst()
            val nameIndex = it.getColumnIndexOrThrow(MediaStore.MediaColumns.DISPLAY_NAME)
            val fileName = it.getString(nameIndex)
            it.close()
            return fileName
        }
        return null
    }


    private suspend fun getCurrentLocation(): Location? {
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        return try {
            fusedLocationClient.lastLocation.await()
        } catch (e: SecurityException) {
            Log.d(TAG, "$e")
            null
        }
    }

    private suspend fun addGeoTagMetaDataToImage(contentResolver: ContentResolver, uri: Uri) {

        val location = getCurrentLocation() ?: return
        Log.d(TAG, location.toString())
        val latitude = location.latitude
        val longitude = location.longitude


        try {
            // Open an input stream from the URI
            val inputStream = contentResolver.openInputStream(uri)

            // Create an ExifInterface object
            val exif = ExifInterface(inputStream!!)

            // Convert latitude and longitude to EXIF format
            val latRef = if (latitude > 0) "N" else "S"
            val lonRef = if (longitude > 0) "E" else "W"
            val latString = convertToExifLatLong(abs(latitude))
            val lonString = convertToExifLatLong(abs(longitude))

            // Set the EXIF tags
            exif.setAttribute(ExifInterface.TAG_GPS_LATITUDE_REF, latRef)
            exif.setAttribute(ExifInterface.TAG_GPS_LATITUDE, latString)
            exif.setAttribute(ExifInterface.TAG_GPS_LONGITUDE_REF, lonRef)
            exif.setAttribute(ExifInterface.TAG_GPS_LONGITUDE, lonString)

            // Save the changes
            val outputStream = contentResolver.openOutputStream(uri)
            if (outputStream != null) {
                exif.saveAttributes()
                withContext(Dispatchers.IO) {
                    outputStream.close()
                }
            }

            withContext(Dispatchers.IO) {
                inputStream.close()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // Helper function to convert decimal degrees to EXIF format
    private fun convertToExifLatLong(coordinate: Double): String {
        val degrees = coordinate.toInt()
        val minutes = ((coordinate - degrees) * 60.0).toInt()
        val seconds = (((coordinate - degrees) * 60.0 - minutes) * 60.0).toInt()
        return "$degrees/1,$minutes/1,$seconds/1"
    }


}