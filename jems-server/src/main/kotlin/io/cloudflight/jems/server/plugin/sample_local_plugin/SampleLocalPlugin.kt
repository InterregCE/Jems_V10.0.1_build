package io.cloudflight.jems.server.plugin.sample_local_plugin

import io.cloudflight.jems.plugin.pre_condition_check.PreConditionCheckPlugin
import io.cloudflight.jems.plugin.pre_condition_check.models.MessageType
import io.cloudflight.jems.plugin.pre_condition_check.models.PreConditionCheckMessage
import io.cloudflight.jems.plugin.pre_condition_check.models.PreConditionCheckResult
import org.springframework.stereotype.Service

@Service
class SampleLocalPlugin : PreConditionCheckPlugin {

    override fun check(projectId: Long): PreConditionCheckResult =
        PreConditionCheckResult(
            listOf(
                PreConditionCheckMessage(
                    "local",
                    MessageType.INFO,
                    emptyList()
                )
            ),
            isSubmissionAllowed = true
        )

    override fun getDescription(): String =
        "sample local plugin description"

    override fun getKey() =
        "sample-local-plugin"

    override fun getName() =
        "sample local pre condition check plugin"

    override fun getVersion(): String =
        "V1.0"


}
