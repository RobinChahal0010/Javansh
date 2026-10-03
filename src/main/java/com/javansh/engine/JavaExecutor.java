package com.javansh.engine;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;

public class JavaExecutor {

    public static void main(String[] args) throws IOException {

        String sourceCode = """
                public class UserProgram {
                    public static void main(String[] args) {
                        int a = 10;
                        int b = 20;
                        int sum = a + b;

                        System.out.println("Sum: " + sum);
                    }
                }
                """;

        Path tempDir = Files.createTempDirectory("javansh-");
        File javaFile = tempDir.resolve("UserProgram.java").toFile();

        Files.writeString(javaFile.toPath(), sourceCode);

        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();

        if (compiler == null) {
            System.out.println("Java compiler not found.");
            return;
        }

        // Compile
        int compileResult = compiler.run(
                null,
                null,
                null,
                "-d",
                tempDir.toString(),
                javaFile.getAbsolutePath()
        );

        if (compileResult != 0) {
            System.out.println("Compilation failed!");
            return;
        }

        System.out.println("Compilation successful!");

        // Execute
        ProcessBuilder processBuilder = new ProcessBuilder(
                "java",
                "-cp",
                tempDir.toString(),
                "UserProgram"
        );

        processBuilder.redirectErrorStream(true);

        Process process = processBuilder.start();

        BufferedReader reader = new BufferedReader(
                new InputStreamReader(process.getInputStream())
        );

        String line;

        System.out.println("\n--- Program Output ---");

        while ((line = reader.readLine()) != null) {
            System.out.println(line);
        }

        int exitCode;

        try {
            exitCode = process.waitFor();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.out.println("Execution interrupted.");
            return;
        }

        System.out.println("----------------------");
        System.out.println("Exit Code: " + exitCode);
    }
}