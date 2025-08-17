package org.jtoptool.jtop;

import org.jtoptool.jtop.logger.*;
import org.jtoptool.jtop.logger.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
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
import java.util.function.BiConsumer;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;

@SpringBootTest
@AutoConfigureJsonTesters
@ExtendWith(SpringExtension.class)
public class ActionLogServiceTests {

    @MockitoBean
    private ActionLogRepository actionLogRepository;

    @Autowired
    private ActionLogService actionLogService;

    @Autowired
    private JacksonTester<Process> json;

    @Value("classpath:expected/process.json")
    Resource processJson;

    @ParameterizedTest
    @MethodSource("logMethods")
    void shouldLogCorrectComment(BiConsumer<ActionLogService, Process> logMethod, ActionComment expectedComment) throws IOException {
        String content = new String(processJson.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
        Process process = json.parseObject(content);

        logMethod.accept(actionLogService, process);

        ArgumentCaptor<ActionLogEntity> captor = ArgumentCaptor.forClass(ActionLogEntity.class);
        verify(actionLogRepository).save(captor.capture());
        assertEquals(expectedComment, captor.getValue().getComment());
    }

    static Stream<Arguments> logMethods() {
        return Stream.of(
                Arguments.of((BiConsumer<ActionLogService, Process>) (s, p) -> s.logFailedTerminationAttempt(p, ActionType.SIGTERM), ActionComment.FAILED),
                Arguments.of((BiConsumer<ActionLogService, Process>) (s, p) -> s.logNotPermittedTerminationAttempt(p, ActionType.SIGTERM), ActionComment.NO_PERMISSION),
                Arguments.of((BiConsumer<ActionLogService, Process>) (s, p) -> s.logCurrentProcessTerminationAttempt(p, ActionType.SIGTERM), ActionComment.CURRENT_PROCESS),
                Arguments.of((BiConsumer<ActionLogService, Process>) (s, p) -> s.logSuccessfulTermination(p, ActionType.SIGTERM), ActionComment.SUCCESS),
                Arguments.of((BiConsumer<ActionLogService, Process>) (s, p) -> s.logFailedTerminationAttempt(p, ActionType.SIGKILL), ActionComment.FAILED),
                Arguments.of((BiConsumer<ActionLogService, Process>) (s, p) -> s.logNotPermittedTerminationAttempt(p, ActionType.SIGKILL), ActionComment.NO_PERMISSION),
                Arguments.of((BiConsumer<ActionLogService, Process>) (s, p) -> s.logCurrentProcessTerminationAttempt(p, ActionType.SIGKILL), ActionComment.CURRENT_PROCESS),
                Arguments.of((BiConsumer<ActionLogService, Process>) (s, p) -> s.logSuccessfulTermination(p, ActionType.SIGKILL), ActionComment.SUCCESS)
        );
    }

    @ParameterizedTest
    @EnumSource(ActionType.class)
    void shouldLogCorrectCommentWhenNotFound(ActionType actionType) throws IOException {
        String content = new String(processJson.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
        Process process = json.parseObject(content);

        actionLogService.logProcessNotFoundTerminationAttempt(process.pid(), actionType);

        ArgumentCaptor<ActionLogEntity> captor = ArgumentCaptor.forClass(ActionLogEntity.class);
        verify(actionLogRepository).save(captor.capture());
        assertEquals(ActionComment.NOT_FOUND, captor.getValue().getComment());
    }
}
