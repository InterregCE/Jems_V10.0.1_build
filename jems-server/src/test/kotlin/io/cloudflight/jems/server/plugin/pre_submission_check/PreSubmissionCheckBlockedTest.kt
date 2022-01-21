package io.cloudflight.jems.server.plugin.pre_submission_check

import io.cloudflight.jems.plugin.contract.models.common.I18nMessageData
import io.cloudflight.jems.plugin.contract.pre_condition_check.models.MessageType
import io.cloudflight.jems.plugin.contract.pre_condition_check.models.PreConditionCheckMessage
import io.cloudflight.jems.plugin.contract.pre_condition_check.models.PreConditionCheckResult
import io.cloudflight.jems.server.UnitTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test


internal class PreSubmissionCheckBlockedTest : UnitTest() {

    private val preSubmissionCheckBlocked = PreSubmissionCheckBlocked()

    @Test
    fun `should always block the pre submission check for any project`(){
        assertThat(preSubmissionCheckBlocked.check(1L)).isEqualTo(
            PreConditionCheckResult(listOf(
                PreConditionCheckMessage(I18nMessageData("jems.pre.condition.check.blocked.warning"), MessageType.WARNING, emptyList())
            ) , false)
        )
    }
}
