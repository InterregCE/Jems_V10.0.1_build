import user from '../../../../fixtures/users.json';
import call from '../../../../fixtures/api/call/1.step.call.json';
import application from '../../../../fixtures/api/application/application.json';
import call2step from '../../../../fixtures/api/call/2.step.call.json';
import application2step from '../../../../fixtures/api/application/2.step.application.json';
import partner from '../../../../fixtures/api/application/partner/partner.json';
import date from "date-and-time";

context('Partners budget exports', () => {
  beforeEach(() => {
    cy.loginByRequest(user.applicantUser.email);
  });

  it('TB-369 Export partners budget using two sets of input and export language', () => {
    cy.createCall(call, user.programmeUser.email).then(callId => {
      application.details.projectCallId = callId;
      cy.publishCall(callId, user.programmeUser.email);
      cy.createApprovedApplication(application, user.programmeUser.email).then(applicationId => {
        cy.visit(`app/project/detail/${applicationId}/export`, {failOnStatusCode: false});

        cy.contains('Partners budget').click();
        cy.contains('div', 'Input language').find('mat-select').click();
        cy.contains('mat-option', 'Deutsch').click();

        cy.contains('button', 'Export').clickToDownload(`api/project/${applicationId}/export/budget?exportLanguage=EN&inputLanguage=DE*`, 'xlsx').then(exportFile => {
          const fileNameRegex = generateRegex(applicationId, application.identification.acronym);
          expect(exportFile.fileName).to.match(fileNameRegex);
          cy.fixture('project/exports/partners-budget/TB-369-export-en-de.xlsx', null).parseXLSX().then(testDataFile => {
            const assertionMessage = 'Verify downloaded en-de xlsx file';
            expect(exportFile.content[0].data.slice(1), assertionMessage).to.deep.equal(testDataFile[0].data.slice(1));
            expect(exportFile.content[1].data.slice(1), assertionMessage).to.deep.equal(testDataFile[1].data.slice(1));
          });
        });

        cy.contains('div', 'Export language').find('mat-select').click();
        cy.contains('mat-option', 'Deutsch').click();
        cy.contains('div', 'Input language').find('mat-select').click();
        cy.contains('mat-option', 'English').click();

        cy.contains('button', 'Export').clickToDownload(`api/project/${applicationId}/export/budget?exportLanguage=DE&inputLanguage=EN*`, 'xlsx').then(exportFile => {
          const fileNameRegex = generateRegex(applicationId, application.identification.acronym);
          expect(exportFile.fileName).to.match(fileNameRegex);
          cy.fixture('project/exports/partners-budget/TB-369-export-de-en.xlsx', null).parseXLSX().then(testDataFile => {
            const assertionMessage = 'Verify downloaded de-en lsx file';
            expect(exportFile.content[0].data.slice(1), assertionMessage).to.deep.equal(testDataFile[0].data.slice(1));
            expect(exportFile.content[1].data.slice(1), assertionMessage).to.deep.equal(testDataFile[1].data.slice(1));
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

            cy.contains('div#export-config button', 'Export').clickToDownload(`api/project/${applicationId}/export/budget?*version=1.0`, 'xlsx').then(exportFile => {
              cy.fixture('project/exports/partners-budget/TB-370-v1.xlsx', null).parseXLSX().then(testDataFile => {
                const assertionMessage = 'Verify downloaded xlsx file for step 1 version';
                expect(exportFile.content[0].data.slice(1), assertionMessage).to.deep.equal(testDataFile[0].data.slice(1));
                expect(exportFile.content[1].data.slice(1), assertionMessage).to.deep.equal(testDataFile[1].data.slice(1));
              });
            });

            // export step 2 version
            cy.get('div#export-config').contains('div', 'Project version').find('mat-select').click();
            cy.contains('mat-option', 'V. 2.0').click();

            cy.contains('button', 'Export').clickToDownload(`api/project/${applicationId}/export/budget?*version=2.0`, 'xlsx').then(exportFile => {
              cy.fixture('project/exports/partners-budget/TB-370-v2.xlsx', null).parseXLSX().then(testDataFile => {
                const assertionMessage = 'Verify downloaded xlsx file for step 2 version';
                expect(exportFile.content[0].data.slice(1), assertionMessage).to.deep.equal(testDataFile[0].data.slice(1));
                expect(exportFile.content[1].data.slice(1), assertionMessage).to.deep.equal(testDataFile[1].data.slice(1));
              });
            });

            // export rejected version
            cy.get('div#export-config').contains('div', 'Project version').find('mat-select').click();
            cy.contains('mat-option', 'V. 4.0').click();

            cy.contains('button', 'Export').clickToDownload(`api/project/${applicationId}/export/budget?*version=4.0`, 'xlsx').then(exportFile => {
              cy.fixture('project/exports/partners-budget/TB-370-v4.xlsx', null).parseXLSX().then(testDataFile => {
                const assertionMessage = 'Verify downloaded xlsx file for rejected version';
                expect(exportFile.content[0].data.slice(1), assertionMessage).to.deep.equal(testDataFile[0].data.slice(1));
                expect(exportFile.content[1].data.slice(1), assertionMessage).to.deep.equal(testDataFile[1].data.slice(1));
              });
            });
          });
        });
      });
    });
  });

  it('TB-686 Export partners budget in different steps [step 2 only]', () => {
    cy.fixture('project/exports/application-form/TB-373.json').then(testData => {
      call2step.applicationFormConfiguration = testData.call.applicationFormConfiguration;
      cy.create2StepCall(call2step, user.programmeUser.email).then(callId => {
        cy.publishCall(callId, user.programmeUser.email);
        application2step.details.projectCallId = callId;
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
            const updatedPartner = testData.application.partners[0];
            application2step.secondStep.identification.acronym = testData.application.acronym + ' v2';
            cy.updateProjectIdentification(applicationId, application2step.secondStep.identification);
            cy.createProjectWorkPlan(applicationId, testData.application.description.workPlan);
            cy.updateProjectRelevanceAndContext(applicationId, application2step.secondStep.description.relevanceAndContext);
            cy.updateProjectPartnership(applicationId, application2step.secondStep.description.partnership);
            cy.createProjectResults(applicationId, testData.application.description.results);
            cy.updateProjectManagement(applicationId, application2step.secondStep.description.management);
            cy.updateProjectLongTermPlans(applicationId, application2step.secondStep.description.longTermPlans);
            cy.updatePartner(partnerId, updatedPartner.details);
            cy.updatePartnerAddress(partnerId, updatedPartner.address);
            cy.updatePartnerContact(partnerId, updatedPartner.contact);
            cy.updatePartnerBudget(partnerId, updatedPartner.budget);
            cy.updatePartnerCofinancing(partnerId, updatedPartner.cofinancing);
            cy.runPreSubmissionCheck(applicationId);
            cy.submitProjectApplication(applicationId);

            cy.approveApplication(applicationId, application2step.assessments, user.programmeUser.email);
            cy.visit(`app/project/detail/${applicationId}/export`, {failOnStatusCode: false});

            // export current step 2 (approved) version
            cy.contains('Partners budget').click();
            cy.contains('button', 'Export').clickToDownload(`api/project/${applicationId}/export/budget?*`, 'xlsx').then(exportFile => {
              cy.fixture('project/exports/partners-budget/TB-686-v2.xlsx', null).parseXLSX().then(testDataFile => {
                const assertionMessage = 'Verify downloaded xlsx file for rejected version';
                expect(exportFile.content[0].data.slice(1), assertionMessage).to.deep.equal(testDataFile[0].data.slice(1));
                expect(exportFile.content[1].data.slice(1), assertionMessage).to.deep.equal(testDataFile[1].data.slice(1));
              });
            });

            // export step 1 version
            cy.get('div#export-config').contains('div', 'Project version').find('mat-select').click();
            cy.contains('mat-option', 'V. 1.0').click();

            cy.contains('div#export-config button', 'Export').clickToDownload(`api/project/${applicationId}/export/budget?*version=1.0`, 'xlsx').then(exportFile => {
              cy.fixture('project/exports/partners-budget/TB-686-v1.xlsx', null).parseXLSX().then(testDataFile => {
                const assertionMessage = 'Verify downloaded xlsx file for step 1 version';
                expect(exportFile.content[0].data.slice(1), assertionMessage).to.deep.equal(testDataFile[0].data.slice(1));
                expect(exportFile.content[1].data.slice(1), assertionMessage).to.deep.equal(testDataFile[1].data.slice(1));
              });
            });
          });
        });
      });
    });
  });

  it('TB-368 Export partners budget in different steps [step 1&2]', () => {
    cy.fixture('project/exports/application-form/TB-545.json').then(testData => {
      call2step.applicationFormConfiguration = testData.call.applicationFormConfiguration;
      cy.create2StepCall(call2step, user.programmeUser.email).then(callId => {
        cy.publishCall(callId, user.programmeUser.email);
        testData.application.details.projectCallId = callId;
        cy.createApplication(testData.application).then(applicationId => {

          // 1st step version
          cy.updateProjectIdentification(applicationId, testData.application.identification);
          const leadPartner = testData.application.partners[0];
          cy.createPartner(applicationId, leadPartner.details).then(partnerId => {
            cy.createProjectWorkPlan(applicationId, testData.application.description.workPlan);
            cy.updateProjectRelevanceAndContext(applicationId, testData.application.description.relevanceAndContext);
            cy.updateProjectPartnership(applicationId, testData.application.description.partnership);
            cy.createProjectResults(applicationId, testData.application.description.results);
            cy.updateProjectManagement(applicationId, testData.application.description.management);
            cy.updateProjectLongTermPlans(applicationId, testData.application.description.longTermPlans);
            cy.updatePartnerAddress(partnerId, leadPartner.address);
            cy.updatePartnerContact(partnerId, leadPartner.contact);
            cy.updatePartnerBudget(partnerId, leadPartner.budget);
            cy.updatePartnerCofinancing(partnerId, leadPartner.cofinancing);

            cy.runPreSubmissionCheck(applicationId);
            cy.submitProjectApplication(applicationId);

            cy.loginByRequest(user.programmeUser.email);
            cy.approveApplication(applicationId, application2step.assessments);
            cy.startSecondStep(applicationId);

            // 2nd step version
            cy.loginByRequest(user.applicantUser.email);
            cy.runPreSubmissionCheck(applicationId);
            cy.submitProjectApplication(applicationId);

            cy.approveApplication(applicationId, application2step.assessments, user.programmeUser.email);
            cy.visit(`app/project/detail/${applicationId}/export`, {failOnStatusCode: false});

            // export current step 2 (approved) version
            cy.contains('Partners budget').click();
            cy.contains('button', 'Export').clickToDownload(`api/project/${applicationId}/export/budget?*`, 'xlsx').then(exportFile => {
              cy.fixture('project/exports/partners-budget/TB-368-v2.xlsx', null).parseXLSX().then(testDataFile => {
                const assertionMessage = 'Verify downloaded xlsx file for step 2 version';
                expect(exportFile.content[0].data.slice(1), assertionMessage).to.deep.equal(testDataFile[0].data.slice(1));
                expect(exportFile.content[1].data.slice(1), assertionMessage).to.deep.equal(testDataFile[1].data.slice(1));
              });
            });

            // export step 1 version
            cy.get('div#export-config').contains('div', 'Project version').find('mat-select').click();
            cy.contains('mat-option', 'V. 1.0').click();

            cy.contains('div#export-config button', 'Export').clickToDownload(`api/project/${applicationId}/export/budget?*version=1.0`, 'xlsx').then(exportFile => {
              cy.fixture('project/exports/partners-budget/TB-368-v1.xlsx', null).parseXLSX().then(testDataFile => {
                const assertionMessage = 'Verify downloaded xlsx file for step 1 version';
                expect(exportFile.content[0].data.slice(1), assertionMessage).to.deep.equal(testDataFile[0].data.slice(1));
                expect(exportFile.content[1].data.slice(1), assertionMessage).to.deep.equal(testDataFile[1].data.slice(1));
              });
            });
          });
        });
      });
    });
  });
  
  function generateRegex(applicationId, applicationAcronym) {
    const id = String(applicationId).padStart(5, '0');
    const today = new Date();
    const formattedToday = date.format(today, 'YYMM');
    return new RegExp(`${id}_${applicationAcronym}_Budget_${formattedToday}\\d\\d_\\d{6}.xlsx`);
  }
});
