package com.example.womenssafetyapplication

interface AccelerometerListener {
    fun onAccelerationChanged(x: Float, y: Float, z: Float)
    fun onShake(force: Float)
    fun onEmergencyDetected()
}