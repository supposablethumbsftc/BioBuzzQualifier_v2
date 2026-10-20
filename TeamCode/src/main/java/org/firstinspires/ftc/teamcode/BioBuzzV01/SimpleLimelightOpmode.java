package org.firstinspires.ftc.teamcode.BioBuzzV01;

import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.robotcore.external.Telemetry;

import java.util.List;

@Disabled
@TeleOp(name = "Concept: Three Limelight Pipelines abx", group = "Concept")
public class SimpleLimelightOpmode extends OpMode {
    Limelight3A limelight3A;
    //    private List<LLResultTypes.ColorResult> colorResults;
    private double[] cornerX = new double[4];
    private double[] cornerY = new double[4];
    @Override
    public void init() {
        limelight3A = hardwareMap.get(Limelight3A.class, "limelight");
        limelight3A.setPollRateHz(100);
        limelight3A.pipelineSwitch(0);
        limelight3A.start();
        telemetry.setMsTransmissionInterval(11);   // Speed up telemetry updates, Just use for debugging.
        telemetry.setDisplayFormat(Telemetry.DisplayFormat.MONOSPACE);
    }

    @Override
    public void loop() {
        if(gamepad1.a){
            limelight3A.pipelineSwitch(0);
        }else if(gamepad1.b){
            limelight3A.pipelineSwitch(1);
        }else if(gamepad1.x){
            limelight3A.pipelineSwitch(2);
        }
        LLResult llResult = limelight3A.getLatestResult();

        if(llResult != null) {
            telemetry.addData("Pipeline Num", llResult.getPipelineIndex());
            if (llResult.isValid()) {
                telemetry.addData("Tx", llResult.getTx());
                telemetry.addData("Ty", llResult.getTy());
                telemetry.addData("Ta", llResult.getTa());
                telemetry.addData("TxNC", llResult.getTxNC());
                telemetry.addData("TyNC", llResult.getTyNC());
                telemetry.addData("DetectorResults", llResult.getColorResults().get(0).getTargetCorners()); // this works

                // Manually parse corner X/Y arrays
                if (!llResult.getColorResults().isEmpty()) {
                    List<List<Double>> corners = llResult.getColorResults().get(0).getTargetCorners();
                    if (corners != null && corners.size() >= 4) {

                        for (int i = 0; i < 4; i++) {
                            List<Double> point = corners.get(i);
                            if (point != null && point.size() >= 2) {
                                cornerX[i] = point.get(0); // X-coordinate (first element)
                                cornerY[i] = point.get(1); // Y-coordinate (second element)
                            }
                        }
                    }

//                        // Display data
                    telemetry.addData("Corners", "X: %.2f, %.2f, %.2f, %.2f | Y: %.2f, %.2f, %.2f, %.2f",
                            cornerX[0], cornerX[1], cornerX[2], cornerX[3],
                            cornerY[0], cornerY[1], cornerY[2], cornerY[3]);
                }
            }
        }
        telemetry.update();
    }

}