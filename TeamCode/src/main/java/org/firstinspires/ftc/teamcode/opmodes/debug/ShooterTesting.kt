package org.firstinspires.ftc.teamcode.opmodes.debug

import com.acmerobotics.dashboard.config.Config
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import com.qualcomm.robotcore.hardware.PIDFCoefficients
import org.firstinspires.ftc.teamcode.common.Log
import org.firstinspires.ftc.teamcode.subsystems.Hood
import org.firstinspires.ftc.teamcode.subsystems.Intake
import org.firstinspires.ftc.teamcode.subsystems.Shooter

@TeleOp(group = "Debug")
@Config
class ShooterTesting : LinearOpMode() {
    companion object {
        @JvmField
        var rpm = 0.0 // Set this in FTC Dashboard
        @JvmField
        var velo=0.0
        @JvmField
        var position =0.0
        @JvmField
        var powerIntake =0.0
    }

    private lateinit var log: Log

    override fun runOpMode() {
        Shooter.init(hardwareMap)
        Hood.init(hardwareMap)
        Intake.init(hardwareMap)
        log = Log(telemetry)

        waitForStart()

        while (opModeIsActive()) {
            Intake.setPower(powerIntake)
            Hood.setPosition(position)

            if(rpm != 0.0)
                Shooter.setRPM(rpm)
            else
                Shooter.setVelocity(velo)

            var error = velo - Shooter.getVelocity()
            var errorRPM = rpm - Shooter.getRPMfirst()
            val currentRPM = Shooter.getRPM()


            log.add("Currect velocity",Shooter.getVelocity())

            log.add("Target RPM", rpm)
            log.add("Current RPM", currentRPM)
            log.add("Shooter Power", Shooter.getPower())
            log.add("Shooter Second",Shooter.getRPMsecond())
            log.add("Shooter First",Shooter.getRPMfirst())
            log.add("error velo",error)
            log.add("error rpm",errorRPM)
            log.tick()
        }

        Shooter.stop()
    }
}