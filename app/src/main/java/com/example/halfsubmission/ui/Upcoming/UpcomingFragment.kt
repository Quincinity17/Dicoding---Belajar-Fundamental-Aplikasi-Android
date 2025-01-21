package com.example.halfsubmission.ui.Upcoming

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.halfsubmission.databinding.FragmentUpcomingBinding
import com.example.halfsubmission.ui.Adapter.EventAdapter
import com.example.halfsubmission.ui.EventDetail.EventDetailActivity
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.android.material.snackbar.Snackbar

class UpcomingFragment : Fragment() {

    private var _binding: FragmentUpcomingBinding? = null
    private lateinit var upcomingViewModel: UpcomingViewModel
    private lateinit var shimmerLayout: ShimmerFrameLayout

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        upcomingViewModel = ViewModelProvider(this).get(UpcomingViewModel::class.java)

        _binding = FragmentUpcomingBinding.inflate(inflater, container, false)
        val root: View = binding.root

        shimmerLayout = binding.shimmerLayout

        observeViewModel()

        getListEvent()
        logCurrentDestination()

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun observeViewModel() {

        upcomingViewModel.events.observe(viewLifecycleOwner) { listEvent ->
            if (listEvent.isNullOrEmpty()) {
                Log.d("UpcomingFragment", "Data is empty")
            } else {
                val adapterUpcoming = EventAdapter(listEvent) { eventId ->
                    // Saat item diklik, pindah ke EventDetailActivity dengan ID event
                    val intent = Intent(requireContext(), EventDetailActivity::class.java)
                    intent.putExtra("EVENT_ID", eventId)  // Kirimkan ID ke EventDetailActivity
                    intent.putExtra("SOURCE_FRAGMENT", "Upcoming")
                    startActivity(intent)
                }
                binding.listEvent.layoutManager =
                    LinearLayoutManager(requireContext()) // Layout manager untuk RecyclerView
                binding.listEvent.adapter = adapterUpcoming
            }
        }

        upcomingViewModel.isLoading.observe(viewLifecycleOwner){ isLoading ->
            Log.d("UpcomingFragment", "Masuk loading")
            if (isLoading) {
                Log.d("UpcomingFragment", "Shimmer started for upcomming events isLoading")
                shimmerLayout.startShimmer()
                shimmerLayout.visibility = View.VISIBLE
                binding.listEvent.visibility = View.GONE
            } else {
                shimmerLayout.stopShimmer()
                shimmerLayout.visibility = View.GONE
                binding.listEvent.visibility = View.VISIBLE
            }
        }


        upcomingViewModel.errorMessage.observe(viewLifecycleOwner) { errorMessage ->
            if (!errorMessage.isNullOrEmpty()) {
                Snackbar.make(requireView(), errorMessage, Snackbar.LENGTH_SHORT).show()
            }
        }
    }

    private fun getListEvent(){
        upcomingViewModel.getListActiveEvents(1)
    }
    private fun logCurrentDestination() {
        val navController = findNavController()
        val currentDestination = navController.currentDestination
        Log.d("NavController", "Current destination: ${currentDestination?.label}")
    }
}