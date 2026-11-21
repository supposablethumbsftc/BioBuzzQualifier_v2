package org.firstinspires.ftc.teamcode.BioBuzzV01;

import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.Vector2d;
import com.acmerobotics.roadrunner.ftc.Actions;
import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.LLResultTypes;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.hardware.lynx.LynxModule;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.navigation.VoltageUnit;
import org.firstinspires.ftc.teamcode.BioBuzzV01.AbstractClasses.MatchInformation;
import org.firstinspires.ftc.teamcode.BioBuzzV01.Subsystems.CSRobotV01;
import org.firstinspires.ftc.teamcode.MecanumDrive;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Autonomous (name="Down Park",group="test")
public class AutoDownPark extends LinearOpMode {

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
        MecanumDrive boxermechanumdrive = new MecanumDrive(hardwareMap, new Pose2d(
                0, 0, Math.toRadians(0)
        ));
        int AprilTagID = 0;
        boolean tagfound = false;

        Limelight3A limelight;

        limelight = hardwareMap.get(Limelight3A.class, "limelight");
        telemetry.setMsTransmissionInterval(11);
        limelight.pipelineSwitch(5);
        limelight.start();

        List<LynxModule> hubs = hardwareMap.getAll(LynxModule.class);
        for (LynxModule hub : hubs) {
            if (hub.isParent()) {
                controlHub = hub;
                break;
            }
        }


        waitForStart();

//--------------------------------------------------------------------------
//        Down - park
//        Assume partner has good up auto wait for goal to tip and then shoot
//--------------------------------------------------------------------------

//                shoot
        //Turn on shooter motor
        //Open Blockers
        // Pause - 3 sec
        //  Turn on shooter in overshoot to start ramping up
        voltage = controlHub.getInputVoltage(VoltageUnit.VOLTS);
        robot.shooter.nearV(voltage);
        robot.hood.on();//hood down
//        Turn on intake
        robot.turret.setturretpos(.85);
        //   delay 7 or April 7
        Set<Integer> targetTags = new HashSet<>(Arrays.asList(40, 41, 32, 33));
        timer.reset();
        tagfound = false;
        while (!tagfound && timer.milliseconds() < 7000 && opModeIsActive()) {
            LLResult result = limelight.getLatestResult();

            if (result != null && result.isValid()) {
                List<LLResultTypes.FiducialResult> fiducials = result.getFiducialResults();

                for (LLResultTypes.FiducialResult fr : fiducials) {
                    int tagId = fr.getFiducialId();

                    if (targetTags.contains(tagId)) {
                        tagfound = true;
                        int foundTagID = tagId;
                        telemetry.addData("Matched Tag", tagId);
                        break; // Stop checking fiducials once a target tag is matched
                    }
                }
            }
        }
        telemetry.update();
//        timer.reset();
//        tagfound = false;
//        while (!tagfound && timer.milliseconds() < 7000) {
//            //        Read april tag
//            LLResult result = limelight.getLatestResult();
//            if (result != null) {
//                if (result.isValid()) {
//                    // Access fiducial results
//                    List<LLResultTypes.FiducialResult> fiducialResults = result.getFiducialResults();
//                    for (LLResultTypes.FiducialResult fr : fiducialResults) {
//                        AprilTagID = fr.getFiducialId();
//                        telemetry.addData("Fiducial", "ID: %d", AprilTagID);
//                        if (AprilTagID == 44) {
//                            tagfound = true;
//                            robot.turret.setturretpos(.99);
//                        } else if (AprilTagID == 40) {
//                            tagfound = true;
//                            robot.turret.setturretpos(.99);
//                        }
//
//                        telemetry.update();
//
//                    }
//                } else {
//                    telemetry.addData("Limelight", "No data available");
//                }
//
//                telemetry.update();
//            }
//        }
        if (tagfound){
        robot.turret.setturretpos(.99);
            robot.intake.takein();
            robot.transfer.takein();
        robot.pause(1000);
        //        shoot
//Turn on intake

            // shoot four pollen
            robot.thumbz.open();
            robot.pause(70);
            robot.thumbz.block();
            robot.pause(400);
            robot.thumbz.open();
            robot.pause(70);
            robot.thumbz.block();
            robot.pause(400);
            robot.thumbz.open();
            robot.pause(70);
            robot.thumbz.block();
            robot.pause(400);
            robot.thumbz.open();
            robot.pause(70);
            robot.thumbz.block();
            robot.pause(400);

            //try again if stuck
            robot.thumbz.open();
            robot.pause(500);
            robot.thumbz.block();
            robot.pause(500);
        //robot.shooter.stop();
        //  robot.turret.setturretpos(.5);

        //                flower x=8, y=6, -90

        robot.funnel.on();
        Actions.runBlocking(
                boxermechanumdrive.actionBuilder(boxermechanumdrive.localizer.getPose())
                        // .splineToLinearHeading(new Pose2d(0,-4,Math.toRadians(0)), Math.toRadians(90))
                        .splineToLinearHeading(new Pose2d(17, 12, Math.toRadians(-90)), Math.toRadians(45))
                        .splineToLinearHeading(new Pose2d(17, 2, Math.toRadians(-90)), Math.toRadians(-90))
                        .build());
        robot.pause(4000);

        //        return to shoot
        robot.funnel.off();

//                far shot
        Actions.runBlocking(
                boxermechanumdrive.actionBuilder(boxermechanumdrive.localizer.getPose())
                        .splineToLinearHeading(new Pose2d(0, 0, Math.toRadians(0)), Math.toRadians(180))
                        .build());
        robot.shooter.nearV(voltage);


        robot.turret.setturretpos(.99);
        robot.pause(1000);
            // shoot four pollen
            robot.thumbz.open();
            robot.pause(70);
            robot.thumbz.block();
            robot.pause(400);
            robot.thumbz.open();
            robot.pause(70);
            robot.thumbz.block();
            robot.pause(400);
            robot.thumbz.open();
            robot.pause(70);
            robot.thumbz.block();
            robot.pause(400);
            robot.thumbz.open();
            robot.pause(70);
            robot.thumbz.block();
            robot.pause(400);

            //try again if stuck
            robot.thumbz.open();
            robot.pause(500);
            robot.thumbz.block();
            robot.pause(500);
        robot.shooter.stop();
    }
        robot.funnel.off();

         //        park/collect nectar

        Actions.runBlocking(
                boxermechanumdrive.actionBuilder(boxermechanumdrive.localizer.getPose())
                        .splineToLinearHeading(new Pose2d(4,14,Math.toRadians(0)), Math.toRadians(30))
                        .splineToLinearHeading(new Pose2d(42,20,Math.toRadians(0)), Math.toRadians(80))
                        .splineToLinearHeading(new Pose2d(50,16,Math.toRadians(0)), Math.toRadians(-15))
                        .build());

    }

}