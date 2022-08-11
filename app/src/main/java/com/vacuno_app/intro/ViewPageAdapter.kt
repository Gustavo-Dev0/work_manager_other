package com.vacuno_app.intro

import com.vacuno_app.R
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.viewpager.widget.PagerAdapter


class ViewPagerAdapter(
    val context: Context
    ) : PagerAdapter() {
    private var images = intArrayOf(
        R.drawable.image1,
        R.drawable.image2,
        R.drawable.image3,
        R.drawable.image4,
        R.drawable.image5
    )
    private var headings = intArrayOf(
        R.string.heading_one,
        R.string.heading_two,
        R.string.heading_three,
        R.string.heading_fourth,
        R.string.heading_fifth
    )
    private var description = intArrayOf(
        R.string.desc_one,
        R.string.desc_two,
        R.string.desc_three,
        R.string.desc_fourth,
        R.string.desc_fifth
    )


    private var layoutInflater: LayoutInflater? = null

    override fun getCount(): Int {
        return headings.size
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view === `object` as ConstraintLayout
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        layoutInflater = LayoutInflater.from(context)
        val view: View = layoutInflater!!.inflate(R.layout.slider_layout, container, false)
        val slideTitleImage: ImageView = view.findViewById(R.id.titleImage) as ImageView
        val slideHeading = view.findViewById(R.id.textTitle) as TextView
        val slideDescription = view.findViewById(R.id.textDescription) as TextView
        slideTitleImage.setImageResource(images[position])
        slideHeading.setText(headings[position])
        slideDescription.setText(description[position])
        container.addView(view)
        return view
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as ConstraintLayout)
    }
}