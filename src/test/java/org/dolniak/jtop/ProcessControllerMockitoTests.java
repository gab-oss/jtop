package org.dolniak.jtop;

import org.dolniak.jtop.exceptions.NoPermissionToKillProcessException;
import org.dolniak.jtop.exceptions.ProcessNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
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
        Mockito.when(processService.getProcessById(process.pid())).thenReturn(Optional.of(process));

        mockMvc.perform(get(GET_BY_ID, process.pid()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(CONTENT_JSON))
                .andExpect(content().json(content));
    }

    @Test
    public void getProcessById_ifDoesNotExist_shouldReturnNotFound() throws Exception {
        int id = -100;
        Mockito.when(processService.getProcessById(id)).thenReturn(Optional.empty());

        mockMvc.perform(get(GET_BY_ID, id))
                .andExpect(status().isNotFound());
    }

    @Test
    public void terminateProcessByPid_forced_whenPidExists_shouldReturnAccepted() throws Exception {
        int id = -100;
        Mockito.when(processService.terminate(id, false)).thenReturn(true);

        mockMvc.perform(post(POST_KILL, id))
                .andExpect(status().isAccepted());
        Mockito.verify(processService).terminate(id, false);
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "?force=false"})
    public void terminateProcessByPid_whenNotFound_shouldReturnNotFound(String params) throws Exception {
        int id = -100;
        Mockito.when(processService.terminate(id, false)).thenThrow(new ProcessNotFoundException());

        mockMvc.perform(post(POST_KILL + params, id))
                .andExpect(status().isNotFound());
        Mockito.verify(processService).terminate(id, false);
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "?force=false"})
    public void terminateProcessByPid_ifNotPermitted_shouldReturnForbidden(String params) throws Exception {
        int id = -100;
        Mockito.when(processService.terminate(id, false)).thenThrow(new NoPermissionToKillProcessException());

        mockMvc.perform(post(POST_KILL + params, id))
                .andExpect(status().isForbidden());
        Mockito.verify(processService).terminate(id, false);
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "?force=false"})
    public void terminateProcessByPid_ifFailed_shouldReturnConflict(String params) throws Exception {
        int id = -100;
        Mockito.when(processService.terminate(id, false)).thenReturn(false);

        mockMvc.perform(post(POST_KILL + params, id))
                .andExpect(status().isConflict());
        Mockito.verify(processService).terminate(id, false);
    }

    @Test
    public void terminateProcessByPid_forced_whenNotFound_shouldReturnNotFound() throws Exception {
        int id = -100;
        Mockito.when(processService.terminate(id, true)).thenThrow(new ProcessNotFoundException());

        mockMvc.perform(post(POST_KILL + "?force=true", id))
                .andExpect(status().isNotFound());
        Mockito.verify(processService).terminate(id, true);
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "?force=false"})
    public void terminateProcessByPid_whenPidExists_shouldReturnAccepted(String params) throws Exception {
        int id = -100;
        Mockito.when(processService.terminate(id, false)).thenReturn(true);

        mockMvc.perform(post(POST_KILL + params, id))
                .andExpect(status().isAccepted());
        Mockito.verify(processService).terminate(id, false);
    }

    @Test
    public void terminateProcessByPid_forced_ifNotPermitted_shouldReturnForbidden() throws Exception {
        int id = -100;
        Mockito.when(processService.terminate(id, true)).thenThrow(new NoPermissionToKillProcessException());

        mockMvc.perform(post(POST_KILL + "?force=true", id))
                .andExpect(status().isForbidden());
        Mockito.verify(processService).terminate(id, true);
    }

    @Test
    public void terminateProcessByPid_forced_ifFailed_shouldReturnConflict() throws Exception {
        int id = -100;
        Mockito.when(processService.terminate(id, true)).thenReturn(false);

        mockMvc.perform(post(POST_KILL + "?force=true", id))
                .andExpect(status().isConflict());
        Mockito.verify(processService).terminate(id, true);
    }
}
