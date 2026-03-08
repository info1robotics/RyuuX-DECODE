package org.firstinspires.ftc.teamcode.subsystems

import com.qualcomm.robotcore.hardware.DcMotor
import com.qualcomm.robotcore.hardware.DcMotorSimple
import com.qualcomm.robotcore.hardware.DigitalChannel
import com.qualcomm.robotcore.hardware.HardwareMap

object Intake {

    lateinit var motorIntakeMain: DcMotor
    lateinit var motorIntakeSupport: DcMotor
    private lateinit var breakBeamBottom: DigitalChannel
    private lateinit var breakBeamMid: DigitalChannel
    private lateinit var breakBeamTop: DigitalChannel

    fun init(hardwareMap: HardwareMap) {
        motorIntakeMain = hardwareMap.get(DcMotor::class.java, "motorIntakeMain")
        motorIntakeMain = hardwareMap.get(DcMotor::class.java, "motorIntakeMain")
        motorIntakeSupport = hardwareMap.get(DcMotor::class.java, "motorIntakeSupport")

        breakBeamBottom = hardwareMap.get(DigitalChannel::class.java, "sensorIntakeBottom")
        breakBeamBottom.mode = DigitalChannel.Mode.INPUT

        breakBeamMid = hardwareMap.get(DigitalChannel::class.java, "sensorIntakeMid")
        breakBeamMid.mode = DigitalChannel.Mode.INPUT

        breakBeamTop = hardwareMap.get(DigitalChannel::class.java, "sensorIntakeTop")
        breakBeamTop.mode = DigitalChannel.Mode.INPUT

        motorIntakeMain.direction = DcMotorSimple.Direction.FORWARD
        motorIntakeSupport.direction = DcMotorSimple.Direction.REVERSE

        motorIntakeMain.zeroPowerBehavior = DcMotor.ZeroPowerBehavior.BRAKE
        motorIntakeSupport.zeroPowerBehavior = DcMotor.ZeroPowerBehavior.BRAKE

        val motorConfigurationType = motorIntakeMain.motorType.clone()
        motorConfigurationType.achieveableMaxRPMFraction = 1.0

        motorIntakeMain.motorType = motorConfigurationType
        motorIntakeSupport.motorType = motorConfigurationType
    }

    fun setPower(power: Double) {
        motorIntakeMain.power = power
        motorIntakeSupport.power = power
    }

    fun setPowerMain(power: Double) {
        motorIntakeMain.power = power
    }

    fun setPowerSupport(power: Double) {
        motorIntakeSupport.power = power
    }

    fun getPowerMain(): Double {
        return motorIntakeMain.power
    }

    fun stop() {
        motorIntakeMain.power = 0.0
        motorIntakeSupport.power = 0.0
    }

    fun reverse() {
        motorIntakeMain.power = -1.0
        motorIntakeSupport.power = -1.0
    }

    fun take() {
        motorIntakeMain.power = 1.0
        motorIntakeSupport.power = 1.0
    }

    fun takeMain() {
        motorIntakeMain.power = 1.0
    }

    fun takeSupport() {
        motorIntakeSupport.power = 1.0
    }

    /**
     * @return true if intake is empty (beam NOT broken)
     */
    fun isEmptyBottom(): Boolean {
        return breakBeamBottom.state
    }

    fun isEmptyMid():Boolean{
        return breakBeamMid.state
    }

    fun isEmptyTop():Boolean{
        return breakBeamTop.state
    }


    /**
     * @return true if object is detected (beam broken)
     */
    fun isFull(): Boolean {
        return !isEmptyTop() && !isEmptyBottom() && !isEmptyMid()
    }

    fun isUpper(): Boolean {
        return !isEmptyTop() && !isEmptyMid()
    }
    fun isLower(): Boolean {
        return !isEmptyMid() && !isEmptyBottom()
    }
}