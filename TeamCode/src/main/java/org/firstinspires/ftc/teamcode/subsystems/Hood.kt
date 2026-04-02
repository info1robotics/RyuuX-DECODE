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
    var distanceOffset= 2.0

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
        var y =0.0
        var convertedDistance = distance-distanceOffset
        if(distance<=195)
        {
            y = 0.4246272 + (-0.01968272 - 0.4246272)/(1 + (distance/135.4403).pow(5.437693))
        }
        else if(distance >195 && distance<240)
        {
            y = 0.6514307 + (-0.04297386 - 0.6514307)/(1 + (distance/153.5256).pow(2.812665))
        }
        else
        {
            y  = 0.6003789 + (-81110.65 - 0.6003789)/(1 + (distance/103.3978).pow(13.25975))
        }


        return MathFunctions.clamp(y, 0.0, 1.0)
    }

}