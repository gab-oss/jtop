package org.dolniak.jtop;

import org.dolniak.jtop.exceptions.FailedToKillProcessException;
import org.dolniak.jtop.exceptions.NoPermissionToKillProcessException;
import org.dolniak.jtop.exceptions.ProcessNotFoundException;
import org.dolniak.jtop.exceptions.TriedToKillCurrentProcessException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.core.io.Resource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@AutoConfigureJsonTesters
@ExtendWith(SpringExtension.class)
public class ProcessServiceMockitoTests {

    @MockitoBean
    SystemInfoProvider systemInfoProvider;

    @MockitoBean
    ProcessKiller processKiller;

    @Autowired
    ProcessService processService;

    @Autowired
    private JacksonTester<Process> json;

    @Value("classpath:expected/process.json")
    Resource processJson;

    @Value("classpath:expected/root-owned-process.json")
    Resource rootProcessJson;

    private static final Path LOG_FILE = Paths.get("target/test-logs/test.log");

    @BeforeAll
    static void clearLogFile() throws Exception {
        if (Files.exists(LOG_FILE)) {
            Files.write(LOG_FILE, new byte[0]);
        }
    }

    @Test
    void getProcessById_whenNotRunning_shouldReturnEmpty() throws IOException {
        // arrange
        int id = -100;
        Mockito.when(systemInfoProvider.getProcessById(Mockito.anyInt())).thenReturn(Optional.empty());

        // act
        Optional<Process> actualProcess = processService.getProcessById(id);

        // assert
        Assertions.assertTrue(actualProcess.isEmpty());
    }

    @Test
    void getProcessById_whenRunning_shouldReturnProcess() throws IOException {
        // arrange
        String content = new String(processJson.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
        Process process = json.parseObject(content);
        Mockito.when(systemInfoProvider.getProcessById(process.pid())).thenReturn(Optional.of(process));

        // act
        Optional<Process> actualProcess = processService.getProcessById(process.pid());

        // assert
        Assertions.assertTrue(actualProcess.isPresent());
        Assertions.assertEquals(process, actualProcess.get());
    }

    @Test
    void getProcesses_whenNoProcessRunning_shouldRespondWithEmptyList() { // a bit of an artificial case
        // arrange
        Mockito.when(systemInfoProvider.getProcesses()).thenReturn(Collections.emptyList());

        // act
        List<Process> processes = processService.getProcesses();

        // assert
        Assertions.assertEquals(0, processes.size());
    }

    @Test
    void getProcesses_whenProcessRunning_shouldReturnNonEmptyList() throws IOException {
        // arrange
        String content = new String(processJson.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
        Process process = json.parseObject(content);
        Mockito.when(systemInfoProvider.getProcesses()).thenReturn(Collections.singletonList(process));

        // act
        List<Process> processes = processService.getProcesses();

        // assert
        Assertions.assertEquals(1, processes.size());
        Assertions.assertEquals(process, processes.getFirst());
    }

    @Test
    void terminateProcess_whenPidForCurrentProcess_shouldReturnFailed() throws IOException {
        // arrange
        String content = new String(processJson.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
        Process process = json.parseObject(content);
        Mockito.when(systemInfoProvider.getProcessById(process.pid())).thenReturn(Optional.of(process));
        Mockito.when(systemInfoProvider.getCurrentProcessId()).thenReturn(process.pid());

        // act + assert
        assertThrows(TriedToKillCurrentProcessException.class, () -> {
            processService.terminate(process.pid());
        });

    }

    @Test
    void terminateProcess_whenPidDoesNotExist_shouldReturnNotFound() {
        // arrange
        int id = -100;

        // act + assert
        assertThrows(ProcessNotFoundException.class, () -> {
            processService.terminate(id);
        });
    }

    @Test
    void terminateProcess_whenPidExists_shouldReturnSuccess() throws IOException {
        // arrange
        String content = new String(processJson.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
        Process process = json.parseObject(content);
        Mockito.when(systemInfoProvider.getProcessById(process.pid())).thenReturn(Optional.of(process));
        Mockito.when(processKiller.kill(process.pid())).thenReturn(true);

        // act
        boolean terminated = processService.terminate(process.pid());

        // assert
        Assertions.assertTrue(terminated);
    }

    @Test
    void terminateProcess_whenOwnedByRoot_shouldReturnNotPermitted() throws IOException {
        // arrange
        String content = new String(rootProcessJson.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
        Process process = json.parseObject(content);
        Mockito.when(systemInfoProvider.getProcessById(process.pid())).thenReturn(Optional.of(process));

        // act + assert
        assertThrows(NoPermissionToKillProcessException.class, () -> {
            processService.terminate(process.pid());
        });
    }

    @Test
    void terminateProcess_whenKillingFailed_shouldReturnFailed() throws IOException {
        // arrange
        String content = new String(processJson.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
        Process process = json.parseObject(content);
        Mockito.when(systemInfoProvider.getProcessById(process.pid())).thenReturn(Optional.of(process));
        Mockito.when(processKiller.kill(process.pid())).thenReturn(false);

        // act
        boolean terminated = processService.terminate(process.pid());

        // assert
        Assertions.assertFalse(terminated);
    }
}
