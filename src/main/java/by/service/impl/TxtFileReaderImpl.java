package by.service.impl;

import by.service.TxtFileReader;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;

@Service
public class TxtFileReaderImpl implements TxtFileReader {
    @Override
    public String readFile(File file) {
        StringBuilder resultStringBuilder = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new java.io.FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                resultStringBuilder.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return resultStringBuilder.toString();
    }
}
