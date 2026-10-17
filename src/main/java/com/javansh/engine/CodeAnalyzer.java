
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

        if (loopCount == 0) {
            findings.add("No loops detected.");
        } else if (loopCount == 1) {
            findings.add("One loop detected. Review its iteration count.");
        } else {
            findings.add(
                    loopCount + " loops detected. "
                    + "This basic analyzer cannot yet confirm nesting "
                    + "or determine Big-O complexity."
            );
        }

        return findings;
    }
}
