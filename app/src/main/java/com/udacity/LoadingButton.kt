package com.udacity

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import androidx.core.content.withStyledAttributes
import kotlin.properties.Delegates

class LoadingButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private var widthSize = 0f
    private var heightSize = 0f

    //custom attributes
    private var buttonBackgroundColor = 0
    private var buttonProgressColor = 0
    private var buttonCircleColor = 0
    private var buttonTextColor = 0

    private var valueAnimator = ValueAnimator()
    private var progress = 0
    private var text = "Download"
    private var textBounds = Rect()
    private val circleMargin = 15f

    private var buttonState: ButtonState by Delegates.observable<ButtonState>(ButtonState.Completed) { p, old, new ->
        when (new){
            ButtonState.Clicked -> {
                valueAnimator = ValueAnimator.ofInt(0, widthSize.toInt())
                valueAnimator.duration = 3000
                valueAnimator.addUpdateListener {
                    progress = it.animatedValue as Int
                    invalidate()
                }
                valueAnimator.disableViewDuringAnimation(this)
                valueAnimator.start()
            }
            ButtonState.Completed -> {
                valueAnimator.cancel()
                invalidate()
            }
        }
    }


    init {
        buttonState = ButtonState.Completed
        isClickable = true;

        context.withStyledAttributes(attrs, R.styleable.LoadingButton) {
            buttonBackgroundColor = getColor(R.styleable.LoadingButton_backgroundColor, 0)
            buttonProgressColor = getColor(R.styleable.LoadingButton_progressColor, 0)
            buttonCircleColor = getColor(R.styleable.LoadingButton_circleColor, 0)
            buttonTextColor = getColor(R.styleable.LoadingButton_textColor, 0)
        }

    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val minw: Int = paddingLeft + paddingRight + suggestedMinimumWidth
        val w: Int = resolveSizeAndState(minw, widthMeasureSpec, 1)
        val h: Int = resolveSizeAndState(MeasureSpec.getSize(w), heightMeasureSpec, 0)
        widthSize = w.toFloat()
        heightSize = h.toFloat()
        setMeasuredDimension(w, h)
    }

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        textAlign = Paint.Align.CENTER
        textSize = 55.0f
        color = buttonTextColor
        typeface = Typeface.create( "", Typeface.BOLD)
    }

    private val paintRect = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = buttonProgressColor
    }

    private val paintCircle = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = buttonCircleColor
    }

    //button background color
    //private var background_color = context.getColor(R.color.colorPrimary)


    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        canvas.drawColor(buttonBackgroundColor)

        if(buttonState == ButtonState.Clicked) {
            paint.getTextBounds(text,0,text.length, textBounds)
            canvas.drawRect(0f, 0f, progress.toFloat(), heightSize, paintRect)
            canvas.drawArc(
                widthSize / 2 + textBounds.width() / 2 + circleMargin,
                circleMargin,
                widthSize / 2 + textBounds.width() / 2 + heightSize - circleMargin,
                heightSize - circleMargin,
                0f, 360 * progress / widthSize, true, paintCircle
            )
            if(progress.toFloat() == widthSize)
                buttonState = ButtonState.Completed
        }

        canvas.drawText(text, widthSize/2, heightSize/2 + paint.textSize/4, paint)
    }

    override fun performClick(): Boolean {
        return if (buttonState != ButtonState.Clicked) {
            buttonState = ButtonState.Clicked
            super.performClick()
        } else
            true;
    }

    /**
     * Prevent restarts while the animation is running
     */
    private fun ValueAnimator.disableViewDuringAnimation(view: View) {
        addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationStart(animation: Animator?) {
                view.isEnabled = false
            }

            override fun onAnimationEnd(animation: Animator?) {
                view.isEnabled = true
            }
        })
    }

}