package jp.ac.myrollingball

import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.SurfaceHolder
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), SensorEventListener, SurfaceHolder.Callback {

    private var surfaceWidth:Int = 0
    private var surfaceHeight:Int = 0

    private val radius = 50.0f
    private val coef = 1000.0f

    private var ballX:Float = 0f
    private var ballY:Float = 0f
    private var vx:Float = 0f
    private var vy:Float = 0f
    private var time:Long = 0L

    //private val blockList1: List<Float> = listOf(150F, 400F, 400F, 500F)
    //private val blockList2: List<Float> = listOf(700F, 300F, 800F, 500F)
    //private val blockList3: List<Float> = listOf(500F, 800F, 800F, 900F)
    //private val blockList4: List<Float> = listOf(150F, 400F, 400F, 500F)

    private val blockList: List<Float> = listOf(350F, 600F, 700F, 650F)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val holder = surfaceView.holder
        holder.addCallback(this)
    }

    override fun onResume() {
        super.onResume()

    }

    override fun onPause() {
        super.onPause()

    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {  }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event == null) {
            return
        }
        if (time == 0L) {
            time = System.currentTimeMillis()
        }
        if (event.sensor.type == Sensor.TYPE_ACCELEROMETER) {
            val x = event.values[0] * -1
            val y = event.values[1]

            var t = ((System.currentTimeMillis()) - time).toFloat()
            time = System.currentTimeMillis()
            t /= 1000.0f

            val dx = (vx * t) + (x * t * t) / 2.0f
            val dy = (vy * t) + (y * t * t) / 2.0f
            this.ballX += (dx * coef)
            this.ballY += (dy * coef)
            this.vx += (x * t)
            this.vy += (y * t)

            if ((this.ballX - radius < 0) && vx < 0) {//左
                vx = (vx * -1) / 1.5f
                this.ballX = this.radius
            } else if ((this.ballX + radius > this.surfaceWidth) && vx > 0) {//右
                vx = (vx * -1) / 1.5f
                this.ballX = (this.surfaceWidth - this.radius)
            } else if ((this.ballY - radius < 0) && vy < 0) {//上
                vy = (vy * -1) / 1.5f
                this.ballY = this.radius
            } else if ((this.ballY + radius > this.surfaceHeight)) {//下
                vy = (vy * -1) / 1.5f
                this.ballY = (this.surfaceHeight - this.radius)
            }


            if((ballX > blockList[0])&&(ballX < blockList[2])
                    &&(ballY > blockList[1])&&(ballY < blockList[3])) {
                time = 0
            }




            this.drawCanvas()
        }
    }

    override fun surfaceChanged(holder: SurfaceHolder?, format: Int, width: Int, height: Int) {
        this.surfaceWidth = width
        this.surfaceHeight = height

        //this.ballX = ( this.surfaceWidth/2 ).toFloat()
        //this.ballY = ( this.surfaceHeight/2 ).toFloat()

        this.ballX = 0F
        this.ballY = 0F

    }

    override fun surfaceDestroyed(holder: SurfaceHolder?) {
        val sensorManager = this.getSystemService( Context.SENSOR_SERVICE )as SensorManager
        sensorManager.unregisterListener(this)
    }

    override fun surfaceCreated(holder: SurfaceHolder?) {
        val sensorManager = this.getSystemService( Context.SENSOR_SERVICE )as SensorManager
        val accSensor = sensorManager.getDefaultSensor( Sensor.TYPE_ACCELEROMETER )
        sensorManager.registerListener(
            this, accSensor, SensorManager.SENSOR_DELAY_GAME )
    }


    private fun drawCanvas() {
        val canvas = surfaceView.holder.lockCanvas()
        canvas.drawColor(Color.GREEN)


        canvas.drawCircle(500F, 300F, 50F, Paint().apply { this.color = Color.BLACK })
        canvas.drawRect(150F, 400F, 400F, 500F, Paint().apply { this.color = Color.BLACK })
        canvas.drawRect(700F, 300F, 800F, 500F, Paint().apply { this.color = Color.BLACK })
        canvas.drawRect(500F, 800F, 800F, 900F, Paint().apply { this.color = Color.BLACK })
        canvas.drawRect(150F, 1000F, 450F, 1100F, Paint().apply { this.color = Color.BLACK })

        canvas.drawRect(blockList[0], blockList[1], blockList[2], blockList[3], Paint().apply { this.color = Color.BLUE })



        canvas.drawCircle (
            this.ballX, this.ballY, this.radius,
            Paint().apply { this.color = Color.RED }
        )

        surfaceView.holder.unlockCanvasAndPost(canvas)
    }

    //private fun hitCheck() {}

}
