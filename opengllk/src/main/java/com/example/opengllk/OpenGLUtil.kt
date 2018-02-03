package com.example.opengllk

import android.app.ActivityManager
import android.content.Context
import android.content.res.Resources
import android.graphics.BitmapFactory
import android.opengl.GLES20
import android.opengl.GLUtils
import android.os.Build
import android.util.Log
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader

/**
 * 自定义的一些工具类
 * Created by Cry on 2018/2/3.
 */
/**
 * 判断是否支持OpenGL es2
 */
fun Context.supportOpenGLES2(): Boolean {
    val activityManager =
            this.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager

    return (activityManager.deviceConfigurationInfo.reqGlEsVersion >= 0x20000
            || (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1
            && (Build.FINGERPRINT.startsWith("generic")
            || Build.FINGERPRINT.startsWith("unknown")
            || Build.MODEL.contains("google_sdk")
            || Build.MODEL.contains("Emulator")
            || Build.MODEL.contains("Android SDK built for x86")
            )))
}

inline fun String.throwRuntime(e: Exception): Nothing = throw RuntimeException(this, e)
/**
 * 将GLSL的着色器代码读成字符串
 */
fun Int.glslResRead(context: Context): String {
    val body = StringBuilder()
    try {
        val openRawResource = context.resources.openRawResource(this)
        val inputStreamReader = InputStreamReader(openRawResource)
        val bufferedReader = BufferedReader(inputStreamReader)
        var line: String?
        do {
            line = bufferedReader.readLine()
            if (line != null) {
                body.append(line)
                body.append("\n")
            }
        } while (line != null)
    } catch (e: IOException) {
        //io exception
        ("Could not open resource:" + this).throwRuntime(e)
    } catch (e1: Resources.NotFoundException) {
        //not resource found
        ("Could not found resource:" + this).throwRuntime(e1)
    }
    return body.toString()
}

/**
 * 编译代码。编译后，会返回一个shaderObjectId，代表这个shader的引用来使用
 */
fun String.compileShaderCode(type: Int): Int {
    //先创建一个shader
    val shaderObjectId = GLES20.glCreateShader(type)

    //如果表示0 ，则表示创建成功
    if (shaderObjectId != 0) {
        //上传代码
        GLES20.glShaderSource(shaderObjectId, this)
        //进行编译
        GLES20.glCompileShader(shaderObjectId)

        //检查这个shader当前的状态
        val status = IntArray(1)

        GLES20.glGetShaderiv(shaderObjectId, GLES20.GL_COMPILE_STATUS, status, 0)

        //等于0，则表示编译失败了
        if (status[0] == 0) {
            //删除对这个shader的引用
            GLES20.glDeleteShader(shaderObjectId)
            //输出log
            Log.w("OpenGL Utils", "compile failed!");
            return 0
        }
    }
    return shaderObjectId
}


/**
 * 当当前表示ShaderObjectId的Int值绑定到项目中.
 */
fun IntArray.linkProgram(): Int {
    //先创建一个shader
    val programObjectId = GLES20.glCreateProgram()

    //如果表示0 ，则表示创建成功
    if (programObjectId != 0) {
        //2. 附上着色器
        GLES20.glAttachShader(programObjectId, this[0])
        GLES20.glAttachShader(programObjectId, this[1])
        //3.链接项目
        GLES20.glLinkProgram(programObjectId)

        //检查link的状态
        val status = IntArray(1)

        GLES20.glGetProgramiv(programObjectId, GLES20.GL_LINK_STATUS, status, 0)

        //等于0，则表示链接失败了
        if (status[0] == 0) {
            //删除对这个program的引用
            GLES20.glDeleteProgram(programObjectId)
            //输出log
            Log.w("OpenGL Utils", "link failed!")
            return 0
        }
    }
    return programObjectId
}

/**
 * 校验和使用项目
 */
fun Int.validateAndUseProgram() {
    GLES20.glValidateProgram(this)
    //检查link的状态
    val status = IntArray(1)

    GLES20.glGetProgramiv(this, GLES20.GL_VALIDATE_STATUS, status, 0)

    //等于0，则表示链接失败了
    if (status[0] == 0) {
        Log.w("OpenGL Utils", "Program validate failed!")
    } else {
        Log.d("OpenGL Utils", "Program validate Success!")
        GLES20.glUseProgram(this)
    }
}

/**
 * 加载纹理
 */
fun Int.loadTexture(context: Context): Int {
    var textureObjectsIds = IntArray(1)
    //渲染纹理
    GLES20.glGenTextures(1, textureObjectsIds, 0)
    if (textureObjectsIds[0] == 0) {
        Log.e("OpenGL Utils", "Could not generate a new OpenGL texture object.")
        return 0
    }
    //进行位图bitmap的绑定
    var options = BitmapFactory.Options()
    options.inScaled = false

    val bitmap = BitmapFactory.decodeResource(context.resources, this, options)
    if (bitmap == null) {
        Log.e("OpenGL Utils", "Resource ID" + this + " could not be decoded.")
        GLES20.glDeleteTextures(1, textureObjectsIds, 0)
        return 0
    }
    //接着进行绑定
    GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureObjectsIds[0])

    //设置纹理的过滤模式
    //缩小过滤算法，三线性贴图.放大使用双线性
    GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR_MIPMAP_LINEAR)
    GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR)

    //绑定上bitmap
    GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0)

    GLES20.glGenerateMipmap(GLES20.GL_TEXTURE_2D)

    bitmap.recycle()

    GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0)

    return textureObjectsIds[0]
}

