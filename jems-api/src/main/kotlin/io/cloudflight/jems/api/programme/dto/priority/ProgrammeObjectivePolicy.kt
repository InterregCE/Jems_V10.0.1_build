package io.cloudflight.jems.api.programme.dto.priority

import io.cloudflight.jems.api.programme.dto.priority.ProgrammeObjective.ISO1
import io.cloudflight.jems.api.programme.dto.priority.ProgrammeObjective.ISO12
import io.cloudflight.jems.api.programme.dto.priority.ProgrammeObjective.ISO2
import io.cloudflight.jems.api.programme.dto.priority.ProgrammeObjective.PO1
import io.cloudflight.jems.api.programme.dto.priority.ProgrammeObjective.PO2
import io.cloudflight.jems.api.programme.dto.priority.ProgrammeObjective.PO3
import io.cloudflight.jems.api.programme.dto.priority.ProgrammeObjective.PO4
import io.cloudflight.jems.api.programme.dto.priority.ProgrammeObjective.PO5

enum class ProgrammeObjectivePolicy(val objective: ProgrammeObjective) {

    AdvancedTechnologies(PO1),
    Digitisation(PO1),
    Growth(PO1),
    IndustrialTransition(PO1),
    DigitalConnectivity(PO1),

    EnergyEfficiency(PO2),
    RenewableEnergy(PO2),
    SmartEnergy(PO2),
    ClimateChange(PO2),
    WaterManagement(PO2),
    CircularEconomy(PO2),
    GreenInfrastructure(PO2),
    ZeroCarbonEconomy(PO2),

    InterModalTenT(PO3),
    CrossBorderMobility(PO3),

    SocialInfrastructure(PO4),
    QualityInEducation(PO4),
    DisadvantagedGroups(PO4),
    IntegratedActionsForMigrants(PO4),
    Healthcare(PO4),
    CultureAndTourism(PO4),
    PeacePlus(PO4),
    JobSeekers(PO4),
    LabourMarketMatching(PO4),
    GenderBalance(PO4),
    HealthyAgeing(PO4),
    DualTrainingSystems(PO4),
    EqualAccess(PO4),
    LifelongLearning(PO4),
    EqualOpportunities(PO4),
    IntegrationOfThirdCountryNationals(PO4),
    IntegrationOfMarginalised(PO4),
    AffordableServices(PO4),
    SocialIntegration(PO4),
    MaterialAssistance(PO4),

    EnvDevelopment(PO5),
    LocalEnvDevelopment(PO5),

    ISO1PublicAuthorities(ISO1),
    ISO1AdministrativeCooperation(ISO1),
    ISO1MutualTrust(ISO1),
    ISO1MacroRegion(ISO1),
    ISO1Democracy(ISO1),
    ISO1Other(ISO1),

    ISO2BorderCrossing(ISO2),
    ISO2MobilityMigration(ISO2),
    ISO2InternationalProtection(ISO2),
    ISO2Other(ISO2),

    ISO12PublicAuthorities(ISO12),
    ISO12PromotingCooperation(ISO12),
    ISO12MutualTrust(ISO12),
    ISO12MacroRegion(ISO12),
    ISO12Democracy(ISO12),
    ISO12BorderCrossing(ISO12),
    ISO12MobilityMigration(ISO12),
    ISO12InternationalProtection(ISO12),
    ISO12Other(ISO12),

}
