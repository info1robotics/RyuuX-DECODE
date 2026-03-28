package org.firstinspires.ftc.teamcode.subsystems

import com.qualcomm.robotcore.hardware.HardwareMap
import org.firstinspires.ftc.teamcode.subsystems.extra.Limelight

object Controller {
    enum class State{ UNKNOWN,
        INIT,
        VISION,
        LOCKED
    }
    lateinit var state:State

    fun init(hardwareMap: HardwareMap) {
        Drivetrain.init(hardwareMap)
        Intake.init(hardwareMap)
        Shooter.init(hardwareMap)
        Turret.init(hardwareMap)
        Hood.init(hardwareMap)
        //Limelight.init(hardwareMap,0)
        Joint.init(hardwareMap)
        Jack.init(hardwareMap)
        Wicket.init(hardwareMap)
        state = State.INIT
    }

    fun setInit()
    {
        Hood.setPositionDeg(40.0)
        Wicket.setPosition(Wicket.CLOSE_POSITION)
        Turret.setPosition(Turret.FORWARD_POSITION)
        Joint.setPosition(Joint.INIT_POSITION)
        Wicket.setPosition(Wicket.CLOSE_POSITION)
        state = State.INIT
    }

    fun setInitAuto()
    {
        Wicket.setPosition(Wicket.CLOSE_POSITION)
        Turret.setPosition(Turret.FORWARD_POSITION)
        Joint.setPosition(Joint.INIT_POSITION)
        state = State.INIT
    }

    fun setVision()
    {
        state = State.VISION
    }
    fun setLock()
    {

    }



}