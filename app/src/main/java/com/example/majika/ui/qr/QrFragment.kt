package com.example.majika.ui.qr

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.FrameLayout
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.budiyev.android.codescanner.AutoFocusMode
import com.budiyev.android.codescanner.CodeScanner
import com.budiyev.android.codescanner.CodeScannerView
import com.budiyev.android.codescanner.DecodeCallback
import com.budiyev.android.codescanner.ErrorCallback
import com.budiyev.android.codescanner.ScanMode
import com.example.majika.R
import com.example.majika.data.TransactionStatus
import com.example.majika.databinding.FragmentQrBinding
import com.example.majika.transaction.TransactionAPI
import com.example.majika.transaction.TransactionClient
import com.example.majika.ui.cart.CartFragment
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class QrFragment : Fragment() {

    /**
     * Transaction API
     */
    private var transactionAPI: TransactionAPI? = null
    private var status: TransactionStatus? = null

    /**
     * Camera Request dan Code Scanner
     */
    private lateinit var qrScanner: CodeScanner
    private lateinit var qrFrame: FrameLayout
    private lateinit var qrTextView: TextView
    private lateinit var qrButton: Button
    private lateinit var qrCamera: CodeScannerView

    private var _binding: FragmentQrBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    /**
     * Handle back button on create
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val callback:OnBackPressedCallback = requireActivity().onBackPressedDispatcher.addCallback(this) {
            /**
             * Handle the back button event
             */
            val cartFragment = CartFragment()
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            transaction.replace(R.id.nav_host_fragment_activity_main, cartFragment)
            transaction.remove(this@QrFragment)
            transaction.commit()
            /*
            val manager = requireActivity().supportFragmentManager
            manager.popBackStack()

             */
        }
    }

    /**
     * Inflate the fragment's view
     */
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
         * Bind to local variables
         */
        qrFrame = binding.qrFrame
        qrTextView = binding.qrTextStatus
        qrButton = binding.qrButton

        /**
         * Set API Service
         */
        transactionAPI = TransactionClient.getInstance().create(TransactionAPI::class.java)

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
                Log.d("QR_SCAN", it.text)
                val transactionId = it.text
                verifyTransaction(transactionId)
            }
            errorCallback = ErrorCallback {
                Log.d("QR_SCAN", it.message!!)
            }
            binding.qrScannerCam.setOnClickListener {
                qrScanner.startPreview()
                qrTextView.text = "Scanning..."
            }
            binding.qrFrame.setOnClickListener {
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

    /**
     * Transaction verifier
     */
    private fun verifyTransaction(transactionId: String) {
        val call: Call<TransactionStatus> = transactionAPI!!.verifyTransaction(transactionId)
        call.enqueue(object: Callback<TransactionStatus> {
            override fun onResponse(
                call: Call<TransactionStatus>,
                response: Response<TransactionStatus>
            ) {
                if (response.isSuccessful) {
                    val transactionStatus: TransactionStatus? = response.body()
                    Log.d("VERIFY_TRANSACTION", transactionStatus!!.status)
                    qrTextView.text = "Transaksi Anda ${transactionStatus!!.status}"
                    qrButton.text = "Scan Again"
                    qrButton.setOnClickListener {
                        qrScanner.startPreview()
                        qrTextView.text = "Scanning..."
                    }
                }
            }

            override fun onFailure(call: Call<TransactionStatus>, t: Throwable) {
                Log.wtf("VERIFY_TRANSACTION", t.message)
            }
        }
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}