package org.firstinspires.ftc.teamcode.opmodes
import com.pedropathing.follower.Follower
import com.pedropathing.geometry.Pose
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import com.qualcomm.robotcore.hardware.Gamepad
import org.firstinspires.ftc.teamcode.common.ActionQueue
import org.firstinspires.ftc.teamcode.common.AprilTags
import org.firstinspires.ftc.teamcode.common.AutoConstants
import org.firstinspires.ftc.teamcode.common.GamepadEx
import org.firstinspires.ftc.teamcode.common.Log
import org.firstinspires.ftc.teamcode.enums.Colours
import org.firstinspires.ftc.teamcode.opmodes.debug.ShooterTesting
import org.firstinspires.ftc.teamcode.pedro.Constants
import org.firstinspires.ftc.teamcode.pinpoint.Pinpoint
import org.firstinspires.ftc.teamcode.subsystems.Controller
import org.firstinspires.ftc.teamcode.subsystems.Drivetrain
import org.firstinspires.ftc.teamcode.subsystems.Hood
import org.firstinspires.ftc.teamcode.subsystems.Intake
import org.firstinspires.ftc.teamcode.subsystems.Jack
import org.firstinspires.ftc.teamcode.subsystems.Joint
import org.firstinspires.ftc.teamcode.subsystems.Shooter
import org.firstinspires.ftc.teamcode.subsystems.Turret
import org.firstinspires.ftc.teamcode.subsystems.Wicket
import org.firstinspires.ftc.teamcode.subsystems.extra.Limelight
import org.firstinspires.ftc.teamcode.tasks.TaskBuilder.sleepuntil
import kotlin.math.absoluteValue

@TeleOp(name = "@Teleop")
class Teleop : LinearOpMode() {

    fun Gamepad.corrected_left_stick_y(): Float = -this.left_stick_y

    private var startPose: Pose = AutoConstants.CENTER_POS
    private var allianceColour:Colours = Colours.RED

    lateinit var gamepadEx1: GamepadEx
    lateinit var gamepadEx2: GamepadEx
    lateinit var follower: Follower
    val actionQueue = ActionQueue()

    private var empty = 1.0//for intake
    private var transition = false//ignore other systems commands while in a motion
    private var distanceLL = 0.0//distance got from limelight
    private var distancePP = 0.0//distance got from odo
    private var distance = 0.0
    private var max = 205
    var available = false

    var forwardPower =0.0
    var strafePower = 0.0
    var primaryRotationPower = 0.0
    var delay = 0L
    var rawHeading = 0.0
    var correctedHeading = 0.0

    private fun handleInputDrivetrain()
    {
        forwardPower = gamepad1.corrected_left_stick_y().toDouble()
        strafePower =  gamepad1.left_stick_x.toDouble()
        primaryRotationPower = (gamepad1.right_trigger.toDouble() - gamepad1.left_trigger.toDouble())
        Drivetrain.driveMecanum(forwardPower, strafePower, primaryRotationPower, 1.0)
    }
    private fun handleInputIntake()
    {
        if(!transition)
        {

            if(!Intake.isEmptyBottom())
            {
                gamepad1.rumbleBlips(1)
                gamepad2.rumbleBlips(1)
            }

            if (gamepad2.left_trigger > 0.1 )
            {
                Intake.reverse()
            }
            else
            {
                Intake.setPowerMain(gamepad2.right_trigger.toDouble())

                if(Intake.isFull())
                    Intake.setPowerSupport(0.1)
                else if(!Intake.isEmptyTop())
                    Intake.setPowerSupport((gamepad2.right_trigger.toDouble())*(0.3))
                else
                    Intake.setPowerSupport((gamepad2.right_trigger.toDouble())*(0.9))
            }

            if(gamepad2.right_bumper)
                Joint.setPosition(0.31)
            else if(gamepad2.left_bumper)
                Joint.setPosition(Joint.COLLECT_POSITION+0.18)
            else if(gamepad2.right_trigger+gamepad2.left_trigger>0)
                Joint.setPosition(Joint.COLLECT_POSITION)
            else
                Joint.setPosition(Joint.INIT_POSITION)
        }
    }
    private fun handleInputJack()
    {
        if(gamepad2.dpad_up) Jack.setPosition(Jack.PARKED_POSITION)
        if(gamepad2.dpad_down) Jack.setPosition(Jack.INIT_POSITION)
    }
    var far = false
    var power = 0.0
    private fun handleInputShooter() {
        if(((Intake.isUpper() && distance<230) || gamepadEx1.getButtonDown("x")) && !transition )
        {
            if(distance>190)
                Shooter.setRPM(power+125)
            else
                Shooter.setRPM(power)

        }
        if(distance<max)
        {

            far = false
            Hood.setPosition(Hood.calculate(distance))
            if(gamepadEx1.getButtonDown("a") && available)
            {
                Wicket.setPosition(Wicket.OPEN_POSITION)
                transition=true
                Intake.setPowerSupport(-0.3)
                actionQueue.add(delay)//if this doesn t work 1200
                {
                    Intake.setPowerMain(1.0)
                    Intake.setPowerSupport(0.9)
                    actionQueue.add(700)
                    {
                        Intake.stop()
                        Shooter.setRPM(0.0)
                        Wicket.setPosition(Wicket.CLOSE_POSITION)
                        transition = false
                        empty = 1.0
                    }
                }
            }
            if(gamepadEx1.getButtonDown("b") && available)
            {
                Wicket.setPosition(Wicket.OPEN_POSITION)
                transition=true
                Intake.setPowerSupport(-0.4)
                actionQueue.add(300)//if this doesn t work 1200
                {
                    Intake.setPowerMain(1.0)
                    Intake.setPowerSupport(0.9)
                    actionQueue.add(700)
                    {
                        Intake.stop()
                        Shooter.setRPM(0.0)
                        Wicket.setPosition(Wicket.CLOSE_POSITION)
                        transition = false
                        empty = 1.0
                    }
                }
            }

        }
        else far  = true
        if(transition && distance<max)
            Shooter.setRPM(power)

    }
    var tx = 0.0
    var hold = false
    private fun handleInputTurret() {

        if(gamepadEx2.getButtonDown("y") && !hold) hold=true
        else if(gamepadEx2.getButtonDown("y") && hold) hold=false

        if(!hold && distance < 225)
        {

            if(allianceColour==Colours.BLUE)
            {
                if(Math.toDegrees(correctedHeading)<230 && Math.toDegrees(correctedHeading)>60) {
                    available=true
                    Turret.lockToTarget(follower.pose.x,follower.pose.y,correctedHeading,allianceColour,0.0)
                }
                else {
                    available=false
                    Turret.hold()
                }
            }
            else {
                if(Math.toDegrees(rawHeading)<120 && Math.toDegrees(rawHeading)>-52) {
                    available=true
                    Turret.lockToTarget(follower.pose.x,follower.pose.y,rawHeading,allianceColour,0.0)
                }
                else {
                    available=false
                    Turret.hold()
                }
            }
        }
        else Turret.hold()
    }
    var wasSelected = false
    override fun runOpMode() {
        Controller.init(hardwareMap)
        Pinpoint.init(hardwareMap)

        //Limelight.start()

        val log = Log(telemetry)

        follower = Constants.createFollower(hardwareMap);
        follower.pose = startPose


        empty=1.0
        log.tick()
        waitForStart()

        while (!gamepad1.dpad_left && !gamepad1.dpad_right);
        Controller.setInit()
        gamepadEx1 = GamepadEx(gamepad1)
        gamepadEx2 = GamepadEx(gamepad2)

        while (opModeIsActive() && !isStopRequested) {

            handleInputDrivetrain()
            handleInputIntake()
            handleInputShooter()
            handleInputTurret()
            handleInputJack()

            gamepadEx1.update()
            gamepadEx2.update()
            actionQueue.update()

            follower.update()
            if (gamepad1.dpad_right) {
                allianceColour = Colours.RED
                //Limelight.allianceTag = AprilTags.RED
                follower.pose = Pose(//TODO change to autopos
                    98.0,
                    83.0,
                    Math.toRadians(0.0)
                )
                wasSelected = true
            }
            else if (gamepad1.dpad_left) {
                allianceColour = Colours.BLUE//TODO change to autopos
                //Limelight.allianceTag = AprilTags.BLUE
                follower.pose = Pose(
                    56.0,
                    79.0,
                    Math.toRadians(160.0)
                )
                wasSelected = true
            }

            if (gamepad1.dpad_up && allianceColour==Colours.RED) {
                follower.pose = Pose(
                    120.0,
                    123.0,
                    Math.toRadians(32.0)
                )
            }
            else if(gamepad1.dpad_up && allianceColour==Colours.BLUE) {
                follower.pose = Pose(
                    24.0,
                    123.0,
                    Math.toRadians(148.0)
                )
            }

            power = Shooter.calculate(distance)
            //var ta = Limelight.getTa()
            //var distanceLL = ta?.let { Limelight.getDistanceToAprilTag(it) }
            distancePP = Pinpoint.distance(follower.pose.x,follower.pose.y, allianceColour)
            distance = distancePP//change distance method

            delay = if(distance<=175) 0
            else if(distance <200) 300
            else 600

            rawHeading = follower.pose.heading
            correctedHeading = if(rawHeading < 0)
                Math.PI + (rawHeading + Math.PI )
            else
                rawHeading

            log.add("choose alliance colour RED/BLUE by dpad left/right",allianceColour.toString())
            //Limelight.getTx()?.let { log.add("tx", it) }
            //Limelight.getTa()?.let  { log.add("ta", it) }
            //tx= Limelight.getTx()!!
            log.add("distanceLL $distanceLL")
            log.add("distancePP $distancePP")
            log.add("Intake has 3 balls",Intake.isFull())
            //log.add("rgb",Intake.getColorReading())
            log.add("@X", follower.pose.x)
            log.add("@Y", follower.pose.y)
            log.add("@Heading", Math.toDegrees(follower.pose.heading))
            log.add("distance from $allianceColour goal: $distancePP")
            log.add(("far "+(far).toString()))
            log.tick()
        }
    }
}