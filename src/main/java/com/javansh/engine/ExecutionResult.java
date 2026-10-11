
package com.javansh.engine;

public class ExecutionResult {

    private final ExecutionStatus status;
    private final boolean compilationSuccessful;
    private final String output;
    private final String error;
    private final int exitCode;
    private final long executionTimeMs;

    public ExecutionResult(
            ExecutionStatus status,
            boolean compilationSuccessful,
            String output,
            String error,
            int exitCode,
            long executionTimeMs
    ) {
        this.status = status;
        this.compilationSuccessful = compilationSuccessful;
        this.output = output;
        this.error = error;
        this.exitCode = exitCode;
        this.executionTimeMs = executionTimeMs;
    }

    public ExecutionStatus getStatus() {
        return status;
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
                "status=" + status +
                ", compilationSuccessful=" + compilationSuccessful +
                ", output='" + output + '\'' +
                ", error='" + error + '\'' +
                ", exitCode=" + exitCode +
                ", executionTimeMs=" + executionTimeMs +
                '}';
    }
}
