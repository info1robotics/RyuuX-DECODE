package org.firstinspires.ftc.teamcode.subsystems

import com.pedropathing.math.MathFunctions
import com.qualcomm.robotcore.hardware.HardwareMap
import com.qualcomm.robotcore.hardware.PwmControl.PwmRange
import com.qualcomm.robotcore.hardware.ServoImplEx
import kotlin.math.pow

object Hood {
    //TODO reverse 53 and 33

    var offset = 0.0
    var HIGHER_LIMIT = 0.85 //~33 degrees
    var LOWER_LIMIT = 0.00  //~53 degrees   0.03

    var FAR_DEGREE = 40.0

    private lateinit var servoHood: ServoImplEx

    fun init(hardwareMap: HardwareMap) {
        servoHood = hardwareMap.get(ServoImplEx::class.java, "servoHood")
        servoHood.pwmRange = PwmRange(500.0, 2500.0)
    }

    fun setPosition(position: Double) {
        servoHood.position = position.coerceIn(LOWER_LIMIT, HIGHER_LIMIT)
    }

    fun setPositionDeg(degrees: Double) {
        val minDeg = 33.0   // corresponds to HIGHER_LIMIT
        val maxDeg = 53.0   // corresponds to LOWER_LIMIT

        val clampedDeg = degrees.coerceIn(minDeg, maxDeg)

        // Map degrees to servo position: 33° -> HIGHER_LIMIT, 53° -> LOWER_LIMIT
        val position =
            HIGHER_LIMIT - (clampedDeg - minDeg) * (HIGHER_LIMIT - LOWER_LIMIT) / (maxDeg - minDeg)

        servoHood.position = position.coerceIn(LOWER_LIMIT, HIGHER_LIMIT)
    }

    fun getPosition(): Double {
        return servoHood.position
    }


    fun calculate(distance: Double): Double {
        val y = 1.29798 +
                (-0.09011866 - 1.29798) /
                (1 + (distance / 268.5674).pow(1.731773))

        return MathFunctions.clamp(y, 0.0, 1.0)
    }

}