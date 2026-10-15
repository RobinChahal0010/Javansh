package com.javansh.engine;

public class Main {

    public static void main(String[] args) throws Exception {

        String code = """
        public class UserProgram {
            public static void main(String[] args) {
                while (true) {
                }
            }
        }
        """;

        CodeAnalyzer analyzer = new CodeAnalyzer();

String sampleCode = """
        public class Example {
            public static void main(String[] args) {
                for (int i = 0; i < 10; i++) {
                    for (int j = 0; j < 10; j++) {
                        System.out.println(i + j);
                    }
                }
            }
        }
        """;

System.out.println("\nCode Intelligence:");
analyzer.analyze(sampleCode).forEach(System.out::println);

        JavaExecutor executor = new JavaExecutor();

        ExecutionResult result = executor.execute(code);

        System.out.println("Status: " + result.getStatus());
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