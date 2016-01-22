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
    public boolean parseXML(String rootPath, String filename)
    {
        try
        {
            //create a file object and make sure it exists
            File file = new File(rootPath + '/' + filename);
            if(!file.exists())
            {
                return false
            }

            //initialize parser and get the first tag of the xml file
            SAXBuilder saxBuilder = new SAXBuilder();
            Document document = saxBuilder.build(file);
            Element labState = document.getRootElement();


            System.out.println("File Type: " + labState.getName());
            System.out.println("Lab ID: " + labState.getAttribute("sessionId").getValue());

            //Get the array of students
            List<Element> students = labState.getChildren();

            System.out.println("----------------------------");
            //For each student,
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

        return true;
    }

    public void writeFile()
    {
        try
        {
            String path = System.getProperty("user.dir");
            File file = new File(path + "/testFile.txt");
            String content = "hello nick";
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
