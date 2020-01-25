package com.example.simplemusicnotesreader.models

import android.os.Parcelable
import com.example.simplemusicnotesreader.enums.Tie
import kotlinx.android.parcel.Parcelize

@Parcelize
data class barData(
    val timeSignature: String,
    val keySignaturest: String,
    val notes: ArrayList<note>,
    val ties: ArrayList<tie>,
    val width: Int,
    val height: Int,
    val speed: Int
) : Parcelable


@Parcelize
data class note(
    val key: String,
    val type: String,
    val tie: Tie,
    val haveDot: Boolean
) : Parcelable

//@Parcelize
//data class note(
//    val key: String,
//    val duration: Float,
//    val tie: Tie,
//    val isRest: Boolean,
//    val haveDot: Boolean
//) : Parcelable

@Parcelize
data class tie(val start: Int, val end: Int) : Parcelable

