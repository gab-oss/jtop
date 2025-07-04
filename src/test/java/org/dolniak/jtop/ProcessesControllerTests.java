package org.dolniak.jtop;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
public class ProcessesControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void getProcesses_shouldReturnOk() throws Exception {
        mockMvc.perform(get("/processes"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"));
    }

    @Test
    public void terminateProcessByPid_whenPidDoesNotExist_shouldReturnNotFound() throws Exception {
        mockMvc.perform(post("/processes/{pid}/terminate}", -5))
                .andExpect(status().isNotFound());
    }

}
