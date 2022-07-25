package io.cloudflight.jems.server.controllerInstitution.controller

import io.cloudflight.jems.api.controllerInstitutions.dto.ControllerInstitutionAssignmentDTO
import io.cloudflight.jems.api.controllerInstitutions.dto.InstitutionPartnerAssignmentDTO
import io.cloudflight.jems.api.controllerInstitutions.dto.InstitutionPartnerDetailsDTO
import io.cloudflight.jems.server.controllerInstitution.service.model.ControllerInstitutionAssignment
import io.cloudflight.jems.server.controllerInstitution.service.model.InstitutionPartnerAssignment
import io.cloudflight.jems.server.controllerInstitution.service.model.InstitutionPartnerDetails
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.factory.Mappers
import org.springframework.data.domain.Page


private val mapper = Mappers.getMapper(InstitutionPartnerDTOMapper::class.java)

fun Page<InstitutionPartnerDetails>.toPageDto() = map { it.toDto() }


fun ControllerInstitutionAssignmentDTO.toModel() = mapper.map(this)

fun List<InstitutionPartnerAssignmentDTO>.toModels() = map { it.toModel() }

fun List<InstitutionPartnerAssignment>.toDTOs() = map { it.toDto() }

fun InstitutionPartnerAssignment.toDto() = mapper.map(this)

fun InstitutionPartnerAssignmentDTO.toModel() = mapper.map(this)

fun InstitutionPartnerDetails.toDto() =  mapper.map(this)

@Mapper
interface InstitutionPartnerDTOMapper {

    fun map(dto: ControllerInstitutionAssignmentDTO): ControllerInstitutionAssignment
    fun map(dto: ControllerInstitutionAssignment): ControllerInstitutionAssignmentDTO
    @Mapping(target = "partnerProjectId", ignore = true)
    fun map(dto: InstitutionPartnerAssignmentDTO): InstitutionPartnerAssignment

    fun map(model: InstitutionPartnerAssignment): InstitutionPartnerAssignmentDTO

    fun map(model: InstitutionPartnerDetails): InstitutionPartnerDetailsDTO
}
