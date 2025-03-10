package io.cloudflight.jems.server.common.controller

import io.cloudflight.jems.api.common.InfoApi
import io.cloudflight.jems.api.common.dto.VersionDTO
import io.cloudflight.platform.spring.server.ServerModuleIdentification
import org.springframework.boot.actuate.info.InfoEndpoint
import org.springframework.web.bind.annotation.RestController
import java.time.ZoneId
import java.time.ZonedDateTime

@RestController
class InfoController(
    private val serverModuleIdentification: ServerModuleIdentification,
    private val infoEndpoint: InfoEndpoint
) : InfoApi {

    override fun getVersionInfo(): VersionDTO = VersionDTO(
        version = serverModuleIdentification.getVersion(),
        commitId = serverModuleIdentification.getId(),
        commitIdShort = serverModuleIdentification.getIdShort(),
        commitTime = serverModuleIdentification.getTime()?.let { ZonedDateTime.ofInstant(it, ZoneId.systemDefault()) },
        helpdeskUrl = infoEndpoint.info()["helpdesk-url"]?.toString() ?: "",
        helpdeskEmail = infoEndpoint.info()["helpdesk-email"]?.toString() ?: "",
        accessibilityStatementUrl = infoEndpoint.info()["accessibility-statement-url"]?.toString() ?: "",
        termsAndPrivacyPolicyUrl = infoEndpoint.info()["terms-privacy-policy-url"]?.toString() ?: "",
    )

}
