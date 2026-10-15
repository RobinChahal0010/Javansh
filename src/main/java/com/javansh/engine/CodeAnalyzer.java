
package com.javansh.engine;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CodeAnalyzer {

    public List<String> analyze(String sourceCode) {
        List<String> findings = new ArrayList<>();

        Pattern loopPattern = Pattern.compile(
                "\\b(for|while)\\s*\\("
        );

        Matcher matcher = loopPattern.matcher(sourceCode);
        int loopCount = 0;

        while (matcher.find()) {
            loopCount++;
        }

        if (loopCount >= 2) {
            findings.add(
                    "Multiple loops detected. Review whether any loops "
                    + "are nested, as nested loops may increase time complexity."
            );
        }

        if (findings.isEmpty()) {
            findings.add("No obvious loop-related patterns detected.");
        }

        return findings;
    }
}
