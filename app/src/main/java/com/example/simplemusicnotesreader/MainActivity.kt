package com.example.simplemusicnotesreader

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.simplemusicnotesreader.models.note
import com.google.gson.Gson

//import kotlinx.serialization.internal.UnitSerializer.serialize

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)



//        val test = note("e/4","8")
//        var gson = Gson()
//
//        var jsonString:String = gson.toJson(test)
//        println(jsonString)
    }
}
