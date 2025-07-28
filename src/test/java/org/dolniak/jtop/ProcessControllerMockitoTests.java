package org.dolniak.jtop;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.Resource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Optional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProcessController.class)
@AutoConfigureJsonTesters
@Import(NoSecurityConfig.class)
public class ProcessControllerMockitoTests {

    private static final String GET_ALL = "/processes";
    private static final String GET_BY_ID = "/processes/{id}";
    private static final String POST_KILL = "/processes/{pid}/terminate";

    private static final String CONTENT_JSON = "application/json";

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProcessService processService;

    @Autowired
    private JacksonTester<Process> json;

    @Autowired
    private JacksonTester<Process[]> jsonList;

    @Value("classpath:expected/process.json")
    Resource expectedProcessJson;

    @Value("classpath:expected/processes.json")
    Resource expectedProcessesJson;


    @Test
    public void getProcesses_shouldReturnOk() throws Exception {
        String content = new String(expectedProcessesJson.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
        Process[] processes = jsonList.parseObject(content);
        Mockito.when(processService.getProcesses()).thenReturn(Arrays.asList(processes));

        mockMvc.perform(get(GET_ALL))
                .andExpect(status().isOk())
                .andExpect(content().contentType(CONTENT_JSON))
                .andExpect(content().json(content));
    }

    @Test
    public void getProcessById_ifExists_shouldReturnOk() throws Exception {
        String content = new String(expectedProcessJson.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
        Process process = json.parseObject(content);
        Mockito.when(processService.getProcessById(Mockito.anyInt())).thenReturn(Optional.of(process));

        mockMvc.perform(get(GET_BY_ID, process.pid()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(CONTENT_JSON))
                .andExpect(content().json(content));
    }

    @Test
    public void getProcessById_ifDoesNotExist_shouldReturnNotFound() throws Exception {
        int id = -100;
        Mockito.when(processService.getProcessById(Mockito.anyInt())).thenReturn(Optional.empty());

        mockMvc.perform(get(GET_BY_ID, id))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(CONTENT_JSON))
                .andExpect(content().json("{ error: \"" + String.format(Messages.PROCESS_NOT_FOUND, id) + "\" }"));
    }

    @Test
    public void terminateProcessByPid_whenPidExists_shouldReturnAccepted() throws Exception {
        int id = -100;
        Mockito.when(processService.terminate(Mockito.anyInt())).thenReturn(KillAttemptResult.SUCCESS);

        mockMvc.perform(post(POST_KILL, id))
                .andExpect(status().isAccepted());
    }

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
