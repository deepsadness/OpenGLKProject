package com.example.opengllk.renderer

import android.content.Context
import android.graphics.Color
import android.opengl.GLES20
import android.opengl.Matrix
import com.example.opengllk.R
import com.example.opengllk.geometry.Geometry
import com.example.opengllk.geometry.ParticleRandomShooter
import com.example.opengllk.geometry.ParticleSystem
import com.example.opengllk.helper.MatrixHelper
import com.example.opengllk.loadTexture
import com.example.opengllk.program.ParticlesShader
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

/**
 * 这里需要注意的是，gl的初始化，都需要在SurfaceCreated之后，不能在其之前
 * Created by Cry on 2018/2/3.
 */
class KParticlesRandomRenderer(private val context: Context) : BaseRenderer() {
    private val projectionMatrix = FloatArray(16)
    private val viewMatrix = FloatArray(16)
    private val viewProjectionMatrix = FloatArray(16)

    private var particleProgram: ParticlesShader? = null
    private var particleSystem: ParticleSystem? = null
    private var redShooter: ParticleRandomShooter? = null
    private var greenShooter: ParticleRandomShooter? = null
    private var blueShooter: ParticleRandomShooter? = null
    private var globalStartTime = 0L
    private var textureId = 0

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        super.onSurfaceCreated(gl, config)

        // Enable additive blending
        GLES20.glEnable(GLES20.GL_BLEND)
        GLES20.glBlendFunc(GLES20.GL_ONE, GLES20.GL_ONE)


        particleProgram = ParticlesShader(context)

        particleSystem = ParticleSystem(10000)

        globalStartTime = System.nanoTime()

        val particleDirection = Geometry.Vector(0f, 0.5f, 0f)

        val angleVarianceInDegrees = 5f
        val speedVariance = 1f


        redShooter = ParticleRandomShooter(
                Geometry.Point(-1f, 0f, 0f),
                particleDirection,
                Color.rgb(255, 50, 5),
                angleVarianceInDegrees,
                speedVariance
        )
        greenShooter = ParticleRandomShooter(
                Geometry.Point(0f, 0f, 0f),
                particleDirection,
                Color.rgb(255, 255, 25),
                angleVarianceInDegrees,
                speedVariance
        )
        blueShooter = ParticleRandomShooter(
                Geometry.Point(1f, 0f, 0f),
                particleDirection,
                Color.rgb(5, 50, 255),
                angleVarianceInDegrees,
                speedVariance
        )

        //添加纹理
        textureId = R.drawable.particle_texture.loadTexture(context)

    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        super.onSurfaceChanged(gl, width, height)

        val aspect = (width.toFloat() / height.toFloat())
        MatrixHelper.perspectiveM(projectionMatrix, 45, aspect, 1f, 10f)

        Matrix.setIdentityM(viewMatrix, 0)
        Matrix.translateM(viewMatrix, 0, 0f, -1.5f, -5f)
        Matrix.multiplyMM(viewProjectionMatrix, 0, projectionMatrix, 0, viewMatrix, 0)
    }

    override fun onDrawFrame(gl: GL10?) {
        super.onDrawFrame(gl)

        val currentTime = (System.nanoTime() - globalStartTime) / 1000000000f

        redShooter!!.addParticles(particleSystem!!, currentTime, 5)
        greenShooter!!.addParticles(particleSystem!!, currentTime, 5)
        blueShooter!!.addParticles(particleSystem!!, currentTime, 5)

        particleProgram!!.useProgram()
        particleProgram!!.setUniforms(viewProjectionMatrix, currentTime,textureId)
        particleSystem!!.bindData(particleProgram!!)
        particleSystem!!.draw()

    }
}