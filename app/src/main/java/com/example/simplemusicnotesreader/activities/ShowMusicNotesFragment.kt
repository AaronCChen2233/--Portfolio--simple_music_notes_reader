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
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.simplemusicnotesreader.databinding.FragmentShowMusicNotesBinding
import com.example.simplemusicnotesreader.factories.MusicNotesViewModelFactory
import com.example.simplemusicnotesreader.models.parseXml
import com.example.simplemusicnotesreader.models.musicXmlReader
import com.example.simplemusicnotesreader.viewmodels.MusicNotesViewModel
import com.google.gson.Gson
import android.animation.ObjectAnimator
import android.view.animation.LinearInterpolator
import androidx.core.animation.doOnEnd

class ShowMusicNotesFragment : Fragment() {
    private lateinit var musicNotesViewModel: MusicNotesViewModel
    private lateinit var viewModelFactory: MusicNotesViewModelFactory
    private lateinit var browser: WebView
    private lateinit var anim: ObjectAnimator
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
            com.example.simplemusicnotesreader.R.layout.fragment_show_music_notes, container, false
        )

        viewModelFactory = MusicNotesViewModelFactory()
        musicNotesViewModel =
            ViewModelProviders.of(this, viewModelFactory).get(MusicNotesViewModel::class.java)

        /**Select musicXMl File*/
        binding.openFileBtn.setOnClickListener { view ->
            /**If is playing stop it*/
            if (::anim.isInitialized) {
                /**Scroll stop*/
                anim.cancel()
            }

            val intent = Intent()
                .setType("*/*")
                .setAction(Intent.ACTION_GET_CONTENT)
            startActivityForResult(Intent.createChooser(intent, "Select musicXml File"), 111)
        }

        /**webView setting*/
        browser = binding.notesWebView
        browser.isVerticalFadingEdgeEnabled = false
        val settings = browser.settings
        settings.javaScriptCanOpenWindowsAutomatically = true
        settings.javaScriptEnabled = true
        settings.allowFileAccess = true
        settings.domStorageEnabled = true
        browser.loadUrl("file:///android_asset/musicnotes.html")


        musicNotesViewModel.isPlaying.observe(this, Observer { isPlaying ->
            if (isPlaying) {
                val timePreBar = musicNotesViewModel.barTime.value ?: 0L
                val height = (browser.contentHeight * browser.scale) - browser.height
                /**if barCount is null mean hadn't open file*/
                if (musicNotesViewModel.barCount.value != null) {
                    /**If timePreBar is 0 means that file doesn't speed data so ask user input speed*/
                    if (timePreBar == 0L) {

                    }

                    browser.scrollTo(0, 0)

                    val barCount = musicNotesViewModel.barCount.value!!
                    anim = ObjectAnimator.ofInt(
                        browser,
                        "scrollY",
                        0, height.toInt()
                    )
                    /**Even speed*/
                    anim.setInterpolator(LinearInterpolator())
                    anim.doOnEnd {
                        musicNotesViewModel.OnPlayEnd()
                    }
                    anim.setDuration(barCount * timePreBar).start()
                }
            } else {
                if (::anim.isInitialized) {
                    /**Scroll stop*/
                    anim.cancel()
                    if(musicNotesViewModel.isStop.value!!){
                        browser.scrollTo(0, 0)
                    }
                }
            }
        })

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
            var sheetData = musicXmlReader(doc)

            /**Convert to json*/
            var gson = Gson()
            var jsonString: String = gson.toJson(sheetData)
            browser.post {
                run {
                    var url = "javascript:Drawmusicnotes('$jsonString')"
                    browser.loadUrl(url)
                }
            }

            musicNotesViewModel.OpenFileFinish(
                sheetData.barDatas.size,
                sheetData.barDatas.get(0).barTime
            )
        }
    }

}
