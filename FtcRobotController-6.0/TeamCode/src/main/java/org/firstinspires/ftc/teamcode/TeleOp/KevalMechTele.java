/*
    Ideas to Implement:

    Automate some parts of teleop
        Automate tower shooting (power and POSSIBLY location)
        Automate power shooting (either turn the robot just enough or move the robot just enough)

    Use slowmode with other areas of the robot, like slides

 */

package org.firstinspires.ftc.teamcode.TeleOp;

import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.robotcore.external.navigation.Orientation;

import static java.lang.Math.abs;
import static java.lang.Math.sqrt;

@TeleOp(name = "KevalMechTele", group = "a")
public class KevalMechTele extends OpMode {

    //Configuration parameters
    double slowModePower = 0.35;
    double mediumModePower = 0.55;
    double normalModePower = 0.8;
    double buttonIsPressedThreshold = 0.10;

    //State variables
    DcMotor fl, fr, bl, br;
    BNO055IMU imu;
    private double currentAngle;
    Orientation lastAngles = new Orientation();
    double flPower, frPower, blPower, brPower;

    @Override
    public void init() {
        fl = hardwareMap.dcMotor.get("fl");
        fr = hardwareMap.dcMotor.get("fr");
        bl = hardwareMap.dcMotor.get("bl");
        br = hardwareMap.dcMotor.get("br");

        //TODO: Find which motors to reverse
        fl.setDirection(DcMotor.Direction.REVERSE);
        bl.setDirection(DcMotor.Direction.REVERSE);
        fr.setDirection(DcMotor.Direction.REVERSE);
        br.setDirection(DcMotor.Direction.REVERSE);

        fl.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        fr.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        bl.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        br.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        IMUSetup();
    }

    public void IMUSetup() {
        BNO055IMU.Parameters parameters = new BNO055IMU.Parameters();
        parameters.mode = BNO055IMU.SensorMode.IMU;
        parameters.angleUnit = BNO055IMU.AngleUnit.RADIANS;
        parameters.accelUnit = BNO055IMU.AccelUnit.METERS_PERSEC_PERSEC;
        parameters.loggingEnabled = false;
        imu = hardwareMap.get(BNO055IMU.class, "imu");
        imu.initialize(parameters);
    }

    @Override
    public void loop() {
        /*
        The left joystick to move forward/backward/left/right, right joystick to turn

        gamepad 1 controls movement
        gamepad 2 currently has no function
         */

        double y = gamepad1.left_stick_y * -1; // Reversed
        double x = gamepad1.left_stick_x * (sqrt(2)); // Counteract imperfect strafing
        double rx = gamepad1.right_stick_x;

        flPower = (normalModePower) * (y + x + rx);
        frPower = (normalModePower) * (y - x - rx);
        blPower = (normalModePower) * (y - x + rx);
        brPower = (normalModePower) * (y + x - rx);

        if (abs(flPower) > 1 || abs(blPower) > 1 ||
                abs(frPower) > 1 || abs(brPower) > 1 ) {
            // Find the largest power
            double max;
            max = Math.max(abs(flPower), abs(blPower));
            max = Math.max(abs(frPower), max);
            max = Math.max(abs(brPower), max);

            max = abs(max);

            // Divide everything by max (it's positive so we don't need to worry about signs)
            flPower /= max;
            blPower /= max;
            frPower /= max;
            brPower /= max;
        }

        if (gamepad1.right_trigger > buttonIsPressedThreshold){
            flPower *= mediumModePower;
            frPower *= mediumModePower;
            blPower *= mediumModePower;
            brPower *= mediumModePower;
        }

        else if (gamepad1.left_trigger > buttonIsPressedThreshold){
            flPower *= slowModePower;
            frPower *= slowModePower;
            blPower *= slowModePower;
            brPower *= slowModePower;
        }

        if(gamepad1.dpad_down || gamepad1.dpad_left || gamepad1.dpad_right || gamepad1.dpad_up) {
            if (gamepad1.dpad_up) {
                flPower = 0.25;
                frPower = 0.25;
                blPower = 0.25;
                brPower = 0.25;
            }

            if (gamepad1.dpad_down) {
                flPower = -0.25;
                frPower = -0.25;
                blPower = -0.25;
                brPower = -0.25;
            }

            if (gamepad1.dpad_left) {
                flPower = -0.25;
                frPower = 0.25;
                blPower = 0.25;
                brPower = -0.25;
            }

            if (gamepad1.dpad_right) {
                flPower = 0.25;
                frPower = -0.25;
                blPower = -0.25;
                brPower = 0.25;
            }
        }

        if (gamepad1.left_bumper) {

        }

        if (gamepad1.right_bumper) {

        }

        fl.setPower(flPower);
        fr.setPower(frPower);
        bl.setPower(blPower);
        br.setPower(brPower);
    }
}