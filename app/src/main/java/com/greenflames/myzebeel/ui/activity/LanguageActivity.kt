package com.greenflames.myzebeel.ui.activity

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import com.greenflames.myzebeel.R
import com.greenflames.myzebeel.helpers.Global
import com.greenflames.myzebeel.preferences.PREF_LANGUAGE
import com.greenflames.myzebeel.preferences.Pref
import java.util.*


class LanguageActivity : AppCompatActivity() {
    private var ivEnglishTck : ImageView?=null
    private var ivArabicTick : ImageView?=null
    private var  llArabic : LinearLayout?=null
    private var  llEnglish : LinearLayout?=null
    private var pref: Pref? = null
    private var languageCode : String?=null
    private var back: ImageButton? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_language)
        ivEnglishTck = findViewById(R.id.image_tick)
        ivArabicTick = findViewById(R.id.image_arabic_tick)
        llArabic = findViewById(R.id.layout_arabic)
        llEnglish = findViewById(R.id.layout_english)
        back = findViewById(R.id.imageButton_back_contact)
        back?.setOnClickListener(View.OnClickListener {
            finish()
        })
        pref = Pref(this)
        languageCode = pref?.getString(PREF_LANGUAGE)
        if (languageCode == "en"){
            ivEnglishTck?.visibility = View.VISIBLE
            ivArabicTick?.visibility = View.GONE
        }else{
            ivEnglishTck?.visibility = View.GONE
            ivArabicTick?.visibility = View.VISIBLE
        }
        llEnglish?.setOnClickListener {
            pref?.putString(PREF_LANGUAGE,"en")
            ivEnglishTck?.visibility = View.VISIBLE
            ivArabicTick?.visibility = View.GONE
            val locale = Locale("en")
            Locale.setDefault(locale)
            val config = Configuration()
            config.locale = locale
            baseContext.resources.updateConfiguration(
                config,
                baseContext.resources.displayMetrics
            )
            Global.STORE_LANGUAGE = "en/"
            var intent = Intent(this,HomeActivity::class.java)
            startActivity(intent)
            finishAffinity()
        }
        llArabic?.setOnClickListener {
            val locale = Locale("ar")
            Locale.setDefault(locale)
            val config = Configuration()
            config.locale = locale
            baseContext.resources.updateConfiguration(
                config,
                baseContext.resources.displayMetrics
            )
            pref?.putString(PREF_LANGUAGE,"ar")
            ivEnglishTck?.visibility = View.GONE
            ivArabicTick?.visibility = View.VISIBLE
            Global.STORE_LANGUAGE = "ar/"
            var intent = Intent(this,HomeActivity::class.java)
            startActivity(intent)
            finishAffinity()
        }
    }
}