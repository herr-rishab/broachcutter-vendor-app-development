package net.broachcutter.vendorapp.views

import android.graphics.*
import android.graphics.drawable.Drawable

@Suppress("MagicNumber")
class NotificationDotDrawable : Drawable() {

    private var mBadgePaint: Paint = Paint()

    private var willDraw = false

    init {
        mBadgePaint.color = Color.parseColor("#ffb900")
        mBadgePaint.isAntiAlias = true
        mBadgePaint.style = Paint.Style.FILL
    }

    override fun draw(canvas: Canvas) {
        if (!willDraw) {
            return
        }
        val bounds = bounds
        val width = bounds.right - bounds.left
        val height = bounds.bottom - bounds.top

        // Position the badge in the top-right quadrant of the icon.
        /*Using Math.max rather than Math.min */
        val radius: Float = (width.coerceAtLeast(height) / 2 / 2).toFloat()
        val centerX: Float = width - radius - 1f + 5
        val centerY: Float = radius - 5
        canvas.drawCircle(centerX, centerY, ((radius + 0.5).toFloat()), mBadgePaint)
    }

    override fun setVisible(visible: Boolean, restart: Boolean): Boolean {
        super.setVisible(visible, restart)
        willDraw = visible
        invalidateSelf()
        return true
    }

    override fun setAlpha(alpha: Int) {
        // do nothing
    }

    override fun setColorFilter(cf: ColorFilter?) {
        // do nothing
    }

    override fun getOpacity(): Int {
        return PixelFormat.UNKNOWN
    }
}
