package com.example.majika.ui.branch

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class BranchViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is branch Fragment"
    }
    val text: LiveData<String> = _text
}