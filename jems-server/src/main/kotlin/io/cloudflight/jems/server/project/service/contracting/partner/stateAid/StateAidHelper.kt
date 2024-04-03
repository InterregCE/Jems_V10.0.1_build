package io.cloudflight.jems.server.project.service.contracting.partner.stateAid

import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerStateAid

fun stateAidSectionShouldBeDisplayed(partnerStateAid: ProjectPartnerStateAid): Boolean =
    partnerStateAid.stateAidScheme != null
