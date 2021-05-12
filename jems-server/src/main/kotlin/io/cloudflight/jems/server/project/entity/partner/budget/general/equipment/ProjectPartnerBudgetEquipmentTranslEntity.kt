package io.cloudflight.jems.server.project.entity.partner.budget.general.equipment

import io.cloudflight.jems.server.common.entity.TranslationId
import io.cloudflight.jems.server.project.entity.partner.budget.general.ProjectPartnerBudgetGeneralTranslBase
import javax.persistence.EmbeddedId
import javax.persistence.Entity

@Entity(name = "project_partner_budget_equipment_transl")
class ProjectPartnerBudgetEquipmentTranslEntity(

    @EmbeddedId
    override val translationId: TranslationId<ProjectPartnerBudgetEquipmentEntity>,

    override val awardProcedures: String? = null,

    override val unitType: String? = null,

    override val description: String? = null

) : ProjectPartnerBudgetGeneralTranslBase()
