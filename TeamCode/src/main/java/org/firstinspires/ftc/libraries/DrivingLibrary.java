package org.firstinspires.ftc.libraries;

/**
 * Created by lamanwyner on 12/29/17.
 */

import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.hardware.bosch.JustLoggingAccelerationIntegrator;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import org.firstinspires.ftc.robotcore.external.navigation.Acceleration;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder;
import org.firstinspires.ftc.robotcore.external.navigation.AxesReference;
import org.firstinspires.ftc.robotcore.external.navigation.Orientation;
import org.opencv.core.Mat;

public class DrivingLibrary {
    // hardware variables
    private DcMotor leftFront;
    private DcMotor rightFront;
    private DcMotor leftRear;
    private DcMotor rightRear;
    private DcMotor[] allMotors;
    private HardwareMap hardwareMap;

    // sensor variables
    private BNO055IMU imu;
    private Orientation angles;
    private Acceleration gravity;

    // other variables
    private double speedSetting;

    public DrivingLibrary(OpMode opMode) {
        hardwareMap = opMode.hardwareMap;

        leftFront = hardwareMap.get(DcMotor.class, "left_front");
        rightFront = hardwareMap.get(DcMotor.class, "right_front");
        leftRear = hardwareMap.get(DcMotor.class, "left_rear");
        rightRear = hardwareMap.get(DcMotor.class, "right_rear");

        rightRear.setDirection(DcMotor.Direction.REVERSE);
        rightFront.setDirection(DcMotor.Direction.REVERSE);

        imu = hardwareMap.get(BNO055IMU.class, "imu");
        BNO055IMU.Parameters parameters = new BNO055IMU.Parameters();
        parameters.angleUnit = BNO055IMU.AngleUnit.RADIANS;
        parameters.accelUnit = BNO055IMU.AccelUnit.METERS_PERSEC_PERSEC;
        parameters.calibrationDataFile = "BNO055IMUCalibration.json";
        parameters.loggingEnabled      = true;
        parameters.loggingTag          = "IMU";
        parameters.accelerationIntegrationAlgorithm = new JustLoggingAccelerationIntegrator();
        imu.initialize(parameters);

        allMotors = new DcMotor[] {leftFront, rightFront, leftRear, rightRear};
    }

    public void driveStraight(float x, float y) {
        float maxSpeed = Math.max(y + x, y - x);
        double multiplier = 1;

        if (maxSpeed > 1) {
            multiplier = 1 / maxSpeed;
        }

        leftFront.setPower(multiplier * speedSetting * (y + x));
        rightFront.setPower(multiplier * speedSetting * (y - x));
        leftRear.setPower(multiplier * speedSetting * (y - x));
        rightRear.setPower(multiplier * speedSetting * (y + x));
    }

    public void turn(float x, float y) {
        float maxSpeed = Math.max(y + x, y - x);
        double multiplier = 1;

        if (maxSpeed > 1) {
            multiplier = 1 / maxSpeed;
        }

        leftFront.setPower((y + x) * speedSetting * multiplier);
        leftRear.setPower((y + x) * speedSetting * multiplier);
        rightFront.setPower((y - x) * speedSetting * multiplier);
        rightRear.setPower((y - x) * speedSetting * multiplier);
    }

    public void turnRight(double radians) {
        double currentYaw = imu.getAngularOrientation(AxesReference.INTRINSIC,
                AxesOrder.ZYX, AngleUnit.RADIANS).firstAngle;
        double targetYaw = currentYaw + radians;
        double subtractYaw = 0;

        if (targetYaw > 2 * Math.PI) {
            targetYaw -= (2 * Math.PI);
            subtractYaw = 2 * Math.PI;
        }

        while (currentYaw - subtractYaw < targetYaw) {
            turn(1, 0);
            currentYaw = imu.getAngularOrientation(AxesReference.INTRINSIC,
                    AxesOrder.ZYX, AngleUnit.RADIANS).firstAngle;
        }

        stopDrivingMotors();
    }

    public void turnLeft(double radians) {
        double currentYaw = imu.getAngularOrientation(AxesReference.INTRINSIC,
                AxesOrder.ZYX, AngleUnit.RADIANS).firstAngle;
        double targetYaw = currentYaw - radians;
        double addYaw = 0;

        if (targetYaw < 0) {
            targetYaw += (2 * Math.PI);
            addYaw = 2 * Math.PI;
        }

        while (currentYaw + addYaw > targetYaw) {
            turn(-1, 0);
            currentYaw = imu.getAngularOrientation(AxesReference.INTRINSIC,
                    AxesOrder.ZYX, AngleUnit.RADIANS).firstAngle;
        }

        stopDrivingMotors();
    }

    public void setSpeed(double speed) {
        speedSetting = speed;
    }

    public void setAllMotors(double speed) {
        for (DcMotor motor : allMotors) {
            motor.setPower(speed);
        }
    }

    public void stopDrivingMotors() {
        for (DcMotor motor : allMotors) {
            motor.setPower(0);
        }
    }

    public void resetEncoders() {
        for (DcMotor motor : allMotors) {
            if (motor != null) motor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        }
    }
}
