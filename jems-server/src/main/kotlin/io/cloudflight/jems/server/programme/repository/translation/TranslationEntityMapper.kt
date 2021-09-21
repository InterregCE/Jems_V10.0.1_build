package io.cloudflight.jems.server.programme.repository.translation

import io.cloudflight.jems.server.programme.entity.translation.TranslationFileEntity
import io.cloudflight.jems.server.programme.service.translation.model.TranslationFileMetaData
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.Mappings
import org.mapstruct.factory.Mappers


private val translationEntityMapper = Mappers.getMapper(TranslationEntityMapper::class.java)

fun Iterable<TranslationFileEntity>.toModel() =
    map{ translationEntityMapper.map(it)}

fun TranslationFileEntity.toModel() =
     translationEntityMapper.map(this)

@Mapper
abstract class TranslationEntityMapper {

    @Mappings(
        Mapping(source = "id.language", target = "language"),
        Mapping(source = "id.fileType", target = "fileType")
    )
    abstract fun map(translationFileEntity: TranslationFileEntity): TranslationFileMetaData
}
