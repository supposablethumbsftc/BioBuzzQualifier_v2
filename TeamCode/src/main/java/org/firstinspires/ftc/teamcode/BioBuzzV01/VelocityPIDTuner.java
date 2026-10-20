package org.firstinspires.ftc.teamcode.BioBuzzV01;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.LLResultTypes;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.PIDFCoefficients;
import com.qualcomm.robotcore.hardware.VoltageSensor;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.BioBuzzV01.AbstractClasses.MatchInformation;
import org.firstinspires.ftc.teamcode.BioBuzzV01.Subsystems.CSRobotV01;
import org.firstinspires.ftc.teamcode.TuningController;

import java.util.List;

@Config
@Disabled
@TeleOp
public class VelocityPIDTuner extends OpMode {
    public static PIDFCoefficients MOTOR_VELO_PID = new PIDFCoefficients(0, 0, 0, 0);

    private final FtcDashboard dashboard = FtcDashboard.getInstance();

    private VoltageSensor batteryVoltageSensor;
    public double lastKp = 0.0;
    public double lastKi = 0.0;
    public double lastKd = 0.0;
    public double lastKf = 0.0;

    public double targetVelo = 0.0;
    public double motorVelo = 0.0;

    TuningController tuningController = new TuningController();

    MatchInformation matchInfo;

    public CSRobotV01 robot;
    double SpeedFactor = 1;
    double StrafeSpeedFactor = 1;
    boolean firstLoop = true;
    boolean DetectPixelColor1 = false;
    boolean fieldCentric = true;
    double heading = 0;
    double headingOffset = 0;
    int liftencoder = 0;
    double twistvalue = 0.5;

    boolean shooter_is_on = false;

    boolean intake_is_on = false;

    double shootervelocity = 0;
    int AprilTagID = 0;
    double AprilTagTx = 0;
    Limelight3A limelight;

    ElapsedTime SM_Timer = new ElapsedTime();
    public enum ShooterState {
        SSM_LEFT_START,
        SSM_LEFT_OPEN,
        SSM_LEFT_BLOCK,
        SSM_CENTER_DELAY,
        SSM_CENTER_OPEN,
        SSM_CENTER_BLOCK,
        SSM_RIGHT_DELAY,
        SSM_RIGHT_OPEN,
        SSM_RIGHT_BLOCK
    };
    ShooterState shooterState = ShooterState.SSM_LEFT_START;


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

        robot.shooter.setVelocityPID(0,0,0,0);//MOTOR_VELO_PID);



        lastKp = 0.0;
        lastKi = 0.0;
        lastKd = 0.0;
        lastKf = getMotorVelocityF();

        telemetry = new MultipleTelemetry(telemetry, dashboard.getTelemetry());

        telemetry.addLine("Ready!");
        telemetry.update();
        telemetry.clearAll();
    }

    @Override
    public void loop() {
//        public void runOpMode() throws InterruptedException {
        if (firstLoop) {
            robot.motorInit();
            robot.teleopServoInit();
            firstLoop = false;
        }

        liftencoder = robot.lift.currentLiftPosition;

////        handleResetMotors();
//        handleDrivetrain();
////        handleWinch();
//        handleLift();
//        handleIntake();
//        handleTelemetry();
//        handleBlinkinColors();
//        handleButtons();
//        handleShooter();
//        handleHood();

        targetVelo = tuningController.update();
        robot.shooter.setVelocity(targetVelo/5400);

        telemetry.addData("targetVelocity", targetVelo);

        motorVelo = robot.shooter.getVelocity();
        telemetry.addData("velocity", -motorVelo);
        telemetry.addData("error", targetVelo - motorVelo);

        telemetry.addData("upperBound", TuningController.rpmToTicksPerSecond(TuningController.TESTING_MAX_SPEED * 1.15));
        telemetry.addData("lowerBound", 0);

        if (lastKp != MOTOR_VELO_PID.p || lastKi != MOTOR_VELO_PID.i || lastKd != MOTOR_VELO_PID.d || lastKf != MOTOR_VELO_PID.f) {
            robot.shooter.setVelocityPID(MOTOR_VELO_PID.p,MOTOR_VELO_PID.i,MOTOR_VELO_PID.d,MOTOR_VELO_PID.f);

            lastKp = MOTOR_VELO_PID.p;
            lastKi = MOTOR_VELO_PID.i;
            lastKd = MOTOR_VELO_PID.d;
            lastKf = MOTOR_VELO_PID.f;
        }

        tuningController.update();
        telemetry.update();

    }

//    private void setPIDFCoefficients(DcMotorEx motor, PIDFCoefficients coefficients) {
//        motor.setPIDFCoefficients(DcMotor.RunMode.RUN_USING_ENCODER, new PIDFCoefficients(
//                coefficients.p, coefficients.i, coefficients.d, coefficients.f * 12 / batteryVoltageSensor.getVoltage()
//        ));
//    }

    public static double getMotorVelocityF() {
        // see https://docs.google.com/document/d/1tyWrXDfMidwYyP_5H4mZyVgaEswhOC35gvdmP-V-5hA/edit#heading=h.61g9ixenznbx
        return 32767 * 60.0 / (TuningController.MOTOR_MAX_RPM * TuningController.MOTOR_TICKS_PER_REV);
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

        } else if (gamepad2.dpad_right || gamepad2.dpad_left) { // use limelight camera to track April tags on goals
                LLResult result = limelight.getLatestResult();
                if (result != null) {
                    if (result.isValid()) {
                        // Access fiducial results
                        List<LLResultTypes.FiducialResult> fiducialResults = result.getFiducialResults();
                        for (LLResultTypes.FiducialResult fr : fiducialResults) {
                            AprilTagID = fr.getFiducialId();
                            AprilTagTx = fr.getTargetXDegrees();
                            telemetry.addData("Fiducial", "ID: %d, Family: %s, X: %.2f, Y: %.2f, Area %.2f", fr.getFiducialId(), fr.getFamily(), fr.getTargetXDegrees(), fr.getTargetYDegrees(), fr.getTargetArea());
                            if (gamepad2.dpad_right && AprilTagID == 24) {  //red right goal
                                if (AprilTagTx > 5) {
                                    robot.drivetrain.setSpeedForEachMotor(.5, -.5, .5, -.5); // turn Right
                                } else if (AprilTagTx < -5) {
                                    robot.drivetrain.setSpeedForEachMotor(-.5, .5, -.5, .5); // turn Left
                                } else { // lock in place
                                    robot.drivetrain.setSpeedForEachMotor(-0.5, -0.5, 0.5, 0.5); // hold position
                                        }
                                robot.blinkins.red();
                            } else if (gamepad2.dpad_left && AprilTagID == 20) { //blue left goal
                                if (AprilTagTx > 5) {
                                    robot.drivetrain.setSpeedForEachMotor(.5, -.5, .5, -.5); // turn Right
                                } else if (AprilTagTx < -5) {
                                    robot.drivetrain.setSpeedForEachMotor(-.5, .5, -.5, .5); // turn Left
                                } else { // lock in place
                                    robot.drivetrain.setSpeedForEachMotor(-0.5, -0.5, 0.5, 0.5); // hold position
                                }
                                robot.blinkins.blue();
                            } else {
                                robot.blinkins.black();
                                robot.drivetrain.setSpeedForEachMotor(0, 0, 0, 0);
                            }
                            telemetry.update();
                        }
                    } else {
                        telemetry.addData("Limelight", "No data available");
                        robot.drivetrain.setSpeedForEachMotor(0, 0, 0, 0);
                    }

                    telemetry.update();
                    robot.drivetrain.setSpeedForEachMotor(0, 0, 0, 0);
                }
        } else {
            robot.drivetrain.setSpeedForEachMotor(0, 0, 0, 0);
        }
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
        if (!gamepad2.x && !gamepad2.y){
            if (gamepad1.a) {
                 robot.thumbz.open();
            } else if (gamepad1.b) {
//                 robot.cookie.ballright();
            } else if (gamepad1.x) {
//                 robot.cookie.ballleft();
            } else if (gamepad1.y) {
//                 robot.cookie.ballcenter();
             }else if(gamepad2.b){
//                 robot.cookie.ballhalfleft();
             }else if(gamepad2.a){
//                 robot.cookie.ballalmostleft();
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
        } else if (gamepad1.dpad_down) {
            shootervelocity = 0.44;
            robot.shooter.setVelocity(shootervelocity);
            shooter_is_on = true;
        } else if (gamepad1.dpad_up) {
            shootervelocity = 0.615;
            robot.shooter.setVelocity(shootervelocity);
            shooter_is_on = true;
        }else if (gamepad1.dpad_left&&(shootervelocity > .01)) {
            shootervelocity = shootervelocity - .001;
            robot.shooter.setVelocity(shootervelocity);
        }else if (gamepad1.dpad_right&&(shootervelocity < 0.99)) {
            shootervelocity = shootervelocity + .001;
            robot.shooter.setVelocity(shootervelocity);
            shooter_is_on = true;
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
//          telemetry.addData("Ticks per rev",);
//        telemetry.addData("left par0 encoder", (robot.drivetrain));
//        telemetry.addData("Current left lift Position", robot.lift.currentLeftLiftPosition);
//        telemetry.addData("Current right lift Position", robot.lift.currentRightLiftPosition);
//        telemetry.addData("imu", robot.drivetrain.getExternalHeading());
//        telemetry.addData("Current Arm Position", robot.arm.currentArmPosition);
//        telemetry.addData("Current Winch Position", robot.winch.currentWinchPosition);
//        telemetry.addData("touch sensor", robot.lift.touch_sensor.isPressed());
//        telemetry.addData("Red",robot.intake.getSampleRed());
//        telemetry.addData("Green",robot.intake.getSampleGreen());
//        telemetry.addData("Blue",robot.intake.getSampleBlue());
//        telemetry.addData("Alpha",robot.intake.getSampleAlpha());
        telemetry.update();
    }

    private void handleIntake() {
    //    double currentLeftLiftPosition = robot.lift.currentLeftLiftPosition;
if ((gamepad1.left_bumper && gamepad1.right_bumper) && intake_is_on) {
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


