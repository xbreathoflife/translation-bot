package org.telegram.translatebot

import com.github.kotlintelegrambot.bot
import com.github.kotlintelegrambot.dispatch
import com.github.kotlintelegrambot.dispatcher.callbackQuery
import com.github.kotlintelegrambot.dispatcher.command
import com.github.kotlintelegrambot.entities.ChatId
import com.github.kotlintelegrambot.entities.InlineKeyboardMarkup
import com.github.kotlintelegrambot.entities.keyboard.InlineKeyboardButton
import me.bush.translator.Language
import org.telegram.translatebot.config.PropertiesSetup
import org.telegram.translatebot.model.Settings
import org.telegram.translatebot.service.TranslateService

fun main() {
    val translateService = TranslateService()
    val settings = Settings()
    val botId: String = PropertiesSetup().getProp("bot_id")
    val bot = bot {
        token = botId
        println("Bot started")
        dispatch {
            command("to_language") {
                val keyboardMarkup = InlineKeyboardMarkup.create(
                    generateUsersButton("to")
                )
                bot.sendMessage(
                    chatId = ChatId.fromId(message.chat.id),
                    text = "Translate TO language:",
                    replyMarkup = keyboardMarkup
                )
            }

            supportedLanguages().map {
                callbackQuery("to_${it.name}") {
                    val chatId = callbackQuery.message?.chat?.id ?: return@callbackQuery
                    val language = callbackQuery.data.split("_")[1]
                    settings.toLanguage = language
                    bot.sendMessage(ChatId.fromId(chatId), "Language is set to: $language")
                }
            }

            supportedLanguages().map {
                callbackQuery("from_${it.name}") {
                    val chatId = callbackQuery.message?.chat?.id ?: return@callbackQuery
                    val language = callbackQuery.data.split("_")[1]
                    settings.fromLanguage = language
                    bot.sendMessage(ChatId.fromId(chatId), "Language is set to: $language")
                }
            }

            command("from_language") {
                val keyboardMarkup = InlineKeyboardMarkup.create(
                    generateUsersButton("from")
                )
                bot.sendMessage(
                    chatId = ChatId.fromId(message.chat.id),
                    text = "Translate FROM language:",
                    replyMarkup = keyboardMarkup
                )
            }
            command("translate") {
                val text = args.joinToString(" ")
                if (settings.toLanguage == null) {
                    bot.sendMessage(
                        chatId = ChatId.fromId(message.chat.id),
                        text = "Target language is not specified",
                    )
                } else if (settings.fromLanguage == null) {
                    bot.sendMessage(
                        chatId = ChatId.fromId(message.chat.id),
                        text = "Source language is not specified",
                    )
                } else {
                    val translation = translateService.translate(text, settings.toLanguage!!, settings.fromLanguage!!)
                        ?: "Translation failed"
                    bot.sendMessage(
                        chatId = ChatId.fromId(message.chat.id),
                        text = translation,
                    )
                }
            }
        }
    }
    bot.startPolling()
}

fun supportedLanguages() = listOf(
    Language.GERMAN,
    Language.ENGLISH,
    Language.RUSSIAN,
    Language.SPANISH
)

fun generateUsersButton(prefix: String): List<List<InlineKeyboardButton.CallbackData>> {
    return listOf(
        supportedLanguages().map {
            InlineKeyboardButton.CallbackData(
                text = it.name,
                callbackData = "${prefix}_${it.name}"
            )
        }
    )
}