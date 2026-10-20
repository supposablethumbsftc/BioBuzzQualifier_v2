package org.firstinspires.ftc.teamcode.BioBuzzV01.Subsystems;

import androidx.annotation.NonNull;

import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.acmerobotics.roadrunner.Action;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.BioBuzzV01.AbstractClasses.Subsystem;

public class Hood extends Subsystem {
    private Servo hoodServo;
    public enum Pos{
        ON(0.5),
        OFF(0.1);


        public final double pos;
        private Pos(double pos) {
            this.pos = pos;
        }

    }
    public Hood(HardwareMap hardwareMap, LinearOpMode linearOpMode, ElapsedTime timer) {
        super(hardwareMap, linearOpMode, timer);
    }

    @Override
    public void init() { hoodServo = hardwareMap.servo.get("hood");}

    @Override
    public void motorInit() {

    }

    @Override
    public void teleopServoInit() {
    hoodServo.setPosition(0.15); // changed from .5 for centered

    }

    @Override
    public void autonomousServoInit() {
    hoodServo.setPosition(0.15); // changed from .5 for centered

    }


    public void on(){hoodServo.setPosition(0.5);}
    public void off(){hoodServo.setPosition(0.15);}
    public void expel(){hoodServo.setPosition(1);}


        public Action hoodAction(Hood.Pos hoodPos){
            return new Action() {
                @Override
                public boolean run(@NonNull TelemetryPacket telemetryPacket) {
                        hoodServo.setPosition(hoodPos.pos);
                    return false;
                }
            };
        }
}
