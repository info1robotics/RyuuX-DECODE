package org.firstinspires.ftc.teamcode.opmodes

import androidx.annotation.CallSuper
import com.pedropathing.follower.Follower
import com.pedropathing.geometry.Pose
//import com.pedropathing.localization.Pose
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import org.firstinspires.ftc.teamcode.common.ActionQueue
import org.firstinspires.ftc.teamcode.common.GamepadEx
import org.firstinspires.ftc.teamcode.common.Log
import org.firstinspires.ftc.teamcode.enums.AutoStartPos
import org.firstinspires.ftc.teamcode.enums.Colours
import org.firstinspires.ftc.teamcode.pedro.Constants
import org.firstinspires.ftc.teamcode.pinpoint.Pinpoint

import org.firstinspires.ftc.teamcode.subsystems.Controller
import org.firstinspires.ftc.teamcode.subsystems.Drivetrain
import org.firstinspires.ftc.teamcode.subsystems.Hood
import org.firstinspires.ftc.teamcode.subsystems.Shooter
import org.firstinspires.ftc.teamcode.subsystems.Turret
import org.firstinspires.ftc.teamcode.tasks.Task
import org.firstinspires.ftc.teamcode.tasks.TaskBuilder.serial
import org.openftc.easyopencv.OpenCvCamera
import org.openftc.easyopencv.OpenCvPipeline

abstract class AutoBase(private val startPose: Pose = Pose(0.0, 0.0, Math.toRadians(0.0)),private val allianceColour:Colours) : LinearOpMode() {

    var far = false//play far or near
    lateinit var gamepadEx1: GamepadEx

    lateinit var follower: Follower
    lateinit var log: Log
    lateinit var camera: OpenCvCamera
    lateinit var pipeline: OpenCvPipeline

    //lateinit var drive: MecanumDrive
    open var task: Task = serial()

    var actionQueue = ActionQueue()

    var distance=0.0
    var deg = 0.0
    var power = 0.0
    var full = true

    var rawHeading = 0.0
    var correctedHeading =0.0

@CallSuper
    open fun onInit() {
        gamepadEx1 = GamepadEx(gamepad1)
        Drivetrain.initAuto(hardwareMap)
        Pinpoint.init(hardwareMap)
       // Limelight.init(hardwareMap)

        follower = Constants.createFollower(hardwareMap);
        follower.pose = startPose
        log = Log(this.telemetry)

        Controller.init(hardwareMap)
        Controller.setInitAuto()


    }

    @CallSuper
    fun onInitTick() {
        state = State.INIT

        while (!isStarted && !isStopRequested) {
            gamepadEx1.update()
            log.tick()
        }

    }

    @CallSuper
    @Throws(InterruptedException::class)
    fun onStart() {
        if (isStopRequested) return

        println("left init while loop ")
        println(isStarted)
        println(isStopRequested)
        log.tick()
        state = State.START
        task.start(this)
    }

    fun onStartTick() {
        //turretLock()
        distance = Pinpoint.distance(follower.pose.x,follower.pose.y, allianceColour)
        power = Shooter.calculate(distance)
        deg = Hood.calculate(distance)
        if(!far)
            Hood.setPosition(deg)
        else
            Hood.setPosition(Hood.HIGHER_LIMIT)
        follower.update()
        log.add("@X", follower.pose.x)
        log.add("@Y", follower.pose.y)
        log.add("@Heading", Math.toDegrees(follower.pose.heading))
        log.add("4 distance from"+allianceColour.toString()+"goal" + distance)

        task.tick()
        log.tick()

        actionQueue.update()
    }
    @Throws(InterruptedException::class)
    override fun runOpMode() {

        onInit()
        instance=this

        while (!isStarted && !isStopRequested) {
            onInitTick()
            log.tick()
        }

        waitForStart()

        if (isStopRequested) return
       // Limelight.start()
        onStart()
        state = State.START

        while (opModeIsActive() && !isStopRequested) {

            rawHeading = follower.pose.heading
            correctedHeading = if(rawHeading < 0)
                Math.PI + (rawHeading + Math.PI )
            else
                rawHeading

            onStartTick()
            log.tick()
            actionQueue.update()
            log.tick()
        }
        //Limelight.stop()
    }


    enum class State {
        DEFAULT,
        INIT,
        START
    }

    companion object {
        var state: State = State.DEFAULT
        var instance: AutoBase? = null
    }
    fun turretLock()
    {
        if(allianceColour==Colours.BLUE)
        {
            if(Math.toDegrees(correctedHeading)<230 && Math.toDegrees(correctedHeading)>60)//previously 96 -60
                Turret.lockToTarget(follower.pose.x,follower.pose.y,correctedHeading,allianceColour,0.0)
            else
                Turret.setPosition(Turret.FORWARD_POSITION)
        }
        else
        {
            if(Math.toDegrees(rawHeading)<96 && Math.toDegrees(rawHeading)>-60)//previously 96 -60
                Turret.lockToTarget(follower.pose.x,follower.pose.y,rawHeading,allianceColour,0.0)
            else
                Turret.setPosition(Turret.FORWARD_POSITION)
        }
    }
}
