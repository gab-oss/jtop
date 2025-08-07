package org.dolniak.jtop;

import org.dolniak.jtop.exceptions.SystemInfoLoadingException;
import oshi.SystemInfo;

public class SystemInfoProvider {

    public long getTotalMemory() {
        try {
            return new SystemInfo().getHardware().getMemory().getTotal();
        } catch (Exception e) {
            throw new SystemInfoLoadingException();
        }
    }

    public SystemInfoProvider() {}
}
