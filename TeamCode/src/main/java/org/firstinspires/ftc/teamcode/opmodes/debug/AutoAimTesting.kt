package org.firstinspires.ftc.teamcode.opmodes.debug

import com.acmerobotics.dashboard.config.Config
import com.pedropathing.follower.Follower
import com.pedropathing.geometry.Pose
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import org.firstinspires.ftc.teamcode.common.ActionQueue
import org.firstinspires.ftc.teamcode.common.GamepadEx
import org.firstinspires.ftc.teamcode.common.Log
import org.firstinspires.ftc.teamcode.enums.Colours
import org.firstinspires.ftc.teamcode.pedro.Constants
import org.firstinspires.ftc.teamcode.pinpoint.Pinpoint
import org.firstinspires.ftc.teamcode.subsystems.Hood
import org.firstinspires.ftc.teamcode.subsystems.Intake
import org.firstinspires.ftc.teamcode.subsystems.Joint
import org.firstinspires.ftc.teamcode.subsystems.Shooter
import org.firstinspires.ftc.teamcode.subsystems.Turret
import org.firstinspires.ftc.teamcode.subsystems.Wicket
import org.firstinspires.ftc.teamcode.subsystems.extra.Limelight

@TeleOp
@Config
class AutoAimTesting : LinearOpMode() {
    lateinit var gamepadEx1: GamepadEx
    lateinit var follower: Follower
    val actionQueue = ActionQueue()
    companion object {
        @JvmField
        var deg = 0.0
        @JvmField
        var rpm = 0.0
        @JvmField
        var intakePower = 0.0
        @JvmField
        var turretPosition = 0.0

    }

    private lateinit var log: Log
    var distance =0.0
    var achieved=false
    var rawHeading = 0.0
    var correctedHeading = 0.0
    private fun handleInputTurret()
    {
        if(Math.toDegrees(rawHeading)<182 && Math.toDegrees(rawHeading)>-118) {
            Turret.lockToTarget(follower.pose.x,follower.pose.y,rawHeading,Colours.RED,0.0)
        }
        else {
            Turret.hold()
        }
    }
    override fun runOpMode() {

        Shooter.init(hardwareMap)
        Joint.init(hardwareMap)
        Hood.init(hardwareMap)
        Intake.init(hardwareMap)
        Turret.init(hardwareMap)
        log = Log(telemetry)

        follower = Constants.createFollower(hardwareMap);
        follower.pose = Pose(
            116.0,
            120.0,
            Math.toRadians(32.0)
        )
        waitForStart()
        gamepadEx1 = GamepadEx(gamepad1)

        while (opModeIsActive()) {

            Joint.setPosition(Joint.COLLECT_POSITION)
            Hood.setPosition(deg)
            Shooter.setRPM(rpm)

            if(gamepadEx1.getButtonDown("a"))
            {
                Wicket.setPosition(Wicket.OPEN_POSITION)
                actionQueue.add(100)//if this doesn t work 1200
                {
                    Intake.setPowerMain(1.0)
                    Intake.setPowerSupport(1.0)
                    actionQueue.add(600)//lower this
                    {
                        Intake.stop()
                        Wicket.setPosition(Wicket.CLOSE_POSITION)
                    }
                }
            }

            Turret.setPosition(turretPosition) //or
            //handleInputTurret()


            if(rpm-Shooter.getRPM()<=30)
                achieved = true
            else
                achieved = false

            rawHeading = follower.pose.heading
            correctedHeading = if(rawHeading < 0)
                Math.PI + (rawHeading + Math.PI )
            else
                rawHeading

            distance = Pinpoint.distance(follower.pose.x,follower.pose.y, Colours.RED)


            log.add("Shooter Power", Shooter.getPower())
            log.add("Shooter first rpm",Shooter.getRPMfirst())
            log.add("Shooter second rpm",Shooter.getRPMsecond())
            log.add("Is velocity achieved:",achieved)
            log.add("first velo",Shooter.motorShooterFirst.velocity)
            log.add("second velo",Shooter.motorShooterSecond.velocity)
            log.add("rpm",Shooter.getRPMfirst())
            log.add("@X", follower.pose.x)
            log.add("@Y", follower.pose.y)
            log.add("@Heading", Math.toDegrees(follower.pose.heading))
            log.add("Corrected heading", Math.toDegrees(correctedHeading))
            log.add("distance to red goal",distance)
            log.tick()

            follower.update()
            actionQueue.update()
            Shooter.updateCompensatedPIDF()//TODO tune

        }

        Shooter.stop()
    }
}