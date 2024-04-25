package com.d121211069.submissionawalandroidintermediate.customview

import android.content.Context
import android.graphics.Canvas
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.widget.AppCompatEditText
import com.d121211069.submissionawalandroidintermediate.R
import com.google.android.material.textfield.TextInputLayout

class EmailEditText @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : AppCompatEditText(context, attrs), View.OnTouchListener {

    init {
        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                val textInputLayout = (parent.parent as? TextInputLayout)
                val gmailPattern = "[a-zA-Z0-9._-]+@gmail\\.com"
                if (!s.toString().matches(gmailPattern.toRegex())) {
                    textInputLayout?.apply {
                        errorIconDrawable = null
                        isErrorEnabled = false
                        error = context.getString(R.string.error_email)
                    }
                } else {
                    textInputLayout?.error = null
                }
            }

            override fun afterTextChanged(s: Editable) {
            }
        })
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        textAlignment = View.TEXT_ALIGNMENT_VIEW_START
    }


    override fun onTouch(v: View?, event: MotionEvent): Boolean {
        return false
    }
}