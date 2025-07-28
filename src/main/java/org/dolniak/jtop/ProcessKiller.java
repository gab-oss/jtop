package org.dolniak.jtop;

public interface ProcessKiller {

    boolean kill(int pid);

    boolean forceKill(int pid);

}
