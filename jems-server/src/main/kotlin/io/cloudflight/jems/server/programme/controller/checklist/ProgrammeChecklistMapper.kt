package io.cloudflight.jems.server.programme.controller.checklist

import io.cloudflight.jems.api.programme.dto.checklist.ProgrammeChecklistComponentDTO
import io.cloudflight.jems.api.programme.dto.checklist.ProgrammeChecklistComponentTypeDTO
import io.cloudflight.jems.api.programme.dto.checklist.ProgrammeChecklistDTO
import io.cloudflight.jems.api.programme.dto.checklist.ProgrammeChecklistDetailDTO
import io.cloudflight.jems.api.programme.dto.checklist.metadata.HeadlineMetadataDTO
import io.cloudflight.jems.api.programme.dto.checklist.metadata.OptionsToggleMetadataDTO
import io.cloudflight.jems.server.programme.service.checklist.model.ProgrammeChecklist
import io.cloudflight.jems.server.programme.service.checklist.model.ProgrammeChecklistComponent
import io.cloudflight.jems.server.programme.service.checklist.model.ProgrammeChecklistComponentType
import io.cloudflight.jems.server.programme.service.checklist.model.ProgrammeChecklistDetail
import io.cloudflight.jems.server.programme.service.checklist.model.metadata.HeadlineMetadata
import io.cloudflight.jems.server.programme.service.checklist.model.metadata.OptionsToggleMetadata
import org.mapstruct.AfterMapping
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.MappingTarget
import org.mapstruct.factory.Mappers

private val mapper = Mappers.getMapper(ProgrammeChecklistMapper::class.java)

fun Iterable<ProgrammeChecklist>.toDto() = map { mapper.map(it) }
fun ProgrammeChecklistDTO.toModel() = mapper.map(this)
fun ProgrammeChecklist.toDto() = mapper.map(this)
fun ProgrammeChecklistDetail.toDetailDto() = mapper.mapToDetail(this)
fun ProgrammeChecklistDetailDTO.toDetailModel() = mapper.mapToModel(this)

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
        dto.metadata = when (model.type) {
            ProgrammeChecklistComponentType.HEADLINE -> (model.metadata as HeadlineMetadata).toDto()
            ProgrammeChecklistComponentType.OPTIONS_TOGGLE -> (model.metadata as OptionsToggleMetadata).toDto()
        }
    }

    @Mapping(target = "metadata", ignore = true)
    abstract fun map(dto: ProgrammeChecklistComponentDTO): ProgrammeChecklistComponent

    @AfterMapping
    fun fromDtoToModel(
        @MappingTarget model: ProgrammeChecklistComponent, dto: ProgrammeChecklistComponentDTO
    ) {
        model.metadata = when (dto.type) {
            ProgrammeChecklistComponentTypeDTO.HEADLINE -> (dto.metadata as HeadlineMetadataDTO).toModel()
            ProgrammeChecklistComponentTypeDTO.OPTIONS_TOGGLE -> (dto.metadata as OptionsToggleMetadataDTO).toModel()
        }
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
            thirdOption
        )

    private fun OptionsToggleMetadataDTO.toModel(): OptionsToggleMetadata =
        OptionsToggleMetadata(
            question,
            firstOption,
            secondOption,
            thirdOption
        )

}
