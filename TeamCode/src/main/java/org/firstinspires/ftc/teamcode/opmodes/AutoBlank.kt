package org.firstinspires.ftc.teamcode.opmodes

import com.acmerobotics.roadrunner.TrajectoryActionBuilder
import com.acmerobotics.roadrunner.ftc.runBlocking
import com.pedropathing.geometry.Pose
import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import com.qualcomm.robotcore.eventloop.opmode.Disabled
import org.firstinspires.ftc.teamcode.common.AutoUtil.p
import org.firstinspires.ftc.teamcode.common.AutoUtil.rad
import org.firstinspires.ftc.teamcode.enums.Colours
import org.firstinspires.ftc.teamcode.subsystems.Controller
import org.firstinspires.ftc.teamcode.subsystems.extra.Limelight
import org.firstinspires.ftc.teamcode.tasks.Task
import org.firstinspires.ftc.teamcode.tasks.TaskBuilder.action
import org.firstinspires.ftc.teamcode.tasks.TaskBuilder.execute
import org.firstinspires.ftc.teamcode.tasks.TaskBuilder.serial
import org.firstinspires.ftc.teamcode.tasks.TaskBuilder.sleepms
import org.firstinspires.ftc.teamcode.tasks.TaskBuilder.sleepuntil

@Autonomous
@Disabled
class AutoBlank : AutoBase(Pose(0.0,0.0, Math.toRadians(0.0)),Colours.RED) {

    fun turnTo(degrees: Double) { // if you want to turn right, use negative degrees
        val temp = Pose(follower.pose.x, follower.pose.y, Math.toRadians(degrees))
        follower.holdPoint(temp)
    }

    fun goTo(x: Double, y: Double, degrees: Double) {
        val temp = Pose(x, y, Math.toRadians(degrees))
        follower.holdPoint(temp)
    }
    fun stopMidTrajectory() {
        follower.holdPoint(follower.pose)
    }
    private val seq = serial(
        //execute{action}
    )
    
    override fun onInit(){
        super.onInit()
        task = serial(
            execute{ goTo(0.0,0.0,0.0) },//middle of the field
            sleepms(200),//wait for the previous action to stop
            execute{ goTo(20.0,0.0,0.0) },//strafe left
            execute{stopMidTrajectory()},//stops at the point in time
            execute{seq}
        )



    }
    
}