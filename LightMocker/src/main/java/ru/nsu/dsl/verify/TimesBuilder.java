package ru.nsu.dsl.verify;

public class TimesBuilder {
    private final VerificationBuilder verificationBuilder;

    public TimesBuilder(VerificationBuilder verificationBuilder) {
        this.verificationBuilder = verificationBuilder;
    }

    public void times(int n) {
        verificationBuilder.times(n);
    }
}
