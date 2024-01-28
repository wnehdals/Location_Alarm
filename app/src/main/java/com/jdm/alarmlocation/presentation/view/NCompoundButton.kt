package com.jdm.alarmlocation.presentation.view

import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.widget.CompoundButton
import android.widget.LinearLayout
import androidx.annotation.DrawableRes
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import com.jdm.alarmlocation.R
import com.jdm.alarmlocation.presentation.ui.util.toPx

abstract class NCompoundButton @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyleAttr: Int = 0,
    @DrawableRes imageDrawableId: Int
) : LinearLayout(context, attributeSet, defStyleAttr) {
    private var imageView: AppCompatImageView = AppCompatImageView(context, attributeSet, defStyleAttr)

    private var onCheckedChangeListener =
        CompoundButton.OnCheckedChangeListener { buttonView, isChecked -> }

    var isChecked = false
        set(value) {
            imageView.isSelected = value
            field = value
        }
    override fun setEnabled(enabled: Boolean) {
        super.setEnabled(enabled)
        imageView.isEnabled = enabled
    }
    init {
        orientation = HORIZONTAL
        gravity = Gravity.CENTER_VERTICAL
        context.theme.obtainStyledAttributes(
            attributeSet,
            R.styleable.NCompoundButton,
            defStyleAttr,
            0
        ).apply {

            imageView.setImageDrawable(ContextCompat.getDrawable(context, imageDrawableId))

            isChecked = getBoolean(R.styleable.NCompoundButton_android_checked, false)
            isEnabled = getBoolean(R.styleable.NCompoundButton_android_enabled, true)


            addView(imageView)
            recycle()
        }

        this.setOnClickListener {
            isChecked = !isChecked
            onCheckedChangeListener.onCheckedChanged(null, isChecked)
        }
    }
    fun setOnCheckedChangeListener(listener: CompoundButton.OnCheckedChangeListener) {
        onCheckedChangeListener = listener
    }


}