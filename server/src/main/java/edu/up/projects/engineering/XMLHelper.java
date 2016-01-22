package edu.up.projects.engineering;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

public class XMLHelper
{
    public void parseXML(String rootPath, String filename)
    {
        try
        {
            File file = new File(rootPath + '/' + filename);
            SAXBuilder saxBuilder = new SAXBuilder();

            Document document = saxBuilder.build(file);
            Element labState = document.getRootElement();

            System.out.println("File Type: " + labState.getName());
            System.out.println("Lab ID: " + labState.getAttribute("sessionId").getValue());

            //Get the array of students
            List<Element> students = labState.getChildren();

            System.out.println("----------------------------");
            for(Element currentStudent : students)
            {
                System.out.println("\nStudent: " + currentStudent.getAttribute("userId").getValue());
                List<Element> studentCheckpoints = currentStudent.getChildren();
                for(Element checkpoint : studentCheckpoints)
                {
                    System.out.println(checkpoint.getName() + ": " + checkpoint.getValue().toLowerCase());
                }
            }
        }
        catch (JDOMException e)
        {
            e.printStackTrace();
        }

        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public void writeFile()
    {
        try
        {
            String path = System.getProperty("user.dir");
            File file = new File(path + "/testFile.txt");
            String content = "hello world";
            if(!file.exists())
            {
                file.createNewFile();
            }
            FileWriter write = new FileWriter(file.getAbsoluteFile());
            PrintWriter print_line = new PrintWriter(write);
            print_line.print(content);
            print_line.close();
            write.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }


    public void blah()
    {
        System.out.println("success");
    }
}
