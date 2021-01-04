package com.example.kotlinIDE.service;

import com.example.kotlinIDE.OutputEvent;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Log4j2
@Service
public class KotlinIdeService {

    private final ApplicationEventPublisher publisher;

    KotlinIdeService(ApplicationEventPublisher publisher){
        this.publisher = publisher;
    }

    public void execute(ProcessBuilder processBuilder, String sourceCodePath) {
        processBuilder.command("kotlinc", "-script", sourceCodePath);
    }

    public void runScript(String script) throws IOException {
        String sourceCodePath = createFileInTempDirectory(script);
        ProcessBuilder processBuilder = new ProcessBuilder();
        execute(processBuilder, sourceCodePath);
        Process process = processBuilder.start();
        Thread inputStreamThread = new Thread(new ReadStreamThread(process.getInputStream(), this.publisher, "inputStream"));
        Thread errorStreamThread = new Thread(new ReadStreamThread(process.getErrorStream(), this.publisher, "errorStream"));
        errorStreamThread.start();
        inputStreamThread.start();
        try{
            inputStreamThread.join();
            errorStreamThread.join();
        }
        catch (InterruptedException interruptedException){
            System.out.println(interruptedException);
        }


    }

    String createFileInTempDirectory(String content) throws IOException{
        String fileName = "scripToRun.kts";
        Path directoryPath = Files.createTempDirectory(Paths.get("./"),"temp");
        String absoluteFilePath = directoryPath.toString() + File.separator + fileName;
        File sourceFile = new File(absoluteFilePath);
        FileWriter fileWriter = new FileWriter(sourceFile);
        fileWriter.write(content);
        fileWriter.close();
        directoryPath.toFile().deleteOnExit();
        System.out.println(absoluteFilePath);
        return absoluteFilePath;
    }
}
