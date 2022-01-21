package io.cloudflight.jems.server.plugin.pre_submission_check

import io.cloudflight.jems.plugin.contract.models.common.I18nMessageData
import io.cloudflight.jems.plugin.contract.pre_condition_check.models.MessageType
import io.cloudflight.jems.plugin.contract.pre_condition_check.models.PreConditionCheckMessage
import io.cloudflight.jems.plugin.contract.pre_condition_check.models.PreConditionCheckResult
import io.cloudflight.jems.server.UnitTest
import org.assertj.core.api.AssertionsForClassTypes.assertThat
import org.junit.Test


internal class PreSubmissionCheckOffTest : UnitTest(){
    private val preSubmissionCheckOff = PreSubmissionCheckOff()

    @Test
    fun `should always pass the pre submission check for any project`(){
        assertThat(preSubmissionCheckOff.check(1L)).isEqualTo(
            PreConditionCheckResult(listOf(
                PreConditionCheckMessage(I18nMessageData("jems.pre.condition.check.off.info"), MessageType.INFO, emptyList())
            ) , true)
        )
    }
}
