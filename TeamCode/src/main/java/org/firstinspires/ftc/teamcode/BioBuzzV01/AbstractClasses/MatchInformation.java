package org.firstinspires.ftc.teamcode.BioBuzzV01.AbstractClasses;
import android.os.Environment;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.Date;

/**
 * Reads match information from a file on the phone for running autonomous missions.  The file is created by an app we wrote
 */
public class MatchInformation
{

    public boolean isReadyToCollectSample = false;

    /**
     * True enables logging through the android system
     */
    public boolean enableLogging;


    /**
     * Alliance color read from the settings file
     */
    public AllianceColor myAllianceColor;

    /**
     * The autonomous mission read from the settings file
     */
    public Mission myMission;

    /**
     * The start position read from the settings file
     */
    public StartPosition myStartPosition;

    /**
     * The delay time read from the settings file
     */
    public int myDelayTime;

    public static int visionProcessingMs = 750; //1000

    public int myReachEncoderValue;

    private static final String autoConfigDirectory = Environment.getExternalStorageDirectory() + File.separator + "Thumbs4106";
    private static final String autoConfigFile = "Settings.txt";


    /**
     * Possible start positions for an autonomous mission
     */
    public enum StartPosition{

        CLOSE,

        FAR
    }

    public double PID_pVal;


    public static String[] getAllStartPositions(){
        StartPosition[] sp = StartPosition.values();
        String[] psArray = new String[sp.length];

        for(int i = 0; i < sp.length; i++){
            psArray[i] = sp[i].name();
        }
        return psArray;
    }


    /**
     * Possible alliance colors
     */
    public enum AllianceColor{
        /**
         * Red alliance
         */
        TEAM_RED,
        /**
         * Blue alliance
         */
        TEAM_BLUE,

    }

    public static String[] getAllAlliance(){
        AllianceColor[] ac = AllianceColor.values();
        String[] sArray = new String[ac.length];

        for(int i = 0; i < ac.length; i++){
            sArray[i] = ac[i].name();
        }

        return sArray;
    }
    /**
     * Possible autonomous missions
     */
    public enum Mission {
        No_Collection,
        One_Collection

    }
    public enum MissionsWiState {
        CRATER_CYCLE,
        CRATER_3X_CYCLE_SAMPLE,
        CRATER_CYCLE_2_GOLD,
        DEPOT_SCORE_GOLD,
        CRATER_NO_CYCLE_FULL_AUTO

    }

    public static String[] getAllMissions(){
        Mission[] vals = Mission.values();
        String[] sArray = new String[vals.length];

        for(int i = 0; i < vals.length; i++){
            sArray[i] = vals[i].name();
        }

        return sArray;
    }

    public static Mission StringToMission(String mission){
        Mission[] vals = Mission.values();
        Mission selectedMission = Mission.One_Collection;
        for(int i = 0; i < vals.length; i++){
            if(vals[i].name().equals(mission)){
                selectedMission = vals[i];
                break;
            }
        }

        return selectedMission;
    }
    public String myDetectedGoldPosition = "UNKNOWN";

    public static String ColorToString(AllianceColor color){
        String clr = "Invalid";

        switch (color){
            case TEAM_RED:
                clr = "TEAM_RED";
                break;
            case TEAM_BLUE:
                clr = "TEAM_BLUE";
                break;
        }

        return clr;
    }

    public static String StartPostionToString(StartPosition startPos){
        String tPos = "";
        switch (startPos){
            case CLOSE:
                tPos = "LEFT";
                break;
            case FAR:
                tPos = "RIGHT";
                break;
        }
        return tPos;
    }

    /**
     * Read a settings file created by our android app for running autonomous missions
     */
    public MatchInformation()
    {
        myMission = Mission.One_Collection; //if this is not set, don't run autonomous
        myStartPosition = StartPosition.CLOSE;
        myDelayTime = 0;
        enableLogging = false;
        myAllianceColor = AllianceColor.TEAM_RED;
        PID_pVal = 0;
        ReadSettingsFile();

    }

    /**
     * Get the team color from the settings file
     * @return team color
     */
    public AllianceColor getTeamColor()
    {
        return myAllianceColor == AllianceColor.TEAM_BLUE  ? AllianceColor.TEAM_BLUE: AllianceColor.TEAM_RED;
    }


    /**
     * Read the settings file from the phone file system
     */
    public void ReadSettingsFile()
    {
        String filename = Environment.getExternalStorageDirectory()+"/Thumbs4106/Settings.txt";

        try {
            FileInputStream fis = new FileInputStream(filename);
            InputStreamReader isr = new InputStreamReader(fis, "UTF-8");

            BufferedReader bufferedReader = new BufferedReader(isr);
            StringBuilder sb = new StringBuilder();
            String line;
            int index = -1;
            while ((line = bufferedReader.readLine()) != null)
            {
                line = line.trim();
//                Utility.appendLog("MatchInfo", "Index: " + index);
//                Utility.appendLog("MatchInfo", "CurrentLine: " + line);
                switch(++index)
                {
                    case 0: //mission
                        switch (line) {
                            case "One_Collection":
                                myMission = Mission.One_Collection;
                                break;
                            case "No_Collection":
                                myMission = Mission.No_Collection;

                            default:
                                myMission = Mission.One_Collection;
                                break;

                        }
                        break;
                    case 1:
                        switch (line){
                            case "TEAM_BLUE":
                                myAllianceColor = AllianceColor.TEAM_BLUE;
                                break;
                            case "TEAM_RED":
                                myAllianceColor = AllianceColor.TEAM_RED;
                                break;
                        }
                        break;
                    case 2:
                        switch (line){
                            case "LEFT":
                                myStartPosition = StartPosition.CLOSE;
                                break;
                            case "RIGHT":
                                myStartPosition = StartPosition.FAR;
                                break;
                        }
                        break;
                    case 3:
                        myDelayTime = Integer.parseInt(line);
                        break;
                    case 4:
                        enableLogging = Boolean.parseBoolean(line);
                        break;
                    case 5:
                        PID_pVal = Double.parseDouble(line);
                        break;
                    case 6:

                        try {
                            Date modifiedDate = new Date(new File(filename).lastModified());
                            //make sure the file is written within 5 minutes of current time to be considered valid
                            if (new Date().getTime() - modifiedDate.getTime() < 300000) {
                                myReachEncoderValue = Integer.parseInt(line);
                            } else {
                                myReachEncoderValue = 0;
                            }
                        }catch(Exception e){
                            myReachEncoderValue = 0;
                        }

                        break;
                }
            }
        } catch (FileNotFoundException e) {

        } catch (UnsupportedEncodingException e) {

        } catch (IOException e) {

        }
    }
    public void saveSettings() {
        String settingsXml = collectInfo().toString();//collectXml();
        FileOutputStream outputStream;

        try {

            createDirectory(); //create the application directory

            File f = new File(autoConfigDirectory, autoConfigFile);

            outputStream = new FileOutputStream(f);
            outputStream.write(settingsXml.getBytes());
            outputStream.close();

//            telemetry.addData("Config Saved", "Saved Completed!");
        } catch (Exception e) {
//            telemetry.addData("Config Save Failed", e.getMessage());
            e.printStackTrace();
        }
    }
    private StringBuffer collectInfo() {
        StringBuffer sb = new StringBuffer();
        String ln = "\r\n";

        sb.append(myMission.toString());
        sb.append(ln);
        sb.append(myAllianceColor.toString());
        sb.append(ln);
        sb.append(myStartPosition.toString());
        sb.append(ln);
        sb.append(myDelayTime);
        sb.append(ln);
        sb.append(enableLogging);
        sb.append(ln);
        sb.append(myReachEncoderValue);

        return sb;
    }
    private void createDirectory() {
        File dir = new File(autoConfigDirectory);

        try {
            if (!dir.isDirectory()) {
                dir.mkdirs();
            }
        } catch (Exception e) {
//            telemetry.addData("Create AutoConfig Directory Failed", e.getMessage());
        }
    }
}
