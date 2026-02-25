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
class AutoCloseRedPlayoff : AutoBase(Pose(120.0,123.0, 32.0),Colours.RED) {//32 cm from tile intersection
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
            Shooter.setRPM(power)
            actionQueue.add(100)
            {
                Wicket.setPosition(Wicket.OPEN_POSITION)
                actionQueue.add(300)
                {
                    Shooter.setRPM(power)
                    Intake.setPowerMain(1.0)
                    Intake.setPowerSupport(0.9)
                    actionQueue.add(400)
                    {
                        Shooter.setRPM(power)
                        actionQueue.add(700)
                        {
                            Wicket.setPosition(Wicket.CLOSE_POSITION)
                            Shooter.setRPM(0.0)
                        }
                    }

                }
            }
        }
    )
    private val preCollectSeq = serial(
        execute{
            Joint.setPosition(Joint.COLLECT_POSITION)
            Intake.setPowerMain(1.0)
            Intake.setPowerSupport(1.0)
        }
    )
    private val afterCollectSeq = serial(
        execute{
            Intake.setPowerSupport(0.4)
            Intake.setPowerMain(1.0)
            Joint.setPosition(Joint.INIT_POSITION)
        }
    )

    override fun onInit(){
        super.onInit()
        far=false

        task = serial(
            execute{ goTo(88.0,93.0,45.0)},//preload-1
            execute{Shooter.charge(power)},
            execute{Intake.setPowerMain(0.7)},
            sleepms(1500),
            shootSeq,
            sleepms(700),


            execute{ goTo(90.0,61.8,0.0)},//pre collect -2  open gate at y 61.2
            preCollectSeq,
            sleepms(1000),
            execute{ goTo(119.9,61.8,0.0)},//collect open gate at x=119 y=61.2
            sleepms(1000),
            afterCollectSeq,
            execute{Turret.setPosition(0.73)},
            execute{Shooter.charge(power)},
            sleepms(300),
            execute{ goTo(85.0,83.0,0.0)},//shoot
            sleepms(1500),
            shootSeq,//TODO calibrate the gate position y

            sleepms(700),
            execute{ Joint.setPosition(Joint.COLLECT_POSITION) },
            execute{ goTo(103.0,61.5,0.0)},//
            sleepms(1300),
            execute{ goTo(122.6,61.5,0.0)},//collect -3
            preCollectSeq,
            sleepms(700),
            execute{ goTo(124.0,57.0,20.0)},//push gate
            sleepms(1400),//wait at gate
            execute{Shooter.charge(power)},
            execute{ goTo(85.0,83.0,0.0)},
            sleepms(300),
            execute{Joint.setPosition(Joint.COLLECT_POSITION+0.1)},
            sleepms(1200),
            shootSeq,

            sleepms(700),
            execute{ Joint.setPosition(Joint.COLLECT_POSITION) },
            execute{ goTo(103.0,61.5,0.0)},//
            sleepms(1200),
            execute{ goTo(122.6,61.5,0.0)},//collect -4
            preCollectSeq,
            sleepms(700),
            execute{ goTo(124.0,57.0,20.0)},//push gate
            sleepms(1400),//wait at gate
            execute{Shooter.charge(power)},
            execute{ goTo(85.0,83.0,0.0)},
            sleepms(300),
            execute{Joint.setPosition(Joint.COLLECT_POSITION+0.1)},
            sleepms(1200),
            shootSeq,

            sleepms(700),
            execute{ Joint.setPosition(Joint.COLLECT_POSITION) },//spike mark
            execute{ goTo(98.0,82.0,0.0)} ,//pre collect -5
            preCollectSeq,
            sleepms(500),
            execute{ goTo(120.0,82.0,0.0)},//collect
            sleepms(900),
            execute{ goTo(122.0,69.6,-10.0)},//open gate hearts
            sleepms(800),
            afterCollectSeq,
            execute{Shooter.charge(power)},
            execute{ goTo(85.0,83.0,0.0)},
            sleepms(1500),
            shootSeq,

            sleepms(700),
            execute{ Joint.setPosition(Joint.COLLECT_POSITION) },
            execute{ goTo(103.0,61.5,0.0)},//
            sleepms(1200),
            execute{ goTo(122.6,61.5,0.0)},//collect -6
            preCollectSeq,
            sleepms(700),
            execute{ goTo(124.0,57.0,20.0)},//push gate
            sleepms(1400),//wait at gate
            execute{Shooter.charge(power)},
            execute{ goTo(85.0,83.0,0.0)},
            sleepms(300),
            execute{Joint.setPosition(Joint.COLLECT_POSITION+0.1)},
            sleepms(1200),
            shootSeq,


            sleepms(999999999),






            )
    }

}
