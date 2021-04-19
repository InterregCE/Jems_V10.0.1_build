package org.example.plugin.pre_condition_check

import io.cloudflight.jems.plugin.pre_condition_check.PreConditionCheckPlugin
import io.cloudflight.jems.plugin.pre_condition_check.models.MessageType
import io.cloudflight.jems.plugin.pre_condition_check.models.PreConditionCheckMessage
import io.cloudflight.jems.plugin.pre_condition_check.models.PreConditionCheckResult
import org.springframework.stereotype.Service


@Service
open class PreConditionCheckSample : PreConditionCheckPlugin {

    override fun check(projectId: Long): PreConditionCheckResult =
        PreConditionCheckResult(
            listOf(
                PreConditionCheckMessage(
                    "external",
                    MessageType.ERROR,
                    emptyList()
                )
            ),
            isSubmissionAllowed = true
        )

    override fun getDescription(): String =
        "sample pre condition check plugin!"

    override fun getKey() =
        "sample-external-plugin"

    override fun getName() =
        "sample pre condition check plugin"

    override fun getVersion(): String =
        "V1.0"
}
