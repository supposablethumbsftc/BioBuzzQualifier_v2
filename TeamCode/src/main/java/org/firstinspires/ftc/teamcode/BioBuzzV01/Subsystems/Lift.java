package org.firstinspires.ftc.teamcode.BioBuzzV01.Subsystems;

import androidx.annotation.NonNull;

import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.acmerobotics.roadrunner.Action;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.TouchSensor;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.BioBuzzV01.AbstractClasses.Subsystem;

public class Lift extends Subsystem {
    private DcMotor Lift;
 //   private DcMotor rightLift;

    public int currentLiftPosition = 0;
//    public int currentRightLiftPosition = 0;

//    private final int MAX_LIFT_EXTEND = -2400;
    private final int MAX_LIFT_HEIGHT = 11000;
    public TouchSensor touch_sensor;
    public Lift(HardwareMap hardwareMap, LinearOpMode linearOpMode, ElapsedTime timer) {
        super(hardwareMap, linearOpMode, timer);
    }

    @Override
    public void init() {
        Lift = hardwareMap.dcMotor.get("lift");
        //rightLift = hardwareMap.dcMotor.get("rlift");
        touch_sensor = hardwareMap.touchSensor.get("touch_sensor");
    }

    @Override
    public void motorInit() {

        Lift.setDirection(DcMotorSimple.Direction.REVERSE);
        Lift.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        Lift.setTargetPosition(0);
        Lift.setMode(DcMotor.RunMode.RUN_TO_POSITION);
//        leftLift.setPower(1.0);

//        rightLift.setDirection(DcMotorSimple.Direction.FORWARD);
//        rightLift.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
//        rightLift.setTargetPosition(0);
//        rightLift.setMode(DcMotor.RunMode.RUN_TO_POSITION);
////        rightLift.setPower(1.0);


    }
    public void EncoderInit() {
        Lift.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
//        rightLift.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
    }

    @Override
    public void teleopServoInit() {

    }

    @Override
    public void autonomousServoInit() {

    }


    public void armOverride(int position) {

    }



    public void moveLift(double power){
        Lift.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        currentLiftPosition = Lift.getCurrentPosition();
        if(currentLiftPosition < MAX_LIFT_HEIGHT && power>0) {
            Lift.setPower(power);
        } else if (currentLiftPosition >0 && power<0){
            Lift.setPower(power);
        }else {
            Lift.setPower(0);
        }
//        rightLift.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
//        rightLift.setPower(power);
//        currentRightLiftPosition = rightLift.getCurrentPosition();
    }

    public void stop() {
    if (touch_sensor.isPressed()){
        Lift.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
//        rightLift.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        currentLiftPosition = 0;
//        currentRightLiftPosition = 0;

    }
    else {
        Lift.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        Lift.setPower(1.0);
        Lift.setTargetPosition(currentLiftPosition);

//        rightLift.setMode(DcMotor.RunMode.RUN_TO_POSITION);
//        rightLift.setPower(1.0);
//        rightLift.setTargetPosition(currentRightLiftPosition);
    }



    }

        public void raise(double power){
//            currentRightLiftPosition = rightLift.getCurrentPosition();
//        if (currentRightLiftPosition < -10 ){
            moveLift(power);
//        } else {
//            stop();
//        }

    }
/*
    public void lower(double power, int armencoder) {
        currentRightLiftPosition = rightLift.getCurrentPosition();
        if ((armencoder>1500)&&(currentRightLiftPosition > MAX_LIFT_HEIGHT)) { // armencoder compare was 2700
            moveLift(-power);
        } else if (currentRightLiftPosition > MAX_LIFT_EXTEND) {
            moveLift(-power);
        } else {
            stop();
        }
        }
    public void liftToPosition(int position) {
        leftLift.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        leftLift.setPower(1.0); //check this code TODO
        leftLift.setTargetPosition(position);
        currentLeftLiftPosition = position;
        rightLift.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        rightLift.setPower(1.0); // ask David why this 1.0 and not 0
        rightLift.setTargetPosition(position);
        currentRightLiftPosition = position;
    }

    public Action LiftToPositionAction(int position){
        return new Action() {
            private boolean initialized = false;

            @Override
            public boolean run(@NonNull TelemetryPacket telemetryPacket) {
                if (!initialized) {
                    leftLift.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                    leftLift.setPower(1.0);
                    leftLift.setTargetPosition(position);
                    currentLeftLiftPosition = position;
                    rightLift.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                    rightLift.setPower(1.0);
                    rightLift.setTargetPosition(position);
                    currentRightLiftPosition = position;
                    initialized = true;
                }
                return Math.abs(rightLift.getCurrentPosition() - position) > 200;
            }
        };
    }
    public Action LiftToPositionActionNW(int position){ //no waiting
        return new Action() {
            private boolean initialized = false;

            @Override
            public boolean run(@NonNull TelemetryPacket telemetryPacket) {
                if (!initialized) {
                    leftLift.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                    leftLift.setPower(1.0);
                    leftLift.setTargetPosition(position);
                    currentLeftLiftPosition = position;
                    rightLift.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                    rightLift.setPower(1.0);
                    rightLift.setTargetPosition(position);
                    currentRightLiftPosition = position;
                    initialized = true;
                }
                return false;
            }
        };
    }*/
}
