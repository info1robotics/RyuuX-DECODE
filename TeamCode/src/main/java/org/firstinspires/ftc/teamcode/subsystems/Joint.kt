package org.firstinspires.ftc.teamcode.subsystems

import com.qualcomm.robotcore.hardware.HardwareMap
import com.qualcomm.robotcore.hardware.PwmControl.PwmRange
import com.qualcomm.robotcore.hardware.Servo
import com.qualcomm.robotcore.hardware.ServoImplEx

object  Joint {

    var COLLECT_POSITION = 0.433
    var INIT_POSITION = 0.233

    private lateinit var servoJointLeft: ServoImplEx
    private lateinit var servoJointRight: ServoImplEx

    fun init(hardwareMap: HardwareMap) {
        servoJointLeft = hardwareMap.get(ServoImplEx::class.java, "servoJointLeft")
        servoJointRight = hardwareMap.get(ServoImplEx::class.java, "servoJointRight")
        servoJointLeft.direction = Servo.Direction.REVERSE
        servoJointLeft.pwmRange = PwmRange(500.0, 2500.0)
        servoJointRight.pwmRange = PwmRange(500.0,2500.0)
    }

    fun setPosition(position: Double) {
        servoJointRight.position = position
        servoJointLeft.position = position
    }

    fun getPosition(): Double {
        return servoJointLeft.position
    }


}