package org.firstinspires.ftc.teamcode.BioBuzzV01.Tests;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.BioBuzzV01.AbstractClasses.MatchInformation;
import org.firstinspires.ftc.teamcode.BioBuzzV01.Subsystems.OpenCVDetector;

@TeleOp
public class OpenCVDetectorTest extends LinearOpMode {
    MatchInformation matchInfo;
    OpenCVDetector detector;

    @Override
    public void runOpMode() throws InterruptedException {
        matchInfo = new MatchInformation();
        detector = new OpenCVDetector(this, matchInfo);
        detector.init(hardwareMap);

        waitForStart();

        while (opModeIsActive()){
            telemetry.addData("Block Position", detector.detectBlockPos());
            telemetry.addData("Average Cr Left", detector.getAvgCrLeft());
            telemetry.addData("Average Cr Center", detector.getAvgCrCenter());
            telemetry.addData("Average Cr Right", detector.getAvgCrRight());

            telemetry.addData("Average Cb Left", detector.getAvgCbLeft());
            telemetry.addData("Average Cb Center", detector.getAvgCbCenter());
            telemetry.addData("Average Cb Right", detector.getAvgCbRight());

            telemetry.addData("Average Y Left", detector.getAvgYLeft());
            telemetry.addData("Average Y Center", detector.getAvgYCenter());
            telemetry.addData("Average Y Right", detector.getAvgYRight());

            telemetry.addData("Red Left", detector.getAvgRedLeft());
            telemetry.addData("Red Center", detector.getAvgRedCenter());
            telemetry.addData("Red Right", detector.getAvgRedRight());

            telemetry.addData("Blue Left", detector.getAvgBlueLeft());
            telemetry.addData("Blue Center", detector.getAvgBlueCenter());
            telemetry.addData("Blue Right", detector.getAvgBlueRight());

            telemetry.addData("Green Left", detector.getAvgGreenLeft());
            telemetry.addData("Green Center", detector.getAvgGreenCenter());
            telemetry.addData("Green Right", detector.getAvgGreenRight());

            telemetry.addData("Hue Left", detector.getAvgHueLeft());
            telemetry.addData("Hue Center", detector.getAvgHueCenter());
            telemetry.addData("Hue Right", detector.getAvgHueRight());

            telemetry.addData("Saturation Left", detector.getAvgSaturationLeft());
            telemetry.addData("Saturation Center", detector.getAvgSaturationCenter());
            telemetry.addData("Saturation Right", detector.getAvgSaturationRight());

            telemetry.addData("Value Left", detector.getAvgValueLeft());
            telemetry.addData("Value Center", detector.getAvgValueCenter());
            telemetry.addData("Value Right", detector.getAvgValueRight());
            telemetry.update();
        }



    }
}
