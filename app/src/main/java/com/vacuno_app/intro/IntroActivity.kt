package com.vacuno_app.intro

import android.content.Intent
import android.os.Bundle
import android.text.Html
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import androidx.viewpager.widget.ViewPager.OnPageChangeListener
import com.vacuno_app.R
import com.vacuno_app.databinding.ActivityIntroBinding
import com.vacuno_app.select_farm.SelectFarmActivity


class IntroActivity : AppCompatActivity() {

    private lateinit var binding: ActivityIntroBinding

    lateinit var mSlideViewPager: ViewPager
    lateinit var mDotLayout: LinearLayout
    lateinit var backBtn: Button
    lateinit var nextBtn:Button
    lateinit var skipBtn:Button

    lateinit var dots: Array<TextView?>
    lateinit var viewPagerAdapter: ViewPagerAdapter

    private lateinit var currentUserId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityIntroBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val idUser: String = intent.extras?.getString("userId").toString()
        currentUserId = idUser

        val sf = readSharedPreferences()
        if(sf == "YES"){
            val intent = Intent(applicationContext, SelectFarmActivity::class.java)
            intent.putExtra("userId", currentUserId)
            startActivity(intent)
            this@IntroActivity.finish()
        }


        backBtn = binding.backbtn
        nextBtn = binding.nextbtn
        skipBtn = binding.skipButton

        mSlideViewPager = binding.slideViewPager
        mDotLayout = binding.indicatorLayout
        viewPagerAdapter = ViewPagerAdapter(this)
        mSlideViewPager.adapter = viewPagerAdapter
        setUpIndicator(0)
        mSlideViewPager.addOnPageChangeListener(viewListener)



        backBtn.setOnClickListener{
            if (getItem(0) > 0) {
                mSlideViewPager.setCurrentItem(getItem(-1), true)
            }
        }
        nextBtn.setOnClickListener {
            if (getItem(0) < 4) mSlideViewPager.setCurrentItem(getItem(1), true) else {
                saveInSharedPreferences()
                val intent = Intent(applicationContext, SelectFarmActivity::class.java)
                intent.putExtra("userId", currentUserId)
                startActivity(intent)
                this@IntroActivity.finish()
            }
        }
        skipBtn.setOnClickListener {
            val intent = Intent(applicationContext, SelectFarmActivity::class.java)
            saveInSharedPreferences()
            intent.putExtra("userId", currentUserId)
            startActivity(intent)
            this@IntroActivity.finish()
        }
    }

    private fun setUpIndicator(position: Int) {
        dots = arrayOfNulls(5)
        mDotLayout.removeAllViews()
        for (i in dots.indices) {
            dots[i] = TextView(this)
            dots[i]!!.text = Html.fromHtml("&#8226")
            dots[i]!!.textSize = 35f
            dots[i]!!.setTextColor(resources.getColor(R.color.secondaryColor, applicationContext.theme))
            mDotLayout.addView(dots[i])
        }
        dots[position]!!.setTextColor(resources.getColor(R.color.primaryColor, applicationContext.theme))
    }

    private var viewListener: OnPageChangeListener = object : OnPageChangeListener {
        override fun onPageScrolled(
            position: Int,
            positionOffset: Float,
            positionOffsetPixels: Int
        ) {
        }

        override fun onPageSelected(position: Int) {
            setUpIndicator(position)
            if (position > 0) {
                backBtn.visibility = View.VISIBLE
            } else {
                backBtn.visibility = View.INVISIBLE
            }
        }

        override fun onPageScrollStateChanged(state: Int) {}
    }

    private fun getItem(i: Int): Int {
        return mSlideViewPager.currentItem + i
    }


    private fun saveInSharedPreferences() {
        val sharedPref = getSharedPreferences("vacuno", 0)

        with(sharedPref.edit()){
            putString("introOpened", "YES")
            apply()
        }
    }

    private fun readSharedPreferences(): String? {
        val defaultValue = ""
        val sharedPref = getSharedPreferences("vacuno", 0)

        return sharedPref.getString("introOpened", defaultValue)
    }

}