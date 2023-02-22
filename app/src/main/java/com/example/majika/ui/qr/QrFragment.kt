package com.example.majika.ui.qr

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.majika.databinding.FragmentQrBinding

class QrFragment : Fragment() {

    private var _binding: FragmentQrBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val qrViewModel =
            ViewModelProvider(this).get(QrViewModel::class.java)

        _binding = FragmentQrBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textCamera
        qrViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }
        return root
    }

    /**
     * TODO: Bikin QR Code Scanner
     * 1. Bikin intent camera - ZXing
     * 2. Scan QR Code
     * 3. Proses, dapat endpointnya
     * 4. Post ke backend
     * 5. Balikin jadi teks
     */

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}