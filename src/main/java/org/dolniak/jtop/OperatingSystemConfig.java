package org.dolniak.jtop;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import oshi.software.os.OperatingSystem;
import oshi.software.os.linux.LinuxOperatingSystem;

@Configuration
public class OperatingSystemConfig {

    @Bean
    public OperatingSystem operatingSystem() {
        return new LinuxOperatingSystem();
    }
}
