
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
        long startTime = System.nanoTime();

        try {
            File javaFile = tempDir.resolve("UserProgram.java").toFile();
            Files.writeString(javaFile.toPath(), sourceCode);

            Process compiler = new ProcessBuilder(
                    "javac", "-d", tempDir.toString(),
                    javaFile.getAbsolutePath()
            ).start();

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
                return result(ExecutionStatus.INTERRUPTED, false,
                        "", "Compilation interrupted.", -1, startTime);
            }

            if (!compiled) {
                compiler.destroyForcibly();
                return result(ExecutionStatus.TIMED_OUT, false,
                        "", "Compilation timed out.", -1, startTime);
            }

            String compilerErrors = getOutput(compileError);
            getOutput(compileOutput);

            if (compiler.exitValue() != 0) {
                return result(ExecutionStatus.COMPILATION_FAILED, false,
                        "", compilerErrors, compiler.exitValue(), startTime);
            }

            Process process = new ProcessBuilder(
                    "java", "-cp", tempDir.toString(), "UserProgram"
            ).start();

            CompletableFuture<String> outputFuture =
                    readAsync(process.getInputStream());
            CompletableFuture<String> errorFuture =
                    readAsync(process.getErrorStream());

            long executionStart = System.nanoTime();
            boolean finished;

            try {
                finished = process.waitFor(
                        EXECUTION_TIMEOUT_SECONDS, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                process.destroyForcibly();
                Thread.currentThread().interrupt();
                return result(ExecutionStatus.INTERRUPTED, true,
                        "", "Execution interrupted.", -1, executionStart);
            }

            if (!finished) {
                process.destroyForcibly();
                return result(ExecutionStatus.TIMED_OUT, true,
                        "", "Execution timed out after "
                                + EXECUTION_TIMEOUT_SECONDS + " seconds.",
                        -1, executionStart);
            }

            String output = getOutput(outputFuture);
            String error = getOutput(errorFuture);
            int exitCode = process.exitValue();

            ExecutionStatus status = exitCode == 0
                    ? ExecutionStatus.COMPLETED
                    : ExecutionStatus.RUNTIME_ERROR;

            return result(status, true, output, error,
                    exitCode, executionStart);

        } finally {
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

    private ExecutionResult result(
            ExecutionStatus status,
            boolean compilationSuccessful,
            String output,
            String error,
            int exitCode,
            long startTime
    ) {
        long elapsed = (System.nanoTime() - startTime) / 1_000_000;

        return new ExecutionResult(
                status, compilationSuccessful,
                output, error, exitCode, elapsed
        );
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
}
