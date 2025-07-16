package org.dolniak.jtop;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProcessesController.class)
@Import(ProcessesControllerTests.TestConfig.class)
public class ProcessesControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProcessService processService;

    @TestConfiguration
    static class TestConfig {
        @Bean
        public ProcessService processService() {
            return Mockito.mock(ProcessService.class);
        }
    }

    @Test
    public void getProcesses_shouldReturnOk() throws Exception {
        Mockito.when(processService.getProcesses()).thenReturn(List.of(new Process(-5, "process1")));
        mockMvc.perform(get("/processes"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(content().string("[{\"pid\":-5,\"command\":\"process1\"}]"));
    }

    @Test
    public void terminateProcessByPid_whenPidDoesNotExist_shouldReturnNotFound() throws Exception {
        mockMvc.perform(post("/processes/{pid}/terminate}", -5))
                .andExpect(status().isNotFound());
    }

    @Test
    public void terminateProcessByPid_whenPidExists_shouldReturnAccepted() throws Exception {
        Mockito.when(processService.terminate(Mockito.anyInt())).thenReturn(true);
        mockMvc.perform(post("/processes/{pid}/terminate", -5))
                .andExpect(status().isAccepted());

    }

}
