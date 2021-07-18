package eu.deltacraft.deltacraftteams.utils

import net.kyori.adventure.text.TextComponent
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
import net.md_5.bungee.api.ChatColor
import java.util.regex.Matcher
import java.util.regex.Pattern

class TranslateTextCodes {
    companion object {
        private val HEX_PATTERN: Pattern = Pattern.compile("&#(\\w{5}[0-9a-f])")

        fun translate(textToTranslate: String): TextComponent {
            val matcher: Matcher = HEX_PATTERN.matcher(textToTranslate)
            val buffer = StringBuffer()
            while (matcher.find()) {
                matcher.appendReplacement(buffer, ChatColor.of("#" + matcher.group(1)).toString())
            }
            return LegacyComponentSerializer.legacy('&').deserialize(matcher.appendTail(buffer).toString())
        }
    }
}