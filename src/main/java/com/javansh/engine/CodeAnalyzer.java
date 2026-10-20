
package com.javansh.engine;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CodeAnalyzer {

    private static final Pattern LOOP_PATTERN =
            Pattern.compile("\\b(for|while)\\s*\\(");

    public List<String> analyze(String sourceCode) {
        List<String> findings = new ArrayList<>();

        String code = removeCommentsAndStrings(sourceCode);
        Matcher matcher = LOOP_PATTERN.matcher(code);

        int braceDepth = 0;
        int maxLoopDepth = 0;
        int totalLoops = 0;

        while (matcher.find()) {
            int loopPosition = matcher.start();
            int depth = 0;

            for (int i = 0; i < loopPosition; i++) {
                if (code.charAt(i) == '{') {
                    depth++;
                } else if (code.charAt(i) == '}') {
                    depth--;
                }
            }

            totalLoops++;
            maxLoopDepth = Math.max(maxLoopDepth, depth);
        }

        if (totalLoops == 0) {
            findings.add("No loops detected.");
        } else {
            findings.add("Loops detected: " + totalLoops);
            findings.add(
                    "This basic analyzer cannot reliably determine "
                    + "loop nesting or Big-O complexity yet."
            );
        }

        return findings;
    }

    private String removeCommentsAndStrings(String source) {
        return source
                .replaceAll("(?s)/\\*.*?\\*/", " ")
                .replaceAll("(?m)//.*$", " ")
                .replaceAll("\"(?:\\\\.|[^\"\\\\])*\"", "\"\"");
    }
}
