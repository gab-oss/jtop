package org.jtoptool.jtop.system;

import org.jtoptool.jtop.exceptions.SystemInfoLoadingException;
import oshi.SystemInfo;

public class BasicSystemInfoProvider implements SystemInfoProvider {

    @Override
    public long getTotalMemory() {
        try {
            return new SystemInfo().getHardware().getMemory().getTotal();
        } catch (Exception e) {
            throw new SystemInfoLoadingException();
        }
    }

    public BasicSystemInfoProvider() {}
}
