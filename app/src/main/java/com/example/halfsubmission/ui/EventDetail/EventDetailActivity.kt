package com.example.halfsubmission.ui.EventDetail

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.HtmlCompat
import com.bumptech.glide.Glide
import com.example.halfsubmission.data.response.ListEventsItem
import com.example.halfsubmission.databinding.ActivityEventDetailBinding
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.android.material.snackbar.Snackbar

class EventDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEventDetailBinding
    private val viewModel: EventDetailViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEventDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        val eventId = intent.getStringExtra("EVENT_ID")
        if (eventId == null) {
            Toast.makeText(this, "Invalid Event ID", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        observeViewModel()
        viewModel.fetchEventDetail(eventId)

        binding.back.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun observeViewModel() {
        viewModel.eventDetail.observe(this) { eventDetail ->
            updateUI(eventDetail)
        }

        viewModel.isLoading.observe(this) { isLoading ->
            showLoading(isLoading)
        }

        viewModel.errorMessage.observe(this) { errorMessage ->
            Snackbar.make(binding.root, errorMessage, Snackbar.LENGTH_SHORT).show()
        }
    }

    private fun updateUI(eventDetail: ListEventsItem) {
        Glide.with(this)
            .load(eventDetail.imageLogo)
            .into(binding.imageViewLogo)

        with(binding) {
            textViewName.text = eventDetail.name
            textViewDescription.text = HtmlCompat.fromHtml(
                eventDetail.description ?: "",
                HtmlCompat.FROM_HTML_MODE_LEGACY
            )
            tvCategory.text = eventDetail.category
            tvCityName.text = eventDetail.beginTime
            tvOwnerName.text = eventDetail.ownerName
            tvQuota.text = (eventDetail.quota - eventDetail.registrants).toString()

            wideButton.setOnClickListener {
                val url = eventDetail.link
                val intent = Intent(Intent.ACTION_VIEW).apply {
                    data = Uri.parse(url)
                }
                startActivity(intent)
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.apply {
            shimmerLayout.visibility = if (isLoading) View.VISIBLE else View.GONE
            shimmerLayout.apply {
                if (isLoading) startShimmer() else stopShimmer()
            }

            imageViewLogo.visibility = if (isLoading) View.GONE else View.VISIBLE
            eventDetail.visibility = if (isLoading) View.GONE else View.VISIBLE
            textViewDescription.visibility = if (isLoading) View.GONE else View.VISIBLE
            wideButton.visibility = if (isLoading) View.GONE else View.VISIBLE
        }
    }
}
