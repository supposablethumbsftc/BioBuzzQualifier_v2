package org.firstinspires.ftc.teamcode.BioBuzzV01.Subsystems;

import androidx.annotation.NonNull;

import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.acmerobotics.roadrunner.Action;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.BioBuzzV01.AbstractClasses.Subsystem;

public class Thumbz extends Subsystem {
    public static final int intake_stop_time_ms = 0;
    public static final double intake_stop_time_sec = 0;
    public static final int thumbz_open_time_ms = 150;
    public static final double thumbz_open_time_sec = .150;
    public static final int thumbz_open_time1_ms = 150;
    public static final double thumbz_open_time1_sec = .150;
    public static final int thumbz_open_time2_ms = 150;
    public static final double thumbz_open_time2_sec = .150;
    public static final int thumbz_open_time3_ms = 300;
    public static final double thumbz_open_time3_sec = .300;

    public static final int ball_fire_time_ms = 1100;
    public static final double ball_fire_time_sec = 1.100;

    public static final int thumbz_near_block_time_ms = 300;
    public static final double thumbz_near_block_time_sec = .300;
    public static final int thumbz_near_block_time1_ms = 400;
    public static final double thumbz_near_block_time1_sec = .400;
    public static final int thumbz_near_block_time2_ms = 250;
    public static final double thumbz_near_block_time2_sec = .250;
    public static final int thumbz_near_block_time3_ms = 300;
    public static final double thumbz_near_block_time3_sec = .300;
    public static final int thumbz_far_block_time_ms = 350;
    public static final double thumbz_far_block_time_sec = .350;
    public static final int thumbz_far_block_time1_ms = 450;
    public static final double thumbz_far_block_time1_sec = .450;
    public static final int thumbz_far_block_time2_ms = 350;
    public static final double thumbz_far_block_time2_sec = .350;
    public static final int thumbz_far_block_time3_ms = 350;
    public static final double thumbz_far_block_time3_sec = .350;
    private Servo thumbzServo1;
    private Servo thumbzServo2;
    public enum Pos{
        BLOCK(.65),
        HALFOPEN(.3),
        OPEN(.1);



        public final double pos;
        private Pos(double pos) {
            this.pos = pos;
        }

    }
    public Thumbz(HardwareMap hardwareMap, LinearOpMode linearOpMode, ElapsedTime timer) {
        super(hardwareMap, linearOpMode, timer);
    }

    @Override
    public void init() {
        thumbzServo1 = hardwareMap.servo.get("thumbz1");
        thumbzServo2 = hardwareMap.servo.get("thumbz2");
    }

    @Override
    public void motorInit() {

    }

    @Override
    public void teleopServoInit() {
        thumbzServo1.setPosition(.65);
        thumbzServo2.setPosition(.35);
    }

    @Override
    public void autonomousServoInit() {
        thumbzServo1.setPosition(.65);
        thumbzServo2.setPosition(.35);
    }


    public void open(){
        thumbzServo1.setPosition(.1);//left servo
        thumbzServo2.setPosition(.9);//right servo
    }
    public void block(){
        thumbzServo1.setPosition(.65);
        thumbzServo2.setPosition(.35);
    }
    public void halfopen(){
        thumbzServo1.setPosition(.3);
        thumbzServo2.setPosition(.7);
    }


    public Action thumbzAction(Thumbz.Pos thumbzPos){
            return new Action() {
                @Override
                public boolean run(@NonNull TelemetryPacket telemetryPacket) {
                    thumbzServo1.setPosition(thumbzPos.pos);
                    thumbzServo2.setPosition(Math.abs(1-thumbzPos.pos));
                    return false;
                }
            };
        }
}
