package org.dolniak.jtop;

import oshi.software.os.OSProcess;

import java.util.List;

public interface SystemInfoProvider {

    List<Process> getProcesses();
    Process getProcess(int pid);

}
