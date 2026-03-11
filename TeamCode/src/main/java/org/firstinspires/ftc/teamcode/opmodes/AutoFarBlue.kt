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
            Shooter.setRPM(4000.0)
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
    private val shootPreload = serial(
        execute {
            Intake.stop()
            Shooter.setRPM(3975.0)
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
    private val shootSeqPow = serial(
        execute {
            Intake.stop()
            Shooter.setRPM(4075.0)
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
        execute{Joint.setPosition(Joint.COLLECT_POSITION)},
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
            Shooter.setRPM(4000.0)
        }
    )
    private val chargePreload = serial(
        execute{
            Shooter.setRPM(3975.0)
        }
    )

    override fun onInit() {
        super.onInit()
        far=true
        task = serial(
            execute { Wicket.setPosition(Wicket.CLOSE_POSITION) }, // preload -1
            chargePreload,
            execute { Turret.setPosition(0.645) },
            execute { goTo(54.0, 16.0, 90.0) },
            sleepms(600),
            shootPreload,

            sleepms(700),
            execute { goTo(48.0, 34.0, 180.0) },//first spike mark -2
            sleepms(700),
            preCollectSeq,
            execute { goTo(14.0, 34.0, 180.0) }, // collected
            sleepms(1000),
            execute{Turret.setPosition(0.09)},
            charge,
            execute { goTo(54.0, 16.0, 180.0) },
            sleepms(500),
            afterCollectSeq,
            sleepms(1000),
            shootSeq,

            sleepms(800),
            preCollectSeq,
            execute { goTo(1.0, 8.0, 180.0) },//human -3
            sleepms(1200),
            execute { goTo(20.0, 8.0, 180.0) },
            sleepms(300),
            execute { goTo(1.0, 8.0, 180.0) },
            sleepms(400),
            execute{Joint.setPosition(Joint.COLLECT_POSITION+0.3)},
            execute { goTo(54.0, 16.0, 180.0) },
            sleepms(600),
            afterCollectSeq,
            sleepms(600),
            shootSeq,

            sleepms(800),
            preCollectSeq,
            execute { goTo(1.0, 8.0, 180.0) },//human -4
            sleepms(1600),
            execute{Joint.setPosition(Joint.COLLECT_POSITION+0.3)},
            execute { goTo(54.0, 16.0, 180.0) },
            sleepms(600),
            afterCollectSeq,
            sleepms(600),
            shootSeq,

            sleepms(800),
            preCollectSeq,
            execute { goTo(1.0, 8.0, 180.0) },//human -5
            sleepms(1600),
            execute{Joint.setPosition(Joint.COLLECT_POSITION+0.3)},
            execute { goTo(54.0, 16.0, 180.0) },
            sleepms(600),
            afterCollectSeq,
            sleepms(600),
            shootSeq,

            sleepms(800),
            preCollectSeq,
            execute { goTo(1.0, 8.0, 180.0) },//human -6
            sleepms(1600),
            execute{Joint.setPosition(Joint.COLLECT_POSITION+0.3)},
            execute { goTo(54.0, 16.0, 180.0) },
            sleepms(600),
            afterCollectSeq,
            sleepms(600),
            shootSeq,

            sleepms(800),
            preCollectSeq,
            execute { goTo(1.0, 8.0, 180.0) },//human -7
            sleepms(1600),
            execute{Joint.setPosition(Joint.COLLECT_POSITION+0.3)},
            execute { goTo(54.0, 16.0, 180.0) },
            sleepms(600),
            afterCollectSeq,
            sleepms(600),
            shootSeq,

            sleepms(800),
            preCollectSeq,
            execute { goTo(1.0, 8.0, 180.0) },//human -8
            sleepms(1600),
            execute{Joint.setPosition(Joint.COLLECT_POSITION+0.3)},
            execute { goTo(54.0, 16.0, 180.0) },
            sleepms(600),
            afterCollectSeq,
            sleepms(600),
            shootSeq,

            sleepms(1400),
            execute{Shooter.setRPM(0.0)}

        )
    }
}