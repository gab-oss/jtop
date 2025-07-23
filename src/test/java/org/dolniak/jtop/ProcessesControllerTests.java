package org.dolniak.jtop;

import org.junit.jupiter.api.Test;
import org.springframework.core.io.Resource;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Optional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.springframework.boot.test.json.JacksonTester;

@WebMvcTest(ProcessesController.class)
@AutoConfigureJsonTesters
@Import({ProcessesControllerTests.TestConfig.class, NoSecurityConfig.class})
public class ProcessesControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProcessService processService;

    @Autowired
    private JacksonTester<Process> json;

    @Autowired
    private JacksonTester<Process[]> jsonList;

    @Value("classpath:expected/process.json")
    Resource expectedProcessJson;

    @Value("classpath:expected/processes.json")
    Resource expectedProcessesJson;

    private static final String GET_ALL = "/processes";
    private static final String GET_BY_ID = "/processes/{id}";
    private static final String POST_KILL = "/processes/{pid}/terminate";

    @TestConfiguration
    static class TestConfig {
        @Bean
        public ProcessService processService() {
            return Mockito.mock(ProcessService.class);
        }
    }

    @Test
    public void getProcesses_shouldReturnOk() throws Exception {
        String content = new String(expectedProcessesJson.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
        Process[] processes = jsonList.parseObject(content);
        Mockito.when(processService.getProcesses()).thenReturn(Arrays.asList(processes));

        mockMvc.perform(get(GET_ALL))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(content().json(content));
    }

    @Test
    public void getProcessById_ifExists_shouldReturnOk() throws Exception {
        String content = new String(expectedProcessJson.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
        Process process = json.parseObject(content);
        Mockito.when(processService.getProcessById(Mockito.anyInt())).thenReturn(Optional.of(process));

        mockMvc.perform(get(GET_BY_ID, process.pid()))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(content().json(content));
    }

    @Test
    public void getProcessById_ifDoesNotExist_shouldReturnNotFound() throws Exception {
        int id = -100;
        Mockito.when(processService.getProcessById(Mockito.anyInt())).thenReturn(Optional.empty());

        mockMvc.perform(get(GET_BY_ID, id))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType("application/json"))
                .andExpect(content().json("{ error: \"" + String.format(Messages.PROCESS_NOT_FOUND, id) + "\" }"));
    }

    @Test
    public void terminateProcessByPid_whenPidDoesNotExist_shouldReturnNotFound() throws Exception {
        int id = -100;
        Mockito.when(processService.terminate(Mockito.anyInt())).thenReturn(KillAttemptResult.NOT_FOUND);
        mockMvc.perform(post(POST_KILL, id))
                .andExpect(status().isNotFound());
    }

    @Test
    public void terminateProcessByPid_whenPidExists_shouldReturnAccepted() throws Exception {
        int id = -100;
        Mockito.when(processService.terminate(Mockito.anyInt())).thenReturn(KillAttemptResult.SUCCESS);
        mockMvc.perform(post("/processes/{pid}/terminate", id))
                .andExpect(status().isAccepted());
    }

    // todo change all ids to pids
    @Test
    public void terminateProcessByPid_ifNotPermitted_shouldReturnForbidden() throws Exception {
        int id = -100;
        Mockito.when(processService.terminate(Mockito.anyInt())).thenReturn(KillAttemptResult.NOT_PERMITTED);

        mockMvc.perform(post(POST_KILL, id))
                .andExpect(status().isForbidden());
    }

    @Test
    public void terminateProcessByPid_ifFailed_shouldReturnConflict() throws Exception {
        int id = -100;
        Mockito.when(processService.terminate(Mockito.anyInt())).thenReturn(KillAttemptResult.FAILED);

        mockMvc.perform(post(POST_KILL, id))
                .andExpect(status().isConflict());
    }

}
