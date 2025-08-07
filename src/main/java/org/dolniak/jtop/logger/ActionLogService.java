package org.dolniak.jtop.logger;

import org.dolniak.jtop.Process;
import org.springframework.stereotype.Component;

@Component
public class ActionLogService {

    private final ActionLogRepository actionLogRepository;

    public ActionLogService(ActionLogRepository actionLogRepository) {
        this.actionLogRepository = actionLogRepository;
    }

    public void logFailedTerminationAttempt(org.dolniak.jtop.Process process, ActionType actionType) {
        logWithAllParams(process, actionType, ActionComment.FAILED);
    }

    public void logNotPermittedTerminationAttempt(org.dolniak.jtop.Process process, ActionType actionType) {
        logWithAllParams(process, actionType, ActionComment.NO_PERMISSION);
    }

    public void logProcessNotFoundTerminationAttempt(int pid, ActionType actionType) {
        ActionLogEntity actionLogEntity = new ActionLogEntity.Builder(
                pid, actionType)
                .comment(ActionComment.NOT_FOUND)
                .build();

        actionLogRepository.save(actionLogEntity);
    }

    public void logCurrentProcessTerminationAttempt(org.dolniak.jtop.Process process, ActionType actionType) {
        logWithAllParams(process, actionType, ActionComment.CURRENT_PROCESS);
    }

    public void logSuccessfulTermination(org.dolniak.jtop.Process process, ActionType actionType) {
        logWithAllParams(process, actionType, ActionComment.SUCCESS);
    }

    private void logWithAllParams(Process process, ActionType actionType, ActionComment actionComment) {
        ActionLogEntity actionLogEntity = new ActionLogEntity.Builder(
                process.pid(), actionType)
                .command(process.command())
                .owner(process.owner())
                .comment(actionComment)
                .build();

        actionLogRepository.save(actionLogEntity);
    }
}
