package org.dolniak.jtop;

import org.springframework.stereotype.Component;
import oshi.software.os.OSProcess;
import oshi.software.os.OperatingSystem;

import java.util.List;

@Component
public class BasicOSWrapper implements OSWrapper {

    private final OperatingSystem os;

    public BasicOSWrapper(OperatingSystem os) {
        this.os = os;
    }

    @Override
    public List<OSProcess> getProcesses() {
        return os.getProcesses();
    }

    @Override
    public OSProcess getProcess(int pid) {
        return os.getProcess(pid);
    }
}
