package com.example.simplemusicnotesreader.activities

import android.animation.ObjectAnimator
import android.app.Activity.RESULT_OK
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.view.*
import android.view.animation.LinearInterpolator
import android.webkit.WebView
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.Toast
import androidx.core.animation.doOnEnd
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.simplemusicnotesreader.R
import com.example.simplemusicnotesreader.databinding.FragmentShowMusicNotesBinding
import com.example.simplemusicnotesreader.factories.MusicNotesViewModelFactory
import com.example.simplemusicnotesreader.models.musicXmlReader
import com.example.simplemusicnotesreader.models.parseXml
import com.example.simplemusicnotesreader.viewmodels.MusicNotesViewModel
import com.google.gson.Gson

class ShowMusicNotesFragment : Fragment() {
    var CSpeed = 0
    val CUSTOMSPEED = "MySpeed"
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
            R.layout.fragment_show_music_notes, container, false
        )

        viewModelFactory = MusicNotesViewModelFactory()
        musicNotesViewModel =
            ViewModelProviders.of(this, viewModelFactory).get(MusicNotesViewModel::class.java)


//        binding.openFileBtn.setOnClickListener { view ->
//
//            openFile()
//        }

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
                        showAskTempoDialog()
                        musicNotesViewModel.onPlayOrStop()
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
                        musicNotesViewModel.onPlayEnd()
                    }
                    anim.setDuration(barCount * timePreBar).start()
                }
            } else {
                if (::anim.isInitialized) {
                    /**Scroll stop*/
                    anim.cancel()
                    if (musicNotesViewModel.isStop.value!!) {
                        browser.scrollTo(0, 0)
                    }
                }
            }
        })

        setHasOptionsMenu(true)

        binding.musicNotesViewModel = musicNotesViewModel
        binding.setLifecycleOwner(this)

        return binding.root
    }

    private fun openFile() {
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

            musicNotesViewModel.openFileFinish(sheetData)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater?.inflate(R.menu.option_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId){
            R.id.openfile ->{
                /**Select musicXMl File*/
                openFile()
                true
            }
            R.id.setting ->{
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    fun showAskTempoDialog() {
        var cTempo = 0
        val builder: AlertDialog.Builder = AlertDialog.Builder(this.activity)
        builder.setTitle("Please input the tempo")
        builder.setMessage("This Music Source File doesn't have tempo data\nPlease input the tempo(Beats Per Minute)")
        builder.setCancelable(false)
        val input = EditText(this.activity)

        input.setSingleLine()
        var container = FrameLayout(this.activity!!)

        val params = FrameLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        params.leftMargin = resources.getDimensionPixelSize(R.dimen.dialog_margin)
        params.rightMargin = resources.getDimensionPixelSize(R.dimen.dialog_margin)
        input.layoutParams = params
        container.addView(input)


        input.inputType = InputType.TYPE_CLASS_NUMBER
        builder.setView(container)
        builder.setPositiveButton("OK", null)
        val dialog = builder.create()
        dialog.show()
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(View.OnClickListener
        { v ->
            val inputString = input.text.toString()
            if (inputString == "") {
                Toast.makeText(context, "Please input the Tempo", Toast.LENGTH_SHORT).show()
            } else if (inputString == "0") {
                Toast.makeText(context, "The tempo must greater than 0", Toast.LENGTH_SHORT).show()
            } else {
                cTempo = inputString.toInt()
                musicNotesViewModel.reSetbarTime(cTempo)
                musicNotesViewModel.onPlayOrStop()
                dialog.dismiss()
            }
        })
    }
}
