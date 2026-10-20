package org.firstinspires.ftc.teamcode.BioBuzzV01.Subsystems;

import androidx.annotation.NonNull;

import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.acmerobotics.roadrunner.Action;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.BioBuzzV01.AbstractClasses.Subsystem;

public class Liftlock extends Subsystem {
    private Servo liftlockServo;
    public enum Pos{
        Downlock(0.48),
        Uplock(0.52);


        public final double pos;
        private Pos(double pos) {
            this.pos = pos;
        }

    }
    public Liftlock(HardwareMap hardwareMap, LinearOpMode linearOpMode, ElapsedTime timer) {
        super(hardwareMap, linearOpMode, timer);
    }

    @Override
    public void init() { liftlockServo = hardwareMap.servo.get("liftlock");}

    @Override
    public void motorInit() {

    }

    @Override
    public void teleopServoInit() {
    liftlockServo.setPosition(.46); // changed from .5 for centered

    }

    @Override
    public void autonomousServoInit() {
    liftlockServo.setPosition(.46); // changed from .5 for centered

    }


    public void downlock(){liftlockServo.setPosition(.46);}
    public void uplock(){liftlockServo.setPosition(.52);}


        public Action liftlockAction(Liftlock.Pos liftlockPos){
            return new Action() {
                @Override
                public boolean run(@NonNull TelemetryPacket telemetryPacket) {
                        liftlockServo.setPosition(liftlockPos.pos);
                    return false;
                }
            };
        }
}
