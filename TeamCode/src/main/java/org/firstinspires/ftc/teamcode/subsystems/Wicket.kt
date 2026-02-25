package org.firstinspires.ftc.teamcode.subsystems

import com.qualcomm.robotcore.hardware.HardwareMap
import com.qualcomm.robotcore.hardware.PwmControl.PwmRange
import com.qualcomm.robotcore.hardware.Servo
import com.qualcomm.robotcore.hardware.ServoImplEx

object Wicket {

    var OPEN_POSITION = 0.3
    var CLOSE_POSITION = 0.62

    private lateinit var servoWicket: ServoImplEx


    fun init(hardwareMap: HardwareMap) {
        servoWicket = hardwareMap.get(ServoImplEx::class.java, "servoWicket")
        servoWicket.direction = Servo.Direction.REVERSE
        servoWicket.pwmRange = PwmRange(500.0, 2500.0)
    }

    fun setPosition(position: Double) {
        servoWicket.position = position
    }

    fun getPosition(): Double {
        return servoWicket.position
    }


}