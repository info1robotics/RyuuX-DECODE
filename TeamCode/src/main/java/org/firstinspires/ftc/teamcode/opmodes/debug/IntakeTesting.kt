package org.firstinspires.ftc.teamcode.opmodes.test

import com.acmerobotics.dashboard.config.Config
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import org.firstinspires.ftc.teamcode.common.Log
import org.firstinspires.ftc.teamcode.subsystems.Intake
import org.firstinspires.ftc.teamcode.subsystems.Jack
import org.firstinspires.ftc.teamcode.subsystems.Joint
import org.firstinspires.ftc.teamcode.subsystems.Shooter
import org.firstinspires.ftc.teamcode.subsystems.Turret
import org.firstinspires.ftc.teamcode.subsystems.extra.Limelight

@TeleOp
@Config
class IntakeTesting : LinearOpMode() {
    companion object {
        @JvmField
        var mainPower = 0.0

        @JvmField
        var supportPower = 0.0

        @JvmField
        var position = Joint.COLLECT_POSITION

    }
    lateinit var log: Log
    override fun runOpMode() {
        Intake.init(hardwareMap)
        Joint.init(hardwareMap)
        log = Log(this.telemetry)
        waitForStart()

        while (opModeIsActive()) {
            Intake.setPowerMain(mainPower)
            Intake.setPowerSupport(supportPower)
            Joint.setPosition(position)


            log.add("Intake Power",Intake.motorIntakeMain.power)

            //log.add("Sensor colours", Intake.getColorReading())
            log.add("Is full",Intake.isFull())
            log.add("Is empty top",Intake.isEmptyTop())
            log.tick()
        }
    }
}