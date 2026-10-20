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
import org.firstinspires.ftc.teamcode.MecanumDrive;

import java.util.List;

@Autonomous (name="Blue Far 12 balls",group="test")
public class AutoBlueFar12 extends LinearOpMode {

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

//-----------------------------------------------------------------------------------------
//        Red Far Auto                                                  -------------------
//        Branch 1 - 9 Artifacts, No Bar Lifting, No Partner Auto       -------------------
//-----------------------------------------------------------------------------------------

//        Start Square to far wall facing inward on farthest edge of triangle

//        Turn on shooter for far
    voltage = controlHub.getInputVoltage(VoltageUnit.VOLTS);
    robot.shooter.overshootfarV(voltage);
    robot.hood.on();//hood down
//        Turn on intake
        robot.intake.takein();
//        Move four inches forward off wall
        Actions.runBlocking(
                boxermechanumdrive.actionBuilder(boxermechanumdrive.localizer.getPose())
                        .splineToLinearHeading(new Pose2d(8, 0, Math.toRadians(23.5)), Math.toRadians(-60)) //angle was -30 lje
                        .build());
    robot.pause(700);
//----------------------------------------------------------------------
//        Shoot the 3 starting balls from far position
//----------------------------------------------------------------------
        robot.thumbz.halfopen(); //fire first ball
        robot.pause(robot.thumbz.thumbz_open_time1_ms);
        robot.thumbz.block();
        robot.pause(robot.thumbz.thumbz_far_block_time1_ms);
        robot.thumbz.halfopen(); //fire second ball
        robot.pause(robot.thumbz.thumbz_open_time2_ms);
        robot.thumbz.block();
        robot.pause(robot.thumbz.thumbz_far_block_time2_ms);
        robot.thumbz.halfopen(); //fire third ball
        robot.pause(robot.thumbz.thumbz_open_time3_ms);
        robot.thumbz.block();
//        robot.pause(robot.thumbz.thumbz_far_block_time3_ms);

        telemetry.update();
        voltage = controlHub.getInputVoltage(VoltageUnit.VOLTS);
        robot.shooter.farV(voltage);

        // Collect first line of balls
        Actions.runBlocking( new ParallelAction(
                new SequentialAction(
                        new SleepAction(.3)
                ),
                boxermechanumdrive.actionBuilder(boxermechanumdrive.localizer.getPose())
                        .splineToLinearHeading(new Pose2d(30, 47, Math.toRadians(90)), Math.toRadians(-30))
                        .splineToLinearHeading(new Pose2d(8, 0, Math.toRadians(22.5)), Math.toRadians(-60)) // x was 37 and y was -26
                        .build()
        ));

//----------------------------------------------------------------------
//        Shoot the second set of 3 balls
//----------------------------------------------------------------------
        robot.thumbz.halfopen(); //fire first ball
        robot.pause(robot.thumbz.thumbz_open_time1_ms);
        robot.thumbz.block();
        robot.pause(robot.thumbz.thumbz_far_block_time1_ms);
        robot.thumbz.halfopen(); //fire second ball
        robot.pause(robot.thumbz.thumbz_open_time2_ms);
        robot.thumbz.block();
        robot.pause(robot.thumbz.thumbz_far_block_time2_ms);
        robot.thumbz.halfopen(); //fire third ball
        robot.pause(robot.thumbz.thumbz_open_time3_ms);
        robot.thumbz.block();
//        robot.pause(robot.thumbz.thumbz_far_block_time3_ms);

        voltage = controlHub.getInputVoltage(VoltageUnit.VOLTS);
        robot.shooter.farV(voltage);

        //      Collect balls from opposing team zone
        Actions.runBlocking( new ParallelAction(
                new SequentialAction(
                        new SleepAction(0.3)
                ),
                boxermechanumdrive.actionBuilder(boxermechanumdrive.localizer.getPose())
                        // Align to wall and drive into corner method
//                        .splineToLinearHeading(new Pose2d(14, -6, Math.toRadians(-90)), Math.toRadians(0)) // initial move to avoid line of balls no longer needed
//                        .splineToLinearHeading(new Pose2d(24, -47, Math.toRadians(-180)), Math.toRadians(0)) // move all the way against wall
                        // Grab first ball at angle and sweep into corner method
                        .splineToLinearHeading(new Pose2d(14, 46, Math.toRadians(120)), Math.toRadians(0)) // grab left ball at angle
                        .splineToLinearHeading(new Pose2d(-2, 47, Math.toRadians(180)), Math.toRadians(225)) // sweep into corner
                        .splineToLinearHeading(new Pose2d(8, 0, Math.toRadians(22.5)), Math.toRadians(-60)) // back to firing position
                        // Single ball at a time method - really close to running out of time
//                        .splineToLinearHeading(new Pose2d(14, -6, Math.toRadians(-90)), Math.toRadians(0)) // initial move to avoid line of balls no longer needed
//                        .splineToLinearHeading(new Pose2d(12, -47, Math.toRadians(-90)), Math.toRadians(0)) // collect left ball only
//                        .splineToLinearHeading(new Pose2d(12, -30, Math.toRadians(-90)), Math.toRadians(0)) // back off of wall
//                        .splineToLinearHeading(new Pose2d(7, -47, Math.toRadians(-90)), Math.toRadians(0)) // collect center ball only
//                        .splineToLinearHeading(new Pose2d(7, -30, Math.toRadians(-90)), Math.toRadians(0)) // back off of wall
//                        .splineToLinearHeading(new Pose2d(2, -47, Math.toRadians(-90)), Math.toRadians(0)) // collect rigth ball only
//                        .splineToLinearHeading(new Pose2d(8, 0, Math.toRadians(-20.5)), Math.toRadians(60)) // back to firing position
                        .build()
        ));
        robot.pause(100);//delay to try to get to correct angle before shooting
//----------------------------------------------------------------------
//        Shoot the 3rd set of 3 balls
//----------------------------------------------------------------------
        robot.thumbz.halfopen(); //fire first ball
        robot.pause(robot.thumbz.thumbz_open_time1_ms);
        robot.thumbz.block();
        robot.pause(robot.thumbz.thumbz_far_block_time1_ms);
        robot.thumbz.halfopen(); //fire second ball
        robot.pause(robot.thumbz.thumbz_open_time2_ms);
        robot.thumbz.block();
        robot.pause(robot.thumbz.thumbz_far_block_time2_ms);
        robot.thumbz.halfopen(); //fire third ball
        robot.pause(robot.thumbz.thumbz_open_time3_ms);
        robot.thumbz.block();
//        robot.pause(robot.thumbz.thumbz_far_block_time3_ms);


        // Collect second line of balls
        Actions.runBlocking(
                boxermechanumdrive.actionBuilder(boxermechanumdrive.localizer.getPose())
                        .splineToLinearHeading(new Pose2d(47, 9, Math.toRadians(90)), Math.toRadians(-30)) // x was 29
                        .splineToLinearHeading(new Pose2d(48, 47, Math.toRadians(90)), Math.toRadians(0))
                        .build());
        //Separate the final spline back to shooting position in an attempt to avoid hitting gate
        Actions.runBlocking(
                boxermechanumdrive.actionBuilder(boxermechanumdrive.localizer.getPose())
                        .splineToLinearHeading(new Pose2d(8, 0, Math.toRadians(22.5)), Math.toRadians(-60)) // x was 37 and y was -26
                        .build());



//----------------------------------------------------------------------
//        Shoot the 4th set of 3 balls
//----------------------------------------------------------------------
        robot.thumbz.halfopen(); //fire first ball
        robot.pause(robot.thumbz.thumbz_open_time1_ms);
        robot.thumbz.block();
        robot.pause(robot.thumbz.thumbz_far_block_time1_ms);
        robot.thumbz.halfopen(); //fire second ball
        robot.pause(robot.thumbz.thumbz_open_time2_ms);
        robot.thumbz.block();
        robot.pause(robot.thumbz.thumbz_far_block_time2_ms);
        robot.thumbz.halfopen(); //fire third ball
        robot.pause(robot.thumbz.thumbz_open_time3_ms);
        robot.thumbz.block();
//        robot.pause(robot.thumbz.thumbz_far_block_time3_ms);

        robot.intake.stop();

//      Important! move off of shooting line for 3 points and ranking point
//      perhaps should trigger this move from timer if it is too close?
        Actions.runBlocking(
                boxermechanumdrive.actionBuilder(boxermechanumdrive.localizer.getPose())
                        .splineToLinearHeading(new Pose2d(8, 19, Math.toRadians(45)), Math.toRadians(-45))
                        .build());

    }

    }