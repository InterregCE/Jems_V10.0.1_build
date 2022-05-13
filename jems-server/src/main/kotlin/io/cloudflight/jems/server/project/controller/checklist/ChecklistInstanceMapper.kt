package io.cloudflight.jems.server.project.controller.checklist

import io.cloudflight.jems.api.programme.dto.checklist.ProgrammeChecklistComponentTypeDTO
import io.cloudflight.jems.api.project.dto.checklist.ChecklistComponentInstanceDTO
import io.cloudflight.jems.api.project.dto.checklist.ChecklistInstanceDTO
import io.cloudflight.jems.api.project.dto.checklist.ChecklistInstanceDetailDTO
import io.cloudflight.jems.api.project.dto.checklist.CreateChecklistInstanceDTO
import io.cloudflight.jems.api.project.dto.checklist.metadata.HeadlineInstanceMetadataDTO
import io.cloudflight.jems.api.project.dto.checklist.metadata.OptionsToggleInstanceMetadataDTO
import io.cloudflight.jems.api.project.dto.checklist.metadata.TextInputInstanceMetadataDTO
import io.cloudflight.jems.server.programme.controller.checklist.toDtoMetadata
import io.cloudflight.jems.server.programme.controller.checklist.toModelMetadata
import io.cloudflight.jems.server.programme.service.checklist.model.ChecklistComponentInstance
import io.cloudflight.jems.server.programme.service.checklist.model.ChecklistInstance
import io.cloudflight.jems.server.project.service.checklist.model.ChecklistInstanceDetail
import io.cloudflight.jems.server.programme.service.checklist.model.ProgrammeChecklistComponentType
import io.cloudflight.jems.server.programme.service.checklist.model.metadata.HeadlineInstanceMetadata
import io.cloudflight.jems.server.programme.service.checklist.model.metadata.OptionsToggleInstanceMetadata
import io.cloudflight.jems.server.project.service.checklist.model.CreateChecklistInstanceModel
import io.cloudflight.jems.server.project.service.checklist.model.metadata.TextInputInstanceMetadata
import org.mapstruct.AfterMapping
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.MappingTarget
import org.mapstruct.factory.Mappers

private val mapper = Mappers.getMapper(ChecklistInstanceMapper::class.java)

fun List<ChecklistInstance>.toDto() = map { mapper.map(it) }
fun ChecklistInstanceDTO.toModel() = mapper.map(this)
fun ChecklistInstance.toDto() = mapper.map(this)
fun ChecklistInstanceDetail.toDetailDto() = mapper.mapToDetail(this)
fun ChecklistInstanceDetailDTO.toDetailModel() = mapper.mapToModel(this)
fun CreateChecklistInstanceModel.toDto() = mapper.mapToCreateDto(this)
fun CreateChecklistInstanceDTO.toModel() = mapper.mapToCreateModel(this)

@Mapper(uses = [ChecklistInstanceComponentMapper::class])
interface ChecklistInstanceMapper {
    fun map(model: ChecklistInstance): ChecklistInstanceDTO
    fun map(dto: ChecklistInstanceDTO): ChecklistInstance
    fun mapToDetail(model: ChecklistInstanceDetail): ChecklistInstanceDetailDTO
    fun mapToModel(checklistDetailDTO: ChecklistInstanceDetailDTO): ChecklistInstanceDetail
    fun mapToCreateModel(createChecklistInstanceDTO: CreateChecklistInstanceDTO): CreateChecklistInstanceModel
    fun mapToCreateDto(createChecklistInstanceModel: CreateChecklistInstanceModel): CreateChecklistInstanceDTO
}

@Mapper
abstract class ChecklistInstanceComponentMapper {

    @Mapping(target = "programmeMetadata", ignore = true)
    @Mapping(target = "instanceMetadata", ignore = true)
    abstract fun map(model: ChecklistComponentInstance): ChecklistComponentInstanceDTO

    @AfterMapping
    fun fromModelToDto(@MappingTarget dto: ChecklistComponentInstanceDTO, model: ChecklistComponentInstance) {
        dto.programmeMetadata = model.programmeMetadata?.toDtoMetadata(model.type)
        dto.instanceMetadata = when (model.type) {
            ProgrammeChecklistComponentType.HEADLINE -> HeadlineInstanceMetadataDTO()
            ProgrammeChecklistComponentType.OPTIONS_TOGGLE -> (model.instanceMetadata as? OptionsToggleInstanceMetadata)?.toDto()
            ProgrammeChecklistComponentType.TEXT_INPUT -> (model.instanceMetadata as? TextInputInstanceMetadata)?.toDto()
        }
    }

    @Mapping(target = "programmeMetadata", ignore = true)
    @Mapping(target = "instanceMetadata", ignore = true)
    abstract fun map(dto: ChecklistComponentInstanceDTO): ChecklistComponentInstance

    @AfterMapping
    fun fromDtoToModel(@MappingTarget model: ChecklistComponentInstance, dto: ChecklistComponentInstanceDTO
    ) {
        model.programmeMetadata = dto.programmeMetadata?.toModelMetadata(dto.type)
        model.instanceMetadata = when (dto.type) {
            ProgrammeChecklistComponentTypeDTO.HEADLINE -> HeadlineInstanceMetadata()
            ProgrammeChecklistComponentTypeDTO.OPTIONS_TOGGLE -> (dto.instanceMetadata as OptionsToggleInstanceMetadataDTO).toModel()
            ProgrammeChecklistComponentTypeDTO.TEXT_INPUT -> (dto.instanceMetadata as TextInputInstanceMetadataDTO).toModel()
        }
    }

    private fun OptionsToggleInstanceMetadata.toDto(): OptionsToggleInstanceMetadataDTO =
        OptionsToggleInstanceMetadataDTO(answer, justification)

    private fun OptionsToggleInstanceMetadataDTO.toModel(): OptionsToggleInstanceMetadata =
        OptionsToggleInstanceMetadata(answer, justification)

    private fun TextInputInstanceMetadata.toDto(): TextInputInstanceMetadataDTO =
        TextInputInstanceMetadataDTO(explanation)

    private fun TextInputInstanceMetadataDTO.toModel(): TextInputInstanceMetadata =
        TextInputInstanceMetadata(explanation)

}
