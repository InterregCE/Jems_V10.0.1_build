import user from '../../../fixtures/users.json';
import call from '../../../fixtures/api/call/1.step.call.json';
import call2step from '../../../fixtures/api/call/2.step.call.json';
import application from '../../../fixtures/api/application/application.json';
import application2step from '../../../fixtures/api/application/2.step.application.json';
import partner from '../../../fixtures/api/application/partner/partner.json';
import faker from "@faker-js/faker";

context('Application form exports', () => {
  beforeEach(() => {
    cy.viewport(1920, 1080);
    cy.loginByRequest(user.applicantUser.email);
  });

  it('TB-366 Export application form using two sets of input and export language', function () {
    cy.fixture('application/application-form-export/TB-366.json').then(testData => {
      cy.createCall(call).then(callId => {
        // call.generalCallSettings.id = callId;
        application.details.projectCallId = callId;
        cy.publishCall(callId);
        application.identification.intro = testData.intro;
        application.identification.acronym = testData.acronym;
        application.details.acronym = testData.acronym;
        cy.createFullApplication(application, user.applicantUser.email).then(applicationId => {
          cy.visit(`app/project/detail/${applicationId}/export`, {failOnStatusCode: false});

          cy.contains('div', 'Input language').find('mat-select').click();
          cy.contains('mat-option', 'Deutsch').click();

          cy.contains('button', 'Export').clickToDownload(`api/project/${applicationId}/export/application?exportLanguage=EN&inputLanguage=DE`, 'pdf').then(file => {
            expect(file.fileName).to.contain('tb-366_en_de_2022');
            const assertionMessage = 'Verify downloaded pdf file';
            expect(file.text.includes(testData.exportedApplicationDataDE), assertionMessage).to.be.true;
          });

          cy.contains('div', 'Export language').find('mat-select').click();
          cy.contains('mat-option', 'Deutsch').click();
          cy.contains('div', 'Input language').find('mat-select').click();
          cy.contains('mat-option', 'English').click();

          cy.contains('button', 'Export').clickToDownload(`api/project/${applicationId}/export/application?exportLanguage=DE&inputLanguage=EN`, 'pdf').then(file => {
            expect(file.fileName).to.contain('tb-366_de_en_2022');
            const assertionMessage = 'Verify downloaded pdf file';
            expect(file.text.includes(testData.exportedApplicationDataEN), assertionMessage).to.be.true;
          });
        });
      });
    });
  });

  it.only('TB-367 Export application form in version other than the current', () => {
    cy.fixture('application/application-form-export/TB-367.json').then(testData => {
      call2step.generalCallSettings.startDateTime = faker.date.recent();
      call2step.generalCallSettings.endDateTimeStep1 = faker.date.soon(1);
      call2step.generalCallSettings.endDateTime = faker.date.soon(1, call2step.generalCallSettings.endDateTimeStep1);
      cy.createCall(call2step).then(callId => {
        cy.publishCall(callId);
        application2step.details.projectCallId = callId;
        application2step.firstStep.identification.acronym = testData.acronym;
        application2step.details.acronym = testData.acronym;
        cy.createApplication(application2step, user.applicantUser.email).then(applicationId => {

          // 1st step version
          cy.updateProjectIdentification(applicationId, application2step.firstStep.identification);
          cy.createPartner(applicationId, application2step.firstStep.partners[0].details).then(partnerId => {

            cy.approveApplication(applicationId, application2step.assessments);
            cy.startSecondStep(applicationId);

            // 2nd step version
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
            cy.approveApplication(applicationId, application2step.assessments);

            // modification (approved - current) version
            cy.startModification(applicationId, user.programmeUser.email);
            cy.updateProjectIdentification(applicationId, testData.approvedModificationData.identification);
            partner.details.abbreviation = testData.approvedModificationData.partnerAbbreviation;
            cy.createPartners(applicationId, [partner]);
            cy.runPreSubmissionCheck(applicationId);
            cy.submitProjectApplication(applicationId);
            cy.approveModification(applicationId, testData.approvalInfo, user.programmeUser.email);

            // modification (rejected - non-current) version
            cy.startModification(applicationId, user.programmeUser.email);
            cy.updateProjectIdentification(applicationId, testData.rejectedModificationData.identification);
            cy.get('@partnerId').then((partnerId: any) => {
              // deactivate the last created partner
              cy.deactivatePartner(partnerId);
            });
            const thirdPartner = JSON.parse(JSON.stringify(partner));
            thirdPartner.details.abbreviation = testData.rejectedModificationData.partnerAbbreviation;
            cy.createPartners(applicationId, [thirdPartner]);
            cy.runPreSubmissionCheck(applicationId);
            cy.submitProjectApplication(applicationId);
            cy.rejectModification(applicationId, testData.rejectionInfo, user.programmeUser.email);

            cy.visit(`app/project/detail/${applicationId}/export`, {failOnStatusCode: false});

            // export current (approved) version
            cy.contains('button', 'Export').clickToDownload(`api/project/${applicationId}/export/application?*`, 'pdf').then(file => {
              const assertionMessage = 'Verify downloaded pdf file for current version';
              expect(file.text.includes(testData.approvedModificationExportData), assertionMessage).to.be.true;
            });

            // export step 1 version
            cy.get('div#export-config').contains('div', 'Project version').find('mat-select').click();
            cy.contains('mat-option', 'V. 1.0').click();

            cy.contains('div#export-config button', 'Export').clickToDownload(`api/project/${applicationId}/export/application?*version=1.0`, 'pdf').then(file => {
              const assertionMessage = 'Verify downloaded pdf file for step 1 version';
              expect(file.text.includes(testData.step1ExportData), assertionMessage).to.be.true;
            });

            // export step 2 version
            cy.get('div#export-config').contains('div', 'Project version').find('mat-select').click();
            cy.contains('mat-option', 'V. 2.0').click();

            cy.contains('button', 'Export').clickToDownload(`api/project/${applicationId}/export/application?*version=2.0`, 'pdf').then(file => {
              const assertionMessage = 'Verify downloaded pdf file for step 2 version';
              expect(file.text.includes(testData.step2ExportData), assertionMessage).to.be.true;
            });

            // export rejected version
            cy.get('div#export-config').contains('div', 'Project version').find('mat-select').click();
            cy.contains('mat-option', 'V. 4.0').click();

            cy.contains('button', 'Export').clickToDownload(`api/project/${applicationId}/export/application?*version=4.0`, 'pdf').then(file => {
              const assertionMessage = 'Verify downloaded pdf file for rejected version';
              expect(file.text.includes(testData.rejectedModificationExportData), assertionMessage).to.be.true;
            });
          });
        });
      });
    });
  });
});
