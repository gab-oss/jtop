package org.jtoptool.jtop;

import org.jtoptool.jtop.exceptions.NoPermissionToKillProcessException;
import org.jtoptool.jtop.exceptions.ProcessNotFoundException;
import org.jtoptool.jtop.exceptions.TriedToKillCurrentProcessException;
import org.jtoptool.jtop.logger.ActionLogService;
import org.jtoptool.jtop.logger.ActionType;
import org.jtoptool.jtop.system.ProcessInfoProvider;
import org.jtoptool.jtop.system.ProcessKiller;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class ProcessService {

    private final ActionLogService actionLogService;
    private final ProcessInfoProvider systemInfoProvider;
    private final ProcessKiller processKiller;
    private static final Logger LOGGER = LoggerFactory.getLogger(ProcessService.class);

    public ProcessService(ActionLogService actionLogService, ProcessInfoProvider processInfoProvider,
                          ProcessKiller processKiller) {
        this.actionLogService = actionLogService;
        this.systemInfoProvider = processInfoProvider;
        this.processKiller = processKiller;
    }

    public List<Process> getProcesses() {
        return systemInfoProvider.getProcesses();
    }

    public boolean terminate(int pid, boolean force) {
        ActionType actionType = force ? ActionType.SIGKILL : ActionType.SIGTERM;
        Optional<Process> optProcess = systemInfoProvider.getProcessById(pid); 

        if (optProcess.isEmpty()) {
            LOGGER.warn("Failed to kill: pid {}; process was null", pid);
            actionLogService.logProcessNotFoundTerminationAttempt(pid, actionType);
            throw new ProcessNotFoundException();
        }

        Process process = optProcess.get();

        if (isCurrentProcess(pid)) {
            LOGGER.warn("Failed to kill: pid {}; current process", pid);
            actionLogService.logCurrentProcessTerminationAttempt(process, actionType);
            throw new TriedToKillCurrentProcessException();
        }

        if (isKillingPermitted(process)) {
            LOGGER.warn("Failed to kill: pid {}; owner was root", pid);
            actionLogService.logNotPermittedTerminationAttempt(process, actionType);
            throw new NoPermissionToKillProcessException();
        }

        if (processKiller.kill(pid, force)) {
            LOGGER.info("Killed: pid {}, command {}, owner {}", pid, process.command(), process.owner());
            actionLogService.logSuccessfulTermination(process, actionType);
            return true;
        }

        LOGGER.warn("Failed to kill: pid {}, command {}, owner {}", pid, process.command(), process.owner());
        actionLogService.logFailedTerminationAttempt(process, actionType);
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
