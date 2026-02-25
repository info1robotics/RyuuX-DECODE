package org.firstinspires.ftc.teamcode.subsystems

import com.qualcomm.robotcore.hardware.HardwareMap
import com.qualcomm.robotcore.hardware.PwmControl.PwmRange
import com.qualcomm.robotcore.hardware.Servo
import com.qualcomm.robotcore.hardware.ServoImplEx

object Jack {

    var offset = -0.00
    var INIT_POSITION = 0.15
    var PARKED_POSITION = 0.8
    var STEP = 0.05
    private lateinit var servoJackFirst: ServoImplEx
    private lateinit var servoJackSecond: ServoImplEx

    fun init(hardwareMap: HardwareMap) {
        servoJackFirst = hardwareMap.get(ServoImplEx::class.java, "servoJackLeft")
        servoJackSecond = hardwareMap.get(ServoImplEx::class.java, "servoJackRight")
        servoJackSecond.direction = Servo.Direction.REVERSE
        servoJackFirst.pwmRange = PwmRange(500.0, 2500.0)
        servoJackSecond.pwmRange = PwmRange(500.0, 2500.0)
        servoJackFirst.position = INIT_POSITION
        servoJackSecond.position = INIT_POSITION
    }

    fun setPosition(position: Double) {
        servoJackFirst.position = position
        servoJackSecond.position = (position + offset)
    }

    fun getPosition(): Double {
        return servoJackFirst.position
    }
    fun getOffsetedPosition():Double
    {
        return  servoJackSecond.position
    }


}