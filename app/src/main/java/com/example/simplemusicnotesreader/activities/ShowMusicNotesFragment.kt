package com.example.simplemusicnotesreader.activities

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import com.example.simplemusicnotesreader.R
import com.example.simplemusicnotesreader.databinding.FragmentShowMusicNotesBinding
import com.example.simplemusicnotesreader.factories.MusicNotesViewModelFactory
import com.example.simplemusicnotesreader.models.parseXml
import com.example.simplemusicnotesreader.models.xmldocListCorvertTobarDataList
import com.example.simplemusicnotesreader.viewmodels.MusicNotesViewModel
import com.google.gson.Gson


class ShowMusicNotesFragment : Fragment() {

    private lateinit var musicNotesViewModel: MusicNotesViewModel
    private lateinit var viewModelFactory: MusicNotesViewModelFactory
    private lateinit var browser:WebView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val binding: FragmentShowMusicNotesBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_show_music_notes, container, false
        )

        viewModelFactory = MusicNotesViewModelFactory()
        musicNotesViewModel =
            ViewModelProviders.of(this, viewModelFactory).get(MusicNotesViewModel::class.java)

        binding.openFileBtn.setOnClickListener { view ->
            val intent = Intent()
                .setType("*/*")
                .setAction(Intent.ACTION_GET_CONTENT)
            startActivityForResult(Intent.createChooser(intent, "Select musicXml File"), 111)

        }

        browser = binding.notesWebView
        val settings = browser.settings
        settings.javaScriptCanOpenWindowsAutomatically = true
        settings.javaScriptEnabled = true
        settings.allowFileAccess = true
        settings.domStorageEnabled = true

        browser.loadUrl("file:///android_asset/musicnotes.html")

        binding.musicNotesViewModel = musicNotesViewModel
        binding.setLifecycleOwner(this)
        return binding.root
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 111 && resultCode == RESULT_OK) {
            val selectedFile = data?.data
            val inputStream = activity?.contentResolver?.openInputStream(selectedFile!!)
            var doc = parseXml(inputStream!!)

            val barList = doc.getElementsByTagName("measure")

            val bars = xmldocListCorvertTobarDataList(barList)
            var gson = Gson()
            var jsonString: String = gson.toJson(bars)
//            println(jsonString)

            browser.post {
                run {
                    var url = "javascript:Drawmusicnotes('$jsonString')"
                    browser.loadUrl(url)
//                    browser.loadDataWithBaseURL("file:///android_asset/musicnotes.html", url, "text/html", "utf-8", null);
                }
            }

            musicNotesViewModel.OpenFileFinish()
        }
    }

}
