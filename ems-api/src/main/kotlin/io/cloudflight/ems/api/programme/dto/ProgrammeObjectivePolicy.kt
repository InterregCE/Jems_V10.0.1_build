package io.cloudflight.ems.api.programme.dto

import io.cloudflight.ems.api.programme.dto.ProgrammeObjective.ISO1
import io.cloudflight.ems.api.programme.dto.ProgrammeObjective.ISO12
import io.cloudflight.ems.api.programme.dto.ProgrammeObjective.ISO2
import io.cloudflight.ems.api.programme.dto.ProgrammeObjective.PO1
import io.cloudflight.ems.api.programme.dto.ProgrammeObjective.PO2
import io.cloudflight.ems.api.programme.dto.ProgrammeObjective.PO3
import io.cloudflight.ems.api.programme.dto.ProgrammeObjective.PO4
import io.cloudflight.ems.api.programme.dto.ProgrammeObjective.PO5

enum class ProgrammeObjectivePolicy(val objective: ProgrammeObjective) {

    AdvancedTechnologies(PO1),
    Digitalization(PO1),
    Growth(PO1),
    IndustrialTransition(PO1),

    EnergyEfficiency(PO2),
    RenewableEnergy(PO2),
    SmartEnergy(PO2),
    ClimateChange(PO2),
    WaterManagement(PO2),
    CircularEconomy(PO2),
    GreenUrban(PO2),

    DigitalConnectivity(PO3),
    InterModalTenT(PO3),
    CrossBorderMobility(PO3),
    MultiModalUrban(PO3),

    SocialInnovation(PO4),
    Infrastructure(PO4),
    DisadvantagedGroups(PO4),
    Healthcare(PO4),
    EmploymentAcrossBorders(PO4),
    LearningAcrossBorders(PO4),
    HealthcareAcrossBorders(PO4),
    LongTermHealthcareAcrossBorders(PO4),
    EqualOpportunitiesAcrossBorders(PO4),

    EnvDevelopment(PO5),
    LocalEnvDevelopment(PO5),

    ISO1PublicAuthorities(ISO1),
    ISO1AdministrativeCooperation(ISO1),
    ISO1IncreaseTrust(ISO1),
    ISO1MacroRegion(ISO1),
    ISO1Democracy(ISO1),
    ISO1Other(ISO1),

    ISO2PublicAuthorities(ISO2),
    ISO2AdministrativeCooperation(ISO2),
    ISO2IncreaseTrust(ISO2),
    ISO2MacroRegion(ISO2),
    ISO2Democracy(ISO2),
    ISO2Other(ISO2),

    ISO12PublicAuthorities(ISO12),
    ISO12AdministrativeCooperation(ISO12),
    ISO12IncreaseTrust(ISO12),
    ISO12MacroRegion(ISO12),
    ISO12Democracy(ISO12),
    ISO12Other(ISO12),

}
