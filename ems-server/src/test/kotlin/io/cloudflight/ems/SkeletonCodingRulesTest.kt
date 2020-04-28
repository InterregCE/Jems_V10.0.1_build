package io.cloudflight.ems

import com.tngtech.archunit.junit.AnalyzeClasses
import com.tngtech.archunit.junit.ArchRules
import com.tngtech.archunit.junit.ArchTest
import io.cloudflight.platform.test.archunit.CloudflightArchitectureRules
import io.cloudflight.platform.test.archunit.CloudflightCodingRules

@AnalyzeClasses(packages = ["io.cloudflight.ems"])
class SkeletonCodingRulesTest {

    @ArchTest
    private val CODING_RULES = ArchRules.`in`(CloudflightCodingRules::class.java)

    @ArchTest
    private val ARCH_RULES = ArchRules.`in`(CloudflightArchitectureRules::class.java)

}
