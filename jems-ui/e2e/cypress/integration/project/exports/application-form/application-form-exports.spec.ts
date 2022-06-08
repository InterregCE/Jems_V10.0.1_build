import user from '../../../../fixtures/users.json';
import call from '../../../../fixtures/api/call/1.step.call.json';
import call2step from '../../../../fixtures/api/call/2.step.call.json';
import application from '../../../../fixtures/api/application/application.json';
import application2step from '../../../../fixtures/api/application/2.step.application.json';
import partner from '../../../../fixtures/api/application/partner/partner.json';

context('Application form exports', () => {
  beforeEach(() => {
    cy.loginByRequest(user.applicantUser.email);
  });

  it('TB-366 Export application form using two sets of input and export language', () => {
    cy.fixture('project/exports/application-form/TB-366.json').then(testData => {
      cy.createCall(call, user.programmeUser.email).then(callId => {
        application.details.projectCallId = callId;
        cy.publishCall(callId, user.programmeUser.email);
        application.identification.intro = testData.intro;
        cy.createFullApplication(application, user.programmeUser.email).then(applicationId => {
          cy.visit(`app/project/detail/${applicationId}/export`, {failOnStatusCode: false});

          cy.contains('div', 'Input language').find('mat-select').click();
          cy.contains('mat-option', 'Deutsch').click();

          cy.contains('button', 'Export').clickToDownload(`api/project/${applicationId}/export/application?exportLanguage=EN&inputLanguage=DE`, 'pdf').then(file => {
            expect(file.fileName).to.contain(`${application.identification.acronym}_en_de_2022`);
            cy.fixture('project/exports/application-form/TB-366-export-de.txt').then(testDataFile => {
              const assertionMessage = 'Verify downloaded pdf file';
              testDataFile = replace(testDataFile, applicationId, application.identification.acronym);
              expect(file.text === testDataFile, assertionMessage).to.be.true;
            });
          });

          cy.contains('div', 'Export language').find('mat-select').click();
          cy.contains('mat-option', 'Deutsch').click();
          cy.contains('div', 'Input language').find('mat-select').click();
          cy.contains('mat-option', 'English').click();

          cy.contains('button', 'Export').clickToDownload(`api/project/${applicationId}/export/application?exportLanguage=DE&inputLanguage=EN`, 'pdf').then(file => {
            expect(file.fileName).to.contain(`${application.identification.acronym}_de_en_2022`);
            cy.fixture('project/exports/application-form/TB-366-export-en.txt').then(testDataFile => {
              testDataFile = replace(testDataFile, applicationId, application.identification.acronym);
              const assertionMessage = 'Verify downloaded pdf file';
              expect(file.text === testDataFile, assertionMessage).to.be.true;
            });
          });
        });
      });
    });
  });

  it('TB-367 Export application form in version other than the current', () => {
    cy.fixture('project/exports/application-form/TB-367.json').then(testData => {
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
            cy.runPreSubmissionCheck(applicationId);
            cy.submitProjectApplication(applicationId);

            cy.approveApplication(applicationId, application2step.assessments, user.programmeUser.email);

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
            cy.get(`@${partner.details.abbreviation}`).then((partnerId: any) => {
              cy.deactivatePartner(partnerId);
            });
            const thirdPartner = JSON.parse(JSON.stringify(partner));
            thirdPartner.details.abbreviation = testData.rejectedModificationData.partnerAbbreviation;
            cy.createPartners(applicationId, [thirdPartner]);
            cy.runPreSubmissionCheck(applicationId);
            cy.submitProjectApplication(applicationId);
            cy.rejectModification(applicationId, testData.rejectionInfo, user.programmeUser.email);

            cy.visit(`app/project/detail/${applicationId}/export`, {failOnStatusCode: false});

            // export step 1 version
            cy.get('div#export-config').contains('div', 'Project version').find('mat-select').click();
            cy.contains('mat-option', 'V. 1.0').click();

            cy.contains('div#export-config button', 'Export').clickToDownload(`api/project/${applicationId}/export/application?*version=1.0`, 'pdf').then(file => {
              cy.fixture('project/exports/application-form/TB-367-v1.txt').then(testDataFile => {
                testDataFile = replace(testDataFile, applicationId, application2step.firstStep.identification.acronym);
                const assertionMessage = 'Verify downloaded pdf file for step 1 version';
                expect(file.text === testDataFile, assertionMessage).to.be.true;
              });
            });

            // export step 2 version
            cy.get('div#export-config').contains('div', 'Project version').find('mat-select').click();
            cy.contains('mat-option', 'V. 2.0').click();

            cy.contains('button', 'Export').clickToDownload(`api/project/${applicationId}/export/application?*version=2.0`, 'pdf').then(file => {
              cy.fixture('project/exports/application-form/TB-367-v2.txt').then(testDataFile => {
                testDataFile = replace(testDataFile, applicationId, application2step.secondStep.identification.acronym);
                const assertionMessage = 'Verify downloaded pdf file for step 2 version';
                expect(file.text === testDataFile, assertionMessage).to.be.true;
              });
            });

            // export rejected version
            cy.get('div#export-config').contains('div', 'Project version').find('mat-select').click();
            cy.contains('mat-option', 'V. 4.0').click();

            cy.contains('button', 'Export').clickToDownload(`api/project/${applicationId}/export/application?*version=4.0`, 'pdf').then(file => {
              cy.fixture('project/exports/application-form/TB-367-v4.txt').then(testDataFile => {
                testDataFile = replace(testDataFile, applicationId, testData.rejectedModificationData.identification.acronym);
                const assertionMessage = 'Verify downloaded pdf file for rejected version';
                expect(file.text === testDataFile, assertionMessage).to.be.true;
              });
            });
          });
        });
      });
    });
  });

  it('TB-373 Export application form in different steps [step 2 only]', () => {
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
            cy.contains('button', 'Export').clickToDownload(`api/project/${applicationId}/export/application?*`, 'pdf').then(file => {
              cy.fixture('project/exports/application-form/TB-373-exports-v2.txt').then(fileContent => {
                fileContent = replace(fileContent, applicationId, application2step.secondStep.identification.acronym);
                const assertionMessage = 'Verify downloaded pdf file for step 2 version';
                expect(file.text === fileContent, assertionMessage).to.be.true;
              });
            });

            // export step 1 version
            cy.get('div#export-config').contains('div', 'Project version').find('mat-select').click();
            cy.contains('mat-option', 'V. 1.0').click();

            cy.contains('div#export-config button', 'Export').clickToDownload(`api/project/${applicationId}/export/application?*version=1.0`, 'pdf').then(file => {
              cy.fixture('project/exports/application-form/TB-373-exports-v1.txt').then(fileContent => {
                fileContent = replace(fileContent, applicationId, application2step.firstStep.identification.acronym);
                const assertionMessage = 'Verify downloaded pdf file for step 1 version';
                expect(file.text === fileContent, assertionMessage).to.be.true;
              });
            });
          });
        });
      });
    });
  });

  it('TB-545 Export application form in different steps [step 1&2]', () => {
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
            cy.contains('button', 'Export').clickToDownload(`api/project/${applicationId}/export/application?*`, 'pdf').then(file => {
              cy.fixture('project/exports/application-form/TB-545-exports-v2.txt').then(fileContent => {
                fileContent = replace(fileContent, applicationId, testData.application.identification.acronym);
                const assertionMessage = 'Verify downloaded pdf file for step 2 version';
                expect(file.text === fileContent, assertionMessage).to.be.true;
              });
            });

            // export step 1 version
            cy.get('div#export-config').contains('div', 'Project version').find('mat-select').click();
            cy.contains('mat-option', 'V. 1.0').click();

            cy.contains('div#export-config button', 'Export').clickToDownload(`api/project/${applicationId}/export/application?*version=1.0`, 'pdf').then(file => {
              cy.fixture('project/exports/application-form/TB-545-exports-v1.txt').then(fileContent => {
                fileContent = replace(fileContent, applicationId, testData.application.identification.acronym);
                const assertionMessage = 'Verify downloaded pdf file for step 1 version';
                expect(file.text === fileContent, assertionMessage).to.be.true;
              });
            });
          });
        });
      });
    });
  });

  function replace(testDataFile: string, applicationId: number, acronym: string) {
    const id = String(applicationId).padStart(5, '0');
    return testDataFile
      .replace('{{acronym}}', acronym)
      .replace('{{applicationId}}', id)
      .replaceAll(/\r/g, '');
  }
});
