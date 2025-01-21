package com.example.halfsubmission.ui.SearchResult

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

class SearchResultViewModel : ViewModel() {

    private val _searchResults = MutableLiveData<List<ListEventsItem>>()
    val searchResults: LiveData<List<ListEventsItem>> get() = _searchResults

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> get() = _errorMessage

    fun searchEvents(query: String) {
        _isLoading.value = true
        val call = ApiConfig.getApiService().searchEvents(-1, query)
        call.enqueue(object : Callback<EventResponse> {
            override fun onResponse(call: Call<EventResponse>, response: Response<EventResponse>) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    val events = response.body()?.event ?: emptyList()
                    _searchResults.value = events
                } else {
                    _errorMessage.value = "Failed to get search results: ${response.message()}"
                }
            }

            override fun onFailure(call: Call<EventResponse>, t: Throwable) {
                _isLoading.value = false
                _errorMessage.value = "Failed to get search results: ${t.message}"
                Log.e("SearchResultViewModel", t.message.toString())
            }
        })
    }
}