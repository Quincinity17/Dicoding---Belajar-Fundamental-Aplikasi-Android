package com.example.halfsubmission.ui.finished

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.halfsubmission.data.response.EventResponse
import com.example.halfsubmission.data.response.ListEventsItem
import com.example.halfsubmission.data.retrofit.ApiConfig
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FinishedViewModel : ViewModel() {

    companion object {
        private const val TAG = "HomeViewModel"
    }

    private val _eventList = MutableLiveData<List<ListEventsItem>>()
    val events: MutableLiveData<List<ListEventsItem>> get() = _eventList

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage

    fun getListActiveEvents(active:Int) {
        _isLoading.value = true
        val client = ApiConfig.getApiService().getActiveEvents(active)
        client.enqueue(object : Callback<EventResponse> {
            override fun onResponse(call: Call<EventResponse>, response: Response<EventResponse>) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    // Jika response sukses, update event list
                    _eventList.value = response.body()?.event ?: emptyList()
                } else {
                    // Jika terjadi error, tampilkan pesan error
                    _errorMessage.value = "Error: ${response.message()}"
                    Log.e(TAG, "Error: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<EventResponse>, t: Throwable) {
                _isLoading.value = false
                // Jika gagal dalam melakukan request
                _errorMessage.value = "Failure: ${t.message}"
                Log.e(TAG, "Failure: ${t.message}")
            }
        })
    }
}

