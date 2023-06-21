package io.cloudflight.jems.server.project.entity.report.project.identification

import io.cloudflight.jems.server.common.entity.TranslationEntity
import io.cloudflight.jems.server.common.entity.TranslationId
import io.cloudflight.jems.server.project.entity.report.project.ProjectReportEntity
import javax.persistence.EmbeddedId
import javax.persistence.Entity

@Entity(name = "report_project_identification_transl")
class ProjectReportIdentificationTranslEntity(

    @EmbeddedId
    override val translationId: TranslationId<ProjectReportEntity>,
    var highlights: String?,
    var partnerProblems: String?,
    var deviations: String?,
) : TranslationEntity()
