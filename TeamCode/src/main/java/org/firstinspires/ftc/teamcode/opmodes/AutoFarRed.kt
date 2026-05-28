package org.firstinspires.ftc.teamcode.opmodes

import com.acmerobotics.roadrunner.TrajectoryActionBuilder
import com.acmerobotics.roadrunner.ftc.runBlocking
import com.pedropathing.geometry.Pose
import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import com.qualcomm.robotcore.eventloop.opmode.Disabled
import org.firstinspires.ftc.teamcode.enums.Colours
import org.firstinspires.ftc.teamcode.subsystems.Hood
import org.firstinspires.ftc.teamcode.subsystems.Intake
import org.firstinspires.ftc.teamcode.subsystems.Joint
import org.firstinspires.ftc.teamcode.subsystems.Shooter
import org.firstinspires.ftc.teamcode.subsystems.Turret
import org.firstinspires.ftc.teamcode.subsystems.Wicket
import org.firstinspires.ftc.teamcode.tasks.TaskBuilder.execute
import org.firstinspires.ftc.teamcode.tasks.TaskBuilder.serial
import org.firstinspires.ftc.teamcode.tasks.TaskBuilder.sleepms

@Autonomous
class AutoFarRed : AutoBase(Pose(90.0, 9.0, Math.toRadians(90.0)),Colours.RED) {

    fun turnTo(degrees: Double) {
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
        execute {
            Intake.stop()
            Shooter.setRPM(4175.0)
            Hood.setPosition(0.5)
            actionQueue.add(100) {
                Wicket.setPosition(Wicket.OPEN_POSITION)
                actionQueue.add(300) {
                    Intake.setPowerMain(1.0)
                    Intake.setPowerSupport(0.65)
                    actionQueue.add(700)
                    {
                        Wicket.setPosition(Wicket.CLOSE_POSITION)
                    }

                }

            }
        }
    )
    private val shootPreload = serial(
        execute {
            Intake.stop()
            Shooter.setRPM(4075.0)//30
            Hood.setPosition(0.5)
            actionQueue.add(100) {
                Wicket.setPosition(Wicket.OPEN_POSITION)
                actionQueue.add(300) {
                    Intake.setPowerMain(1.0)
                    Intake.setPowerSupport(0.65)
                    actionQueue.add(700)
                    {
                        Wicket.setPosition(Wicket.CLOSE_POSITION)
                    }

                }

            }
        }
    )

    private val preCollectSeq = serial(
        execute{Joint.setPosition(Joint.FAR_POSITION)},
        execute { Intake.setPowerMain(1.0) },
        execute { Intake.setPowerSupport(0.7) },
    )

    private val afterCollectSeq = serial(
        execute {
            Intake.setPowerMain(1.0)
            Intake.setPowerSupport(0.3)
        }
    )
    private val charge = serial(
        execute{
            Shooter.setRPM(4175.0)
        }
    )
    private val chargePreload = serial(
        execute{
            Shooter.setRPM(4075.0)
        }
    )


    override fun onInit() {
        super.onInit()
        far=true
        task = serial(
            execute{Wicket.setPosition(Wicket.CLOSE_POSITION)},//preload -1
            chargePreload,
            execute{Turret.setPosition(0.56)},
            execute{goTo(90.0,16.0,90.0)},
            sleepms(700),
            shootPreload,

            sleepms(900),
            //sleepms(25000)
            execute { goTo(95.0, 37.0, 0.0) }, // first spike mark -2
            sleepms(700),
            preCollectSeq,
            execute { goTo(129.0, 37.0, 0.0) },//collected
            sleepms(1000),
            execute{Turret.setPosition(0.32)},//
            charge,
            execute{goTo(90.0,12.0,0.0)},//shooting position
            sleepms(500),
            afterCollectSeq,
            sleepms(1250),
            shootSeq,

            sleepms(700),
            preCollectSeq,
            execute{goTo(134.0,6.0,0.0)},//human player -3
            sleepms(1200),
            execute{goTo(127.0,6.0,0.0)},//human player -3
            sleepms(300),
            execute{goTo(136.0,6.0,0.0)},//human player -2
            sleepms(400),
            execute{goTo(90.0,12.0,0.0)},
            afterCollectSeq,
            sleepms(1250),
            shootSeq,


            sleepms(700),
            preCollectSeq,
            execute{goTo(136.0,6.0,40.0)},//human player -4
            sleepms(1000),
            execute{goTo(136.0,43.0,40.0)},//human player -4
            sleepms(1000),
            execute{goTo(90.0,12.0,0.0)},
            afterCollectSeq,
            sleepms(1400),
            shootSeq,

            sleepms(700),
            preCollectSeq,
            execute{goTo(136.0,6.0,40.0)},//human player -5
            sleepms(1000),
            execute{goTo(136.0,43.0,40.0)},//human player -5
            sleepms(1000),
            execute{goTo(90.0,12.0,0.0)},
            afterCollectSeq,
            sleepms(1400),
            shootSeq,

            sleepms(700),
            preCollectSeq,
            execute{goTo(136.0,6.0,40.0)},//human player -6
            sleepms(1000),
            execute{goTo(136.0,43.0,40.0)},//human player -6
            sleepms(1000),
            execute{goTo(90.0,12.0,0.0)},
            afterCollectSeq,
            sleepms(1400),
            shootSeq,

            sleepms(700),
            preCollectSeq,
            execute{goTo(136.0,6.0,40.0)},//human player -7
            sleepms(1000),
            execute{goTo(136.0,43.0,40.0)},//human player -7
            sleepms(1000),
            execute{goTo(90.0,12.0,0.0)},
            afterCollectSeq,
            sleepms(1400),
            shootSeq,


            sleepms(700),
            execute{goTo(120.0,30.0,0.0)},


            )
    }
}