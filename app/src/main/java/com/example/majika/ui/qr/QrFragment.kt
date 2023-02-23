package com.example.majika.ui.qr

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.budiyev.android.codescanner.AutoFocusMode
import com.budiyev.android.codescanner.CodeScanner
import com.budiyev.android.codescanner.DecodeCallback
import com.budiyev.android.codescanner.ErrorCallback
import com.budiyev.android.codescanner.ScanMode
import com.example.majika.R
import com.example.majika.databinding.FragmentQrBinding

class QrFragment : Fragment() {

    /**
     * Companion object
     */
    companion object {
        private const val CAMERA_REQ_CODE = 69
    }
    /**
     * Camera Request dan Code Scanner
     */
    private lateinit var qrScanner: CodeScanner

    private var _binding: FragmentQrBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        val qrViewModel =
            ViewModelProvider(this).get(QrViewModel::class.java)

        _binding = FragmentQrBinding.inflate(inflater, container, false)
        val root: View = binding.root

        /**
         * Setup Camera Scanner
         */
        qrScanner = CodeScanner(this.requireContext(), binding.qrScannerCam)
        qrScanner.apply {
            camera = CodeScanner.CAMERA_BACK
            formats = CodeScanner.TWO_DIMENSIONAL_FORMATS
            autoFocusMode = AutoFocusMode.SAFE
            scanMode = ScanMode.SINGLE
            isAutoFocusEnabled = true
            isFlashEnabled = false
            decodeCallback = DecodeCallback {
                TODO("SCAN QR CODE")
            }
            errorCallback = ErrorCallback {
                TODO("Error Handling")
            }
            binding.qrScannerCam.setOnClickListener {
                qrScanner.startPreview()
            }
        }
        qrScanner.startPreview()

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