package com.fukurou.telemetry;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CompileDataTest {
    private CompileData data;

    @BeforeEach
    void setUp() {
        data = new CompileData();
    }

    @Test
    void newInstanceStartsAtZeroWithEmptyErrorInfo() {
        assertEquals(0, data.getCompileCount());
        assertEquals(0, data.getCompileSuccess());
        assertEquals(0, data.getCompileFail());
        assertEquals(0, data.getCompileError());
        assertEquals(0, data.getCompileWarn());
        assertTrue(data.getErrorInfo().isEmpty());
    }

    @Test
    void incrementorsEachAdvanceTheirOwnCounterIndependently() {
        data.countIncrease();
        data.countIncrease();
        data.successIncrease();
        data.failIncrease();
        data.errorIncrease();
        data.errorIncrease();
        data.errorIncrease();
        data.warnIncrease();

        assertEquals(2, data.getCompileCount());
        assertEquals(1, data.getCompileSuccess());
        assertEquals(1, data.getCompileFail());
        assertEquals(3, data.getCompileError());
        assertEquals(1, data.getCompileWarn());
    }

    @Test
    void addErrorInfoAppendsMessagesInOrder() {
        data.addErrorInfo("first error");
        data.addErrorInfo("second error");

        ArrayList<String> errors = data.getErrorInfo();
        assertEquals(2, errors.size());
        assertEquals("first error", errors.get(0));
        assertEquals("second error", errors.get(1));
    }
}
