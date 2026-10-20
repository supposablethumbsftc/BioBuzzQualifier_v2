package org.firstinspires.ftc.teamcode.BioBuzzV01.Subsystems;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.teamcode.BioBuzzV01.AbstractClasses.MatchInformation;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;
import org.openftc.easyopencv.OpenCvCamera;
import org.openftc.easyopencv.OpenCvCameraFactory;
import org.openftc.easyopencv.OpenCvCameraRotation;
import org.openftc.easyopencv.OpenCvPipeline;

import java.util.Objects;

public class OpenCVDetector extends OpenCvPipeline {
    MatchInformation matchInfo;
    private double numOrangePixels;

    private double avgRed1;
    private double avgGreen1;
    private double avgBlue1;
    private double avgHue1;
    private double avgSaturation1;
    private double avgValue1;
    private double avgCr1;
    private double avgCb1;
    private double avgY1;


    private double avgRed2;
    private double avgGreen2;
    private double avgBlue2;
    private double avgHue2;
    private double avgSaturation2;
    private double avgValue2;
    private double avgCr2;
    private double avgCb2;
    private double avgY2;

    private double avgRed3;
    private double avgGreen3;
    private double avgBlue3;
    private double avgHue3;
    private double avgSaturation3;
    private double avgValue3;
    private double avgCr3;
    private double avgCb3;
    private double avgY3;


    //todo: Figure out correct values for all starting positions
    //was 40
    private final int width = 100;
    private final int height = 70;

    private final int farLeftX = 20;
    private final int farLeftY = 110;

    private final int farCenterX = 110;
    private final int farCenterY = 110;

    private final int farRightX = 200;
    private final int farRightY = 110;

    private final int closeLeftX = 0;
    private final int closeLeftY = 110;

    private final int closeCenterX = 110;
    private final int closeCenterY = 110;

    private final int closeRightX = 220;
    private final int closeRightY = 110;

    private BlockPosition blockPosition =  BlockPosition.NONE;

    private LinearOpMode parentOpMode;

    private MatchInformation.StartPosition startPosition;
    private MatchInformation.AllianceColor allianceColor;

    public enum BlockPosition {
        NONE,
        LEFT,
        CENTER,
        RIGHT
    }

    OpenCvCamera webcam;

    public OpenCVDetector(LinearOpMode parentOpMode, MatchInformation matchInfo){
        this.parentOpMode = parentOpMode;
        this.matchInfo = matchInfo;
        startPosition = matchInfo.myStartPosition;
        allianceColor = matchInfo.myAllianceColor;

    }

    @Override
    public Mat processFrame(Mat input) {
        Mat originalHSV = new Mat();
        Mat originalYCrCb = new Mat();

        Imgproc.cvtColor(input, originalHSV, Imgproc.COLOR_RGB2HSV);
        Imgproc.cvtColor(input, originalYCrCb, Imgproc.COLOR_RGB2YCrCb);

        if (originalYCrCb.empty()) {
            blockPosition = BlockPosition.NONE;
            return input;
        }

        int x1;
        int x2;
        int x3;

        int y1;
        int y2;
        int y3;

        //displays rectangle on screen
        if (Objects.requireNonNull(startPosition) == MatchInformation.StartPosition.FAR) {
            x1 = farLeftX;
            x2 = farCenterX;
            x3 = farRightX;

            y1 = farLeftY;
            y2 = farCenterY;
            y3 = farRightY;
        } else {
            x1 = closeLeftX;
            x2 = closeCenterX;
            x3 = closeRightX;

            y1 = closeLeftY;
            y2 = closeCenterY;
            y3 = closeRightY;
        }

        Imgproc.rectangle(input, new Rect(x1, y1, width, height), new Scalar(0, 255, 0));
        Imgproc.rectangle(input, new Rect(x2, y2, width, height), new Scalar(0, 255, 0));
        Imgproc.rectangle(input, new Rect(x3, y3, width, height), new Scalar(0, 255, 0));

        //Create submatrix region to analyze
        Mat detectionRegionRGB1 = input.submat(y1,y1 + height, x1, x1 + width);
        Mat detectionRegionRGB2 = input.submat(y2,y2 + height, x2, x2 + width);
        Mat detectionRegionRGB3 = input.submat(y3,y3 + height, x3, x3 + width);

        Mat detectionRegionHSV1 = originalHSV.submat(y1,y1 + height, x1, x1 + width);
        Mat detectionRegionHSV2 = originalHSV.submat(y2,y2 + height, x2, x2 + width);
        Mat detectionRegionHSV3 = originalHSV.submat(y3,y3 + height, x3, x3 + width);

        Mat detectionRegionYCrCb1 = originalYCrCb.submat(y1, y1 + height, x1, x1 + width);
        Mat detectionRegionYCrCb2 = originalYCrCb.submat(y2, y2 + height, x2, x2 + width);
        Mat detectionRegionYCrCb3 = originalYCrCb.submat(y3, y3 + height, x3, x3 + width);

        //Find the average amount of red, hue, and Cr
        avgRed1 = Core.mean(detectionRegionRGB1).val[0];
        avgGreen1 = Core.mean(detectionRegionRGB1).val[1];
        avgBlue1 = Core.mean(detectionRegionRGB1).val[2];
        avgHue1 = Core.mean(detectionRegionHSV1).val[0];
        avgSaturation1 = Core.mean(detectionRegionHSV1).val[1];
        avgValue1 = Core.mean(detectionRegionHSV1).val[2];
        avgY1 = Core.mean(detectionRegionYCrCb1).val[0];
        avgCr1 = Core.mean(detectionRegionYCrCb1).val[1];
        avgCb1 = Core.mean(detectionRegionYCrCb1).val[2];

        avgRed2 = Core.mean(detectionRegionRGB2).val[0];
        avgGreen2 = Core.mean(detectionRegionRGB2).val[1];
        avgBlue2 = Core.mean(detectionRegionRGB2).val[2];
        avgHue2 = Core.mean(detectionRegionHSV2).val[0];
        avgSaturation2 = Core.mean(detectionRegionHSV2).val[1];
        avgValue2 = Core.mean(detectionRegionHSV2).val[2];
        avgY2 = Core.mean(detectionRegionYCrCb2).val[0];
        avgCr2 = Core.mean(detectionRegionYCrCb2).val[1];
        avgCb2 = Core.mean(detectionRegionYCrCb2).val[2];

        avgRed3 = Core.mean(detectionRegionRGB3).val[0];
        avgGreen3 = Core.mean(detectionRegionRGB3).val[1];
        avgBlue3 = Core.mean(detectionRegionRGB3).val[2];
        avgHue3 = Core.mean(detectionRegionHSV3).val[0];
        avgSaturation3 = Core.mean(detectionRegionHSV3).val[1];
        avgValue3 = Core.mean(detectionRegionHSV3).val[2];
        avgY3 = Core.mean(detectionRegionYCrCb3).val[0];
        avgCr3 = Core.mean(detectionRegionYCrCb3).val[1];
        avgCb3 = Core.mean(detectionRegionYCrCb3).val[2];

        switch (allianceColor){
            case TEAM_RED:
                if (avgSaturation1 > avgSaturation2){
                    if (avgSaturation1 > avgSaturation3){
                        blockPosition = BlockPosition.LEFT;
                    }else {
                        blockPosition = BlockPosition.RIGHT;
                    }
                }else {
                    if (avgSaturation2 > avgSaturation3){
                        blockPosition = BlockPosition.CENTER;
                    }else {
                        blockPosition = BlockPosition.RIGHT;
                    }
                }
                break;
            case TEAM_BLUE:
                if (avgSaturation1 > avgSaturation2){
                    if (avgSaturation1 > avgSaturation3){
                        blockPosition = BlockPosition.LEFT;
                    }else {
                        blockPosition = BlockPosition.RIGHT;
                    }
                }else {
                    if (avgSaturation2 > avgSaturation3){
                        blockPosition = BlockPosition.CENTER;
                    }else {
                        blockPosition = BlockPosition.RIGHT;
                    }
                }
                break;
        }


        return input;

    }

    public void init(HardwareMap hardwareMap){
        int cameraMonitorViewId = hardwareMap.appContext.getResources().getIdentifier("cameraMonitorViewId", "id", hardwareMap.appContext.getPackageName());

//        parentOpMode.telemetry.addData("Status", "Initialized cameraMontiroViewId");
//        parentOpMode.telemetry.update();

        webcam = OpenCvCameraFactory.getInstance().createWebcam(hardwareMap.get(WebcamName.class, "Webcam 1"), cameraMonitorViewId);

//        parentOpMode.telemetry.addData("Status", "initized webcam");
//        parentOpMode.telemetry.update();

        webcam.setPipeline(this);

//        parentOpMode.telemetry.addData("Status", "Initialized pipeline");
//        parentOpMode.telemetry.update();

        webcam.openCameraDeviceAsync(new OpenCvCamera.AsyncCameraOpenListener()
        {
            @Override
            public void onOpened()
            {
                /*
                 * Tell the webcam to start streaming images to us! Note that you must make sure
                 * the resolution you specify is supported by the camera. If it is not, an exception
                 * will be thrown.
                 *
                 * Keep in mind that the SDK's UVC driver (what OpenCvWebcam uses under the hood) only
                 * supports streaming from the webcam in the uncompressed YUV image format. This means
                 * that the maximum resolution you can stream at and still get up to 30FPS is 480p (640x480).
                 * Streaming at e.g. 720p will limit you to up to 10FPS and so on and so forth.
                 *
                 * Also, we specify the rotation that the webcam is used in. This is so that the image
                 * from the camera sensor can be rotated such that it is always displayed with the image upright.
                 * For a front facing camera, rotation is defined assuming the user is looking at the screen.
                 * For a rear facing camera or a webcam, rotation is defined assuming the camera is facing
                 * away from the user.
                 */

//                parentOpMode.telemetry.addData("Status", "Before Streaming");
//                parentOpMode.telemetry.update();

                webcam.startStreaming(320, 240, OpenCvCameraRotation.UPRIGHT);

                //parentOpMode.sleep(1000);

//                parentOpMode.telemetry.addData("Status", "After Streaming");
//                parentOpMode.telemetry.update();
            }

            @Override
            public void onError(int errorCode) {

            }
        });
    }

    public String detectBlockPos(){
        parentOpMode.sleep(100);

        switch (blockPosition){
            case NONE:
                return "No Element Detected";
            case LEFT:
                return "LEFT";
            case CENTER:
                return "CENTER";
            case RIGHT:
                return "RIGHT";
            default:
                return "Error";
        }

    }

    public void setStartLeft(){
        startPosition = MatchInformation.StartPosition.CLOSE;
    }

    public void setStartRight(){
        startPosition = MatchInformation.StartPosition.FAR;
    }

    public double getNumOrangePixels(){
        return numOrangePixels;
    }


    public double getAvgHueLeft() {
        return avgHue1;
    }
    public double getAvgCrLeft(){
        return avgCr1;
    }
    public double getAvgRedLeft(){
        return avgRed1;
    }
    public double getAvgBlueLeft() {
        return avgBlue1;
    }
    public double getAvgCbLeft() {
        return avgCb1;
    }
    public double getAvgGreenLeft() {
        return avgGreen1;
    }
    public double getAvgSaturationLeft() {
        return avgSaturation1;
    }
    public double getAvgValueLeft() {
        return avgValue1;
    }
    public double getAvgYLeft() {
        return avgY1;
    }

    public double getAvgHueCenter() {
        return avgHue2;
    }
    public double getAvgCrCenter(){
        return avgCr2;
    }
    public double getAvgRedCenter(){
        return avgRed2;
    }
    public double getAvgBlueCenter() {
        return avgBlue2;
    }
    public double getAvgCbCenter() {
        return avgCb2;
    }
    public double getAvgGreenCenter() {
        return avgGreen2;
    }
    public double getAvgSaturationCenter() {
        return avgSaturation2;
    }
    public double getAvgValueCenter() {
        return avgValue2;
    }
    public double getAvgYCenter() {
        return avgY2;
    }


    public double getAvgHueRight() {
        return avgHue3;
    }
    public double getAvgCrRight(){
        return avgCr3;
    }
    public double getAvgRedRight(){
        return avgRed3;
    }
    public double getAvgBlueRight() {
        return avgBlue3;
    }
    public double getAvgCbRight() {
        return avgCb3;
    }
    public double getAvgGreenRight() {
        return avgGreen3;
    }
    public double getAvgSaturationRight() {
        return avgSaturation3;
    }
    public double getAvgValueRight() {
        return avgValue3;
    }
    public double getAvgYRight() {
        return avgY3;
    }


    public BlockPosition getBlockPos() {
        return blockPosition;
    }


}
