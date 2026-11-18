package org.firstinspires.ftc.teamcode.BioBuzzV01.Subsystems;

import androidx.annotation.NonNull;

import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.acmerobotics.roadrunner.Action;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.BioBuzzV01.AbstractClasses.Subsystem;

public class Funnel extends Subsystem {
    private Servo funnelServo;
    public enum Pos{
        ON(0),
        OFF(0.5);


        public final double pos;
        private Pos(double pos) {
            this.pos = pos;
        }

    }
    public Funnel(HardwareMap hardwareMap, LinearOpMode linearOpMode, ElapsedTime timer) {
        super(hardwareMap, linearOpMode, timer);
    }

    @Override
    public void init() { funnelServo = hardwareMap.servo.get("funnel");}

    @Override
    public void motorInit() {

    }

    @Override
    public void teleopServoInit() {
    funnelServo.setPosition(0.55); // changed from .5 for centered

    }

    @Override
    public void autonomousServoInit() {
    funnelServo.setPosition(.55); // changed from .5 for centered

    }


    public void on(){funnelServo.setPosition(1);}
    public void off(){funnelServo.setPosition(.55);}
    //
     public void half(){funnelServo.setPosition(.7);}


        public Action funnelAction(Funnel.Pos funnelPos){
            return new Action() {
                @Override
                public boolean run(@NonNull TelemetryPacket telemetryPacket) {
                        funnelServo.setPosition(funnelPos.pos);
                    return false;
                }
            };
        }
}
