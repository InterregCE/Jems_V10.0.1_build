package io.cloudflight.jems.server.call.service.translation

import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Path
import java.util.stream.Collectors
import java.util.stream.Stream

val PROPERTIES_CHARSET: Charset = StandardCharsets.UTF_8
const val LINE_ENDING = "\n"

private val VALIDATE_LINE = "^[\\.\\-0-9A-Za-z\\_]+=.+\$".toRegex()

fun callIdPrefix(callId: Long) = "call-id-$callId"

fun InputStream.addCallIdPrefixes(callId: Long) = Stream
    // concat empty string is fix for not-reading last empty line
    .concat(BufferedReader(InputStreamReader(this, PROPERTIES_CHARSET)).lines(), Stream.of(""))
    .filter { line -> VALIDATE_LINE.matches(line) || line.ignorable() /* leave empty lines un-touched for readability */ }
    .map { line -> if (line.ignorable()) line else "${callIdPrefix(callId)}.$line" }
    .collect(Collectors.toList()).toList()

fun String.ignorable() = isEmpty() || startsWith('#')

fun List<String>.withoutEmptyLines() = filter { it.isNotBlank() && !it.startsWith('#') }

fun List<String>.toByteArray() = joinToString(LINE_ENDING).toByteArray(PROPERTIES_CHARSET)

fun List<String>.ifContentOrNull() = let {
        if (it.all { it.isBlank() })
            return@let null
        else
            return@let it
    }?.toByteArray()


fun Pair<String, InputStream>.removeCallIdPrefixes(callId: Long) =
    Pair(first, second.removeCallIdPrefixes(callId).toByteArray(PROPERTIES_CHARSET))

fun InputStream.removeCallIdPrefixes(callId: Long): String = BufferedReader(InputStreamReader(this, PROPERTIES_CHARSET))
    .lines()
    .map { it.removePrefix("${callIdPrefix(callId)}.") }
    .collect(Collectors.joining(LINE_ENDING, "", LINE_ENDING))

fun updateCallTranslations(callId: Long, callSpecificTranslationFilePath: Path, updatedContent: ByteArray) {
    val previous = Files.lines(callSpecificTranslationFilePath, PROPERTIES_CHARSET)
        .filter { !it.startsWith("${callIdPrefix(callId)}.") }
        .collect(Collectors.joining(LINE_ENDING, "", LINE_ENDING)).toByteArray(PROPERTIES_CHARSET)
    val output = callSpecificTranslationFilePath.toFile()
    output.writeBytes(previous)
    output.appendBytes(updatedContent)
}
