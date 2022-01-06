
DELETE project_work_package_output, project_work_package_output_transl
FROM project_work_package_output
         INNER JOIN programme_indicator_output
         INNER JOIN project_work_package_output_transl
                    ON ( project_work_package_output.indicator_output_id = programme_indicator_output.id
                        AND project_work_package_output_transl.work_package_id = project_work_package_output.work_package_id)
WHERE programme_priority_policy_id IN ('PeacePlus','JobSeekers','LabourMarketMatching','GenderBalance','HealthyAgeing','DualTrainingSystems','EqualAccess','LifelongLearning','EqualOpportunities','IntegrationOfThirdCountryNationals','IntegrationOfMarginalised','AffordableServices','SocialIntegration','MaterialAssistance','ISO12Other',
'ISO1PublicAuthorities','ISO1AdministrativeCooperation','ISO1MutualTrust','ISO1MacroRegion','ISO1Democracy','ISO1Other','ISO2BorderCrossing', 'ISO2MobilityMigration','ISO2InternationalProtection','ISO2Other');


DELETE programme_indicator_output, programme_indicator_output_transl
FROM programme_indicator_output
INNER JOIN programme_indicator_output_transl
ON programme_indicator_output.id = programme_indicator_output_transl.source_entity_id
WHERE programme_priority_policy_id IN ('PeacePlus','JobSeekers','LabourMarketMatching','GenderBalance','HealthyAgeing','DualTrainingSystems','EqualAccess','LifelongLearning','EqualOpportunities','IntegrationOfThirdCountryNationals','IntegrationOfMarginalised','AffordableServices','SocialIntegration','MaterialAssistance','ISO12Other',
'ISO1PublicAuthorities','ISO1AdministrativeCooperation','ISO1MutualTrust','ISO1MacroRegion','ISO1Democracy','ISO1Other','ISO2BorderCrossing', 'ISO2MobilityMigration','ISO2InternationalProtection','ISO2Other');


DELETE programme_indicator_result, programme_indicator_result_transl
FROM programme_indicator_result
INNER JOIN programme_indicator_result_transl
ON programme_indicator_result.id = programme_indicator_result_transl.source_entity_id
WHERE programme_priority_policy_id IN ('PeacePlus','JobSeekers','LabourMarketMatching','GenderBalance','HealthyAgeing','DualTrainingSystems','EqualAccess','LifelongLearning','EqualOpportunities','IntegrationOfThirdCountryNationals','IntegrationOfMarginalised','AffordableServices','SocialIntegration','MaterialAssistance','ISO12Other',
'ISO1PublicAuthorities','ISO1AdministrativeCooperation','ISO1MutualTrust','ISO1MacroRegion','ISO1Democracy','ISO1Other','ISO2BorderCrossing', 'ISO2MobilityMigration','ISO2InternationalProtection','ISO2Other');

SET FOREIGN_KEY_CHECKS=0;

DELETE programme_priority, programme_priority_transl
FROM programme_priority
INNER JOIN programme_priority_transl
INNER JOIN programme_objective_policy ON ( programme_priority.id = programme_priority_transl.programme_priority_id
 AND programme_priority.objective_id  = programme_objective_policy.objective_id )
WHERE programme_objective_policy.code IN ('ISO1PublicAuthorities','ISO1AdministrativeCooperation','ISO1MutualTrust','ISO1MacroRegion','ISO1Democracy','ISO1Other','ISO2BorderCrossing', 'ISO2MobilityMigration','ISO2InternationalProtection','ISO2Other');


DELETE
FROM programme_priority_specific_objective
WHERE programme_objective_policy_code IN ('PeacePlus','JobSeekers','LabourMarketMatching','GenderBalance','HealthyAgeing','DualTrainingSystems','EqualAccess','LifelongLearning','EqualOpportunities','IntegrationOfThirdCountryNationals','IntegrationOfMarginalised','AffordableServices','SocialIntegration','MaterialAssistance','ISO12Other',
                                          'ISO1PublicAuthorities','ISO1AdministrativeCooperation','ISO1MutualTrust','ISO1MacroRegion','ISO1Democracy','ISO1Other','ISO2BorderCrossing', 'ISO2MobilityMigration','ISO2InternationalProtection','ISO2Other');

DELETE
FROM project_call_priority_specific_objective
WHERE programme_specific_objective IN ('PeacePlus','JobSeekers','LabourMarketMatching','GenderBalance','HealthyAgeing','DualTrainingSystems','EqualAccess','LifelongLearning','EqualOpportunities','IntegrationOfThirdCountryNationals','IntegrationOfMarginalised','AffordableServices','SocialIntegration','MaterialAssistance','ISO12Other',
                                       'ISO1PublicAuthorities','ISO1AdministrativeCooperation','ISO1MutualTrust','ISO1MacroRegion','ISO1Democracy','ISO1Other','ISO2BorderCrossing', 'ISO2MobilityMigration','ISO2InternationalProtection','ISO2Other');

DELETE
FROM programme_objective_policy
WHERE code IN ('PeacePlus','JobSeekers','LabourMarketMatching','GenderBalance','HealthyAgeing','DualTrainingSystems','EqualAccess','LifelongLearning','EqualOpportunities','IntegrationOfThirdCountryNationals','IntegrationOfMarginalised','AffordableServices','SocialIntegration','MaterialAssistance','ISO12Other',
               'ISO1PublicAuthorities','ISO1AdministrativeCooperation','ISO1MutualTrust','ISO1MacroRegion','ISO1Democracy','ISO1Other','ISO2BorderCrossing', 'ISO2MobilityMigration','ISO2InternationalProtection','ISO2Other');

DELETE FROM programme_objective WHERE code IN ('ISO1', 'ISO2');

UPDATE project
SET project.programme_priority_policy_objective_policy = NULL
WHERE project.programme_priority_policy_objective_policy IN ('PeacePlus','JobSeekers','LabourMarketMatching','GenderBalance','HealthyAgeing','DualTrainingSystems','EqualAccess','LifelongLearning','EqualOpportunities','IntegrationOfThirdCountryNationals','IntegrationOfMarginalised','AffordableServices','SocialIntegration','MaterialAssistance','ISO12Other',
                                                             'ISO1PublicAuthorities','ISO1AdministrativeCooperation','ISO1MutualTrust','ISO1MacroRegion','ISO1Democracy','ISO1Other','ISO2BorderCrossing', 'ISO2MobilityMigration','ISO2InternationalProtection','ISO2Other');

SET FOREIGN_KEY_CHECKS=1;

INSERT INTO programme_objective_policy  VALUES
                                            ("PO4", "PeacePlusCoDesignedLocalCommunity"),
                                            ("PO4", "PeacePlusEmpoweringCommunities"),
                                            ("PO4", "PeacePlusPositiveRelations"),
                                            ("PO4", "PeacePlusReimagingCommunities"),
                                            ("PO4", "PeacePlusSharedLearning"),
                                            ("PO4", "PeacePlusYouthProgramme"),
                                            ("PO4", "PeacePlusYouthMentalHealth"),
                                            ("PO4", "PeacePlusRuralRegeneration"),
                                            ("PO4", "PeacePlusVictimsAndSurvivors"),
                                            ("ISO12", "ISO12OtherActionsCooperation"),
                                            ("ISO12", "ISO12OtherActionsSecureEurope");
