package com.example.cry.openglkproject

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.icu.lang.UCharacter.GraphemeClusterBreak.L
import android.opengl.GLSurfaceView
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import com.example.cry.openglkproject.utils.shortToast
import com.example.opengllk.refer.ParticlesSkyBoxRenderer
import com.example.opengllk.renderer.*
import com.example.opengllk.supportOpenGLES2
import android.R.attr.angle
import android.R.attr.x
import android.R.attr.y
import com.example.opengllk.refer.ParticlesheightmapRenderer


class MainActivity : AppCompatActivity() {
    private lateinit var glSurfaceView: GLSurfaceView

    private var rendererSet: Boolean = false
    //    private var render: KParticlesSkyBoxRenderer? = null
    private var render: KParticlesHeightmapRenderer? = null

    private var sensorManager: SensorManager? = null

    private var gyroscopeSensor: Sensor? = null
    val value: SensorEventListener = object : SensorEventListener {

        private var previousX: Float = 0f
        private var previousY: Float = 0f
        var bx = 0f
        var by = 0f
        var bz = 0f
        var btime: Long = 0//这一次的时间
        private val angle = FloatArray(3)
        private val NS2S = 1.0f / 1000000000.0f // 将纳秒转化为秒
        private val timestamp: Float = 0.toFloat()
        var yAngle = 0f
        var xAngle = 0f
        override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
            Log.d("zzx", "onAccuracyChanged accuracy=" + accuracy)
        }

        override fun onSensorChanged(event: SensorEvent?) {
            //从 x、y、z 轴的正向位置观看处于原始方位的设备，如果设备逆时针旋转，将会收到正值；否则，为负值
            if (event != null) {
                val dT = (event.timestamp - timestamp) * NS2S
                angle[0] += event.values[0] * dT
                angle[1] += event.values[1] * dT
                angle[2] += event.values[2] * dT
                val anglex = Math.toDegrees(angle[0].toDouble())
                val angley = Math.toDegrees(angle[1].toDouble())
                val anglez = Math.toDegrees(angle[2].toDouble())

//                Log.d("onSensorChanged","values[0]="+ event.values[0])
//                Log.d("onSensorChanged","values[1]="+ event.values[1])
//                Log.d("onSensorChanged","values[2]="+ event.values[2])
//                Log.d("onSensorChanged","anglex="+ anglex)
//                Log.d("onSensorChanged","angley="+ angley)
//                Log.d("onSensorChanged","anglez="+ anglez)

//                    glSurfaceView.queueEvent {
//                        render!!.handleTouchDrag(
//                                previousX, previousY
//                        )
//                    }

            }
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //第一步，先判断是否支持
        var isSupport = this.supportOpenGLES2()
        if (!isSupport) {
            "This device does not support OpenGL ES 2.0!!".shortToast(this)
        } else {
            //2.如果支持的话，则开始创建glSurfaceView
            glSurfaceView = GLSurfaceView(this)
            //将其版本设置为2
            glSurfaceView.setEGLContextClientVersion(2)

//            glSurfaceView.setRenderer(SimpleRenderer(this))
//            glSurfaceView.setRenderer(SimpleColorRenderer(this))
//            glSurfaceView.setRenderer(SimpleMatrixRenderer(this))
//            glSurfaceView.setRenderer(Simple3DRenderer(this))
//            glSurfaceView.setRenderer(KParticlesRenderer(this))
            render = KParticlesHeightmapRenderer(this)
//            val render0 = ParticlesheightmapRenderer(this)
//            glSurfaceView.setRenderer(render0)
            glSurfaceView.setRenderer(render)
//            glSurfaceView.setRenderer(ParticlesRenderer(this))
//            glSurfaceView.setRenderer(AirHockeyRenderer(this))
            //设置标识位
            rendererSet = true

            queueTouchEvent()

            setContentView(glSurfaceView)
        }

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager



        gyroscopeSensor = sensorManager!!.getDefaultSensor(Sensor.TYPE_GYROSCOPE)
//        gyroscopeSensor = sensorManager!!.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

        //注册陀螺仪传感器，并设定传感器向应用中输出的时间间隔类型是SensorManager.SENSOR_DELAY_GAME(20000微秒)

        //SensorManager.SENSOR_DELAY_FASTEST(0微秒)：最快。最低延迟，一般不是特别敏感的处理不推荐使用，该模式可能在成手机电力大量消耗，由于传递的为原始数据，诉法不处理好会影响游戏逻辑和UI的性能

        //SensorManager.SENSOR_DELAY_GAME(20000微秒)：游戏。游戏延迟，一般绝大多数的实时性较高的游戏都是用该级别

        //SensorManager.SENSOR_DELAY_NORMAL(200000微秒):普通。标准延时，对于一般的益智类或EASY级别的游戏可以使用，但过低的采样率可能对一些赛车类游戏有跳帧现象

        //SensorManager.SENSOR_DELAY_UI(60000微秒):用户界面。一般对于屏幕方向自动旋转使用，相对节省电能和逻辑处理，一般游戏开发中不使用


    }

    private fun queueTouchEvent(renderObject: Any) {
        if (renderObject is ParticlesSkyBoxRenderer || renderObject is ParticlesheightmapRenderer) {
            val touchEvent: View.OnTouchListener
            if (renderObject is ParticlesSkyBoxRenderer) {
                val skyBoxRender = renderObject as ParticlesSkyBoxRenderer
                touchEvent = object : View.OnTouchListener {
                    private var previousX: Float = 0f
                    private var previousY: Float = 0f

                    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                        if (event != null) {
                            when (event.action) {
                                MotionEvent.ACTION_DOWN -> {
                                    previousX = event.x
                                    previousY = event.y
                                }
                                MotionEvent.ACTION_MOVE -> {
                                    val deltaX = event.x - previousX
                                    val deltaY = event.y - previousY

                                    previousX = event.x
                                    previousY = event.y

                                    glSurfaceView.queueEvent {
                                        skyBoxRender.handleTouchDrag(
                                                deltaX, deltaY
                                        )
                                    }
                                }
                            }
                            return true
                        }
                        return false
                    }
                }
            } else {
                val skyBoxRender = renderObject as ParticlesheightmapRenderer
                touchEvent = object : View.OnTouchListener {
                    private var previousX: Float = 0f
                    private var previousY: Float = 0f

                    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                        if (event != null) {
                            when (event.action) {
                                MotionEvent.ACTION_DOWN -> {
                                    previousX = event.x
                                    previousY = event.y
                                }
                                MotionEvent.ACTION_MOVE -> {
                                    val deltaX = event.x - previousX
                                    val deltaY = event.y - previousY

                                    previousX = event.x
                                    previousY = event.y

                                    glSurfaceView.queueEvent {
                                        skyBoxRender.handleTouchDrag(
                                                deltaX, deltaY
                                        )
                                    }
                                }
                            }
                            return true
                        }
                        return false
                    }
                }
            }

            glSurfaceView.setOnTouchListener(touchEvent)
        }
    }

    private fun queueTouchEvent() {
        if (render is KParticlesHeightmapRenderer) {
            val skyBoxRender = render as KParticlesHeightmapRenderer
            val touchEvent: View.OnTouchListener = object : View.OnTouchListener {
                private var previousX: Float = 0f
                private var previousY: Float = 0f

                override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                    if (event != null) {
                        when (event.action) {
                            MotionEvent.ACTION_DOWN -> {
                                previousX = event.x
                                previousY = event.y
                            }
                            MotionEvent.ACTION_MOVE -> {
                                val deltaX = event.x - previousX
                                val deltaY = event.y - previousY

                                previousX = event.x
                                previousY = event.y

                                glSurfaceView.queueEvent {
                                    skyBoxRender.handleTouchDrag(
                                            deltaX, deltaY
                                    )
                                }
                            }
                        }
                        return true
                    }
                    return false
                }
            }
            glSurfaceView.setOnTouchListener(touchEvent)
        }
    }

    override fun onPause() {
        super.onPause()
        if (rendererSet) {
            glSurfaceView.onPause()
//            sensorManager!!.unregisterListener(value)

        }
    }

    override fun onResume() {
        super.onResume()
        if (rendererSet) {
            glSurfaceView.onResume()
//            sensorManager!!.registerListener(value, gyroscopeSensor, SensorManager.SENSOR_DELAY_GAME)
//            sensorManager!!.registerListener(value, gyroscopeSensor, SensorManager.SENSOR_DELAY_UI)

        }
    }
}

