package com.elasticsearch.engine.base.config;

import com.elasticsearch.engine.base.common.utils.ClassPathResourceReaderUtils;
import com.elasticsearch.engine.base.model.constant.CommonConstant;
/**
 * @author wanghuan
 * @description: LoadFactory
 * 加载一些初始化的配置
 * @date 2022-04-09 16:15
 */
public class LoadFactory {

    public static String readBanner() {
        try {
            //获取文件的URL
            String banner = ClassPathResourceReaderUtils.getContent(CommonConstant.BANNER_PATH);
            String version = ClassPathResourceReaderUtils.getContent(CommonConstant.VERSION_PATH);
            banner = String.format(banner, version);
            return banner;
        } catch (Exception e) {
            return "";
        }
    }
}
