package me.leefeng.libverify

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.drawable.ColorDrawable
import android.text.Editable
import android.text.InputFilter
import android.text.InputType
import android.text.TextWatcher
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.view.KeyEvent
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.TextView

/**
 *
 * Created by lilifeng on 2019/4/8
 *
 * 自定义View - 手机验证码输入框
 *
 */
class VerificationView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : ViewGroup(context, attrs, defStyleAttr), TextWatcher, View.OnKeyListener {
    override fun onKey(v: View?, keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_DEL) {
            backFocus()
        }
        return false
    }

    override fun afterTextChanged(s: Editable?) {
        synchronized(this) {
            focus()
        }

    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
    }


    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        val middle = (sizeW - sizeH * etTextCount) / (etTextCount - 1)
        for (i in 0 until etTextCount) {
            val et = getChildAt(i) as EditText
            val left = (middle + sizeH) * i
            et.layout(left, 0, left + sizeH, sizeH)
        }
        getChildAt(etTextCount).layout(l, t, r, b)

    }

    private val density = resources.displayMetrics.density
    private var etTextSize = 18 * density
    private var etTextCount = 4
    private var etBackGround = 0
    private var etBackGroundColor = Color.BLACK
    private var etTextColor = Color.BLACK

    init {
        val array = context.obtainStyledAttributes(attrs, R.styleable.VerificationView)
        etTextSize = array.getDimension(R.styleable.VerificationView_vTextSize, 18 * density)
        etTextCount = array.getInteger(R.styleable.VerificationView_vTextCount, 4)
        etBackGround = array.getResourceId(R.styleable.VerificationView_vBackgroundResource, 0)
        etBackGroundColor = array.getColor(R.styleable.VerificationView_vBackgroundColor, Color.BLACK)
        etTextColor = array.getColor(R.styleable.VerificationView_vTextColor, Color.BLACK)
        array.recycle()

    }

    private val pant = Paint()
    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        pant.isAntiAlias = true
        pant.color = Color.parseColor("#DCDCDC")
        pant.strokeWidth = density * 1
        var left = 0f
        val top = sizeH - pant.strokeWidth
//        for (i in 0 until etTextCount) {
//            canvas?.drawLine(left, top, left + w, top, pant)
//            left += w + middlw
//        }

    }

    private fun backFocus() {

        var editText: EditText
        for (i in etTextCount - 1 downTo 0) {
            editText = getChildAt(i) as EditText
            if (editText.text.length == 1) {
                showInputPad(editText)
                editText.setSelection(1)
                return
            }
        }
    }

    /**
     * 展示输入键盘
     */
    private fun showInputPad(editText: EditText) {
        editText.isFocusable = true
        editText.isFocusableInTouchMode = true
        editText.requestFocus()
        (context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).showSoftInput(editText, 0)
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        postDelayed({
            focus()
        }, 200)

    }


    private fun focus() {
        for (i in 0 until etTextCount) {
            val editText = getChildAt(i) as EditText
            if (editText.text.isEmpty()) {
                showInputPad(editText)
                editText.isCursorVisible = true
                return
            }
        }
        if ((getChildAt(etTextCount - 1) as EditText).text.isNotEmpty()) {
            val text = StringBuffer()
            for (i in 0 until etTextCount) {
                text.append((getChildAt(i) as EditText).text.toString())
            }
            finish?.invoke(text.toString())
            (context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).hideSoftInputFromWindow(
                getChildAt(etTextCount - 1).windowToken,
                0
            )
            (getChildAt(etTextCount - 1) as EditText).isCursorVisible = false

        }
    }

    /**
     * 最后一个完成回调
     */
    var finish: ((String) -> Unit)? = null
    private val paint = Paint()
    private val textRect = Rect()

    init {
        paint.isAntiAlias = true
        paint.textSize = etTextSize
        paint.getTextBounds("8", 0, 1, textRect)
        setWillNotDraw(false)
        for (i in 0 until etTextCount) {
            val et = EditText(context)
            et.gravity = Gravity.CENTER
            et.includeFontPadding = false
            if (etBackGroundColor != Color.BLACK)
                et.background = ColorDrawable(etBackGroundColor)
            et.includeFontPadding = false
            if (etBackGround != 0)
                et.setBackgroundResource(etBackGround)
            et.setEms(1)
            try {
                val f = TextView::class.java.getDeclaredField("mCursorDrawableRes")
                f.isAccessible = true
                f.set(et, R.drawable.cursor_color)
            } catch (e: Throwable) {
            }
            et.inputType = InputType.TYPE_CLASS_NUMBER
            et.setTextColor(etTextColor)
            et.setTextSize(TypedValue.COMPLEX_UNIT_PX, etTextSize)
            et.addTextChangedListener(this)
            et.setOnKeyListener(this)
            et.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(1))
            addView(et)
        }
        val view = View(context)
        view.setOnClickListener {
            requestEditeFocus()
        }
        addView(view)

    }

    private var sizeH = 0
    private var sizeW = 0
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        sizeW = View.MeasureSpec.getSize(widthMeasureSpec)
        sizeH = View.MeasureSpec.getSize(heightMeasureSpec)
        for (i in 0 until etTextCount) {
            val lp = getChildAt(0).layoutParams
            lp.width = sizeH
            lp.height = sizeH
            getChildAt(i).layoutParams = lp
        }
        measureChildren(widthMeasureSpec, heightMeasureSpec)
    }

    fun clear() {
        for (j in 0 until etTextCount) {
            val et = (getChildAt(j) as EditText)
            et.removeTextChangedListener(this)
            et.setText("")
            et.addTextChangedListener(this)
        }
        focus()
    }

    /**
     * 主动获取焦点 弹起键盘
     */
    private fun requestEditeFocus() {
        val lastETC = getChildAt(etTextCount - 1) as EditText
        if (lastETC.text.isNotEmpty()) {
            showInputPad(lastETC)
            lastETC.isCursorVisible = true
        } else
            focus()
    }
}