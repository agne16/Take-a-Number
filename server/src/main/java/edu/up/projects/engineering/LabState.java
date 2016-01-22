package edu.up.projects.engineering;

import java.util.ArrayList;
import java.util.HashMap;

public class LabState
{
    private String[] classRoster;
    private boolean[][] checkpoints;
    private ArrayList<String> labQueue;
    private HashMap<String, Integer> seatPositions;

    public LabState(String[] initClassRoster, boolean[][] initCheckpoints,
                    ArrayList<String> initLabQueue)
    {
        classRoster = initClassRoster;
        checkpoints = initCheckpoints;
        labQueue = initLabQueue;
    }
}
