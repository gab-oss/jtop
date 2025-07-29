package org.dolniak.jtop;

import org.springframework.stereotype.Component;

@Component
public class ActionLogService {

    private final ActionLogRepository actionLogRepository;

    public ActionLogService(ActionLogRepository actionLogRepository) {
        this.actionLogRepository = actionLogRepository;
    }

    public void logFailedTerminationAtt(Process process, ActionType actionType) {
        ActionLogEntity actionLogEntity = new ActionLogEntity.Builder(
                process.pid(), actionType)
                .command(process.command())
                .owner(process.owner())
                .comment(ActionComment.FAILED)
                .build();

        actionLogRepository.save(actionLogEntity);
    }

    public void logNotPermittedTerminationAtt(Process process, ActionType actionType) {
        ActionLogEntity actionLogEntity = new ActionLogEntity.Builder(
                process.pid(), actionType)
                .command(process.command())
                .owner(process.owner())
                .comment(ActionComment.NO_PERMISSION)
                .build();

        actionLogRepository.save(actionLogEntity);
    }

    public void logProcessNotFoundTerminationAtt(int pid, ActionType actionType) {
        ActionLogEntity actionLogEntity = new ActionLogEntity.Builder(
                pid, actionType)
                .comment(ActionComment.NOT_FOUND)
                .build();

        actionLogRepository.save(actionLogEntity);
    }

    public void logCurrentProcessTerminationAtt(Process process, ActionType actionType) {
        ActionLogEntity actionLogEntity = new ActionLogEntity.Builder(
                process.pid(), actionType)
                .command(process.command())
                .owner(process.owner())
                .comment(ActionComment.CURRENT_PROCESS)
                .build();

        actionLogRepository.save(actionLogEntity);
    }

    public void logSuccessfulTermination(Process process, ActionType actionType) {
        ActionLogEntity actionLogEntity = new ActionLogEntity.Builder(
                process.pid(), actionType)
                .command(process.command())
                .owner(process.owner())
                .comment(ActionComment.SUCCESS)
                .build();

        actionLogRepository.save(actionLogEntity);
    }
}
