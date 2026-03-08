package org.firstinspires.ftc.teamcode.subsystems

import com.pedropathing.math.MathFunctions
import com.qualcomm.robotcore.hardware.DcMotor
import com.qualcomm.robotcore.hardware.DcMotorEx
import com.qualcomm.robotcore.hardware.DcMotorSimple
import com.qualcomm.robotcore.hardware.HardwareMap
import com.qualcomm.robotcore.hardware.PIDFCoefficients
import com.qualcomm.robotcore.hardware.VoltageSensor
import org.firstinspires.ftc.robotcore.external.navigation.CurrentUnit
import kotlin.math.pow

object Shooter {
     lateinit var motorShooterFirst: DcMotorEx // 6000 RPM goBILDA (≈8400 RPM after 1.4x gear)
     lateinit var motorShooterSecond: DcMotorEx
    private lateinit var voltageSensor: VoltageSensor

    private const val MOTOR_TICKS_PER_REV = 28
    const val MAX_RPM = 3600.0//
    const val MAX_VELOCITY = 1700.0//overshoots
    const val FAR_POWER=1400
    const val SUPER_CYCLE_POWER = 3175.0
    val offset = 105.0// offset the function for more power


    private val BASE_PIDF = PIDFCoefficients(190.0, 0.0, 0.0, 14.9) // Base feedforward at 12V
    //default is 190, 14.9
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
        val compensated = PIDFCoefficients(base.p, base.i, base.d, base.f)
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
        val value = 3845.331 +
                (2519.676 - 3845.331) /
                (1 + (distance / 187.4814).pow(3.621187)) + offset

        return MathFunctions.clamp(value, 0.0, MAX_RPM)
    }

    fun charge(power:Double)
    {
        setRPM(power.coerceIn(0.0, SUPER_CYCLE_POWER))
    }
    fun charge()
    {
        setRPM(3000.0 + offset)
    }

}