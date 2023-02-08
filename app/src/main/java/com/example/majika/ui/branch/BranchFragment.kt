package com.example.majika.ui.branch

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.majika.databinding.FragmentBranchBinding

class BranchFragment : Fragment() {

    private var _binding: FragmentBranchBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val branchViewModel =
            ViewModelProvider(this).get(BranchViewModel::class.java)

        _binding = FragmentBranchBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textBranch
        branchViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}