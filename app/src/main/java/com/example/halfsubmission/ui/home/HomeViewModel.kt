package com.example.halfsubmission.ui.home

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.halfsubmission.data.response.EventResponse
import com.example.halfsubmission.data.response.ListEventsItem
import com.example.halfsubmission.data.retrofit.ApiConfig
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeViewModel : ViewModel() {

    companion object {
        private const val TAG = "HomeViewModel"
    }


    private val _eventUpcoming = MutableLiveData<List<ListEventsItem>>()
    val eventUpcoming: MutableLiveData<List<ListEventsItem>> get() = _eventUpcoming

    private val _eventFinished = MutableLiveData<List<ListEventsItem>>()
    val eventFinished: MutableLiveData<List<ListEventsItem>> get() = _eventFinished

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage

    fun getListActiveEvents(active:Int) {
        _isLoading.value = true
        val client = ApiConfig.getApiService().getActiveEvents(active)
        client.enqueue(object : Callback<EventResponse> {
            override fun onResponse(call: Call<EventResponse>, response: Response<EventResponse>) {
                viewModelScope.launch {
                    delay(0) // Delay selama 1 detik
                    _isLoading.value = false // Set isLoading menjadi false setelah delay

                    if (response.isSuccessful) {
                        if (active == 0) {
                            _eventUpcoming.value = response.body()?.event ?: emptyList()
                        } else {
                            _eventFinished.value = response.body()?.event ?: emptyList()
                        }
                    } else {
                        _errorMessage.value = "Error: ${response.message()}"
                        Log.e(TAG, "Error: ${response.message()}")
                    }
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

