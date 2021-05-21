package io.cloudflight.jems.server.plugin.services.update_plugin_status

import io.cloudflight.jems.server.plugin.entity.PluginStatusEntity
import io.cloudflight.jems.server.plugin.repository.PluginStatusRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional


//TODO should be improved in MP2-1510 - missing tests, exception handling, access control, persistence and etc
@Service
class UpdatePluginStatus(private val repository: PluginStatusRepository) :
    UpdatePluginStatusInteractor {

    @Transactional
    override fun enable(pluginKey: String) {
        repository.findById(pluginKey).ifPresentOrElse(
            { it.enabled = true },
            { repository.save(PluginStatusEntity(pluginKey, true)) }
        )
    }

    @Transactional
    override fun disable(pluginKey: String) {
        repository.findById(pluginKey).ifPresentOrElse(
            { it.enabled = false },
            { repository.save(PluginStatusEntity(pluginKey, false)) }
        )
    }

}
