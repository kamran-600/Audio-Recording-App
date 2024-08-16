package com.kamran.xurveykshan.viewModels

import android.content.Context
import android.media.MediaRecorder
import android.net.Uri
import android.os.Build
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kamran.xurveykshan.repository.DataRepository
import com.kamran.xurveykshan.data.DataEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainViewModel(
    private val applicationContext: Context,
    private val dataRepository: DataRepository
) : ViewModel() {

    var imageUri = MutableLiveData<Uri?>(null)
        private set

    var firstFocusOfAgeEdittext = true
    var hasShownRecordingToast = false
    var hasShownSavedToast = false

    fun saveImageUri(uri: Uri?) {
        imageUri.value = uri
    }

    private var mediaRecorder: MediaRecorder? = null
    var recordingStatus = MutableLiveData<Boolean>()
        private set

    var audioFilePath: String? = null
        private set

    fun startRecording() {

        val timeStamp = SimpleDateFormat("ddMMyyyy_HHmmss", Locale.getDefault()).format(Date())
        audioFilePath =
            "${applicationContext.externalCacheDir?.absolutePath}/audio_record_${timeStamp}.wav"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            mediaRecorder = MediaRecorder(applicationContext).apply {
                setAudioSource(MediaRecorder.AudioSource.MIC)
                setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
                setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
                setOutputFile(audioFilePath)
                prepare()
                start()
            }
        } else {
            mediaRecorder = MediaRecorder().apply {
                setAudioSource(MediaRecorder.AudioSource.MIC)
                setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
                setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
                setOutputFile(audioFilePath)
                prepare()
                start()
            }
        }

        recordingStatus.value = true
    }

    fun stopRecording() {
        if (mediaRecorder == null) return
        mediaRecorder?.apply {
            stop()
            release()
        }
        mediaRecorder = null
        recordingStatus.value = false
    }


    fun resetRecording() {
        firstFocusOfAgeEdittext = true
        hasShownRecordingToast = false
        hasShownSavedToast = false
    }

    fun insertContact(data : DataEntity){

        viewModelScope.launch(Dispatchers.IO) {
            dataRepository.insertContact(data)
        }
    }

    override fun onCleared() {
        super.onCleared()
        mediaRecorder?.release()
    }
}