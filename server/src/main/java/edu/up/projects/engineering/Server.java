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

        helper.parseXML(rootPath, filename);
    }

    public LabState initLabState(LabSession labSession)
    {
        int numCheckpoints = labSession.getNumCheckpoints();
        String[] classRoster = labSession.getClassRoster();
        boolean[][] checkpoints = labSession.getCheckpoints();
        ArrayList<String> labQueue = labSession.getLabQueue();

        LabState labState = new LabState(classRoster, checkpoints, labQueue);
        return labState;
    }

}