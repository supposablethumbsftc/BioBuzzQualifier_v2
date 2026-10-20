package org.firstinspires.ftc.teamcode.BioBuzzV01.Subsystems;

import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.robotcore.external.navigation.YawPitchRollAngles;
import org.firstinspires.ftc.teamcode.BioBuzzV01.AbstractClasses.Subsystem;

public class Drivetrain extends Subsystem {

    private final double TRACK_WIDTH = 9.5*2.54;
    private final double ROBOT_CIRCUMFERENCE = TRACK_WIDTH * Math.PI;
    private final double CM_PER_DEG = ROBOT_CIRCUMFERENCE / 360;
    private final double WHEEL_DIAMETER = 48 / 10.0;
    private final int COUNTS_PER_ROT = 2000;
    private final double WHEEL_CIRCUMFERENCE = WHEEL_DIAMETER*Math.PI;
    private final double INCH_PER_COUNT = (WHEEL_CIRCUMFERENCE / COUNTS_PER_ROT) / 2.54;
    private final double COUNTS_PER_CM = COUNTS_PER_ROT / WHEEL_CIRCUMFERENCE;
    private final double COUNTS_PER_DEG = COUNTS_PER_CM / CM_PER_DEG;


    private DcMotor frontLeft;
    private DcMotor frontRight;
    private DcMotor backLeft;
    private DcMotor backRight;

    private DcMotor perp;
    private DcMotor par0;
    private DcMotor par1;
    private BNO055IMU imu;
    private BNO055IMU.Parameters parameters;
    private YawPitchRollAngles myRobotOrientation;
    private double targetHeading = 0;
    private double diffInHeading = 0;
    private double StrafeLeftCorrectionFactor = 0.90;
    private double StrafeRightCorrectionFactor = 0.96;
    public Drivetrain(HardwareMap hardwareMap, LinearOpMode linearOpMode, ElapsedTime timer) {
        super(hardwareMap, linearOpMode, timer);
    }

    @Override
    public void init() {
        //Initializes drive motors
        frontLeft = hardwareMap.dcMotor.get("fl"); // control hub port 1
        frontRight = hardwareMap.dcMotor.get("fr"); // expansion hub port 1

        backLeft = hardwareMap.dcMotor.get("bl"); // control hub port 0
        backRight = hardwareMap.dcMotor.get("br"); // expansion hub port 0

        //Initializes dead wheel encoders
       perp = hardwareMap.dcMotor.get("fr"); // expansion hub port 1 "fr"

       par0 = hardwareMap.dcMotor.get("bl"); // control hub port 0 "bl"

        par1 = hardwareMap.dcMotor.get("br"); // expansion hub port 0 "br"

        imu = hardwareMap.get(BNO055IMU.class, "imu");

        parameters = new BNO055IMU.Parameters();
        parameters.angleUnit = BNO055IMU.AngleUnit.DEGREES;
        imu.initialize(parameters);


    }

    @Override
    public void motorInit() {
        //Needs to be reversed in these sets
        frontLeft.setDirection(DcMotorSimple.Direction.REVERSE);
        backRight.setDirection(DcMotorSimple.Direction.FORWARD);
        frontRight.setDirection(DcMotorSimple.Direction.FORWARD);
        backLeft.setDirection(DcMotorSimple.Direction.REVERSE);

        frontLeft.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        frontRight.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        backLeft.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        backRight.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

    }

    @Override
    public void teleopServoInit() {

    }

    @Override
    public void autonomousServoInit() {}

    public void armOverride(int position) {  }


    public void setSpeedForEachMotor(double frSpeed, double flSpeed, double brSpeed, double blSpeed){
        frontLeft.setPower(flSpeed);
        backLeft.setPower(blSpeed);
        backRight.setPower(brSpeed);
        frontRight.setPower(frSpeed);

    }
    public void driveForward(double distance,double speed){
        distance = distance * 2.54 * COUNTS_PER_CM;
        par0.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);//was par0
        par0.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);//was par0
        while (linearOpMode.opModeIsActive() && Math.abs(par0.getCurrentPosition()) < distance){
            linearOpMode.telemetry.addData("Driving forward",par0.getCurrentPosition()*INCH_PER_COUNT);
            linearOpMode.telemetry.update();
            setSpeedForEachMotor(-speed,-speed,-speed,-speed);
        }
        setSpeedForEachMotor(0,0,0,0);
    }
    public void driveForwardByTime(double distance,double speed, double timeout){
        double initTime = timer.time();
        distance = distance * 2.54 * COUNTS_PER_CM;
        par0.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);//was par0
        par0.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);//was par0
        while (linearOpMode.opModeIsActive() && Math.abs(par0.getCurrentPosition()) < distance && timer.time()-initTime < timeout){
            linearOpMode.telemetry.addData("Driving forward",par0.getCurrentPosition()*INCH_PER_COUNT);
            linearOpMode.telemetry.update();
            setSpeedForEachMotor(-speed,-speed,-speed,-speed);
        }
        setSpeedForEachMotor(0,0,0,0);
    }
    public void driveinit() {
        par0.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        par0.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        }
    public void Forward(double speed) {
        setSpeedForEachMotor(-speed, -speed, -speed, -speed);
    }
    public void robotstop(){
        setSpeedForEachMotor(0,0,0,0);
    }
    public void driveForwardPI(double distance,double speed){
        double Encoder_counts = distance * 2.54 * COUNTS_PER_CM;
        par0.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        par0.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        double distanceError = distance;
        double power = speed;
        double previous_power = 0.2;
        while (linearOpMode.opModeIsActive() && Math.abs(par0.getCurrentPosition()) < Encoder_counts){

            distanceError = ((Encoder_counts - Math.abs(par0.getCurrentPosition()))/2.54)/COUNTS_PER_CM;
            power = Range.clip(distanceError * (speed-0.2)/14, 0.2, 1);
            if ((power-0.1) > previous_power) {
                power = previous_power +0.02;

            }

            linearOpMode.telemetry.addData("Driving forward",par0.getCurrentPosition()*INCH_PER_COUNT);
            linearOpMode.telemetry.addData("power", power);
            linearOpMode.telemetry.addData("Error", distanceError);
            linearOpMode.telemetry.update();
            setSpeedForEachMotor(-power,-power,-power,-power);
            previous_power = power;
        }
        if (speed > 0.6){
            distance = 0.5 * 2.54 * COUNTS_PER_CM;
            par0.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);//was par0
            par0.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);//was par0
            while (linearOpMode.opModeIsActive() && Math.abs(par0.getCurrentPosition()) < distance) {
                linearOpMode.telemetry.addData("Driving Backward", par0.getCurrentPosition() * INCH_PER_COUNT);
                linearOpMode.telemetry.update();
                setSpeedForEachMotor(speed, speed, speed, speed);
            }
        }
        setSpeedForEachMotor(0,0,0,0);
    }
    public void driveBackward(double distance,double speed){
        distance = distance * 2.54 * COUNTS_PER_CM;
        par0.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);//was par0
        par0.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);//was par0
        while (linearOpMode.opModeIsActive() && Math.abs(par0.getCurrentPosition()) < distance){
            linearOpMode.telemetry.addData("Driving Backward",par0.getCurrentPosition()*INCH_PER_COUNT);
            linearOpMode.telemetry.update();
            setSpeedForEachMotor(speed,speed,speed,speed);
        }
        setSpeedForEachMotor(0,0,0,0);
    }
    public void driveBackwardByTime(double distance,double speed, double timeout){
        double initTime = timer.time();
        distance = distance * 2.54 * COUNTS_PER_CM;
        par0.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);//was par0
        par0.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);//was par0
        while (linearOpMode.opModeIsActive() && Math.abs(par0.getCurrentPosition()) < distance && timer.time()-initTime < timeout){
            linearOpMode.telemetry.addData("Driving Backward",par0.getCurrentPosition()*INCH_PER_COUNT);
            linearOpMode.telemetry.update();
            setSpeedForEachMotor(speed,speed,speed,speed);
        }
        setSpeedForEachMotor(0,0,0,0);
    }
    public void driveBackwardPI(double distance,double speed){
        double Encoder_counts = distance * 2.54 * COUNTS_PER_CM;
        par0.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);//was par0
        par0.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);//was par0
        double distanceError = distance;
        double power = speed;
        double previous_power = 0.2;
        while (linearOpMode.opModeIsActive() && Math.abs(par0.getCurrentPosition()) < Encoder_counts){

            distanceError = ((Encoder_counts - Math.abs(par0.getCurrentPosition()))/2.54)/COUNTS_PER_CM;
            power = Range.clip(distanceError * (speed-0.2)/14, 0.2, 1);
            if ((power-0.1) > previous_power) {
                power = previous_power +0.02;

            }

            linearOpMode.telemetry.addData("Driving forward",par0.getCurrentPosition()*INCH_PER_COUNT);
            linearOpMode.telemetry.addData("power", power);
            linearOpMode.telemetry.addData("Error", distanceError);
            linearOpMode.telemetry.update();
            setSpeedForEachMotor(power,power,power,power);
            previous_power = power;
        }
        if (speed > 0.6){
        distance = 0.5 * 2.54 * COUNTS_PER_CM;
        par0.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);//was par0
        par0.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);//waspar0
        while (linearOpMode.opModeIsActive() && Math.abs(par0.getCurrentPosition()) < distance) {
            linearOpMode.telemetry.addData("Driving Backward", par0.getCurrentPosition() * INCH_PER_COUNT);
            linearOpMode.telemetry.update();
            setSpeedForEachMotor(-speed, -speed, -speed, -speed);
            }
        }
        setSpeedForEachMotor(0,0,0,0);
    }
    public void Right(double speed) {
        setSpeedForEachMotor(speed * StrafeRightCorrectionFactor,-speed * StrafeRightCorrectionFactor,-speed,speed);
    }
    public void strafeRight(double distance,double speed){
        distance = distance * 2.54 * COUNTS_PER_CM;
        perp.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        perp.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        while (linearOpMode.opModeIsActive() && Math.abs(perp.getCurrentPosition()) < distance){
            linearOpMode.telemetry.addData("Strafing Right",perp.getCurrentPosition()*INCH_PER_COUNT);
            linearOpMode.telemetry.update();
            setSpeedForEachMotor(speed * StrafeRightCorrectionFactor,-speed * StrafeRightCorrectionFactor,-speed,speed);

        }
        setSpeedForEachMotor(0,0,0,0);
    }

    public void strafeRightbyTime(double distance,double speed,int timeout){
        double initTime = timer.time();
        distance = distance * 2.54 * COUNTS_PER_CM;
        perp.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);//was perp
        perp.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);//was perp
        while (linearOpMode.opModeIsActive() && Math.abs(perp.getCurrentPosition()) < distance && timer.time()-initTime < timeout){
            linearOpMode.telemetry.addData("Strafing Right",perp.getCurrentPosition()*INCH_PER_COUNT);
            linearOpMode.telemetry.update();

            setSpeedForEachMotor(speed * StrafeRightCorrectionFactor,-speed * StrafeRightCorrectionFactor,-speed,speed);


        }
        setSpeedForEachMotor(0,0,0,0);
    }
    public void strafeRightPI(double distance,double speed) {
        double Encoder_counts = distance * 2.54 * COUNTS_PER_CM;
        perp.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        perp.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        double distanceError = distance;
        double power = speed;
        double previous_power = 0.4;

        while (linearOpMode.opModeIsActive() && Math.abs(perp.getCurrentPosition()) < Encoder_counts) {

            distanceError = ((Encoder_counts - Math.abs(perp.getCurrentPosition())) / 2.54) / COUNTS_PER_CM;
            power = Range.clip(distanceError * (speed - 0.4) / 10, 0.4, 1);
            if ((power - 0.1) > previous_power) {
                power = previous_power + 0.02;

            }
            linearOpMode.telemetry.addData("Strafing Right", perp.getCurrentPosition() * INCH_PER_COUNT);
            linearOpMode.telemetry.addData("power", power);
            linearOpMode.telemetry.addData("Error", distanceError);
            linearOpMode.telemetry.update();

            setSpeedForEachMotor(power * StrafeRightCorrectionFactor, -power * StrafeRightCorrectionFactor, -power, power);

            previous_power = power;
        }
        if (speed > 0.6) {
            distance = 0.5 * 2.54 * COUNTS_PER_CM;
            perp.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            perp.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
            while (linearOpMode.opModeIsActive() && Math.abs(perp.getCurrentPosition()) < distance) {
                linearOpMode.telemetry.addData("Strafing Left", perp.getCurrentPosition() * INCH_PER_COUNT);
                linearOpMode.telemetry.update();
                setSpeedForEachMotor(-speed * StrafeRightCorrectionFactor, speed * StrafeRightCorrectionFactor, speed, -speed);
            }

            setSpeedForEachMotor(0, 0, 0, 0);
        }
    }
    public void Left(double speed) {
        setSpeedForEachMotor(-speed * StrafeLeftCorrectionFactor,speed * StrafeLeftCorrectionFactor,speed,-speed);
    }
    public void strafeLeft(double distance,double speed){
        distance = distance * 2.54 * COUNTS_PER_CM;
        perp.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        perp.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        while (linearOpMode.opModeIsActive() && Math.abs(perp.getCurrentPosition()) < distance){
            linearOpMode.telemetry.addData("Strafing Left",perp.getCurrentPosition()*INCH_PER_COUNT);
            linearOpMode.telemetry.update();
            setSpeedForEachMotor(-speed * StrafeLeftCorrectionFactor,speed * StrafeLeftCorrectionFactor,speed,-speed);
        }
        setSpeedForEachMotor(0,0,0,0);
    }
    public void strafeLeftbytime(double distance,double speed, double timeout){
        // timout should be in milliseconds
        double initTime = timer.time();
       distance = distance * 2.54 * COUNTS_PER_CM;
        perp.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);//was perp
        perp.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);//was perp
        while (linearOpMode.opModeIsActive() && Math.abs(perp.getCurrentPosition()) < distance && timer.time()-initTime < timeout){
                linearOpMode.telemetry.addData("Strafing Left",perp.getCurrentPosition()*INCH_PER_COUNT);
            linearOpMode.telemetry.addData("time", timer.time());
            linearOpMode.telemetry.update();
            setSpeedForEachMotor(-speed * StrafeLeftCorrectionFactor,speed * StrafeLeftCorrectionFactor,speed,-speed);

            linearOpMode.telemetry.addData("time", timer.time());
        }
        setSpeedForEachMotor(0,0,0,0);
    }
    public void strafeLeftPI(double distance,double speed) {
        double Encoder_counts = distance * 2.54 * COUNTS_PER_CM;
        perp.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        perp.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        double distanceError = distance;
        double power = speed;
        double previous_power = 0.2;

        while (linearOpMode.opModeIsActive() && Math.abs(perp.getCurrentPosition()) < Encoder_counts) {

            distanceError = ((Encoder_counts - Math.abs(perp.getCurrentPosition())) / 2.54) / COUNTS_PER_CM;
            power = Range.clip(distanceError * (speed - 0.4) / 10, 0.4, 1);
            if ((power - 0.1) > previous_power) {
                power = previous_power + 0.02;

            }
            linearOpMode.telemetry.addData("Strafing Left", perp.getCurrentPosition() * INCH_PER_COUNT);
            linearOpMode.telemetry.addData("power", power);
            linearOpMode.telemetry.addData("Error", distanceError);
            linearOpMode.telemetry.update();

            setSpeedForEachMotor(-power * StrafeLeftCorrectionFactor, power * StrafeLeftCorrectionFactor, power, -power);

            previous_power = power;
        }
        if (speed > 0.6) {
            distance = 0.5 * 2.54 * COUNTS_PER_CM;
            perp.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            perp.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
            while (linearOpMode.opModeIsActive() && Math.abs(perp.getCurrentPosition()) < distance) {
                linearOpMode.telemetry.addData("Strafing Right", perp.getCurrentPosition() * INCH_PER_COUNT);
                linearOpMode.telemetry.update();
                setSpeedForEachMotor(speed * StrafeLeftCorrectionFactor, -speed * StrafeLeftCorrectionFactor, -speed, speed);
            }

            setSpeedForEachMotor(0, 0, 0, 0);
        }
    }

    public void turnLeftEncoders(double angle,double speed){
        int counts = (int) (angle * COUNTS_PER_DEG);
        par1.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        par1.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        par0.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        par0.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        while (linearOpMode.opModeIsActive()&&(Math.abs(par1.getCurrentPosition()) + Math.abs(par0.getCurrentPosition()))/2.0 < counts){
            setSpeedForEachMotor(-speed,speed,-speed,speed);
        }
        setSpeedForEachMotor(0,0,0,0);
    }

    public void turnLeft(double angle, double speed){
        imu.initialize(parameters);
        while (linearOpMode.opModeIsActive() && Math.abs(getExternalHeading()) < angle){
            setSpeedForEachMotor(-speed,speed,-speed,speed);
            linearOpMode.telemetry.addData("Turning Left", getExternalHeading());
            linearOpMode.telemetry.update();
        }
        setSpeedForEachMotor(0,0,0,0);

    }

    public void turnRight(double angle, double speed){
        imu.initialize(parameters);
        while (linearOpMode.opModeIsActive() && Math.abs(getExternalHeading()) < angle){
            setSpeedForEachMotor(speed,-speed,speed,-speed);
            linearOpMode.telemetry.addData("Turning Right", getExternalHeading());
            linearOpMode.telemetry.update();
        }
        setSpeedForEachMotor(0,0,0,0);
    }
    public void turnRightPID(double angle){
        imu.initialize(parameters);
        double speed = 0;
        double error = 0;
        double current_heading = 0; //Math.abs(getExternalHeading());
        while (linearOpMode.opModeIsActive() && current_heading < angle){
            current_heading = Math.abs(getExternalHeading());
            speed = 0.2 + (angle - current_heading)/400;
            setSpeedForEachMotor(speed,-speed,speed,-speed);
            //linearOpMode.telemetry.addData("Turning Right", getExternalHeading());
            //linearOpMode.telemetry.update();
        }
        setSpeedForEachMotor(0,0,0,0);
    }

    public void RightTurnBetter(double angle, double power){
        diffInHeading += angle;
        targetHeading += angle;

        if (targetHeading > 360){
            targetHeading = targetHeading - 360;
        }else if (targetHeading < 0){
            targetHeading = 360 + targetHeading;
        }

        double currentHeading = getExternalHeading();
        if (getExternalHeading() < 0){
            currentHeading = 180 - currentHeading;
        }

        double angularError = targetHeading - currentHeading;

        if (angularError > 180){
            angularError = angularError - 360;
        }else if (angularError < -180){
            angularError = angularError + 360;
        }

        while (linearOpMode.opModeIsActive() && Math.abs(angularError) > 3){
            currentHeading = getExternalHeading();
            if (currentHeading < 0){
                currentHeading = 180 - currentHeading;
            }
            angularError = targetHeading - currentHeading;
            if (angularError > 180){
                angularError = angularError - 360;
            }else if (angularError < -180){
                angularError = angularError + 360;
            }
            power = Range.clip(angularError * 0.0055, 0.2, 0.5);
            setSpeedForEachMotor(power, -power, power, -power);
            linearOpMode.telemetry.addData("power", power);
            linearOpMode.telemetry.addData("Error", angularError);
            linearOpMode.telemetry.addData("Heading",currentHeading);
            linearOpMode.telemetry.update();
        }
        setSpeedForEachMotor(0,0,0,0);


    }
    public void LeftTurnBetter(double angle, double power){
        diffInHeading += angle;
        targetHeading += angle;

        if (targetHeading > 360){
            targetHeading = targetHeading - 360;
        }else if (targetHeading < 0){
            targetHeading = 360 + targetHeading;
        }

        double currentHeading = getExternalHeading();
        if (getExternalHeading() < 0){
            currentHeading = 180 - currentHeading;
        }

        double angularError = targetHeading - currentHeading;

        if (angularError > 180){
            angularError = angularError - 360;
        }else if (angularError < -180){
            angularError = angularError + 360;
        }

        while (linearOpMode.opModeIsActive() && Math.abs(angularError) > 3){
            currentHeading = getExternalHeading();
            if (currentHeading < 0){
                currentHeading = 180 - currentHeading;
            }
            angularError = targetHeading - currentHeading;
            if (angularError > 180){
                angularError = angularError - 360;
            }else if (angularError < -180){
                angularError = angularError + 360;
            }
            power = Range.clip(angularError * 0.0055, 0.2, 0.5);
            setSpeedForEachMotor(-power, power, -power, power);
            linearOpMode.telemetry.addData("power", power);
            linearOpMode.telemetry.addData("Error", angularError);
            linearOpMode.telemetry.addData("Heading",currentHeading);
            linearOpMode.telemetry.update();
        }
        setSpeedForEachMotor(0,0,0,0);


    }
    public void LeftTurnBetterByTime(double angle, double power, double timeout ) {
        double initTime = timer.time();
        diffInHeading += angle;
        targetHeading += angle;

        if (targetHeading > 360) {
            targetHeading = targetHeading - 360;
        } else if (targetHeading < 0) {
            targetHeading = 360 + targetHeading;
        }

        double currentHeading = getExternalHeading();
        if (getExternalHeading() < 0) {
            currentHeading = 180 - currentHeading;
        }

        double angularError = targetHeading - currentHeading;

        if (angularError > 180) {
            angularError = angularError - 360;
        } else if (angularError < -180) {
            angularError = angularError + 360;
        }

        while (linearOpMode.opModeIsActive() && Math.abs(angularError) > 3 && timer.time()-initTime < timeout) {
            currentHeading = getExternalHeading();
            if (currentHeading < 0) {
                currentHeading = 180 - currentHeading;
            }
            angularError = targetHeading - currentHeading;
            if (angularError > 180) {
                angularError = angularError - 360;
            } else if (angularError < -180) {
                angularError = angularError + 360;
            }
            power = Range.clip(angularError * 0.0055, 0.2, 0.5);
            setSpeedForEachMotor(-power, power, -power, power);
            linearOpMode.telemetry.addData("power", power);
            linearOpMode.telemetry.addData("Error", angularError);
            linearOpMode.telemetry.addData("Heading", currentHeading);
            linearOpMode.telemetry.update();
        }
        setSpeedForEachMotor(0, 0, 0, 0);
    }


        public void turnRightEncoders(double angle,double speed){
        int counts = (int) (angle * COUNTS_PER_DEG);
        par1.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        par1.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        par0.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        par0.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        while (linearOpMode.opModeIsActive()&&(Math.abs(par1.getCurrentPosition()) + Math.abs(par0.getCurrentPosition()))/2.0 < counts){
            setSpeedForEachMotor(speed,-speed,speed,-speed);
        }
        setSpeedForEachMotor(0,0,0,0);
    }

    public double getExternalHeading(){
        return imu.getAngularOrientation().firstAngle;
    }
}
