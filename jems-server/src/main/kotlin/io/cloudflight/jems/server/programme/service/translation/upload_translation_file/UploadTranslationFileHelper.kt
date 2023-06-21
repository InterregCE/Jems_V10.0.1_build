package io.cloudflight.jems.server.programme.service.translation.upload_translation_file

import java.nio.file.Files
import java.nio.file.Path

fun copyTranslationFiles(folder: String, content: ByteArray, fileName: String) =
    with(Path.of(folder)) {
        if (!Files.exists(this))
            Files.createDirectories(this)

        Path.of(this.toString(), fileName).toFile().writeBytes(content)
    }
