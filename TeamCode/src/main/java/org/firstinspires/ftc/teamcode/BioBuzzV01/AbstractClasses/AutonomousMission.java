package org.firstinspires.ftc.teamcode.BioBuzzV01.AbstractClasses;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.BioBuzzV01.Subsystems.CSRobotV01;

/**
 * Created by mkeup on 9/27/2016.
 */
abstract public class AutonomousMission {
    protected LinearOpMode parentOpMode;
    protected Telemetry telemetry;
    public MatchInformation matchInfo;
    private ElapsedTime missionTimer;
    public CSRobotV01 robot;

    public void SetRobot(CSRobotV01 robot)
    {
        this.robot = robot;
    }

//    public UGAutonomousMission()
//    {
//        parentOpMode = null;
//        telemetry = null;
//    }

    public AutonomousMission(LinearOpMode parent, Telemetry telemetry, MatchInformation matchInfo, CSRobotV01 robot)
    {
        parentOpMode = parent;
        this.telemetry = telemetry;
        this.matchInfo = matchInfo;
        this.robot = robot;
    }

//    public UGAutonomousMission(LinearOpMode parent, Telemetry t)
//    {
//        parentOpMode = parent;
//        telemetry = t;
//    }

    public MatchInformation GetMatchInfo()
    {
        if(matchInfo == null){
            matchInfo = new MatchInformation();
        }

        return matchInfo;
    }

    public void SetMatchInfo(MatchInformation matchInfo){

        if(matchInfo==null)
        {
            matchInfo = new MatchInformation();
        }

        this.matchInfo = matchInfo;
    }

    public ElapsedTime getMissionTimer(){
        return this.missionTimer;
    }

    public void setMissionTimer(ElapsedTime missionTimer) {
        this.missionTimer = missionTimer;
    }

    abstract public void run();
}
