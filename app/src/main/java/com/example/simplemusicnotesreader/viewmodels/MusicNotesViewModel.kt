package com.example.simplemusicnotesreader.viewmodels

import android.content.Intent
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel


class MusicNotesViewModel : ViewModel() {

    private val _isPlayBtnEnable = MutableLiveData<Boolean>()

    val isPlayBtnEnable: LiveData<Boolean>
        get() = _isPlayBtnEnable


    private val _isShowTitleImage = MutableLiveData<Boolean>()

    val isShowTitleImage: LiveData<Boolean>
        get() = _isShowTitleImage

    init {
        _isShowTitleImage.value = true
    }

    fun OpenFileFinish() {
        _isPlayBtnEnable.value = true
        _isShowTitleImage.value = false
    }
}