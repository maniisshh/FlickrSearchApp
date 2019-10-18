package com.manisks.flickrsearchapp

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

/**
 * Created by user on 17-10-2019.
 */

class MainViewModel : ViewModel() {

    val photoList = MutableLiveData<List<FlickrResult.Photos.Photo>>()
    val photos = MutableLiveData<FlickrResult.Photos>()
    val message = MutableLiveData<String>()

    private var viewModelJob = Job()
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    fun getPhotosFromFlickr(newText: String, pageCount: Int) {
        coroutineScope.launch {
            var getPhotosDeferred = MarsApi.retrofitService
                .getPhotosAsync(newText, pageCount)
            try {
                val flickrResult: FlickrResult = getPhotosDeferred.await()
                if (flickrResult.stat == "ok") {
                    photos.value = flickrResult.photos
                    photoList.value = flickrResult.photos.photo

                    message.value = if (flickrResult.photos.total == "0") "No data found" else ""
                }
            } catch (e: Exception) {
                Log.e("MainViewModel", e.toString())
                message.value = "Oops! Some error occurred."
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}