package io.cloudflight.jems.server.project.controller.partner

import io.cloudflight.jems.api.project.dto.ProjectPartnerStateAidDTO
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerStateAid

fun ProjectPartnerStateAidDTO.toModel() = ProjectPartnerStateAid(
    answer1 = answer1,
    justification1 = justification1,
    answer2 = answer2,
    justification2 = justification2,
    answer3 = answer3,
    justification3 = justification3,
    answer4 = answer4,
    justification4 = justification4,
)

fun ProjectPartnerStateAid.toDto() = ProjectPartnerStateAidDTO(
    answer1 = answer1,
    justification1 = justification1,
    answer2 = answer2,
    justification2 = justification2,
    answer3 = answer3,
    justification3 = justification3,
    answer4 = answer4,
    justification4 = justification4,
)
