import user from '../../../../fixtures/users.json';
import call from '../../../../fixtures/api/call/1.step.call.json';
import call2step from '../../../../fixtures/api/call/2.step.call.json';
import application from '../../../../fixtures/api/application/application.json';
import application2step from '../../../../fixtures/api/application/2.step.application.json';
import partner from '../../../../fixtures/api/application/partner/partner.json';
import { faker } from '@faker-js/faker';

const baselinePath = "/project/exports/application-form/";

// mask that suit most cases
const comparePdfMask = [
  { pageIndex: 0, coordinates: { x0: 387, x1: 440, y0: 324, y1: 343} },
  { pageIndex: 1, coordinates: { x0:400, x1: 450, y0: 207, y1: 224} },
  { pageIndex: 1, coordinates: { x0:400, x1: 580, y0: 370, y1: 390} },
  { pageIndex: 0, coordinates: { x0:260, x1: 580, y0: 373, y1: 404} },
  { pageIndex: 0, coordinates: { x0:400, x1: 560, y0: 515, y1: 535} }
];

// mask for comparing 1st step versioned PDFs
const comparePdfMaskV1 = [
  { pageIndex: 0, coordinates: { x0: 387, x1: 440, y0: 324, y1: 343} },
  { pageIndex: 1, coordinates: { x0:400, x1: 450, y0: 207, y1: 224} },
  { pageIndex: 1, coordinates: { x0:400, x1: 580, y0: 247, y1: 267} },
  { pageIndex: 0, coordinates: { x0:260, x1: 580, y0: 373, y1: 404} },
  { pageIndex: 0, coordinates: { x0:400, x1: 560, y0: 515, y1: 535} }
];
context('Application form exports', () => {
  beforeEach(() => {
    cy.loginByRequest(user.applicantUser.email);
  });

  it('TB-366 Export application form using two sets of input and export language', () => {
    const currentMask = comparePdfMask;
    currentMask.push({pageIndex: 18, coordinates: {x0: 425, x1: 760, y0: 235, y1: 255}});
    currentMask.push({pageIndex: 15, coordinates: {x0: 425, x1: 760, y0: 681, y1: 705}});
    currentMask.push({pageIndex: 15, coordinates: {x0: 400, x1: 760, y0: 701, y1: 725}});
    currentMask.push({pageIndex: 15, coordinates: {x0: 425, x1: 760, y0: 955, y1: 980}});
    currentMask.push({pageIndex: 15, coordinates: {x0: 400, x1: 760, y0: 978, y1: 1000}});
    currentMask.push({pageIndex: 19, coordinates: {x0: 96, x1: 133, y0: 270, y1: 282}});
    currentMask.push({pageIndex: 19, coordinates: {x0: 96, x1: 133, y0: 322, y1: 333}});

    cy.createCall(call, user.programmeUser.email).then(callId => {
      application.details.projectCallId = callId;
      cy.publishCall(callId, user.programmeUser.email);
      cy.createApprovedApplication(application, user.programmeUser.email).then(applicationId => {
        cy.visit(`app/project/detail/${applicationId}/export`, {failOnStatusCode: false});

        cy.contains('div', 'Input language').find('mat-select').click();
        cy.contains('mat-option', 'Deutsch').click();

        cy.contains('button', 'Export').clickToDownload(`api/project/${applicationId}/export/application?exportLanguage=EN&inputLanguage=DE*`, 'pdf').then(file => {
          const templateFile = 'TB-366-export-template-en-de.pdf';
          cy.comparePdf(templateFile, file, currentMask, baselinePath).then(x => {
            expect(x.status === "passed").to.be.true;
          });
        });

        cy.contains('div', 'Export language').find('mat-select').click();
        cy.contains('mat-option', 'Deutsch').click();
        cy.contains('div', 'Input language').find('mat-select').click();
        cy.contains('mat-option', 'English').click();

        cy.contains('button', 'Export').clickToDownload(`api/project/${applicationId}/export/application?exportLanguage=DE&inputLanguage=EN*`, 'pdf').then(file => {
          const templateFile = 'TB-366-export-template-de-en.pdf';
          cy.comparePdf(templateFile, file, currentMask, baselinePath).then(x => {
            expect(x.status==="passed").to.be.true;
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
              const templateFile = 'TB-367-export-template-v1.pdf';
              cy.comparePdf(templateFile, file, comparePdfMaskV1, baselinePath).then(x => {
                expect(x.status==="passed").to.be.true;
              });
            });

            // export step 2 version
            cy.get('div#export-config').contains('div', 'Project version').find('mat-select').click();
            cy.contains('mat-option', 'V. 2.0').click();

            cy.contains('button', 'Export').clickToDownload(`api/project/${applicationId}/export/application?*version=2.0`, 'pdf').then(file => {
              const templateFile = 'TB-367-export-template-v2.pdf';
              const currentMask = comparePdfMask;
              currentMask.push({ pageIndex: 18, coordinates: { x0:425, x1: 760, y0: 235, y1: 255}});
              currentMask.push({ pageIndex: 15, coordinates: { x0:419, x1: 760, y0: 626, y1: 650}});
              currentMask.push({ pageIndex: 15, coordinates: { x0:400, x1: 760, y0: 646, y1: 670}});
              currentMask.push({ pageIndex: 15, coordinates: { x0:419, x1: 760, y0: 900, y1: 925}});
              currentMask.push({ pageIndex: 15, coordinates: { x0:400, x1: 760, y0: 923, y1: 945}});
              currentMask.push({ pageIndex: 19, coordinates: { x0: 96, x1: 133, y0: 270, y1: 282}});
              currentMask.push({ pageIndex: 19, coordinates: { x0: 96, x1: 133, y0: 322, y1: 333}});
              cy.comparePdf(templateFile, file, currentMask, baselinePath).then(x => {
                expect(x.status==="passed").to.be.true;
              });
            });

            // export rejected version
            cy.get('div#export-config').contains('div', 'Project version').find('mat-select').click();
            cy.contains('mat-option', 'V. 4.0').click();

            cy.contains('button', 'Export').clickToDownload(`api/project/${applicationId}/export/application?*version=4.0`, 'pdf').then(file => {
              const templateFile = 'TB-367-export-template-v4.pdf';
              const currentMask = comparePdfMask;
              currentMask.push({ pageIndex: 24, coordinates: { x0:425, x1: 760, y0: 235, y1: 255}});
              currentMask.push({ pageIndex: 21, coordinates: { x0:419, x1: 760, y0: 626, y1: 650}});
              currentMask.push({ pageIndex: 21, coordinates: { x0:400, x1: 760, y0: 646, y1: 670}});
              currentMask.push({ pageIndex: 21, coordinates: { x0:419, x1: 760, y0: 900, y1: 925}});
              currentMask.push({ pageIndex: 21, coordinates: { x0:400, x1: 760, y0: 923, y1: 945}});
              currentMask.push({ pageIndex: 25, coordinates: { x0: 96, x1: 133, y0: 270, y1: 282}});
              currentMask.push({ pageIndex: 25, coordinates: { x0: 96, x1: 133, y0: 322, y1: 333}});
              cy.comparePdf(templateFile, file, currentMask, baselinePath).then(x => {
                expect(x.status==="passed").to.be.true;
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
              const templateFile = 'TB-373-export-template-v2.pdf';
              const currentMask = comparePdfMask;
              currentMask.push({pageIndex: 11, coordinates: {x0: 430, x1: 480, y0: 855, y1: 875}});
              currentMask.push({pageIndex: 12, coordinates: {x0: 425, x1: 480, y0: 235, y1: 252}});
              currentMask.push({pageIndex: 13, coordinates: {x0: 96, x1: 136, y0: 244, y1: 256}});
              cy.comparePdf(templateFile, file, currentMask, baselinePath).then(x => {
                expect(x.status==="passed").to.be.true;
              });
            });

            // export step 1 version
            cy.get('div#export-config').contains('div', 'Project version').find('mat-select').click();
            cy.contains('mat-option', 'V. 1.0').click();

            cy.contains('div#export-config button', 'Export').clickToDownload(`api/project/${applicationId}/export/application?*version=1.0`, 'pdf').then(file => {
              const templateFile = 'TB-373-export-template-v1.pdf';
              cy.comparePdf(templateFile, file, comparePdfMaskV1, baselinePath).then(x => {
                expect(x.status==="passed").to.be.true;
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
              const templateFile = 'TB-545-export-template-v2.pdf';
              const currentMask = comparePdfMask;
              currentMask.push({pageIndex: 11, coordinates: {x0: 425, x1: 480, y0: 855, y1: 875}});
              currentMask.push({pageIndex: 12, coordinates: {x0: 425, x1: 480, y0: 235, y1: 252}});
              currentMask.push({pageIndex: 13, coordinates: {x0: 96, x1: 136, y0: 244, y1: 256}});
              cy.comparePdf(templateFile, file, currentMask, baselinePath).then(x => {
                expect(x.status==="passed").to.be.true;
              });
            });

            // export step 1 version
            cy.get('div#export-config').contains('div', 'Project version').find('mat-select').click();
            cy.contains('mat-option', 'V. 1.0').click();

            cy.contains('div#export-config button', 'Export').clickToDownload(`api/project/${applicationId}/export/application?*version=1.0`, 'pdf').then(file => {
              const templateFile = 'TB-545-export-template-v1.pdf';
              const currentMask = comparePdfMask;
              currentMask.push({pageIndex: 9, coordinates: {x0: 426, x1: 480, y0: 855, y1: 875}});
              currentMask.push({pageIndex: 10, coordinates: {x0: 425, x1: 480, y0: 235, y1: 255}});
              currentMask.push({pageIndex: 11, coordinates: {x0: 96, x1: 136, y0: 244, y1: 256}});
              cy.comparePdf(templateFile, file, currentMask, baselinePath).then(x => {
                expect(x.status==="passed").to.be.true;
              });
            });
          });
        });
      });
    });
  });

  it('TB-635 Export application form that contains special characters', () => {
    cy.fixture('project/exports/application-form/TB-635.json').then(testData => {
      cy.createCall(call, user.programmeUser.email).then(callId => {
        application.details.projectCallId = callId;
        cy.publishCall(callId, user.programmeUser.email);
        cy.createApplication(application).then(applicationId => {
          cy.updateProjectIdentification(applicationId, application.identification);
          cy.visit(`app/project/detail/${applicationId}/applicationFormIdentification`, {failOnStatusCode: false});

          cy.contains('div', 'Summary').find('textarea').then(textarea => {
            cy.wrap(textarea).clear();
            cy.wrap(textarea).type(testData.special + '{enter}');
            cy.wrap(textarea).type(testData.cyrillic + '{enter}');
            cy.wrap(textarea).type(testData.arabic + '{enter}');
            cy.wrap(textarea).type(testData.ascii, {parseSpecialCharSequences: false});
            cy.wrap(textarea).type('{enter}' + testData.generic + '{enter}');
            cy.wrap(textarea).type(testData.isoChars + '{enter}');
            cy.wrap(textarea).type(testData.isoSymbols + '{enter}');
            cy.wrap(textarea).type(testData.math + '{enter}');
            cy.wrap(textarea).type(testData.greek + '{enter}');
            cy.wrap(textarea).type(testData.miscellaneous + '{enter}');
            cy.wrap(textarea).type(testData.htmlTags + '{enter}');
            cy.wrap(textarea).type(testData.lineSeparators);
          });
          cy.contains('Save changes').click();

          cy.contains('Export').click();
          cy.contains('div', 'Input language').find('mat-select').click();
          cy.contains('mat-option', 'Deutsch').click();

          cy.contains('button', 'Export').clickToDownload(`api/project/${applicationId}/export/application?exportLanguage=EN&inputLanguage=DE*`, 'pdf').then(actualFile => {
            const templateFile = 'TB-635-export-template.pdf';
            cy.comparePdf(templateFile, actualFile, comparePdfMask, baselinePath).then(x => {
              expect(x.status==="passed").to.be.true;
            });
          });
        });
      });
    });
  });

  it('TB-391 Export to pdf shall contain SPF specific data', () => {
    cy.fixture('project/exports/application-form/TB-391.json').then(testData => {
      cy.loginByRequest(user.programmeUser.email);
      cy.createCall(testData.spfCall).then(callId => {
        cy.publishCall(callId);
        application.details.projectCallId = callId;
        cy.loginByRequest(user.applicantUser.email);
        cy.createApplication(application).then(applicationId => {
          cy.visit('/app/project/detail/'+applicationId, {failOnStatusCode: false});

          cy.contains('Partners overview').click();
          cy.contains('Add new partner').click();
          cy.contains('div.mat-form-field-flex', 'Abbreviated name of the organisation').find('input').type(testData.organisationAbbreviation);
          cy.contains('div.mat-form-field-flex', 'Legal status').click();
          cy.contains('Public').click();
          cy.contains('button', 'Create').click();
          cy.contains('a.mat-tab-link', 'Budget').click();
          cy.contains('mat-card-content', 'Partner budget - Small project funds').within(() => {
            cy.contains('button', 'Add').click();
            cy.get('div.mat-form-field-flex').first().type(faker.word.adjective() + " " + faker.word.noun());
            cy.get('div.mat-form-field-flex').last().clear().type("100");
          })
          cy.contains('button', 'Save changes').click();

          cy.contains('Export').scrollIntoView().click();
          cy.contains('button', 'Export').clickToDownload(`api/project/${applicationId}/export/application?exportLanguage=EN&inputLanguage=EN*`, 'pdf').then(actualFile => {
            const templateFile = 'TB-391-export-template.pdf';
            cy.comparePdf(templateFile, actualFile, comparePdfMask, baselinePath).then(x => {
              expect(x.status==="passed").to.be.true;
            });
          });
        });
      })
    });
  })
});
