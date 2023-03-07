package io.cloudflight.jems.server.project.entity.report.project.resultPrinciple

import io.cloudflight.jems.server.common.entity.TranslationEntity
import io.cloudflight.jems.server.common.entity.TranslationId
import javax.persistence.Column
import javax.persistence.EmbeddedId
import javax.persistence.Entity

@Entity(name = "report_project_horizontal_principle_transl")
class ProjectReportHorizontalPrincipleTranslEntity(

    @EmbeddedId
    override val translationId: TranslationId<ProjectReportHorizontalPrincipleEntity>,

    @Column
    var sustainableDevelopmentDescription: String?,

    @Column
    var equalOpportunitiesDescription: String?,

    @Column
    var sexualEqualityDescription: String?
): TranslationEntity()
