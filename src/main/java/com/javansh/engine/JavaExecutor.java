package com.javansh.engine;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;
import java.io.File;
import java.io.IOException;
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
        System.out.println("Java compiler not found. Use a JDK.");
        return;
    }

    int result = compiler.run(
            null,
            System.out,
            System.err,
            "-d", tempDir.toString(),
            javaFile.getAbsolutePath()
    );

    if (result == 0) {
        System.out.println("\nCompilation successful!");
    } else {
        System.out.println("\nCompilation failed!");
    }
}


}
