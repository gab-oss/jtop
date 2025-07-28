package org.dolniak.jtop;

import java.util.List;
import java.util.Optional;

public interface SystemInfoProvider {

    List<Process> getProcesses();

    Optional<Process> getProcessById(int pid);

    int getCurrentProcessId();
}
