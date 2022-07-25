package io.cloudflight.jems.server.controllerInstitution.repository

import io.cloudflight.jems.server.controllerInstitution.entity.ControllerInstitutionPartnerEntity
import io.cloudflight.jems.server.controllerInstitution.service.model.InstitutionPartnerAssignment
import org.mapstruct.Mapper
import org.mapstruct.factory.Mappers


fun List<InstitutionPartnerAssignment>.toEntities() = map { it.toEntity() }

fun List<ControllerInstitutionPartnerEntity>.toModels() = map { it.toModel() }

fun InstitutionPartnerAssignment.toEntity() = institutionPartnerMapper.map(this)

fun ControllerInstitutionPartnerEntity.toModel() = institutionPartnerMapper.map(this)


private val institutionPartnerMapper = Mappers.getMapper(ControllerInstitutionPartnerMapper::class.java)

@Mapper
interface ControllerInstitutionPartnerMapper {

     fun map(model: InstitutionPartnerAssignment): ControllerInstitutionPartnerEntity

     fun map(entity: ControllerInstitutionPartnerEntity): InstitutionPartnerAssignment
}
