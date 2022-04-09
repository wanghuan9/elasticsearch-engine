package com.elasticsearch.engine.elasticsearchengine.config;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author wanghuan
 * @description: LoadFactory
 * 加载一些初始化的配置
 * @date 2022-04-09 16:15
 */
public class LoadFactory {

    private static final URL BANNER_LOC = Thread.currentThread().getContextClassLoader().getResource("engine-banner.txt");
    private static final URL PROP_LOC = Thread.currentThread().getContextClassLoader().getResource("elasticseach-engine.properties");

    public static String readBanner() {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(new File(BANNER_LOC.toURI())));
            String banner = reader.lines().collect(Collectors.joining("\n"));
            banner = String.format(banner, getProperty("elasticsearch-engine.version"));
            return banner;
        } catch (Exception e) {
            return "";
        }
    }

    private static String getProperty(String fullKey) throws URISyntaxException, FileNotFoundException {
        BufferedReader reader = new BufferedReader(new FileReader(new File(PROP_LOC.toURI())));
        Optional<String> targetLine = reader.lines().filter(l -> l.startsWith(fullKey)).findAny();
        if (targetLine.isPresent()) {
            String[] split = targetLine.get().split("=");
            return split[1];
        }
        return "";
    }
}
