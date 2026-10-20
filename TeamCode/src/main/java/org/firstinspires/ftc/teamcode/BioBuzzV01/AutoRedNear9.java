package org.firstinspires.ftc.teamcode.BioBuzzV01;

import com.acmerobotics.roadrunner.ParallelAction;
import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.SequentialAction;
import com.acmerobotics.roadrunner.SleepAction;
import com.acmerobotics.roadrunner.ftc.Actions;
import com.qualcomm.hardware.lynx.LynxModule;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.navigation.VoltageUnit;
import org.firstinspires.ftc.teamcode.BioBuzzV01.AbstractClasses.MatchInformation;
import org.firstinspires.ftc.teamcode.BioBuzzV01.Subsystems.CSRobotV01;
import org.firstinspires.ftc.teamcode.BioBuzzV01.Subsystems.Thumbz;
import org.firstinspires.ftc.teamcode.MecanumDrive;

import java.util.List;

@Autonomous (name="Red Near 9 balls",group="test")
public class AutoRedNear9 extends LinearOpMode {

    //    public boolean firstLoop;
    public CSRobotV01 robot;
    public ElapsedTime timer;
    private LynxModule controlHub;
    public double voltage = 0;
    MatchInformation matchInfo;

    @Override
    public void runOpMode() {
        matchInfo = new MatchInformation();
        robot = new CSRobotV01(hardwareMap, this, matchInfo);
        timer = new ElapsedTime(ElapsedTime.Resolution.MILLISECONDS);
        robot.motorInit();
//        robot.arm.QuickArmInit();
        robot.autonomousServoInit();
        MecanumDrive boxermechanumdrive = new MecanumDrive(hardwareMap,new Pose2d(
                0, 0, Math.toRadians(0)
        ));
        List<LynxModule> hubs = hardwareMap.getAll(LynxModule.class);
        for (LynxModule hub : hubs) {
            if (hub.isParent()) {
                controlHub = hub;
                break;
            }
        }

        if (controlHub == null) {
            telemetry.addLine("ERROR: Control Hub not found!");
            telemetry.update();
            return;
        }
        waitForStart();

//--------------------------------------------------------------------------
//        Red Close Auto No April Tags Open Goal after 3 -------------------
//        Branch 1 - No Partner Auto, 9 ArtifactS           -------------------
//--------------------------------------------------------------------------

//  Turn on shooter in overshoot to start ramping up
        voltage = controlHub.getInputVoltage(VoltageUnit.VOLTS);
        robot.shooter.overshootnearV(voltage);
        robot.hood.off();//hood up
        //        Turn intake on
        robot.intake.takein();

        Actions.runBlocking( new ParallelAction(
                new SequentialAction(
// ----------------------------------------------------------------------
//        Shoot first set of 3 balls
// ----------------------------------------------------------------------
                        // time firing balls while moving
                        new SleepAction(1.2), // this is the time to adjust so that robot is aligned for first shot
                        // fire
                        robot.thumbz.thumbzAction(Thumbz.Pos.HALFOPEN),//fire first ball
                        new SleepAction(robot.thumbz.thumbz_open_time1_sec),
                        robot.thumbz.thumbzAction(Thumbz.Pos.BLOCK),
                        new SleepAction(robot.thumbz.thumbz_near_block_time1_sec),
                        robot.thumbz.thumbzAction(Thumbz.Pos.HALFOPEN),//fire second ball
                        new SleepAction(robot.thumbz.thumbz_open_time2_sec),
                        robot.thumbz.thumbzAction(Thumbz.Pos.BLOCK),
                        new SleepAction(robot.thumbz.thumbz_near_block_time2_sec),
                        robot.thumbz.thumbzAction(Thumbz.Pos.HALFOPEN),//fire third ball
                        new SleepAction(robot.thumbz.thumbz_open_time3_sec),
                        robot.thumbz.thumbzAction(Thumbz.Pos.BLOCK)
//                        new SleepAction(robot.thumbz.thumbz_near_block_time3_sec)
                ),
                boxermechanumdrive.actionBuilder(boxermechanumdrive.localizer.getPose())
                        .splineToLinearHeading(new Pose2d(-10, 30, Math.toRadians(-45)), Math.toRadians(0))
                        .build()
        ));

        //        set near shooter speed
        voltage = controlHub.getInputVoltage(VoltageUnit.VOLTS);
        robot.shooter.nearV(voltage);


//      Move to front of first line of balls
        Actions.runBlocking( new ParallelAction(
                new SequentialAction(
                        new SleepAction(robot.thumbz.thumbz_near_block_time_sec)
        ),
                boxermechanumdrive.actionBuilder(boxermechanumdrive.localizer.getPose())
                        .splineToLinearHeading(new Pose2d(-22, 30, Math.toRadians(-90)), Math.toRadians(-60))
                        .splineToLinearHeading(new Pose2d(-27, 30, Math.toRadians(-90)), Math.toRadians(-60))
                        .splineToLinearHeading(new Pose2d(-27, 3, Math.toRadians(-90)), Math.toRadians(-60)) // collect balls
                        .build()
        ));

//      Move robot to shooting position
        Actions.runBlocking( new ParallelAction(
                new SequentialAction(
// ----------------------------------------------------------------------
//        Shoot 3 balls 2nd time
// ----------------------------------------------------------------------
                        // time firing balls while moving
                        new SleepAction(1.5), // this is the time to adjust so that robot is just crossing the line
                        // fire
                        robot.thumbz.thumbzAction(Thumbz.Pos.HALFOPEN),//fire first ball
                        new SleepAction(robot.thumbz.thumbz_open_time1_sec),
                        robot.thumbz.thumbzAction(Thumbz.Pos.BLOCK),
                        new SleepAction(robot.thumbz.thumbz_near_block_time1_sec),
                        robot.thumbz.thumbzAction(Thumbz.Pos.HALFOPEN),//fire second ball
                        new SleepAction(robot.thumbz.thumbz_open_time2_sec),
                        robot.thumbz.thumbzAction(Thumbz.Pos.BLOCK),
                        new SleepAction(robot.thumbz.thumbz_near_block_time2_sec),
                        robot.thumbz.thumbzAction(Thumbz.Pos.HALFOPEN),//fire third ball
                        new SleepAction(robot.thumbz.thumbz_open_time3_sec),
                        robot.thumbz.thumbzAction(Thumbz.Pos.BLOCK)
//                        new SleepAction(robot.thumbz.thumbz_near_block_time3_sec)
                ),
                boxermechanumdrive.actionBuilder(boxermechanumdrive.localizer.getPose())
                        .splineToLinearHeading(new Pose2d(-16, 30, Math.toRadians(-44)), Math.toRadians(0)) //
                        .build()
        ));


//      Move to front of second line of balls
        Actions.runBlocking( new ParallelAction(
                new SequentialAction(
// ----------------------------------------------------------------------
//        Shoot 3 balls 3rd time
// ----------------------------------------------------------------------
                        // time firing balls while moving
                        new SleepAction(8.3), // this is the time to adjust so that robot is just crossing the line
                        // fire
                        robot.thumbz.thumbzAction(Thumbz.Pos.HALFOPEN),//fire first ball
                        new SleepAction(robot.thumbz.thumbz_open_time1_sec),
                        robot.thumbz.thumbzAction(Thumbz.Pos.BLOCK),
                        new SleepAction(robot.thumbz.thumbz_near_block_time1_sec),
                        robot.thumbz.thumbzAction(Thumbz.Pos.HALFOPEN),//fire second ball
                        new SleepAction(robot.thumbz.thumbz_open_time2_sec),
                        robot.thumbz.thumbzAction(Thumbz.Pos.BLOCK),
                        new SleepAction(robot.thumbz.thumbz_near_block_time2_sec),
                        robot.thumbz.thumbzAction(Thumbz.Pos.HALFOPEN),//fire third ball
                        new SleepAction(robot.thumbz.thumbz_open_time3_sec),
                        robot.thumbz.thumbzAction(Thumbz.Pos.BLOCK)
//                        new SleepAction(robot.thumbz.thumbz_near_block_time3_sec)
                ),
                boxermechanumdrive.actionBuilder(boxermechanumdrive.localizer.getPose())
                        .splineToLinearHeading(new Pose2d(-52, 30, Math.toRadians(-90)), Math.toRadians(-60)) //
                        .splineToLinearHeading(new Pose2d(-52, -7, Math.toRadians(-90)), Math.toRadians(0))
                        .splineToLinearHeading(new Pose2d(-52, 18, Math.toRadians(-70)), Math.toRadians(-60)) //
                        .splineToLinearHeading(new Pose2d(0, 40, Math.toRadians(-60)), Math.toRadians(-80)) //
                        .build()
        ));
    }

}