package edu.up.projects.engineering.takeanumberandroid;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;

/**
 * Created by TJ on 3/12/2016.
 */
public class WebSocketHandler extends WebSocketClient
{
    String lastMessage = "";

    public WebSocketHandler(URI serverURI)
    {
        super(serverURI);
    }
    @Override
    public void onOpen(ServerHandshake handshakedata)
    {
        System.out.println("new connection opened");
    }

    @Override
    public void onClose(int code, String reason, boolean remote)
    {
        System.out.println("closed with exit code " + code + " additional info: " + reason);
    }

    @Override
    public void onMessage(String message)
    {
        System.out.println("received message: " + message);
        interpretMessage(message);
    }

    @Override
    public void onError(Exception ex)
    {
        System.err.println("an error occurred:" + ex);
    }

    public String getLastMessage()
    {
        String copy = lastMessage;
        lastMessage = "";
        return copy;
    }

    public void waitForReady()
    {
        int remainingTries = 15;
        while (true)
        {
            if (remainingTries == 0)
            {
                System.out.println("Took too long to connect to server. Exiting");
                System.exit(-408);
            }

            String state = this.getReadyState().toString();
            if (state.equals("OPEN"))
            {
                break;
            }
            try
            {
                Thread.sleep(100);
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }
            remainingTries--;
        }
    }

    public void interpretMessage(String s)
    {
        String[] parms = s.split("#");
        switch (parms[0].toLowerCase().trim())
        {
            case "":
                this.close();
                break;
            case "sessionid":
                this.lastMessage = s;
                break;
            case "checkpointsync":
                this.lastMessage = s;
                break;
            case "checkpointretrieve":
                this.lastMessage = s;
                break;
            default:
                break;
        }
    }

}