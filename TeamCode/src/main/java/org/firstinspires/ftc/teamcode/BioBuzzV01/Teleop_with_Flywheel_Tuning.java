package org.firstinspires.ftc.teamcode.BioBuzzV01;

import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.LLResultTypes;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.hardware.lynx.LynxModule;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.VoltageUnit;
import org.firstinspires.ftc.teamcode.BioBuzzV01.AbstractClasses.MatchInformation;
import org.firstinspires.ftc.teamcode.BioBuzzV01.Subsystems.CSRobotV01;

import java.util.List;

@TeleOp
public class Teleop_with_Flywheel_Tuning extends OpMode {
    MatchInformation matchInfo;
    /**
     * Gamepad 1 controls the driving (field centric)
     * Gamepad 2 controls the lift and the arm (triggers control lift right for up and down for left)
     * for the arm dpad down makes arm go down and dpad up goes up
     * Gamepad 1 controls the up down movement of wrist(x is up, a is down, and b is middle)
     * Gamepad 2 controls the other wrist movements (Right bumper is up, left bumper is down)
     * Gamepad 2 controls the twisting (dpad down is left, dpad up is right)
     */
    public CSRobotV01 robot;
    double SpeedFactor = 1;
    double StrafeSpeedFactor = 1;
    boolean firstLoop = true;
    boolean DetectPixelColor1 = false;
    boolean fieldCentric = true;
    double heading = 0;
    double headingOffset = 0;
    int liftencoder = 0;
    double turretPos = 0.5;

    boolean shooter_is_on = false;
    boolean shooter_is_on_low = false;
    boolean shooter_is_on_high = false;

    boolean intake_is_on = false;

    double shootervelocity = 0;
    int AprilTagID = 0;
    double AprilTagTx = 0;
    Limelight3A limelight;

    ElapsedTime SM_Timer = new ElapsedTime();

    private LynxModule controlHub;
    public double voltage = 0;

    double F=2.72;
    double P=1.7;
    double D=23.0;
    double[] StepSizes = {10,1,.1,.01,.001,.0001};
    int stepIndex = 0;

//
//    public class SensorTouch extends Teleop {
//        TouchSensor touchSensor;  // Touch sensor Object

    @Override
    public void init() {
        matchInfo = new MatchInformation();
        robot = new CSRobotV01(hardwareMap, null, matchInfo);
        limelight = hardwareMap.get(Limelight3A.class, "limelight");
        limelight.setPollRateHz(100);
        limelight.pipelineSwitch(5);
        limelight.start();
        telemetry.setMsTransmissionInterval(11);   // Speed up telemetry updates, Just use for debugging.
        telemetry.setDisplayFormat(Telemetry.DisplayFormat.MONOSPACE);
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
    }

    @Override
    public void loop() {
        if (firstLoop) {
            robot.motorInit();
            robot.teleopServoInit();
            firstLoop = false;
        }

        liftencoder = robot.lift.currentLiftPosition;

//        handleResetMotors();
        handleDrivetrain();
//        handleTurret();
        handleLift();
        handleIntake();
        handleTelemetry();
        handleBlinkinColors();
        handleButtons();
        handleShooter();
        handleHood();
        handleFlyWheelTuningButtons();
        voltage = controlHub.getInputVoltage(VoltageUnit.VOLTS);
    }


    private void handleFlyWheelTuningButtons() {

        if(gamepad2.bWasPressed()){
            stepIndex = (stepIndex+1)%StepSizes.length;
        }

        if(gamepad2.dpadLeftWasPressed()){
            F -= StepSizes[stepIndex];
        }
        if(gamepad2.dpadRightWasPressed()){
            F += StepSizes[stepIndex];
        }

        if(gamepad2.xWasPressed()){
            D -= StepSizes[stepIndex];
        }
        if(gamepad2.yWasPressed()){
            D += StepSizes[stepIndex];
        }

            if(gamepad2.dpadUpWasPressed()){
            P += StepSizes[stepIndex];
        }
        if(gamepad2.dpadDownWasPressed()){
            P -= StepSizes[stepIndex];
        }

        //set new PIDcoefficients
        robot.shooter.setVelocityPID(P,0,0,F);

        telemetry.addData("Tuning P","%.4f (Dpad-U/D)",P);
        telemetry.addData("Tuning D","%.4f (-X/Y button)",D);
        telemetry.addData("Tuning F","%.4f (Dpad-L/R)",F);
        telemetry.addData("Step Size","%.4f (B Button)",StepSizes[stepIndex]);
        telemetry.addLine( "------------------------------");
    }
    private void handleBlinkinColors() {
//        if (robot.intake.getSampleGreen()>900)
//        robot.blinkins.yellow();
//        else if (robot.intake.getSampleBlue()>500)
//            robot.blinkins.blue();
//        else if (robot.intake.getSampleRed()>500)
//            robot.blinkins.red();
//        else
//            robot.blinkins.black();
    }

    private void handleResetMotors() {
        if (gamepad1.left_stick_button && gamepad1.right_stick_button)
        {
//            robot.arm.EncoderInit();
            robot.lift.EncoderInit();
//            robot.arm.QuickArmInit();
            robot.lift.motorInit();
        }
    }
    private void handleDrivetrain() {
        if (Math.abs(gamepad1.left_stick_y) > 0.1 || Math.abs(gamepad1.left_stick_x) > 0.1 || Math.abs(gamepad1.right_stick_y) > 0.1 || Math.abs(gamepad1.right_stick_x) > 0.1) {

            double r;
            double robotAngle;
            //Drives/strafes robot based on absolute field position with gyro
            if (fieldCentric) {
                r = Math.hypot(-gamepad1.left_stick_x, gamepad1.left_stick_y);
                //adds
                robotAngle = (2 * Math.PI) - heading + Math.atan2(gamepad1.left_stick_y, -gamepad1.left_stick_x) - (Math.PI / 4);


            } else {
                r = Math.hypot(-gamepad1.left_stick_x, gamepad1.left_stick_y);
                robotAngle = Math.atan2(gamepad1.left_stick_y, -gamepad1.left_stick_x) - Math.PI / 4;


            }
            double rightX = -gamepad1.right_stick_x;
            double rightY = gamepad1.right_stick_y;
            final double vFL = StrafeSpeedFactor * r * Math.cos(robotAngle) + SpeedFactor * rightX + SpeedFactor * rightY;

            final double vFR = StrafeSpeedFactor * r * Math.sin(robotAngle) - SpeedFactor * rightX + SpeedFactor * rightY;
            final double vBL = StrafeSpeedFactor * r * Math.sin(robotAngle) + SpeedFactor * rightX + SpeedFactor * rightY;
            final double vBR = StrafeSpeedFactor * r * Math.cos(robotAngle) - SpeedFactor * rightX + SpeedFactor * rightY;

            robot.drivetrain.setSpeedForEachMotor(vFR, vFL, vBR, vBL);

        } else {
            robot.drivetrain.setSpeedForEachMotor(0, 0, 0, 0);
        }
    }

    private void handleTurret() {
        if (gamepad2.dpad_right || gamepad2.dpad_left) { // use limelight camera to track April tags on goals
            LLResult result = limelight.getLatestResult();
            if (result != null) {
                telemetry.addData("Limelight", "Data available!");
                if (result.isValid()) {
                    telemetry.addData("Limelight", "result is Valid!");
                    // Access fiducial results
                    List<LLResultTypes.FiducialResult> fiducialResults = result.getFiducialResults();
                    for (LLResultTypes.FiducialResult fr : fiducialResults) {
                        AprilTagID = fr.getFiducialId();
                        AprilTagTx = fr.getTargetXDegrees();
                        telemetry.addData("Fiducial", "ID: %d, Family: %s, X: %.2f, Y: %.2f, Area %.2f", fr.getFiducialId(), fr.getFamily(), fr.getTargetXDegrees(), fr.getTargetYDegrees(), fr.getTargetArea());
                        if (gamepad2.dpad_right && AprilTagID == 24) {  //red right goal
                            telemetry.addData("Limelight", "Red tag detected");
                            if (AprilTagTx > 0.5) {
                                turretPos = turretPos - Math.abs(AprilTagTx*0.00025);
                                    if (turretPos < .01) {
                                        turretPos=0.01;
                                    }
                                robot.turret.setturretpos(turretPos); // turn turret Right
                            } else if (AprilTagTx < -0.5) {
                                turretPos = turretPos +  Math.abs(AprilTagTx*0.00025);
                                if (turretPos > .99) {
                                    turretPos=0.99;
                                }
                                robot.turret.setturretpos(turretPos); // turn turret Left
                            } else { // already locked in
                                robot.blinkins.red();
                                // hold position
                            }
                        } else if (gamepad2.dpad_left && AprilTagID == 20) { //blue left goal
                            telemetry.addData("Limelight", "Blue tag detected");
                            if (AprilTagTx > 0.5) {
                                turretPos = turretPos -  Math.abs(AprilTagTx*0.00025);
                                if (turretPos < .01) {
                                    turretPos=0.01;
                                }
                                robot.turret.setturretpos(turretPos); // turn turret Right
                            } else if (AprilTagTx < -0.5) {
                                turretPos = turretPos +  Math.abs(AprilTagTx*0.00025);
                                if (turretPos > .99) {
                                    turretPos=0.99;
                                }
                                robot.turret.setturretpos(turretPos); // turn turret Left
                            } else { // already locked in
                                robot.blinkins.blue();
                                // hold position
                            }
                        } else {
                            // hold position
                        }
//                        telemetry.update();
                    }
                } else {
                    telemetry.addData("Limelight", "result is invalid");
                    // hold position
                }

//                telemetry.update();
                // hold position
            } else {
                telemetry.addData("Limelight", "No data available");
                // hold position
            }
        } else if (gamepad1.x && turretPos < 0.99) {
            turretPos = turretPos+0.005;
            robot.turret.setturretpos(turretPos);
//                 robot.cookie.ballleft();
        } else if (gamepad1.b && turretPos > 0.01) {
            turretPos = turretPos-.005;
            robot.turret.setturretpos(turretPos);
        } else if (gamepad1.y) {
            turretPos = 0.5;
            robot.turret.setturretpos(turretPos);
            }
        telemetry.addData("turretPos",turretPos );
//        telemetry.update();
        }



    private void handleHood() {
        if (gamepad1.right_trigger>.05) {
            robot.hood.on();//hood down
        } else if (gamepad1.left_trigger>.05) {
            robot.hood.off();//hood up
            }
        }

    private void handleButtons() {
//---------------------- ABXY commands-----
        if (!gamepad2.x && !gamepad2.y){ //check if state machine buttons are pressed instead
            if (gamepad1.a) {
                 robot.thumbz.open();
//            } else if (gamepad1.b) {
//                 robot.cookie.ballright();
//            } else if (gamepad1.x) {
//                robot.turret.setturretpos(0.25);
////                 robot.cookie.ballleft();
//            } else if (gamepad1.y) {
//                robot.turret.setturretpos(0.75);
//                 robot.cookie.ballcenter();
//             }else if(gamepad2.b){
//                 robot.cookie.ballhalfleft();
 //            }else if(gamepad2.a){
   //              robot.cookie.ballalmostleft();
             } else{
                 robot.thumbz.block();

            }
        }

    }

    private void handleShooter() {
        if ((gamepad1.right_stick_button||gamepad1.left_stick_button) && shooter_is_on) {
            shootervelocity = 0;
            robot.shooter.setVelocity(shootervelocity);
            shooter_is_on = false;
            shooter_is_on_low = false;
            shooter_is_on_high = false;
        }else if (gamepad1.dpad_left&&(shootervelocity > .01)) {
            shootervelocity = shootervelocity - .001;
            robot.shooter.setVelocity(shootervelocity);
            shooter_is_on = true;
            shooter_is_on_low = false;
            shooter_is_on_high = false;
        }else if (gamepad1.dpad_right&&(shootervelocity < 0.99)) {
            shootervelocity = shootervelocity + .001;
            robot.shooter.setVelocity(shootervelocity);
            shooter_is_on = true;
            shooter_is_on_low = false;
            shooter_is_on_high = false;
        } else if (gamepad1.dpad_down||shooter_is_on_low) {
            shootervelocity = 0.5*(13.25/voltage);
            robot.shooter.setVelocity(shootervelocity);
            shooter_is_on = true;
            shooter_is_on_low = true;
        } else if (gamepad1.dpad_up||shooter_is_on_high) {
            shootervelocity = 1.00*(13.25/voltage);
            robot.shooter.setVelocity(shootervelocity);
            shooter_is_on = true;
            shooter_is_on_high = true;
        }
    }
    private void handleLift() {
        if (gamepad1.right_trigger>.05 && gamepad2.right_trigger>.05) {
            robot.lift.moveLift(1);//ascention of robot
            robot.liftlock.uplock();
        } else if (gamepad1.left_trigger>.05 && gamepad2.left_trigger>.05) {
            robot.lift.moveLift(-1);//descention of robot
            robot.liftlock.downlock();
        }else{
//            robot.lift.moveLift(0);
        }

    }


    private void handleTelemetry() {
          telemetry.addData("shooter velocity",shootervelocity );
          telemetry.addData("Control Hub Voltage", "%.2f V", voltage);
          telemetry.addData("turretPos",turretPos );
          telemetry.update();
    }

    private void handleIntake() {
    //    double currentLeftLiftPosition = robot.lift.currentLeftLiftPosition;
//if ((gamepad1.left_bumper && gamepad1.right_bumper) && intake_is_on) {
    if (gamepad1.left_bumper && gamepad1.right_bumper) {
            robot.intake.stop();
            intake_is_on = false;
        } else if (gamepad1.left_bumper) {
            robot.intake.takeout();
            intake_is_on = true;
        } else if (gamepad1.right_bumper) {
            robot.intake.takein();
            intake_is_on = true;
        }

    }

    private void handleHeading() {

        if (gamepad1.left_stick_button) {
            headingOffset = -robot.drivetrain.getExternalHeading();
        }

        heading = robot.drivetrain.getExternalHeading() + headingOffset;

        if (heading < 0) {
            heading = (2 * Math.PI) + heading;
        } else if (heading >= (2 * Math.PI)) {
            heading = heading - (2 * Math.PI);
        }
    }




}


