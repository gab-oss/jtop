package org.dolniak.jtop;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ProcessHandlerDestroyTest {

    // todo check if really needed (here?)
    @Test
    void supportsNormalTermination_shouldReturnTrue() {
        Assertions.assertTrue(ProcessHandle.current().supportsNormalTermination());
    }

}
