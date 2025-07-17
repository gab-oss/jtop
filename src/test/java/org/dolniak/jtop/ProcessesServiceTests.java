package org.dolniak.jtop;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

@SpringBootTest
@AutoConfigureJsonTesters
@Import(TestSystemInfoProviderConfig.class)
public class ProcessesServiceTests {

    @Autowired
    MockSystemInfoProvider mockSystemInfoProvider;

    @Autowired
    MockProcessKiller mockProcessKiller;

    @Autowired
    ProcessService processService;

    @Autowired
    private JacksonTester<Process> json;

    @Autowired
    private JacksonTester<Process[]> jsonList;

    @Value("classpath:expected/process.json")
    Resource expectedProcessJson;

    @Value("classpath:expected/processes.json")
    Resource expectedProcessesJson;

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
    void getProcesses_whenOneProcessRunning_shouldReturnItListed() throws IOException {
        // arrange
        String content = new String(expectedProcessJson.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
        Process process = json.parseObject(content);
        mockSystemInfoProvider.addProcess(process);
        
        // act
        List<Process> processes = processService.getProcesses();
        
        // assert
        Assertions.assertEquals(1, processes.size());
        Assertions.assertEquals(process, processes.getFirst());
    }



    @Test
    public void terminateProcess_whenPidDoesNotExist_shouldReturnNotFound() {
        // act
        KillAttemptResult terminated = processService.terminate(-100);

        // assert
        Assertions.assertEquals(KillAttemptResult.NOT_FOUND, terminated);
    }

    @Test
    public void terminateProcess_whenPidExists_shouldReturnSuccess() throws IOException {
        // arrange
        String content = new String(expectedProcessJson.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
        Process process = json.parseObject(content);
        mockSystemInfoProvider.addProcess(process);
        mockProcessKiller.kill(process.pid());

        // act
        KillAttemptResult terminated = processService.terminate(process.pid());

        // assert
        Assertions.assertEquals(KillAttemptResult.SUCCESS, terminated);
    }

    @Test
    public void terminateProcess_whenOwnedByRoot_shouldReturnNotPermitted() throws IOException {
        // arrange
        // todo maybe move setting up processes to system info mock class
        String content = new String(expectedProcessesJson.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
        Process[] processes = jsonList.parseObject(content);
        mockSystemInfoProvider.addProcesses(processes);
        int rootsProcessPid = -2;

        // act
        KillAttemptResult terminated = processService.terminate(rootsProcessPid);

        // assert
        Assertions.assertEquals(KillAttemptResult.NOT_PERMITTED, terminated);
    }

    @Test
    public void terminateProcess_whenFailed_shouldReturnFailed() throws IOException {
        // arrange
        String content = new String(expectedProcessJson.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
        Process process = json.parseObject(content);
        mockSystemInfoProvider.addUnkillableProcess(process);
        mockProcessKiller.kill(process.pid());

        // act
        KillAttemptResult terminated = processService.terminate(process.pid());

        // assert
        Assertions.assertEquals(KillAttemptResult.FAILED, terminated);
    }

}
