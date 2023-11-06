package io.cloudflight.jems.server.project.repository.auditAndControl.correction.identification

import io.cloudflight.jems.server.project.entity.auditAndControl.ProjectAuditControlCorrectionEntity
import io.cloudflight.jems.server.project.entity.auditAndControl.ProjectCorrectionIdentificationEntity
import io.cloudflight.jems.server.project.repository.auditAndControl.correction.toModel
import io.cloudflight.jems.server.project.service.auditAndControl.correction.model.ProjectCorrectionIdentification
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.Named
import org.mapstruct.factory.Mappers


private val mapper = Mappers.getMapper(AuditControlCorrectionIdentificationModelMapper::class.java)

fun ProjectCorrectionIdentificationEntity.toModel() = mapper.map(this)

fun ProjectCorrectionIdentification.toEntity() = mapper.map(this)

@Mapper
interface AuditControlCorrectionIdentificationModelMapper {

    @Named("toCorrectionModel")
    fun toCorrectionModel(entity: ProjectAuditControlCorrectionEntity) = entity.toModel()

    @Mapping(source = "correctionEntity", target = "correction", qualifiedByName = ["toCorrectionModel"])
    fun map(entity: ProjectCorrectionIdentificationEntity): ProjectCorrectionIdentification
    fun map(model: ProjectCorrectionIdentification): ProjectCorrectionIdentificationEntity

}


