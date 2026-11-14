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
        MecanumDrive boxermechanumdrive = new MecanumDrive(hardwareMap,new Pose2d(
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
//        Red Close Auto No April Tags Open Goal after 3 -------------------
//        Branch 1 - No Partner Auto, 9 ArtifactS           -------------------
//--------------------------------------------------------------------------

//  Turn on shooter in overshoot to start ramping up
     //   voltage = controlHub.getInputVoltage(VoltageUnit.VOLTS);
  //      robot.shooter.overshootnearV(voltage);
   //     robot.hood.off();//hood up
        //        Turn intake on
  //      robot.intake.takein();
//
//        Up – Solo
//                shoot
        //Turn on shooter motor
        //Open Blockers
        // Pause - 3 sec
        voltage = controlHub.getInputVoltage(VoltageUnit.VOLTS);
        robot.shooter.nearV(voltage);
        robot.hood.on();//hood down
//        Turn on intake
        robot.turret.setturretpos(.01);
        robot.pause(4000);
        robot.intake.takein();
        robot.thumbz.open();

        robot.pause(1000);
        robot.thumbz.block();
        robot.shooter.stop();
        robot.turret.setturretpos(.5);
        // close Blockers
//               Collect in corner
        //Turn on intake

        //Drive in Corner 48 to left back 1 1/2
        Actions.runBlocking(
                boxermechanumdrive.actionBuilder(boxermechanumdrive.localizer.getPose())
                       // .splineToLinearHeading(new Pose2d(0,-4,Math.toRadians(0)), Math.toRadians(90))
                        .splineToLinearHeading(new Pose2d(47.5,1.5,Math.toRadians(0)), Math.toRadians(0))
                        .build());
        Actions.runBlocking(
                boxermechanumdrive.actionBuilder(boxermechanumdrive.localizer.getPose())
                        // .splineToLinearHeading(new Pose2d(0,-4,Math.toRadians(0)), Math.toRadians(90))
                        .splineToLinearHeading(new Pose2d(0,0,Math.toRadians(0)), Math.toRadians(0))
                        .build());
        //   delay 7 or April 7
        robot.turret.setturretpos(.01);
        timer.reset();
        tagfound = false;
        while (!tagfound && timer.milliseconds() < 7000) {
            //        Read april tag
            LLResult result = limelight.getLatestResult();
            if (result != null) {
                if (result.isValid()) {
                    // Access fiducial results
                    List<LLResultTypes.FiducialResult> fiducialResults = result.getFiducialResults();
                    for (LLResultTypes.FiducialResult fr : fiducialResults) {
                        AprilTagID = fr.getFiducialId();
                        telemetry.addData("Fiducial", "ID: %d", AprilTagID);
                        if (AprilTagID == 39) {
                            tagfound = true;
                            robot.turret.setturretpos(.01);
                        } else if (AprilTagID == 35) {
                            tagfound = true;
                            robot.turret.setturretpos(.01);
                        }

                        telemetry.update();

                    }
                } else {
                    telemetry.addData("Limelight", "No data available");
                }

                telemetry.update();
            }
        }
        if (tagfound) {
            robot.pause(1000);
            //        shoot

            robot.intake.takein();
            robot.thumbz.open();

            robot.pause(1000);
            robot.thumbz.block();
            robot.shooter.stop();
        }
        //Parking
        Actions.runBlocking(
                boxermechanumdrive.actionBuilder(boxermechanumdrive.localizer.getPose())
                        .splineToLinearHeading(new Pose2d(28,-48,Math.toRadians(0)), Math.toRadians(-60))
                        .splineToLinearHeading(new Pose2d(20,-94,Math.toRadians(0)), Math.toRadians(0))
                        .splineToLinearHeading(new Pose2d( 35,-90,Math.toRadians(0)), Math.toRadians(0))
                        .build());
//                far shot
       /* Actions.runBlocking(
                boxermechanumdrive.actionBuilder(boxermechanumdrive.localizer.getPose())
                        .splineToLinearHeading(new Pose2d(28,-48,Math.toRadians(0)), Math.toRadians(-60))
                     //   .splineToLinearHeading(new Pose2d(24,-98,Math.toRadians(-90)), Math.toRadians(0))
                        .splineToLinearHeading(new Pose2d(8,-118,Math.toRadians(0)), Math.toRadians(0))
                        .build());
        robot.shooter.nearV(voltage);

        robot.turret.setturretpos(.99);
        robot.pause(1000);
        robot.thumbz.open();
        robot.pause(2000);
        robot.thumbz.block();
        robot.shooter.stop();
//                Flower
        Actions.runBlocking(
                boxermechanumdrive.actionBuilder(boxermechanumdrive.localizer.getPose())
                        .splineToLinearHeading(new Pose2d(8,-114,Math.toRadians(-90)), Math.toRadians(-90))
                        .splineToLinearHeading(new Pose2d(8,-118,Math.toRadians(-90)), Math.toRadians(-90))
                        .build());
        robot.pause(4000);
        Actions.runBlocking(
                boxermechanumdrive.actionBuilder(boxermechanumdrive.localizer.getPose())
                        .splineToLinearHeading(new Pose2d(8,-118,Math.toRadians(0)), Math.toRadians(0))
                        .build());

//                Shoot
        robot.turret.setturretpos(.99);
        robot.thumbz.open();
        robot.pause(2000);
        robot.thumbz.block();
//                park/collect nectar
        Actions.runBlocking(
                boxermechanumdrive.actionBuilder(boxermechanumdrive.localizer.getPose())
                        .splineToLinearHeading(new Pose2d(20,-94,Math.toRadians(0)), Math.toRadians(0))
                        .splineToLinearHeading(new Pose2d( 40,-85,Math.toRadians(0)), Math.toRadians(0))
                        .build());

//        Actions.runBlocking(
//                boxermechanumdrive.actionBuilder(boxermechanumdrive.localizer.getPose())
//                        .splineToLinearHeading(new Pose2d(4,-8,Math.toRadians(0)), Math.toRadians(0))
//                        .build());
*/
    }

}