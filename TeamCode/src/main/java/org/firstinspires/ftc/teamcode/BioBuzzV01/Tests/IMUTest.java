package org.firstinspires.ftc.teamcode.BioBuzzV01.Tests;

import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.ColorSensor;

import org.firstinspires.ftc.robotcore.external.navigation.YawPitchRollAngles;

@TeleOp
//@Disabled
public class IMUTest extends OpMode {

    public BNO055IMU imu;
    public ColorSensor colorSensor;
    private YawPitchRollAngles myRobotOrientation;

    @Override
    public void init() {
//        imu = hardwareMap.get(BNO055IMU.class, "imu");
//        BNO055IMU.Parameters parameters = new BNO055IMU.Parameters();
//        parameters.angleUnit = BNO055IMU.AngleUnit.DEGREES;
//        imu.initialize(parameters);
        boolean bLedOn = true;
        colorSensor = hardwareMap.get(ColorSensor.class, "color");
    }

    @Override
    public void loop() {
      //  telemetry.addData("Yaw",imu.getAngularOrientation().firstAngle);
        //telemetry.addData("LED", bLedOn ?

        telemetry.addData("Clear", colorSensor.alpha());
        telemetry.addData("Red", colorSensor.red());
        telemetry.addData("Green", colorSensor.green());
        telemetry.addData("Blue", colorSensor.blue());
        telemetry.update();
        if(colorSensor.alpha() < 200) {
            //moveflagup
            colorSensor.enableLed(false);
        }
        else  colorSensor.enableLed(true);


    }
}
