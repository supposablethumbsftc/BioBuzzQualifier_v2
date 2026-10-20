package org.firstinspires.ftc.teamcode.BioBuzzV01.Subsystems;

import androidx.annotation.NonNull;

import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.acmerobotics.roadrunner.Action;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.BioBuzzV01.AbstractClasses.MatchInformation;
import org.firstinspires.ftc.teamcode.BioBuzzV01.AbstractClasses.Subsystem;

import java.util.ArrayList;

public class CSRobotV01 {
    public Drivetrain drivetrain;
    public Lift lift;
    public Intake intake;
    //public ElapsedTime timer;
    public Blinkins blinkins;
    public Thumbz thumbz;
    public Liftlock liftlock;
    public Turret turret;
    public Shooter shooter;
    private ArrayList <Subsystem> subsystems;
    private LinearOpMode linearOpMode;
    public ElapsedTime timer;
    public Hood hood;


    //public OpenCVDetector detector;

    //public AprilTagDetector aprildetect;

    public CSRobotV01(HardwareMap hardwareMap, LinearOpMode linearOpMode, MatchInformation matchInfo) {
        this.linearOpMode = linearOpMode;
        this.timer = new ElapsedTime();
        subsystems = new ArrayList<>();
        drivetrain = new Drivetrain(hardwareMap,linearOpMode,timer);
        lift = new Lift(hardwareMap,linearOpMode,timer);
        intake = new Intake(hardwareMap,linearOpMode,timer);
        turret = new Turret(hardwareMap,linearOpMode,timer);
        thumbz = new Thumbz(hardwareMap,linearOpMode,timer);
        liftlock = new Liftlock(hardwareMap,linearOpMode,timer);
        blinkins = new Blinkins(hardwareMap,linearOpMode, timer);
        shooter = new Shooter(hardwareMap, linearOpMode, timer);
        hood = new Hood(hardwareMap,linearOpMode,timer);
        subsystems.add(drivetrain);
        subsystems.add(lift);
        subsystems.add(intake);
        subsystems.add(liftlock);
        subsystems.add(turret);
        subsystems.add(thumbz);
        subsystems.add(shooter);
        subsystems.add(hood);
        subsystems.add(blinkins);


        init();
    }
    public void init() {
        for (Subsystem s : subsystems) {
            s.init();


        }
    }
    public void motorInit(){
        for (Subsystem s : subsystems) {
            s.motorInit();
        }
    }

    public void teleopServoInit(){
        for (Subsystem s : subsystems) {
            s.teleopServoInit();
        }
    }

    public void autonomousServoInit(){
        for (Subsystem s : subsystems) {
            s.autonomousServoInit();
        }
    }
    public void pause(int milliseconds) {

        ElapsedTime timer = new ElapsedTime(ElapsedTime.Resolution.MILLISECONDS);

        while (timer.time() < milliseconds && linearOpMode.opModeIsActive()) {
            //do nothing
            linearOpMode.idle();
        }
    }
    public Action delayAction(int pause){
        return new Action() {
            private boolean initialized = false;
            private double startTime = 0;
            @Override
            public boolean run(@NonNull TelemetryPacket telemetryPacket) {
                if (!initialized){
                    startTime = timer.time();
                    initialized = true;
                }
                return (startTime + pause) > timer.time();
            }
        };
    }
}
