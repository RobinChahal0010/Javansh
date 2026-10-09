
package com.javansh.engine;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class JavaExecutor {

    private static final long COMPILE_TIMEOUT_SECONDS = 10;
    private static final long EXECUTION_TIMEOUT_SECONDS = 3;

    public ExecutionResult execute(String sourceCode) throws IOException {

        Path tempDir = Files.createTempDirectory("javansh-");
        File javaFile = tempDir.resolve("UserProgram.java").toFile();

        Files.writeString(javaFile.toPath(), sourceCode);

        try {
            // Compile the submitted Java program
            ProcessBuilder compileBuilder = new ProcessBuilder(
                    "javac", "-d", tempDir.toString(),
                    javaFile.getAbsolutePath()
            );

            Process compiler = compileBuilder.start();

            CompletableFuture<String> compileOutput =
                    readAsync(compiler.getInputStream());

            CompletableFuture<String> compileError =
                    readAsync(compiler.getErrorStream());

            boolean compiled;

            try {
                compiled = compiler.waitFor(
                        COMPILE_TIMEOUT_SECONDS, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                compiler.destroyForcibly();
                Thread.currentThread().interrupt();

                return new ExecutionResult(
                        false, "", "Compilation interrupted.", -1, 0);
            }

            if (!compiled) {
                compiler.destroyForcibly();

                return new ExecutionResult(
                        false, "",
                        "Compilation timed out after "
                                + COMPILE_TIMEOUT_SECONDS + " seconds.",
                        -1, 0);
            }

            String compilerErrors = getOutput(compileError);
            getOutput(compileOutput);

            if (compiler.exitValue() != 0) {
                return new ExecutionResult(
                        false, "", compilerErrors,
                        compiler.exitValue(), 0);
            }

            // Execute the compiled program
            ProcessBuilder runBuilder = new ProcessBuilder(
                    "java", "-cp", tempDir.toString(), "UserProgram"
            );

            Process process = runBuilder.start();

            CompletableFuture<String> outputFuture =
                    readAsync(process.getInputStream());

            CompletableFuture<String> errorFuture =
                    readAsync(process.getErrorStream());

            long startTime = System.nanoTime();

            boolean finished;

            try {
                finished = process.waitFor(
                        EXECUTION_TIMEOUT_SECONDS, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                process.destroyForcibly();
                Thread.currentThread().interrupt();

                return new ExecutionResult(
                        true, "", "Execution interrupted.",
                        -1, elapsedMillis(startTime));
            }

            long executionTime = elapsedMillis(startTime);

            if (!finished) {
                process.destroyForcibly();

                return new ExecutionResult(
                        true, "",
                        "Execution timed out after "
                                + EXECUTION_TIMEOUT_SECONDS + " seconds.",
                        -1, executionTime);
            }

            String output = getOutput(outputFuture);
            String error = getOutput(errorFuture);

            return new ExecutionResult(
                    true,
                    output,
                    error,
                    process.exitValue(),
                    executionTime
            );

        } finally {
            // Remove temporary source and compiled files
            try (var paths = Files.walk(tempDir)) {
                paths.sorted((a, b) -> b.compareTo(a))
                        .forEach(path -> {
                            try {
                                Files.deleteIfExists(path);
                            } catch (IOException ignored) {
                                // Best-effort cleanup
                            }
                        });
            }
        }
    }

    private CompletableFuture<String> readAsync(InputStream stream) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return readOutput(stream);
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        });
    }

    private String getOutput(CompletableFuture<String> future)
            throws IOException {

        try {
            return future.get();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IOException("Output reading interrupted.", e);
        } catch (ExecutionException e) {
            throw new IOException("Failed to read process output.", e.getCause());
        }
    }

    private String readOutput(InputStream stream) throws IOException {
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(stream))) {

            StringBuilder output = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                output.append(line).append(System.lineSeparator());
            }

            return output.toString();
        }
    }

    private long elapsedMillis(long startTime) {
        return (System.nanoTime() - startTime) / 1_000_000;
    }
}