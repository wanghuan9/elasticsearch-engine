package com.elasticsearch.engine.elasticsearchengine.common.utils;

import org.springframework.core.io.ClassPathResource;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

/**
 * @author wanghuan
 * @description: 读取classpath下的文件支持jar
 * @date 2022-05-30 18:41
 */
public class ClassPathResourceReaderUtils {
    
    public static String getContent(String path) {
        String content;
            try {
                ClassPathResource resource = new ClassPathResource(path);
                BufferedReader reader = new BufferedReader(new InputStreamReader(resource.getInputStream()));
                content = reader.lines().collect(Collectors.joining("\n"));
                reader.close();
            } catch (IOException ex) {

                throw new RuntimeException(ex);
            }
        return content;

    }
}
