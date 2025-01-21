@file:Suppress("SameParameterValue")

package com.example.halfsubmission.ui.SearchResult

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.halfsubmission.data.response.EventResponse
import com.example.halfsubmission.data.response.ListEventsItem
import com.example.halfsubmission.data.retrofit.ApiConfig
import com.example.halfsubmission.databinding.ActivitySearchResultBinding
import com.example.halfsubmission.ui.Adapter.EventAdapter
import com.example.halfsubmission.ui.EventDetail.EventDetailActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SearchResultActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySearchResultBinding
    private lateinit var eventAdapter: EventAdapter
    private val viewModel: SearchResultViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        supportActionBar?.hide()
        binding = ActivitySearchResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val query = intent.getStringExtra("query") ?: ""
        val fragmentSrc = intent.getStringExtra("SOURCE_FRAGMENT") ?: return

        "$fragmentSrc /".also { binding.textViewSource.text = it }
        "Pencarian \"$query\"".also { binding.textViewName.text = it }

        observeViewModel()
        viewModel.searchEvents(query)

        binding.back.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun observeViewModel() {
        viewModel.searchResults.observe(this) { events ->
            if (events.isEmpty()) {
                showEmptyResults()
            } else {
                showResults(events)
            }
        }

        viewModel.isLoading.observe(this) { isLoading ->
            showLoading(isLoading)
        }

        viewModel.errorMessage.observe(this) { message ->
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        }
    }

    private fun showResults(events: List<ListEventsItem>) {
        binding.textHasilPencarian.visibility = View.VISIBLE
        binding.recyclerViewSearchResults.visibility = View.VISIBLE
        binding.ilustrasi.visibility = View.GONE
        setupRecyclerView(events)
        "${events.size} Hasil Pencarian".also { binding.textHasilPencarian.text = it }
    }

    private fun showEmptyResults() {
        binding.textHasilPencarian.visibility = View.GONE
        binding.recyclerViewSearchResults.visibility = View.GONE
        binding.ilustrasi.visibility = View.VISIBLE
    }

    private fun setupRecyclerView(events: List<ListEventsItem>) {
        eventAdapter = EventAdapter(events) { eventId ->
            val intent = Intent(this, EventDetailActivity::class.java)
            intent.putExtra("EVENT_ID", eventId)
            intent.putExtra("SOURCE_FRAGMENT", "Home / Pencarian")
            startActivity(intent)
        }
        binding.recyclerViewSearchResults.layoutManager = LinearLayoutManager(this)
        binding.recyclerViewSearchResults.adapter = eventAdapter
    }

    private fun showLoading(isLoading: Boolean) {
        binding.shimmerLayout.visibility = if (isLoading) View.VISIBLE else View.GONE
        if (isLoading) {
            binding.shimmerLayout.startShimmer()
        } else {
            binding.shimmerLayout.stopShimmer()
        }

        binding.recyclerViewSearchResults.visibility = if (isLoading) View.GONE else View.VISIBLE
        binding.textHasilPencarian.visibility = if (isLoading) View.GONE else View.VISIBLE
    }
}