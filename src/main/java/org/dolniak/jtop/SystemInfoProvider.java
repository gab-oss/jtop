package org.dolniak.jtop;

import java.util.List;

public interface SystemInfoProvider {

    List<Process> getProcesses();

    Process getProcessById(int pid);

    int getCurrentProcessId();
}
