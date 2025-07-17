package org.dolniak.jtop;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Import(TestSystemInfoProviderConfig.class)
public class ProcessesServiceTests {

    @Autowired
    MockSystemInfoProvider mockSystemInfoProvider;

    @Autowired
    MockProcessKiller mockProcessKiller;

    @Autowired
    ProcessService processService;

    @AfterEach
    public void cleanup() {
        mockSystemInfoProvider.removeProcesses();
    }

    @Test
    void getProcesses_whenNoProcessRunning_shouldRespondWithEmptyList() { // a bit of an artificial case
        // arrange

        // act
        List<Process> processes = processService.getProcesses();

        // assert
        Assertions.assertEquals(0, processes.size());
    }

    @Test
    void getProcesses_whenOneProcessRunning_shouldReturnItInList() {
        // arrange
        Process process = new Process(1, "process1", "user");
        mockSystemInfoProvider.addProcess(process);
        
        // act
        List<Process> processes = processService.getProcesses();
        
        // assert
        Assertions.assertEquals(1, processes.size());
        Assertions.assertEquals(process, processes.getFirst());
    }

    @Test
    public void terminateProcess_whenPidDoesNotExist_shouldReturnFalse() {
        // act
        KillAttemptResult terminated = processService.terminate(-5);

        // assert
        Assertions.assertEquals(KillAttemptResult.NOT_FOUND, terminated);
    }

    @Test
    public void terminateProcess_whenPidExists_shouldReturnTrue() {
        // arrange
        Process process = new Process(-5, "process1", "user");
        mockSystemInfoProvider.addProcess(process);
        mockProcessKiller.kill(-5);

        // act
        KillAttemptResult terminated = processService.terminate(-5);

        // assert
        Assertions.assertEquals(KillAttemptResult.SUCCESS, terminated);
    }

}
