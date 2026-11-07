package com.fukurou;

public class TelemetryExport {
    private final CompileData compile;
    private final LifecycleData lifecycle;

    public TelemetryExport(CompileData compile, LifecycleData lifecycle) {
        this.compile = compile;
        this.lifecycle = lifecycle;
    }

    public CompileData   getCompile()   { return compile; }
    public LifecycleData getLifecycle() { return lifecycle; }
}