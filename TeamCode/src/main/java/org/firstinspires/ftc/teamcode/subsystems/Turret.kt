package org.firstinspires.ftc.teamcode.subsystems

import com.qualcomm.robotcore.hardware.HardwareMap
import com.qualcomm.robotcore.hardware.PwmControl.PwmRange
import com.qualcomm.robotcore.hardware.Servo
import com.qualcomm.robotcore.hardware.ServoImplEx
import org.firstinspires.ftc.teamcode.common.ActionQueue
import org.firstinspires.ftc.teamcode.common.PidController
import org.firstinspires.ftc.teamcode.enums.Colours
import org.firstinspires.ftc.teamcode.pinpoint.Pinpoint
import kotlin.math.PI
import kotlin.math.atan2

object Turret {
    var HIGHER_LIMIT = 0.972//155 turning right
    var LOWER_LIMIT = 0.028//155 turing left  0.9
    var FORWARD_POSITION = 0.495//0 degrees 0.7
    var offset = 0.038

    private lateinit var servoTurretRight: ServoImplEx//axon max mk2 gear ratio 24-50
    private lateinit var servoTurretLeft: ServoImplEx

    fun init(hardwareMap: HardwareMap) {
        servoTurretRight = hardwareMap.get(ServoImplEx::class.java, "servoTurretRight")
        servoTurretLeft = hardwareMap.get(ServoImplEx::class.java, "servoTurretLeft")

        servoTurretRight.direction = Servo.Direction.REVERSE
        servoTurretLeft.direction = Servo.Direction.REVERSE

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
            (FORWARD_POSITION - (turretAngle / MAX_TURRET_ANGLE) * 0.402  + offset)

        setPosition(servoPosition)
    }

    var direction = 1.0

    //VELO COMPENSATION
    fun lockToTarget(
        robotX: Double,
        robotY: Double,
        robotHeading: Double,
        allianceColour: Colours,
        velocityX: Double,   // robot-centric X (forward)
        velocityY: Double,   // robot-centric Y (left)
        offset: Double
    ) {

        // ✅ field target positions
        if (allianceColour == Colours.BLUE) {
            targetX = 14.0
            targetY = 130.0
        } else {
            targetX = 130.0
            targetY = 130.0
        }

        // 🔥 convert robot-centric velocity → field-centric
        val fieldVx = velocityX * kotlin.math.cos(robotHeading) - velocityY * kotlin.math.sin(robotHeading)
        val fieldVy = velocityX * kotlin.math.sin(robotHeading) + velocityY * kotlin.math.cos(robotHeading)

        // 🔥 lookahead time (TUNE THIS)
        val lookaheadTime = 0.25//0.36

        val compensationSign = if (allianceColour == Colours.BLUE) -1.0 else 1.0

        val dx = targetX - robotX - compensationSign * fieldVx * lookaheadTime
        val dy = targetY - robotY - compensationSign * fieldVy * lookaheadTime

        // angle to target (field frame)
        val targetAngleField = atan2(dy, dx)

        // convert to robot frame
        var turretAngle = targetAngleField - robotHeading

        // normalize angle to [-PI, PI]
        while (turretAngle > PI) turretAngle -= 2 * PI
        while (turretAngle < -PI) turretAngle += 2 * PI

        // clamp turret range
        turretAngle = turretAngle.coerceIn(-MAX_TURRET_ANGLE, MAX_TURRET_ANGLE)

        // map angle → servo position
        val servoPosition =
            FORWARD_POSITION - (turretAngle / MAX_TURRET_ANGLE) * 0.402 + offset

        setPosition(servoPosition.coerceIn(LOWER_LIMIT, HIGHER_LIMIT))
    }

}