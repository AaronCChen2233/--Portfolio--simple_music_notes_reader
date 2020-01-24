package com.example.simplemusicnotesreader.models

import android.os.Parcelable
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
data class note(val key: String, val duration: Float,val tie:Tie) : Parcelable

@Parcelize
data class tie(val start: Int, val end: Int) : Parcelable

enum class Tie{
    Non,
    Start,
    Stop,
    /**have Start and stop*/
    Both
}