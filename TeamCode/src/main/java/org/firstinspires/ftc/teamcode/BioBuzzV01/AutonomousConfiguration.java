package org.firstinspires.ftc.teamcode.BioBuzzV01;

import android.os.Environment;
import android.util.Log;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.teamcode.BioBuzzV01.AbstractClasses.MatchInformation;
import org.firstinspires.ftc.teamcode.BioBuzzV01.AbstractClasses.MatchInformation.AllianceColor;
import org.firstinspires.ftc.teamcode.BioBuzzV01.AbstractClasses.MatchInformation.StartPosition;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
@Disabled
@TeleOp(name="AutoConfig")
//@Disabled
public class AutonomousConfiguration extends LinearOpMode {

    private static final String autoConfigDirectory = Environment.getExternalStorageDirectory() + File.separator + "Thumbs4106";
    private static final String autoConfigFile = "Settings.txt";

    private MatchInformation matchInfo;
    private Map<String, String[]> allMenus;

    private static final String MAIN_MENU_TITLE = "MAIN MENU";
    private static final String ALLIANCE_MENU_TITLE = "ALLIANCE";
    private static final String START_POS_MENU_TITLE = "START POSITION";
    private static final String MISSIONS_MENU_TITLE = "MISSIONS";
    private static final String DELAY_MENU_TITLE = "DELAY (S)";
    private static final String ENABLE_LOGS_MENU_TITLE = "ENABLE LOGS";
    private static final String PID_SET_PVAL_MENU_TITLE = "PID_PVal";
    private static final String CONFIRM_EXIT_TITLE = "CONFIRM EXIT";

    private String MenuMessage = "";

    @Override
    public void runOpMode() throws InterruptedException {

        boolean exitApp = false;

        matchInfo = new MatchInformation();

        allMenus = initializeMenus();

        String currentMenuTitle = "MAIN MENU";

        waitForStart();

        int selectedIndex;
        String selectedOption;

        //show a menu and decide what to do based on what the menu the user selected
        while (opModeIsActive() && !exitApp) {

            //show the menu and return a selected menu option index
            selectedIndex = getSelectedMenu(currentMenuTitle);

            //get the menu item selected based on the selected index
            selectedOption = allMenus.get(currentMenuTitle)[selectedIndex];
            telemetry.addData("Menu", currentMenuTitle);
            telemetry.addData("Index", selectedIndex);
            telemetry.addData("Option", selectedOption);

            Log.d("MainLoop", "MenuTitle: " + currentMenuTitle);
            Log.d("MainLoop", "SelectedIndex: " + selectedIndex);
            Log.d("MainLoop", "SelectedOption: " + selectedOption);

            //decide what to do based on the menu shown and the selected index
            switch (currentMenuTitle) {
                case MAIN_MENU_TITLE:
                    Log.d("MainLoop", "MainMenuSwitch");
                    switch (selectedIndex) {
                        case 0:
                            Log.d("MainLoop", "MainMenuSwitch_Alliance");
                            currentMenuTitle = ALLIANCE_MENU_TITLE;
                            break;
                        case 1:
                            Log.d("MainLoop", "MainMenuSwitch_StartPos");
                            currentMenuTitle = START_POS_MENU_TITLE;
                            break;
                        case 2:
                            Log.d("MainLoop", "MainMenuSwitch_MissionCrater");
                            currentMenuTitle = MISSIONS_MENU_TITLE;
                            break;
                        case 3:
                            Log.d("MainLoop", "MainMenuSwitch_Delay");
                            currentMenuTitle = DELAY_MENU_TITLE;
                            break;
                        case 4:
                            Log.d("MainLoop", "MainMenuSwitch_Logs");
                            currentMenuTitle = ENABLE_LOGS_MENU_TITLE;
                            break;
                        case 5:
                            Log.d("MainLoop", "MainMenuSwitch_PID_p");
                            currentMenuTitle = PID_SET_PVAL_MENU_TITLE;
                            break;

                        case 6:
                            Log.d("MainLoop", "MainMenuSwitch_Exit");
                            if (matchInfo.myStartPosition.toString().toLowerCase().contains("left") && !matchInfo.myMission.toString().toLowerCase().startsWith("left")) {
//                                MenuMessage = "Start left is selected but a start crater mission is selected.  Save?";
//                                currentMenuTitle = CONFIRM_EXIT_TITLE;
                            } else if (matchInfo.myStartPosition.toString().toLowerCase().contains("right") && !matchInfo.myMission.toString().toLowerCase().startsWith("right")) {
//                                MenuMessage = "Start crater is selected but a start right mission is selected.  Save?";
//                                currentMenuTitle = CONFIRM_EXIT_TITLE;
                            } else if (matchInfo.myDelayTime > 10) {
                                MenuMessage = "Delay time is greater than 10s. Save?";
                                currentMenuTitle = CONFIRM_EXIT_TITLE;
                            } else {
                                //no error or warning before saving so exit
                                exitApp = true;
                            }

                    }
                    break;
                case ALLIANCE_MENU_TITLE:
                    Log.d("MainLoop", "SetAlliance: " + selectedOption);
                    matchInfo.myAllianceColor = selectedOption.equals("TEAM_RED") ? AllianceColor.TEAM_RED : AllianceColor.TEAM_BLUE;
                    currentMenuTitle = updateMainMenu();
                    break;
                case START_POS_MENU_TITLE:
                    Log.d("MainLoop", "SetStartPos: " + selectedOption);
                    matchInfo.myStartPosition = selectedOption.equals("LEFT") ? StartPosition.CLOSE : StartPosition.FAR;
                    currentMenuTitle = updateMainMenu();
                    break;
                case MISSIONS_MENU_TITLE:
                    Log.d("MainLoop", "SetMission: " + selectedOption);
                    matchInfo.myMission = MatchInformation.StringToMission(selectedOption);
                    currentMenuTitle = updateMainMenu();
                    break;
                case DELAY_MENU_TITLE:
                    Log.d("MainLoop", "SetDelay: " + selectedOption);
                    matchInfo.myDelayTime = Integer.parseInt(selectedOption);
                    currentMenuTitle = updateMainMenu();
                    break;
                case ENABLE_LOGS_MENU_TITLE:
                    Log.d("MainLoop", "SetLog: " + selectedOption);
                    matchInfo.enableLogging = selectedOption.equals("ENABLE") ? true : false;
                    currentMenuTitle = updateMainMenu();
                    break;
                case PID_SET_PVAL_MENU_TITLE:
                    Log.d("MainLoop", "SetPVal: " + selectedOption);
                    matchInfo.PID_pVal = Double.parseDouble(selectedOption);
                    currentMenuTitle = updateMainMenu();
                case CONFIRM_EXIT_TITLE:
                    Log.d("MainLoop", "ConfirmExit" + selectedOption);
                    if (selectedOption.equals("YES")) {
                        exitApp = true;
                    }
                    currentMenuTitle = updateMainMenu();
                    MenuMessage = ""; //clear message since the user confirm decision already
                    break;
            }

            telemetry.update();
            //sleep(250);
        }

        //user chose to exit so save the data to file
        telemetry.addData("Saving Config", "...");
        saveSettings();
        telemetry.addData("Saving Config", "COMPLETED!");
        telemetry.update();

        int counter = 0;

        //keep showing the saved data until the user quits the opmode
        while (opModeIsActive()) {
            ++counter;

            if (counter == 1) {
                telemetry.addData("", ".......");
            } else if (counter == 2) {
                telemetry.addData("", "       .......");
            } else {
                telemetry.addData("", "              .......");
                counter = 0;
            }

            readSettingsFile();
            telemetry.addData("","Config Saved!!!");
            telemetry.update();
            sleep(750);
        }
    }

    //update the main menu with the current options
    private String updateMainMenu() {
        String currentMenuTitle = MAIN_MENU_TITLE;
        Log.d("MainLoop", "OpenMainMenu");

        String[] mainMenus = getMainMenus();

        //update Main Menus with selected option
        allMenus.remove(MAIN_MENU_TITLE);
        allMenus.put(MAIN_MENU_TITLE, mainMenus);
        return currentMenuTitle;
    }

    //populate the menu menu menu with current options
    private String[] getMainMenus() {
        String[] mainMenus = new String[]{
                matchInfo.myAllianceColor + " - CHANGE ALLIANCE COLOR",
                matchInfo.myStartPosition + " - CHANGE START POSITION",
                matchInfo.myMission + " - CHANGE MISSION",
                matchInfo.myDelayTime + " - CHANGE DELAY",
                Boolean.toString(matchInfo.enableLogging) + " - CHANGE LOGGING",
                matchInfo.PID_pVal + " - PID_PVal",
                "EXIT"
        };
        return mainMenus;
    }

    //Populate all menus with options
    private Map<String, String[]> initializeMenus() {
        Map<String, String[]> allMenus = new HashMap<>();

        String[] mainMenus = getMainMenus();
        String[] allianceMenus = MatchInformation.getAllAlliance();
        String[] startPositionMenus = MatchInformation.getAllStartPositions();
        String[] missionsMenus = MatchInformation.getAllMissions();
        String[] delayMenus = new String[]{
                "0",
                "3",
                "5",
                "7",
                "10",
                "12",
                "15",
                "17",
                "20" //do we need more options?
        };

        String[] loggingMenu = new String[]{
                "ENABLE",
                "DISABLE"
        };

        String[] confirmExitMenu = new String[]{
                "YES",
                "NO"
        };

        String[] pVal = new String[]{
                "0.0",
                "0.1",
                "0.15",
                "0.20",
                "0.25",
                "0.30",
                "0.35",
                "0.40",
                "0.45",
                "0.50",
                "0.55"
        };

        allMenus.put(MAIN_MENU_TITLE, mainMenus);
        allMenus.put(ALLIANCE_MENU_TITLE, allianceMenus);
        allMenus.put(START_POS_MENU_TITLE, startPositionMenus);
        allMenus.put(MISSIONS_MENU_TITLE, missionsMenus);
        allMenus.put(DELAY_MENU_TITLE, delayMenus);
        allMenus.put(ENABLE_LOGS_MENU_TITLE, loggingMenu);
        allMenus.put(PID_SET_PVAL_MENU_TITLE, pVal);
        allMenus.put(CONFIRM_EXIT_TITLE, confirmExitMenu);
        return allMenus;
    }

    //show menu items based on menuTitle and return the selected index
    private int getSelectedMenu(String menuTitle) {

        int selectedMenu = 0;

        //we should only redraw the menu when user presses up/down/select only
        boolean updateScreen = true; //update initially

        //sets to true when a user has selected a menu
        boolean selectedOption = false;

        //store menu items based on menuTitle
        String[] menuOptions;

        menuOptions = allMenus.get(menuTitle);

        while (opModeIsActive() && !selectedOption) {

            if (gamepad1.dpad_up) {
                selectedMenu = Range.clip(--selectedMenu, 0, menuOptions.length - 1);
                updateScreen = true;
                Log.d("ShowLoop", "UpPress: " + selectedMenu);
            } else if (gamepad1.dpad_down) {
                selectedMenu = Range.clip(++selectedMenu, 0, menuOptions.length - 1);
                updateScreen = true;
                Log.d("ShowLoop", "DownPress: " + selectedMenu);
            }

            if (gamepad1.a) {
                selectedOption = true;
                Log.d("ShowLoop", "Selected: " + selectedMenu);
            }

            if (updateScreen) {
                int index = -1;
                telemetry.addData("", "==== " + menuTitle + " ====");

                //show the warning message if application; mostly shown before exiting opmode
                if (!MenuMessage.equals("")) {
                    telemetry.addData("", MenuMessage);
                }
                telemetry.addData("", selectedMenu);

                //show the menu items
                //if the menu item is not selected draw it normally otherwise add ">>>" to show it is selected
                for (String mnu : menuOptions) {
                    telemetry.addData((++index == selectedMenu) ? ">>>" : "", mnu);
                }
                telemetry.update();
                updateScreen = false;
            }
            sleep(150);
        }
        return selectedMenu;
    }

    //get the selected option into a string that can be saved to the phone file system
    private StringBuffer collectInfo() {
        StringBuffer sb = new StringBuffer();
        String ln = "\r\n";

        sb.append(matchInfo.myMission.toString());
        sb.append(ln);
        sb.append(matchInfo.myAllianceColor.toString());
        sb.append(ln);
        sb.append(matchInfo.myStartPosition.toString());
        sb.append(ln);
        sb.append(matchInfo.myDelayTime);
        sb.append(ln);
        sb.append(matchInfo.enableLogging);
        sb.append(ln);
        sb.append(matchInfo.PID_pVal);

        return sb;
    }

    //create a folder on the phone to store the setting file
    private void createDirectory() {
        File dir = new File(autoConfigDirectory);

        try {
            if (!dir.isDirectory()) {
                dir.mkdirs();
            }
        } catch (Exception e) {
            telemetry.addData("Create AutoConfig Directory Failed", e.getMessage());
        }
    }

    //save the opmode settings on the phone
    public void saveSettings() {
        String settingsXml = collectInfo().toString();//collectXml();
        FileOutputStream outputStream;

        try {

            createDirectory(); //create the application directory

            File f = new File(autoConfigDirectory, autoConfigFile);

            outputStream = new FileOutputStream(f);
            outputStream.write(settingsXml.getBytes());
            outputStream.close();

            telemetry.addData("Config Saved", "Saved Completed!");
        } catch (Exception e) {
            telemetry.addData("Config Save Failed", e.getMessage());
            e.printStackTrace();
        }
    }

    //read the settings from the phone file system
    public void readSettingsFile() {
        String filename = Environment.getExternalStorageDirectory() + "/Thumbs4106/Settings.txt";
        String tag = "Reading Config";
        StringBuffer sb = new StringBuffer();

        try {

            if (new File(filename).exists()) {

                FileInputStream fis = new FileInputStream(filename);

                InputStreamReader isr = new InputStreamReader(fis, "UTF-8");
                BufferedReader bufferedReader = new BufferedReader(isr);

                String line;
                int index = -1;
                while ((line = bufferedReader.readLine()) != null) {
                    line = line.trim();
                    switch (++index) {
                        case 0: //mission
                            telemetry.addData(tag, "Mission: " + line);
                            break;
                        case 1:
                            telemetry.addData(tag, "Alliance: " + line);
                            break;
                        case 2:
                            telemetry.addData(tag, "StartPos: " + line);
                            break;
                        case 3:
                            telemetry.addData(tag, "Delay(s): " + line);
                            break;
                        case 4:
                            telemetry.addData(tag, "Logging? " + line);
                            break;
                        case 5:
                            telemetry.addData(tag, "PID_P:" + line);
                            break;
                    }
                }
            }
        } catch (FileNotFoundException e) {
            telemetry.addData(tag, "FileNotFound: " + e.getMessage());
        } catch (UnsupportedEncodingException e) {
            telemetry.addData(tag, "UnsupportedEncoding: " + e.getMessage());
        } catch (IOException e) {
            telemetry.addData(tag, "IOException: " + e.getMessage());
        }
    }
}
