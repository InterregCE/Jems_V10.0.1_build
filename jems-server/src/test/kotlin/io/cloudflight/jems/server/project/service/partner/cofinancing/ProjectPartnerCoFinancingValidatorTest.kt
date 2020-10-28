package io.cloudflight.jems.server.project.service.partner.cofinancing

import io.cloudflight.jems.api.project.dto.partner.cofinancing.InputProjectPartnerCoFinancing
import io.cloudflight.jems.api.project.dto.partner.cofinancing.ProjectPartnerCoFinancingValidator
import io.mockk.MockKAnnotations
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import javax.validation.ConstraintValidatorContext

internal class ProjectPartnerCoFinancingValidatorTest {

    @RelaxedMockK
    lateinit var validatorContext: ConstraintValidatorContext

    lateinit var projectPartnerCoFinancingValidator: ProjectPartnerCoFinancingValidator

    @BeforeEach
    fun setup() {
        MockKAnnotations.init(this)
        projectPartnerCoFinancingValidator = ProjectPartnerCoFinancingValidatorImpl()
    }

    @Test
    fun `test percentage null`() {
        testWrongPercentage(percentages = listOf(null), errorMsg = "project.partner.coFinancing.percentage.invalid")
    }

    @Test
    fun `test negative percentage`() {
        testWrongPercentage(percentages = listOf(-1), errorMsg = "project.partner.coFinancing.percentage.invalid")
    }

    @Test
    fun `test percentage over 100`() {
        testWrongPercentage(percentages = listOf(101), errorMsg = "project.partner.coFinancing.percentage.invalid")
    }

    @Test
    fun `test percentage over 100 and combination`() {
        testWrongPercentage(percentages = listOf(101, 50), errorMsg = "project.partner.coFinancing.percentage.invalid")
    }

    @Test
    fun `test percentage wrong sum`() {
        testWrongPercentage(percentages = listOf(45, 56), errorMsg = "project.partner.coFinancing.sum.invalid")
    }

    private fun testWrongPercentage(percentages: List<Int?>, errorMsg: String) {
        val testCoFinancing = percentages.mapTo(HashSet()) { InputProjectPartnerCoFinancing(percentage = it) }

        assertThat(projectPartnerCoFinancingValidator.isCoFinancingFilledInCorrectly(testCoFinancing, validatorContext)).isFalse()

        verify {
            validatorContext.buildConstraintViolationWithTemplate(errorMsg)
        }
    }

    @Test
    fun `test wrong amount of fundIds - 2 nulls`() {
        val testCoFinancing = setOf(
            InputProjectPartnerCoFinancing(percentage = 20, fundId = null),
            InputProjectPartnerCoFinancing(percentage = 80, fundId = null)
        )

        assertThat(projectPartnerCoFinancingValidator.isCoFinancingFilledInCorrectly(testCoFinancing, validatorContext)).isFalse()
        verify {
            validatorContext.buildConstraintViolationWithTemplate("project.partner.coFinancing.one.and.only.partner.contribution")
        }
    }

    @Test
    fun `test wrong amount of fundIds - no any null`() {
        val testCoFinancing = setOf(
            InputProjectPartnerCoFinancing(percentage = 20, fundId = 10),
            InputProjectPartnerCoFinancing(percentage = 80, fundId = 11)
        )

        assertThat(projectPartnerCoFinancingValidator.isCoFinancingFilledInCorrectly(testCoFinancing, validatorContext)).isFalse()
        verify {
            validatorContext.buildConstraintViolationWithTemplate("project.partner.coFinancing.one.and.only.partner.contribution")
        }
    }

    @Test
    fun `test duplicate fundIds - no any null`() {
        val testCoFinancing = setOf(
            InputProjectPartnerCoFinancing(percentage = 20, fundId = null),
            InputProjectPartnerCoFinancing(percentage = 30, fundId = 555),
            InputProjectPartnerCoFinancing(percentage = 50, fundId = 555)
        )

        assertThat(projectPartnerCoFinancingValidator.isCoFinancingFilledInCorrectly(testCoFinancing, validatorContext)).isFalse()
        verify {
            validatorContext.buildConstraintViolationWithTemplate("project.partner.coFinancing.fund.not.unique")
        }
    }

}
