package org.firstinspires.ftc.teamcode.opmodes.debug

import com.pedropathing.geometry.Pose
import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import org.firstinspires.ftc.teamcode.enums.Colours
import org.firstinspires.ftc.teamcode.opmodes.AutoBase
import org.firstinspires.ftc.teamcode.subsystems.Hood
import org.firstinspires.ftc.teamcode.subsystems.Intake
import org.firstinspires.ftc.teamcode.subsystems.Joint
import org.firstinspires.ftc.teamcode.subsystems.Shooter
import org.firstinspires.ftc.teamcode.subsystems.Wicket
import org.firstinspires.ftc.teamcode.tasks.TaskBuilder.execute
import org.firstinspires.ftc.teamcode.tasks.TaskBuilder.serial
import org.firstinspires.ftc.teamcode.tasks.TaskBuilder.sleepms

@Autonomous

class AutoDebugRed : AutoBase(Pose(120.0,123.0, 32.0),Colours.RED) {

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
    private val shootSeq = serial(
        execute{
            Intake.stop()
            Hood.setPosition(deg)
            Shooter.setRPM(power)
            actionQueue.add(100)
            {
                Wicket.setPosition(Wicket.OPEN_POSITION)
                actionQueue.add(300)
                {
                    Intake.setPowerMain(1.0)
                    Intake.setPowerSupport(0.7)
                    actionQueue.add(1100)
                    {
                        Shooter.setRPM(0.0)
                        Wicket.setPosition(Wicket.CLOSE_POSITION)
                    }
                }
            }
        }
    )
    private val preCollectSeq = serial(
        execute{Joint.setPosition(Joint.COLLECT_POSITION)},
        execute{Intake.setPowerMain(1.0)} ,
        execute{Intake.setPowerSupport(0.8)},
    )
    private val afterCollectSeq = serial(
        execute{
            Intake.setPowerMain(0.7)
            Joint.setPosition(Joint.INIT_POSITION)
        }
    )

    override fun onInit(){
        super.onInit()
        //allianceColour=  Colours.RED//TODO change for each auto


        task = serial(
            execute{ goTo(88.0,93.0,39.0)},//preload-1
            execute{Shooter.charge(power)},
            execute{Intake.setPowerMain(0.8)},
            sleepms(900),
            shootSeq,
            sleepms(650),
            execute{ Joint.setPosition(Joint.COLLECT_POSITION) },
            execute{ goTo(103.0,60.5,20.0)},//
            sleepms(1250),
            execute{ goTo(121.6,60.5,20.0)},//collect
            sleepms(900),
            preCollectSeq,






            )
    }

}