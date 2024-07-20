import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.widget.Toast
import com.example.womenssafetyapplication.AccelerometerListener
import kotlin.math.abs

object AccelerometerManager {
    private var threshold = 15.0f
    private var interval = 200
    private var sensor: Sensor? = null
    private var sensorManager: SensorManager? = null
    private var listener: AccelerometerListener? = null
    private var supported: Boolean? = null

    var isListening = false
        private set

    fun stopListening() {
        isListening = false
        try {
            sensorManager?.unregisterListener(sensorEventListener)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun isSupported(context: Context?): Boolean {
        if (supported == null) {
            if (context != null) {
                val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
                val sensors = sensorManager.getSensorList(Sensor.TYPE_ACCELEROMETER)
                supported = sensors.isNotEmpty()
            } else {
                supported = false
            }
        }
        return supported!!
    }

    fun configure(threshold: Int, interval: Int) {
        AccelerometerManager.threshold = threshold.toFloat()
        AccelerometerManager.interval = interval
    }

    fun startListening(accelerometerListener: AccelerometerListener?) {
        if (accelerometerListener == null) return
        val context = accelerometerListener as? Context ?: return
        sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        val sensors = sensorManager!!.getSensorList(Sensor.TYPE_ACCELEROMETER)
        if (sensors.isNotEmpty()) {
            sensor = sensors[0]
            isListening = sensorManager!!.registerListener(
                sensorEventListener, sensor,
                SensorManager.SENSOR_DELAY_GAME
            )
            listener = accelerometerListener
        }
    }

    fun startListening(accelerometerListener: AccelerometerListener?, threshold: Int, interval: Int) {
        configure(threshold, interval)
        startListening(accelerometerListener)
    }

    private fun handleShakeEvent(force: Float) {
        listener?.onShake(force)
        if (force > threshold) {
            listener?.onEmergencyDetected()
        }
    }

    private val sensorEventListener: SensorEventListener = object : SensorEventListener {
        private var lastUpdate: Long = 0
        private var lastShake: Long = 0
        private var lastX = 0f
        private var lastY = 0f
        private var lastZ = 0f

        override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {}

        override fun onSensorChanged(event: SensorEvent) {
            val now = System.currentTimeMillis()
            val x = event.values[0]
            val y = event.values[1]
            val z = event.values[2]

            if (lastUpdate == 0L) {
                lastUpdate = now
                lastShake = now
                lastX = x
                lastY = y
                lastZ = z
                Toast.makeText(
                    listener as? Context,
                    "No Motion detected",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                val timeDiff = now - lastUpdate
                if (timeDiff > 0) {
                    val force = abs((x + y + z - lastX - lastY - lastZ))
                    if (force > threshold) {
                        if (now - lastShake >= interval) {
                            listener?.onShake(force)
                        }
                        lastShake = now
                    }
                    lastX = x
                    lastY = y
                    lastZ = z
                    lastUpdate = now
                }
            }
            listener?.onAccelerationChanged(x, y, z)
        }
    }
}
