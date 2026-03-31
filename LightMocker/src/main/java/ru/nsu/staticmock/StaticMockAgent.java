package ru.nsu.staticmock;

import java.lang.instrument.Instrumentation;

public final class StaticMockAgent {

    private static volatile Instrumentation instrumentation;

    private StaticMockAgent() {
    }

    public static void premain(String agentArgs, Instrumentation inst) {
        instrumentation = inst;
    }

    public static Instrumentation getInstrumentation() {
        if (instrumentation == null) {
            throw new IllegalStateException(
                    "Static mock agent is not installed. Run tests/app with -javaagent:<path-to-jar>");
        }
        return instrumentation;
    }
}
