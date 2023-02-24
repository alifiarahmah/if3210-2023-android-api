package com.example.majika.ui.twibbon

import android.Manifest
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.camera.core.Camera
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.core.CameraSelector
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.majika.R
import com.example.majika.databinding.FragmentTwibbonBinding
import com.example.majika.twibbon.ImageManip
import com.google.common.util.concurrent.ListenableFuture

class TwibbonFragment : Fragment() {

    //izin berkamera
    private val CAMERA_REQUEST__CODE_PERMISSIONS = 10
    private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)

    private var _binding: FragmentTwibbonBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private lateinit var viewer: PreviewView
    private lateinit var captureButton: Button
    private var imageCapture: ImageCapture? = null
    private lateinit var twibbonViewer: ImageView
    private var camera: Camera? = null
    private lateinit var twibbonViewModel: TwibbonViewModel
    private lateinit var retakePhotoButton: Button
    private lateinit var switchCameraButton: Button
    private lateinit var preview: Preview
    private lateinit var cameraProviderFuture: ListenableFuture<ProcessCameraProvider>

    private lateinit var cameraSelector: CameraSelector

    private lateinit var TWIBBON: Bitmap

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        //initiate twibbon
        TWIBBON = BitmapFactory.decodeResource(resources, R.drawable.twibbon)
        twibbonViewModel =
            ViewModelProvider(this).get(TwibbonViewModel::class.java)

        //observe
        _binding = FragmentTwibbonBinding.inflate(inflater, container, false)
        val root: View = binding.root
        viewer = binding.surfaceView
        captureButton = binding.twibbonButton
        twibbonViewer = binding.imageViewer
        //bikin tombol switch camera
        switchCameraButton = binding.flipCameraButton
        //tambahin listenernya
        switchCameraButton.setOnClickListener {
            switchCamera()
        }
        retakePhotoButton = binding.retakePhoto
        //tambahin listenernya
        retakePhotoButton.setOnClickListener {
            //tutup viewer sekarang
            twibbonViewer.visibility = View.GONE
            //tampilin halaman awal
            viewer.visibility = View.VISIBLE
            captureButton.visibility = View.VISIBLE
            //tutup tombol ini
            retakePhotoButton.visibility = View.GONE
            //balikin tombol switch camera
            switchCameraButton.visibility = View.VISIBLE
            //hapus twibbon
            twibbonViewer.setImageBitmap(null)
        }
        //tambahin listener
        captureButton.setOnClickListener {
            captureImage()
        }
        twibbonViewModel.text.observe(viewLifecycleOwner) {
        }
        twibbonViewModel._bitmap.observe(viewLifecycleOwner) {
            //tampilin gambar
            twibbonViewer.setImageBitmap(twibbonViewModel._bitmap.value)
            Log.v("IMAGE", twibbonViewModel._bitmap.value.toString())
        }
        //tunggu previewview selesai diinit
        // setting data ulang
        twibbonViewer.visibility = View.GONE
        twibbonViewer.setImageBitmap(null)
        viewer.visibility = View.VISIBLE
        //cek izin
        if (!isPermissionGranted()) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                REQUIRED_PERMISSIONS, CAMERA_REQUEST__CODE_PERMISSIONS
            )
        } else {
            setupCamera()
        }
        return root
    }

    private fun switchCamera() {
        //update camera selector
        cameraSelector = if (cameraSelector == CameraSelector.DEFAULT_BACK_CAMERA) {
            CameraSelector.DEFAULT_FRONT_CAMERA
        } else {
            CameraSelector.DEFAULT_BACK_CAMERA
        }
        //diset up viewer
        preview = Preview.Builder().build().also {
            it.setSurfaceProvider(viewer.surfaceProvider)
        }
        imageCapture = ImageCapture.Builder().setTargetRotation(viewer.display.rotation).build()
        //bind ulang
        try {
            //bind kamera lama kalau ada
            camera?.let {
                preview.setSurfaceProvider(null)
            }
            cameraProviderFuture.get().unbindAll()
            //rebind provider
            camera?.let {
                preview.setSurfaceProvider(viewer.surfaceProvider)
            }
            camera = cameraProviderFuture.get()
                .bindToLifecycle(viewLifecycleOwner, cameraSelector, preview, imageCapture)
        } catch (e: Error) {
            Log.e("CAMERA", "Error: $e.localizedMessage")
        }

    }

    private fun captureImage() {
        getBitmapFromImage()
        //hide viewer
        viewer.visibility = View.GONE
        //tampilin gambar
        twibbonViewer.setImageBitmap(twibbonViewModel._bitmap.value)
        twibbonViewer.visibility = View.VISIBLE
        //tampilin tombol retake
        retakePhotoButton.visibility = View.VISIBLE
        //hide tombol capture
        captureButton.visibility = View.GONE
        //hide tombol flip
        switchCameraButton.visibility = View.GONE
    }

    private fun getBitmapFromImage() {
        imageCapture?.takePicture(ContextCompat.getMainExecutor(requireContext()),
            object : ImageCapture.OnImageCapturedCallback() {
                override fun onCaptureSuccess(image: ImageProxy) {
                    //dapetin bitmap
                    val buffer = image.planes[0].buffer
                    val bytes = ByteArray(buffer.remaining())
                    //pindahin buffer ke byte array
                    buffer.get(bytes)
                    //bikin image
                    val bitmapImage = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                    //set gambar bitmap
                    Log.v("IMAGE", bitmapImage.byteCount.toString())
                    //rotasi hasilnya dan simpan ke model bitmap
            //        val capturedImage = bitmapImage
                    //cek tipe dan orientasi
                    val orientation = resources.configuration.orientation
                    var capturedImage:Bitmap? = null
                    if(cameraSelector==CameraSelector.DEFAULT_BACK_CAMERA){
                        //kamera belakang
                        //untuk orintasi potrait
                        if(orientation==Configuration.ORIENTATION_PORTRAIT){
                            capturedImage = ImageManip.rotateImage(bitmapImage, 90.0f)
                        }
                        else{
                            //landscape
                            capturedImage = ImageManip.rotateImage(bitmapImage, 180.0f)
                        }
                    }
                    else{
                        //kamera depan
                        //untuk orintasi potrait
                        if(orientation==Configuration.ORIENTATION_PORTRAIT){
                            capturedImage = ImageManip.rotateImage(bitmapImage, 90.0f)
                            capturedImage = ImageManip.scaleImage(capturedImage!!,-1f,1f)
                        }
                        else{
                            //landscape
                            capturedImage = ImageManip.rotateImage(bitmapImage, 180.0f)
                            capturedImage = ImageManip.scaleImage(capturedImage!!,1f,-1f)
                        }
                    }
                    //apply twibbon
                    Log.v("TWIBBON", capturedImage.toString())
                    twibbonViewModel._bitmap.value = applyTwibbon(capturedImage!!)
                    //tutup gambar
                    image.close()
                }

                override fun onError(exception: ImageCaptureException) {
                    Log.e("IMAGE", "Error capturing image")
                }
            })
    }

    private fun applyTwibbon(image: Bitmap): Bitmap {
        val result: Bitmap = Bitmap.createBitmap(image.width, image.height, image.config)
        val twibbonCanvas = Canvas(result)
        //resize twibbon, karena twibbon sangat gede aslinya
        val Resized_Twibbon = Bitmap.createScaledBitmap(TWIBBON, image.width, image.height, true)
        //gambar gambar awal
        twibbonCanvas.drawBitmap(image, 0f, 0f, null)
        //gambar twibbon
   //     twibbonCanvas.drawBitmap(Resized_Twibbon, 0f, 0f, null)
        return result
    }

    private fun setupCamera() {
        cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())
        //tambahkan listner ke kamera
        cameraProviderFuture.addListener({
            //inisialisasi preview
            preview = Preview.Builder().build().also {
                it.setSurfaceProvider(viewer.surfaceProvider)
            }
            //inisialisasi image capture
            imageCapture = ImageCapture.Builder()
                .setTargetRotation(viewer.display.rotation)
                .build()
            //set kamera depan sebagai default
            cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
            //binding camera
            try {
                //bind kamera lama kalau ada
                camera?.let {
                    preview.setSurfaceProvider(null)
                }
                cameraProviderFuture.get().unbindAll()
                //bind
                camera = cameraProviderFuture.get()
                    .bindToLifecycle(viewLifecycleOwner, cameraSelector, preview, imageCapture)
            } catch (e: Error) {
                Log.e("CAMERA", "$e.localizedMessage")
            }
        }, ContextCompat.getMainExecutor(requireContext()))
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    fun isPermissionGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(requireContext(), it) == PackageManager.PERMISSION_GRANTED
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == CAMERA_REQUEST__CODE_PERMISSIONS) {
            if (isPermissionGranted()) {
                setupCamera()
            } else {
                Toast.makeText(context, "Izin Membuka Kamera Tidak Diberikan!", Toast.LENGTH_SHORT)
                    .show()
                //stop aktivitas
                requireActivity().finish()
            }
        }
    }

    //simpan state kalau ganti halaman
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
    }

    //load state saat kembali
    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun onResume() {
        super.onResume()
    }
}