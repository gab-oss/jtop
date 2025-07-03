package org.dolniak.jtop;

import oshi.software.os.OSProcess;

import java.util.List;

public interface SystemInfoProvider {

    List<OSProcess> getProcesses();
    OSProcess getProcess(int pid);
}
