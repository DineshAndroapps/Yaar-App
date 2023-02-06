package com.yaar.shortvideoapp;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.view.ContextThemeWrapper;

import com.yaar.shortvideoapp.SimpleClasses.Variables;

import java.util.Locale;

public class LanguageUtils {

    public static boolean isLanguageSelected(Context context) {
        return getSelectedLanguage(context) != null;
    }

    public static Locale init(Context context) {
        String selectedLangCode = getSelectedLanguageCode(context);
        if (selectedLangCode != null) {
            Locale locale = new Locale(selectedLangCode);
            Locale.setDefault(locale);
            Configuration config = context.getApplicationContext().getResources().getConfiguration();
            config.locale = locale;
            context.getResources().updateConfiguration(config, context.getResources().getDisplayMetrics());
            return locale;
        }

        return null;
    }

    public static String getSelectedLanguage(Context context) {

        if (Variables.sharedPreferences == null) {
            Variables.sharedPreferences = context.getSharedPreferences(Variables.pref_name, 0);
        }

        return Variables.sharedPreferences.getString(Variables.language, null);
    }

    public static String getSelectedLanguageCode(Context context) {

        if (Variables.sharedPreferences == null) {
            Variables.sharedPreferences = context.getSharedPreferences(Variables.pref_name, 0);
        }

        return Variables.sharedPreferences.getString(Variables.languageCode, "en");
    }

    public static void selectLanguage(Context context, String language, String language_code) {

        if (Variables.sharedPreferences == null) {
            Variables.sharedPreferences = context.getSharedPreferences(Variables.pref_name, 0);
        }

        SharedPreferences.Editor editor = Variables.sharedPreferences.edit();
        editor.putString(Variables.language, language);
        editor.putString(Variables.languageCode, language_code);
        editor.apply();

        Locale locale = new Locale(language_code);
        Locale.setDefault(locale);
        Configuration config = context.getResources().getConfiguration();
        config.locale = locale;
        context.getResources().updateConfiguration(config, context.getResources().getDisplayMetrics());

    }


    //

    private static Locale sLocale;

    public static void setLocale(Context context) {
        Locale locale = new Locale(getSelectedLanguageCode(context));
        sLocale = locale;
        if(sLocale != null) {
            Locale.setDefault(sLocale);
        }
    }

    public static void updateConfig(ContextThemeWrapper wrapper) {
        if(sLocale != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            Configuration configuration = new Configuration();
            configuration.setLocale(sLocale);
            wrapper.applyOverrideConfiguration(configuration);
        }
    }

    public static void updateConfig(Application app, Configuration configuration) {
        if (sLocale != null && Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
            //Wrapping the configuration to avoid Activity endless loop
            Configuration config = new Configuration(configuration);
            // We must use the now-deprecated config.locale and res.updateConfiguration here,
            // because the replacements aren't available till API level 24 and 17 respectively.
            config.locale = sLocale;
            Resources res = app.getBaseContext().getResources();
            res.updateConfiguration(config, res.getDisplayMetrics());
        }
    }


}
