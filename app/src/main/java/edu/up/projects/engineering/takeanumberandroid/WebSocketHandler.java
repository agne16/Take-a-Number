package edu.up.projects.engineering.takeanumberandroid;

import android.util.Log;

import org.jasypt.util.text.BasicTextEncryptor;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;

/**
 * A class to handle all network messages
 */
public class WebSocketHandler extends WebSocketClient
{
    String lastMessage = "";
    private static final String TAG = "WebSocketHandler";
    boolean needUpdate = false;

    public WebSocketHandler(URI serverURI)
    {
        super(serverURI);
    }

    @Override
    public void onOpen(ServerHandshake handshakedata)
    {
        Log.i(TAG, "new connection opened");
    }

    @Override
    public void onClose(int code, String reason, boolean remote)
    {
        Log.i(TAG, "closed with exit code " + code + " additional info: " + reason);
    }

    @Override
    public void onMessage(String message)
    {
        Log.i(TAG, "received message: " + message);
        String header = message.split("#")[0].toLowerCase();
        if(header.equals("encrypted"))
        {
            message = decrypt(message.substring(message.indexOf("#") + 1));
        }
        interpretMessage(message);
    }

    @Override
    public void onError(Exception ex)
    {
        Log.e(TAG, "an error occurred:" + ex);
    }

    /**
     * "Pops the last message received from te server
     * @return the last message received
     */
    public String getLastMessage()
    {
        String copy = lastMessage;
        lastMessage = "";
        return copy;
    }

    /**
     * When called, program ensures that a connection is ready before a message is send
     * Also terminates if connection is not ready after a certain amount of time
     */
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

    /**
     * Interprets a message received from the server
     * @param s the messagre received fromt he server
     */
    public void interpretMessage(String s)
    {
        System.out.println(s);
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
                this.needUpdate = true;
                break;
            case "checkpointretrieve":
                this.lastMessage = s;
                break;
            case "positions":
                this.lastMessage = s;
                break;
            default:
                break;
        }
    }

    /**
     * Same functionality as WebSocketHandler.send(), but used for sending a Jasypt-encrypted string
     * @param s a (plaintext) string to send over the network
     */
    public void sendSecure(String s)
    {
        this.send(encrypt(s));
    }

    /**
     * Encrypts a string using Jasypt
     * @param s the string to encrypt
     * @return a payload string to send over the network (encrypted#<jasypt string>)
     */
    public String encrypt(String s)
    {
        BasicTextEncryptor textEncryptor = new BasicTextEncryptor();
        textEncryptor.setPassword("ForTheLulz");
        String myEncryptedText = textEncryptor.encrypt(s);
        String payload = "encrypted#" + myEncryptedText;
        return payload;
    }

    /**
     * Decrypts a string using Jasypt
     * @param s the string to decrypt (assuming encrypted# is already removed)
     * @return the decrypted string in plaintext
     */
    public String decrypt(String s)
    {
        BasicTextEncryptor textEncryptor = new BasicTextEncryptor();
        textEncryptor.setPassword("ForTheLulz");
        String plainText = textEncryptor.decrypt(s);
        return plainText;

    }

}