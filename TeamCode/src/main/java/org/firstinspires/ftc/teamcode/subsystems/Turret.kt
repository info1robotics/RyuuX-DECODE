package org.firstinspires.ftc.teamcode.subsystems

import com.qualcomm.robotcore.hardware.HardwareMap
import com.qualcomm.robotcore.hardware.PwmControl.PwmRange
import com.qualcomm.robotcore.hardware.ServoImplEx
import org.firstinspires.ftc.teamcode.common.PidController
import org.firstinspires.ftc.teamcode.enums.Colours
import org.firstinspires.ftc.teamcode.pinpoint.Pinpoint
import kotlin.math.PI
import kotlin.math.atan2

object Turret {
    var HIGHER_LIMIT = 0.856//135 turning right
    var LOWER_LIMIT = 0.088//135 turing left  0.9
    var FORWARD_POSITION = 0.462//0 degrees 0.462

    private lateinit var servoTurretRight: ServoImplEx//axon max mk2 gear ratio 24-50
    private lateinit var servoTurretLeft: ServoImplEx

    fun init(hardwareMap: HardwareMap) {
        servoTurretRight = hardwareMap.get(ServoImplEx::class.java, "servoTurretRight")
        servoTurretLeft = hardwareMap.get(ServoImplEx::class.java, "servoTurretLeft")

        servoTurretRight.pwmRange = PwmRange(500.0, 2500.0)
        servoTurretLeft.pwmRange = PwmRange(500.0, 2500.0)

    }

    fun setPosition(position: Double) {
        servoTurretRight.position = position.coerceIn(LOWER_LIMIT, HIGHER_LIMIT)
        servoTurretLeft.position = position.coerceIn(LOWER_LIMIT, HIGHER_LIMIT)
    }

    fun getPosition(): Double {
        return servoTurretRight.position
    }
    fun hold()
    {
        setPosition(getPosition())
    }

    var targetX = 0.0
    var targetY = 0.0

    private val MAX_TURRET_ANGLE = Math.toRadians(135.0)

    fun lockToTarget(
        robotX: Double,
        robotY: Double,
        robotHeading: Double,
        allianceColour: Colours,
        offset:Double
    ) {

        // ✅ use real field goal positions
        if (allianceColour == Colours.BLUE) {
            targetX = 14.0
            targetY = 130.0
        } else {
            targetX = 130.0
            targetY =130.0
        }

        val dx = targetX - robotX
        val dy = targetY - robotY

        // angle to target in FIELD frame
        val targetAngleField = atan2(dy, dx)

        // convert to ROBOT frame
        var turretAngle = targetAngleField - robotHeading

        // ✅ proper normalization
        while (turretAngle > PI) turretAngle -= 2 * PI
        while (turretAngle < -PI) turretAngle += 2 * PI

        // clamp turret motion
        turretAngle = turretAngle.coerceIn(-MAX_TURRET_ANGLE, MAX_TURRET_ANGLE)

        // map angle → servo
        val servoPosition =
            FORWARD_POSITION -
                    (turretAngle / MAX_TURRET_ANGLE) * 0.396
        +
                    offset

        setPosition(servoPosition)
    }


}