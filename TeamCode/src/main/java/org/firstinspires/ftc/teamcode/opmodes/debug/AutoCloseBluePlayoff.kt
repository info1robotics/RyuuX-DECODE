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
@Disabled
@Autonomous
class AutoCloseBluePlayoff : AutoBase(Pose(26.0, 121.0, Math.toRadians(138.0)),Colours.BLUE) {
    var offset =15.0//TODO tune, this is local doesn t affect other classes

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
            Intake.setPowerMain(0.3)
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
            execute { goTo(56.0, 93.0, 140.0) }, // preload-1 (144-88)
            execute { Shooter.charge() },
            execute { Intake.setPowerMain(0.8) },
            execute { Turret.setPosition(0.48)},

            sleepms(850),
            shootSeq,
            sleepms(610),

            execute { goTo(45.0, 62.2, 180.0) }, // pre collect -2
            preCollectSeq,
            sleepms(800),
            execute{Turret.setPosition(0.62)},
            execute { goTo(26.0, 62.2, 180.0) }, // collect
            sleepms(600),
            afterCollectSeq,
            execute { Shooter.charge() },
            sleepms(200),
            execute { goTo(54.0, 79.0, 180.0) },
            sleepms(300),
            execute{Joint.setPosition(Joint.COLLECT_POSITION+0.2)},
            sleepms(850),
            shootSeq,//TODO

            sleepms(550),
            execute { goTo(26.4, 61.0, 160.0) }, // collect -3 gate
            sleepms(1300),
            execute { goTo(7.0, 61.0, 160.0) },
            preCollectSeq,
            sleepms(1450),
            execute { Shooter.charge() },
            execute { goTo(54.0, 79.0, 180.0) },
            sleepms(200),
            execute { Joint.setPosition(Joint.COLLECT_POSITION + 0.2) },
            sleepms(500),
            afterCollectSeq,
            reverse,
            sleepms(850),
            shootSeq,

            sleepms(550),
            execute { goTo(26.4, 60.8, 160.0) }, // collect -4 gate
            sleepms(1300),
            execute { goTo(7.0, 60.8, 160.0) },
            preCollectSeq,
            sleepms(1300),
            execute { Shooter.charge() },
            execute { goTo(54.0, 79.0, 180.0) },
            sleepms(200),
            execute { Joint.setPosition(Joint.COLLECT_POSITION + 0.2) },
            sleepms(500),
            afterCollectSeq,
            reverse,
            sleepms(850),
            shootSeq,

            sleepms(550),
            execute { Joint.setPosition(Joint.COLLECT_POSITION) },
            execute { goTo(46.0, 82.6, 180.0) }, // pre collect -5
            preCollectSeq,
            sleepms(100),
            execute { goTo(20.0, 82.6, 180.0) }, // collect spike mark
            sleepms(600),
            execute { Shooter.charge() },
            execute { goTo(54.0, 79.0, 180.0) },
            sleepms(500),
            execute{Joint.setPosition(Joint.COLLECT_POSITION+0.2)},
            sleepms(850),
            shootSeq,


            sleepms(550),
            execute { goTo(26.4, 60.0, 160.0) }, // collect -6 gate
            sleepms(1300),
            execute { goTo(7.0, 60.0, 160.0) },
            preCollectSeq,
            sleepms(1300),
            execute { Shooter.charge() },
            execute { goTo(54.0, 79.0, 180.0) },
            sleepms(200),
            execute { Joint.setPosition(Joint.COLLECT_POSITION + 0.2) },
            sleepms(500),
            afterCollectSeq,
            reverse,
            sleepms(850),
            shootSeq,

            sleepms(550),
            execute { goTo(26.4, 60.0, 160.0) }, // collect -7 gate
            sleepms(1300),
            execute { goTo(7.0, 60.0, 160.0) },
            preCollectSeq,
            sleepms(1300),
            execute { Shooter.charge() },
            execute { goTo(54.0, 79.0, 180.0) },
            sleepms(200),
            execute { Joint.setPosition(Joint.COLLECT_POSITION + 0.2) },
            sleepms(500),
            afterCollectSeq,
            reverse,
            sleepms(850),
            shootSeq,

            sleepms(500),
            execute { goTo(36.0, 83.0, 180.0) },


            //TODO add the 7ht cycle

            sleepms(999999999)
        )
    }
}
