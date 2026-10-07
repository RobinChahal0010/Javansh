package com.javansh.engine;

public class ExecutionResult {

    private final boolean compilationSuccessful;
    private final String output;
    private final String error;
    private final int exitCode;
    private final long executionTimeMs;

    public ExecutionResult(
            boolean compilationSuccessful,
            String output,
            String error,
            int exitCode,
            long executionTimeMs
    ) {
        this.compilationSuccessful = compilationSuccessful;
        this.output = output;
        this.error = error;
        this.exitCode = exitCode;
        this.executionTimeMs = executionTimeMs;
    }

    public boolean isCompilationSuccessful() {
        return compilationSuccessful;
    }

    public String getOutput() {
        return output;
    }

    public String getError() {
        return error;
    }

    public int getExitCode() {
        return exitCode;
    }

    public long getExecutionTimeMs() {
        return executionTimeMs;
    }

    @Override
    public String toString() {
        return "ExecutionResult{" +
                "compilationSuccessful=" + compilationSuccessful +
                ", output='" + output + '\'' +
                ", error='" + error + '\'' +
                ", exitCode=" + exitCode +
                ", executionTimeMs=" + executionTimeMs +
                '}';
    }
}