package io.cloudflight.jems.server.project.repository.partner

import io.cloudflight.jems.api.programme.dto.language.SystemLanguage
import io.cloudflight.jems.server.project.entity.partner.state_aid.PartnerStateAidRow

data class PartnerStateAidRowTest(
    override val language: SystemLanguage?,
    override val partnerId: Long,
    override val answer1: Boolean? = null,
    override val justification1: String? = null,
    override val answer2: Boolean? = null,
    override val justification2: String? = null,
    override val answer3: Boolean? = null,
    override val justification3: String? = null,
    override val answer4: Boolean? = null,
    override val justification4: String? = null,
): PartnerStateAidRow
