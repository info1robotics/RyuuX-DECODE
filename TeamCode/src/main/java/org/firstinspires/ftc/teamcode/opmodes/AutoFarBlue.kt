package org.firstinspires.ftc.teamcode.opmodes

import com.acmerobotics.roadrunner.TrajectoryActionBuilder
import com.acmerobotics.roadrunner.ftc.runBlocking
import com.pedropathing.geometry.Pose
import com.qualcomm.robotcore.eventloop.opmode.Autonomous
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
class AutoFarBlue : AutoBase(Pose(54.0, 9.0, Math.toRadians(90.0)),Colours.BLUE) {

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
            Shooter.setRPM(4125.0)
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
            Shooter.setRPM(4125.0)
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
            execute { Wicket.setPosition(Wicket.CLOSE_POSITION) }, // preload -1
            chargePreload,
            execute { Turret.setPosition(0.425) },
            execute { goTo(54.0, 16.0, 90.0) },
            sleepms(700),
            shootPreload,

            sleepms(700),
            execute { goTo(48.0, 34.5, 180.0) },//first spike mark -2
            sleepms(700),
            preCollectSeq,
            execute { goTo(23.0, 34.5, 180.0) }, // collected
            sleepms(850),
            execute{Turret.setPosition(0.692)},
            charge,
            execute { goTo(54.0, 16.0, 180.0) },
            sleepms(500),
            afterCollectSeq,
            sleepms(600),
            shootSeq,

            sleepms(750),
            preCollectSeq,
            execute { goTo(1.0, 10.0, 180.0) },//human -3
            sleepms(1200),
            execute { goTo(20.0, 10.0, 180.0) },
            sleepms(300),
            execute { goTo(1.0, 10.0, 180.0) },
            sleepms(400),
            execute { goTo(54.0, 16.0, 180.0) },
            sleepms(500),
            execute{Joint.setPosition(Joint.COLLECT_POSITION+0.3)},
            sleepms(100),
            afterCollectSeq,
            sleepms(700),
            shootSeq,

            sleepms(700),
            preCollectSeq,
            execute { goTo(6.0, 6.0, 130.0) },//human -4
            sleepms(1000),
            execute { goTo(6.0, 43.0, 130.0) },//human -4
            sleepms(1000),
            execute { goTo(54.0, 16.0, 180.0) },
            afterCollectSeq,
            sleepms(1400),
            shootSeq,


            sleepms(700),
            preCollectSeq,
            execute { goTo(6.0, 6.0, 130.0) },//human -5
            sleepms(1000),
            execute { goTo(6.0, 43.0, 130.0) },
            sleepms(1000),
            execute { goTo(54.0, 16.0, 180.0) },
            afterCollectSeq,
            sleepms(1400),
            shootSeq,

            sleepms(700),
            preCollectSeq,
            execute { goTo(6.0, 6.0, 130.0) },//human -6
            sleepms(1000),
            execute { goTo(6.0, 43.0, 130.0) },
            sleepms(1000),
            execute { goTo(54.0, 16.0, 180.0) },
            afterCollectSeq,
            sleepms(1400),
            shootSeq,

            sleepms(700),
            preCollectSeq,
            execute { goTo(6.0, 6.0, 130.0) },//human -7
            sleepms(1000),
            execute { goTo(6.0, 43.0, 130.0) },//human -7
            sleepms(1000),
            execute { goTo(54.0, 16.0, 180.0) },
            afterCollectSeq,
            sleepms(1400),
            shootSeq,

            sleepms(800),


            execute{Shooter.setRPM(0.0)}

        )
    }
}