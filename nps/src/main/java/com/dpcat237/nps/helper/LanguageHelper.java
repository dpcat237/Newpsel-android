package com.dpcat237.nps.helper;

import android.content.Context;
import android.content.Intent;
import android.speech.tts.TextToSpeech;

import java.util.Locale;

public class LanguageHelper {

    //Get first available Locale for TTS by language code
    public static Locale getLocaleFromLanguageTTS(String articleLanguage, TextToSpeech mTTS) {
        Locale[] locales = Locale.getAvailableLocales();
        for (Locale locale : locales) {
            if (locale.getLanguage().equals(articleLanguage) && !NumbersHelper.isNumeric(locale.getCountry()) && mTTS.isLanguageAvailable(locale) == TextToSpeech.LANG_AVAILABLE) {
                return locale;
            }
        }

        return null;
    }

    public static Boolean isLanguageAvailable(Context context, TextToSpeech tts, Locale localeTTs) {
        Boolean available = false;
        switch (tts.isLanguageAvailable(localeTTs)) {
            case TextToSpeech.LANG_AVAILABLE:
            case TextToSpeech.LANG_COUNTRY_AVAILABLE:
            case TextToSpeech.LANG_COUNTRY_VAR_AVAILABLE:
                //Log.d(TAG, "SUPPORTED");
                available = true;
                break;
            case TextToSpeech.LANG_MISSING_DATA:
                //Log.d(TAG, "MISSING_DATA");
                //Log.d(TAG, "require data...");
                Intent installIntent = new Intent();
                installIntent.setAction(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
                context.startActivity(installIntent);
                available = true;
                break;
            case TextToSpeech.LANG_NOT_SUPPORTED:
                //Log.d(TAG, "NOT SUPPORTED");
                available = false;
                break;
        }

        return available;
    }
}
