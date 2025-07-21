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

        // todo expand logging
        LOGGER.warn("Attempt to kill {}", pid);
        Process process = systemInfoProvider.getProcess(pid);
        if (process == null) return KillAttemptResult.NOT_FOUND;
        if (process.owner().contains("root")) return KillAttemptResult.NOT_PERMITTED;

        if (processKiller.kill(pid)) return KillAttemptResult.SUCCESS;
        else return KillAttemptResult.FAILED;
    }

    public Optional<Process> getProcessById(int pid) {
        // todo actual impl
        if (pid == -1) {
            return Optional.of(new Process(-1, "process1", "user"));
        }
        return Optional.empty();
    }
}
