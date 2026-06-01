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
    private var correctedHeading = 0.0
    private  var rawHeading = 0.0
    var available = false
    var compensation = 0.0

    var hold = false

    var forwardPower =0.0
    var strafePower = 0.0
    var primaryRotationPower = 0.0
    var delay = 0L

    private fun handleInputDrivetrain()
    {
        forwardPower = gamepad1.corrected_left_stick_y().toDouble()
        strafePower =  gamepad1.left_stick_x.toDouble()
        primaryRotationPower = (gamepad1.right_trigger.toDouble() - gamepad1.left_trigger.toDouble())
        Drivetrain.driveMecanum(forwardPower, strafePower, primaryRotationPower, 1.0)
        Drivetrain.setBrake()
    }
    var active = false
    private fun handleInputIntake()
    {
        if(!transition)
        {

            if(!Intake.isEmptyBottom())
            {
                gamepad1.rumbleBlips(1)
            }

            if(gamepadEx1.getButtonDown("b") && !active)
                active = true
            else if (gamepadEx1.getButtonDown("b") && active)
                active = false

            if(active && !transition)
            {
                if(Intake.isFull())
                {
                    Intake.setPowerMain(0.95)
                    Intake.setPowerSupport(0.2)
                }
                else if(!Intake.isEmptyTop())
                {
                    Intake.setPowerMain(1.0)
                    Intake.setPowerSupport(0.3)
                }
                else
                {
                    Intake.setPowerMain(1.0)
                    Intake.setPowerSupport(0.95)
                }
            }
            else if(!transition && !active)
            {
                Intake.stop()
            }

        }

        if(gamepad2.right_bumper || gamepad1.right_stick_y>0)
            Joint.setPosition(Joint.COLLECT_POSITION+0.2)
        else
            Joint.setPosition(Joint.COLLECT_POSITION)
    }

    private fun handleInputPark()
    {
        if(gamepad2.dpad_up) Jack.setPosition(Jack.PARKED_POSITION)
        if(gamepad2.dpad_down) Jack.setPosition(Jack.INIT_POSITION)
    }
    var far = false
    var power = 0.0
    var supportConverter = 0.0
    var velOffset=0.0//TODO tune it
    var charge = false
    private fun handleInputShooter() {


        if(gamepadEx2.getButtonDown("b") && !charge)
            charge = true
        else if(gamepadEx2.getButtonDown("b") && charge)
            charge = false

        if(transition || !Intake.isEmptyTop() || charge)
            Shooter.setRPM((power + velOffset))
        else
            Shooter.setRPM(0.0)

        if(distance < 205)
            supportConverter = 1.0//previously 1.0
        else if(distance<240)
            supportConverter = 0.9
        else
            supportConverter= 0.8

        far = false
        Hood.setPosition(Hood.calculate(distance))
        if(gamepadEx1.getButtonDown("a") && available)
        {
            transition=true
            Wicket.setPosition(Wicket.OPEN_POSITION)
            actionQueue.add(delay)//if this doesn t work 1200
            {
                Intake.setPowerMain(1.0)
                Intake.setPowerSupport(supportConverter)
                actionQueue.add(400)//
                {
                    //Shooter.setRPM(0.0)
                    Wicket.setPosition(Wicket.CLOSE_POSITION)
                    transition = false
                    active = false
                    empty = 1.0
                }
            }
        }
    }
    var turretOffset = 0.0
    var velox=0.0
    var veloy=0.0
    private fun handleInputTurret() {


        Pinpoint
        if(gamepadEx2.getButtonDown("y") && !hold) hold=true
        else if(gamepadEx2.getButtonDown("y") && hold) hold=false




        if(!hold /*&& Pinpoint.inBorder(follower.pose.x,follower.pose.y)*/)
        {

            if(allianceColour==Colours.BLUE)
            {
                if(Math.toDegrees(correctedHeading)<293 && Math.toDegrees(correctedHeading)>-17) {//138 centered +-155
                    available=true
                    Turret.lockToTarget(follower.pose.x,follower.pose.y,correctedHeading,allianceColour,velox,veloy,turretOffset)
                }
                else {
                    available=false
                    Turret.setPosition(Turret.FORWARD_POSITION)
                }
            }
            else {
                if(Math.toDegrees(rawHeading)<187 && Math.toDegrees(rawHeading)>-123) {//32 centered
                    available=true
                    Turret.lockToTarget(follower.pose.x,follower.pose.y,rawHeading,allianceColour,velox,veloy,turretOffset)
                }
                else {
                    available=false
                    Turret.setPosition(Turret.FORWARD_POSITION)
                }
            }
        }
        else Turret.setPosition(Turret.FORWARD_POSITION)
    }
    private fun handleInputOffsets()
    {
        if(gamepadEx1.getButtonDown("y")) velOffset+=15
        if(gamepadEx1.getButtonDown("x")) velOffset-=15
    }

    private fun handleInputJack()
    {
        if(gamepad1.dpad_down)
            Jack.setPosition(Jack.PARKED_POSITION)
        if(gamepad1.right_stick_button)
            Jack.setPosition(Jack.INIT_POSITION)
    }

    var wasSelected =false
    override fun runOpMode() {
        Controller.init(hardwareMap)
        Pinpoint.init(hardwareMap)

        //Limelight.start()

        val log = Log(telemetry)

        follower = Constants.createFollower(hardwareMap);
        follower.pose = Pose(90.0,
            83.0,
            Math.toRadians(0.0))


        empty=1.0
        //log.tick()
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
            handleInputPark()
            handleInputOffsets()

            gamepadEx1.update()
            gamepadEx2.update()
            actionQueue.update()

            follower.update()
            if (gamepad1.dpad_right) {
                allianceColour = Colours.RED
                //Limelight.allianceTag = AprilTags.RED
                follower.pose = Pose(
                    91.0,
                    83.0,
                    Math.toRadians(0.0)
                )
                wasSelected = true
            }
            else if (gamepad1.dpad_left) {
                allianceColour = Colours.BLUE
                //Limelight.allianceTag = AprilTags.BLUE
                follower.pose = Pose(
                    36.0,
                    83.0,
                    Math.toRadians(-2.0)
                )
                wasSelected = true
            }
            if (gamepad1.dpad_up && allianceColour==Colours.RED) {//TODO RESETS at gate
                velOffset = 0.0
                turretOffset = 0.007
                follower.pose = Pose(//116 120 32 for goal
                    115.0,
                    76.0,
                    Math.toRadians(-5.0)

                )
            }
            else if(gamepad1.dpad_up && allianceColour==Colours.BLUE) {
                velOffset = 0.0
                turretOffset = 0.0
                follower.pose = Pose(
                    29.0,
                    73.0,
                    Math.toRadians(177.0)
                )
            }

            power = Shooter.calculate(distance,velox,veloy,correctedHeading,follower.pose.x,follower.pose.y,allianceColour)

            distancePP = Pinpoint.distance(follower.pose.x,follower.pose.y, allianceColour)
            distance = distancePP//change distance method

            if(gamepadEx1.getButtonDown("bumper_right"))
                turretOffset+=0.007
            else if(gamepadEx1.getButtonDown("bumper_left"))
                turretOffset-=0.007

            delay = if(distance<=175) 50
            else if(distance <200) 100
            else 300

            rawHeading = follower.pose.heading
            correctedHeading = if(rawHeading < 0)
                Math.PI + (rawHeading + Math.PI )
            else
                rawHeading

            velox = follower.velocity.xComponent
            veloy = follower.velocity.yComponent

            log.add("charge",charge)
            log.add("x",follower.pose.x)
            log.add("y",follower.pose.y)
            log.add("corrected hed",Math.toRadians(correctedHeading))
            log.add("raw hed",Math.toRadians(rawHeading))
            log.add("turret offset, the step is 0.007:",turretOffset)

            log.add("turret pos",Turret.getPosition())
            log.add("velox",follower.velocity.xComponent)
            log.add("veloy",follower.velocity.yComponent)
            log.add("vToward:",Shooter.velocityTowardsGoal)
            Shooter.updateCompensatedPIDF()
            log.tick()

        }
    }
}