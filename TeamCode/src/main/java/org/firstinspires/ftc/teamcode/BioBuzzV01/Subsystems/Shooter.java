package org.firstinspires.ftc.teamcode.BioBuzzV01.Subsystems;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.teamcode.BioBuzzV01.AbstractClasses.Subsystem;

public class Shooter extends Subsystem {
    private DcMotorEx Shooter;
public double TicksPerRev;

    public Shooter(HardwareMap hardwareMap, LinearOpMode linearOpMode, ElapsedTime timer) {
        super(hardwareMap, linearOpMode, timer);
    }

    @Override
    public void init() {
        Shooter = (DcMotorEx) hardwareMap.dcMotor.get("shooter");

    }

    @Override
    public void motorInit() {
        Shooter.setDirection(DcMotorEx.Direction.REVERSE);
        Shooter.setMode(DcMotorEx.RunMode.RUN_USING_ENCODER);
        Shooter.setVelocityPIDFCoefficients(1.7,0,23,2.72);//1.6,0,30,2.65); //1,0,10,1.8);
//        Shooter.setTargetPosition(0);
//        Shooter.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        Shooter.setPower(0);
//        TicksPerRev = Shooter.getMotorType().getTicksPerRev();
    }

    @Override
    public void teleopServoInit() {

    }

    @Override
    public void autonomousServoInit() {

    }

    public void setVelocityPID(double KP, double KI, double KD, double KF) {
        Shooter.setVelocityPIDFCoefficients(KP,KI,KD,KF);
    }

    public void setVelocity(double setvelocity) {
        Shooter.setVelocity(setvelocity*2800, AngleUnit.DEGREES);
    }

    public double getVelocity() {
        double getvelocity = Shooter.getVelocity();
        return getvelocity;
    }
    public void setpower(double power) {

//        Shooter.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        Shooter.setPower(power);
    }


    public void overshootfarV(double voltage) {

//        Shooter.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
//        Shooter.setPower(.7);
        Shooter.setVelocity(0.275*2800* (1+ 0.04*(13.00
 - voltage)), AngleUnit.DEGREES);
    }
    public void overshootfar() {

//        Shooter.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
//        Shooter.setPower(.7);
        Shooter.setVelocity(0.275*2800, AngleUnit.DEGREES);
    }
    public void farV(double voltage) {

//        Shooter.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
//        Shooter.setPower(.7);
        Shooter.setVelocity(0.275*2800* (1+ 0.04*(13.25 - voltage)), AngleUnit.DEGREES);
    }
    public void far() {

//        Shooter.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
//        Shooter.setPower(.7);
        Shooter.setVelocity(0.275*2800, AngleUnit.DEGREES);
    }
    public void overshootnearV(double voltage) {

//        Shooter.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        Shooter.setVelocity(0.22 *2800* (1+ 0.04*(13.25 - voltage)), AngleUnit.DEGREES);
    }
    public void overshootnear() {

//        Shooter.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        Shooter.setVelocity(0.22
*2800, AngleUnit.DEGREES);
    }
    public void fireballnearlow() {

//        Shooter.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        Shooter.setVelocity(0.22 *2800, AngleUnit.DEGREES);
    }
    public void fireballnearhigh() {

//        Shooter.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        Shooter.setVelocity(0.22 *2800, AngleUnit.DEGREES);
    }
    public void nearV(double voltage) {

//        Shooter.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        Shooter.setVelocity(0.19 *2800* (1+ 0.04*(13.25 - voltage)), AngleUnit.DEGREES);
    }

    public void near() {

//        Shooter.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            Shooter.setVelocity(.22 *2800, AngleUnit.DEGREES);
        }


    public void stop() {
//        Shooter.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        Shooter.setVelocity(0, AngleUnit.DEGREES);

    }


}
