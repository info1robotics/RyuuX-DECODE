package org.firstinspires.ftc.teamcode.subsystems

import com.pedropathing.math.MathFunctions
import com.qualcomm.robotcore.hardware.DcMotor
import com.qualcomm.robotcore.hardware.DcMotorEx
import com.qualcomm.robotcore.hardware.DcMotorSimple
import com.qualcomm.robotcore.hardware.HardwareMap
import com.qualcomm.robotcore.hardware.PIDFCoefficients
import com.qualcomm.robotcore.hardware.VoltageSensor
import org.firstinspires.ftc.robotcore.external.navigation.CurrentUnit
import org.firstinspires.ftc.teamcode.enums.Colours
import org.firstinspires.ftc.teamcode.pinpoint.Pinpoint
import kotlin.math.pow
import kotlin.math.sqrt

object Shooter {
     lateinit var motorShooterFirst: DcMotorEx // 6000 RPM goBILDA (≈8400 RPM after 1.4x gear)
     lateinit var motorShooterSecond: DcMotorEx
    private lateinit var voltageSensor: VoltageSensor

    private const val MOTOR_TICKS_PER_REV = 28
    const val MAX_RPM = 4400.0//
    const val MAX_VELOCITY = 1700.0//overshoots
    const val FAR_POWER=1400
    const val SUPER_CYCLE_POWER = 3075.0
    val offsetClose = 235.0// TODO tune, general use, affects all opmode, can be changed locally on other classes
    val offsetFar = 165.0
    val distanceOffset = 2.0
    var close = true

    private val BASE_PIDF = PIDFCoefficients(155.0, 0.0, 5.0, 11.7)// p = 155  d = 5.0 f = 11.7
    fun init(hardwareMap: HardwareMap) {
        motorShooterFirst = hardwareMap.get(DcMotorEx::class.java, "motorShooterFirst")
        motorShooterSecond = hardwareMap.get(DcMotorEx::class.java, "motorShooterSecond")

        motorShooterFirst.direction = DcMotorSimple.Direction.FORWARD
        motorShooterSecond.direction = DcMotorSimple.Direction.FORWARD

        motorShooterFirst.zeroPowerBehavior = DcMotor.ZeroPowerBehavior.FLOAT
        motorShooterFirst.zeroPowerBehavior = DcMotor.ZeroPowerBehavior.FLOAT


        motorShooterFirst.mode = DcMotor.RunMode.RUN_USING_ENCODER
        motorShooterSecond.mode = DcMotor.RunMode.RUN_USING_ENCODER

        val config = motorShooterFirst.motorType.clone()
        config.achieveableMaxRPMFraction = 1.0

        motorShooterFirst.motorType = config
        motorShooterSecond.motorType = config

        voltageSensor = hardwareMap.voltageSensor.iterator().next()

        applyPIDFCoefficients(BASE_PIDF)
    }

    fun applyPIDFCoefficients(base: PIDFCoefficients) {
        val base = PIDFCoefficients(base.p, base.i, base.d, base.f)
        motorShooterFirst.setVelocityPIDFCoefficients(
            base.p,
            base.i,
            base.d,
            base.f
        )
        motorShooterSecond.setVelocityPIDFCoefficients(
            base.p,
            base.i,
            base.d,
            base.f
        )
    }

    fun setVelocity(velo:Double)
    {
        motorShooterFirst.velocity = velo
        motorShooterSecond.velocity = velo
    }
    fun getVelocity(): Double {
     return motorShooterFirst.velocity
    }

    fun setRPM(rpm: Double) {
        val targetVelocityTicksPerSec = (rpm * MOTOR_TICKS_PER_REV) / 60.0
        motorShooterFirst.velocity = targetVelocityTicksPerSec
        motorShooterSecond.velocity = targetVelocityTicksPerSec

    }

    fun getRPM(): Double {
        val ticksPerSec = motorShooterFirst.velocity
        return (ticksPerSec / MOTOR_TICKS_PER_REV) * 60.0
    }
    fun getRPMfirst(): Double {
        val ticksPerSec = motorShooterFirst.velocity
        return (ticksPerSec / MOTOR_TICKS_PER_REV) * 60.0
    }
    fun getRPMsecond(): Double {
        val ticksPerSec = motorShooterSecond.velocity
        return (ticksPerSec / MOTOR_TICKS_PER_REV) * 60.0
    }


    fun stop() {
        motorShooterFirst.power = 0.0
        motorShooterSecond.power = 0.0
    }
    fun getCurrentDraw(): Double =
        motorShooterFirst.getCurrent(CurrentUnit.AMPS) + motorShooterSecond.getCurrent(CurrentUnit.AMPS)

    fun getAverageCurrent(): Double =
        (motorShooterFirst.getCurrent(CurrentUnit.AMPS) + motorShooterSecond.getCurrent(CurrentUnit.AMPS)) / 2.0

    fun setPower(power: Double) {

        motorShooterFirst.power = power
        motorShooterSecond.power = power
    }
    fun getPower(): Double {
        return motorShooterFirst.power
    }

    fun calculate(distance: Double): Double {
        var value =0.0
        var convertedDistance = distance - distanceOffset
        value = if(distance<=195) {
            205652000 +
                    (2510.913 - 205652000) /
                    (1 + (convertedDistance / 17692.07).pow(2.781764)) + offsetClose
        }
        else if(distance >195 && distance<=240)
        {
            3864.831 +
                    (2510.995 - 3864.831) /
                    (1 + (convertedDistance / 189.8164).pow(3.407866)) + offsetClose
        }
            else {
            4518.389 + (3726.74 - 4518.389)/(1 + (convertedDistance/349.4118).pow(12.73016)) + offsetFar
        }


        return MathFunctions.clamp(value, 0.0, MAX_RPM)
    }
    var velocityTowardsGoal = 0.0
    fun calculate(
        distance: Double,
        velocityX: Double,   // robot-centric (forward)
        velocityY: Double,   // robot-centric (strafe)
        heading: Double,     // radians
        currentX: Double,
        currentY: Double,
        colour: Colours
    ): Double {

        // 🔴 Get goal position
        val (goalX, goalY) = when (colour) {
            Colours.RED -> Pinpoint.RED_GOAL_X to Pinpoint.RED_GOAL_Y
            Colours.BLUE -> Pinpoint.BLUE_GOAL_X to Pinpoint.BLUE_GOAL_Y
        }

        // 🧭 Convert robot-centric velocity → field-centric
        val cos = kotlin.math.cos(heading)
        val sin = kotlin.math.sin(heading)

        val fieldVX = velocityX * cos - velocityY * sin
        val fieldVY = velocityX * sin + velocityY * cos

        // 🎯 Direction to goal
        val dx = goalX - currentX
        val dy = goalY - currentY
        val dist = kotlin.math.sqrt(dx * dx + dy * dy)

        val ux = dx / dist
        val uy = dy / dist

        // 🚀 Velocity toward goal (dot product)
        val compensationSign = if (colour == Colours.BLUE) -1.0 else 1.0
        velocityTowardsGoal = (fieldVX * ux + fieldVY * uy)*compensationSign

        // 🔥 TUNE THIS CONSTANT
        val k = 1.25

        val velocityCompensation = velocityTowardsGoal * k

        // 📈 Base RPM (your original curve)
        val convertedDistance = distance - distanceOffset

        val baseRPM = when {
            distance <= 195 -> {
                205652000 +
                        (2510.913 - 205652000) /
                        (1 + (convertedDistance / 17692.07).pow(2.781764)) + offsetClose
            }
            distance <= 240 -> {
                3864.831 +
                        (2510.995 - 3864.831) /
                        (1 + (convertedDistance / 189.8164).pow(3.407866)) + offsetClose
            }
            else -> {
                4518.389 +
                        (3726.74 - 4518.389) /
                        (1 + (convertedDistance / 349.4118).pow(12.73016)) + offsetFar
            }
        }

        // 🎯 Apply compensation
        val finalRPM = baseRPM - velocityCompensation

        return MathFunctions.clamp(finalRPM, 0.0, MAX_RPM)
    }
    fun updateCompensatedPIDF()
    {
        val voltage = voltageSensor.voltage
        val compensatedF = BASE_PIDF.f * (12.0 / voltage)
        val compensated = PIDFCoefficients(BASE_PIDF.p, BASE_PIDF.i, BASE_PIDF.d, compensatedF)
        motorShooterFirst.setVelocityPIDFCoefficients(
            compensated.p,
            compensated.i,
            compensated.d,
            compensated.f
        )
        motorShooterSecond.setVelocityPIDFCoefficients(
            compensated.p,
            compensated.i,
            compensated.d,
            compensated.f
        )
    }

    fun charge(power:Double)
    {
        setRPM(power.coerceIn(0.0, SUPER_CYCLE_POWER))
    }
    fun charge()
    {
        setRPM(2800.0)
    }

}