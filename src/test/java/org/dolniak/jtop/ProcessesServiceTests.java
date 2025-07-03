package org.dolniak.jtop;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import java.util.List;

@SpringBootTest
@Import(TestSystemInfoProviderConfig.class)
public class ProcessesServiceTests {

    @Autowired
    MockSystemInfoProvider mockSystemInfoProvider;

    @Autowired
    ProcessService processService;

    @Test
    void getProcesses_whenNoProcessRunning_shouldRespondWithEmptyList() { // a bit of an artificial case
        // arrange

        // act
        List<Process> processEntryList = processService.getProcesses();

        // assert
        Assertions.assertEquals(0, processEntryList.size());
    }
}
