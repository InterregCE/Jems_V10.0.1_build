package io.cloudflight.jems.server.programme.controller.checklist

import io.cloudflight.jems.api.programme.dto.checklist.ProgrammeChecklistComponentDTO
import io.cloudflight.jems.api.programme.dto.checklist.ProgrammeChecklistComponentTypeDTO
import io.cloudflight.jems.api.programme.dto.checklist.ProgrammeChecklistDTO
import io.cloudflight.jems.api.programme.dto.checklist.ProgrammeChecklistDetailDTO
import io.cloudflight.jems.api.programme.dto.checklist.ProgrammeChecklistTypeDTO
import io.cloudflight.jems.api.programme.dto.checklist.metadata.HeadlineMetadataDTO
import io.cloudflight.jems.api.programme.dto.checklist.metadata.OptionsToggleMetadataDTO
import io.cloudflight.jems.api.programme.dto.checklist.metadata.ProgrammeChecklistMetadataDTO
import io.cloudflight.jems.api.programme.dto.checklist.metadata.TextInputMetadataDTO
import io.cloudflight.jems.server.programme.service.checklist.model.ProgrammeChecklist
import io.cloudflight.jems.server.programme.service.checklist.model.ProgrammeChecklistComponent
import io.cloudflight.jems.server.programme.service.checklist.model.ProgrammeChecklistComponentType
import io.cloudflight.jems.server.programme.service.checklist.model.ProgrammeChecklistDetail
import io.cloudflight.jems.server.programme.service.checklist.model.ProgrammeChecklistType
import io.cloudflight.jems.server.programme.service.checklist.model.metadata.HeadlineMetadata
import io.cloudflight.jems.server.programme.service.checklist.model.metadata.OptionsToggleMetadata
import io.cloudflight.jems.server.programme.service.checklist.model.metadata.ProgrammeChecklistMetadata
import io.cloudflight.jems.server.programme.service.checklist.model.metadata.TextInputMetadata
import org.mapstruct.AfterMapping
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.MappingTarget
import org.mapstruct.factory.Mappers

private val mapper = Mappers.getMapper(ProgrammeChecklistMapper::class.java)
private val typeMapper = Mappers.getMapper(ProgrammeChecklistTypeMapper::class.java)

fun Iterable<ProgrammeChecklist>.toDto() = map { mapper.map(it) }
fun ProgrammeChecklistDTO.toModel() = mapper.map(this)
fun ProgrammeChecklist.toDto() = mapper.map(this)
fun ProgrammeChecklistDetail.toDetailDto() = mapper.mapToDetail(this)
fun ProgrammeChecklistDetailDTO.toDetailModel() = mapper.mapToModel(this)
fun ProgrammeChecklistType.toDto() = typeMapper.map(this)
fun ProgrammeChecklistTypeDTO.toModel() = typeMapper.map(this)

@Mapper(uses = [ProgrammeChecklistComponentMapper::class])
interface ProgrammeChecklistMapper {
    fun map(model: ProgrammeChecklist): ProgrammeChecklistDTO
    fun map(dto: ProgrammeChecklistDTO): ProgrammeChecklist
    fun mapToDetail(model: ProgrammeChecklistDetail): ProgrammeChecklistDetailDTO
    fun mapToModel(checklistDetailDTO: ProgrammeChecklistDetailDTO): ProgrammeChecklistDetail
}

@Mapper
abstract class ProgrammeChecklistComponentMapper {

    @Mapping(target = "metadata", ignore = true)
    abstract fun map(model: ProgrammeChecklistComponent): ProgrammeChecklistComponentDTO

    @AfterMapping
    fun fromModelToDto(@MappingTarget dto: ProgrammeChecklistComponentDTO, model: ProgrammeChecklistComponent) {
        dto.metadata = model.metadata?.toDtoMetadata(model.type)
    }

    @Mapping(target = "metadata", ignore = true)
    abstract fun map(dto: ProgrammeChecklistComponentDTO): ProgrammeChecklistComponent

    @AfterMapping
    fun fromDtoToModel(
        @MappingTarget model: ProgrammeChecklistComponent, dto: ProgrammeChecklistComponentDTO
    ) {
        model.metadata = dto.metadata?.toModelMetadata(dto.type)
    }

}

@Mapper
interface ProgrammeChecklistTypeMapper {
    fun map(model: ProgrammeChecklistType): ProgrammeChecklistTypeDTO
    fun map(dto: ProgrammeChecklistTypeDTO): ProgrammeChecklistType
}

fun ProgrammeChecklistMetadata.toDtoMetadata(type: ProgrammeChecklistComponentType): ProgrammeChecklistMetadataDTO =
    when (type) {
        ProgrammeChecklistComponentType.HEADLINE -> (this as HeadlineMetadata).toDto()
        ProgrammeChecklistComponentType.OPTIONS_TOGGLE -> (this as OptionsToggleMetadata).toDto()
        ProgrammeChecklistComponentType.TEXT_INPUT -> (this as TextInputMetadata).toDto()
    }

fun ProgrammeChecklistMetadataDTO.toModelMetadata(type: ProgrammeChecklistComponentTypeDTO): ProgrammeChecklistMetadata =
    when (type) {
        ProgrammeChecklistComponentTypeDTO.HEADLINE -> (this as HeadlineMetadataDTO).toModel()
        ProgrammeChecklistComponentTypeDTO.OPTIONS_TOGGLE -> (this as OptionsToggleMetadataDTO).toModel()
        ProgrammeChecklistComponentTypeDTO.TEXT_INPUT -> (this as TextInputMetadataDTO).toModel()
    }

private fun HeadlineMetadata.toDto(): HeadlineMetadataDTO =
    HeadlineMetadataDTO(
        value = value
    )

private fun HeadlineMetadataDTO.toModel(): HeadlineMetadata =
    HeadlineMetadata(
        value = value
    )

private fun OptionsToggleMetadata.toDto(): OptionsToggleMetadataDTO =
    OptionsToggleMetadataDTO(
        question,
        firstOption,
        secondOption,
        thirdOption,
        justification
    )

private fun OptionsToggleMetadataDTO.toModel(): OptionsToggleMetadata =
    OptionsToggleMetadata(
        question,
        firstOption,
        secondOption,
        thirdOption,
        justification
    )

private fun TextInputMetadata.toDto(): TextInputMetadataDTO =
        TextInputMetadataDTO(
            question,
            explanationLabel,
            explanationMaxLength
        )

private fun TextInputMetadataDTO.toModel(): TextInputMetadata =
        TextInputMetadata(
            question,
            explanationLabel,
            explanationMaxLength
        )
