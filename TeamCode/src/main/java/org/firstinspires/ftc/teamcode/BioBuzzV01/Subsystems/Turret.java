package org.firstinspires.ftc.teamcode.BioBuzzV01.Subsystems;

import androidx.annotation.NonNull;

import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.acmerobotics.roadrunner.Action;
import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.LLResultTypes;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.teamcode.BioBuzzV01.AbstractClasses.Subsystem;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class Turret extends Subsystem {

    public static final int turret_60_degree_move_ms = 137;
    public static final double turret_60_degree_move_sec = .137;
    public static final int turret_120_degree_move_ms = 275;
      public static final double turret_120_degree_move_sec = .275;
    public static final int turret_240_degree_move_ms = 550;
    public static final double turret_240_degree_move_sec = .550;

    private double turretPos = 0.5;
    private int AprilTagID = 0;
    private double AprilTagTx = 0;
    Limelight3A limelight;
    public TrackAprilTagTurretAction turretTracking;
    private final ElapsedTime loopTimer = new ElapsedTime();

    private Servo turretServo1;
    private Servo turretServo2;
    public enum Pos{
        FORWARD(.5);


        public final double pos;
        private Pos(double pos) {
            this.pos = pos;
        }

    }
       public Turret(HardwareMap hardwareMap, LinearOpMode linearOpMode, ElapsedTime timer) {
        super(hardwareMap, linearOpMode, timer);
        }

    @Override
    public void init() {
        turretServo1 = hardwareMap.servo.get("turret1");
        turretServo2 = hardwareMap.servo.get("turret2");
        limelight = hardwareMap.get(Limelight3A.class, "limelight");
        limelight.setPollRateHz(100);
        limelight.pipelineSwitch(5);
        limelight.start();
    }

    @Override
    public void motorInit() {

    }

    @Override
    public void teleopServoInit() {
    turretServo1.setPosition(0.5);
    turretServo2.setPosition(0.5);
    }

    @Override
    public void autonomousServoInit() {
        turretServo1.setPosition(0.5);
        turretServo2.setPosition(0.5);

    }
    public void initTurretTracking(Boolean trajectoryDone,double turretOffset, int tag){
        turretTracking = new TrackAprilTagTurretAction(false,0,24);
    }
    public void setturretpos(double turretPos) {
        turretServo1.setPosition(turretPos);
        turretServo2.setPosition(turretPos);
    }
        public Action setturretposAction(double turretPos){
            return new Action() {

                @Override
                public boolean run(@NonNull TelemetryPacket telemetryPacket) {
                    turretServo1.setPosition(turretPos);
                    turretServo2.setPosition(turretPos);
                    return false;
                }
            };
        }

    public class TrackAprilTagTurretAction implements Action{
        public Boolean trajectoryDone;
        public double turretOffset;
        public int tag;
        public TrackAprilTagTurretAction(Boolean trajectoryDone,double turretOffset, int tag){
            this.trajectoryDone = trajectoryDone;
            this.turretOffset=turretOffset;
            this.tag=tag;
        }
        @Override
        public boolean run(@NonNull TelemetryPacket telemetryPacket) {
            LLResult result = limelight.getLatestResult();
            if (result != null) {
//                    telemetry.addData("Limelight", "Data available!");
                if (result.isValid()) {
//                        telemetry.addData("Limelight", "result is Valid!");
                    // Access fiducial results
                    List<LLResultTypes.FiducialResult> fiducialResults = result.getFiducialResults();
                    for (LLResultTypes.FiducialResult fr : fiducialResults) {
                        AprilTagID = fr.getFiducialId();
                        AprilTagTx = fr.getTargetXDegrees();
//                            telemetry.addData("Fiducial", "ID: %d, Family: %s, X: %.2f, Y: %.2f, Area %.4f", fr.getFiducialId(), fr.getFamily(), fr.getTargetXDegrees(), fr.getTargetYDegrees(), fr.getTargetArea());
//                            distance = 6.8*Math.pow(fr.getTargetArea(),-0.5);
//                            telemetry.addData("Calculated distance","Distance %.4f", distance);
                        if (AprilTagID == tag) {  //selected alliance tag is in vision 20 is blue goal, 24 is red goal
                            loopTimer.reset();
//                                if (gamepad2.dpad_up) {
//                                    shootervelocity = 0.26 * (1+ 0.02*(13.25 - voltage)) * (1 + 0.0025 * (distance - 42));
//                                    telemetry.addData("Shooter", "Velocity %.4f", shootervelocity);
//                                    robot.shooter.setVelocity(shootervelocity);
//                                }
//                                telemetry.addData("Limelight", "Red tag detected");
                            if (AprilTagTx > turretOffset + .5) {
                                turretPos = turretPos - Math.abs((AprilTagTx-turretOffset)*0.00005);
                                if (turretPos < .01) {
                                    turretPos=0.01;
                                }
//                                    robot.turret.setturretpos(turretPos); // turn turret Right
                            } else if (AprilTagTx < turretOffset -0.5) {
                                turretPos = turretPos +  Math.abs((AprilTagTx-turretOffset)*0.00005);
                                if (turretPos > .99) {
                                    turretPos=0.99;
                                }
//                                    robot.turret.setturretpos(turretPos); // turn turret Left
                                } else { // already locked in
//                                    robot.blinkins.red();
                                if (trajectoryDone) {
                                    return false; // exit when trajectory done
                                }
                                return true; // keep tracking
                            }

                        } else if (loopTimer.milliseconds() >= 100){
//                                // reset position
                            turretPos = 0.5;
                            turretServo1.setPosition(turretPos);
                            turretServo2.setPosition(turretPos);
                            if (trajectoryDone) {
                                return false; // exit when trajectory done
                            }
                            return true; // keep tracking
                        }
//                        telemetry.update();
                    }
//                    } else {
//                        telemetry.addData("Limelight", "result is invalid");
                    // hold position
                }

//                telemetry.update();
                // hold position
//                } else {
//                    telemetry.addData("Limelight", "No data available");
                // hold position

            }
            turretServo1.setPosition(turretPos);
            turretServo2.setPosition(turretPos);
//        telemetry.addData("turretPos",turretPos );
//        telemetry.update();

//            if (trajectoryDone) {
//                return false; // exit when trajectory done
//            }
            return true; // keep tracking
        }

    }
}
