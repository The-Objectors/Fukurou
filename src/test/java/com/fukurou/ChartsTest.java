package com.fukurou;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import java.util.Collections;
import org.junit.jupiter.api.Test;

public class ChartsTest {
    //Tests to make sure that chart builders handle empty data correctly
    
    @Test
    void createsActiveIdleCharWithEmptyData(){
        assertNotNull(Charts.activeIdleDonut(new EditorData()));
    }
    @Test
    void createsCompileCharWithEmptyData(){
        assertNotNull(Charts.compileOutcome(new CompileData()));
    }
    @Test
    void createsErrorChartWithNoErrors(){
        assertNotNull(Charts.errorBar(Collections.<String>emptyList()));
    }
    @Test
    void createsClassTimelineWithNoEvents(){
        assertNotNull(Charts.classTimeline(new EventLog()));
    }
}
