import user from '../../../../fixtures/users.json';
import call from '../../../../fixtures/api/call/1.step.call.json';
import application from '../../../../fixtures/api/application/application.json';
import call2step from '../../../../fixtures/api/call/2.step.call.json';
import application2step from '../../../../fixtures/api/application/2.step.application.json';
import partner from '../../../../fixtures/api/application/partner/partner.json';

context('Partners budget exports', () => {
  beforeEach(() => {
    cy.viewport(1920, 1080);
    cy.loginByRequest(user.applicantUser.email);
  });

  it('TB-369 Export partners budget using two sets of input and export language', () => {
    cy.fixture('project/exports/partners-budget/TB-369.json').then(testData => {
      cy.createCall(call, user.programmeUser.email).then(callId => {
        application.details.projectCallId = callId;
        cy.publishCall(callId, user.programmeUser.email);
        application.identification.acronym = testData.acronym;
        application.details.acronym = testData.acronym;
        cy.createFullApplication(application, user.programmeUser.email).then(applicationId => {
          cy.visit(`app/project/detail/${applicationId}/export`, {failOnStatusCode: false});

          cy.contains('Partners budget').click();
          cy.contains('div', 'Input language').find('mat-select').click();
          cy.contains('mat-option', 'Deutsch').click();

          cy.contains('button', 'Export').clickToDownload(`api/project/${applicationId}/export/budget?exportLanguage=EN&inputLanguage=DE`, 'csv').then(exportFile => {
            expect(exportFile.fileName).to.contain('tb-369_Budget_22');
            cy.fixture('project/exports/partners-budget/TB-369-export-en-de.csv').parseCSV().then(testDataFile => {
              const assertionMessage = 'Verify downloaded csv file';
              expect(exportFile.content.slice(1), assertionMessage).to.deep.equal(testDataFile);
            });
          });

          cy.contains('div', 'Export language').find('mat-select').click();
          cy.contains('mat-option', 'Deutsch').click();
          cy.contains('div', 'Input language').find('mat-select').click();
          cy.contains('mat-option', 'English').click();

          cy.contains('button', 'Export').clickToDownload(`api/project/${applicationId}/export/budget?exportLanguage=DE&inputLanguage=EN`, 'csv').then(exportFile => {
            expect(exportFile.fileName).to.contain('tb-369_Budget_22');
            cy.fixture('project/exports/partners-budget/TB-369-export-de-en.csv').parseCSV().then(testDataFile => {
              const assertionMessage = 'Verify downloaded csv file';
              expect(exportFile.content.slice(1), assertionMessage).to.deep.equal(testDataFile);
            });
          });
        });
      });
    });
  });


  it('TB-370 Export partners budget in version other than the current', () => {
    cy.fixture('project/exports/partners-budget/TB-370.json').then(testData => {
      cy.create2StepCall(call2step, user.programmeUser.email).then(callId => {
        cy.publishCall(callId, user.programmeUser.email);
        application2step.details.projectCallId = callId;
        application2step.firstStep.identification.acronym = testData.acronym;
        application2step.details.acronym = testData.acronym;
        cy.createApplication(application2step).then(applicationId => {

          // 1st step version
          cy.updateProjectIdentification(applicationId, application2step.firstStep.identification);
          cy.createPartner(applicationId, application2step.firstStep.partners[0].details).then(partnerId => {
            cy.runPreSubmissionCheck(applicationId);
            cy.submitProjectApplication(applicationId);

            cy.loginByRequest(user.programmeUser.email);
            cy.approveApplication(applicationId, application2step.assessments);
            cy.startSecondStep(applicationId);

            // 2nd step version
            cy.loginByRequest(user.applicantUser.email);
            application2step.secondStep.identification.acronym = testData.acronym + ' v2';
            const updatedPartner = application2step.secondStep.partners[0];
            cy.updateProjectIdentification(applicationId, application2step.secondStep.identification);
            cy.createProjectWorkPlan(applicationId, application2step.secondStep.description.workPlan);
            cy.updateProjectOverallObjective(applicationId, application2step.secondStep.description.overallObjective);
            cy.updateProjectRelevanceAndContext(applicationId, application2step.secondStep.description.relevanceAndContext);
            cy.updateProjectPartnership(applicationId, application2step.secondStep.description.partnership);
            cy.createProjectResults(applicationId, application2step.secondStep.description.results);
            cy.updateProjectManagement(applicationId, application2step.secondStep.description.management);
            cy.updateProjectLongTermPlans(applicationId, application2step.secondStep.description.longTermPlans);
            cy.updatePartner(partnerId, updatedPartner.details);
            cy.updatePartnerAddress(partnerId, updatedPartner.address);
            cy.updatePartnerContact(partnerId, updatedPartner.contact);
            cy.updatePartnerMotivation(partnerId, updatedPartner.motivation);
            cy.get('@investmentId').then(investmentId => {
              cy.updatePartnerBudget(partnerId, updatedPartner.budget, +investmentId);
            });
            cy.updatePartnerCofinancing(partnerId, updatedPartner.cofinancing);
            cy.updatePartnerStateAid(partnerId, updatedPartner.stateAid);
            cy.createAssociatedOrganization(applicationId, partnerId, updatedPartner.associatedOrganization);

            partner.details.abbreviation = testData.secondStep.partnerAbbreviation;
            cy.createPartners(applicationId, [partner]);

            cy.runPreSubmissionCheck(applicationId);
            cy.submitProjectApplication(applicationId);

            cy.approveApplication(applicationId, application2step.assessments, user.programmeUser.email);

            // modification (approved - current) version
            cy.startModification(applicationId, user.programmeUser.email);
            cy.updateProjectIdentification(applicationId, testData.approvedModificationData.identification);

            // deactivate second partner
            cy.get(`@${partner.details.abbreviation}`).then((partnerId: any) => {
              cy.deactivatePartner(partnerId);
            });

            // modify budget for the lead partner
            const modifiedPartner = JSON.parse(JSON.stringify(application2step.secondStep.partners[0]));
            modifiedPartner.details.abbreviation = testData.approvedModificationData.partnerAbbreviation;
            modifiedPartner.budget.infrastructure = [];
            modifiedPartner.budget.unit = testData.approvedModificationData.modifiedUnitCosts;
            modifiedPartner.cofinancing = testData.approvedModificationData.modifiedCofinancing;
            cy.updatePartner(partnerId, modifiedPartner.details);
            cy.updatePartnerBudget(partnerId, modifiedPartner.budget);
            cy.updatePartnerCofinancing(partnerId, modifiedPartner.cofinancing);

            cy.runPreSubmissionCheck(applicationId);
            cy.submitProjectApplication(applicationId);
            cy.approveModification(applicationId, testData.approvalInfo, user.programmeUser.email);

            // modification (rejected - non-current) version
            cy.startModification(applicationId, user.programmeUser.email);
            cy.updateProjectIdentification(applicationId, testData.rejectedModificationData.identification);
            const thirdPartner = JSON.parse(JSON.stringify(partner));
            thirdPartner.details.abbreviation = testData.rejectedModificationData.partnerAbbreviation;
            cy.createPartners(applicationId, [thirdPartner]);
            cy.runPreSubmissionCheck(applicationId);
            cy.submitProjectApplication(applicationId);
            cy.rejectModification(applicationId, testData.rejectionInfo, user.programmeUser.email);

            cy.visit(`app/project/detail/${applicationId}/export`, {failOnStatusCode: false});

            // export step 1 version
            cy.contains('Partners budget').click();
            cy.get('div#export-config').contains('div', 'Project version').find('mat-select').click();
            cy.contains('mat-option', 'V. 1.0').click();

            cy.contains('div#export-config button', 'Export').clickToDownload(`api/project/${applicationId}/export/budget?*version=1.0`, 'csv').then(exportFile => {
              cy.fixture('project/exports/partners-budget/TB-370-v1.csv').parseCSV().then(testDataFile => {
                const assertionMessage = 'Verify downloaded csv file for step 1 version';
                expect(exportFile.content.slice(1), assertionMessage).to.deep.equal(testDataFile);
              });
            });

            // export step 2 version
            cy.get('div#export-config').contains('div', 'Project version').find('mat-select').click();
            cy.contains('mat-option', 'V. 2.0').click();

            cy.contains('button', 'Export').clickToDownload(`api/project/${applicationId}/export/budget?*version=2.0`, 'csv').then(exportFile => {
              cy.fixture('project/exports/partners-budget/TB-370-v2.csv').parseCSV().then(testDataFile => {
                const assertionMessage = 'Verify downloaded csv file for step 2 version';
                expect(exportFile.content.slice(1), assertionMessage).to.deep.equal(testDataFile);
              });
            });

            // export rejected version
            cy.get('div#export-config').contains('div', 'Project version').find('mat-select').click();
            cy.contains('mat-option', 'V. 4.0').click();

            cy.contains('button', 'Export').clickToDownload(`api/project/${applicationId}/export/budget?*version=4.0`, 'csv').then(exportFile => {
              cy.fixture('project/exports/partners-budget/TB-370-v4.csv').parseCSV().then(testDataFile => {
                const assertionMessage = 'Verify downloaded csv file for rejected version';
                expect(exportFile.content.slice(1), assertionMessage).to.deep.equal(testDataFile);
              });
            });
          });
        });
      });
    });
  });
});
