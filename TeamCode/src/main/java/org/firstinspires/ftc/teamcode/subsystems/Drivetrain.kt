package org.firstinspires.ftc.teamcode.subsystems

import com.acmerobotics.dashboard.config.Config
import com.qualcomm.robotcore.hardware.DcMotor
import com.qualcomm.robotcore.hardware.DcMotor.RunMode
import com.qualcomm.robotcore.hardware.DcMotorEx
import com.qualcomm.robotcore.hardware.DcMotorSimple
import com.qualcomm.robotcore.hardware.HardwareMap
import org.firstinspires.ftc.teamcode.common.Log
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.sin

@Config

object Drivetrain {
    private var LATERAL_MULTIPLIER = 1.4

    lateinit var fl: DcMotor
    lateinit var fr: DcMotor
    lateinit var bl: DcMotor
    lateinit var br: DcMotor
    lateinit var motors: Array<DcMotor>

    const val FINAL_POSITION = -390


    fun init(hardwareMap: HardwareMap) {
        fl = hardwareMap.get(DcMotor::class.java, "leftFront")
        fr = hardwareMap.get(DcMotor::class.java, "rightFront")
        bl = hardwareMap.get(DcMotor::class.java, "leftRear")
        br = hardwareMap.get(DcMotor::class.java, "rightRear")

        motors = arrayOf(fl, fr, bl, br)

        fl.direction = DcMotorSimple.Direction.REVERSE
        bl.direction = DcMotorSimple.Direction.REVERSE
        br.direction = DcMotorSimple.Direction.FORWARD
        fr.direction = DcMotorSimple.Direction.FORWARD

        motors.forEach {  motor ->
            motor.mode = RunMode.RUN_WITHOUT_ENCODER
            motor.zeroPowerBehavior = DcMotor.ZeroPowerBehavior.BRAKE

            motor.motorType.clone().apply {
                achieveableMaxRPMFraction = 1.0
                motor.motorType = this
            }
        }
    }

    fun initAuto(hardwareMap: HardwareMap) {
        LATERAL_MULTIPLIER = 1.1
        fl = hardwareMap.get<DcMotorEx>(DcMotorEx::class.java, "leftFront")
        bl = hardwareMap.get<DcMotorEx>(DcMotorEx::class.java, "leftRear")
        br = hardwareMap.get<DcMotorEx>(DcMotorEx::class.java, "rightRear")
        fr = hardwareMap.get<DcMotorEx>(DcMotorEx::class.java, "rightFront")

        motors = arrayOf(fl, fr, bl, br)


        fl.direction = DcMotorSimple.Direction.REVERSE
        bl.direction = DcMotorSimple.Direction.REVERSE
        br.direction = DcMotorSimple.Direction.FORWARD
        fr.direction = DcMotorSimple.Direction.FORWARD

        motors.forEach {  motor ->
            motor.mode = RunMode.RUN_WITHOUT_ENCODER
            motor.zeroPowerBehavior = DcMotor.ZeroPowerBehavior.BRAKE

            motor.motorType.clone().apply {
                achieveableMaxRPMFraction = 1.0
                motor.motorType = this
            }
        }
    }

    fun setRunMode(runMode: RunMode?) {
        motors.forEach {
            it.mode = runMode
        }
    }

    fun driveMecanum(inAxial: Double, inLateral: Double, inYaw: Double, maxOutput: Double) {
        val modMaintainMotorRatio: Double

        val inputAxial = (inAxial * maxOutput)
        val inputLateral = (inLateral * maxOutput) * LATERAL_MULTIPLIER // TODO: tune
        Log.instance.add("inLateral", inLateral)
        Log.instance.add("inputLateral", inputLateral)
        val inputYaw = (inYaw * maxOutput)

        modMaintainMotorRatio = max(abs(inputAxial) + abs(inputLateral) + abs(inputYaw), 1.0)

        val leftFrontPower = (inputAxial + inputLateral + inputYaw) / modMaintainMotorRatio
        val rightFrontPower = (inputAxial - inputLateral - inputYaw) / modMaintainMotorRatio
        val leftBackPower = (inputAxial - inputLateral + inputYaw) / modMaintainMotorRatio
        val rightBackPower = (inputAxial + inputLateral - inputYaw) / modMaintainMotorRatio

        Log.instance.let {
            it.add("leftFrontPower", leftFrontPower.toString())
            it.add("rightFrontPower", rightFrontPower.toString())
            it.add("leftBackPower", leftBackPower.toString())
            it.add("rightBackPower", rightBackPower.toString())
        }

        setDriveMotorPower(leftFrontPower, rightFrontPower, leftBackPower, rightBackPower)
    }

    fun stop() {
        setDriveMotorPower(0.0, 0.0, 0.0, 0.0)
    }

    fun setDriveMotorPower(
        leftFrontPower: Double,
        rightFrontPower: Double,
        leftBackPower: Double,
        rightBackPower: Double
    ) {
        fl.power = leftFrontPower
        fr.power = rightFrontPower
        bl.power = leftBackPower
        br.power = rightBackPower
    }

    fun resetEncoders()
    {
        motors.forEach {
            it.mode = RunMode.STOP_AND_RESET_ENCODER
            it.targetPosition = 0
            it.mode = RunMode.RUN_TO_POSITION
        }
    }
    fun getCurrentPosition(): Int {
        return fl.currentPosition
    }

}