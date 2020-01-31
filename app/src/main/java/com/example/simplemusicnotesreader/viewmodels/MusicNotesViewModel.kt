package com.example.simplemusicnotesreader.viewmodels

import android.animation.ObjectAnimator
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.simplemusicnotesreader.models.corvertSpeedtobarTime
import com.example.simplemusicnotesreader.models.musicSheet


class MusicNotesViewModel : ViewModel() {
    var musicSheet: musicSheet? = null
    var anim: ObjectAnimator? = null

    private val _isPlayBtnEnable = MutableLiveData<Boolean>()

    val isPlayBtnEnable: LiveData<Boolean>
        get() = _isPlayBtnEnable

    private val _isShowTitleImage = MutableLiveData<Boolean>()

    val isShowTitleImage: LiveData<Boolean>
        get() = _isShowTitleImage

    private val _isPlaying = MutableLiveData<Boolean>()

    val isPlaying: LiveData<Boolean>
        get() = _isPlaying

    private val _isStop = MutableLiveData<Boolean>()

    val isStop: LiveData<Boolean>
        get() = _isStop

    private val _barCount = MutableLiveData<Int>()

    val barCount: LiveData<Int>
        get() = _barCount

    private val _barTime = MutableLiveData<Long>()

    val barTime: LiveData<Long>
        get() = _barTime

    init {
        _isShowTitleImage.value = true
        _isPlaying.value = false
        _isStop.value = false
    }

    fun openFileFinish(sheetData: musicSheet) {
        _barTime.value = sheetData.barDatas.get(0).barTime
        _isPlayBtnEnable.value = true
        _isShowTitleImage.value = false
        _barCount.value = sheetData.barDatas.size
        musicSheet = sheetData

        _isStop.value = true
        _isPlaying.value = false
    }

    /**Play and Stop are same button click will change function*/
    fun onPlayOrStop() {
        _isStop.value = _isPlaying.value
        _isPlaying.value = !_isPlaying.value!!
    }

    fun onPlayEnd() {
        _isPlaying.value = false
    }

    fun reSetbarTime(newTime: Int) {
        /**Get beat from first bar timeSignature just for now
         * because now hadn't support different barTime*/
        _barTime.value = corvertSpeedtobarTime(newTime, musicSheet!!.barDatas[0].timeSignature.get(0).toString().toInt())
    }
}