package edu.up.projects.engineering.takeanumberandroid;


public class WebSocketHandler
{
    private static WebSocket webSocket;

    public static synchronized WebSocket getWebSocket()
    {
        return webSocket;
    }

    public static synchronized void setWebSocket(WebSocket webSocket)
    {
        WebSocketHandler.webSocket = webSocket;
    }
}
