package net.mamot.bot.commands

import com.pengrad.telegrambot.TelegramBot
import com.pengrad.telegrambot.commands.MessageCommand
import com.pengrad.telegrambot.model.Chat
import com.pengrad.telegrambot.model.User
import com.pengrad.telegrambot.request.SendPhoto
import org.apache.commons.io.IOUtils
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.InputStream
import java.net.URL
import javax.imageio.ImageIO


/**
 * Created by k1per on 19.03.2017.
 */
class BeautifulGirlCommand : MessageCommand(commandName, "BeautifulGirl") {

    override fun execute(bot: TelegramBot?, user: User?, chat: Chat?, params: String?) {
        val imageURL = URL(randomImageUrl)
        val originalImage = ImageIO.read(imageURL)
        val baos = ByteArrayOutputStream()
        ImageIO.write(originalImage, "jpg", baos)

        val request = SendPhoto(chat?.id(), baos.toByteArray()).caption("caption")
        bot?.execute(request)
    }


    companion object {
        var commandName = "/makemehappy"
        var randomImageUrl = "http://loremflickr.com/1280/1024/girl, sexy"
    }

    @Throws(Exception::class)
    private fun fetchRemoteFile(location: String): ByteArray? {
        val url = URL(location)
        var `is`: InputStream? = null
        var bytes: ByteArray? = null
        try {
            `is` = url.openStream()
            bytes = IOUtils.toByteArray(`is`)
        } catch (e: IOException) {
            //handle errors
        } finally {
            if (`is` != null) `is`.close()
        }
        return bytes
    }
}