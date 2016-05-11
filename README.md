# Take a Number
Senior Capstone Project

Take a Number is a suite of applications intended to optimize the rate at which instructors and lab aides provide help to students in computer lab courses at the University of Portland. This project additionally allows instructors to log student's checkpoint progress and sync it to the lab aides.

This is the Android portion of Take a Number. It gives instructors and aides a portable interface to be able to reference throughout a lab and determine which student to help next and mark down a student's progress. There is additional functionaly to export grades to a format readable by Microsoft Excel.

Take a Number Android is made with Android-Java and requires to be run on the same network as the Take a Number Server

Dependencies:

[Jasypt](http://www.jaysypt.org) - To encrypt student checkpoint to data to conform to FERPA regulations

[WebSocket](https://github.com/TooTallNate/Java-WebSocket) - To ensure proper communication between Java, Android-Java, and Javascript


The Server application can be found **[here](https://github.com/agne16/Take-a-Number-Server)**

The web page interface can be found **[here](https://github.com/agne16/Take-a-Number-Web)**
