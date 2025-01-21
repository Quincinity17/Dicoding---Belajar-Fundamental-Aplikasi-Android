package com.example.halfsubmission.ui.EventDetail

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

class EventDetailViewModel : ViewModel(){
    companion object {
        private const val TAG = "EventDetailViewModel"
    }

    private val _eventDetail = MutableLiveData<ListEventsItem>()
    val eventDetail: LiveData<ListEventsItem> get() = _eventDetail

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _errorMessage = MutableLiveData<String> ()
    val errorMessage: LiveData<String> get() = _errorMessage

    fun fetchEventDetail(eventId: String) {
        _isLoading.value = true
        val client = ApiConfig.getApiService().getEventDetail(eventId)
        client.enqueue(object : Callback<EventResponse> {
            override fun onResponse(call: Call<EventResponse>, response: Response<EventResponse>) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    response.body()?.singleEvent?.let {
                        _eventDetail.value = it
                    } ?: run {
                        _errorMessage.value = "Event not found"
                    }
                } else {
                    _errorMessage.value = "Error: ${response.message()}"
                }
            }

            override fun onFailure(call: Call<EventResponse>, t: Throwable) {
                _isLoading.value = false
                _errorMessage.value = "Failed to load event details: ${t.message}"
                Log.e("EventDetailViewModel", t.message.toString())
            }
        })
    }
}