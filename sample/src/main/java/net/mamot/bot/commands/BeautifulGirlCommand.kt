package net.mamot.bot.commands

import com.pengrad.telegrambot.TelegramBot
import com.pengrad.telegrambot.commands.MessageCommand
import com.pengrad.telegrambot.model.Chat
import com.pengrad.telegrambot.model.User
import com.pengrad.telegrambot.request.SendMessage.message

/**
 * Created by k1per on 19.03.2017.
 */
class BeautifulGirlCommand : MessageCommand(commandName, "BeautifulGirl") {

    override fun execute(bot: TelegramBot?, user: User?, chat: Chat?, params: String?) {
        bot?.execute(message(chat, randomImageUrl))
    }

    companion object {
        var commandName = "/makemehappy"
        var randomImageUrl = "http://loremflickr.com/1280/1024/girl, sexy"
    }
}