package com.example.opengllk.program

import android.content.Context
import android.opengl.GLES20
import com.example.opengllk.compileShaderCode
import com.example.opengllk.glslResRead
import com.example.opengllk.linkProgram
import com.example.opengllk.validateAndUseProgram

/**
 * 代表shader program
 * 持有一个programid 和对应shader需要的属性。
 * Created by Cry on 2018/2/3.
 */
abstract class BaseShaderProgram(context: Context, vertexShaderRes: Int, fragShaderRes: Int) {
    protected val programId: Int

    init {
        //先取得文本
        val vertexShaderObjectId = vertexShaderRes.glslResRead(context).compileShaderCode(GLES20.GL_VERTEX_SHADER)
        val fragShaderObjectId = fragShaderRes.glslResRead(context).compileShaderCode(GLES20.GL_FRAGMENT_SHADER)

//        val vertexShaderObjectString =  TextResourceReader.readTextFileFromResource(context,vertexShaderRes)
//        val fragShaderObjectString =  TextResourceReader.readTextFileFromResource(context,fragShaderRes)
//        val vertexShaderObjectId = ShaderHelper.compileVertexShader(vertexShaderObjectString)
//        val fragShaderObjectId = ShaderHelper.compileFragmentShader(fragShaderObjectString)

        var intArrayOf = intArrayOf(vertexShaderObjectId, fragShaderObjectId)
        programId = intArrayOf.linkProgram()
    }

    fun useProgram() = programId.validateAndUseProgram()
}