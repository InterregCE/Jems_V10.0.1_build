package io.cloudflight.jems.server.project.entity.partner.state_aid

import io.cloudflight.jems.server.common.entity.TranslationView

interface PartnerStateAidRow: TranslationView {
    val partnerId: Long

    val answer1: Boolean?
    val justification1: String?
    val answer2: Boolean?
    val justification2: String?
    val answer3: Boolean?
    val justification3: String?
    val answer4: Boolean?
    val justification4: String?
}
