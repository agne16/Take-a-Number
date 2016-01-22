package edu.up.projects.engineering;

/**
 * Created by TJ on 1/21/2016.
 */
public class ServerMain
{
    public static void main(String[] args)
    {
        Server server = new Server();
        //LabState labState = new LabState();


        server.runServer();

        System.out.println("Main");
    }
}
