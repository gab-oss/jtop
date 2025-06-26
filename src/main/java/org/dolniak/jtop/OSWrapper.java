package org.dolniak.jtop;

import oshi.software.os.OSProcess;

import java.util.List;

public interface OSWrapper {

    List<OSProcess> getProcesses();
    OSProcess getProcess(int pid);
}
