package org.firstinspires.ftc.libraries;

import android.graphics.Color;

import com.disnodeteam.dogecv.CameraViewDisplay;
import com.disnodeteam.dogecv.detectors.GlyphDetector;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DistanceSensor;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.enums.Direction;
import org.firstinspires.ftc.enums.FTCAlliance;
import org.firstinspires.ftc.enums.FTCPosition;
import org.firstinspires.ftc.enums.JewelColor;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.robotcore.external.navigation.RelicRecoveryVuMark;
import org.opencv.core.Point;

/**
 * Created by megankaye on 1/3/18.
 */

public class AutonModeLibrary {
    LinearOpMode opMode;

    DrivingLibrary drivingLibrary;
    VuMarkIdentifyLibrary vuMarkIdentify;
    GlyphArmLibrary glyphArm;

    FTCAlliance alliance;
    FTCPosition position;

    Servo colorArm;
    ColorSensor jewelColorSensor;
    ColorSensor cryptoColorSensor;
    DistanceSensor cryptoDistanceSensor;

//    private GlyphDetector glyphDetector;

    boolean[][] cryptobox;

    public AutonModeLibrary(LinearOpMode opMode, FTCAlliance alliance, FTCPosition position) {
        this.alliance = alliance;
        this.position = position;
        drivingLibrary = new DrivingLibrary(opMode);
        this.drivingLibrary.setSpeed(0.75);
        glyphArm = new GlyphArmLibrary(opMode);
        this.colorArm = opMode.hardwareMap.get(Servo.class,"color_arm");
        jewelColorSensor = opMode.hardwareMap.get(ColorSensor.class, "jewelColorSensor");
        cryptoColorSensor = opMode.hardwareMap.get(ColorSensor.class, "cryptoColorSensor");
        cryptoDistanceSensor = opMode.hardwareMap.get(DistanceSensor.class, "cryptoColorSensor");
        this.opMode = opMode;
        this.vuMarkIdentify = new VuMarkIdentifyLibrary(opMode);
        this.cryptobox = new boolean[3][4];

//        glyphDetector = new GlyphDetector();
//        glyphDetector.init(opMode.hardwareMap.appContext, CameraViewDisplay.getInstance());
//        glyphDetector.enable();
    }

    //internal methods
    private JewelColor senseColor() {
        //threshold variables
        int redBThreshold = 60;
        int redTThreshold = 320;
        int blueBThreshold = 120;
        int blueTThreshold = 260;
        int waitSenseColor = 500;

        // hsvValues is an array that will hold the hue, saturation, and value information.
        float hsvValues[] = {0F, 0F, 0F};
        // convert the RGB values to HSV values.
        //SENSE COLOR
        Color.RGBToHSV(jewelColorSensor.red() * 8, jewelColorSensor.green() * 8, jewelColorSensor.blue() * 8, hsvValues);
        float hue = hsvValues[0];
        opMode.telemetry.addData("hue", hue);
        opMode.sleep(waitSenseColor);

        boolean seeingRedJewel = hue < redBThreshold || hue > redTThreshold;
        boolean seeingBlueJewel = hue > blueBThreshold && hue < blueTThreshold;

        if (seeingBlueJewel) {
            opMode.telemetry.addData("Saw:", "Blue Jewel");
            opMode.telemetry.update();
            return JewelColor.BLUE;
        }
        else if (seeingRedJewel) {
            opMode.telemetry.addData("Saw:", "Red Jewel");
            opMode.telemetry.update();
            return JewelColor.RED;
        }
        else {
            opMode.telemetry.addData("Saw:", "Unknown");
            opMode.telemetry.update();
            return JewelColor.UNKNOWN;
        }
    }

    public void callSenseColor() {
        senseColor();
    }

    private RelicRecoveryVuMark identifyPictograph() {
        //pick up
        return vuMarkIdentify.identifyPictograph(opMode);
    }

    //external methods
    public void pickUpGlyph() {
        int waitTime = 500;
        glyphArm.allArmsPreset(true, 0);
        opMode.sleep(waitTime);
        glyphArm.movePulley(true);
    }

    public Direction knockOffJewel() {
        //test variables
        int waitMoveArm = 1000;
        int waitDriveTime = 300;
        int colorArmResetPos = 1;
        double colorArmDownPos = 0.1;
        float driveSpeed = .4f;

        //other variables
        Direction dir;

        //MOVE SERVO
        colorArm.setPosition(colorArmDownPos);
        opMode.sleep(waitMoveArm);
        JewelColor color = senseColor();

        if (alliance == FTCAlliance.RED) {
            if (color == JewelColor.BLUE) {
                drivingLibrary.driveStraight(0, driveSpeed);
                dir = Direction.FORWARD;
            }
            else if (color == JewelColor.RED) {
                drivingLibrary.driveStraight(0, -driveSpeed);
                dir = Direction.BACKWARD;
            }
            else {
                colorArm.setPosition(colorArmResetPos);
                opMode.sleep(waitMoveArm);
                drivingLibrary.driveStraight(0, driveSpeed);
                dir = Direction.FORWARD;
            }
        } else {
            if (color == JewelColor.RED) {
                drivingLibrary.driveStraight(0, driveSpeed);
                dir = Direction.FORWARD;
            }
            else if (color == JewelColor.BLUE){
                drivingLibrary.driveStraight(0, -driveSpeed);
                dir = Direction.BACKWARD;
            }
            else {
                colorArm.setPosition(colorArmResetPos);
                opMode.sleep(waitMoveArm);
                drivingLibrary.driveStraight(0, driveSpeed);
                dir = Direction.FORWARD;
            }
        }
        opMode.sleep(waitDriveTime);
        colorArm.setPosition(colorArmResetPos);
        opMode.telemetry.update();
        return dir;
    }

    //TODO: fix lol
    public int glyptograph(Direction dir) {
        float driveSpeedStone = .6f;
        float driveSpeedStrafe = 1f;
        float driveSpeed = .4f;

        int waitShort = 500;
        int waitMed = 1000;
        int waitLong = 2500;
        int waitSensePictograph = 500;

        driveSpeed = .5f;
        int drive1 = 850;
        int drive2 = 500;
        int drive3 = 500;
        int drive4 = 500;
        int drive5 = 500;
        int drive6 = 300;

        drivingLibrary.driveStraight(0,driveSpeed);
        opMode.sleep(drive1);
        drivingLibrary.driveStraight(-driveSpeed,0);
        opMode.sleep(drive2);
        drivingLibrary.turnRight(Math.PI / 2);
       /* drivingLibrary.turn(driveSpeed,0);
        opMode.sleep(drive3);
        drivingLibrary.driveStraight (driveSpeed,0);
        opMode.sleep(drive4);
        drivingLibrary.turn(driveSpeed,0);
        opMode.sleep(drive5);
        drivingLibrary.driveStraight(0,-driveSpeed);
        opMode.sleep(drive6);*/
        drivingLibrary.brakeStop();

        //red left forward
        /*
        if (position == FTCPosition.LEFT) {
            if (dir == Direction.BACKWARD) {
                if (alliance == FTCAlliance.RED) {
                    //if red, on left side, and went backwards
                    drivingLibrary.driveStraight(0, driveSpeedStone);
                    opMode.sleep(2500);
                    drivingLibrary.driveStraight(driveSpeedStrafe, 0);
                    opMode.sleep(250);
                } else {
                    //if blue, on left side, and went backwards
                    drivingLibrary.driveStraight(0, -driveSpeed);
                    opMode.sleep(500);
                    drivingLibrary.driveStraight(-driveSpeedStrafe, 0);
                    opMode.sleep(1000);
                    drivingLibrary.driveStraight(0, -driveSpeed);
                    opMode.sleep(500);
                }
            } else {
                //TODO: LOOK ATTHIS ONE MEGAN
                if (alliance == FTCAlliance.RED) {
                    //if red, on left side, and went forwards

                }
                else {
                    //if blue, on left side, and went forwards
                    drivingLibrary.driveStraight(0, -driveSpeedStone);
                    opMode.sleep(1250);
                    drivingLibrary.driveStraight(-driveSpeedStrafe, 0);
                    opMode.sleep(1000);
                    drivingLibrary.driveStraight(0, -driveSpeed);
                    opMode.sleep(800);
                }
            }

        } else {
            if (dir == Direction.BACKWARD) {
                if (alliance == FTCAlliance.RED) {
                    //if red, on right side, and went backwards
                    drivingLibrary.driveStraight(0, driveSpeedStone);
                    opMode.sleep(2000);
                    drivingLibrary.driveStraight(-driveSpeedStrafe, 0);
                    opMode.sleep(1000);
                    drivingLibrary.driveStraight(0, driveSpeed);
                    opMode.sleep(500);
                } else {
                    //if blue, on right side, and went backwards
                    drivingLibrary.driveStraight(0, -driveSpeed);
                    opMode.sleep(750);
                    drivingLibrary.driveStraight(driveSpeedStrafe, 0);
                    opMode.sleep(250);
                }
            } else {
                if (alliance == FTCAlliance.RED) {
                    //if red, on right side, and went forwards
                    drivingLibrary.driveStraight(0, driveSpeed);
                    opMode.sleep(800);
                    drivingLibrary.driveStraight(-driveSpeedStrafe, 0);
                    opMode.sleep(1000);
                    drivingLibrary.driveStraight(0, driveSpeed);
                    opMode.sleep(300);
                } else {
                    //if blue, on right side, and went forwards
                    drivingLibrary.driveStraight(0, -driveSpeedStone);
                    opMode.sleep(2500);
                    drivingLibrary.driveStraight(driveSpeedStrafe, 0);
                    opMode.sleep(250);
                }
            }
        }*/
        //use vuforia to identify
        RelicRecoveryVuMark vuMark = identifyPictograph();
        opMode.sleep(waitSensePictograph);


        int count;
        if (vuMark == RelicRecoveryVuMark.RIGHT) {
            count = 1;
        } else if (vuMark == RelicRecoveryVuMark.CENTER) {
            count = 2;
        }
        else if (vuMark == RelicRecoveryVuMark.LEFT) {
            count = 3;
        } else {
            count = 0                                                                              ;
        }
        opMode.telemetry.addData("count", count);
        return count;
    }

    public void placeGlyphs(int count) {
        //turn a little
        drivingLibrary.turnRight(0.2617993878);
        //strafe slowly
        drivingLibrary.driveStraight(-.3f,0);
        opMode.sleep(1000);
        while (count != 0) {
            double dist = cryptoDistanceSensor.getDistance(DistanceUnit.CM);
            if (dist != java.lang.Double.NaN) {
                count -= 1;
            }
            opMode.telemetry.addData("Distance", dist);
            opMode.telemetry.update();
        }
        drivingLibrary.brakeStop();

        /*

        //put glyph in
        drivingLibrary.driveStraight(0,.5f);
        dropGlyph();
        opMode.sleep(300);
        drivingLibrary.brakeStop();

        //turns around 180 to face glyph pit
        drivingLibrary.turnRight(Math.PI);
        opMode.sleep(1000);
        drivingLibrary.brakeStop();*/
    }

    //sensing
//    public void getGlyphs() {
//        vuMarkIdentify.closeVuforia();
//        if (glyphDetector.isFoundRect()) {
//            double offset = glyphDetector.getChosenGlyphOffset();
//            Point pos = glyphDetector.getChosenGlyphPosition();
//
//            opMode.telemetry.addData("Offset", offset);
//            opMode.telemetry.addData("Pos X", pos.x);
//            opMode.telemetry.addData("Pos Y", pos.y);
//            opMode.telemetry.addData("Glyph Status", "Available");
//        }
//
//        opMode.telemetry.addData("Offset", "0");
//        opMode.telemetry.addData("Pos X", "0");
//        opMode.telemetry.addData("Pos Y", "0");
//        opMode.telemetry.addData("Glyph Status", "Unavailable");
//        opMode.telemetry.update();
//    }

    public void dropGlyph() {
        int waitTime = 500;
        //put down
        glyphArm.movePulley(false);
        opMode.sleep(waitTime);
        glyphArm.allArmsPreset(false, .6f);

    }

    //EXCLUSIVELY for Jewel Only Run
    public void driveToSafeZone(Direction dir) {
        float driveSpeedStone = .6f;
        float driveSpeedStrafe = 1f;
        float driveSpeed = .4f;

        int waitShort = 500;
        int waitMed = 1000;
        int waitLong = 2500;


        if (position == FTCPosition.LEFT) {
            if (dir == Direction.BACKWARD) {
                if (alliance == FTCAlliance.RED) {
                    //if red, on left side, and went backwards
                    drivingLibrary.driveStraight(0, driveSpeedStone);
                    opMode.sleep(2500);
                    drivingLibrary.driveStraight(driveSpeedStrafe, 0);
                    opMode.sleep(250);
                } else {
                    //if blue, on left side, and went backwards
                    drivingLibrary.driveStraight(0, -driveSpeed);
                    opMode.sleep(500);
                    drivingLibrary.driveStraight(-driveSpeedStrafe, 0);
                    opMode.sleep(1000);
                    drivingLibrary.driveStraight(0, -driveSpeed);
                    opMode.sleep(500);
                }
            } else {
                if (alliance == FTCAlliance.RED) {
                    //if red, on left side, and went forwards
                    drivingLibrary.driveStraight(0, driveSpeed);
                    opMode.sleep(750);
                    drivingLibrary.driveStraight(driveSpeedStrafe, 0);
                    opMode.sleep(250);
                }
                else {
                    //if blue, on left side, and went forwards
                    drivingLibrary.driveStraight(0, -driveSpeedStone);
                    opMode.sleep(1250);
                    drivingLibrary.driveStraight(-driveSpeedStrafe, 0);
                    opMode.sleep(1000);
                    drivingLibrary.driveStraight(0, -driveSpeed);
                    opMode.sleep(800);
                }
            }

        } else {
            if (dir == Direction.BACKWARD) {
                if (alliance == FTCAlliance.RED) {
                    //if red, on right side, and went backwards
                    drivingLibrary.driveStraight(0, driveSpeedStone);
                    opMode.sleep(2000);
                    drivingLibrary.driveStraight(-driveSpeedStrafe, 0);
                    opMode.sleep(1000);
                    drivingLibrary.driveStraight(0, driveSpeed);
                    opMode.sleep(500);
                } else {
                    //if blue, on right side, and went backwards
                    drivingLibrary.driveStraight(0, -driveSpeed);
                    opMode.sleep(750);
                    drivingLibrary.driveStraight(driveSpeedStrafe, 0);
                    opMode.sleep(250);
                }
            } else {
                if (alliance == FTCAlliance.RED) {
                    //if red, on right side, and went forwards
                    drivingLibrary.driveStraight(0, driveSpeed);
                    opMode.sleep(800);
                    drivingLibrary.driveStraight(-driveSpeedStrafe, 0);
                    opMode.sleep(1000);
                    drivingLibrary.driveStraight(0, driveSpeed);
                    opMode.sleep(300);
                } else {
                    //if blue, on right side, and went forwards
                    drivingLibrary.driveStraight(0, -driveSpeedStone);
                    opMode.sleep(2500);
                    drivingLibrary.driveStraight(driveSpeedStrafe, 0);
                    opMode.sleep(250);
                }
            }
        }

        opMode.telemetry.update();
        drivingLibrary.stopDrivingMotors();
    }

}
