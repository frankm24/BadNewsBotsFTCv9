package com.badnewsbots.util;

import android.os.Environment;

import java.io.FileWriter;
import java.io.IOException;

public final class DataSaver {
    public static void saveDataStringToTxt(String data, String fileName) {
        fileName = Environment.getExternalStorageDirectory() + "/Pictures/" + fileName + ".txt";
        try {
            FileWriter fileWriter = new FileWriter(fileName);
            fileWriter.write(data);
            fileWriter.close();
            System.out.println("Map saved.");
        } catch (IOException e) {
            System.out.println("Error: The map save file could not be written.");
            e.printStackTrace();
        }
    }
}
