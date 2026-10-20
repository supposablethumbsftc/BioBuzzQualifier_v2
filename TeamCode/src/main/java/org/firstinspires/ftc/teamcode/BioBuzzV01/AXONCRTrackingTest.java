package org.firstinspires.ftc.teamcode.BioBuzzV01;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.ServoImplEx;

@Disabled
@TeleOp
public class AXONCRTrackingTest extends LinearOpMode {


    /**
     * Demonstrates using an Axon continuous rotation servo with position feedback
     * to track rotations beyond 360° using wrap detection.
     */

        private CRServo axonCRServo;       // Continuous rotation control
        private ServoImplEx axonFeedback;  // Feedback interface

        // Rotation tracking variables
        private double lastPosition = 0.0; // Last absolute position (0.0–1.0)
        private int rotationCount = 0;     // Number of full rotations completed
        private static final double WRAP_THRESHOLD = 0.5; // Half-turn threshold for wrap detection

        @Override
        public void runOpMode() {
            // Map the CRServo for motion control
            axonCRServo = hardwareMap.get(CRServo.class, "axonServo");

            // Map the same hardware as ServoImplEx for feedback reading
            axonFeedback = (ServoImplEx) hardwareMap.get(com.qualcomm.robotcore.hardware.Servo.class, "axonServo");

            telemetry.addLine("Axon CRServo Rotation Tracker Ready");
            telemetry.update();

            waitForStart();

            while (opModeIsActive()) {
                // Read joystick input for power (-1.0 to 1.0)
                double power = -gamepad1.left_stick_y;

                // Apply power to CRServo
                axonCRServo.setPower(power);

                // Read absolute position feedback (0.0–1.0)
                double currentPos = axonFeedback.getPosition();

                // Detect wrap-around
                double delta = currentPos - lastPosition;
                if (delta > WRAP_THRESHOLD) {
                    // Wrapped backward (e.g., 0.95 → 0.05)
                    rotationCount--;
                } else if (delta < -WRAP_THRESHOLD) {
                    // Wrapped forward (e.g., 0.05 → 0.95)
                    rotationCount++;
                }

                lastPosition = currentPos;

                // Calculate total rotations and degrees
                double totalRotations = rotationCount + currentPos;
                double totalDegrees = totalRotations * 360.0;

                // Telemetry
                telemetry.addData("Power", "%.2f", power);
                telemetry.addData("Absolute Pos", "%.3f", currentPos);
                telemetry.addData("Rotation Count", rotationCount);
                telemetry.addData("Total Rotations", "%.3f", totalRotations);
                telemetry.addData("Total Degrees", "%.1f°", totalDegrees);
                telemetry.update();
            }
        }
    }
