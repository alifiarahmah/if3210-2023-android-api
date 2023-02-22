package com.example.majika.ui.twibbon

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
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
//import androidx.camera.core.*
//import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.majika.databinding.FragmentTwibbonBinding
import com.example.majika.twibbon.ImageManip
import java.util.concurrent.atomic.AtomicReference

class TwibbonFragment : Fragment() {

    //izin berkamera
    private val CAMERA_REQUEST__CODE_PERMISSIONS = 10
    private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)

    private var _binding: FragmentTwibbonBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

//    private lateinit var textView: TextView
    private lateinit var viewer:PreviewView
    private lateinit var captureButton:Button
    private var imageCapture: ImageCapture? = null
    private lateinit var twibbonViewer:ImageView
    private var camera: Camera? = null
    private lateinit var twibbonViewModel: TwibbonViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        twibbonViewModel =
            ViewModelProvider(this).get(TwibbonViewModel::class.java)

        //observ
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
        twibbonViewModel._bitmap.observe(viewLifecycleOwner){
            viewer.visibility = View.GONE
            //tampilin gambar
            twibbonViewer.setImageBitmap(twibbonViewModel._bitmap.value)
            Log.v("IMAGE",twibbonViewModel._bitmap.value.toString())
            twibbonViewer.visibility = View.VISIBLE
        }
        //tunggu previewview selesai diinit
        //cek izin
        if(!isPermissionGranted()){
            ActivityCompat.requestPermissions(requireActivity(),
                REQUIRED_PERMISSIONS,CAMERA_REQUEST__CODE_PERMISSIONS)
        }
        else{
            setupCamera()
        }
        return root
    }

    private fun captureImage() {
      //  val bitmap = getBitmapFromImage()
      //  Log.v("BITMAP",bitmap.toString())
        getBitmapFromImage()
        //hide viewer
        viewer.visibility = View.GONE
        //tampilin gambar
        twibbonViewer.setImageBitmap(twibbonViewModel._bitmap.value)
        twibbonViewer.visibility = View.VISIBLE
    }

    private fun getBitmapFromImage() {

       // val bitmap = AtomicReference<Bitmap>()
        imageCapture?.takePicture(ContextCompat.getMainExecutor(requireContext()),
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
                Log.v("IMAGE",bitmapImage.byteCount.toString())
                //bitmap.set(bitmapImage)
                //rotasi hasilnya dan simpan ke model bitmap
                twibbonViewModel._bitmap.value = ImageManip.rotateImage(bitmapImage,90.0f);
                //tututp gambar
                image.close()
            }

            override fun onError(exception: ImageCaptureException) {
                Log.e("IMAGE","Error capturing image")
            }
        })
     //   Log.v("IMAGE",bitmap.toString())
      //  return bitmap.get()
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
          //  val displayOrientation = viewer.display.rotation
          //  val metadata = ImageCapture.Metadata().apply{
            //this.rotationDegrees= rotationDegree
           // }
            imageCapture = ImageCapture.Builder()
           //     .setTargetRotation(displayOrientation)
                .setTargetRotation(requireView().display.rotation)
                .build()
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


    fun isPermissionGranted() = REQUIRED_PERMISSIONS.all{
        ContextCompat.checkSelfPermission(requireContext(),it)==PackageManager.PERMISSION_GRANTED
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if(requestCode==CAMERA_REQUEST__CODE_PERMISSIONS){
            if(isPermissionGranted()){
                setupCamera()
            }
            else{
                Toast.makeText(context,"Izin Membuka Kamera Tidak Diberikan!",Toast.LENGTH_SHORT)
                    .show()
                //stop aktivitas
                requireActivity().finish()
            }
        }
    }
}