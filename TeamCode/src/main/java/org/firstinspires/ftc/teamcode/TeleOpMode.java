package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.enums.DrivingMode;
import org.firstinspires.ftc.enums.GlyphArmMode;
import org.firstinspires.ftc.libraries.DrivingLibrary;
import org.firstinspires.ftc.libraries.GlyphArmLibrary;
import org.firstinspires.ftc.libraries.RelicArmLibrary;

/**
 * Created by lamanwyner on 12/30/17.
 */

/**
 * GC1
 * rt: relic claw
 * rb: relic claw
 * lt: relic lift
 * lb: relic lift
 * dpad: relic extend (x)
 * rjoy: move (y) turn (x)
 * ljoy: move (y) strafe (x)
 * a: relic lift preset
 * b: switch driving modes
 * x: relic drop preset
 * y: relic preset

 * GC2
 * rt: glyph arm
 * rb: glyph arm
 * lt: glyph arm
 * lb: glyph arm
 * dpad: pulley (y)
 * rjoy:
 * ljoy:
 * a: switch glyph arm modes
 * b: preset: ready for more glyphs
 * x: preset: stack glyphs
 * y: preset: quick drop glyphs
 */

@TeleOp
public class TeleOpMode extends LinearOpMode {
    DrivingLibrary drivingLibrary;
    GlyphArmLibrary glyphArmLibrary;
    int drivingMode;
    GlyphArmMode glyphArmMode;
    RelicArmLibrary relicArmLibrary;
    int glyphArmInt;

    public void runOpMode() throws InterruptedException {
        drivingLibrary = new DrivingLibrary(this);
        drivingLibrary.setSpeed(1);
        drivingMode = 0;
        drivingLibrary.setMode(drivingMode);

        glyphArmLibrary = new GlyphArmLibrary(this);
        relicArmLibrary = new RelicArmLibrary(this);
        glyphArmInt = 0;
        glyphArmMode = GlyphArmMode.values()[0];

        telemetry.addData("Status", "Initialized");
        telemetry.update();

        waitForStart();

        while (opModeIsActive()) {
            if (gamepad2.b) {
                telemetry.addData("pushed", "b");
                relicArmLibrary.extendArm();
            } else if (gamepad2.x) {
                telemetry.addData("pushed", "x");
                relicArmLibrary.retractArm();
            } else {
                telemetry.addData("pushed", "neither b nor x");
                relicArmLibrary.stopAll();
            }

            if (gamepad2.y) {
                    telemetry.addData("pushed", "y");
                    relicArmLibrary.liftClaw();
            }

            if (gamepad1.b) {
                drivingMode++;
                drivingMode %= DrivingMode.values().length;
                drivingLibrary.setMode(drivingMode);
            }

            drivingLibrary.driveStraight(gamepad1.left_stick_x, -gamepad1.left_stick_y);
            drivingLibrary.turn(gamepad1.right_stick_x, -gamepad1.right_stick_y);

            if (gamepad2.a) {
                glyphArmInt++;
                glyphArmInt %= GlyphArmMode.values().length;
                glyphArmMode = GlyphArmMode.values()[glyphArmInt];
            }

            /*if (gamepad2.b) glyphArmLibrary.resetArmPosition();
            if (gamepad2.x) glyphArmLibrary.liftTwoGlyphs();
            if (gamepad2.y) glyphArmLibrary.dropGlyphs();*/

            if (gamepad2.left_stick_y < 0) {
                glyphArmLibrary.setPulleyBottom();
            }

            switch (glyphArmMode) {
                case ALL_MOVE:
                    glyphArmLibrary.allArmsPreset(gamepad2.left_bumper, gamepad2.left_trigger);
                    glyphArmLibrary.allArmsIncrement(gamepad2.right_bumper, gamepad2.right_trigger);
                    break;
                case TOP_BOTTOM_PRESET:
                    glyphArmLibrary.topArmsPreset(gamepad2.left_bumper, gamepad2.left_trigger);
                    glyphArmLibrary.bottomArmsPreset(gamepad2.right_bumper, gamepad2.right_trigger);
                    break;
                case TOP_BOTTOM_INCREMENT:
                    glyphArmLibrary.topArmsIncrement(gamepad2.left_bumper, gamepad2.left_trigger);
                    glyphArmLibrary.bottomArmsIncrement(gamepad2.right_bumper,
                            gamepad2.right_trigger);
                    break;
            }

            glyphArmLibrary.movePulley(gamepad2);
            glyphArmLibrary.alignArms(gamepad2);

            relicArmLibrary.outputInfo();
            telemetry.addData("Status", "Running");
            telemetry.addData("Brake Mode", drivingLibrary.getMode());
            telemetry.addData("Glyph Arm Mode", glyphArmMode.getModeString());

            telemetry.update();
        }
    }
}
