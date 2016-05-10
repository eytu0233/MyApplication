package edu.ncku.application.util;

import java.util.Locale;

/**
 * Created by NCKU on 2016/5/3.
 * 工具靜態類別，用來取得一些環境參數
 */
public class EnvChecker {

    /**
     * 判斷是否為(簡繁)中文環境
     * @return
     */
    public static boolean isLunarSetting() {
        String language = getLanguageEnv();

        if (language != null
                && (language.trim().equals("zh-CN") || language.trim().equals("zh-TW")))
            return true;
        else
            return false;
    }

    /**
     * 取得語言環境參數
     * @return
     */
    private static String getLanguageEnv() {
        Locale l = Locale.getDefault();
        String language = l.getLanguage();
        String country = l.getCountry().toLowerCase();

        if ("zh".equals(language)) {
            if ("cn".equals(country)) {
                language = "zh-CN";
            } else if ("tw".equals(country)) {
                language = "zh-TW";
            }
        } else if ("pt".equals(language)) {
            if ("br".equals(country)) {
                language = "pt-BR";
            } else if ("pt".equals(country)) {
                language = "pt-PT";
            }
        }

        return language;
    }

}
