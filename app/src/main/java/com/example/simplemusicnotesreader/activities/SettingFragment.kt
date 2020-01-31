package com.example.simplemusicnotesreader.activities

import android.content.Context
import android.os.Bundle
import android.text.InputType
import androidx.preference.EditTextPreference
import androidx.preference.PreferenceFragmentCompat
import com.example.simplemusicnotesreader.R


class SettingFragment : PreferenceFragmentCompat() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        addPreferencesFromResource(R.xml.preferences)
    }


    override fun onAttach(context: Context) {
        super.onAttach(context)

    }



    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)

        var customTempoEditTextPreference =
            preferenceManager.findPreference<EditTextPreference>("custom_tempo")
        customTempoEditTextPreference!!.setOnBindEditTextListener { editText ->
            editText.inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_SIGNED
        }

        var intervalEditTextPreference =
            preferenceManager.findPreference<EditTextPreference>("interval")
        intervalEditTextPreference!!.setOnBindEditTextListener { editText ->
            editText.inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_SIGNED
        }
    }
}
