package com.example.opengllk.renderer

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.opengl.GLES20
import android.opengl.Matrix
import com.example.opengllk.R
import com.example.opengllk.geometry.*
import com.example.opengllk.helper.MatrixHelper
import com.example.opengllk.loadCubeMap
import com.example.opengllk.loadTexture
import com.example.opengllk.program.HeightMapShaderProgram
import com.example.opengllk.program.ParticlesShader
import com.example.opengllk.program.SkyboxShaderProgram
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

/**
 * 这里需要注意的是，gl的初始化，都需要在SurfaceCreated之后，不能在其之前
 * Created by Cry on 2018/2/3.
 */
class KParticlesHeightmapRenderer(private val context: Context) : BaseRenderer() {


    private val modelMatrix = FloatArray(16)
    private val viewMatrix = FloatArray(16)
    private val viewMatrixForSkyBox = FloatArray(16)
    private val projectionMatrix = FloatArray(16)

    private val tempMatrix = FloatArray(16)
    private val modelViewProjectionMatrix = FloatArray(16)

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

    //加入高度图
    private var heightShaderProgram: HeightMapShaderProgram? = null
    private var heightmap: Heightmap? = null
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

        updateViewMatrices()
    }

    private fun updateViewMatrices() {
        Matrix.setIdentityM(viewMatrix, 0)

        Matrix.rotateM(viewMatrix, 0, -yRotation, 1f, 0f, 0f)
        Matrix.rotateM(viewMatrix, 0, -xRotation, 0f, 1f, 0f)

        System.arraycopy(viewMatrix, 0, viewMatrixForSkyBox, 0, viewMatrix.size)

        Matrix.translateM(viewMatrix, 0, 0f, -1.5f, -5f)
    }

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        super.onSurfaceCreated(gl, config)

        //打开深度缓冲区功能
        GLES20.glEnable(GLES20.GL_DEPTH_TEST)
        //OpenGL就会查看每个三角形的卷曲属性，他就是我们定义顶点的顺序。如果是逆时针，就绘制。不是就丢弃。
        GLES20.glEnable(GLES20.GL_CULL_FACE)

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


        heightShaderProgram = HeightMapShaderProgram(context)
        val bitmapDrawable = context.resources.getDrawable(R.drawable.heightmap) as BitmapDrawable

        heightmap = Heightmap(bitmapDrawable.bitmap)

    }

    /**
     * 因为不希望天空盒和粒子使用相同的模型矩阵，所以单独出来
     */
    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        super.onSurfaceChanged(gl, width, height)

        val aspect = (width.toFloat() / height.toFloat())
        MatrixHelper.perspectiveM(
                projectionMatrix,
                45,
                aspect, 1f,
                100f)

        updateViewMatrices()

    }

    override fun onDrawFrame(gl: GL10?) {
        //告诉新帧上也要清除深度缓冲区
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT or GLES20.GL_DEPTH_BUFFER_BIT)

        //绘制高度图
        drawHeightmap()
        drawSkyBox()
        drawParticles()
    }

    private fun drawHeightmap() {
        Matrix.setIdentityM(modelMatrix, 0)

        Matrix.scaleM(modelMatrix, 0, 100f, 10f, 100f)
        updateMvpMatrices()
        heightShaderProgram!!.useProgram()
        heightShaderProgram!!.setUniforms(modelViewProjectionMatrix)
        heightmap!!.bindData(heightShaderProgram!!)
        heightmap!!.draw()
    }

    private fun drawSkyBox() {
        Matrix.setIdentityM(modelMatrix, 0)
        updateMvpMatricesForSkyBox()

        GLES20.glDepthFunc(GLES20.GL_LEQUAL); // This avoids problems with the skybox itself getting clipped.

        skyboxProgram!!.useProgram()
        skyboxProgram!!.setUniforms(modelViewProjectionMatrix, skyBoxTexture)
        skybox!!.bindData(skyboxProgram!!)
        skybox!!.draw()

        GLES20.glDepthFunc(GLES20.GL_LESS);

    }

    private fun drawParticles() {
        val currentTime = (System.nanoTime() - globalStartTime) / 1000000000f

        redShooter!!.addParticles(particleSystem!!, currentTime, 5)
        greenShooter!!.addParticles(particleSystem!!, currentTime, 5)
        blueShooter!!.addParticles(particleSystem!!, currentTime, 5)

        Matrix.setIdentityM(modelMatrix, 0)

        updateMvpMatrices()

        GLES20.glDepthMask(false);
        // Enable additive blending
        GLES20.glEnable(GLES20.GL_BLEND)
        GLES20.glBlendFunc(GLES20.GL_ONE, GLES20.GL_ONE)

        particleProgram!!.useProgram()
        particleProgram!!.setUniforms(modelViewProjectionMatrix, currentTime, textureId)
        particleSystem!!.bindData(particleProgram!!)
        particleSystem!!.draw()

        //停止混合渲染
        GLES20.glDisable(GLES20.GL_BLEND)
        GLES20.glDepthMask(true);
    }


    private fun updateMvpMatrices() {
        Matrix.multiplyMM(tempMatrix, 0, viewMatrix, 0, modelMatrix, 0)
        Matrix.multiplyMM(modelViewProjectionMatrix, 0, projectionMatrix, 0, tempMatrix, 0)

    }

    private fun updateMvpMatricesForSkyBox() {
        Matrix.multiplyMM(tempMatrix, 0, viewMatrixForSkyBox, 0, modelMatrix, 0)
        Matrix.multiplyMM(modelViewProjectionMatrix, 0, projectionMatrix, 0, tempMatrix, 0)
    }
}