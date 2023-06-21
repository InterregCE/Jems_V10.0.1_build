package io.cloudflight.jems.server.project.service.contracting.partner.stateAid

import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerStateAid

fun stateAidSectionShouldBeDisplayed(partnerStateAid: ProjectPartnerStateAid): Boolean {
    if (partnerStateAid.stateAidScheme == null) {
        return false
    } else if ((partnerStateAid.answer1 == true && partnerStateAid.answer2 == true && partnerStateAid.answer3 == true && partnerStateAid.answer4 == true) ||
        ((partnerStateAid.answer1 == false || partnerStateAid.answer2 == false || partnerStateAid.answer3 == false) && partnerStateAid.answer4 == true)) {
        return true
    }
    return false
}
