import user from '../../../fixtures/users.json';
import call from "../../../fixtures/api/call/1.step.call.json";
import application from "../../../fixtures/api/application/application.json";

context('Project description tests', () => {
  
  before(() => {
    cy.loginByRequest(user.applicantUser.email);
    cy.createCall(call, user.programmeUser.email).then(callId => {
      application.details.projectCallId = callId;
      cy.publishCall(callId, user.programmeUser.email);
      cy.createApplication(application).then(applicationId => {
        cy.updateProjectIdentification(applicationId, application.identification);
        cy.createPartners(applicationId, application.partners);
      });
    });
  });

  beforeEach(function() {
    cy.loginByRequest(user.applicantUser.email);
    cy.visit(`app/project/detail/${this.applicationId}`, {failOnStatusCode: false});
    cy.wrap('WP1').as('workPlanId');
    cy.wrap('Lead Partner').as('partnerAbbreviation');
  });

  it('TB-639 Applicant can edit project overall objective', function () {
    cy.fixture('project/application-form/project-description/TB-639').then(testData => {

      cy.contains('C.1 Project overall objective').click();

      cy.contains('div', 'Programme priority specific objective').find('input').should('contain.value', testData.specificObjective);

      cy.contains('jems-multi-language-container', 'Project overall objective').within(() => {
        testData.overallObjective.forEach(overallObjective => {
          cy.contains('button', overallObjective.language).click();
          cy.get('textarea').type(overallObjective.translation);
        });
      });

      cy.contains('Save changes').click();
      cy.contains('Project identification saved successfully.').should('be.visible');
    });
  });

  it('TB-641 Applicant can edit project relevance and context', function () {
    cy.fixture('project/application-form/project-description/TB-641').then(testData => {

      cy.contains('C.2 Project relevance and context').click();

      cy.contains('C.2.1').next().next().within(() => {
        testData.territorialChallenge.forEach(territorialChallenge => {
          cy.contains('button', territorialChallenge.language).click();
          cy.get('textarea').type(territorialChallenge.translation);
        });
      });

      cy.contains('C.2.2').next().next().within(() => {
        testData.commonChallenge.forEach(commonChallenge => {
          cy.contains('button', commonChallenge.language).click();
          cy.get('textarea').type(commonChallenge.translation);
        });
      });

      cy.contains('C.2.3').next().next().within(() => {
        testData.transnationalCooperation.forEach(transnationalCooperation => {
          cy.contains('button', transnationalCooperation.language).click();
          cy.get('textarea').type(transnationalCooperation.translation);
        });
      });

      cy.contains('C.2.4').next().next().within(() => {
        testData.projectBenefits.forEach(projectBenefit => {
          cy.contains('button', 'add').click();
          cy.get('.jems-table-config').children().last().within(() => {
            cy.contains('mat-form-field', 'Target Group').click();
            cy.root().closest('body').contains(projectBenefit.group).click();
            projectBenefit.specification.forEach(transnationalCooperation => {
              cy.root().closest('jems-multi-language-container').contains('button', transnationalCooperation.language).click();
              cy.get('textarea').type(transnationalCooperation.translation);
            });
          });
        });
      });

      cy.contains('C.2.5').next().next().within(() => {
        testData.projectStrategies.forEach(projectStrategy => {
          cy.contains('button', 'add').click();
          cy.get('.jems-table-config').children().last().within(() => {
            cy.contains('mat-form-field', 'Strategy').click();
            cy.root().closest('body').contains(projectStrategy.strategy).click();
            projectStrategy.specification.forEach(transnationalCooperation => {
              cy.root().closest('jems-multi-language-container').contains('button', transnationalCooperation.language).click();
              cy.get('textarea').type(transnationalCooperation.translation);
            });
          });
        });
      });

      cy.contains('C.2.6').next().next().within(() => {
        testData.projectSynergies.forEach(projectSynergy => {
          cy.contains('button', 'add').click();
          cy.get('.jems-table-config').children().last().within(() => {
            projectSynergy.initiative.forEach(initiative => {
              cy.root().closest('jems-multi-language-container').contains('button', initiative.language).click();
              cy.get('textarea').eq(0).type(initiative.translation);
            });
            projectSynergy.synergy.forEach(synergy => {
              cy.root().closest('jems-multi-language-container').contains('button', synergy.language).click();
              cy.get('textarea').eq(1).type(synergy.translation);
            });
          });
        });
      });

      cy.contains('C.2.7').next().next().within(() => {
        testData.availableKnowledge.forEach(availableKnowledge => {
          cy.contains('button', availableKnowledge.language).click();
          cy.get('textarea').type(availableKnowledge.translation);
        });
      });

      cy.contains('Save changes').click();
      cy.contains('Project identification saved successfully.').should('be.visible');
    });
  });

  it('TB-642 Applicant can edit project partnership', function () {
    cy.fixture('project/application-form/project-description/TB-642').then(testData => {

      cy.contains('C.3 Project partnership').click();

      cy.get('jems-multi-language-container').within(() => {
        testData.projectPartnership.forEach(projectPartnership => {
          cy.contains('button', projectPartnership.language).click();
          cy.get('textarea').type(projectPartnership.translation);
        });
      });

      cy.contains('Save changes').click();
      cy.contains('Project identification saved successfully.').should('be.visible');
    });
  });

  it('TB-643 Applicant can create project work plans and edit objectives', function () {
    cy.fixture('project/application-form/project-description/TB-643').then(testData => {

      cy.contains('C.4 Project work plan').click();
      cy.contains('Add new work package').click();
      cy.get('jems-table').contains('1').click();

      cy.contains('jems-multi-language-container', 'Work package title').within(() => {
        testData.workPlanTitle.forEach(workPlanTitle => {
          cy.contains('button', workPlanTitle.language).click();
          cy.get('input').type(workPlanTitle.translation);
        });
      });

      cy.contains('jems-multi-language-container', 'Project specific objective').within(() => {
        testData.specificObjective.forEach(specificObjective => {
          cy.contains('button', specificObjective.language).click();
          cy.get('textarea').type(specificObjective.translation);
        });
      });

      cy.contains('jems-multi-language-container', 'Communication objective and target audience').within(() => {
        testData.objectiveAndAudience.forEach(objectiveAndAudience => {
          cy.contains('button', objectiveAndAudience.language).click();
          cy.get('textarea').type(objectiveAndAudience.translation);
        });
      });

      cy.contains('Save changes').click();
      cy.contains('Work package saved successfully').should('be.visible');
    });
  });

  it('TB-644 Applicant can create investments within work plans', function () {
    cy.fixture('project/application-form/project-description/TB-644').then(testData => {

      cy.contains(this.workPlanId).click();
      cy.contains('Investments').click();
      cy.contains('Add investment').click();

      cy.contains('jems-multi-language-container', 'Investment title').within(() => {
        testData.title.forEach(item => {
          cy.contains('button', item.language).click();
          cy.get('input').type(item.translation);
        });
      });

      cy.contains('mat-form-field', 'Expected delivery period').click();
      cy.contains(testData.expectedDeliveryPeriod).click();

      cy.contains('Please explain why this investment is needed.').next().within(() => {
        testData.justificationExplanation.forEach(item => {
          cy.contains('button', item.language).click();
          cy.get('textarea').type(item.translation);
        });
      });

      cy.contains('Please clearly describe the cross-border/transnational relevance').next().within(() => {
        testData.justificationTransactionalRelevance.forEach(item => {
          cy.contains('button', item.language).click();
          cy.get('textarea').type(item.translation);
        });
      });

      cy.contains('Please describe who is benefiting').next().within(() => {
        testData.justificationBenefits.forEach(item => {
          cy.contains('button', item.language).click();
          cy.get('textarea').type(item.translation);
        });
      });

      cy.contains('In the case of pilot investment, please clarify').next().within(() => {
        testData.justificationPilot.forEach(item => {
          cy.contains('button', item.language).click();
          cy.get('textarea').type(item.translation);
        });
      });

      cy.contains('mat-form-field', 'Country').click();
      cy.contains(testData.address.country).click();
      cy.contains('mat-form-field', 'NUTS 2').click();
      cy.contains(testData.address.region2).click();
      cy.contains('mat-form-field', 'NUTS 3').click();
      cy.contains(testData.address.region3).click();
      cy.contains('div', 'Street').find('input').type(testData.address.street);
      cy.contains('div', 'House number').find('input').type(testData.address.houseNumber);
      cy.contains('div', 'Postal code').find('input').type(testData.address.postalCode);
      cy.contains('div', 'City').find('input').type(testData.address.city);

      cy.contains('Describe the risk associated with the investment').next().within(() => {
        testData.risk.forEach(item => {
          cy.contains('button', item.language).click();
          cy.get('textarea').type(item.translation);
        });
      });

      cy.contains('Please list all technical requirements and permissions').next().within(() => {
        testData.documentation.forEach(item => {
          cy.contains('button', item.language).click();
          cy.get('textarea').type(item.translation);
        });
      });

      cy.contains('For investments in infrastructure with an expected lifespan').next().within(() => {
        testData.documentationExpectedImpacts.forEach(item => {
          cy.contains('button', item.language).click();
          cy.get('textarea').type(item.translation);
        });
      });

      cy.contains('Who owns the site where the investment is located?').next().within(() => {
        testData.ownershipSiteLocation.forEach(item => {
          cy.contains('button', item.language).click();
          cy.get('textarea').type(item.translation);
        });
      });

      cy.contains('Who will retain ownership of the investment at the end of the project?').next().within(() => {
        testData.ownershipMaintenance.forEach(item => {
          cy.contains('button', item.language).click();
          cy.get('textarea').type(item.translation);
        });
      });

      cy.contains('Who will take care of the maintenance of the investment? How will this be done?').next().within(() => {
        testData.ownershipRetain.forEach(item => {
          cy.contains('button', item.language).click();
          cy.get('textarea').type(item.translation);
        });
      });

      cy.contains('button', 'Create').click();
      cy.get('jems-table mat-row').then(investmentItem => {
        expect(investmentItem).to.contain('I1.1');
        expect(investmentItem).to.contain(testData.title[0].translation);
        expect(investmentItem).to.contain(testData.address.region3);
      });

      cy.contains(this.partnerAbbreviation).click();
      cy.contains('Budget').click();
      cy.contains('mat-select', 'N/A').scrollIntoView().click();
      cy.contains('mat-option', 'I1.1').should('be.visible');
    });
  });

  it('TB-647 Applicant can create activities within work plans', function () {
    cy.fixture('project/application-form/project-description/TB-647').then(testData => {
      cy.contains(this.workPlanId).click();
      cy.contains('Activities').click();
      cy.contains('Add activity').click();

      cy.contains('jems-multi-language-container', 'Title').within(() => {
        testData.title.forEach(item => {
          cy.contains('button', item.language).click();
          cy.get('textarea').type(item.translation);
        });
      });

      cy.contains('mat-form-field', 'Start period').click();
      cy.contains(testData.startPeriod).click();

      cy.contains('mat-form-field', 'End period').click();
      cy.contains(testData.endPeriod).click();

      cy.contains('jems-multi-language-container', 'Description').within(() => {
        testData.description.forEach(item => {
          cy.contains('button', item.language).click();
          cy.get('textarea').type(item.translation);
        });
      });

      cy.contains('mat-form-field', 'Partner(s) involved').click();
      cy.contains('mat-option', testData.partnerInvolved).click();


      testData.deliverables.forEach(deliverable => {
        cy.contains('button', 'add').click();
        cy.get('div.jems-table-config').children().last().within(() => {

          deliverable.title.forEach(item => {
            cy.root().closest('jems-multi-language-container').contains('button', item.language).click();
            cy.contains('div', 'Deliverable title').find('textarea').type(item.translation);
          });

          deliverable.description.forEach(item => {
            cy.root().closest('jems-multi-language-container').contains('button', item.language).click();
            cy.contains('div', 'Description').find('textarea').type(item.translation);
          });

          cy.contains('mat-form-field', 'Delivery period').click();
          cy.root().closest('body').contains(deliverable.period).click();
        });
      });

      cy.contains('button', 'Save changes').click();
      cy.contains('The work package activities were saved successfully').should('be.visible');
      
      cy.contains(this.partnerAbbreviation).click();
      cy.get('.mat-tab-header-pagination-after').click();
      cy.wait(500);
      cy.contains('a', 'State Aid').click();
      cy.contains('mat-form-field', 'State aid relevant activities').click();
      cy.contains('mat-option', 'ACTIVITY 1.1').should('be.visible');
    });
  });

  it('TB-648 Applicant can create outputs within work plans', function () {
    cy.fixture('project/application-form/project-description/TB-648').then(testData => {

      cy.contains(this.workPlanId).click();
      cy.contains('Outputs').click();
      cy.contains('Add Output').click();

      cy.contains('jems-multi-language-container', 'Output Title').within(() => {
        testData.title.forEach(item => {
          cy.contains('button', item.language).click();
          cy.get('textarea').type(item.translation);
        });
      });

      cy.contains('mat-form-field', 'Programme Output Indicator').click();
      cy.contains(testData.programmeOutputIndicator).click();

      cy.contains('div', 'Target Value').find('input').type(testData.targetValue);

      cy.contains('mat-form-field', 'Delivery Period').click();
      cy.contains(testData.period).click();

      cy.contains('jems-multi-language-container', 'Output Description').within(() => {
        testData.description.forEach(item => {
          cy.contains('button', item.language).click();
          cy.get('textarea').type(item.translation);
        });
      });

      cy.contains('button', 'Save changes').click();
      cy.contains('Work package outputs saved successfully').should('be.visible');
    });
  });

  it('TB-656 Applicant can create project results', function () {
    cy.fixture('project/application-form/project-description/TB-656').then(testData => {

      cy.contains('C.5 Project Results').click();
      cy.contains('Add result').click();

      cy.contains('mat-form-field', 'Programme result indicator').click();
      cy.contains(testData.programmeResultIndicator).click();

      cy.contains('div', 'Target value').find('input').type(testData.targetValue);

      cy.contains('mat-form-field', 'Delivery period').click();
      cy.contains(testData.period).click();

      cy.contains('jems-multi-language-container', 'Result description').within(() => {
        testData.description.forEach(item => {
          cy.contains('button', item.language).click();
          cy.get('textarea').type(item.translation);
        });
      });

      cy.contains('button', 'Save changes').click();
      cy.contains('Project results saved successfully').should('be.visible');
    });
  });

  it('TB-657 Applicant can edit project management', function () {
    cy.fixture('project/application-form/project-description/TB-657').then(testData => {

      cy.contains('C.7 Project management').click();

      cy.contains('C.7.1 How will you coordinate your project?').next().next().within(() => {
        testData.projectCoordination.forEach(item => {
          cy.contains('button', item.language).click();
          cy.get('textarea').type(item.translation);
        });
      });

      cy.contains('C.7.2 Which measures will you take to ensure quality in your project?').next().next().within(() => {
        testData.projectQualityAssurance.forEach(item => {
          cy.contains('button', item.language).click();
          cy.get('textarea').type(item.translation);
        });
      });

      cy.contains('C.7.3 What will be the general approach you will follow to communicate').next().next().within(() => {
        testData.projectCommunication.forEach(item => {
          cy.contains('button', item.language).click();
          cy.get('textarea').type(item.translation);
        });
      });

      cy.contains('C.7.4 How do you foresee the financial management of the project').next().next().within(() => {
        testData.projectFinancialManagement.forEach(item => {
          cy.contains('button', item.language).click();
          cy.get('textarea').type(item.translation);
        });
      });

      cy.contains('mat-checkbox', 'Joint development').parent().within(() => {
        cy.get('input').check({force: true});
        testData.projectJointDevelopmentDescription.forEach(item => {
          cy.root().closest('jems-multi-language-container').contains('button', item.language).click();
          cy.get('textarea').type(item.translation);
        });
      });

      cy.contains('mat-checkbox', 'Joint implementation').parent().within(() => {
        cy.get('input').check({force: true});
        testData.projectJointImplementationDescription.forEach(item => {
          cy.root().closest('jems-multi-language-container').contains('button', item.language).click();
          cy.get('textarea').type(item.translation);
        });
      });

      cy.contains('mat-checkbox', 'Joint staffing').parent().within(() => {
        cy.get('input').check({force: true});
        testData.projectJointStaffingDescription.forEach(item => {
          cy.root().closest('jems-multi-language-container').contains('button', item.language).click();
          cy.get('textarea').type(item.translation);
        });
      });

      cy.contains('mat-checkbox', 'Joint financing').parent().within(() => {
        cy.get('input').check({force: true});
        testData.projectJointFinancingDescription.forEach(item => {
          cy.root().closest('jems-multi-language-container').contains('button', item.language).click();
          cy.get('textarea').type(item.translation);
        });
      });

      cy.contains('div', 'Sustainable development').within(() => {
        cy.contains('button', testData.sustainableDevelopmentCriteriaEffect).click();
        testData.sustainableDevelopmentDescription.forEach(item => {
          cy.root().closest('jems-multi-language-container').contains('button', item.language).click();
          cy.get('textarea').type(item.translation);
        });
      });

      cy.contains('div', 'Equal opportunities and non-discrimination').within(() => {
        cy.contains('button', testData.equalOpportunitiesEffect).click();
        testData.equalOpportunitiesDescription.forEach(item => {
          cy.root().closest('jems-multi-language-container').contains('button', item.language).click();
          cy.get('textarea').type(item.translation);
        });
      });

      cy.contains('div', 'Equality between men and women').within(() => {
        cy.contains('button', testData.sexualEqualityEffect).click();
        testData.sexualEqualityDescription.forEach(item => {
          cy.root().closest('jems-multi-language-container').contains('button', item.language).click();
          cy.get('textarea').type(item.translation);
        });
      });

      cy.contains('Save changes').click();
      cy.contains('Project identification saved successfully.').should('be.visible');
    });
  });

  it('TB-658 Applicant can edit project long-term plans', function () {
    cy.fixture('project/application-form/project-description/TB-658').then(testData => {

      cy.contains('C.8 Long-term plans').click();

      cy.contains('C.8.1 Ownership').next().next().within(() => {
        testData.projectOwnership.forEach(item => {
          cy.contains('button', item.language).click();
          cy.get('textarea').type(item.translation);
        });
      });

      cy.contains('C.8.2 Durability').next().next().within(() => {
        testData.projectDurability.forEach(item => {
          cy.contains('button', item.language).click();
          cy.get('textarea').type(item.translation);
        });
      });

      cy.contains('C.8.3 Transferability').next().next().within(() => {
        testData.projectTransferability.forEach(item => {
          cy.contains('button', item.language).click();
          cy.get('textarea').type(item.translation);
        });
      });

      cy.contains('Save changes').click();
      cy.contains('Project identification saved successfully.').should('be.visible');
    });
  });
});
