package com.example.majika.ui.twibbon

import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class TwibbonViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is twibbon Fragment"
    }
    val text: LiveData<String> = _text

    val _bitmap = MutableLiveData<Bitmap?>()
    private val bitmap: LiveData<Bitmap?>
        get() = _bitmap
}