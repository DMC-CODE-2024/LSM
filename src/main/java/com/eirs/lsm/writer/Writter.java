package com.eirs.lsm.writer;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

public class Writter {

    public boolean createFile(String filepath) {
        try {
            File file = new File(filepath);
            file.getParentFile().mkdirs();
            file.createNewFile();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean moveFile(String sourcePath, String destinationPath) {
        try {
            Files.move(Paths.get(sourcePath), Paths.get(destinationPath), REPLACE_EXISTING);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}
