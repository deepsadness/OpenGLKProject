package com.example.opengllk.helper

/**
 * Created by Cry on 2018/2/3.
 */
class MatrixHelper {
    companion object {
        fun perspectiveM(m: FloatArray, yForInDegrees: Int, aspect: Float, n: Float, f: Float): Unit {
            //1.先计算视角。
            val angleInRadians = (yForInDegrees * 1.0f * Math.PI / 180.0f);
            val a = (1.0f / Math.tan(angleInRadians / 2.0f)).toFloat()

            //2.带入公式
            m[0] = a / aspect
            m[1] = 0f
            m[2] = 0f
            m[3] = 0f

            m[4] = 0f
            m[5] = a
            m[6] = 0f
            m[7] = 0f

            m[8] = 0f
            m[9] = 0f
            m[10] = -((f + n) / (f - n))
            m[11] = -1f

            m[12] = 0f
            m[13] = 0f
            m[14] = -((2f * f * n) / (f - n))
            m[15] = 0f
        }
    }
}