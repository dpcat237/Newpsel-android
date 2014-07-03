package com.dpcat237.nps.helper;

import android.speech.tts.TextToSpeech;
import java.util.Locale;

/**
 * Created by denys on 5/3/14.
 */
public class LanguageHelper {

    //Get first available Locale for TTS by language code
    public static Locale getLocaleFromLanguageTTS(String articleLanguage, TextToSpeech mTTS) {
        Locale[] locales = Locale.getAvailableLocales();
        for (Locale locale : locales) {
            if (locale.getLanguage().equals(articleLanguage) && !PreferencesHelper.isNumeric(locale.getCountry()) && mTTS.isLanguageAvailable(locale) == TextToSpeech.LANG_AVAILABLE) {
                return locale;
            }
        }

        return null;
    }
}
