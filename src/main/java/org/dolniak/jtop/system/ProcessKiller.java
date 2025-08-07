package org.dolniak.jtop.system;

public interface ProcessKiller {

    boolean kill(int pid, boolean force);
}
