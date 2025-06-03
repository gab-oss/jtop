package org.dolniak.jtop;

import org.springframework.stereotype.Component;
import oshi.software.os.OSProcess;
import oshi.software.os.OperatingSystem;
import oshi.software.os.linux.LinuxOperatingSystem;

import java.util.List;
import java.util.stream.Stream;

@Component
public class ProcessService {

    private final OperatingSystem os;

    public ProcessService() {
        this.os = new LinuxOperatingSystem();
    }

    public Process getCurrentProcess() {
        // OperatingSystem os = new LinuxOperatingSystem();
        OSProcess osProcess = os.getCurrentProcess();

        if (osProcess == null) {
            throw new IllegalStateException("Current process could not be retrieved.");
        }

        return new Process(osProcess.getProcessID(), osProcess.getName());
    }

    public List<Process> getProcesses() {
        List<OSProcess> osProcesses = os.getProcesses();
        return osProcesses.stream().map(x -> new Process(x.getProcessID(), x.getName())).toList();
    }

}
