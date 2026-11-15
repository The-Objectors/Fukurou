package com.fukurou.telemetry;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class InvocationDataTest {
    private InvocationData data;

    @BeforeEach
    void setUp() {
        data = new InvocationData();
    }

    @Test
    void newInstanceStartsAtZero() {
        assertEquals(0, data.getInvocations());
        assertEquals(0, data.getRuntimeExceptions());
        assertEquals(0, data.getUserTerminations());
    }

    @Test
    void invocationsIncreaseAdvancesOnlyTheTotal() {
        data.invocationsIncrease();
        data.invocationsIncrease();

        assertEquals(2, data.getInvocations());
        assertEquals(0, data.getRuntimeExceptions());
        assertEquals(0, data.getUserTerminations());
    }

    @Test
    void runtimeExceptionIncreaseAlsoIncreasesTotalInvocations() {
        data.runtimeExceptionsIncrease();

        assertEquals(1, data.getInvocations());
        assertEquals(1, data.getRuntimeExceptions());
        assertEquals(0, data.getUserTerminations());
    }

    @Test
    void userTerminationIncreaseAlsoIncreasesTotalInvocations() {
        data.userTerminationsIncrease();

        assertEquals(1, data.getInvocations());
        assertEquals(0, data.getRuntimeExceptions());
        assertEquals(1, data.getUserTerminations());
    }

    @Test
    void mixedOutcomesResultInConsistentTotals() {
        data.invocationsIncrease();        // +1 total
        data.runtimeExceptionsIncrease();  // +1 total, +1 runtime
        data.userTerminationsIncrease();   // +1 total, +1 termination

        assertEquals(3, data.getInvocations());
        assertEquals(1, data.getRuntimeExceptions());
        assertEquals(1, data.getUserTerminations());
    }
}
