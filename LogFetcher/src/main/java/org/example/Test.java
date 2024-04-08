package org.example;

import java.io.IOException;
import java.util.HashMap;

public class Test {
    public native Object[] getArray(String neededLog);

    static {
        System.load("C:\\Users\\harine-pt7602\\source\\repos\\test\\x64\\Debug\\test.dll");
    }

    public static void main(String[] args) throws IOException,NullPointerException {



    }
}