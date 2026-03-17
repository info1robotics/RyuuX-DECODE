package org.firstinspires.ftc.teamcode.opmodes

import com.pedropathing.geometry.Pose
import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import com.qualcomm.robotcore.eventloop.opmode.Disabled
import org.firstinspires.ftc.teamcode.enums.Colours
import org.firstinspires.ftc.teamcode.subsystems.Intake
import org.firstinspires.ftc.teamcode.subsystems.Joint
import org.firstinspires.ftc.teamcode.subsystems.Shooter
import org.firstinspires.ftc.teamcode.subsystems.Turret
import org.firstinspires.ftc.teamcode.subsystems.Wicket
import org.firstinspires.ftc.teamcode.tasks.TaskBuilder.execute
import org.firstinspires.ftc.teamcode.tasks.TaskBuilder.serial
import org.firstinspires.ftc.teamcode.tasks.TaskBuilder.sleepms
@Autonomous
class AutoCloseBlue : AutoBase(Pose(24.0, 123.0, Math.toRadians(138.0)),Colours.BLUE) {
    var offset =35.0//TODO tune, this is local doesn t affect other classes

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
        execute{
            Intake.stop()
            Shooter.setRPM(power+offset)
            actionQueue.add(100)
            {
                Wicket.setPosition(Wicket.OPEN_POSITION)
                actionQueue.add(300)
                {
                    Shooter.setRPM(power+offset)
                    Intake.setPowerMain(1.0)
                    Intake.setPowerSupport(0.9)
                    actionQueue.add(600)
                    {
                        Shooter.setRPM(0.0)
                        Wicket.setPosition(Wicket.CLOSE_POSITION)
                    }
                }
            }
        }
    )

    private val preCollectSeq = serial(
        execute { Joint.setPosition(Joint.COLLECT_POSITION) },
        execute { Intake.setPowerMain(1.0) },
        execute { Intake.setPowerSupport(1.0) },
    )

    private val afterCollectSeq = serial(
        execute {
            Joint.setPosition(Joint.INIT_POSITION)
            Intake.setPowerMain(0.5)
            Intake.setPowerSupport(0.2)
        }
    )
    private val reverse = serial(
        execute{
            Intake.setPowerSupport(-0.5)
            actionQueue.add(300)
            {
                Intake.setPowerSupport(0.0)
            }
        }
    )

    override fun onInit() {
        super.onInit()
        far=false

        task = serial(
            execute { goTo(56.0, 93.0, 138.0) }, // preload-1 (144-88)
            execute { Shooter.charge() },
            execute { Intake.setPowerMain(0.8) },
            sleepms(900),
            shootSeq,
            sleepms(600),

            execute { goTo(45.0, 62.2, 180.0) }, // pre collect -2
            preCollectSeq,
            sleepms(900),
            execute{Turret.setPosition(0.184)},
            execute { goTo(20.0, 62.2, 180.0) }, // collect
            sleepms(600),
            afterCollectSeq,
            execute { Shooter.charge() },
            sleepms(200),
            execute { goTo(55.0, 83.0, 180.0) },
            sleepms(300),
            execute{Joint.setPosition(Joint.COLLECT_POSITION+0.2)},
            sleepms(800),
            shootSeq,//TODO

            sleepms(550),
            execute { goTo(40.4, 67.8, 180.0) }, // collect -3
            sleepms(1200),
            execute { goTo(14.7, 67.8, 180.0) },
            execute{Joint.setPosition(Joint.COLLECT_POSITION-0.06)},
            sleepms(800),
            preCollectSeq,
            execute { goTo(8.5, 57.0, 130.0) }, // push gate
            sleepms(1100),//wait gate
            execute { Shooter.charge() },
            execute { goTo(55.0, 83.0, 180.0) },
            sleepms(300),
            execute { Joint.setPosition(Joint.COLLECT_POSITION + 0.1) },
            sleepms(600),
            afterCollectSeq,
            reverse,
            sleepms(650),
            shootSeq,


            sleepms(550),
            execute { goTo(40.4, 67.0, 180.0) }, // collect -4
            sleepms(1300),
            execute { goTo(14.7, 67.0, 180.0) },
            execute{Joint.setPosition(Joint.COLLECT_POSITION-0.06)},
            sleepms(800),
            preCollectSeq,
            execute { goTo(8.5, 57.0, 130.0) }, // push gate
            sleepms(1000),//wait gate
            execute { Shooter.charge() },
            execute { goTo(55.0, 83.0, 180.0) },
            sleepms(200),
            execute { Joint.setPosition(Joint.COLLECT_POSITION + 0.1) },
            sleepms(500),
            reverse,
            afterCollectSeq,
            sleepms(650),
            shootSeq,

            sleepms(550),
            execute { Joint.setPosition(Joint.COLLECT_POSITION) },
            execute { goTo(46.0, 82.6, 180.0) }, // pre collect -5
            preCollectSeq,
            sleepms(100),
            execute { goTo(21.0, 82.6, 180.0) }, // collect spike mark
            sleepms(500),
            afterCollectSeq,
            execute { Shooter.charge() },
            execute { goTo(55.0, 83.0, 180.0) },
            sleepms(300),
            execute{Joint.setPosition(Joint.COLLECT_POSITION+0.2)},
            sleepms(500),
            shootSeq,


            sleepms(550),
            execute { goTo(40.4, 67.0, 180.0) }, // collect -6
            sleepms(900),
            execute { goTo(14.7, 67.0, 180.0) },
            execute{Joint.setPosition(Joint.COLLECT_POSITION-0.06)},
            sleepms(800),
            preCollectSeq,
            execute { goTo(8.5, 58.5, 130.0) }, // push gate
            sleepms(1100),//wait at agte
            execute { Shooter.charge() },
            execute { goTo(55.0, 83.0, 180.0) },
            sleepms(200),
            execute { Joint.setPosition(Joint.COLLECT_POSITION + 0.1) },
            sleepms(600),
            afterCollectSeq,
            reverse,
            sleepms(650),
            shootSeq,


            sleepms(650),
            execute { goTo(40.4, 67.0, 180.0) }, // collect -7
            sleepms(1200),
            execute { goTo(14.7, 67.0, 180.0) },
            execute{Joint.setPosition(Joint.COLLECT_POSITION-0.06)},
            sleepms(800),
            preCollectSeq,
            execute { goTo(8.5, 58.5, 130.0) }, // push gate
            sleepms(1100),//wait at agte
            execute { Shooter.charge() },
            execute { goTo(55.0, 83.0, 178.0) },
            sleepms(200),
            execute { Joint.setPosition(Joint.COLLECT_POSITION + 0.1) },
            sleepms(600),
            afterCollectSeq,
            reverse,
            sleepms(300),
            shootSeq,

            sleepms(999999999)
        )
    }
}
