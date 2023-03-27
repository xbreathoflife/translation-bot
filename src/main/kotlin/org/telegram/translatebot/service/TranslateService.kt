package org.telegram.translatebot.service

import me.bush.translator.Translator
import me.bush.translator.languageOf

class TranslateService {

    fun translate(text: String, toLanguage: String, fromLanguage: String): String? {
        val translator = Translator()
        val target = languageOf(toLanguage)
        val source = languageOf(fromLanguage)
        if (target == null || source == null) {
            return null
        }
        return translator.translateBlocking(text, target, source).translatedText
    }

}