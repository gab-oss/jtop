package org.dolniak.jtop;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.Optional;

@Component
public class ProcessService {

    private final SystemInfoProvider systemInfoProvider;
    private final ProcessKiller processKiller;
    private static final Logger LOGGER = LoggerFactory.getLogger(ProcessService.class);

    public ProcessService(SystemInfoProvider systemInfoProvider, ProcessKiller processKiller) {
        this.systemInfoProvider = systemInfoProvider;
        this.processKiller = processKiller;
    }

    public List<Process> getProcesses() {
        return systemInfoProvider.getProcesses();
    }

    public KillAttemptResult terminate(int pid) {
        // todo make info return an optional

        Process process = systemInfoProvider.getProcess(pid);
        if (process == null) {
            LOGGER.warn("Failed to kill: pid {}; process was null", pid);
            return KillAttemptResult.NOT_FOUND;
        }
        if (isKillingPermitted(process)) {
            LOGGER.warn("Failed to kill: pid {}; owner was root", pid);
            return KillAttemptResult.NOT_PERMITTED;
        }

        if (processKiller.kill(pid)) {
            LOGGER.info("Killed: pid {}, command {}, owner {}", pid, process.command(), process.owner());
            return KillAttemptResult.SUCCESS;
        }

        LOGGER.warn("Failed to kill: pid {}, command {}, owner {}", pid, process.command(), process.owner());
        return KillAttemptResult.FAILED;
    }

    private boolean isKillingPermitted(Process process) {
        return process.owner().contains("root");
    }

    public Optional<Process> getProcessById(int pid) {
        // todo actual impl
        if (pid == -1) {
            return Optional.of(new Process(-1, "process1", "user"));
        }
        return Optional.empty();
    }
}
