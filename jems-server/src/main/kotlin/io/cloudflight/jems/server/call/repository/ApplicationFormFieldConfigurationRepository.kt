package io.cloudflight.jems.server.call.repository

import io.cloudflight.jems.server.call.entity.ApplicationFormFieldConfigurationEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface ApplicationFormFieldConfigurationRepository : JpaRepository<ApplicationFormFieldConfigurationEntity, String> {
    @Query("SELECT e FROM #{#entityName} e  where e.id.applicationFormConfigurationEntity.id=:applicationFormConfigurationId")
    fun findAllByApplicationFormConfigurationId(applicationFormConfigurationId: Long?): MutableSet<ApplicationFormFieldConfigurationEntity>
}
