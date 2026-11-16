package org.firstinspires.ftc.teamcode.BioBuzzV01;

import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.ftc.Actions;
import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.LLResultTypes;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.hardware.lynx.LynxModule;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.util.ElapsedTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import org.firstinspires.ftc.robotcore.external.navigation.VoltageUnit;
import org.firstinspires.ftc.teamcode.BioBuzzV01.AbstractClasses.MatchInformation;
import org.firstinspires.ftc.teamcode.BioBuzzV01.Subsystems.CSRobotV01;
import org.firstinspires.ftc.teamcode.MecanumDrive;

import java.util.List;

@Autonomous (name="Duo Up",group="test")
public class AutoDuoUp extends LinearOpMode {

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
//        Up – Duo
//        Assume working with partner that starts in down position and can score/tip goal back
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
        robot.turret.setturretpos(.01);
        robot.pause(4000);
        robot.intake.takein();
        robot.transfer.takein();
        robot.thumbz.open();

        robot.pause(1000);
        robot.thumbz.block();
       // robot.shooter.stop();
        robot.turret.setturretpos(.5);
        // close Blockers
//               Collect in corner
        //Turn on intake

        //Drive in Corner 48 to left back 1 1/2
        Actions.runBlocking(
                boxermechanumdrive.actionBuilder(boxermechanumdrive.localizer.getPose())
                        // .splineToLinearHeading(new Pose2d(0,-4,Math.toRadians(0)), Math.toRadians(90))
                        .splineToLinearHeading(new Pose2d(47.5, 1.5, Math.toRadians(0)), Math.toRadians(0))
                        .build());
        Actions.runBlocking(
                boxermechanumdrive.actionBuilder(boxermechanumdrive.localizer.getPose())
                        // .splineToLinearHeading(new Pose2d(0,-4,Math.toRadians(0)), Math.toRadians(90))
                        .splineToLinearHeading(new Pose2d(0, 0, Math.toRadians(0)), Math.toRadians(0))
                        .build());
        //   delay 7 or April 7
        robot.turret.setturretpos(.1);
        Set<Integer> targetTags = new HashSet<>(Arrays.asList(35, 43, 42, 34));
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
        


            if (tagfound) {
                robot.pause(1000);
                //        shoot

                robot.intake.takein();
                robot.transfer.takein();
                robot.thumbz.open();

                robot.pause(2000);
                robot.thumbz.block();
               // robot.shooter.stop();
            }

            //Parking
            Actions.runBlocking(
                    boxermechanumdrive.actionBuilder(boxermechanumdrive.localizer.getPose())
                            .splineToLinearHeading(new Pose2d(28, -48, Math.toRadians(0)), Math.toRadians(-60))
                            .splineToLinearHeading(new Pose2d(24, -94, Math.toRadians(0)), Math.toRadians(0))
                            .splineToLinearHeading(new Pose2d(37, -90, Math.toRadians(0)), Math.toRadians(0))
                            .build());




    }
}