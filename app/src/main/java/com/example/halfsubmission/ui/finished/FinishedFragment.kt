package com.example.halfsubmission.ui.finished

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
import com.example.halfsubmission.databinding.FragmentFinishedBinding
import com.example.halfsubmission.ui.Adapter.EventAdapter
import com.example.halfsubmission.ui.EventDetail.EventDetailActivity
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.android.material.snackbar.Snackbar

class FinishedFragment : Fragment() {

    private var _binding: FragmentFinishedBinding? = null

    private lateinit var finishedViewModel: FinishedViewModel
    private lateinit var shimmerLayout: ShimmerFrameLayout

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        finishedViewModel =
            ViewModelProvider(this).get(FinishedViewModel::class.java)

        _binding = FragmentFinishedBinding.inflate(inflater, container, false)
        val root: View = binding.root

        shimmerLayout = binding.shimmerLayoutFragmentFinished

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

        finishedViewModel.events.observe(viewLifecycleOwner) { listEvent ->
            if (listEvent.isNullOrEmpty()) {
                Log.d("DashboardFragment", "Data is empty")
            } else {
                val adapterFinished = EventAdapter(listEvent) { eventId ->
                    // Saat item diklik, pindah ke EventDetailActivity dengan ID event
                    val intent = Intent(requireContext(), EventDetailActivity::class.java)
                    intent.putExtra("EVENT_ID", eventId)  // Kirimkan ID ke EventDetailActivity
                    intent.putExtra("SOURCE_FRAGMENT", "Finished")
                    startActivity(intent)
                }
                binding.listEvent.layoutManager =
                    LinearLayoutManager(requireContext()) // Layout manager untuk RecyclerView
                binding.listEvent.adapter = adapterFinished
            }
        }


        finishedViewModel.errorMessage.observe(viewLifecycleOwner) { errorMessage ->
            if (!errorMessage.isNullOrEmpty()) {
                Snackbar.make(requireView(), errorMessage, Snackbar.LENGTH_SHORT).show()
            }
        }

        finishedViewModel.isLoading.observe(viewLifecycleOwner){ isLoading ->
            if (isLoading) {
                shimmerLayout.startShimmer()
                shimmerLayout.visibility = View.VISIBLE
                binding.listEvent.visibility = View.GONE
            } else {
                shimmerLayout.stopShimmer()
                shimmerLayout.visibility = View.GONE
                binding.listEvent.visibility = View.VISIBLE
            }
        }
    }

    private fun getListEvent(){
        finishedViewModel.getListActiveEvents(0)
    }
    private fun logCurrentDestination() {
        val navController = findNavController()
        val currentDestination = navController.currentDestination
        Log.d("NavController", "Current destination: ${currentDestination?.label}")
    }

}