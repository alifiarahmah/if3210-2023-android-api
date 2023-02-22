package com.example.majika.twibbon

import android.graphics.Bitmap
import android.graphics.Matrix


class ImageManip {
    companion object {
        fun rotateImage(source: Bitmap, angle: Float): Bitmap? {
            val matrix = Matrix()
            matrix.postRotate(angle)
            return Bitmap.createBitmap(
                source, 0, 0, source.width, source.height,
                matrix, true
            )
        }
    }
}