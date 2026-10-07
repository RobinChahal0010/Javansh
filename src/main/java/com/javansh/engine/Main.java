package com.javansh.engine;

public class Main {

    public static void main(String[] args) throws Exception {

        String code = """
                public class UserProgram {

                    public static void main(String[] args) {

                        int sum = 0;

                        for (int i = 1; i <= 5; i++) {
                            sum += i;
                        }

                        System.out.println("Sum = " + sum);
                    }
                }
                """;

        JavaExecutor executor = new JavaExecutor();

        ExecutionResult result = executor.execute(code);

        System.out.println("Compilation: "
                + result.isCompilationSuccessful());

        System.out.println("Output:");
        System.out.println(result.getOutput());

        System.out.println("Exit Code: "
                + result.getExitCode());

        System.out.println("Execution Time: "
                + result.getExecutionTimeMs() + " ms");
    }
}