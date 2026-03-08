package org.firstinspires.ftc.teamcode.pinpoint

import com.qualcomm.robotcore.hardware.HardwareMap
import org.firstinspires.ftc.teamcode.enums.Colours
import kotlin.math.pow
import kotlin.math.sqrt

object Pinpoint {
    const val RED_GOAL_X = 122.5//144 on pedro 117 previously
    const val RED_GOAL_Y = 128.0//144
    const val BLUE_GOAL_X = 22.5//0
    const val BLUE_GOAL_Y = 128.0//144
    const val cm = 2.54
    const val offset = 0.0//TODO change incase smth
    var comp =0.0
    var converterNegative =-500.0
    var converterPositiv = 1000.0

    fun init(hardwareMap: HardwareMap) {}

    fun distance(currentX: Double, currentY: Double, colour: Colours): Double {

        val (goalX, goalY) = when (colour) {
            Colours.RED -> RED_GOAL_X to RED_GOAL_Y
            Colours.BLUE -> BLUE_GOAL_X to BLUE_GOAL_Y

        }

        return cm * sqrt(
            (goalX - currentX).pow(2) +
                    (goalY - currentY).pow(2)
        )- offset
    }
}