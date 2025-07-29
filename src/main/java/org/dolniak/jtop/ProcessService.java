package org.dolniak.jtop;

import org.dolniak.jtop.exceptions.FailedToKillProcessException;
import org.dolniak.jtop.exceptions.NoPermissionToKillProcessException;
import org.dolniak.jtop.exceptions.ProcessNotFoundException;
import org.dolniak.jtop.exceptions.TriedToKillCurrentProcessException;
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

    public boolean terminate(int pid) {
        if (isCurrentProcess(pid)) {
            LOGGER.warn("Failed to kill: pid {}; current process", pid);
            throw new TriedToKillCurrentProcessException();
        }

        Optional<Process> optProcess = systemInfoProvider.getProcessById(pid);
        if (optProcess.isEmpty()) {
            LOGGER.warn("Failed to kill: pid {}; process was null", pid);
            throw new ProcessNotFoundException();
        }

        Process process = optProcess.get();
        if (isKillingPermitted(process)) {
            LOGGER.warn("Failed to kill: pid {}; owner was root", pid);
            throw new NoPermissionToKillProcessException();
        }

        if (processKiller.kill(pid)) {
            LOGGER.info("Killed: pid {}, command {}, owner {}", pid, process.command(), process.owner());
            return true;
        }

        LOGGER.warn("Failed to kill: pid {}, command {}, owner {}", pid, process.command(), process.owner());
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
