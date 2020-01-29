package com.example.simplemusicnotesreader.models

import android.os.Parcelable
import com.example.simplemusicnotesreader.enums.Tie
import kotlinx.android.parcel.Parcelize


data class musicSheet(
    val title: String,
    val barWidth: Int,
    val barHeight: Int,
    val barDatas: ArrayList<barData>
)


@Parcelize
data class barData(
    val timeSignature: String,
    val keySignaturest: String,
    val notes: ArrayList<note>,
    val ties: ArrayList<tie>,
    val barTime: Long
) : Parcelable


@Parcelize
data class note(
    val key: String,
    val type: String,
    val accidental:String,
    val tie: Tie,
    val haveDot: Boolean
) : Parcelable

@Parcelize
data class tie(val start: Int, val end: Int) : Parcelable

