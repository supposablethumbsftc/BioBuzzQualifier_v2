package org.firstinspires.ftc.teamcode.BioBuzzV01.Subsystems;

import androidx.annotation.NonNull;

import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.acmerobotics.roadrunner.Action;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.BioBuzzV01.AbstractClasses.Subsystem;

public class Transfer extends Subsystem {
    private DcMotor Transfer;


    public Transfer(HardwareMap hardwareMap, LinearOpMode linearOpMode, ElapsedTime timer) {
        super(hardwareMap, linearOpMode, timer);
    }

    @Override
    public void init() {
        Transfer = hardwareMap.dcMotor.get("transfer");
    }

    @Override
    public void motorInit() {
        Transfer.setDirection(DcMotorSimple.Direction.FORWARD);
        Transfer.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
//        Transfer.setTargetPosition(0);
//        Transfer.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        Transfer.setPower(0);
    }

    @Override
    public void teleopServoInit() {

    }

    @Override
    public void autonomousServoInit() {

    }



    public void takeout() {

        Transfer.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        Transfer.setPower(-.6);
    }

    public void takein() {

            Transfer.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
            Transfer.setPower(.6);
        }


    public void stop() {
//        Transfer.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        Transfer.setPower(0); // ask David why this 1.0 and not 0
//        Transfer.setTargetPosition(currentWinchPosition);
    }

    public Action TransferTakeInActionNW(){ //no waiting
        return new Action() {
            private boolean initialized = false;

            @Override
            public boolean run(@NonNull TelemetryPacket telemetryPacket) {
                if (!initialized) {
                    Transfer.setDirection(DcMotorSimple.Direction.FORWARD);
                    Transfer.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
                    Transfer.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

                    initialized = true;
                }
                Transfer.setPower(.6);
                return false;
            }
        };
    }
    public Action TransferStopActionNW(){ //no waiting
        return new Action() {
            private boolean initialized = false;

            @Override
            public boolean run(@NonNull TelemetryPacket telemetryPacket) {
                if (!initialized) {
                    Transfer.setDirection(DcMotorSimple.Direction.FORWARD);
                    Transfer.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
                    Transfer.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

                    initialized = true;
                }
                Transfer.setPower(0);
                return false;
            }
        };
    }

}
