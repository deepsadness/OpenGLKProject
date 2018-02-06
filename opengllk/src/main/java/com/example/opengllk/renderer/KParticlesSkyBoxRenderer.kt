package com.example.opengllk.renderer

import android.content.Context
import android.graphics.Color
import android.opengl.GLES20
import android.opengl.Matrix
import com.example.opengllk.R
import com.example.opengllk.geometry.Geometry
import com.example.opengllk.geometry.ParticleRandomShooter
import com.example.opengllk.geometry.ParticleSystem
import com.example.opengllk.geometry.SkyBox
import com.example.opengllk.helper.MatrixHelper
import com.example.opengllk.loadCubeMap
import com.example.opengllk.loadTexture
import com.example.opengllk.program.ParticlesShader
import com.example.opengllk.program.SkyboxShaderProgram
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

/**
 * 这里需要注意的是，gl的初始化，都需要在SurfaceCreated之后，不能在其之前
 * Created by Cry on 2018/2/3.
 */
class KParticlesSkyBoxRenderer(private val context: Context) : BaseRenderer() {
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

    //加入天空盒
    private var skyboxProgram: SkyboxShaderProgram? = null
    private var skybox: SkyBox? = null
    private var skyBoxTexture = 0

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        super.onSurfaceCreated(gl, config)


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


        //添加天空盒
        skyboxProgram = SkyboxShaderProgram(context)
        skybox = SkyBox()
        skyBoxTexture = intArrayOf(
                R.drawable.left,
                R.drawable.right,
                R.drawable.bottom,
                R.drawable.top,
                R.drawable.front,
                R.drawable.back
        ).loadCubeMap(context)

    }

    /**
     * 因为不希望天空盒和粒子使用相同的模型矩阵，所以单独出来
     */
    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        super.onSurfaceChanged(gl, width, height)

        val aspect = (width.toFloat() / height.toFloat())
        MatrixHelper.perspectiveM(projectionMatrix, 45, aspect, 1f, 10f)

//        Matrix.setIdentityM(viewMatrix, 0)
//        Matrix.translateM(viewMatrix, 0, 0f, -1.5f, -5f)
//        Matrix.multiplyMM(viewProjectionMatrix, 0, projectionMatrix, 0, viewMatrix, 0)

    }

    override fun onDrawFrame(gl: GL10?) {
        super.onDrawFrame(gl)

        drawSkyBox()
        drawParticles()


    }

    var xRotation = 0f
    var yRotation = 0f

    fun handleTouchDrag(deltaX: Float, deltaY: Float) {
        xRotation += deltaX / 16f
        yRotation += deltaY / 16f

        if (yRotation < -90) {
            yRotation = -90f
        } else if (yRotation > 90) {
            yRotation = 90f
        }
    }


    private fun drawSkyBox() {

        Matrix.setIdentityM(viewMatrix, 0)
        //先应用y轴的旋转矩阵，然后应用x轴。这叫fps样式的旋转。向上或者向下旋转总是让你向头上看，或者脚下看，向左或者向右旋转总是让你围着以你的脚为中心的圆进行来回旋转
        Matrix.rotateM(viewMatrix, 0, -yRotation, 1f, 0f, 0f)
        Matrix.rotateM(viewMatrix, 0, -xRotation, 0f, 1f, 0f)

        Matrix.multiplyMM(viewProjectionMatrix, 0, projectionMatrix, 0, viewMatrix, 0)

        skyboxProgram!!.useProgram()
        skyboxProgram!!.setUniforms(viewProjectionMatrix, skyBoxTexture)
        skybox!!.bindData(skyboxProgram!!)
        skybox!!.draw()

    }

    private fun drawParticles() {
        val currentTime = (System.nanoTime() - globalStartTime) / 1000000000f

        redShooter!!.addParticles(particleSystem!!, currentTime, 5)
        greenShooter!!.addParticles(particleSystem!!, currentTime, 5)
        blueShooter!!.addParticles(particleSystem!!, currentTime, 5)

        Matrix.setIdentityM(viewMatrix, 0)

        Matrix.rotateM(viewMatrix, 0, -yRotation, 1f, 0f, 0f)
        Matrix.rotateM(viewMatrix, 0, -xRotation, 0f, 1f, 0f)

        Matrix.translateM(viewMatrix, 0, 0f, -1.5f, -5f)
        Matrix.multiplyMM(viewProjectionMatrix, 0, projectionMatrix, 0, viewMatrix, 0)

        // Enable additive blending
        GLES20.glEnable(GLES20.GL_BLEND)
        GLES20.glBlendFunc(GLES20.GL_ONE, GLES20.GL_ONE)

        particleProgram!!.useProgram()
        particleProgram!!.setUniforms(viewProjectionMatrix, currentTime, textureId)
        particleSystem!!.bindData(particleProgram!!)
        particleSystem!!.draw()

        //停止混合渲染
        GLES20.glDisable(GLES20.GL_BLEND)
    }


}