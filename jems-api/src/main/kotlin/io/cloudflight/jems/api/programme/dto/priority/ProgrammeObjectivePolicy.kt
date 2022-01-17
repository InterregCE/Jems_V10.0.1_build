package io.cloudflight.jems.api.programme.dto.priority

import io.cloudflight.jems.api.programme.dto.priority.ProgrammeObjective.ISO12
import io.cloudflight.jems.api.programme.dto.priority.ProgrammeObjective.PO1
import io.cloudflight.jems.api.programme.dto.priority.ProgrammeObjective.PO2
import io.cloudflight.jems.api.programme.dto.priority.ProgrammeObjective.PO3
import io.cloudflight.jems.api.programme.dto.priority.ProgrammeObjective.PO4
import io.cloudflight.jems.api.programme.dto.priority.ProgrammeObjective.PO5

enum class ProgrammeObjectivePolicy(val objective: ProgrammeObjective, val officialProgrammePolicyCode: String) {

    AdvancedTechnologies(PO1, "RSO1.1"),
    Digitisation(PO1, "RSO1.2"),
    Growth(PO1, "RSO1.3"),
    IndustrialTransition(PO1, "RSO1.4"),
    DigitalConnectivity(PO1, "RSO1.5"),

    EnergyEfficiency(PO2, "RSO2.1"),
    RenewableEnergy(PO2, "RSO2.2"),
    SmartEnergy(PO2, "RSO2.3"),
    ClimateChange(PO2, "RSO2.4"),
    WaterManagement(PO2, "RSO2.5"),
    CircularEconomy(PO2, "RSO2.6"),
    GreenInfrastructure(PO2, "RSO2.7"),
    ZeroCarbonEconomy(PO2, " RSO2.8"),

    InterModalTenT(PO3, "RSO3.1"),
    CrossBorderMobility(PO3, "RSO3.2"),

    SocialInfrastructure(PO4, "RSO4.1"),
    QualityInEducation(PO4, "RSO4.2"),
    DisadvantagedGroups(PO4, "RSO4.3"),
    IntegratedActionsForMigrants(PO4, "RSO4.4"),
    Healthcare(PO4,"RSO4.5"),
    CultureAndTourism(PO4, "RSO4.6"),
    PeacePlusCoDesignedLocalCommunity(PO4, "ISO4.1"),
    PeacePlusEmpoweringCommunities(PO4, "ISO4.2"),
    PeacePlusPositiveRelations(PO4, "ISO4.3"),
    PeacePlusReimagingCommunities(PO4, "ISO4.4"),
    PeacePlusSharedLearning(PO4, "ISO4.5"),
    PeacePlusYouthProgramme(PO4, "ISO4.6"),
    PeacePlusYouthMentalHealth(PO4, "ISO4.7"),
    PeacePlusRuralRegeneration(PO4, "ISO4.8"),
    PeacePlusVictimsAndSurvivors(PO4, "ISO4.9"),

    EnvDevelopment(PO5, "RSO5.1"),
    LocalEnvDevelopment(PO5, "RSO5.2"),

    ISO12PublicAuthorities(ISO12, "ISO6.1"),
    ISO12PromotingCooperation(ISO12, "ISO6.2"),
    ISO12MutualTrust(ISO12, "ISO6.3"),
    ISO12MacroRegion(ISO12, "ISO6.4"),
    ISO12Democracy(ISO12, "ISO6.5"),
    ISO12OtherActionsCooperation(ISO12, "ISO6.6"),
    ISO12BorderCrossing(ISO12, "ISO7.1"),
    ISO12MobilityMigration(ISO12, "ISO7.2"),
    ISO12InternationalProtection(ISO12, "ISO7.3"),
    ISO12OtherActionsSecureEurope(ISO12, "ISO7.4")

}
