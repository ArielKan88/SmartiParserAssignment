package com.kanevsky.utils;

import org.springframework.core.io.Resource;
import org.springframework.util.DigestUtils;

import java.io.IOException;
import java.io.InputStream;

public class MD5Utils {
    public static String calculateMD5(Resource resource) throws IOException {
        try (InputStream inputStream = resource.getInputStream()) {
            // Use DigestUtils from Spring to calculate the SHA-256 hash
            return DigestUtils.md5DigestAsHex(inputStream);
        }
    }
}
