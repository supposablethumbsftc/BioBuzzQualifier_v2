package org.firstinspires.ftc.teamcode.BioBuzzV01.AbstractClasses;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.ElapsedTime;

public abstract class Subsystem {
    public HardwareMap hardwareMap;
    public LinearOpMode linearOpMode;
    public ElapsedTime timer;

    public Subsystem(HardwareMap hardwareMap, LinearOpMode linearOpMode, ElapsedTime timer) {
        this.hardwareMap = hardwareMap;
        this.linearOpMode = linearOpMode;
        this.timer = timer;
    }

    public abstract void init();
    public abstract void motorInit();
    public abstract void teleopServoInit();
    public abstract void autonomousServoInit();

//    public abstract void armOverride(int position);
}
