package org.firstinspires.ftc.teamcode.opmodes.auto

import com.pedropathing.geometry.Pose
import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import com.qualcomm.robotcore.eventloop.opmode.Disabled
import org.firstinspires.ftc.teamcode.enums.Colours
import org.firstinspires.ftc.teamcode.opmodes.AutoBase
import org.firstinspires.ftc.teamcode.subsystems.Intake
import org.firstinspires.ftc.teamcode.subsystems.Joint
import org.firstinspires.ftc.teamcode.subsystems.Shooter
import org.firstinspires.ftc.teamcode.subsystems.Turret
import org.firstinspires.ftc.teamcode.subsystems.Wicket
import org.firstinspires.ftc.teamcode.tasks.TaskBuilder.execute
import org.firstinspires.ftc.teamcode.tasks.TaskBuilder.serial
import org.firstinspires.ftc.teamcode.tasks.TaskBuilder.sleepms
@Autonomous
class AutoCloseRed21 : AutoBase(Pose(120.0,123.0, 32.0),Colours.RED) {//32 cm from tile intersection
var offset =45.0//TODO tune, this is local doesn t affect other classes
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
        execute{
            Joint.setPosition(Joint.COLLECT_POSITION)
            Intake.setPowerMain(1.0)
            Intake.setPowerSupport(0.9)
        }
    )
    private val afterCollectSeq = serial(
        execute{
            Intake.setPowerMain(0.5)
            Intake.setPowerSupport(0.2)
        }
    )
    private val reverse = serial(
        execute{
            Intake.setPowerSupport(-0.5)
            actionQueue.add(200)
            {
                Intake.setPowerSupport(0.0)
            }
        }
    )

    override fun onInit(){
        super.onInit()
        far=false

        task = serial(
            execute{ goTo(88.0,93.0,45.0)},//preload-1
            execute{Shooter.charge()},
            execute{Intake.setPowerMain(0.8)},
            sleepms(850),
            shootSeq,
            sleepms(600),

            execute{ goTo(90.0,63.3,0.0)},//pre collect -2  open gate at y 61.2
            preCollectSeq,
            sleepms(1150),
            execute{Turret.setPosition(0.319)},
            execute{ goTo(112.5,63.3,0.0)},//collectopen gate at x=118 y=61.2
            sleepms(500),
            afterCollectSeq,
            //execute{Turret.setPosition(0.75)},
            execute{Shooter.charge()},
            sleepms(150),
            execute{ goTo(82.0,83.0,0.0)},//shoot
            sleepms(200),
            execute{Joint.setPosition(Joint.COLLECT_POSITION+0.2)},
            sleepms(950),
            shootSeq,//TODO calibrate the gate position y

            sleepms(550),
            execute{ goTo(103.0,59.3,20.0)},//
            sleepms(1200),
            execute{ goTo(123.5,59.3,20.0)},//collect 3
            preCollectSeq,
            sleepms(1500),
            execute{Shooter.charge()},
            execute{ goTo(82.0,83.0,0.0)},
            sleepms(200),
            execute{Joint.setPosition(Joint.COLLECT_POSITION+0.2)},
            sleepms(500),
            afterCollectSeq,
            reverse,
            sleepms(800),
            shootSeq,

            sleepms(600),
            execute{ goTo(103.0,59.3,20.0)},//
            sleepms(1300),//wait for flow
            execute{ goTo(123.5,59.3,20.0)},//collect -4
            preCollectSeq,
            sleepms(1800),//wait at gate
            execute{Shooter.charge()},
            execute{ goTo(82.0,83.0,0.0)},
            sleepms(200),
            execute{Joint.setPosition(Joint.COLLECT_POSITION+0.2)},
            sleepms(500),
            afterCollectSeq,
            reverse,
            sleepms(800),
            shootSeq,

            sleepms(600),
            execute{ Joint.setPosition(Joint.COLLECT_POSITION) },//spike mark
            execute{ goTo(98.0,85.5,0.0)} ,//pre collect -5
            preCollectSeq,
            sleepms(200),
            execute{ goTo(114.0,85.5,0.0)},//collect
            sleepms(650),
            afterCollectSeq,
            execute{Shooter.charge()},
            execute{ goTo(82.0,83.0,0.0)},
            sleepms(300),
            execute{Joint.setPosition(Joint.COLLECT_POSITION+0.2)},
            sleepms(700),
            shootSeq,

            sleepms(600),
            execute{ Joint.setPosition(Joint.COLLECT_POSITION) },
            execute{ goTo(103.0,59.3,20.0)},//
            sleepms(1300),
            execute{ goTo(123.5,59.3,20.0)},//collect -6
            preCollectSeq,
            sleepms(1600),//wait at gate
            execute{Shooter.charge()},
            execute{ goTo(82.0,83.0,0.0)},
            sleepms(200),
            execute{Joint.setPosition(Joint.COLLECT_POSITION+0.2)},
            sleepms(500),
            afterCollectSeq,
            reverse,
            sleepms(750),
            shootSeq,


            sleepms(600),
            execute { goTo(93.0, 37.8, 0.0) }, // last spike mark -7
            preCollectSeq,
            sleepms(1200),
            execute { goTo(122.0, 37.8, 0.0) },//collected
            sleepms(700),
            execute{ goTo(82.0,83.0,0.0)},
            sleepms(200),
            execute{Joint.setPosition(Joint.COLLECT_POSITION+0.2)},
            sleepms(700),
            afterCollectSeq,
            execute{Shooter.charge()},
            sleepms(700),
            shootSeq,

            sleepms(450),
            execute{ goTo(118.0,83.0,-5.0)},


            sleepms(999999999),

            )
    }

}
