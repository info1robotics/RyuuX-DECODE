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
            Shooter.setRPM(3900.0)
            Hood.setPosition(0.5)
            actionQueue.add(100) {
                Wicket.setPosition(Wicket.OPEN_POSITION)
                actionQueue.add(300) {
                    Intake.setPowerMain(1.0)
                    Intake.setPowerSupport(0.6)
                    actionQueue.add(700)
                    {
                        Wicket.setPosition(Wicket.CLOSE_POSITION)
                    }

                }

            }
        }
    )

    private val preCollectSeq = serial(
        execute{
            Joint.setPosition(Joint.COLLECT_POSITION)
        },
        execute { Intake.setPowerMain(1.0) },
        execute { Intake.setPowerSupport(1.0) },
    )

    private val afterCollectSeq = serial(
        execute {
            Intake.setPowerMain(0.3)
            Intake.setPowerSupport(0.1)
        }
    )
    private val charge = serial(
        execute{
            Shooter.setRPM(3900.0)
        }
    )

    override fun onInit() {
        super.onInit()
        far=true
        task = serial(
            execute{Wicket.setPosition(Wicket.CLOSE_POSITION)},//preload -1
            charge,
            execute{Turret.setPosition(0.379)},
            execute{goTo(90.0,16.0,90.0)},
            sleepms(600),
            shootSeq,

            sleepms(700),
            //sleepms(25000)
            execute { goTo(95.0, 37.0, 0.0) }, // first spike mark -2
            sleepms(700),
            preCollectSeq,
            execute { goTo(129.0, 37.0, 0.0) },//collected
            sleepms(1000),
            afterCollectSeq,
            execute{Turret.setPosition(0.93)},
            execute{goTo(90.0,12.0,0.0)},//shooting position
            sleepms(1500),
            shootSeq,

            sleepms(800),
            preCollectSeq,
            execute{goTo(134.0,8.0,0.0)},//human player -3
            sleepms(1200),
            execute{goTo(127.0,8.0,0.0)},//human player -3
            sleepms(300),
            execute{goTo(136.0,8.0,0.0)},//human player -3
            sleepms(400),
            execute{goTo(90.0,16.0,0.0)},
            afterCollectSeq,
            sleepms(1200),
            shootSeq,

            sleepms(800),
            preCollectSeq,
            execute{goTo(134.0,8.0,0.0)},//human player -4
            sleepms(1600),
            execute{Joint.setPosition(Joint.COLLECT_POSITION+0.3)},
            execute{goTo(90.0,16.0,0.0)},
            afterCollectSeq,
            sleepms(1200),
            shootSeq,

            sleepms(800),
            preCollectSeq,
            execute{goTo(134.0,16.0,0.0)},//human player -5
            sleepms(1600),
            execute{Joint.setPosition(Joint.COLLECT_POSITION+0.3)},
            execute{goTo(90.0,16.0,0.0)},
            afterCollectSeq,
            sleepms(1200),
            shootSeq,

            sleepms(800),
            preCollectSeq,
            execute{goTo(134.0,8.0,0.0)},//human player -6
            sleepms(1600),
            execute{Joint.setPosition(Joint.COLLECT_POSITION+0.3)},
            execute{goTo(90.0,16.0,0.0)},
            afterCollectSeq,
            sleepms(1200),
            shootSeq,

            sleepms(800),
            preCollectSeq,
            execute{goTo(134.0,8.0,0.0)},//human player -7
            sleepms(1600),
            execute{Joint.setPosition(Joint.COLLECT_POSITION+0.3)},
            execute{goTo(90.0,16.0,0.0)},
            afterCollectSeq,
            sleepms(1000),
            shootSeq,

            sleepms(800),
            preCollectSeq,
            execute{goTo(134.0,8.0,0.0)},//human player -8
            sleepms(1600),
            execute{Joint.setPosition(Joint.COLLECT_POSITION+0.3)},
            execute{goTo(90.0,16.0,0.0)},
            afterCollectSeq,
            sleepms(1100),
            shootSeq,

            sleepms(1400),
            execute{Shooter.setRPM(0.0)}


        )
    }
}