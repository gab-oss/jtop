package org.jtoptool.jtop;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureJsonTesters
public class JtopIntegrationTests {

    private static final String GET_ALL = "/processes";
    private static final String GET_BY_ID = "/processes/{id}";
    private static final String POST_KILL = "/processes/{pid}/terminate";

    private static final String CONTENT_JSON = "application/json";

    @Autowired
    TestRestTemplate restTemplate;

    @Test
    public void getProcesses_shouldReturnOk() throws Exception {
        ResponseEntity<Iterable> response = restTemplate.withBasicAuth("user", "password").getForEntity(GET_ALL, Iterable.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }
}
