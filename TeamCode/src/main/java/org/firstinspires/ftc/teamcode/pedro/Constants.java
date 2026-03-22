package org.firstinspires.ftc.teamcode.pedro;
import com.pedropathing.control.FilteredPIDFCoefficients;
import com.pedropathing.control.PIDFCoefficients;
import com.pedropathing.control.PredictiveBrakingCoefficients;
import com.pedropathing.follower.Follower;
import com.pedropathing.follower.FollowerConstants;
import com.pedropathing.ftc.FollowerBuilder;
import com.pedropathing.ftc.drivetrains.MecanumConstants;
import com.pedropathing.ftc.localization.constants.PinpointConstants;
import com.pedropathing.paths.PathConstraints;
import com.qualcomm.hardware.gobilda.GoBildaPinpointDriver;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

public class Constants {
    public static FollowerConstants followerConstants = new FollowerConstants()
            .forwardZeroPowerAcceleration(-33.03929157557946)
            .lateralZeroPowerAcceleration(-51.9875361599866)
            .translationalPIDFCoefficients(new PIDFCoefficients(0.3, 0, 0.025, 0))
            .headingPIDFCoefficients(new PIDFCoefficients(2,0,0.1,0))
            .drivePIDFCoefficients(new FilteredPIDFCoefficients(0.011,0,0.0008,0.6,0))
            .centripetalScaling(0.0005)
            .forwardZeroPowerAcceleration(-33.03929157557946)
            .lateralZeroPowerAcceleration(-51.9875361599866)
            .mass(11.2)
            /*
            .headingPIDFCoefficients(new PIDFCoefficients(2,0,0.1,0))
            .predictiveBrakingCoefficients(new PredictiveBrakingCoefficients(0.08, 0.14775377529115588, 0.0012604480375906929))
            .centripetalScaling(0)

             */
            ;
    public static MecanumConstants driveConstants = new MecanumConstants()
            .maxPower(1)
            .rightFrontMotorName("rightFront")
            .rightRearMotorName("rightRear")
            .leftRearMotorName("leftRear")
            .leftFrontMotorName("leftFront")
            .leftFrontMotorDirection(DcMotorSimple.Direction.REVERSE)
            .leftRearMotorDirection(DcMotorSimple.Direction.REVERSE)
            .rightFrontMotorDirection(DcMotorSimple.Direction.FORWARD)
            .rightRearMotorDirection(DcMotorSimple.Direction.FORWARD)
            .xVelocity(94.1617998738927)
            .yVelocity(78.28762799353225)

            ;
    public static PinpointConstants localizerConstants = new PinpointConstants()
            .forwardPodY(70.317)
            .strafePodX(-84.828)
            .distanceUnit(DistanceUnit.MM)
            .hardwareMapName("pinpoint")
            .encoderResolution(GoBildaPinpointDriver.GoBildaOdometryPods.goBILDA_4_BAR_POD)
            .forwardEncoderDirection(GoBildaPinpointDriver.EncoderDirection.FORWARD)
            .strafeEncoderDirection(GoBildaPinpointDriver.EncoderDirection.REVERSED);

    public static PathConstraints pathConstraints = new PathConstraints(0.99, 100, 0.7, 1.1);

    public static Follower createFollower(HardwareMap hardwareMap) {
        return new FollowerBuilder(followerConstants, hardwareMap)
                .pinpointLocalizer(localizerConstants)
                .mecanumDrivetrain(driveConstants)
                .pathConstraints(pathConstraints)

                .build();
    }

}
