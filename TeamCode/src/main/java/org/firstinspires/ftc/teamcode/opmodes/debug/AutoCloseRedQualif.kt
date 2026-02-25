package org.firstinspires.ftc.teamcode.opmodes.debug

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
class AutoCloseRedQualif : AutoBase(Pose(120.0,123.0, 32.0),Colours.RED) {//32 cm from tile intersection
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
            Joint.setPosition(Joint.INIT_POSITION)
            Intake.setPowerMain(0.5)
            Intake.setPowerSupport(0.2)
        }
    )

    override fun onInit(){
        super.onInit()
        far=false

        task = serial(
            execute{ goTo(88.0,93.0,45.0)},//preload-1
            execute{Shooter.charge()},
            execute{Intake.setPowerMain(0.8)},
            sleepms(800),
            shootSeq,
            sleepms(700),

            execute{ goTo(90.0,62.7,0.0)},//pre collect -2  open gate at y 61.2
            preCollectSeq,
            sleepms(1000),
            execute{Turret.setPosition(0.77)},
            execute{ goTo(118.0,62.7,0.0)},//collectopen gate at x=119 y=61.2
            sleepms(800),
            afterCollectSeq,
            //execute{Turret.setPosition(0.75)},
            execute{Shooter.charge()},
            sleepms(300),
            execute{ goTo(85.0,83.0,0.0)},//shoot
            sleepms(300),
            execute{Joint.setPosition(Joint.COLLECT_POSITION+0.2)},
            sleepms(900),
            shootSeq,//TODO calibrate the gate position y


            sleepms(700),
            execute{ goTo(103.0,65.5,0.0)},//
            sleepms(1200),
            execute{ goTo(120.7,65.5,0.0)},//collect -3
            execute{Joint.setPosition(Joint.COLLECT_POSITION-0.06)},
            sleepms(680),
            preCollectSeq,
            execute{ goTo(124.5,56.5,30.0)},//push gate
            sleepms(1100),//wait at gate
            execute{Shooter.charge()},
            execute{ goTo(85.0,83.0,0.0)},
            sleepms(300),
            execute{Joint.setPosition(Joint.COLLECT_POSITION+0.2)},
            sleepms(800),
            shootSeq,

            sleepms(700),
            execute{ goTo(103.0,65.5,0.0)},//
            sleepms(1300),
            execute{ goTo(120.7,65.5,0.0)},//collect -4
            execute{Joint.setPosition(Joint.COLLECT_POSITION-0.06)},
            sleepms(680),
            preCollectSeq,
            execute{ goTo(125.5,56.5,30.0)},//push gate
            sleepms(1100),//wait at gate
            execute{Shooter.charge()},
            execute{ goTo(85.0,83.0,0.0)},
            sleepms(200),
            execute{Joint.setPosition(Joint.COLLECT_POSITION+0.2)},
            sleepms(1000),
            shootSeq,

            sleepms(700),
            execute{ Joint.setPosition(Joint.COLLECT_POSITION) },//spike mark
            execute{ goTo(98.0,82.0,0.0)} ,//pre collect -5
            preCollectSeq,
            sleepms(300),
            execute{ goTo(119.0,82.0,0.0)},//collect
            sleepms(650),
            afterCollectSeq,
            execute{Shooter.charge()},
            execute{ goTo(85.0,83.0,0.0)},
            sleepms(300),
            execute{Joint.setPosition(Joint.COLLECT_POSITION+0.2)},
            sleepms(900),
            shootSeq,

            sleepms(700),
            execute{ Joint.setPosition(Joint.COLLECT_POSITION) },
            execute{ goTo(103.0,65.5,0.0)},//
            sleepms(1200),
            execute{ goTo(120.7,65.5,0.0)},//collect -6
            execute{Joint.setPosition(Joint.COLLECT_POSITION-0.06)},
            sleepms(680),
            preCollectSeq,
            execute{ goTo(125.5,56.5,30.0)},//push gate
            sleepms(1100),//wait at gate
            execute{Shooter.charge()},
            execute{ goTo(85.0,83.0,0.0)},
            sleepms(200),
            execute{Joint.setPosition(Joint.COLLECT_POSITION+0.2)},
            sleepms(1000),
            shootSeq,


            sleepms(700),
            execute { goTo(95.0, 37.5, 0.0) }, // last spike mark -7
            preCollectSeq,
            sleepms(1300),
            execute { goTo(122.0, 37.5, 0.0) },//collected
            sleepms(700),
            execute{ goTo(85.0,83.0,0.0)},
            sleepms(200),
            execute{Joint.setPosition(Joint.COLLECT_POSITION+0.2)},
            sleepms(700),
            afterCollectSeq,
            execute{Shooter.charge()},
            sleepms(700),
            shootSeq,

            sleepms(600),
            execute{ goTo(98.0,70.0,0.0)},


            sleepms(999999999),






            )
    }

}
