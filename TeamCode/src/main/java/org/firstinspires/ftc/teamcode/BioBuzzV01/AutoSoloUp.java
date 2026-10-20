package org.firstinspires.ftc.teamcode.BioBuzzV01;

import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.ftc.Actions;
import com.qualcomm.hardware.lynx.LynxModule;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.BioBuzzV01.AbstractClasses.MatchInformation;
import org.firstinspires.ftc.teamcode.BioBuzzV01.Subsystems.CSRobotV01;
import org.firstinspires.ftc.teamcode.MecanumDrive;

import java.util.List;

@Autonomous (name="Solo Up",group="test")
public class AutoSoloUp extends LinearOpMode {

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
        robot.pause(3000);
        // close Blockers
//               Collect in corner
        //Turn on intake

        //Drive in Corner 48 to left back 1 1/2
        Actions.runBlocking(
                boxermechanumdrive.actionBuilder(boxermechanumdrive.localizer.getPose())
                        .splineToLinearHeading(new Pose2d(4,0,Math.toRadians(0)), Math.toRadians(90))
                        .splineToLinearHeading(new Pose2d(-1.5,48,Math.toRadians(90)), Math.toRadians(0))
                        .build());
//                far shot
        Actions.runBlocking(
                boxermechanumdrive.actionBuilder(boxermechanumdrive.localizer.getPose())
                        .splineToLinearHeading(new Pose2d(98,24,Math.toRadians(0)), Math.toRadians(-180))
                        .splineToLinearHeading(new Pose2d(124,-7,Math.toRadians(-180)), Math.toRadians(0))
                        .build());
//                Flower

//                Shoot

//                park/collect nectar

//        Actions.runBlocking(
//                boxermechanumdrive.actionBuilder(boxermechanumdrive.localizer.getPose())
//                        .splineToLinearHeading(new Pose2d(4,-8,Math.toRadians(0)), Math.toRadians(0))
//                        .build());

    }

}