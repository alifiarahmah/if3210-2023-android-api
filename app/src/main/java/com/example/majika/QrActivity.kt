package com.example.majika

import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.budiyev.android.codescanner.AutoFocusMode
import com.budiyev.android.codescanner.CodeScanner
import com.budiyev.android.codescanner.CodeScannerView
import com.budiyev.android.codescanner.DecodeCallback
import com.budiyev.android.codescanner.ErrorCallback
import com.budiyev.android.codescanner.ScanMode
import com.example.majika.data.TransactionStatus
import com.example.majika.transaction.TransactionAPI
import com.example.majika.transaction.TransactionClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class QrActivity : AppCompatActivity() {

    /**
     * Bindings
     */
    private lateinit var qrScanner: CodeScanner
    private lateinit var qrScannerView: CodeScannerView
    private lateinit var qrTextView: TextView
    private lateinit var qrButton: Button

    /**
     * Constants / Vals
     */
    private val REQUIRED_PERMISSIONS = arrayOf(android.Manifest.permission.CAMERA)
    private val CAMERA_REQ_CODE = 69

    /**
     * Vars
     */
    private var transactionAPI: TransactionAPI? = null
    private var status: TransactionStatus? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_qr_new)
        /**
         * Set API Service
         */
        transactionAPI = TransactionClient.getInstance().create(TransactionAPI::class.java)
        /**
         * Bind to the vars
         */
        qrButton = findViewById(R.id.qrButton)
        qrTextView = findViewById(R.id.qrTextStatus)
        qrScannerView = findViewById(R.id.qrScannerCam)
        /**
         * Make button visibility gone
         */
        qrButton.visibility = View.GONE
        /**
         * Check the permissions, mainly to use camera.
         */
        if (!isPermissionGranted()) {
            ActivityCompat.requestPermissions(
                this,
                REQUIRED_PERMISSIONS,
                CAMERA_REQ_CODE
            )
        }
        else {
            setupScanner()
        }
    }

    /**
     * Response the result of permission requests
     */
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CAMERA_REQ_CODE) {
            if (isPermissionGranted()) {
                setupScanner()
            }
            else {
                Toast.makeText(
                    applicationContext,
                    "Izin membuka kamera tidak diberikan!",
                    Toast.LENGTH_SHORT
                ).show()
                /**
                 * Stop aktivitas
                 */
                this.finish()
            }
        }
    }

    /**
     * Check permission
     */
    fun isPermissionGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(applicationContext, it) == PackageManager.PERMISSION_GRANTED
    }

    /**
     * Setup camera procedure
     */
    fun setupScanner() {
        /**
         * Setup scanner
         */
        qrScanner = CodeScanner(applicationContext, qrScannerView)
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
        }
        /**
         * Start scanning
         */
        qrScanner.startPreview()
        /**
         * Set on click for qrButton
         */
        qrButton.setOnClickListener {
            qrScanner.startPreview()
        }
    }

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
                    qrButton.visibility = View.VISIBLE
                    qrTextView.text = "Transaksi Anda ${transactionStatus!!.status}"
                    qrButton.text = "Scan Again"
                    qrButton.setOnClickListener {
                        qrScanner.startPreview()
                        qrTextView.text = "Scanning..."
                        qrButton.visibility = View.GONE
                    }
                }
            }

            override fun onFailure(call: Call<TransactionStatus>, t: Throwable) {
                Log.wtf("VERIFY_TRANSACTION", t.message)
            }
        }
        )
    }
}