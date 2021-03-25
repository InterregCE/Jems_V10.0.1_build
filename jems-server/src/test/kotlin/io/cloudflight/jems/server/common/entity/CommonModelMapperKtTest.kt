package io.cloudflight.jems.server.common.entity

import io.cloudflight.jems.api.programme.dto.language.SystemLanguage.DE
import io.cloudflight.jems.api.programme.dto.language.SystemLanguage.EN
import io.cloudflight.jems.api.programme.dto.language.SystemLanguage.FR
import io.cloudflight.jems.api.project.dto.InputTranslation
import io.cloudflight.jems.server.UnitTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test


class CommonModelMapperKtTest : UnitTest() {

    @Test
    fun `should filter null or empty translations when mapping entities to models`() {
        val translatedValue = mutableSetOf(
            SampleTranslationEntity(TranslationId(this, FR), ""),
            SampleTranslationEntity(TranslationId(this, DE), null),
            SampleTranslationEntity(TranslationId(this, EN), "not empty"),
        )

        assertThat(translatedValue.extractField { it.text }).containsExactly(InputTranslation(EN, "not empty"))

    }

    @Test
    fun `should filter null or empty translations when mapping models to entities`() {
        val text = setOf(InputTranslation(EN, ""), InputTranslation(DE, "a"))
        val title = setOf(InputTranslation(EN, ""), InputTranslation(DE, "ABC"))

        val translatedValue = mutableSetOf<SampleTranslationEntity>()
        translatedValue.addTranslationEntities(
            { language ->
                SampleTranslationEntity(
                    translationId = TranslationId(object {}, language),
                    text = text.extractTranslation(language),
                    title = title.extractTranslation(language)
                )
            }, arrayOf(text, title)
        )
        assertThat(translatedValue.size).isEqualTo(1)
    }
}

internal class SampleTranslationEntity(
    override val translationId: TranslationId<Any>,
    val text: String? = null,
    val title: String? = null
) : TranslationEntity()
