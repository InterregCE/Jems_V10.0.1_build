import user from '../../../../fixtures/users.json';

context('Project description tests', () => {

  beforeEach(() => {
    cy.loginByRequest(user.applicantUser.email);
    cy.wrap(1).as('applicationId');
    cy.wrap('Lead Partner').as('partnerAbbreviation');
  });

  it('TB-639 Applicant can edit project overall objective', function () {
    cy.fixture('project/application-form/project-description/TB-639').then(testData => {
      cy.visit(`app/project/detail/${this.applicationId}`, {failOnStatusCode: false});

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
      cy.visit(`app/project/detail/${this.applicationId}`, {failOnStatusCode: false});

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
      cy.visit(`app/project/detail/${this.applicationId}`, {failOnStatusCode: false});

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
      cy.visit(`app/project/detail/${this.applicationId}`, {failOnStatusCode: false});

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
    const workplanId = 'WP1';
    cy.fixture('project/application-form/project-description/TB-644').then(testData => {
      cy.visit(`app/project/detail/${this.applicationId}`, {failOnStatusCode: false});

      cy.contains(workplanId).click();
      cy.contains('Investments').click();
      cy.contains('Add investment').click();

      cy.contains('jems-multi-language-container', 'Investment title').within(() => {
        testData.title.forEach(invetsmentTitle => {
          cy.contains('button', invetsmentTitle.language).click();
          cy.get('input').type(invetsmentTitle.translation);
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
    });
  });
});
