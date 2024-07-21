package net.kdigital.ec21.util;

import org.springframework.stereotype.Component;

@Component
public class StringProcessor {
    public String maskIpAddress(String ipAddress) {
        if (ipAddress == null || ipAddress.isEmpty()) {
            return ipAddress;
        }

        int lastDotIndex = ipAddress.lastIndexOf(".");
        if (lastDotIndex == -1) {
            return ipAddress;
        }

        String maskedIpAddress = ipAddress.substring(0, lastDotIndex + 1) + "***";
        return maskedIpAddress;
    }
}
