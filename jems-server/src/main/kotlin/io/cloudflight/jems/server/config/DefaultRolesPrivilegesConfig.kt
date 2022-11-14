package io.cloudflight.jems.server.config

import io.cloudflight.jems.server.project.repository.OptimizationProjectVersionRepository
import io.cloudflight.jems.server.user.repository.UserRolePersistenceProvider
import io.cloudflight.jems.server.user.service.model.UserRole
import io.cloudflight.jems.server.user.service.model.UserRolePermission
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.SmartInitializingSingleton
import org.springframework.context.annotation.Configuration
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.time.ZoneOffset

@Configuration
class DefaultRolesPrivilegesConfig(
    private val userRolePersistenceProvider: UserRolePersistenceProvider,
    private val optimizationProjectVersionRepository: OptimizationProjectVersionRepository,
): SmartInitializingSingleton{

    companion object {
        private val logger = LoggerFactory.getLogger(DefaultRolesPrivilegesConfig::class.java)
    }

    @Transactional
    override fun afterSingletonsInstantiated() {
        val startUpDate = LocalDateTime.now(ZoneOffset.UTC)
        val initialMigrationTimeStamp = optimizationProjectVersionRepository.getInitialMigrationTimeStamp()

        if (startUpDate.isBefore(initialMigrationTimeStamp.plusMinutes(15))) {
            userRolePersistenceProvider.findUserRoleByName("programme user").ifPresentOrElse(
                {
                     userRolePersistenceProvider.update(
                         UserRole(
                             id = it.id,
                             name = it.name,
                             permissions = UserRolePermission.getProgrammeUserRoleDefaultPermissions(),
                             isDefault = it.isDefault
                         )
                     )
                    logger.info("Successfully set 'programme user' role default permissions")
                },
                { logger.warn("Programme user role does not exist") }
            )
        }
    }
}
