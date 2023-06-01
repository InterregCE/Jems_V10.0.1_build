import user from '../fixtures/users.json';
import call from '../fixtures/api/call/1.step.call.json';
import application from '../fixtures/api/application/application.json';
import partner from '../fixtures/api/application/partner/partner.json';
import {faker} from '@faker-js/faker';

it('TB-1054 Application with all data maxed out can be created and exported', () => {
  call.generalCallSettings.lengthOfPeriod = 1;
  const fund = {programmeFund: {id: 9}, selected: true, rate: 80, adjustable: true};
  // @ts-ignore
  call.generalCallSettings.funds.push(fund);
  cy.createCall(call, user.programmeUser.email).then(callId => {
    application.details.projectCallId = callId;
    cy.publishCall(callId);
    cy.loginByRequest(user.applicantUser.email);

    const randomString = faker.random.words(1000);
    const numberOfPeriods = 10; // max 999, max supported 60
    const numberOfPartners = 30; // max 30
    const numberOfOrganisations = 30; // max 30
    const numberOfCostItems = 5; // no limit, max supported 30
    const numberOfWorkPackages = 5; // max 20
    const numberOfInvestmentsAndActivities = 20; // max 20
    const numberOfDeliverables = 20; // max 20
    const numberOfOutputs = 10; // max 10
    const numberOfResults = 20; // max 20
    const numberOfLumpSums = 50; // max 50
    const numberOfProjectProposedUnitCosts = 10; // max 10

    cy.createApplication(application).then(applicationId => {

      // A - Project identification
      application.identification.acronym = randomString.substring(0, 25);
      application.identification.title.forEach(title => {
        title.translation = randomString.substring(0, 200);
      });
      application.identification.duration = numberOfPeriods
      application.identification.intro.forEach(intro => {
        intro.translation = randomString.substring(0, 5000);
      });
      cy.updateProjectIdentification(applicationId, application.identification);

      // B - Project partners
      for (let i = 0; i < numberOfPartners; i++) {
        cy.then(function () {

          if (i === 0) {
            partner.details.role = 'LEAD_PARTNER';
          } else {
            partner.details.role = 'PARTNER';
          }
          partner.details.abbreviation = faker.random.alphaNumeric(15);
          application.associatedOrganisations[0].cypressReference = partner.details.abbreviation;
          partner.details.nameInOriginalLanguage = randomString.substring(0, 100);
          partner.details.nameInEnglish = randomString.substring(0, 100);
          partner.details.department.forEach(department => {
            department.translation = randomString.substring(0, 250);
          });
          partner.details.vat = randomString.substring(0, 50);
          partner.details.otherIdentifierNumber = randomString.substring(0, 50);
          partner.details.otherIdentifierDescription.forEach(description => {
            description.translation = randomString.substring(0, 100);
          });
          cy.createPartner(applicationId, partner.details).then(partnerId => {

            partner.address[0].street = randomString.substring(0, 50);
            partner.address[0].houseNumber = randomString.substring(0, 20);
            partner.address[0].postalCode = randomString.substring(0, 20);
            partner.address[0].city = randomString.substring(0, 50);
            partner.address[0].homepage = randomString.substring(0, 250);

            partner.address[1].street = randomString.substring(0, 50);
            partner.address[1].houseNumber = randomString.substring(0, 20);
            partner.address[1].postalCode = randomString.substring(0, 20);
            partner.address[1].city = randomString.substring(0, 50);
            cy.updatePartnerAddress(partnerId, partner.address);

            partner.contact[0].title = randomString.substring(0, 25);
            partner.contact[0].firstName = randomString.substring(0, 50);
            partner.contact[0].lastName = randomString.substring(0, 50);
            partner.contact[1].title = randomString.substring(0, 25);
            partner.contact[1].firstName = randomString.substring(0, 50);
            partner.contact[1].lastName = randomString.substring(0, 50);
            partner.contact[1].email = faker.random.alphaNumeric(253) + '@a';
            partner.contact[1].telephone = faker.phone.number('+48 91 ### ## ##### #####');
            cy.updatePartnerContact(partnerId, partner.contact);

            partner.motivation.organizationExperience.forEach(experience => {
              experience.translation = randomString.substring(0, 3000);
            });
            partner.motivation.organizationRole.forEach(role => {
              role.translation = randomString.substring(0, 3000);
            });
            partner.motivation.organizationRelevance.forEach(relevance => {
              relevance.translation = randomString.substring(0, 3000);
            });
            cy.updatePartnerMotivation(partnerId, partner.motivation);

            partner.budget.options.otherCostsOnStaffCostsFlatRate = null;
            partner.budget.staff = [];
            partner.budget.travel = [];
            partner.budget.external = [];
            partner.budget.equipment = [];
            partner.budget.infrastructure = [];
            // @ts-ignore
            partner.budget.unit = [];
            const costItem: any = {
              description: generateTranslatedItem(randomString, 255),
              comments: generateTranslatedItem(randomString, 250),
              unitType: generateTranslatedItem(randomString, 100),
              awardProcedures: generateTranslatedItem(randomString, 250),
              numberOfUnits: 1,
              pricePerUnit: 99999999.99,
              budgetPeriods: [{
                amount: 99999999.99,
                number: 1
              }]
            };
            
            const unitCostItem: any = {
              numberOfUnits: 1,
              unitCostId: 7,
              budgetPeriods: [{
                amount: 1999.99,
                number: 1
              }]
            };

            for (let i = 0; i < numberOfCostItems; i++) {
              partner.budget.staff.push(costItem);
              partner.budget.travel.push(costItem);
              partner.budget.external.push(costItem);
              partner.budget.equipment.push(costItem);
              partner.budget.infrastructure.push(costItem);
              // @ts-ignore
              partner.budget.unit.push(unitCostItem);
            }
            cy.updatePartnerBudget(partnerId, partner.budget);

            partner.cofinancing = {
              finances: [
                {fundId: 1, fundType: 'MainFund', percentage: 60},
                {fundId: 3, fundType: 'MainFund', percentage: 10},
                {fundId: 8, fundType: 'MainFund', percentage: 10},
                {fundId: 9, fundType: 'MainFund', percentage: 10},
                {percentage: 10, fundType: 'PartnerContribution'}
              ],
              partnerContributions: [
                {name: partner.details.abbreviation, status: 'Public', amount: 149100000, partner: true},
                {name: randomString.substring(0, 255), status: 'Public', amount: 100000, partner: false},
                {name: randomString.substring(0, 255), status: 'Public', amount: 100000, partner: false},
                {name: randomString.substring(0, 255), status: 'Public', amount: 100000, partner: false},
                {name: randomString.substring(0, 255), status: 'Public', amount: 100000, partner: false},
                {name: randomString.substring(0, 255), status: 'Public', amount: 100000, partner: false},
                {name: randomString.substring(0, 255), status: 'Public', amount: 100000, partner: false},
                {name: randomString.substring(0, 255), status: 'Public', amount: 100000, partner: false},
                {name: randomString.substring(0, 255), status: 'Public', amount: 100000, partner: false},
                {name: randomString.substring(0, 255), status: 'Public', amount: 100000, partner: false}
              ]
            }
            cy.updatePartnerCofinancing(partnerId, partner.cofinancing);

            partner.stateAid.justification1.forEach(justification => {
              justification.translation = randomString.substring(0, 1000);
            });
            partner.stateAid.justification2.forEach(justification => {
              justification.translation = randomString.substring(0, 1000);
            });
            partner.stateAid.justification3.forEach(justification => {
              justification.translation = randomString.substring(0, 1000);
            });
            partner.stateAid.justification4.forEach(justification => {
              justification.translation = randomString.substring(0, 1000);
            });
            cy.updatePartnerStateAid(partnerId, partner.stateAid);
          });
        });
      }

      // B - Associated organisations
      for (let i = 0; i < numberOfOrganisations; i++) {
        cy.then(function () {
          application.associatedOrganisations[0].nameInOriginalLanguage = randomString.substring(0, 100);
          application.associatedOrganisations[0].nameInEnglish = randomString.substring(0, 100);
          application.associatedOrganisations[0].address.street = randomString.substring(0, 50);
          application.associatedOrganisations[0].address.houseNumber = randomString.substring(0, 20);
          application.associatedOrganisations[0].address.postalCode = randomString.substring(0, 20);
          application.associatedOrganisations[0].address.city = randomString.substring(0, 50);
          application.associatedOrganisations[0].contacts[0].title = randomString.substring(0, 25);
          application.associatedOrganisations[0].contacts[0].firstName = randomString.substring(0, 50);
          application.associatedOrganisations[0].contacts[0].lastName = randomString.substring(0, 50);
          application.associatedOrganisations[0].contacts[1].title = randomString.substring(0, 25);
          application.associatedOrganisations[0].contacts[1].firstName = randomString.substring(0, 50);
          application.associatedOrganisations[0].contacts[1].lastName = randomString.substring(0, 50);
          application.associatedOrganisations[0].contacts[1].email = faker.random.alphaNumeric(253) + '@a';
          application.associatedOrganisations[0].contacts[1].telephone = faker.phone.number('+48 91 ### ## ##### #####');
          application.associatedOrganisations[0].roleDescription.forEach(roleDescription => {
            roleDescription.translation = randomString.substring(0, 3000);
          });
          cy.createAssociatedOrganisation(applicationId, application.associatedOrganisations[0]);
        });
      }

      // C.1 - Project overall objective
      application.description.overallObjective.forEach(overallObjective => {
        overallObjective.translation = randomString.substring(0, 500);
      });
      cy.updateProjectOverallObjective(applicationId, application.description.overallObjective);

      // C.2 Project relevance and context
      application.description.relevanceAndContext.territorialChallenge.forEach(territorialChallenge => {
        territorialChallenge.translation = randomString.substring(0, 5000);
      });
      application.description.relevanceAndContext.commonChallenge.forEach(commonChallenge => {
        commonChallenge.translation = randomString.substring(0, 5000);
      });
      application.description.relevanceAndContext.transnationalCooperation.forEach(transnationalCooperation => {
        transnationalCooperation.translation = randomString.substring(0, 5000);
      });
      application.description.relevanceAndContext.projectBenefits = [];
      const projectBenefit = {
        group: 'LocalPublicAuthority',
        specification: generateTranslatedItem(randomString, 2000)
      }
      application.description.relevanceAndContext.projectStrategies = [];
      const projectStrategy = {
        strategy: 'EUStrategyAdriaticIonianRegion',
        specification: generateTranslatedItem(randomString, 2000)
      }
      application.description.relevanceAndContext.projectSynergies = [];
      const projectSynergy = {
        specification: generateTranslatedItem(randomString, 2000),
        synergy: generateTranslatedItem(randomString, 2000)
      }
      for (let i = 0; i < 20; i++) {
        application.description.relevanceAndContext.projectBenefits.push(projectBenefit);
        application.description.relevanceAndContext.projectStrategies.push(projectStrategy);
        application.description.relevanceAndContext.projectSynergies.push(projectSynergy);
      }
      application.description.relevanceAndContext.availableKnowledge.forEach(availableKnowledge => {
        availableKnowledge.translation = randomString.substring(0, 5000);
      });
      cy.updateProjectRelevanceAndContext(applicationId, application.description.relevanceAndContext);

      // C.3 Project partnership
      application.description.partnership.forEach(partnership => {
        partnership.translation = randomString.substring(0, 5000);
      });
      cy.updateProjectPartnership(applicationId, application.description.partnership);

      // C.4 - Project work plan
      cy.then(function () {

        application.description.workPlan[0].details.name.forEach(name => {
          name.translation = randomString.substring(0, 100);
        });
        application.description.workPlan[0].details.specificObjective.forEach(specificObjective => {
          specificObjective.translation = randomString.substring(0, 1000);
        });
        application.description.workPlan[0].details.objectiveAndAudience.forEach(objectiveAndAudience => {
          objectiveAndAudience.translation = randomString.substring(0, 1000);
        });

        application.description.workPlan[0].investments[0].title.forEach(item => {
          item.translation = randomString.substring(0, 50);
        });
        application.description.workPlan[0].investments[0].justificationExplanation.forEach(item => {
          item.translation = randomString.substring(0, 2000);
        });
        application.description.workPlan[0].investments[0].justificationTransactionalRelevance.forEach(item => {
          item.translation = randomString.substring(0, 2000);
        });
        application.description.workPlan[0].investments[0].justificationBenefits.forEach(item => {
          item.translation = randomString.substring(0, 2000);
        });
        application.description.workPlan[0].investments[0].justificationPilot.forEach(item => {
          item.translation = randomString.substring(0, 2000);
        });
        application.description.workPlan[0].investments[0].address.street = randomString.substring(0, 50);
        application.description.workPlan[0].investments[0].address.houseNumber = randomString.substring(0, 20);
        application.description.workPlan[0].investments[0].address.postalCode = randomString.substring(0, 20);
        application.description.workPlan[0].investments[0].address.city = randomString.substring(0, 50);

        application.description.workPlan[0].investments[0].risk.forEach(item => {
          item.translation = randomString.substring(0, 2000);
        });
        application.description.workPlan[0].investments[0].documentation.forEach(item => {
          item.translation = randomString.substring(0, 2000);
        });
        application.description.workPlan[0].investments[0].documentationExpectedImpacts.forEach(item => {
          item.translation = randomString.substring(0, 2000);
        });
        application.description.workPlan[0].investments[0].ownershipSiteLocation.forEach(item => {
          item.translation = randomString.substring(0, 2000);
        });
        application.description.workPlan[0].investments[0].ownershipMaintenance.forEach(item => {
          item.translation = randomString.substring(0, 2000);
        });
        application.description.workPlan[0].investments[0].ownershipRetain.forEach(item => {
          item.translation = randomString.substring(0, 2000);
        });

        application.description.workPlan[0].activities[0].title.forEach(item => {
          item.translation = randomString.substring(0, 200);
        });
        application.description.workPlan[0].activities[0].description.forEach(item => {
          item.translation = randomString.substring(0, 3000);
        });
        application.description.workPlan[0].activities[0].cypressReferencePartner = partner.details.abbreviation;
        application.description.workPlan[0].activities[0].deliverables[0].title.forEach(item => {
          item.translation = randomString.substring(0, 100);
        });
        application.description.workPlan[0].activities[0].deliverables[0].description.forEach(item => {
          item.translation = randomString.substring(0, 300);
        });

        application.description.workPlan[0].outputs[0].title.forEach(item => {
          item.translation = randomString.substring(0, 200);
        });
        application.description.workPlan[0].outputs[0].targetValue = 9999999.00;

        application.description.workPlan[0].outputs[0].description.forEach(item => {
          item.translation = randomString.substring(0, 500);
        });

        for (let i = 0; i < numberOfWorkPackages; i++) {
          for (let k = 0; k < numberOfInvestmentsAndActivities; k++) {
            application.description.workPlan[0].investments[k] = application.description.workPlan[0].investments[0];
            application.description.workPlan[0].activities[k] = application.description.workPlan[0].activities[0];
            for (let j = 0; j < numberOfDeliverables; j++) {
              application.description.workPlan[0].activities[k].deliverables[j] = application.description.workPlan[0].activities[0].deliverables[0];
            }
          }
          for (let k = 0; k < numberOfOutputs; k++) {
            application.description.workPlan[0].outputs[k] = application.description.workPlan[0].outputs[0];
          }
          application.description.workPlan[i] = application.description.workPlan[0];
        }
        cy.createProjectWorkPlan(applicationId, application.description.workPlan);

        // C.5 - Project results
        application.description.results[0].description.forEach(description => {
          description.translation = randomString.substring(0, 1000);
        });
        application.description.results[0].targetValue = 999999999.99;
        for (let i = 1; i < numberOfResults; i++) {
          application.description.results.push(application.description.results[0]);
        }
        cy.createProjectResults(applicationId, application.description.results);

        // C.7 - Project management
        application.description.management.projectCoordination.forEach(projectCoordination => {
          projectCoordination.translation = randomString.substring(0, 5000);
        });
        application.description.management.projectQualityAssurance.forEach(projectQualityAssurance => {
          projectQualityAssurance.translation = randomString.substring(0, 5000);
        });
        application.description.management.projectCommunication.forEach(projectCommunication => {
          projectCommunication.translation = randomString.substring(0, 5000);
        });
        application.description.management.projectFinancialManagement.forEach(projectFinancialManagement => {
          projectFinancialManagement.translation = randomString.substring(0, 5000);
        });
        application.description.management.projectJointDevelopmentDescription.forEach(projectJointDevelopmentDescription => {
          projectJointDevelopmentDescription.translation = randomString.substring(0, 2000);
        });
        application.description.management.projectJointImplementationDescription.forEach(projectJointImplementationDescription => {
          projectJointImplementationDescription.translation = randomString.substring(0, 2000);
        });
        application.description.management.projectJointStaffingDescription.forEach(projectJointStaffingDescription => {
          projectJointStaffingDescription.translation = randomString.substring(0, 2000);
        });
        application.description.management.projectJointFinancingDescription.forEach(projectJointFinancingDescription => {
          projectJointFinancingDescription.translation = randomString.substring(0, 2000);
        });
        application.description.management.sustainableDevelopmentDescription.forEach(sustainableDevelopmentDescription => {
          sustainableDevelopmentDescription.translation = randomString.substring(0, 2000);
        });
        application.description.management.equalOpportunitiesDescription.forEach(equalOpportunitiesDescription => {
          equalOpportunitiesDescription.translation = randomString.substring(0, 2000);
        });
        application.description.management.sexualEqualityDescription.forEach(sexualEqualityDescription => {
          sexualEqualityDescription.translation = randomString.substring(0, 2000);
        });
        cy.updateProjectManagement(applicationId, application.description.management);

        // C.8 Long-term plans
        application.description.longTermPlans.projectOwnership.forEach(projectOwnership => {
          projectOwnership.translation = randomString.substring(0, 5000);
        });
        application.description.longTermPlans.projectDurability.forEach(projectDurability => {
          projectDurability.translation = randomString.substring(0, 5000);
        });
        application.description.longTermPlans.projectTransferability.forEach(projectTransferability => {
          projectTransferability.translation = randomString.substring(0, 5000);
        });
        cy.updateProjectLongTermPlans(applicationId, application.description.longTermPlans);

        // E.1 - Project lump sums
        const lumpSum = {
          programmeLumpSumId: 2,
          period: 1,
          lumpSumContributions: [{
            amount: 5555.56,
            partnerAbbreviation: partner.details.abbreviation
          }]
        }
        for (let i = 0; i < numberOfLumpSums; i++) {
          application.lumpSums[i] = lumpSum;
        }
        cy.updateLumpSums(applicationId, application.lumpSums);
        // now also update cofinancing for the last partner
        partner.cofinancing.partnerContributions[0].amount = 149100000 + numberOfLumpSums * 5555.56 * 0.1;
        cy.updatePartnerCofinancing(this[partner.details.abbreviation], partner.cofinancing);

        // E.2.1 - Project proposed unit costs
        const projectProposedUnitCosts = [];
        application.projectProposedUnitCosts[0].name.forEach(item => {
          item.translation = randomString.substring(0, 50);
        });
        application.projectProposedUnitCosts[0].description.forEach(item => {
          item.translation = randomString.substring(0, 255);
        });
        application.projectProposedUnitCosts[0].type.forEach(item => {
          item.translation = randomString.substring(0, 25);
        });
        application.projectProposedUnitCosts[0].justification.forEach(item => {
          item.translation = randomString.substring(0, 5000);
        });
        application.projectProposedUnitCosts[0].costPerUnit = 999999999.99;
        application.projectProposedUnitCosts[0].foreignCurrencyCode = 'AED';
        application.projectProposedUnitCosts[0].costPerUnitForeignCurrency = 999999999.99;
        for (let i = 0; i < numberOfProjectProposedUnitCosts; i++) {
          projectProposedUnitCosts[i] = application.projectProposedUnitCosts[0];
        }
        cy.createProjectProposedUnitCosts(applicationId, projectProposedUnitCosts);
      });
    });
  });
});

function generateTranslatedItem(words, size) {
  return [{
    translation: words.substring(0, size),
    language: 'EN'
  }, {
    translation: words.substring(0, size),
    language: 'DE'
  }]
}