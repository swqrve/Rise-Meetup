package me.swerve.meetup.file;

import lombok.SneakyThrows;
import org.bson.Document;
import java.io.File;
import java.io.FileWriter;
import java.util.Scanner;

public class FileUtility {

    @SneakyThrows
    public static Document readFromFile(File file) {
        StringBuilder s = new StringBuilder();

        Scanner reader = new Scanner(file);
        while(reader.hasNextLine()) s.append(reader.nextLine());

        reader.close();
        return Document.parse(s.toString());
    }

    @SneakyThrows
    public static void write(File file, Document document) {
        FileWriter fileWriter = new FileWriter(file);
        fileWriter.write(document.toJson());
        fileWriter.close();
    }
}
