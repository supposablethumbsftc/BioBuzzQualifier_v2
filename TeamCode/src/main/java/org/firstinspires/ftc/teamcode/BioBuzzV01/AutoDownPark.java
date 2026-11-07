package org.firstinspires.ftc.teamcode.BioBuzzV01;

import com.acmerobotics.roadrunner.Pose2d;
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
        robot.turret.setturretpos(.99);
     //   delay 7 or April 7

        robot.pause(7000);
        //        shoot

        robot.intake.takein();
        robot.thumbz.open();

        robot.pause(1000);
        robot.thumbz.block();
        robot.shooter.stop();
      //  robot.turret.setturretpos(.5);
        // close Blockers
//               Collect in corner
        //Turn on intake
        //                flower x=8, y=6, -90


        Actions.runBlocking(
                boxermechanumdrive.actionBuilder(boxermechanumdrive.localizer.getPose())
                       // .splineToLinearHeading(new Pose2d(0,-4,Math.toRadians(0)), Math.toRadians(90))
                        .splineToLinearHeading(new Pose2d(17,6,Math.toRadians(-90)), Math.toRadians(270))
                        .build());
        robot.pause(4000);
        //        shoot


//                far shot
        Actions.runBlocking(
                boxermechanumdrive.actionBuilder(boxermechanumdrive.localizer.getPose())
                        .splineToLinearHeading(new Pose2d(0,0,Math.toRadians(0)), Math.toRadians(270))
                        .build());
        robot.shooter.nearV(voltage);

        robot.turret.setturretpos(.99);
        robot.pause(1000);
        robot.thumbz.open();
        robot.pause(2000);
        robot.thumbz.block();
        robot.shooter.stop();

         //        park/collect nectar

        Actions.runBlocking(
                boxermechanumdrive.actionBuilder(boxermechanumdrive.localizer.getPose())
                        .splineToLinearHeading(new Pose2d(4,10,Math.toRadians(0)), Math.toRadians(0))
                        .splineToLinearHeading(new Pose2d(50,14,Math.toRadians(0)), Math.toRadians(90))
                        .build());

    }

}