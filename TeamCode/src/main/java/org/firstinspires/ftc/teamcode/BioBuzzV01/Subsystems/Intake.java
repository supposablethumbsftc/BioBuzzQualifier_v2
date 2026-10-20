package org.firstinspires.ftc.teamcode.BioBuzzV01.Subsystems;

import androidx.annotation.NonNull;

import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.acmerobotics.roadrunner.Action;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.BioBuzzV01.AbstractClasses.Subsystem;

public class Intake extends Subsystem {
    private DcMotor Intake;


    public Intake(HardwareMap hardwareMap, LinearOpMode linearOpMode, ElapsedTime timer) {
        super(hardwareMap, linearOpMode, timer);
    }

    @Override
    public void init() {
        Intake = hardwareMap.dcMotor.get("intake");
    }

    @Override
    public void motorInit() {
        Intake.setDirection(DcMotorSimple.Direction.FORWARD);
        Intake.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
//        Intake.setTargetPosition(0);
//        Intake.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        Intake.setPower(0);
    }

    @Override
    public void teleopServoInit() {

    }

    @Override
    public void autonomousServoInit() {

    }



    public void takeout() {

        Intake.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        Intake.setPower(-1);
    }

    public void takein() {

            Intake.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
            Intake.setPower(1);
        }


    public void stop() {
//        Intake.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        Intake.setPower(0); // ask David why this 1.0 and not 0
//        Intake.setTargetPosition(currentWinchPosition);
    }

    public Action IntakeTakeInActionNW(){ //no waiting
        return new Action() {
            private boolean initialized = false;

            @Override
            public boolean run(@NonNull TelemetryPacket telemetryPacket) {
                if (!initialized) {
                    Intake.setDirection(DcMotorSimple.Direction.FORWARD);
                    Intake.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
                    Intake.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

                    initialized = true;
                }
                Intake.setPower(1);
                return false;
            }
        };
    }
    public Action IntakeStopActionNW(){ //no waiting
        return new Action() {
            private boolean initialized = false;

            @Override
            public boolean run(@NonNull TelemetryPacket telemetryPacket) {
                if (!initialized) {
                    Intake.setDirection(DcMotorSimple.Direction.FORWARD);
                    Intake.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
                    Intake.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

                    initialized = true;
                }
                Intake.setPower(0);
                return false;
            }
        };
    }

}
