package com.example.opengllk.program

import android.content.Context
import android.opengl.GLES20
import com.example.opengllk.R
import com.example.opengllk.program.SimpleShader.Companion.U_COLOR

/**
 * Created by Cry on 2018/2/3.
 */
class SimpleColorShader(context: Context) :
        BaseShaderProgram(context,
                R.raw.simple_color_vertex_shader,
                R.raw.simple_color_fragment_shader) {
    //定义需要链接的属性
    companion object {
        val A_POSITION = "a_Position"
        val A_COLOR = "a_Color"
    }

    private val aPositionLocation = GLES20.glGetAttribLocation(programId, A_POSITION)
    private val aColorLocation = GLES20.glGetAttribLocation(programId, A_COLOR)

    fun getPositionAttributeLocation() = aPositionLocation
    fun getColorAttributeLocation() = aColorLocation


}