package io.cloudflight.jems.server.plugin.controller

import io.cloudflight.jems.api.plugin.PluginApi
import io.cloudflight.jems.api.plugin.dto.MessageTypeDTO
import io.cloudflight.jems.api.plugin.dto.PluginInfoDTO
import io.cloudflight.jems.api.plugin.dto.PreConditionCheckMessageDTO
import io.cloudflight.jems.api.plugin.dto.PreConditionCheckResultDTO
import io.cloudflight.jems.plugin.contract.JemsPlugin
import io.cloudflight.jems.plugin.contract.pre_condition_check.PreConditionCheckPlugin
import io.cloudflight.jems.server.plugin.PluginRegistry
import org.springframework.web.bind.annotation.RestController

@RestController
class PluginController(private val pluginRegistry: PluginRegistry) : PluginApi {

    override fun getAvailablePluginList(): List<PluginInfoDTO> =
        pluginRegistry.list<JemsPlugin>()
            .map { PluginInfoDTO(it.getKey(), it.getName(), it.getVersion(), it.getDescription()) }

    //todo this is for demo only (should be removed with next release for sprint 25)
    override fun execute(projectId: Long): PreConditionCheckResultDTO =
        pluginRegistry.get<PreConditionCheckPlugin>("standard-pre-condition-check-plugin").check(projectId).run {
            PreConditionCheckResultDTO(
                this.messages.map {
                    PreConditionCheckMessageDTO(
                        it.messageKey, MessageTypeDTO.valueOf(it.messageType.name), emptyList()
                    )
                },
                this.isSubmissionAllowed
            )
        }


}
