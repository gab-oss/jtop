package org.jtoptool.jtop.system;

import org.jtoptool.jtop.Process;
import org.springframework.stereotype.Component;
import oshi.software.os.OSProcess;
import oshi.software.os.OperatingSystem;

import java.util.List;
import java.util.Optional;

@Component
public class BasicProcessInfoProvider implements ProcessInfoProvider {

    private final OperatingSystem os;

    public BasicProcessInfoProvider(OperatingSystem os) {
        this.os = os;
    }

    @Override
    public List<Process> getProcesses() {
        List<OSProcess> osProcesses = os.getProcesses();
        return osProcesses.stream().map(this::convertOsProcess).toList();
    }

    @Override
    public Optional<Process> getProcessById(int pid) {
        return Optional.ofNullable(convertOsProcess(os.getProcess(pid)));
    }

    @Override
    public int getCurrentProcessId() {
        return Math.toIntExact(ProcessHandle.current().pid());
    }

    private Process convertOsProcess(OSProcess osProcess) {
        if (osProcess == null) return null;
        return new Process(osProcess.getProcessID(), osProcess.getName(), osProcess.getUser(),
                osProcess.getState().name(), osProcess.getResidentSetSize(), osProcess.getProcessCpuLoadCumulative());
    }
}
