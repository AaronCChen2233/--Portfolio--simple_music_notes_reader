package com.example.simplemusicnotesreader.factories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.simplemusicnotesreader.viewmodels.MusicNotesViewModel


class MusicNotesViewModelFactory() : ViewModelProvider.Factory {

    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {

        if (modelClass.isAssignableFrom(MusicNotesViewModel::class.java)) {
            return MusicNotesViewModel() as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }

}