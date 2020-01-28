package com.example.simplemusicnotesreader.viewmodels

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

    private val _isPlaying = MutableLiveData<Boolean>()

    val isPlaying: LiveData<Boolean>
        get() = _isPlaying

    private val _barCount = MutableLiveData<Int>()

    val barCount: LiveData<Int>
        get() = _barCount

    private val _barTime = MutableLiveData<Long>()

    val barTime: LiveData<Long>
        get() = _barTime

    init {
        _isShowTitleImage.value = true
        _isPlaying.value = false
    }

    fun OpenFileFinish(barCount: Int, barTime: Long) {
        _barTime.value = barTime
        _isPlayBtnEnable.value = true
        _isShowTitleImage.value = false
        _barCount.value = barCount
    }

    fun OnPlay() {
        _isPlaying.value = !_isPlaying.value!!
    }
}