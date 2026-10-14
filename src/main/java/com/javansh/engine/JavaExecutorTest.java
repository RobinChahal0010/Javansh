
package com.javansh.engine;

public class JavaExecutorTest {

    public static void main(String[] args) throws Exception {
        JavaExecutor executor = new JavaExecutor();

        testSuccessfulExecution(executor);
        testCompilationFailure(executor);
        testRuntimeError(executor);
        testTimeout(executor);

        System.out.println("\nAll JAVANSH tests passed!");
    }

    private static void testSuccessfulExecution(JavaExecutor executor)
            throws Exception {

        ExecutionResult result = executor.execute("""
                public class UserProgram {
                    public static void main(String[] args) {
                        System.out.println("Hello JAVANSH");
                    }
                }
                """);

        check(result.getStatus() == ExecutionStatus.COMPLETED,
                "Successful execution status");
        check(result.getOutput().contains("Hello JAVANSH"),
                "Program output");

        System.out.println("PASS: Successful execution");
    }

    private static void testCompilationFailure(JavaExecutor executor)
            throws Exception {

        ExecutionResult result = executor.execute("""
                public class UserProgram {
                    public static void main(String[] args) {
                        int number = ;
                    }
                }
                """);

        check(result.getStatus() == ExecutionStatus.COMPILATION_FAILED,
                "Compilation failure status");

        System.out.println("PASS: Compilation failure");
    }

    private static void testRuntimeError(JavaExecutor executor)
            throws Exception {

        ExecutionResult result = executor.execute("""
                public class UserProgram {
                    public static void main(String[] args) {
                        int number = 10 / 0;
                    }
                }
                """);

        check(result.getStatus() == ExecutionStatus.RUNTIME_ERROR,
                "Runtime error status");

        check(result.getError().contains("ArithmeticException"),
                "Runtime error details");

        System.out.println("PASS: Runtime error");
    }

    private static void testTimeout(JavaExecutor executor)
            throws Exception {

        ExecutionResult result = executor.execute("""
                public class UserProgram {
                    public static void main(String[] args) {
                        while (true) {
                        }
                    }
                }
                """);

        check(result.getStatus() == ExecutionStatus.TIMED_OUT,
                "Timeout status");

        System.out.println("PASS: Execution timeout");
    }

    private static void check(boolean condition, String testName) {
        if (!condition) {
            throw new AssertionError("FAILED: " + testName);
        }
    }
}
