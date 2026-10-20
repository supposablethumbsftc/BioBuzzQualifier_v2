
package org.firstinspires.ftc.teamcode.BioBuzzV01.Subsystems;



import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.hardware.rev.RevBlinkinLedDriver;
import org.firstinspires.ftc.teamcode.BioBuzzV01.AbstractClasses.Subsystem;
public class Blinkins extends Subsystem {
    RevBlinkinLedDriver blinkinLedDriver;
      RevBlinkinLedDriver.BlinkinPattern pattern;



      public Blinkins(HardwareMap hardwareMap, LinearOpMode linearOpMode, ElapsedTime timer) {
        super(hardwareMap, linearOpMode, timer);

    }




/*
     * Constructor for robot subsystem, allows access to the opMode opmode and telemetry system
     *
     * @param linearOpMode    opMode opmode used to determine if robot is active
     * @param telemetry an object for displaying data to the user
*/


    @Override
    public void init() {
        blinkinLedDriver = hardwareMap.get(RevBlinkinLedDriver.class, "blinkins");
    }

    @Override
    public void motorInit() {

    }

    @Override
    public void teleopServoInit() {

    }

    @Override
    public void autonomousServoInit() {

    }

    public void armOverride(int position) {

    }


    public void yellow(){
        pattern = RevBlinkinLedDriver.BlinkinPattern.YELLOW;
        blinkinLedDriver.setPattern(pattern);

    }
    public void Gold(){
        pattern = RevBlinkinLedDriver.BlinkinPattern.GOLD;
        blinkinLedDriver.setPattern(pattern);
    }
    public void Orange(){
        pattern = RevBlinkinLedDriver.BlinkinPattern.ORANGE;
        blinkinLedDriver.setPattern(pattern);
    }

    public void green(){
        pattern = RevBlinkinLedDriver.BlinkinPattern.GREEN;
        blinkinLedDriver.setPattern(pattern);
    }

    public void red(){
        pattern = RevBlinkinLedDriver.BlinkinPattern.RED;
        blinkinLedDriver.setPattern(pattern);
    }

    public void blue(){
        pattern = RevBlinkinLedDriver.BlinkinPattern.BLUE;
        blinkinLedDriver.setPattern(pattern);
    }
    public void flashingRed(){
        pattern = RevBlinkinLedDriver.BlinkinPattern.STROBE_RED;
        blinkinLedDriver.setPattern(pattern);
    }
    public void flashingYellow(){
        pattern = RevBlinkinLedDriver.BlinkinPattern.STROBE_GOLD;
        blinkinLedDriver.setPattern(pattern);
    }
    public void flashingBlue(){
        pattern = RevBlinkinLedDriver.BlinkinPattern.STROBE_BLUE;
        blinkinLedDriver.setPattern(pattern);
    }
    public void black(){
        pattern = RevBlinkinLedDriver.BlinkinPattern.BLACK;
        blinkinLedDriver.setPattern(pattern);
    }

}
