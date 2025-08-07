package org.dolniak.jtop;

import org.dolniak.jtop.exceptions.NoPermissionToKillProcessException;
import org.dolniak.jtop.exceptions.ProcessNotFoundException;
import org.dolniak.jtop.exceptions.TriedToKillCurrentProcessException;
import org.dolniak.jtop.system.ProcessInfoProvider;
import org.dolniak.jtop.system.ProcessKiller;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
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
import static org.mockito.Mockito.never;

@SpringBootTest
@AutoConfigureJsonTesters
@ExtendWith(SpringExtension.class)
public class ProcessServiceMockitoTests {

    @MockitoBean
    ProcessInfoProvider processInfoProvider;

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
        Mockito.when(processInfoProvider.getProcessById(Mockito.anyInt())).thenReturn(Optional.empty());

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
        Mockito.when(processInfoProvider.getProcessById(process.pid())).thenReturn(Optional.of(process));

        // act
        Optional<Process> actualProcess = processService.getProcessById(process.pid());

        // assert
        Assertions.assertTrue(actualProcess.isPresent());
        Assertions.assertEquals(process, actualProcess.get());
    }

    @Test
    void getProcesses_whenNoProcessRunning_shouldRespondWithEmptyList() { // a bit of an artificial case
        // arrange
        Mockito.when(processInfoProvider.getProcesses()).thenReturn(Collections.emptyList());

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
        Mockito.when(processInfoProvider.getProcesses()).thenReturn(Collections.singletonList(process));

        // act
        List<Process> processes = processService.getProcesses();

        // assert
        Assertions.assertEquals(1, processes.size());
        Assertions.assertEquals(process, processes.getFirst());
    }

    @ParameterizedTest
    @ValueSource(booleans = {false, true})
    void terminateProcess_whenPidForCurrentProcess_shouldReturnFailed(boolean force) throws IOException {
        // arrange
        String content = new String(processJson.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
        Process process = json.parseObject(content);
        Mockito.when(processInfoProvider.getProcessById(process.pid())).thenReturn(Optional.of(process));
        Mockito.when(processInfoProvider.getCurrentProcessId()).thenReturn(process.pid());

        // act + assert
        assertThrows(TriedToKillCurrentProcessException.class, () -> {
            processService.terminate(process.pid(), force);
        });
        Mockito.verify(processKiller, never()).kill(process.pid(), force);
    }

    @ParameterizedTest
    @ValueSource(booleans = {false, true})
    void terminateProcess_whenPidDoesNotExist_shouldReturnNotFound(boolean force) {
        // arrange
        int id = -100;

        // act + assert
        assertThrows(ProcessNotFoundException.class, () -> {
            processService.terminate(id, force);
        });
    }

    @ParameterizedTest
    @ValueSource(booleans = {false, true})
    void terminateProcess_whenPidExists_shouldReturnSuccess(boolean force) throws IOException {
        // arrange
        String content = new String(processJson.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
        Process process = json.parseObject(content);
        Mockito.when(processInfoProvider.getProcessById(process.pid())).thenReturn(Optional.of(process));
        Mockito.when(processKiller.kill(process.pid(), force)).thenReturn(true);

        // act
        boolean terminated = processService.terminate(process.pid(), force);

        // assert
        Assertions.assertTrue(terminated);
        Mockito.verify(processKiller).kill(process.pid(), force);
    }

    @ParameterizedTest
    @ValueSource(booleans = {false, true})
    void terminateProcess_whenOwnedByRoot_shouldReturnNotPermitted(boolean force) throws IOException {
        // arrange
        String content = new String(rootProcessJson.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
        Process process = json.parseObject(content);
        Mockito.when(processInfoProvider.getProcessById(process.pid())).thenReturn(Optional.of(process));

        // act + assert
        assertThrows(NoPermissionToKillProcessException.class, () -> {
            processService.terminate(process.pid(), force);
        });
        Mockito.verify(processKiller, never()).kill(process.pid(), force);
    }

    @ParameterizedTest
    @ValueSource(booleans = {false, true})
    void terminateProcess_whenKillingFailed_shouldReturnFailed(boolean force) throws IOException {
        // arrange
        String content = new String(processJson.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
        Process process = json.parseObject(content);
        Mockito.when(processInfoProvider.getProcessById(process.pid())).thenReturn(Optional.of(process));
        Mockito.when(processKiller.kill(process.pid(), force)).thenReturn(false);

        // act
        boolean terminated = processService.terminate(process.pid(), force);

        // assert
        Assertions.assertFalse(terminated);
        Mockito.verify(processKiller).kill(process.pid(), force);
    }
}
