package org.dolniak.jtop;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ProcessHandlerDestroyTest {

    @Test
    void supportsNormalTermination_shouldReturnTrue() {
        Assertions.assertTrue(ProcessHandle.current().supportsNormalTermination());
    }

}
