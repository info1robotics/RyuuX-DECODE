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
class AutoCloseBluePlayoff : AutoBase(Pose(24.0, 123.0, Math.toRadians(138.0)),Colours.BLUE) {

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
            Shooter.setRPM(power)
            actionQueue.add(100) {
                Wicket.setPosition(Wicket.OPEN_POSITION)
                actionQueue.add(300) {
                    Shooter.setRPM(power)
                    Intake.setPowerMain(1.0)
                    Intake.setPowerSupport(1.0)
                    actionQueue.add(400) {
                        Shooter.setRPM(power)
                        actionQueue.add(700) {
                            Shooter.setRPM(0.0)
                            Wicket.setPosition(Wicket.CLOSE_POSITION)
                        }
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
            Intake.setPowerSupport(0.4)
            Intake.setPowerMain(1.0)
            Joint.setPosition(Joint.INIT_POSITION)
        }
    )

    override fun onInit() {
        super.onInit()
        far=false

        task = serial(
            execute { goTo(56.0, 93.0, 138.0) }, // preload-1 (144-88)
            execute { Shooter.charge(power) },
            execute { Intake.setPowerMain(0.7) },
            sleepms(1500),
            shootSeq,
            sleepms(700),

            execute { goTo(54.0, 62.3, 180.0) }, // pre collect -2
            preCollectSeq,
            sleepms(1000),
            execute { goTo(17.5, 62.3, 180.0) }, // collect
            sleepms(1000),
            execute { Turret.setPosition(0.22) },
            afterCollectSeq,
            execute { Shooter.charge(power) },
            sleepms(300),
            execute { goTo(59.0, 79.0, 180.0) },
            sleepms(1300),
            shootSeq,//TODO

            sleepms(700),
            execute{ Joint.setPosition(Joint.COLLECT_POSITION) },
            execute { goTo(40.4, 61.5, 180.0) }, // collect -3
            sleepms(1200),
            execute { goTo(16.4, 61.5, 180.0) },
            preCollectSeq,
            sleepms(700),
            execute { goTo(10.8, 56.0, 140.0) }, // push gate
            sleepms(1200),
            execute { Shooter.charge(power) },
            execute { goTo(59.0, 79.0, 180.0) },
            sleepms(300),
            execute { Joint.setPosition(Joint.COLLECT_POSITION + 0.1) },
            sleepms(1200),
            shootSeq,

            sleepms(700),
            execute{ Joint.setPosition(Joint.COLLECT_POSITION) },
            execute { goTo(40.4, 61.5, 180.0) }, // collect -4
            sleepms(1200),
            execute { goTo(16.4, 61.5, 180.0) },
            preCollectSeq,
            sleepms(700),
            execute { goTo(9.8, 56.0, 140.0) }, // push gate
            sleepms(1300),
            execute { Shooter.charge(power) },
            execute { goTo(59.0, 79.0, 180.0) },
            sleepms(750),
            execute { Joint.setPosition(Joint.COLLECT_POSITION + 0.1) },
            sleepms(750),
            shootSeq,

            sleepms(700),
            execute { Joint.setPosition(Joint.COLLECT_POSITION) },
            execute { goTo(46.0, 79.0, 180.0) }, // pre collect -5
            preCollectSeq,
            sleepms(500),
            execute { goTo(25.0, 79.0, 180.0) }, //
            sleepms(900),
            execute { goTo(16.0, 67.6, 198.0) }, // push hearts
            sleepms(800),
            afterCollectSeq,
            execute { Shooter.charge(power) },
            execute { goTo(59.0, 79.0, 180.0) },
            sleepms(1500),
            shootSeq,

            sleepms(700),
            execute{ Joint.setPosition(Joint.COLLECT_POSITION) },
            execute { goTo(40.4, 61.0, 180.0) }, // collect -6
            sleepms(1200),
            execute { goTo(16.4, 61.0, 180.0) },
            preCollectSeq,
            sleepms(700),
            execute { goTo(9.8, 56.0, 140.0) }, // push gate
            sleepms(1400),
            execute { Shooter.charge(power) },
            execute { goTo(59.0, 79.0, 180.0) },
            sleepms(300),
            execute { Joint.setPosition(Joint.COLLECT_POSITION + 0.1) },
            sleepms(1200),
            shootSeq,
            sleepms(700),
            execute { goTo(56.0, 79.0, 160.0) },



            sleepms(999999999)
        )
    }
}