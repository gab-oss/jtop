package org.dolniak.jtop;

import org.dolniak.jtop.exceptions.NoPermissionToKillProcessException;
import org.dolniak.jtop.exceptions.ProcessNotFoundException;
import org.dolniak.jtop.exceptions.TriedToKillCurrentProcessException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Component
public class ProcessService {

    private final ActionLogService actionLogService;
    private final SystemInfoProvider systemInfoProvider;
    private final ProcessKiller processKiller;
    private static final Logger LOGGER = LoggerFactory.getLogger(ProcessService.class);

    public ProcessService(ActionLogService actionLogService, SystemInfoProvider systemInfoProvider,
                          ProcessKiller processKiller) {
        this.actionLogService = actionLogService;
        this.systemInfoProvider = systemInfoProvider;
        this.processKiller = processKiller;
    }

    public List<Process> getProcesses() {
        return systemInfoProvider.getProcesses();
    }

    public boolean terminate(int pid) {
        ActionType actionType = ActionType.SIGTERM; // todo add sigkill
        Optional<Process> optProcess = systemInfoProvider.getProcessById(pid);

        if (optProcess.isEmpty()) {
            LOGGER.warn("Failed to kill: pid {}; process was null", pid);
            actionLogService.logProcessNotFoundTerminationAtt(pid, actionType);
            throw new ProcessNotFoundException();
        }

        Process process = optProcess.get();

        if (isCurrentProcess(pid)) {
            LOGGER.warn("Failed to kill: pid {}; current process", pid);
            actionLogService.logCurrentProcessTerminationAtt(process, actionType);
            throw new TriedToKillCurrentProcessException();
        }

        if (isKillingPermitted(process)) {
            LOGGER.warn("Failed to kill: pid {}; owner was root", pid);
            actionLogService.logNotPermittedTerminationAtt(process, actionType);
            throw new NoPermissionToKillProcessException();
        }

        if (processKiller.kill(pid)) {
            LOGGER.info("Killed: pid {}, command {}, owner {}", pid, process.command(), process.owner());
            actionLogService.logSuccessfulTermination(process, actionType);
            return true;
        }

        LOGGER.warn("Failed to kill: pid {}, command {}, owner {}", pid, process.command(), process.owner());
        actionLogService.logFailedTerminationAtt(process, actionType);
        return false;
    }

    private boolean isCurrentProcess(int pid) {
        return pid == systemInfoProvider.getCurrentProcessId();
    }

    private boolean isKillingPermitted(Process process) {
        return process.owner().contains("root");
    }

    public Optional<Process> getProcessById(int pid) {
        return systemInfoProvider.getProcessById(pid);
    }
}
