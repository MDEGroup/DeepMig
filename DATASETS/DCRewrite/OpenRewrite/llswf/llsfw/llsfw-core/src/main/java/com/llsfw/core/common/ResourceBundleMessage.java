package com.llsfw.core.common;

import java.util.Locale;
import java.util.ResourceBundle;

import org.springframework.context.support.ResourceBundleMessageSource;

public class ResourceBundleMessage extends ResourceBundleMessageSource {
    /**
     * 获得属性对象
     * 
     * @param basename
     * @param locale
     * @return
     */
    public ResourceBundle getBundle(String basename, Locale locale) {
        return this.getResourceBundle(basename, locale);
    }
}
