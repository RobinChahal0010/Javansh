package com.javansh.engine;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;

public class JavaExecutor {

    public ExecutionResult execute(String sourceCode) throws IOException {

        Path tempDir = Files.createTempDirectory("javansh-");
        File javaFile = tempDir.resolve("UserProgram.java").toFile();

        Files.writeString(javaFile.toPath(), sourceCode);

        // Compile
        ProcessBuilder compileProcess = new ProcessBuilder(
                "javac",
                "-d",
                tempDir.toString(),
                javaFile.getAbsolutePath()
        );

        Process compilerProcess = compileProcess.start();

        String compileOutput = readOutput(
                compilerProcess.getErrorStream()
        );

        int compileExitCode;

        try {
            compileExitCode = compilerProcess.waitFor();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();

            return new ExecutionResult(
                    false,
                    "",
                    "Compilation interrupted.",
                    -1,
                    0
            );
        }

        // Compilation failed
        if (compileExitCode != 0) {
            return new ExecutionResult(
                    false,
                    "",
                    compileOutput,
                    compileExitCode,
                    0
            );
        }

        // Execute
        ProcessBuilder runProcess = new ProcessBuilder(
                "java",
                "-cp",
                tempDir.toString(),
                "UserProgram"
        );

        Process process = runProcess.start();

        long startTime = System.nanoTime();

        String output = readOutput(
                process.getInputStream()
        );

        String error = readOutput(
                process.getErrorStream()
        );

        int exitCode;

        try {
            exitCode = process.waitFor();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();

            return new ExecutionResult(
                    true,
                    output,
                    "Execution interrupted.",
                    -1,
                    elapsedMillis(startTime)
            );
        }

        long executionTime = elapsedMillis(startTime);

        return new ExecutionResult(
                true,
                output,
                error,
                exitCode,
                executionTime
        );
    }

    private String readOutput(InputStream inputStream)
            throws IOException {

        BufferedReader reader = new BufferedReader(
                new InputStreamReader(inputStream)
        );

        StringBuilder output = new StringBuilder();

        String line;

        while ((line = reader.readLine()) != null) {
            output.append(line)
                  .append(System.lineSeparator());
        }

        return output.toString();
    }

    private long elapsedMillis(long startTime) {
        return (System.nanoTime() - startTime) / 1_000_000;
    }
}