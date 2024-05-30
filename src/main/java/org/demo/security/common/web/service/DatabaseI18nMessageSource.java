package org.demo.security.common.web.service;

import java.util.Locale;

/**
 * 从数据库获取i18n翻译
 */
public interface DatabaseI18nMessageSource {

  /**
   * 获取翻译结果
   * @param i18nKey key
   * @param args 动态参数
   * @param language 语言
   */
  String getMessage(String i18nKey, Object[] args, Locale language);
}
