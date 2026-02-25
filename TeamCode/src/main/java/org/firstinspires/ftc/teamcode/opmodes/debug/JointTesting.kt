package org.firstinspires.ftc.teamcode.opmodes.test

import com.acmerobotics.dashboard.config.Config
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import kotlinx.coroutines.Job
import org.firstinspires.ftc.teamcode.common.ActionQueue
import org.firstinspires.ftc.teamcode.common.Log
import org.firstinspires.ftc.teamcode.subsystems.Joint
import org.firstinspires.ftc.teamcode.subsystems.Shooter
import org.firstinspires.ftc.teamcode.subsystems.Turret
import org.firstinspires.ftc.teamcode.subsystems.extra.Limelight

@TeleOp
@Config
class JointTesting : LinearOpMode() {
    private val actionQueue = ActionQueue()
    companion object {
        @JvmField
        var position = Joint.COLLECT_POSITION


    }
    lateinit var log: Log
    override fun runOpMode() {
        Joint.init(hardwareMap)
        log = Log(this.telemetry)
        waitForStart()

        while (opModeIsActive()) {
            Joint.setPosition(position)
            log.add("Joint Position",Joint.getPosition())
            log.tick()
        }
    }
}