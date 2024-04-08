package org.example;

import java.io.IOException;
import java.util.HashMap;

public class Test {
    public native Object[] getArray(String neededLog);

    static {
        System.load("C:\\Users\\harine-pt7602\\source\\repos\\test\\x64\\Debug\\test.dll");
    }

    public static void main(String[] args) throws IOException,NullPointerException {
        String neededLog="Application";
        Test sample = new Test();
        Object[] result = sample.getArray(neededLog);
        for (Object obj : result) {
            if (obj instanceof HashMap) {
                HashMap<String, String> event = (HashMap<String, String>) obj;
                for (String key : event.keySet()) {
                    System.out.println(key + ": " + event.get(key));
                }
            } else {
                System.out.println("Error");
            }
        }



    }
}