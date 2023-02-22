package com.example.majika.ui.camera

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.majika.databinding.FragmentTwibbonBinding
import java.util.concurrent.atomic.AtomicReference

class TwibbonFragment : Fragment() {

    private var _binding: FragmentTwibbonBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

//    private lateinit var textView: TextView
    private lateinit var viewer:PreviewView
    private lateinit var captureButton:Button
    private lateinit var imageCapture:ImageCapture
    private lateinit var twibbonViewer:ImageView
    private var camera:Camera? = null


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val twibbonViewModel =
            ViewModelProvider(this).get(TwibbonViewModel::class.java)

        _binding = FragmentTwibbonBinding.inflate(inflater, container, false)
        val root: View = binding.root

//        textView= binding.textCamera
        viewer = binding.surfaceView
        captureButton = binding.twibbonButton
        twibbonViewer = binding.imageViewer
        //tambahin listener
        captureButton.setOnClickListener {
            captureImage()
        }
        twibbonViewModel.text.observe(viewLifecycleOwner) {
//            textView.text = it
        }
        //tunggu previewview selesai diinit
        viewer.post{
            setupCamera()
        }
        return root
    }

    private fun captureImage() {
        val bitmap = getBitmapFromImage()

        //hide viewer
        viewer.visibility = View.GONE
        //tampilin gambar
        twibbonViewer.setImageBitmap(bitmap)
        twibbonViewer.visibility = View.VISIBLE
    }

    private fun getBitmapFromImage(): Bitmap? {

        val bitmap = AtomicReference<Bitmap>()
        imageCapture.takePicture(ContextCompat.getMainExecutor(requireContext()),
        object:ImageCapture.OnImageCapturedCallback(){
            override fun onCaptureSuccess(image: ImageProxy) {
                //dapetin bitmap
                val buffer = image.planes[0].buffer
                val bytes = ByteArray(buffer.remaining())
                //pindahin buffer ke byte array
                buffer.get(bytes)

                //bikin image
                val bitmapImage = BitmapFactory.decodeByteArray(bytes,0,bytes.size)
                //set gambar bitmap
                bitmap.set(bitmapImage)

                //tututp gambar
                image.close()
            }

            override fun onError(exception: ImageCaptureException) {
                Log.e("IMAGE","Error capturing image")
            }
        })
        return bitmap.get()
    }

    private fun setupCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())
        //tambahkan listner ke kamera
        Log.d("CAMERA","apakah jalan?")
        cameraProviderFuture.addListener({
            //inisialisasi preview
            Log.d("CAMERA","jalan cok 11")
            val preview = Preview.Builder().build().also{
                it.setSurfaceProvider(viewer.surfaceProvider)
            }
            //inisialisasi image capture
            imageCapture = ImageCapture.Builder().build()
            //set kamera depan sebagai default
            Log.d("CAMERA","jalan cok")
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
            //binding camera
            try{
                //bind kamera lama kalau ada
                camera?.let{
                    preview.setSurfaceProvider(null)
                }
                cameraProviderFuture.get().unbindAll()
                //bind
                camera = cameraProviderFuture.get().bindToLifecycle(viewLifecycleOwner,cameraSelector,preview,imageCapture)
            }catch(e:Error){
                Log.e("CAMERA","$e.localizedMessage")
            }
        },ContextCompat.getMainExecutor(requireContext()))
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}