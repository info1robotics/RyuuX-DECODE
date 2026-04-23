package org.firstinspires.ftc.teamcode.opmodes

import com.pedropathing.geometry.Pose
import com.qualcomm.robotcore.eventloop.opmode.Autonomous
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
class AutoCloseBlue21 : AutoBase(Pose(26.0, 121.0, Math.toRadians(138.0)),Colours.BLUE) {
    var offset =10.0//TODO tune, this is local doesn t affect other classes

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
            execute { goTo(55.0, 89.0, 138.0) }, // preload-1 (144-88)
            execute { Shooter.charge() },
            execute { Intake.setPowerMain(0.8) },
            sleepms(900),
            shootSeq,

            sleepms(600),
            execute { goTo(45.0, 62.2, 180.0) }, // pre collect -2
            preCollectSeq,
            sleepms(800),
            execute{Turret.setPosition(0.62)},
            execute { goTo(26.0, 62.2, 180.0)},
            sleepms(500),
            afterCollectSeq,
            execute { Shooter.charge() },
            sleepms(150),
            execute { goTo(59.0, 74.0, 180.0) },
            sleepms(200),
            execute{Joint.setPosition(Joint.COLLECT_POSITION+0.2)},
            sleepms(1000),
            shootSeq,//TODO

            sleepms(700),
            execute{ Joint.setPosition(Joint.COLLECT_POSITION) },//gate 3
            execute { goTo(12.0, 60.65, 160.0) },
            preCollectSeq,
            sleepms(2340),
            execute { Shooter.charge() },
            execute { goTo(59.0, 74.0, 180.0)},

            sleepms(200),
            execute { Joint.setPosition(Joint.COLLECT_POSITION + 0.2) },
            sleepms(500),
            afterCollectSeq,
            reverse,
            sleepms(800),
            shootSeq,

            sleepms(500),
            execute{ Joint.setPosition(Joint.COLLECT_POSITION) },//gate 4
            execute { goTo(12.0, 60.3, 160.0) },
            preCollectSeq,
            sleepms(3100),
            execute { Shooter.charge() },
            execute { goTo(59.0, 74.0, 180.0) },
            sleepms(200),
            execute { Joint.setPosition(Joint.COLLECT_POSITION + 0.2) },
            sleepms(500),
            afterCollectSeq,
            reverse,
            sleepms(800),
            shootSeq,


            sleepms(500),
            execute{ Joint.setPosition(Joint.COLLECT_POSITION) },//gate 5
            execute { goTo(12.0, 60.4, 160.0) },
            preCollectSeq,
            sleepms(3100),
            execute { Shooter.charge() },
            execute { goTo(59.0, 74.0, 180.0)},
            execute { Joint.setPosition(Joint.COLLECT_POSITION + 0.2) },
            sleepms(700),
            afterCollectSeq,
            reverse,
            sleepms(800),
            shootSeq,

            sleepms(700),
            execute { goTo(44.0, 36.5, 180.0) }, // last spike mark -6
            preCollectSeq,
            sleepms(1300),
            execute { goTo(22.0, 36.5, 180.0) },//collected
            sleepms(700),
            execute { goTo(55.0, 83.0, 180.0) },
            sleepms(200),
            execute{Joint.setPosition(Joint.COLLECT_POSITION+0.2)},
            sleepms(700),
            afterCollectSeq,
            execute{Shooter.charge()},
            sleepms(900),

            shootSeq,
            sleepms(600),
            execute { goTo(16.0, 83.0, 180.0) },
            sleepms(400),
            execute { Joint.setPosition(Joint.COLLECT_POSITION) },
            preCollectSeq,
            sleepms(400),
            execute { Shooter.charge() },
            execute { goTo(59.0, 74.0, 180.0)},

            sleepms(500),
            execute{Joint.setPosition(Joint.COLLECT_POSITION+0.2)},
            sleepms(700),
            shootSeq,
            sleepms(600),
            execute { goTo(40.0, 74.0, 180.0) },


            sleepms(999999999)
        )
    }
}
