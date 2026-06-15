package org.firstinspires.ftc.teamcode.subsystems

import com.qualcomm.robotcore.hardware.HardwareMap
import com.qualcomm.robotcore.hardware.PwmControl.PwmRange
import com.qualcomm.robotcore.hardware.Servo
import com.qualcomm.robotcore.hardware.ServoImplEx

object  Joint {

    var COLLECT_POSITION = 0.18
    var FAR_POSITION = COLLECT_POSITION
    var SAFE_POSITION = COLLECT_POSITION+0.22
    var INIT_POSITION = COLLECT_POSITION
    var LOWER_LIMIT = 0.0//should be the asme as collect position but lower it to calibrate

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
        servoJointRight.position = position.coerceIn(LOWER_LIMIT, 1.0)
        servoJointLeft.position = position.coerceIn(LOWER_LIMIT, 1.0)
    }

    fun getPosition(): Double {
        return servoJointLeft.position
    }


}