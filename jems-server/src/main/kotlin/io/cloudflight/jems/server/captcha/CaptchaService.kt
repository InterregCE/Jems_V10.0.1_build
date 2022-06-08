package io.cloudflight.jems.server.captcha

import nl.captcha.Captcha
import nl.captcha.backgrounds.FlatColorBackgroundProducer
import nl.captcha.text.producer.DefaultTextProducer
import nl.captcha.text.renderer.DefaultWordRenderer
import org.springframework.stereotype.Service
import java.awt.Color
import java.io.ByteArrayOutputStream
import java.util.*
import javax.imageio.ImageIO

@Service
class CaptchaService {

    fun createCaptcha(width: Int, height: Int): Captcha {
        return Captcha.Builder(width, height)
            .addBackground(FlatColorBackgroundProducer(Color.WHITE))
            .addText(DefaultTextProducer(), DefaultWordRenderer())
            .addNoise()
            .build()
    }

    fun encodeCaptcha(captcha: Captcha): String {
        try {
            val bos = ByteArrayOutputStream()
            ImageIO.write(captcha.image, "jpg", bos)
            val byteArray: ByteArray = Base64.getEncoder().encode(bos.toByteArray())
            return String(byteArray)
        } catch (e: Exception) {
            throw CaptchaNotEncodedException()
        }
    }
}
