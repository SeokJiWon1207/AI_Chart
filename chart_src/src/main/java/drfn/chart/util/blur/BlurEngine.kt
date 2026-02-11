package drfn.chart.util.blur

import android.graphics.Bitmap

interface BlurEngine {
    fun blur(image: Bitmap, radius: Int): Bitmap
}
