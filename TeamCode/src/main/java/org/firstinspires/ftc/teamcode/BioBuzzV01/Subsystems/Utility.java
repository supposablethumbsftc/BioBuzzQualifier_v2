package org.firstinspires.ftc.teamcode.BioBuzzV01.Subsystems;

import android.os.Environment;
import android.util.Log;

import org.firstinspires.ftc.robotcore.external.Telemetry;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;

/**
 * Provides logging for debugging
 */
public final class Utility
{

    private static Telemetry telemetry;
    /***
     * Return a nice formated decimal value
     * @param val the value to format
     * @return the formatted value in a string
     */
    public static String formatDecimal(double val){
        DecimalFormat df = new DecimalFormat("#0.0000");
        return df.format(val);
    }

    public static void setTelemetry(Telemetry tel){
        telemetry = tel;
    }

    public static void printInfo(String category, String message) {
        Utility.appendLog(category, message);

        if(telemetry != null){
            telemetry.addData(category, message);
        }
    }

    public static void updateTelemetry(){
        if(telemetry!=null)
            telemetry.update();
    }

    /***
     * Add to log for monitoring
     * @param loggingTag descriptor of information
     * @param message message to log
     */
    public static void appendLog(String loggingTag, String message){
        Log.d(loggingTag, message);
    }

    /***
     * Add to log for monitoring
     * @param message message to log
     */
    public static void appendLog(String message){
        Log.d(loggingTag, message);
    }

    /**
     * Descriptor for log information. Set this before adding common log
     */
    private static String loggingTag = "";
    /**
     * A desciption for the current session.  This is used to easily filter information for debugging
     */
    public static String matchDescription = "";

    public static String actionTag = "";

    /**
     * Sets the logging tag.  The logging tag is used to easily filter information for debugging
     * @param tag Tag used for log entries
     */
    public static void setLoggingTag(String tag) {
        loggingTag = matchDescription+"_" + (!actionTag.equals("")? actionTag + "_": actionTag) + tag;
    }

    public static void setActionTag(String tag){
        actionTag = tag;
    }

    public static void clearActionTag(){
        actionTag = "";
    }

    /**
     * The directory on the robot phone files can be written
     */
    public static String DataDirectory = Environment.getExternalStorageDirectory() + File.separator + "ThumbsData";

    /**
     * The file we want to write to the robot phone
     */
    public static String DataFile = "";

    /**
     * An open file output stream so we can constant add log data without having to open/close files for logging information when debugging our robot
     */
    private static FileOutputStream outputStream;

    //Call this first to log to file. Call writeToFile to append to file, close file after all writes by calling endLogFile

    /**
     * This start writing a log file on a robot phone.  This must be call first, then "WriteToFile" can be call each time we want to write data to the file, then after we finished writing to the file we call "EndLogFile" to securely close the file
     * @param fileName the name of the log file we want to write
     * @param initialLine the initial line in the file.  If we want to write data in csv format, this can be used to create a header for the csv data.  For example "rpm, battery_voltage, motor_voltage"
     */
    public static void startLogFile(String fileName, String initialLine){//, boolean overWrite) {
        try {
            createDirectory(DataDirectory);
            DataFile = DataDirectory + File.separator + fileName;
            File f = new File(DataFile);

            /*
            if(overWrite && f.exists()){
                f.delete();
                f.createNewFile();
            }
            */

            String dataLine = initialLine+"\r\n";
            outputStream = new FileOutputStream(f);
            outputStream.write(dataLine.getBytes());
            outputStream.flush();
        } catch (IOException var2) {

        }
    }


    /**
     * Write individual log entry to the file currently on the file.  "StartLogFile" must be called first to create a file we can write to
     * @param data the data we want to add to the log
     */
    public static void writeToFile(String data){
        try {
            data+="\r\n";
            outputStream.write(data.getBytes());
            outputStream.flush();
        } catch (IOException var2) {
            ;
        }
    }

    /**
     * Close the file nicely after we are done logging so all data are secure
     */
    public static void endLogToFile(){
        try {
            outputStream.close();
        } catch (IOException var3) {

        }
    }


    /**
     * Create a folder on the phone so we can store our data and know where it is located in the phone file system
     */
    public static void createDirectory(String directoryPath) {
        File dir = new File(directoryPath);

        try {
            if(!dir.isDirectory()) {
                dir.mkdirs();
            }
        } catch (Exception var3) {

        }

    }

    public static final String opModeSharedDataDirectory = Environment.getExternalStorageDirectory() + File.separator + "Thumbs4106";
    public static final String rotatorPositionFile = "Position.txt";

    public static void writeRotatorPositionToFile(String position){
        FileOutputStream outputStream;

        try {

            createDirectory(opModeSharedDataDirectory); //create the application directory

            File f =  new File(opModeSharedDataDirectory, rotatorPositionFile);

            outputStream = new FileOutputStream(f);
            outputStream.write(position.getBytes());
            outputStream.close();

            telemetry.addData("Rotator Encoder Save","Rotator Encoder Saved!");
        } catch (Exception e) {
            telemetry.addData("Rotator Encoder Save","Failed: " + e.getMessage());
            e.printStackTrace();
        }
        telemetry.update();
    }

    public static int getSavedRotatorPosition(){

        int rotatorPosition = 0;

        try {

            File f =  new File(opModeSharedDataDirectory, rotatorPositionFile);


            if(f.exists()) {

                FileInputStream fis = new FileInputStream(f.getAbsolutePath());

                InputStreamReader isr = new InputStreamReader(fis, "UTF-8");
                BufferedReader bufferedReader = new BufferedReader(isr);

                String line;
                String data = "";
                while ((line = bufferedReader.readLine()) != null) {

                    Log.d("Read Rotator Position", "Line: " + line);
                    line = line.trim();
                    if(!line.equals("")){
                        data = line;
                        Log.d("Read Rotator Pos", "Set Data to: " + data);
                    }
                }
                try{
                    telemetry.addData("Rotator", "Read Rotator Position: " + data);
                    rotatorPosition =  Integer.parseInt(data.trim());
                    telemetry.addData("Rotator", "Read Rotator Position Converted: " + rotatorPosition);
                }
                catch(NullPointerException e){
                    telemetry.addData("NullPointerError","Conversion Error with data. Error: " + e.getMessage());
                }
                catch (Exception e){
                    telemetry.addData("Error","Conversion Error with data. Error: " + e.getMessage());
                }

            }
        } catch (FileNotFoundException e) {
            telemetry.addData("Error","AutoHeading.txt not found. Error: "+ e.getMessage());
        } catch (UnsupportedEncodingException e) {
            telemetry.addData("Error","Encoding not supported. Error: "+ e.getMessage());
        } catch (IOException e) {
            telemetry.addData("Error","IOException. Error: "+ e.getMessage());
        }

        return rotatorPosition;
    }

}
