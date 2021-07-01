package io.cloudflight.jems.server.programme.controller.translation

import io.cloudflight.jems.api.programme.dto.translation.TranslationFileMetaDataDTO
import io.cloudflight.jems.api.programme.dto.translation.TranslationFileTypeDTO
import io.cloudflight.jems.server.programme.service.translation.model.TranslationFileMetaData
import io.cloudflight.jems.server.programme.service.translation.model.TranslationFileType
import org.mapstruct.Mapper
import org.mapstruct.factory.Mappers

private val translationDTOMapper = Mappers.getMapper(TranslationDTOMapper::class.java)

fun TranslationFileMetaData.toDTO() =
    translationDTOMapper.map(this)

fun List<TranslationFileMetaData>.toDTO() =
    map { translationDTOMapper.map(it) }

fun TranslationFileTypeDTO.toModel() =
    translationDTOMapper.map(this)

@Mapper
abstract class TranslationDTOMapper {
    abstract fun map(translationFileTypeDTO: TranslationFileTypeDTO): TranslationFileType
    abstract fun map(translationFileType: TranslationFileType): TranslationFileTypeDTO
    abstract fun map(translationFileMetaData: TranslationFileMetaData): TranslationFileMetaDataDTO
}
