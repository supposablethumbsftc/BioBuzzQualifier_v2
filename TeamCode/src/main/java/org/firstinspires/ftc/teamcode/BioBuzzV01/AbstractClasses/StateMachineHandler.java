package org.firstinspires.ftc.teamcode.BioBuzzV01.AbstractClasses;

import java.util.ArrayList;

public class StateMachineHandler {
    public static ArrayList<Integer> stateOrder = new ArrayList<>();
    public static int getCurrentState(){
        if (stateOrder.size() != 0){
            return stateOrder.get(0) - 1;
        }
        return 0;
    }

}

