package com.javansh.engine;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;

public class JavaExecutor {

    public ExecutionResult execute(String sourceCode) throws IOException {

        Path tempDir = Files.createTempDirectory("javansh-");
        File javaFile = tempDir.resolve("UserProgram.java").toFile();

        Files.writeString(javaFile.toPath(), sourceCode);

        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();

        if (compiler == null) {
            return new ExecutionResult(
                    false,
                    "",
                    "Java compiler not found.",
                    -1,
                    0
            );
        }

        // Compile
        ProcessBuilder compileProcess = new ProcessBuilder(
                "javac",
                "-d",
                tempDir.toString(),
                javaFile.getAbsolutePath()
        );

        compileProcess.redirectErrorStream(true);

        Process compilerProcess = compileProcess.start();

        String compileOutput = readOutput(
                compilerProcess.getInputStream()
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

        runProcess.redirectErrorStream(true);

        long startTime = System.nanoTime();

        Process process = runProcess.start();

        String output = readOutput(
                process.getInputStream()
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
                "",
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
            output.append(line).append(System.lineSeparator());
        }

        return output.toString();
    }

    private long elapsedMillis(long startTime) {
        return (System.nanoTime() - startTime) / 1_000_000;
    }
}