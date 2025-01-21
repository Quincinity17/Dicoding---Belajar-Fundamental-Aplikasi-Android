package com.example.halfsubmission.ui.home

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView

import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.halfsubmission.R
import com.example.halfsubmission.databinding.FragmentHomeBinding
import com.example.halfsubmission.ui.Adapter.EventAdapter
import com.example.halfsubmission.ui.EventDetail.EventDetailActivity
import com.example.halfsubmission.ui.SearchResult.SearchResultActivity
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.android.material.snackbar.Snackbar


class HomeFragment : Fragment()  {

    private var _binding: FragmentHomeBinding? = null
    private lateinit var homeViewModel: HomeViewModel
    private lateinit var shimmerLayoutUpcoming: ShimmerFrameLayout
    private lateinit var shimmerLayoutFinished: ShimmerFrameLayout
    private var isSearching = false

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        homeViewModel = ViewModelProvider(this).get(HomeViewModel::class.java)
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        shimmerLayoutUpcoming = binding.shimmerLayoutUpcoming
        shimmerLayoutFinished = binding.shimmerLayoutFinished

        binding.seeAllUpcomingEventText.setOnClickListener{
            findNavController().navigate(R.id.navigation_Upcoming)
        }
        binding.seeAllFinishedEventText.setOnClickListener{
            findNavController().navigate(R.id.navigation_Finished)
        }


        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (isSearching) {
                    return false
                }
                isSearching = true

                query?.let {
                    Toast.makeText(requireContext(), "Searching for: $it", Toast.LENGTH_SHORT).show()
                    val intent = Intent(requireContext(), SearchResultActivity::class.java)
                    intent.putExtra("SOURCE_FRAGMENT", "Home")
                    intent.putExtra("query", it)
                    startActivity(intent)
                }
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }
        })

        observeViewModel()
        getListEvent()
        logCurrentDestination()

        return root
    }
    override fun onResume() {
        super.onResume()
        isSearching = false
        // Muat ulang data saat kembali ke HomeFragment
        getListEvent()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun observeViewModel() {
        homeViewModel.eventUpcoming.observe(viewLifecycleOwner) {
                listEvent ->
            if (listEvent.isNullOrEmpty()) {
                Log.d("DashboardFragment", "Data is empty")
            } else {
                val adapterUpcoming = EventAdapter(listEvent) { eventId ->
                    // Saat item diklik, pindah ke EventDetailActivity dengan ID event
                    val intent = Intent(requireContext(), EventDetailActivity::class.java)
                    intent.putExtra("EVENT_ID", eventId)  // Kirimkan ID ke EventDetailActivity
                    intent.putExtra("SOURCE_FRAGMENT", "Home")
                    startActivity(intent)
                }
                binding.listUpcomingEvents.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
                binding.listUpcomingEvents.adapter = adapterUpcoming
            }
        }
        homeViewModel.eventFinished.observe(viewLifecycleOwner) {
                listEvent ->
            if (listEvent.isNullOrEmpty()) {
                Log.d("DashboardFragment", "Data is empty")
            } else {
                val adapterFinished = EventAdapter(listEvent) { eventId ->
                    val intent = Intent(requireContext(), EventDetailActivity::class.java)
                    intent.putExtra("EVENT_ID", eventId)  // Kirimkan ID ke EventDetailActivity
                    intent.putExtra("SOURCE_FRAGMENT", "Home")
                    startActivity(intent)
                }
                binding.listFinishedEvents.layoutManager = LinearLayoutManager(requireContext())
                binding.listFinishedEvents.adapter = adapterFinished
            }
        }
        homeViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->

            if (isLoading) {
                Log.d("HomeFragment", "Masuk loading")
                shimmerLayoutUpcoming.startShimmer()
                shimmerLayoutUpcoming.visibility = View.VISIBLE
                shimmerLayoutFinished.startShimmer()
                shimmerLayoutFinished.visibility = View.VISIBLE

                binding.listUpcomingEvents.visibility = View.GONE
                binding.listFinishedEvents.visibility = View.GONE
            } else {
                Log.d("HomeFragment", "selesai")
                shimmerLayoutUpcoming.stopShimmer()
                shimmerLayoutUpcoming.visibility = View.GONE
                shimmerLayoutFinished.stopShimmer()
                shimmerLayoutFinished.visibility = View.GONE

                binding.listUpcomingEvents.visibility = View.VISIBLE
                binding.listFinishedEvents.visibility = View.VISIBLE
            }
        }
        homeViewModel.errorMessage.observe(viewLifecycleOwner) { errorMessage ->
            if (!errorMessage.isNullOrEmpty()) {
                Snackbar.make(requireView(), errorMessage, Snackbar.LENGTH_SHORT).show()
            }
        }
    }

    private fun getListEvent(){
        homeViewModel.getListActiveEvents(0)
        homeViewModel.getListActiveEvents(1)
    }

    private fun logCurrentDestination() {
        val navController = findNavController()
        val currentDestination = navController.currentDestination
        Log.d("NavController", "Current destination: ${currentDestination?.label}")
    }

}