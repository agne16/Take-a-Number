package edu.up.projects.engineering;

import java.util.ArrayList;

public class Server
{
    private String rosterCsvFilePath;
    private String savedLabSessionFilePath;
    private LabState currentLabState;

    public void runServer()
    {
        XMLHelper helper = new XMLHelper();

        String rootPath = System.getProperty("user.dir");
        String filename = "CS273-A-ComputerScienceLaboratory-17378.xml";

        LabState labState = helper.parseXML(rootPath, filename);
        System.out.println("Lab Session ID: " + labState.getSessionId());
        System.out.println("Student: "+ labState.getClassRoster()[0]);
        System.out.println("Checkpoint 1: " + labState.getCheckpoints()[0][1]);
    }

    public LabState initLabState(LabSession labSession)
    {
        String sessionId = labSession.getSessionId();
        int numCheckpoints = labSession.getNumCheckpoints();
        String[] classRoster = labSession.getClassRoster();
        boolean[][] checkpoints = labSession.getCheckpoints();
        ArrayList<String> labQueue = labSession.getLabQueue();

        LabState labState = new LabState(sessionId, classRoster, checkpoints, labQueue);
        return labState;
    }

}