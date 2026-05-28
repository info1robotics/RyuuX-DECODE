package org.firstinspires.ftc.teamcode.opmodes.debug

import com.acmerobotics.dashboard.config.Config
import com.pedropathing.follower.Follower
import com.pedropathing.geometry.Pose
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import org.firstinspires.ftc.teamcode.common.Log
import org.firstinspires.ftc.teamcode.enums.Colours
import org.firstinspires.ftc.teamcode.pedro.Constants
import org.firstinspires.ftc.teamcode.pinpoint.Pinpoint
import org.firstinspires.ftc.teamcode.subsystems.Drivetrain
import org.firstinspires.ftc.teamcode.subsystems.Turret
import org.firstinspires.ftc.teamcode.subsystems.extra.Limelight
@Config
@TeleOp
class TurretTesting : LinearOpMode() {
    lateinit var follower: Follower
    companion object {
        @JvmField
        var position = 0.5

    }
    lateinit var log: Log
    override fun runOpMode() {

        Turret.init(hardwareMap)
        Drivetrain.init(hardwareMap)
        follower = Constants.createFollower(hardwareMap)
        follower.pose = Pose(120.0,123.0,32.0)
        //Limelight.init(hardwareMap,0)

        //Limelight.start()
        log = Log(this.telemetry)
        waitForStart()

        while (opModeIsActive()) {
            //var tx = Limelight.getTx()
            Turret.setPosition(position)
            log.add("@X", follower.pose.x)
            log.add("@Y", follower.pose.y)
            log.add("@Heading", Math.toDegrees(follower.pose.heading))
            log.add("Turret position",Turret.getPosition())
            log.add("Turret position centered",Turret.FORWARD_POSITION)
            follower.update()
            log.tick()
        }
    }
}