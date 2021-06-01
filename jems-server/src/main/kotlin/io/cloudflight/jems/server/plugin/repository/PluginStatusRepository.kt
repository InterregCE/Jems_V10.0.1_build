package io.cloudflight.jems.server.plugin.repository

import io.cloudflight.jems.server.plugin.entity.PluginStatusEntity
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface PluginStatusRepository : CrudRepository<PluginStatusEntity, String>


