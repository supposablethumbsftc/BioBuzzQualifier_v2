package org.firstinspires.ftc.teamcode.BioBuzzV01;

import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.LLResultTypes;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.PIDFCoefficients;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.BioBuzzV01.AbstractClasses.MatchInformation;
import org.firstinspires.ftc.teamcode.BioBuzzV01.Subsystems.CSRobotV01;

import java.util.List;
import java.util.concurrent.Callable;

@Disabled
@TeleOp
public class FlywheelTuning extends OpMode {

    MatchInformation matchInfo;
    public CSRobotV01 robot;
       boolean firstLoop = true;
    public double high_volocity = 2500;
    public double low_volocity = 1500;
double CurTargetVelocity = high_volocity;
double F=0;
double P=0;
double[] StepSizes = {10,1,.1,.01,.001,.0001};
int stepIndex = 0;

    @Override
    public void init() {
        matchInfo = new MatchInformation();
        robot = new CSRobotV01(hardwareMap, null, matchInfo);
        telemetry.setMsTransmissionInterval(11);   // Speed up telemetry updates, Just use for debugging.
        telemetry.setDisplayFormat(Telemetry.DisplayFormat.MONOSPACE);
//        PIDFCoefficients pidfCoefficients = new PIDFCoefficients(P,0,0,F);
        robot.shooter.setVelocityPID(P,0,0,F);
        telemetry.addLine("Init Complete");
    }

    @Override
    public void loop() {
        if (firstLoop) {
            robot.motorInit();
            robot.teleopServoInit();
            firstLoop = false;
        }

        // get gamepad input
        // set target velocity
        // update telemetry

        if(gamepad1.yWasPressed()){
            if (CurTargetVelocity==high_volocity){
                CurTargetVelocity=low_volocity;
            }
            else {
                CurTargetVelocity = high_volocity;
            }
        }

        if(gamepad1.bWasPressed()){
            stepIndex = (stepIndex+1)%StepSizes.length;
        }

        if(gamepad1.dpadLeftWasPressed()){
            F -= StepSizes[stepIndex];
        }
        if(gamepad1.dpadRightWasPressed()){
            F += StepSizes[stepIndex];
        }

        if(gamepad1.dpadUpWasPressed()){
            P += StepSizes[stepIndex];
        }
        if(gamepad1.dpadDownWasPressed()){
            P -= StepSizes[stepIndex];
        }

        //set new PIDcoefficients
        robot.shooter.setVelocityPID(P,0,0,F);

        //set velocity
        robot.shooter.setVelocity(CurTargetVelocity/2800);

        double CurVelocity = robot.shooter.getVelocity()*-1;
        double error = CurTargetVelocity - CurVelocity;

        telemetry.addData("Target Velocity",CurTargetVelocity);
        telemetry.addData("Current Velocity","%.2f",CurVelocity);
        telemetry.addData("error","%.2f",error);
        telemetry.addLine( "------------------------------");
        telemetry.addData("Tuning P","%.4f (Dpad-U/D)",P);
        telemetry.addData("Tuning F","%.4f (Dpad-L/R)",F);
        telemetry.addData("Step Size","%.4f (B Button)",StepSizes[stepIndex]);
        telemetry.update();

    }




}


