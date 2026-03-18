package ru.nsu.dsl.verify;

public class CalledBuilder {
    private final VerificationBuilder verificationBuilder;

    public CalledBuilder(VerificationBuilder verificationBuilder) {
        this.verificationBuilder = verificationBuilder;
    }

    public void once() {
        verificationBuilder.once();
    }

    public void never() {
        verificationBuilder.never();
    }

    public void times(int n) {
        verificationBuilder.times(n);
    }

    public void atLeast(int n) {
        verificationBuilder.atLeast(n);
    }

    public void atMost(int n) {
        verificationBuilder.atMost(n);
    }
}
