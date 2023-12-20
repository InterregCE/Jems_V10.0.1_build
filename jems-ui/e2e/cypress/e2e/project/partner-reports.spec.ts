import {faker} from '@faker-js/faker';
import user from '../../fixtures/users.json';
import call from '../../fixtures/api/call/1.step.call.json';
import application from '../../fixtures/api/application/application.json';
import partnerReportIdentification from '../../fixtures/api/partnerReport/partnerReportIdentification.json';
import partnerReportExpenditures from '../../fixtures/api/partnerReport/partnerReportExpenditures.json';
import partnerParkedExpenditures from '../../fixtures/api/partnerReport/partnerParkedExpenditures.json';
import approvalInfo from '../../fixtures/api/application/modification/approval.info.json';
import partner from '../../fixtures/api/application/partner/partner.json';
import {partnerReportPage} from './reports-page.pom';
import controlReportIdentification from '../../fixtures/api/partnerControlReport/controlReportIdentification.json';

const costCategories = [
  'Travel and accommodation',
  'External expertise and services',
  'Equipment',
  'Infrastructure and works'
];

context('Partner reports tests', () => {

  it('TB-554 Partner report can be created in the correct status and data in the first tab is taken correctly', function () {
    cy.fixture('project/reporting/TB-554.json').then(testData => {
      cy.fixture('api/application/application.json').then(application => {
        cy.loginByRequest(user.programmeUser.email);
        call.preSubmissionCheckSettings.reportPartnerCheckPluginKey = 'report-partner-check-off';
        cy.createCall(call).then(callId => {
          application.details.projectCallId = callId;
          cy.publishCall(callId);
        });

        cy.loginByRequest(user.applicantUser.email);
        testData.partnerUser.email = faker.internet.email();
        testData.users[0].userEmail = testData.partnerUser.email;
        cy.createUser(testData.partnerUser, user.admin.email);
        const firstPartner = application.partners[0].details.abbreviation;
        const secondPartner = application.partners[1].details.abbreviation;
        cy.createApprovedApplication(application, user.programmeUser.email).then(applicationId => {
          cy.setProjectToContracted(applicationId, user.programmeUser.email);
          const partnerId = this[secondPartner];
          cy.assignPartnerCollaborators(applicationId, partnerId, testData.users);
          cy.loginByRequest(testData.partnerUser.email);
          cy.visit(`app/project/detail/${applicationId}`, {failOnStatusCode: false});

          cy.contains('div', 'Partner reports').within(() => {
            cy.contains(firstPartner).should('not.exist');
            cy.contains(secondPartner).click();
          });

          cy.contains('Add Partner Report').click();
          verifyReport(testData.firstReportInfo);

          cy.startModification(applicationId, user.programmeUser.email);
          cy.loginByRequest(user.applicantUser.email);
          cy.updatePartnerIdentity(partnerId, testData.updatedPartnerDetails);
          cy.updatePartnerAddress(partnerId, testData.updatedPartnerAddress);
          cy.updatePartnerCofinancing(partnerId, testData.updatedPartnerCofinancing);
          cy.submitProjectApplication(applicationId);
          cy.approveModification(applicationId, testData.approvalInfo, user.programmeUser.email);

          cy.loginByRequest(testData.partnerUser.email);
          cy.visit(`app/project/detail/${applicationId}/reporting/${partnerId}/reports`, {failOnStatusCode: false});
          cy.contains(testData.firstReportInfo.partnerReportId).should('be.visible');
          cy.contains(testData.secondReportInfo.partnerReportId).should('not.exist');
          cy.contains(testData.firstReportInfo.partnerReportId).click();
          verifyReport(testData.firstReportInfo);

          cy.visit(`app/project/detail/${applicationId}/reporting/${partnerId}/reports`, {failOnStatusCode: false});
          cy.contains('Add Partner Report').click();
          verifyReport(testData.secondReportInfo);
        });
      });
    });
  });

  it('TB-738 Partner user can report expenditures', function () {
    cy.fixture('project/reporting/TB-738.json').then(testData => {
      cy.fixture('api/partnerReport/partnerProcurement.json').then(partnerProcurement => {
        cy.loginByRequest(user.programmeUser.email);
        cy.createCall(call).then(callId => {
          application.details.projectCallId = callId;
          cy.publishCall(callId);

          cy.loginByRequest(user.applicantUser.email);
          cy.createContractedApplication(application, user.programmeUser.email).then(applicationId => {
            const partnerId1 = this[application.partners[0].details.abbreviation];
            const partnerId2 = this[application.partners[1].details.abbreviation];

            createControllerUser(testData, partnerId1);

            cy.loginByRequest(user.applicantUser.email);
            cy.assignPartnerCollaborators(applicationId, partnerId1, testData.partnerCollaborator);
            cy.assignPartnerCollaborators(applicationId, partnerId2, testData.partnerCollaborator);

            cy.addPartnerReport(partnerId2)
              .then(reportId => {
                cy.addPublicProcurement(partnerId2, reportId, partnerProcurement[1]);
              });

            cy.addPartnerReport(partnerId1)
              .then(reportId => {
                updatePartnerReportDetails(partnerId1, reportId, partnerProcurement);
                performControlWork(testData, reportId, partnerId1);
                openListOfExpenditures(partnerId1, applicationId);

                clickAddExpenditure();

                cy.get(`#expenditure-costs-table mat-cell.mat-column-costOptions`)
                  .click();

                verifyFastTrackLumpSumsNotOnList(applicationId);
                verifyRegularLumpSumsOnListAndAddOne(applicationId);
                verifyExpendituresFieldsEditabilityPerRow(0, false, false, true);
                saveExpenditure();

                clickAddExpenditure();

                verifyCostOptions(applicationId, callId);

                verifyExpendituresFieldsEditabilityPerRow(1, true, true, false);
                saveExpenditure();

                addRegularExpendituresForEachCostCategory();
                reincludeParkedExpenditures();
                verifyExpendituresTableSize(9);

                verifyExpendituresFieldsEditabilityPerRow(6, false, false, true);
                verifyExpendituresFieldsEditabilityPerRow(7, true, false, true);

                // Upload a file to each of the regular, lump sum, unit cost and reincluded expenditure
                uploadFiles();

                // Delete one of the uploaded files
                removeUploadedFileByRowIndex(0);

                // Delete the expenditure that contained the attachment previously
                removeExpenditureByRowIndex(0);
                verifyExpendituresTableSize(8);

                // Delete another expenditure that has no attachments
                removeExpenditureByRowIndex(2);
                verifyExpendituresTableSize(7);

                // We have to save before going for parked expenditure table modification
                saveExpenditure();

                // Delete a parked expenditure
                removeParkedExpenditureByRowIndex(0);
              });
          });
        });
      });
    });
  });

  it('TB-739 Partner user can report work plan progress', function () {
    cy.fixture('project/reporting/TB-739.json').then(testData => {
      cy.loginByRequest(user.programmeUser.email);
      cy.createCall(call).then(callId => {
        application.details.projectCallId = callId;
        cy.publishCall(callId);

        cy.loginByRequest(user.applicantUser.email);

        cy.createContractedApplication(application, user.programmeUser.email)
          .then(applicationId => {
            const partnerId2 = this[application.partners[1].details.abbreviation];

            cy.loginByRequest(user.applicantUser.email);
            cy.assignPartnerCollaborators(applicationId, partnerId2, testData.partnerCollaborator);

            // fill in workplan for a report
            cy.addPartnerReport(partnerId2)
              .then(reportId => {
                cy.visit(`/app/project/detail/${applicationId}/reporting/${partnerId2}/reports/${reportId}/workplan`, {failOnStatusCode: false});
                cy.contains('mat-panel-title', 'Work package 1')
                  .click();
                cy.contains('Please describe your contribution to the activities carried out in this reporting period.').next().within(() => {
                  testData.workplan[0].description.forEach(item => {
                    cy.contains('button', item.language)
                      .click();
                    cy.get('textarea')
                      .type(item.translation);
                  });
                });
                testData.workplan[0].activities.forEach((item, index) => {
                  cy.get(`div.activity-container > jems-multi-language-container`).eq(index + (index % 2)).within(() => {
                    item.progress.forEach(language => {
                      cy.contains('button', language.language)
                        .click();
                      cy.get('textarea')
                        .eq(1)
                        .type(language.translation);
                    });
                  });

                  cy.get(`div.activity-container > jems-multi-language-container`).eq(index + (index % 2) + 1).within(() => {
                    item.deliverables.forEach((deliverable, del_index) => {
                      cy.get(`#deliverables-table > div`).eq(del_index + 1).within(() => {
                        cy.get('mat-checkbox')
                          .click();
                      });
                    });
                  });
                });

                testData.workplan[0].outputs.forEach((output, out_index) => {
                  cy.get(`#outputs-table > div`).eq(out_index + 1).within(() => {
                    cy.get('mat-checkbox')
                      .click();
                  });
                });

                cy.contains('button', 'Save changes')
                  .click();

                cy.contains('The work package activities were saved successfully')
                  .should('be.visible');

                // upload file to activity
                cy.get('div.activity-container > jems-multi-language-container input').eq(0)
                  .scrollIntoView()
                  .invoke('show')
                  .selectFile('cypress/fixtures/project/reporting/fileToUpload.txt');

                cy.get('div.activity-container > jems-multi-language-container mat-chip-list').eq(0).within(() => {
                  cy.get('mat-chip > span')
                    .scrollIntoView()
                    .contains('fileToUpload.txt')
                    .should('be.visible');
                });

                //upload file to deliverable
                cy.get('div.activity-container > jems-multi-language-container').eq(1).within(() => {
                  cy.get(`#deliverables-table > div`).eq(1).within(() => {
                    cy.get('input')
                      .eq(1)
                      .scrollIntoView()
                      .invoke('show')
                      .selectFile('cypress/fixtures/project/reporting/fileToUpload.txt');
                  });
                });

                cy.get('div.activity-container > jems-multi-language-container').eq(1).within(() => {
                  cy.get(`#deliverables-table > div mat-chip-list`).eq(0).within(() => {
                    cy.get('mat-chip > span')
                      .scrollIntoView()
                      .contains('fileToUpload.txt')
                      .should('be.visible');
                  });
                });

                //upload file to output
                cy.get(`#outputs-table > div`).eq(1).within(() => {
                  cy.get('input')
                    .eq(1)
                    .scrollIntoView()
                    .invoke('show')
                    .selectFile('cypress/fixtures/project/reporting/fileToUpload.txt');
                });

                cy.get('#outputs-table > div mat-chip-list').eq(0).within(() => {
                  cy.get('mat-chip > span')
                    .scrollIntoView()
                    .contains('fileToUpload.txt')
                    .should('be.visible');
                });

                //delete file from activity
                cy.get('div.activity-container > jems-multi-language-container mat-chip-list').eq(0).within(() => {
                  cy.get('mat-chip')
                    .contains('mat-icon', 'cancel')
                    .scrollIntoView()
                    .click();
                });

                cy.intercept(/api\/project\/report\/partner\/byPartnerId\/[0-9]+\/byReportId\/[0-9]+\/[0-9]+/).as('deleteFileFromActivity');

                cy.contains('button', 'Confirm')
                  .should('be.visible')
                  .click();

                cy.wait('@deleteFileFromActivity');

                cy.get('div.activity-container > jems-multi-language-container input').eq(0)
                  .scrollIntoView()
                  .invoke('show')
                  .selectFile('cypress/fixtures/project/reporting/fileForUpdate.txt')
                  .invoke('hide');

                cy.wait(1000);

                cy.get('div.activity-container > jems-multi-language-container mat-chip-list').eq(0).within(() => {
                  cy.get('mat-chip > span')
                    .scrollIntoView()
                    .contains('fileForUpdate.txt')
                    .should('be.visible');
                });

                cy.get('div.activity-container > jems-multi-language-container mat-chip-list').eq(0).within(() => {
                  cy.get('mat-chip > span')
                    .scrollIntoView()
                    .contains('fileToUpload.txt')
                    .should('not.exist');
                });
              });
          });
      });
    });
  });

  it('TB-740 Partner user can report public procurements', function () {
    cy.fixture('project/reporting/TB-740.json').then(testData => {
      cy.fixture('api/partnerReport/partnerProcurement.json').then(partnerProcurement => {
        cy.loginByRequest(user.programmeUser.email);
        cy.createCall(call).then(callId => {
          application.details.projectCallId = callId;
          cy.publishCall(callId);

          cy.loginByRequest(user.applicantUser.email);

          // update second partner address to the non-EU address
          application.partners[1].address = testData.address;

          cy.createContractedApplication(application, user.programmeUser.email)
            .then(applicationId => {
              const partnerId1 = this[application.partners[0].details.abbreviation];
              const partnerId2 = this[application.partners[1].details.abbreviation];

              cy.loginByRequest(user.applicantUser.email);
              cy.assignPartnerCollaborators(applicationId, partnerId1, testData.partnerCollaborator);
              cy.assignPartnerCollaborators(applicationId, partnerId2, testData.partnerCollaborator);

              cy.addPartnerReport(partnerId2)
                .then(reportId => {
                  cy.addPublicProcurement(partnerId2, reportId, partnerProcurement[1])
                    .then(procurement => {
                      cy.visit(`/app/project/detail/${applicationId}/reporting/${partnerId2}/reports/${reportId}/procurements/${procurement.id}`, {failOnStatusCode: false});

                      // Partners with other currency than Euro (or no currency at all) can select any currency for their procurement
                      cy.get('#currency')
                        .click();
                      cy.contains('mat-option', 'AMD')
                        .click();
                      cy.contains('button', 'Save changes')
                        .click();
                      cy.contains('Procurement saved successfully')
                        .should('be.visible');

                      // Any partner can select any currency for subcontracts
                      addSubcontractors(testData);
                    });
                });

              cy.addPartnerReport(partnerId1)
                .then(reportId => {
                  cy.addPublicProcurement(partnerId1, reportId, partnerProcurement[0])
                    .then(procurement => {
                      cy.updatePartnerReportIdentification(partnerId1, reportId, partnerReportIdentification);
                      cy.updatePartnerReportExpenditures(partnerId1, reportId, partnerReportExpenditures)
                        .then(response => {
                          for (let i = 0; i < response.length; i++) {
                            partnerParkedExpenditures[i].id = response[i].id;
                          }
                        });
                      // add beneficial owner, subcontractor and attachment description
                      cy.addBeneficialOwnerToProcurement(partnerId1, reportId, procurement.id, testData.beneficialOwners[0]);
                      cy.addSubcontractorToProcurement(partnerId1, reportId, procurement.id, testData.subcontracts[0]);
                      cy.addAttachmentToProcurement('fileToUpload.txt', 'project/reporting/', partnerId1, reportId, procurement.id);
                      cy.runPreSubmissionPartnerReportCheck(partnerId1, reportId);
                      cy.submitPartnerReport(partnerId1, reportId);
                    });
                });

              cy.addPartnerReport(partnerId1)
                .then(reportId => {
                  const partnerProcurmentNameFromPreviousReport = partnerProcurement[0].contractName;
                  partnerProcurement[0].contractName += " FOR REMOVAL";
                  cy.addPublicProcurement(partnerId1, reportId, partnerProcurement[0])
                    .then(procurement => {
                      cy.visit(`/app/project/detail/${applicationId}/reporting/${partnerId1}/reports/${reportId}/procurements`, {failOnStatusCode: false});

                      // New procurement with same name as an already existing one can't be created
                      cy.contains('mat-icon', 'add')
                        .click();
                      cy.get('input[name="contractName"]')
                        .type(partnerProcurement[0].contractName);
                      cy.get('input[name="vatNumber"]')
                        .type(partnerProcurement[0].vatNumber);
                      cy.contains('button', 'Create')
                        .click();
                      cy.contains(`Procurement contract name [${partnerProcurement[0].contractName}] is not unique (error code: S-CPPRP-003)`)
                        .should('be.visible');

                      // Add 2 new beneficial owner
                      cy.visit(`/app/project/detail/${applicationId}/reporting/${partnerId1}/reports/${reportId}/procurements/${procurement.id}`, {failOnStatusCode: false});

                      cy.contains('button', 'Add beneficial owner')
                        .scrollIntoView()
                        .click();

                      cy.get('[formarrayname="beneficialOwners"] [formcontrolname="firstName"]')
                        .eq(0)
                        .type(testData.beneficialOwners[0].firstName);

                      cy.get('[formarrayname="beneficialOwners"] [formcontrolname="lastName"]')
                        .eq(0)
                        .type(testData.beneficialOwners[0].lastName);

                      cy.get('[formarrayname="beneficialOwners"] [formcontrolname="birth"]')
                        .eq(0)
                        .type("03/09/1984"); // used hardcoded value due to date locale formatting

                      cy.get('[formarrayname="beneficialOwners"] [formcontrolname="vatNumber"]')
                        .eq(0)
                        .type(testData.beneficialOwners[0].vatNumber);

                      cy.contains('Add beneficial owner')
                        .click();

                      cy.get('[formarrayname="beneficialOwners"] [formcontrolname="firstName"]')
                        .eq(1)
                        .type(testData.beneficialOwners[1].firstName);

                      cy.get('[formarrayname="beneficialOwners"] [formcontrolname="lastName"]')
                        .eq(1)
                        .type(testData.beneficialOwners[1].lastName);

                      cy.get('[formarrayname="beneficialOwners"] [formcontrolname="birth"]')
                        .eq(1)
                        .type("03/09/1964"); // used hardcoded value due to date locale formatting

                      cy.get('[formarrayname="beneficialOwners"] [formcontrolname="vatNumber"]')
                        .eq(1)
                        .type(testData.beneficialOwners[1].vatNumber);

                      cy.contains('button', 'Save changes')
                        .click();

                      cy.contains('Beneficial owner(s) saved successfully')
                        .should('be.visible');

                      // and delete the second one
                      cy.get('[formarrayname="beneficialOwners"]')
                        .contains('mat-icon', 'delete')
                        .click();

                      cy.contains('button', 'Save changes')
                        .click();

                      cy.contains('Beneficial owner(s) saved successfully')
                        .should('be.visible');

                      // Add 2 new subcontracts
                      addSubcontractors(testData);

                      // and delete the second one
                      cy.get('[formarrayname="subcontracts"]')
                        .contains('mat-icon', 'delete')
                        .click();

                      cy.contains('button', 'Save changes')
                        .click();

                      cy.contains('Subcontract(s) saved successfully')
                        .should('be.visible');

                      // Upload an attachment, then changes its description and delete the attachment
                      // file can be uploaded
                      cy.get('jems-partner-procurement-attachment input')
                        .scrollIntoView()
                        .invoke('show')
                        .selectFile('cypress/fixtures/project/reporting/fileToUpload.txt')
                        .invoke('hide');

                      // description can be changed
                      cy.get('jems-file-list-table')
                        .contains('mat-icon', 'edit')
                        .scrollIntoView()
                        .click();

                      cy.get('[label="file.table.column.name.description"] textarea')
                        .type('Description test 1 for the attachment');

                      cy.contains('button', 'Save').should('be.visible').click();

                      cy.contains('File description for \'fileToUpload.txt\' has been updated.')
                        .should('be.visible');

                      cy.contains('File description for \'fileToUpload.txt\' has been updated.')
                        .should('not.exist');

                      // file can be deleted
                      cy.get('jems-file-list-table')
                        .contains('mat-icon', 'delete')
                        .scrollIntoView()
                        .click();

                      cy.contains('button', 'Confirm').should('be.visible')
                        .click();

                      cy.contains("File \'fileToUpload.txt\' has been deleted successfully.")
                        .should('be.visible');

                      cy.contains('There are no files uploaded.')
                        .should('be.visible');

                      cy.contains('File \'fileToUpload.txt\' has been deleted successfully.')
                        .should('not.exist');

                      // Navigate back to the public procurements list
                      cy.contains('mat-icon', 'arrow_circle_left')
                        .click();

                      // Created public procurement is visible in the list
                      cy.contains(partnerProcurement[0].contractName).should('be.visible');

                      // Go to List of expenditure (LoE) and link an expenditure item with the procurement
                      // The new procurement shown in dropdown and can be linked to an expenditure item
                      cy.contains('List of expenditures')
                        .click();

                      cy.contains('add expenditure')
                        .click();

                      cy.contains('#expenditure-costs-table mat-select', 'Please select a cost category')
                        .click();

                      cy.contains('mat-option', 'Travel and accommodation')
                        .click();

                      cy.get(`#expenditure-costs-table mat-cell.mat-column-contractId`)
                        .scrollIntoView()
                        .click();

                      cy.contains('mat-option', 'LP1 - Very important procurement FOR REMOVAL')
                        .click();

                      cy.contains('Save changes').should('be.visible')
                        .click();

                      // Delete the procurement
                      cy.contains('Public procurements')
                        .click();

                      // The linked procurement cannot be deleted
                      cy.get('mat-row')
                        .eq(1)
                        .contains('mat-icon', 'delete')
                        .should('not.exist');

                      cy.get('mat-row')
                        .eq(0)
                        .contains('mat-icon', 'delete')
                        .should('be.visible')
                        .click();
                      cy.contains('Confirm').should('be.visible')
                        .click();
                      
                      cy.contains('Procurement cannot be deleted, because it is used in a report in the list of expenditure or in a Correction. Please remove the link before trying to delete this procurement.')
                        .should('be.visible');

                      // unlink the procurement
                      cy.contains('List of expenditures')
                        .click();

                      cy.get(`#expenditure-costs-table mat-cell.mat-column-contractId`)
                        .scrollIntoView()
                        .click();

                      cy.contains('mat-option', 'N/A')
                        .click();

                      cy.contains('Save changes').should('be.visible')
                        .click();
                      cy.contains('Report expenditure costs were saved successfully')
                        .should('be.visible');

                      // The unlinked procurement can finally be deleted
                      cy.contains('Public procurements')
                        .click();
                      cy.get('mat-row')
                        .eq(0)
                        .contains('mat-icon', 'delete')
                        .should('be.visible')
                        .click();
                      cy.contains('Confirm').should('be.visible')
                        .click();
                      cy.contains('LP1 - Very important procurement FOR REMOVAL')
                        .should('not.exist');

                      // Open a procurement created in previous report
                      // Info, beneficial owner, subcontractor and attachment description for a procurement
                      // created in a previous report can't be edited
                      cy.contains(partnerProcurmentNameFromPreviousReport)
                        .click();

                      // info validation
                      cy.get('jems-partner-procurement-identification input[name="reportNumber"]')
                        .should('be.disabled');

                      cy.get('jems-partner-procurement-identification input[name="contractName"]')
                        .should('be.disabled');

                      cy.get('jems-partner-procurement-identification input[name="referenceNumber"]')
                        .should('be.disabled');

                      cy.get('jems-partner-procurement-identification input[name="contractDate"]')
                        .should('be.disabled');

                      cy.get('jems-partner-procurement-identification input[name="contractType"]')
                        .should('be.disabled');

                      cy.get('jems-partner-procurement-identification input[name="contractAmount"]')
                        .should('be.disabled');

                      cy.get('mat-select[formcontrolname="currencyCode"]')
                        .should('have.class', 'mat-select-disabled');

                      cy.get('jems-partner-procurement-identification input[name="supplierName"]')
                        .should('be.disabled');

                      cy.get('jems-partner-procurement-identification input[name="vatNumber"]')
                        .should('be.disabled');

                      cy.get('[label="project.application.partner.report.procurements.table.comment"] textarea')
                        .should('be.disabled');

                      // beneficial owners validation
                      cy.get('jems-partner-procurement-beneficial .mat-column-firstName input')
                        .should('not.exist');

                      cy.get('jems-partner-procurement-beneficial .mat-column-lastName input')
                        .should('not.exist');

                      cy.get('jems-partner-procurement-beneficial .mat-column-birth input')
                        .should('not.exist');

                      cy.get('jems-partner-procurement-beneficial .mat-column-vatNumber input')
                        .should('not.exist');

                      cy.get('jems-partner-procurement-beneficial .mat-column-vatNumber input')
                        .should('not.exist');

                      // subcontractors validation
                      cy.get('jems-partner-procurement-subcontract .mat-column-contractName input')
                        .should('not.exist');

                      cy.get('jems-partner-procurement-subcontract .mat-column-referenceNumber input')
                        .should('not.exist');

                      cy.get('jems-partner-procurement-subcontract .mat-column-contractDate input')
                        .should('not.exist');

                      cy.get('jems-partner-procurement-subcontract .mat-column-contractAmount input')
                        .should('not.exist');

                      cy.get('jems-partner-procurement-subcontract .mat-column-currencyCode mat-select')
                        .should('not.exist');

                      cy.get('jems-partner-procurement-subcontract .mat-column-supplierName input')
                        .should('not.exist');

                      cy.get('jems-partner-procurement-subcontract .mat-column-vatNumber input')
                        .should('not.exist');

                      cy.get('jems-partner-procurement-attachment .mat-column-description mat-icon')
                        .should('not.exist');

                      // new beneficial owner can be added/deleted
                      // add beneficial owner
                      cy.contains('button', 'Add beneficial owner')
                        .scrollIntoView()
                        .click();

                      cy.get('[formarrayname="beneficialOwners"] [formcontrolname="firstName"]')
                        .eq(0)
                        .type(testData.beneficialOwners[0].firstName);

                      cy.get('[formarrayname="beneficialOwners"] [formcontrolname="lastName"]')
                        .eq(0)
                        .type(testData.beneficialOwners[0].lastName);

                      cy.get('[formarrayname="beneficialOwners"] [formcontrolname="birth"]')
                        .eq(0)
                        .type("03/09/1984"); // used hardcoded value due to date locale formatting

                      cy.get('[formarrayname="beneficialOwners"] [formcontrolname="vatNumber"]')
                        .eq(0)
                        .type(testData.beneficialOwners[0].vatNumber);

                      cy.contains('button', 'Save changes')
                        .click();

                      cy.contains('Beneficial owner(s) saved successfully')
                        .should('be.visible');

                      // delete beneficial owner
                      cy.get('[formarrayname="beneficialOwners"]')
                        .contains('mat-icon', 'delete')
                        .click();

                      cy.contains('button', 'Save changes')
                        .click();

                      cy.contains('Beneficial owner(s) saved successfully')
                        .should('be.visible');

                      // new subcontractor can be added/deleted
                      addSubcontractors(testData);

                      // delete the second subcontractor
                      cy.get('[formarrayname="subcontracts"]')
                        .contains('mat-icon', 'delete')
                        .click();

                      cy.contains('button', 'Save changes')
                        .click();

                      cy.contains('Subcontract(s) saved successfully')
                        .should('be.visible');

                      // new attachment can be added/deleted
                      cy.get('jems-partner-procurement-attachment input')
                        .scrollIntoView()
                        .invoke('show')
                        .selectFile('cypress/fixtures/project/reporting/fileToUpload.txt')
                        .invoke('hide');

                      // description can be changed
                      cy.get('jems-file-list-table')
                        .contains('mat-icon', 'edit')
                        .scrollIntoView()
                        .click();

                      cy.get('[label="file.table.column.name.description"] textarea')
                        .type('Description test 2 for the attachment');

                      cy.contains('button', 'Save').should('be.visible')
                        .click();

                      cy.contains('File description for \'fileToUpload.txt\' has been updated.')
                        .should('exist');

                      cy.contains('File description for \'fileToUpload.txt\' has been updated.')
                        .should('not.exist');

                      // file can be deleted
                      cy.get('jems-file-list-table')
                        .contains('mat-icon', 'delete')
                        .scrollIntoView()
                        .click();

                      cy.contains('button', 'Confirm').should('be.visible')
                        .click();

                      cy.contains("File \'fileToUpload.txt\' has been deleted successfully.")
                        .should('be.visible');
                    });
                });
            });
        });
      });
    });
  });

  it('TB-745 Partner user can deactivate multiple partners and changes are displayed only after approval', function () {
    cy.fixture('project/reporting/TB-745.json').then(testData => {
      cy.fixture('api/application/application.json').then(application => {
        cy.loginByRequest(user.programmeUser.email);
        cy.createCall(call).then(callId => {
          application.details.projectCallId = callId;
          cy.publishCall(callId);
        });

        prepareTestData(testData, application);

        cy.createApprovedApplication(application, user.programmeUser.email)
          .then(applicationId => {
            const partnerIndexesToDisable = [2, 3];

            openModification(applicationId);
            disableSelectedPartners(application, applicationId, partnerIndexesToDisable);
            verifyPartnerChangesBeforeApproving(application, applicationId, partnerIndexesToDisable);
            cy.approveModification(applicationId, approvalInfo, user.programmeUser.email);

            cy.loginByRequest(user.admin.email).then(() => {
              cy.visit(`app/project/detail/${applicationId}`, {failOnStatusCode: false})
                .then(() => {
                  partnerIndexesToDisable.forEach(id => {
                    cy.get('mat-expansion-panel-header:contains("Partner details")')
                      .next('div')
                      .find(`li:contains("${application.partners[id].details.abbreviation}")`)
                      .contains('mat-icon', 'person_off')
                      .should('exist');
                  });

                  verifyIconsInProjectPrivileges(application, partnerIndexesToDisable, true);
                });
            });

            openModification(applicationId);
            cy.loginByRequest(user.applicantUser.email).then(() => {
              cy.createFullPartner(applicationId, partner);
              submitProjectApp(applicationId);
            });

            cy.loginByRequest(user.admin.email).then(() => {
              verifyPartnerAvailability(applicationId, false);
            });

            cy.approveModification(applicationId, approvalInfo, user.programmeUser.email);

            cy.loginByRequest(user.admin.email).then(() => {
              verifyPartnerAvailability(applicationId, true);
            });

            openModification(applicationId);
            verify50PartnersLimit(applicationId);
          });
      });
    });
  });

  it('TB-840 Partner Reporting cost category overview table', function () {
    cy.fixture('project/reporting/TB-840.json').then(testData => {
      cy.fixture('api/application/application.json').then(application => {
        cy.loginByRequest(user.programmeUser.email);
        call.preSubmissionCheckSettings.reportPartnerCheckPluginKey = 'report-partner-check-off';
        cy.createCall(call).then(callId => {
          application.details.projectCallId = callId;
          cy.publishCall(callId);
        });

        cy.loginByRequest(user.admin.email);
        testData.partnerUser1.email = faker.internet.email();
        testData.partnerUser2.email = faker.internet.email();
        testData.partnerUser3.email = faker.internet.email();
        cy.createUser(testData.partnerUser1, user.admin.email);
        cy.createUser(testData.partnerUser2, user.admin.email);
        cy.createUser(testData.partnerUser3, user.admin.email);

        partner.details.abbreviation = 'Another partner';
        application.partners.push(partner);
        const firstPartner = application.partners[0].details.abbreviation;
        const secondPartner = application.partners[1].details.abbreviation;
        const thirdPartner = application.partners[2].details.abbreviation;

        cy.loginByRequest(user.applicantUser.email);
        cy.createApprovedApplication(application, user.programmeUser.email).then(applicationId => {
          const firstPartnerId = this[firstPartner];
          const secondPartnerId = this[secondPartner];
          const thirdPartnerId = this[thirdPartner];
          testData.firstUser[0].userEmail = testData.partnerUser1.email;
          cy.assignPartnerCollaborators(applicationId, firstPartnerId, testData.firstUser);
          testData.secondUser[0].userEmail = testData.partnerUser2.email;
          cy.assignPartnerCollaborators(applicationId, secondPartnerId, testData.secondUser);
          testData.thirdUser[0].userEmail = testData.partnerUser3.email;
          cy.assignPartnerCollaborators(applicationId, thirdPartnerId, testData.thirdUser);
          cy.setProjectToContracted(applicationId, user.programmeUser.email);

          cy.loginByRequest(testData.partnerUser1.email);
          cy.visit(`app/project/detail/${applicationId}`, {failOnStatusCode: false});
          cy.contains('div', 'Partner reports').within(() => {
            cy.contains(firstPartner).click();
          });
          cy.contains('Add Partner Report').click();
          cy.contains('Financial overview').click({force: true});
          cy.get('jems-partner-breakdown-cost-category').scrollIntoView().contains('Lump sum').should('be.visible');
          cy.get('jems-partner-breakdown-cost-category').scrollIntoView().contains('Other costs').should('be.visible');
          cy.get('jems-partner-breakdown-cost-category').scrollIntoView().contains('Unit Costs').should('be.visible');

          cy.loginByRequest(testData.partnerUser2.email);
          cy.visit(`app/project/detail/${applicationId}`, {failOnStatusCode: false});
          cy.contains('div', 'Partner reports').within(() => {
            cy.contains(secondPartner).click();
          });
          cy.contains('Add Partner Report').click();
          cy.contains('Financial overview').click({force: true});
          cy.get('jems-partner-breakdown-cost-category').scrollIntoView().contains('Lump sum').should('be.visible');
          cy.get('jems-partner-breakdown-cost-category').scrollIntoView().contains('Other costs').should('be.visible');
          cy.get('jems-partner-breakdown-cost-category').scrollIntoView().contains('Unit Costs').should('be.visible');

          cy.loginByRequest(testData.partnerUser3.email);
          cy.visit(`app/project/detail/${applicationId}`, {failOnStatusCode: false});
          cy.contains('div', 'Partner reports').within(() => {
            cy.contains(thirdPartner).click();
          });
          cy.contains('Add Partner Report').click();
          cy.contains('Financial overview').click({force: true});
          cy.get('jems-partner-breakdown-cost-category').scrollIntoView().contains('Lump sum').should('be.visible');
          cy.get('jems-partner-breakdown-cost-category').scrollIntoView().contains('Other costs').should('be.visible');
          cy.get('jems-partner-breakdown-cost-category').scrollIntoView().contains('Unit Costs').should('be.visible');

          const declaredAmount = 20;
          const declaredAmountFormatted = new Intl.NumberFormat('de-DE').format(declaredAmount);
          createCostAsExpenditure(declaredAmountFormatted);

          cy.contains('Financial overview').click({force: true});
          const declaredAmountWithFlatRateValue = (declaredAmount * partner.budget.options.otherCostsOnStaffCostsFlatRate) / 100;
          const declaredAmountWithFlatRateFormatted = new Intl.NumberFormat('de-DE').format(declaredAmountWithFlatRateValue);
          cy.get('jems-partner-breakdown-cost-category').contains('Staff costs').parent().should('contain', declaredAmountFormatted);
          cy.get('jems-partner-breakdown-cost-category').contains('Other costs').parent().should('contain', declaredAmountWithFlatRateFormatted);

          cy.loginByRequest(testData.partnerUser2.email);
          cy.visit(`app/project/detail/${applicationId}/reporting/${secondPartnerId}/reports`, {failOnStatusCode: false});
          cy.contains('R.1').click();
          createLumpSumAsExpenditure();

          cy.contains('Financial overview').click({force: true});
          const lumpSumFormatted = new Intl.NumberFormat('de-DE').format(application.lumpSums[1].lumpSumContributions[1].amount);
          cy.get('jems-partner-breakdown-cost-category').contains('Lump sum').parent().should('contain', lumpSumFormatted);

          cy.loginByRequest(testData.partnerUser1.email);
          cy.visit(`app/project/detail/${applicationId}/reporting/${firstPartnerId}/reports`, {failOnStatusCode: false});
          cy.contains('R.1').click();
          createUnitCostsAsExpenditures();

          cy.contains('Financial overview').click({force: true});
          const unitCostExternalFormatted = new Intl.NumberFormat('de-DE').format(application.partners[0].budget.external[1].pricePerUnit);
          const unitCostsFormatted = new Intl.NumberFormat('de-DE').format(application.partners[0].budget.unit[0].rowSum / 2);
          cy.get('jems-partner-breakdown-cost-category').contains('External expertise and services').parent().should('contain', unitCostExternalFormatted);
          cy.get('jems-partner-breakdown-cost-category').contains('Unit Costs').parent().should('contain', unitCostsFormatted);

          submitPartnerReport();
          cy.visit(`app/project/detail/${applicationId}/reporting/${firstPartnerId}/reports`, {failOnStatusCode: false});
          cy.contains('Add Partner Report').click();
          createUnitCostsAsExpenditures();

          cy.contains('Financial overview').click({force: true});
          cy.get('mat-row:contains("External expertise and services") .mat-column-previouslyReported').should('contain', unitCostExternalFormatted);
          cy.get('mat-row:contains("External expertise and services") .mat-column-currentReport').should('contain', unitCostExternalFormatted);

          cy.loginByRequest(testData.partnerUser2.email);
          cy.visit(`app/project/detail/${applicationId}/reporting/${secondPartnerId}/reports`, {failOnStatusCode: false});
          cy.contains('R.1').click();
          submitPartnerReport();
          cy.visit(`app/project/detail/${applicationId}/reporting/${secondPartnerId}/reports`, {failOnStatusCode: false});
          cy.contains('Add Partner Report').click();
          createLumpSumAsExpenditure();

          cy.contains('Financial overview').click({force: true});
          cy.get('mat-row:contains("Lump sum") .mat-column-previouslyReported').should('contain', lumpSumFormatted);
          cy.get('mat-row:contains("Lump sum") .mat-column-currentReport').should('contain', lumpSumFormatted);

          cy.loginByRequest(testData.partnerUser3.email);
          cy.visit(`app/project/detail/${applicationId}/reporting/${thirdPartnerId}/reports`, {failOnStatusCode: false});
          cy.contains('R.1').click();
          submitPartnerReport();
          cy.visit(`app/project/detail/${applicationId}/reporting/${thirdPartnerId}/reports`, {failOnStatusCode: false});
          cy.contains('Add Partner Report').click();
          createCostAsExpenditure(declaredAmountFormatted);

          cy.contains('Financial overview').click({force: true});
          cy.get('mat-row:contains("Staff costs") .mat-column-previouslyReported').should('contain', declaredAmountFormatted);
          cy.get('mat-row:contains("Staff costs") .mat-column-currentReport').should('contain', declaredAmountFormatted);
          cy.get('mat-row:contains("Other costs") .mat-column-previouslyReported').should('contain', declaredAmountWithFlatRateFormatted);
          cy.get('mat-row:contains("Other costs") .mat-column-currentReport').should('contain', declaredAmountWithFlatRateFormatted);
        });
      });
    });
  });

  it('TB-741 Partner user can report contributions', function () {
    cy.fixture('project/reporting/TB-741.json').then(testData => {
      cy.fixture('api/application/application.json').then(application => {

        cy.loginByRequest(user.programmeUser.email);
        cy.createCall(call).then(callId => {
          application.details.projectCallId = callId;
          cy.publishCall(callId);
        });

        cy.loginByRequest(user.admin.email);
        testData.partnerUser1.email = faker.internet.email();
        cy.createUser(testData.partnerUser1, user.admin.email);
        const leadPartnerAbbreviation = application.partners[0].details.abbreviation;

        cy.createApprovedApplication(application, user.programmeUser.email).then(applicationId => {
          const leadPartnerId = this[leadPartnerAbbreviation];
          const currentlyReportedInput = 'input[name="currentlyReported"]';
          const sourceOfContributionInput = 'input[name="sourceOfContribution"]';

          const firstReportContributionAmounts = testData.reportContributionData[0].amounts;
          const firstReportCurrentContributionSubTotals = testData.reportContributionData[0].subTotals;

          const secondReportContributionAmounts = testData.reportContributionData[1].amounts;

          const thirdReportContributionAmounts = testData.reportContributionData[2].amounts;
          const thirdReportCurrentContributionSubTotals = testData.reportContributionData[2].subTotals;


          testData.firstUser[0].userEmail = testData.partnerUser1.email;
          cy.assignPartnerCollaborators(applicationId, leadPartnerId, testData.firstUser);
          cy.setProjectToContracted(applicationId, user.programmeUser.email);

          cy.loginByRequest(testData.partnerUser1.email);

          cy.addPartnerReport(leadPartnerId).then(reportId => {
            cy.visit(`app/project/detail/${applicationId}/reporting/${leadPartnerId}/reports/${reportId}/identification`, {failOnStatusCode: false});
            cy.contains('Contributions').click({force: true});

            cy.get('[id=contributions-table] mat-row').each((row, index) => {
              expect(row.find(sourceOfContributionInput)).to.have.value(application.partners[0].cofinancing.partnerContributions[index].name);
              expect(row.find(currentlyReportedInput)).to.exist;
            });

            cy.get('[id=contributions-table]').children().eq(1).find(currentlyReportedInput).type(formatAmount(firstReportContributionAmounts[0].currentReport));
            cy.get('[id=contributions-table]').children().eq(2).find(currentlyReportedInput).type(formatAmount(firstReportContributionAmounts[1].currentReport));
            cy.get('[id=contributions-table]').children().eq(3).find(currentlyReportedInput).type(formatAmount(firstReportContributionAmounts[2].currentReport));

            cy.contains('add').click();

            cy.get('[id=contributions-table]').children().last().within(() => {
              cy.contains('mat-form-field', 'Source of contribution').type("Lead contribution report 1");
              cy.contains('mat-select', 'Legal status').click();
              cy.root().closest('body').find('mat-option').contains('Private').click();
              cy.get('input').eq(1).type(formatAmount(firstReportContributionAmounts[3].currentReport));
            });

            cy.contains('add').click();

            cy.get('[id=contributions-table]').children().last().within(() => {
              cy.contains('mat-form-field', 'Source of contribution').type("Lead contribution report 2");
              cy.contains('mat-select', 'Legal status').click();
              cy.root().closest('body').find('mat-option').contains('Automatic Public').click();
              cy.get('input').eq(1).type(formatAmount(firstReportContributionAmounts[4].currentReport));
            });

            cy.contains('button', 'Save changes').click();
            cy.contains('div', 'Report contribution has been saved successfully').should('be.visible');

            cy.get('[id=contributions-table]').children().eq(1).find('input[type=file]').selectFile(`cypress/fixtures/project/reporting/partner-report-attachment01.txt`, {force: true});
            cy.get('[id=contributions-table]').children().eq(5).find('input[type=file]').selectFile(`cypress/fixtures/project/reporting/partner-report-attachment01.txt`, {force: true});

            cy.get('[id=contributions-table]').children().eq(1).within(() => {
              cy.contains('mat-icon', 'cancel').click({force: true});
            });
            cy.contains('Confirm').should('be.visible').click();


            cy.get('[id=contributions-table]').children().last().within(() => {
              cy.contains('button', 'delete').should('be.disabled');
              cy.contains('mat-icon', 'cancel').click({force: true});
            });
            cy.contains('Confirm').should('be.visible').click();


            cy.get('[id=contributions-table]').children().last().within(() => {
              cy.contains('button', 'delete').should('be.enabled').click();
            });

            cy.contains('button', 'Save changes').click();

            cy.contains('div', 'Sub-total public contribution').children().eq(4).then(value => {
              expect(value).to.contain(formatAmount(firstReportCurrentContributionSubTotals.subTotalPublic.currentReport));
            });

            cy.contains('div', 'Sub-total automatic public contribution').parent().children().eq(4).then(value => {
              expect(value).to.contain(formatAmount(firstReportCurrentContributionSubTotals.subTotalAutomaticPublic.currentReport));
            });

            cy.contains('div', 'Sub-total private contribution').parent().children().eq(4).then(value => {
              expect(value).to.contain(formatAmount(firstReportCurrentContributionSubTotals.subTotalPrivate.currentReport));
            });
            cy.runPreSubmissionPartnerReportCheck(leadPartnerId, reportId);
            cy.submitPartnerReport(leadPartnerId, reportId);
          });

          cy.addPartnerReport(leadPartnerId).then(reportId => {
            cy.wrap(reportId).as('idOfSecondReport');
            cy.visit(`app/project/detail/${applicationId}/reporting/${leadPartnerId}/reports/${reportId}/identification`, {failOnStatusCode: false});
            cy.contains('Contributions').click({force: true});
          });

          cy.startModification(applicationId, user.programmeUser.email);
          cy.loginByRequest(user.admin.email);
          cy.visit(`app/project/detail/${applicationId}/applicationFormPartner/${leadPartnerId}/coFinancing`, {failOnStatusCode: false});


          cy.contains('Source of contribution').parent().parent().children().last().within(_ => {
            cy.get('input').eq(1).type(formatAmount(testData.applicationFormNewContributionData.existingContributionNewAmount));
          });

          cy.contains('Add new contribution origin').click();
          cy.contains('Source of contribution').parent().parent().children().last().within(() => {
            cy.contains('mat-form-field', 'Source of contribution').find('input').type(testData.cofinancing.newPartnerContribution.name);
            cy.contains('Legal status').click();
            cy.root().closest('body').find('mat-option').contains("Automatic Public").click();
            cy.get('input').eq(1).type(formatAmount(testData.applicationFormNewContributionData.newContributionAmount));
          });


          const alertMessage = 'The total of contribution must match the total partner contribution';
          cy.contains(alertMessage).should('not.exist');
          cy.contains('Save changes').click();
          cy.contains('Co-financing and partner contributions saved successfully').should('be.visible');

          cy.submitProjectApplication(applicationId);
          cy.approveModification(applicationId, approvalInfo, user.programmeUser.email);
          cy.loginByRequest(testData.partnerUser1.email);


          cy.get('@idOfSecondReport').then(reportId => {
            cy.visit(`app/project/detail/${applicationId}/reporting/${leadPartnerId}/reports/${reportId}/identification`, {failOnStatusCode: false});
            cy.contains('Contributions').click({force: true});

            cy.contains(testData.cofinancing.newPartnerContribution.name).should('not.exist');

            cy.contains('Source of contribution').parent().parent().parent().then(table => {
              cy.wrap(table).children().eq(1).find(currentlyReportedInput).type(formatAmount(secondReportContributionAmounts[0].currentReport));
              cy.wrap(table).children().eq(2).find(currentlyReportedInput).type(formatAmount(secondReportContributionAmounts[1].currentReport));
              cy.wrap(table).children().eq(3).find(currentlyReportedInput).type(formatAmount(secondReportContributionAmounts[2].currentReport));
              cy.wrap(table).children().eq(4).find(currentlyReportedInput).type(formatAmount(secondReportContributionAmounts[3].currentReport));

              cy.contains('button', 'Save changes').click();
              cy.contains('div', 'Report contribution has been saved successfully').should('be.visible');
              cy.submitPartnerReport(leadPartnerId, Number(reportId));
            });
          });

          //TB-741 step 10
          cy.addPartnerReport(leadPartnerId).then(reportId => {
            cy.visit(`app/project/detail/${applicationId}/reporting/${leadPartnerId}/reports/${reportId}/identification`, {failOnStatusCode: false});
            cy.contains('Contributions').click({force: true});

            cy.get('[id=contributions-table] mat-row').then((rows) => {
              cy.wrap(rows.get(0)).find(currentlyReportedInput).type(formatAmount(thirdReportContributionAmounts[0].currentReport));
              cy.wrap(rows.get(1)).find(currentlyReportedInput).type(formatAmount(thirdReportContributionAmounts[1].currentReport));
              cy.wrap(rows.get(2)).find(currentlyReportedInput).type(formatAmount(thirdReportContributionAmounts[2].currentReport));
              cy.wrap(rows.get(3)).find(currentlyReportedInput).type(formatAmount(thirdReportContributionAmounts[3].currentReport));
              cy.wrap(rows.get(4)).find(currentlyReportedInput).type(formatAmount(thirdReportContributionAmounts[4].currentReport));
            });

            const previouslyReportedColumnIndex = 3;
            const currentReportColumnIndex = 4;
            const totalReportedSoFarColumnIndex = 5;
            cy.get('[id=contributions-table] mat-row').then((rows) => {

              cy.wrap(rows.get(2)).should('contain', formatAmount(testData.applicationFormNewContributionData.existingContributionNewAmount));

              cy.wrap(rows.get(3)).find(sourceOfContributionInput).should('have.value', testData.cofinancing.newPartnerContribution.name);
              cy.wrap(rows.get(3)).should('contain', formatAmount(testData.applicationFormNewContributionData.newContributionAmount));

              expect(rows.get(0).childNodes[previouslyReportedColumnIndex]).to.contain(formatAmount(thirdReportContributionAmounts[0].previouslyReported));
              expect(rows.get(1).childNodes[previouslyReportedColumnIndex]).to.contain(formatAmount(thirdReportContributionAmounts[1].previouslyReported));
              expect(rows.get(2).childNodes[previouslyReportedColumnIndex]).to.contain(formatAmount(thirdReportContributionAmounts[2].previouslyReported));
              expect(rows.get(3).childNodes[previouslyReportedColumnIndex]).to.contain(formatAmount(thirdReportContributionAmounts[3].previouslyReported));
              expect(rows.get(4).childNodes[previouslyReportedColumnIndex]).to.contain(formatAmount(thirdReportContributionAmounts[4].previouslyReported));

              expect(rows.get(0).childNodes[totalReportedSoFarColumnIndex]).to.contain(formatAmount(thirdReportContributionAmounts[0].totalReportedSoFar));
              expect(rows.get(1).childNodes[totalReportedSoFarColumnIndex]).to.contain(formatAmount(thirdReportContributionAmounts[1].totalReportedSoFar));
              expect(rows.get(2).childNodes[totalReportedSoFarColumnIndex]).to.contain(formatAmount(thirdReportContributionAmounts[2].totalReportedSoFar));
              expect(rows.get(3).childNodes[totalReportedSoFarColumnIndex]).to.contain(formatAmount(thirdReportContributionAmounts[3].totalReportedSoFar));
              expect(rows.get(4).childNodes[totalReportedSoFarColumnIndex]).to.contain(formatAmount(thirdReportContributionAmounts[4].totalReportedSoFar));
            });

            cy.contains('div', 'Sub-total public contribution').children().then(row => {
              expect(row.get(previouslyReportedColumnIndex)).to.contain(formatAmount(thirdReportCurrentContributionSubTotals.subTotalPublic.previouslyReported));
              expect(row.get(currentReportColumnIndex)).to.contain(formatAmount(thirdReportCurrentContributionSubTotals.subTotalPublic.currentReport));
              expect(row.get(totalReportedSoFarColumnIndex)).to.contain(formatAmount(thirdReportCurrentContributionSubTotals.subTotalPublic.totalReportedSoFar));
            });

            cy.contains('div', 'Sub-total automatic public contribution').parent().children().then(row => {
              expect(row.get(previouslyReportedColumnIndex)).to.contain(formatAmount(thirdReportCurrentContributionSubTotals.subTotalAutomaticPublic.previouslyReported));
              expect(row.get(currentReportColumnIndex)).to.contain(formatAmount(thirdReportCurrentContributionSubTotals.subTotalAutomaticPublic.currentReport));
              expect(row.get(totalReportedSoFarColumnIndex)).to.contain(formatAmount(thirdReportCurrentContributionSubTotals.subTotalAutomaticPublic.totalReportedSoFar));
            });

            cy.contains('div', 'Sub-total private contribution').parent().children().then(row => {
              expect(row.get(previouslyReportedColumnIndex)).to.contain(formatAmount(thirdReportCurrentContributionSubTotals.subTotalPrivate.previouslyReported));
              expect(row.get(currentReportColumnIndex)).to.contain(formatAmount(thirdReportCurrentContributionSubTotals.subTotalPrivate.currentReport));
              expect(row.get(totalReportedSoFarColumnIndex)).to.contain(formatAmount(thirdReportCurrentContributionSubTotals.subTotalPrivate.totalReportedSoFar));
            });

            cy.contains('button', 'Save changes').click();
            cy.contains('div', 'Report contribution has been saved successfully').should('be.visible');
            cy.submitPartnerReport(leadPartnerId, Number(reportId));
          });
        });
      });
    });
  });

  it('TB-742 Partner user can manage report annexes', function () {
    cy.fixture('project/reporting/TB-742.json').then(testData => {
      cy.loginByRequest(user.programmeUser.email);
      cy.createCall(call).then(callId => {
        application.details.projectCallId = callId;
        cy.publishCall(callId);

        cy.loginByRequest(user.applicantUser.email);

        cy.createContractedApplication(application, user.programmeUser.email).then(applicationId => {
          const partnerId1 = this[application.partners[0].details.abbreviation];
          cy.assignPartnerCollaborators(applicationId, partnerId1, testData.partnerCollaborator);

          cy.addPartnerReport(partnerId1).then(firstReportId => {
            createReportByAttachingFiles(applicationId, partnerId1, firstReportId, true);
            submitPartnerReport();
          });

          cy.addPartnerReport(partnerId1).then(secondReportId => {
            createReportByAttachingFiles(applicationId, partnerId1, secondReportId, false);

            cy.loginByRequest(user.applicantUser.email);
            cy.visit(`/app/project/detail/${applicationId}/reporting/${partnerId1}/reports/${secondReportId}/annexes`, {failOnStatusCode: false});

            cy.get('mat-row:contains("fileToUpload.txt")').should('have.length', 4);
            cy.get('mat-row:contains("partner-report-attachment01.txt")').should('have.length', 0);

            cy.contains('button', 'Upload file').click();
            cy.get('input[type=file]').selectFile('cypress/fixtures/project/reporting/fileToUpload.txt', {force: true});
            cy.wait(2000);

            addFileDescription(0);
            addFileDescription(1);
            addFileDescription(2);
            addFileDescription(3);
            addFileDescription(4);
            cy.get('mat-row').eq(0).contains('button', 'delete').scrollIntoView().click();
            cy.contains('button', 'Confirm').click();

            cy.get('mat-row').eq(0).contains('button', 'delete').scrollIntoView().should('be.disabled');
            cy.get('mat-row').eq(1).contains('button', 'delete').scrollIntoView().should('be.disabled');
            cy.get('mat-row').eq(2).contains('button', 'delete').scrollIntoView().should('be.disabled');
            cy.get('mat-row').eq(3).contains('button', 'delete').scrollIntoView().should('be.disabled');

            cy.wait(2000);

            validateDownloadFile(0, partnerId1);
            validateDownloadFile(1, partnerId1);
            validateDownloadFile(2, partnerId1);
            validateDownloadFile(3, partnerId1);
          });
        });
      });
    });
  });

  it('TB-744 Partner report can be submitted', function () {
    cy.fixture('project/reporting/TB-744.json').then(testData => {
      cy.loginByRequest(user.programmeUser.email);
      cy.createCall(call).then(callId => {

        application.details.projectCallId = callId;
        cy.publishCall(callId);
        cy.loginByRequest(user.applicantUser.email);

        cy.createContractedApplication(application, user.programmeUser.email).then(applicationId => {

          const partnerId1 = this[application.partners[0].details.abbreviation];

          cy.loginByRequest(user.applicantUser.email);
          cy.assignPartnerCollaborators(applicationId, partnerId1, testData.partnerCollaborator);

          cy.addPartnerReport(partnerId1).then(reportId => {
            cy.addPublicProcurement(partnerId1, reportId, testData.procurement)
              .then(procurement => {
                cy.updatePartnerReportIdentification(partnerId1, reportId, partnerReportIdentification);
                cy.updatePartnerReportExpenditures(partnerId1, reportId, partnerReportExpenditures)
                  .then(response => {
                    for (let i = 0; i < response.length; i++) {
                      partnerParkedExpenditures[i].id = response[i].id;
                    }
                  });
                cy.addBeneficialOwnerToProcurement(partnerId1, reportId, procurement.id, testData.beneficialOwner);
                cy.addSubcontractorToProcurement(partnerId1, reportId, procurement.id, testData.subcontract);
                cy.addAttachmentToProcurement('fileToUpload.txt', 'project/reporting/', partnerId1, reportId, procurement.id);


                cy.visit(`app/project/detail/${applicationId}/reporting/${partnerId1}/reports/${reportId}/workplan`, {failOnStatusCode: false});
                cy.contains('mat-panel-title', 'Work package 1').click();
                cy.get('div.activity-container > jems-multi-language-container input').eq(0)
                  .scrollIntoView()
                  .invoke('show')
                  .selectFile('cypress/fixtures/project/reporting/fileToUpload.txt')
                  .invoke('hide');

                // submit report
                cy.visit(`app/project/detail/${applicationId}/reporting/${partnerId1}/reports/${reportId}/submission`, {failOnStatusCode: false});
                cy.contains('Submit partner report').should('be.disabled');

                cy.contains('Run pre-submission check').should('be.enabled').click();

                cy.contains('Submit partner report', {timeout: 2000}).should('be.enabled').click();
                cy.contains('button', 'Confirm').should('be.visible').click();
                cy.contains('Partner progress report identification').should('be.visible');
              });

            verifyIdentification(applicationId, partnerId1, reportId);
            verifyWorkplan(applicationId, partnerId1, reportId);
            verifyProcurements(applicationId, partnerId1, reportId);
            verifyExpendituresCheckbox(applicationId, partnerId1, reportId);
            verifyContributions(applicationId, partnerId1, reportId);
            verifyAnnexesEditable(applicationId, partnerId1, reportId);
            verifyReportExport(applicationId, partnerId1, reportId);
          });
        });
      });
    });
  });

  it('TB-993 Partner user can export partner report', function () {
    cy.fixture('project/reporting/TB-993.json').then(testData => {
      cy.loginByRequest(user.programmeUser.email);
      cy.createCall(call).then(callId => {
        application.details.projectCallId = callId;
        cy.publishCall(callId);

        cy.loginByRequest(user.applicantUser.email);

        cy.createContractedApplication(application, user.programmeUser.email).then(applicationId => {
          const partnerId = this[application.partners[0].details.abbreviation];
          cy.assignPartnerCollaborators(applicationId, partnerId, testData.partnerCollaborator);

          cy.addPartnerReport(partnerId).then(reportId => {
            cy.updatePartnerReportExpenditures(partnerId, reportId, testData.expenditures);
            cy.visit(`/app/project/detail/${applicationId}/reporting/${partnerId}/reports/${reportId}/export`, {failOnStatusCode: false});
            cy.contains('Export Plugin').parent().prev().click();
            cy.contains('mat-option span', 'Partner Report budget (Example) export').click();

            cy.contains('Input language').parent().prev().click();
            cy.contains('mat-option span', 'Deutsch').click();
            validateBudgetReportExportFile(applicationId, partnerId, reportId, testData.expenditures, false);

            cy.contains('Export language').parent().prev().click();
            cy.contains('mat-option span', 'English').click();
            cy.contains('Input language').parent().prev().click();
            cy.contains('mat-option span', 'English').click();
            validateBudgetReportExportFile(applicationId, partnerId, reportId, testData.expenditures, true);

            cy.contains('Export Plugin').parent().prev().click();
            cy.contains('mat-option span', 'Partner Report (Example) export').click();
            validatePartnerReportExportFile(applicationId, partnerId, reportId);

            cy.contains('Export language').parent().prev().click();
            cy.contains('mat-option span', 'English').click();
            cy.contains('Input language').parent().prev().click();
            cy.contains('mat-option span', 'Deutsch').click();
            validatePartnerReportExportFile(applicationId, partnerId, reportId);
          });
        });
      });
    });
  });

  it('TB-1012 Partner report - parked display in overviews', function () {
    cy.fixture('project/reporting/TB-1012.json').then(testData => {
      cy.loginByRequest(user.programmeUser.email);
      call.preSubmissionCheckSettings.reportPartnerCheckPluginKey = 'report-partner-check-off';
      call.preSubmissionCheckSettings.controlReportPartnerCheckPluginKey = 'control-report-partner-check-off';
      cy.createCall(call).then(callId => {
        application.details.projectCallId = callId;
        cy.publishCall(callId);

        cy.loginByRequest(user.applicantUser.email);

        application.contractMonitoring.fastTrackLumpSums[0].readyForPayment = false;
        cy.createContractedApplication(application, user.programmeUser.email).then(applicationId => {
          const partnerId = this[application.partners[0].details.abbreviation];
          cy.assignPartnerCollaborators(applicationId, partnerId, testData.partnerCollaborator);
          cy.addPartnerReport(partnerId).then(reportId => {
            cy.updatePartnerReportExpenditures(partnerId, reportId, testData.expenditures);
            cy.runPreSubmissionPartnerReportCheck(partnerId, reportId);
            cy.submitPartnerReport(partnerId, reportId);

            //Group order 1
            cy.visit(`/app/project/detail/${applicationId}/reporting/${partnerId}/reports/${reportId}/financialOverview`, {failOnStatusCode: false});
            partnerReportPage.verifyAmountsInTables(testData.expectedResults.group1); // TODO replace all amount verifications below with this method

            //Group Order 2
            cy.loginByRequest(user.admin.email);
            cy.createTypologyOfErrors(testData.typologyOfErrors);
            testData.controllerRole.name = `controllerRole_${faker.string.alphanumeric(5)}`;
            testData.controllerUserEdit.email = faker.internet.email();
            cy.createRole(testData.controllerRole).then(roleId => {
              testData.controllerUserEdit.userRoleId = roleId;
              cy.createUser(testData.controllerUserEdit);
              testData.controllerInstitution.name = `${faker.word.adjective()} ${faker.word.noun()}`;
              testData.controllerInstitution.institutionUsers[0].userEmail = testData.controllerUserEdit.email;
              cy.createInstitution(testData.controllerInstitution).then(institutionId => {
                testData.controllerAssignment.assignmentsToAdd[0].partnerId = partnerId;
                testData.controllerAssignment.assignmentsToAdd[0].institutionId = institutionId;
                cy.assignInstitution(testData.controllerAssignment);
              });
            });
            cy.assignPartnerCollaborators(applicationId, partnerId, testData.partnerCollaborator);

            cy.loginByRequest(testData.controllerUserEdit.email);
            cy.startControlWork(partnerId, reportId);
            cy.visit(`app/project/detail/${applicationId}/reporting/${partnerId}/reports/${reportId}/controlReport/expenditureVerificationTab`, {failOnStatusCode: false});

            // add deductions
            cy.get('mat-row').eq(1).children().eq(23).within((column) => {
              cy.wrap(column).get('input').type('10,00', {force: true});
            });
            cy.get('mat-row').eq(1).children().eq(25).within((column) => {
              cy.wrap(column).get('mat-select').click();
            });
            cy.contains('mat-option span', 'Typology of Error').first().click();

            cy.get('mat-row').eq(2).children().eq(23).within((column) => {
              cy.wrap(column).get('input').type('10,00', {force: true});
            });
            cy.get('mat-row').eq(2).children().eq(25).within((column) => {
              cy.wrap(column).get('mat-select').click();
            });
            cy.contains('mat-option span', 'Typology of Error').first().click();

            cy.get('mat-row').eq(3).children().eq(23).within((column) => {
              cy.wrap(column).get('input').type('10,00', {force: true});
            });
            cy.get('mat-row').eq(3).children().eq(25).within((column) => {
              cy.wrap(column).get('mat-select').click();
            });
            cy.contains('mat-option span', 'Typology of Error').first().click();

            cy.get('mat-row').eq(5).children().eq(23).within((column) => {
              cy.wrap(column).get('input').type('10,00', {force: true});
            });
            cy.get('mat-row').eq(5).children().eq(25).within((column) => {
              cy.wrap(column).get('mat-select').click();
            });
            cy.contains('mat-option span', 'Typology of Error').first().click();

            //park items
            cy.get('mat-row').eq(0).children().eq(26).within((column) => {
              cy.wrap(column).get('mat-slide-toggle').click();
            });
            cy.get('mat-row').eq(4).children().eq(26).within((column) => {
              cy.wrap(column).get('mat-slide-toggle').click();
            });
            cy.get('button').contains('Save changes').click();
            cy.contains('Report expenditure costs were saved successfully').should('be.visible');

            cy.visit(`app/project/detail/${applicationId}/reporting/${partnerId}/reports/${reportId}/controlReport/overviewAndFinalizeTab`, {failOnStatusCode: false});
            cy.contains('Run pre-submission check').scrollIntoView();
            cy.contains('Run pre-submission check').click();
            cy.contains('button', 'Finalize control').click();
            cy.contains('Confirm').should('be.visible').click();
            cy.get('mat-chip > mat-icon').next().contains('Certified');
            cy.visit(`/app/project/detail/${applicationId}/reporting/${partnerId}/reports/${reportId}/financialOverview`, {failOnStatusCode: false});

            //check total eligible after report values for co-fin breakdown
            cy.get('jems-partner-breakdown-co-financing').contains('ERDF').parent().should('contain', '1.605,96');
            cy.get('jems-partner-breakdown-co-financing').contains('Other fund EN').parent().should('contain', '392,38');
            cy.get('jems-partner-breakdown-co-financing').contains('Partner contribution').parent().should('contain', '678,26');
            cy.get('jems-partner-breakdown-co-financing').contains('of which Public contribution').parent().should('contain', '248,19');
            cy.get('jems-partner-breakdown-co-financing').contains('of which Automatic public contribution').parent().should('contain', '288,03');
            cy.get('jems-partner-breakdown-co-financing').contains('of which Private contribution').parent().should('contain', '142,02');

            //check total eligible after report values for cost category breakdown
            cy.get('jems-partner-breakdown-cost-category').contains('Staff costs').parent().should('contain', '389,53');
            cy.get('jems-partner-breakdown-cost-category').contains('Office and administrative costs').parent().should('contain', '38,95');
            cy.get('jems-partner-breakdown-cost-category').contains('Travel and accommodation').parent().should('contain', '0,00');
            cy.get('jems-partner-breakdown-cost-category').contains('External expertise and services').parent().should('contain', '177,33');
            cy.get('jems-partner-breakdown-cost-category').contains('Equipment').parent().should('contain', '545,55');
            cy.get('jems-partner-breakdown-cost-category').contains('Infrastructure and works').parent().should('contain', '1.224,80');
            cy.get('jems-partner-breakdown-cost-category').contains('Other costs').parent().should('contain', '0,00');
            cy.get('jems-partner-breakdown-cost-category').contains('Lump sum').parent().should('contain', '0,00');
            cy.get('jems-partner-breakdown-cost-category').contains('Unit Costs').parent().should('contain', '300,44');

            //check total eligible after report values for breakdown per lump sum
            cy.get('jems-partner-breakdown-lump-sum').contains('Implementation Lump sum DE').parent().parent().parent().should('contain', '0,00');

            //check total eligible after report values for per unit cost
            cy.get('jems-partner-breakdown-unit-cost').contains('Unit cost multi - all DE').parent().parent().should('contain', '300,44');
          });

          //Group Order 3
          cy.loginByRequest(user.applicantUser.email);
          cy.addPartnerReport(partnerId).then(reportId => {
            cy.visit(`/app/project/detail/${applicationId}/reporting/${partnerId}/reports/${reportId}/expenditures`, {failOnStatusCode: false});
            cy.contains('mat-row', 'R1.1').find('button').contains('sync').click();
            cy.contains('Confirm').should('be.visible').click();
            cy.get('jems-partner-report-expenditures-parked:contains("R1.1")').should('not.exist');
            
            cy.contains('mat-row', 'R1.5').find('button').contains('sync').click();
            cy.contains('Confirm').should('be.visible').click();
            cy.get('jems-partner-report-expenditures-parked:contains("R1.5")').should('not.exist');

            cy.visit(`/app/project/detail/${applicationId}/reporting/${partnerId}/reports/${reportId}/financialOverview`, {failOnStatusCode: false});
            //check expenditure summary table
            cy.get('jems-partner-breakdown-co-financing').contains('ERDF').parent().should('contain', '2.980,43');
            cy.get('jems-partner-breakdown-co-financing').contains('ERDF').parent().should('contain', '1.346,51');
            cy.get('jems-partner-breakdown-co-financing').contains('ERDF').parent().should('contain', 'parked 1.346,51');
            cy.get('jems-partner-breakdown-co-financing').contains('ERDF').parent().should('contain', 're-included 1.346,51');
            cy.get('jems-partner-breakdown-co-financing').contains('Other fund EN').parent().should('contain', '728,21');
            cy.get('jems-partner-breakdown-co-financing').contains('Other fund EN').parent().should('contain', '328,99');
            cy.get('jems-partner-breakdown-co-financing').contains('Other fund EN').parent().should('contain', 'parked 328,99');
            cy.get('jems-partner-breakdown-co-financing').contains('Other fund EN').parent().should('contain', 're-included 328,99');
            cy.get('jems-partner-breakdown-co-financing').contains('Partner contribution').parent().should('contain', '1.258,75');
            cy.get('jems-partner-breakdown-co-financing').contains('Partner contribution').parent().should('contain', '568,69');
            cy.get('jems-partner-breakdown-co-financing').contains('Partner contribution').parent().should('contain', 'parked 568,69');
            cy.get('jems-partner-breakdown-co-financing').contains('Partner contribution').parent().should('contain', 're-included 568,69');
            cy.get('jems-partner-breakdown-co-financing').contains('of which Public contribution').parent().should('contain', '460,62');
            cy.get('jems-partner-breakdown-co-financing').contains('of which Public contribution').parent().should('contain', '208,10');
            cy.get('jems-partner-breakdown-co-financing').contains('of which Public contribution').parent().should('contain', 'parked 208,10');
            cy.get('jems-partner-breakdown-co-financing').contains('of which Public contribution').parent().should('contain', 're-included 208,10');
            cy.get('jems-partner-breakdown-co-financing').contains('of which Automatic public contribution').parent().should('contain', '534,54');
            cy.get('jems-partner-breakdown-co-financing').contains('of which Automatic public contribution').parent().should('contain', '241,49');
            cy.get('jems-partner-breakdown-co-financing').contains('of which Automatic public contribution').parent().should('contain', 'parked 241,49');
            cy.get('jems-partner-breakdown-co-financing').contains('of which Automatic public contribution').parent().should('contain', 're-included 241,49');
            cy.get('jems-partner-breakdown-co-financing').contains('of which Private contribution').parent().should('contain', '263,57');
            cy.get('jems-partner-breakdown-co-financing').contains('of which Private contribution').parent().should('contain', '119,07');
            cy.get('jems-partner-breakdown-co-financing').contains('of which Private contribution').parent().should('contain', 'parked 119,07');
            cy.get('jems-partner-breakdown-co-financing').contains('of which Private contribution').parent().should('contain', 're-included 119,07');

            //check expenditure breakdown per cost category table
            cy.get('jems-partner-breakdown-cost-category').contains('Staff costs').parent().should('contain', '435,56');
            cy.get('jems-partner-breakdown-cost-category').contains('Staff costs').parent().should('contain', '40,03');
            cy.get('jems-partner-breakdown-cost-category').contains('Staff costs').parent().should('contain', 'parked 40,03');
            cy.get('jems-partner-breakdown-cost-category').contains('Staff costs').parent().should('contain', 're-included 40,03');
            cy.get('jems-partner-breakdown-cost-category').contains('Office and administrative costs').parent().should('contain', '43,55');
            cy.get('jems-partner-breakdown-cost-category').contains('Office and administrative costs').parent().should('contain', '4,00');
            cy.get('jems-partner-breakdown-cost-category').contains('Office and administrative costs').parent().should('contain', 'parked 4,00');
            cy.get('jems-partner-breakdown-cost-category').contains('Office and administrative costs').parent().should('contain', 're-included 4,00');
            cy.get('jems-partner-breakdown-cost-category').contains('Travel and accommodation').parent().should('contain', '200,16');
            cy.get('jems-partner-breakdown-cost-category').contains('Travel and accommodation').parent().should('contain', '200,16');
            cy.get('jems-partner-breakdown-cost-category').contains('Travel and accommodation').parent().should('contain', 'parked 200,16');
            cy.get('jems-partner-breakdown-cost-category').contains('Travel and accommodation').parent().should('contain', 're-included 200,16');
            cy.get('jems-partner-breakdown-cost-category').contains('External expertise and services').parent().should('contain', '187,33');
            cy.get('jems-partner-breakdown-cost-category').contains('External expertise and services').parent().should('contain', '0,00');
            cy.get('jems-partner-breakdown-cost-category').contains('External expertise and services').parent().should('contain', 'parked 0,00');
            cy.get('jems-partner-breakdown-cost-category').contains('External expertise and services').parent().should('contain', 're-included 0,00');
            cy.get('jems-partner-breakdown-cost-category').contains('Equipment').parent().should('contain', '555,55');
            cy.get('jems-partner-breakdown-cost-category').contains('Equipment').parent().should('contain', '0,00');
            cy.get('jems-partner-breakdown-cost-category').contains('Equipment').parent().should('contain', 'parked 0,00');
            cy.get('jems-partner-breakdown-cost-category').contains('Equipment').parent().should('contain', 're-included 0,00');
            cy.get('jems-partner-breakdown-cost-category').contains('Infrastructure and works').parent().should('contain', '1.234,80');
            cy.get('jems-partner-breakdown-cost-category').contains('Infrastructure and works').parent().should('contain', '0,00');
            cy.get('jems-partner-breakdown-cost-category').contains('Infrastructure and works').parent().should('contain', 'parked 0,00');
            cy.get('jems-partner-breakdown-cost-category').contains('Infrastructure and works').parent().should('contain', 're-included 0,00');
            cy.get('jems-partner-breakdown-cost-category').contains('Lump sum').parent().should('contain', '2.000,00');
            cy.get('jems-partner-breakdown-cost-category').contains('Lump sum').parent().should('contain', 'parked 2.000,00');
            cy.get('jems-partner-breakdown-cost-category').contains('Lump sum').parent().should('contain', 're-included 2.000,00');
            cy.get('jems-partner-breakdown-cost-category').contains('Unit Costs').parent().should('contain', '310,44');
            cy.get('jems-partner-breakdown-cost-category').contains('Unit Costs').parent().should('contain', '0,00');
            cy.get('jems-partner-breakdown-cost-category').contains('Unit Costs').parent().should('contain', 'parked 0,00');
            cy.get('jems-partner-breakdown-cost-category').contains('Unit Costs').parent().should('contain', 're-included 0,00');

            //check expenditure breakdown per lump sum
            cy.get('jems-partner-breakdown-lump-sum').contains('Implementation Lump sum DE').parent().parent().parent().should('contain', '2.000,00');
            cy.get('jems-partner-breakdown-lump-sum').contains('Implementation Lump sum DE').parent().parent().parent().should('contain', 'parked 2.000,00');
            cy.get('jems-partner-breakdown-lump-sum').contains('Implementation Lump sum DE').parent().parent().parent().should('contain', 're-included 2.000,00');

            //check expenditure breakdown per unit cost
            cy.get('jems-partner-breakdown-unit-cost').contains('Unit cost multi - all DE').parent().parent().should('contain', '310,44');
            cy.get('jems-partner-breakdown-unit-cost').contains('Unit cost multi - all DE').parent().parent().should('contain', '0,00');
            cy.get('jems-partner-breakdown-unit-cost').contains('Unit cost multi - all DE').parent().parent().should('contain', 'parked 0,00');
            cy.get('jems-partner-breakdown-unit-cost').contains('Unit cost multi - all DE').parent().parent().should('contain', 're-included 0,00');

            //Group Order 4
            cy.visit(`/app/project/detail/${applicationId}/reporting/${partnerId}/reports/${reportId}/expenditures`, {failOnStatusCode: false});
            cy.get('mat-row').eq(0).children().eq(17).within((column) => {
              cy.wrap(column).scrollIntoView().click();
              cy.wrap(column).get('input').type('210,16');
            });
            cy.get('button').contains('Save changes').click();
            cy.contains('Report expenditure costs were saved successfully').should('be.visible');

            cy.visit(`/app/project/detail/${applicationId}/reporting/${partnerId}/reports/${reportId}/financialOverview`, {failOnStatusCode: false});
            //check expenditure summary table
            cy.get('jems-partner-breakdown-co-financing').contains('ERDF').parent().should('contain', '2.980,43');
            cy.get('jems-partner-breakdown-co-financing').contains('ERDF').parent().should('contain', '1.353,83');
            cy.get('jems-partner-breakdown-co-financing').contains('ERDF').parent().should('contain', 'parked 1.346,51');
            cy.get('jems-partner-breakdown-co-financing').contains('ERDF').parent().should('contain', 're-included 1.353,83');
            cy.get('jems-partner-breakdown-co-financing').contains('Other fund EN').parent().should('contain', '728,21');
            cy.get('jems-partner-breakdown-co-financing').contains('Other fund EN').parent().should('contain', '330,78');
            cy.get('jems-partner-breakdown-co-financing').contains('Other fund EN').parent().should('contain', 'parked 328,99');
            cy.get('jems-partner-breakdown-co-financing').contains('Other fund EN').parent().should('contain', 're-included 330,78');
            cy.get('jems-partner-breakdown-co-financing').contains('Partner contribution').parent().should('contain', '1.258,75');
            cy.get('jems-partner-breakdown-co-financing').contains('Partner contribution').parent().should('contain', '571,78');
            cy.get('jems-partner-breakdown-co-financing').contains('Partner contribution').parent().should('contain', 'parked 568,69');
            cy.get('jems-partner-breakdown-co-financing').contains('Partner contribution').parent().should('contain', 're-included 571,78');
            cy.get('jems-partner-breakdown-co-financing').contains('of which Public contribution').parent().should('contain', '460,62');
            cy.get('jems-partner-breakdown-co-financing').contains('of which Public contribution').parent().should('contain', '209,23');
            cy.get('jems-partner-breakdown-co-financing').contains('of which Public contribution').parent().should('contain', 'parked 208,10');
            cy.get('jems-partner-breakdown-co-financing').contains('of which Public contribution').parent().should('contain', 're-included 209,23');
            cy.get('jems-partner-breakdown-co-financing').contains('of which Automatic public contribution').parent().should('contain', '534,54');
            cy.get('jems-partner-breakdown-co-financing').contains('of which Automatic public contribution').parent().should('contain', '242,81');
            cy.get('jems-partner-breakdown-co-financing').contains('of which Automatic public contribution').parent().should('contain', 'parked 241,49');
            cy.get('jems-partner-breakdown-co-financing').contains('of which Automatic public contribution').parent().should('contain', 're-included 242,81');
            cy.get('jems-partner-breakdown-co-financing').contains('of which Private contribution').parent().should('contain', '263,57');
            cy.get('jems-partner-breakdown-co-financing').contains('of which Private contribution').parent().should('contain', '119,72');
            cy.get('jems-partner-breakdown-co-financing').contains('of which Private contribution').parent().should('contain', 'parked 119,07');
            cy.get('jems-partner-breakdown-co-financing').contains('of which Private contribution').parent().should('contain', 're-included 119,72');

            //check expenditure breakdown per cost category table
            cy.get('jems-partner-breakdown-cost-category').contains('Staff costs').parent().should('contain', '435,56');
            cy.get('jems-partner-breakdown-cost-category').contains('Staff costs').parent().should('contain', '42,03');
            cy.get('jems-partner-breakdown-cost-category').contains('Staff costs').parent().should('contain', 'parked 40,03');
            cy.get('jems-partner-breakdown-cost-category').contains('Staff costs').parent().should('contain', 're-included 42,03');
            cy.get('jems-partner-breakdown-cost-category').contains('Office and administrative costs').parent().should('contain', '43,55');
            cy.get('jems-partner-breakdown-cost-category').contains('Office and administrative costs').parent().should('contain', '4,20');
            cy.get('jems-partner-breakdown-cost-category').contains('Office and administrative costs').parent().should('contain', 'parked 4,00');
            cy.get('jems-partner-breakdown-cost-category').contains('Office and administrative costs').parent().should('contain', 're-included 4,20');
            cy.get('jems-partner-breakdown-cost-category').contains('Travel and accommodation').parent().should('contain', '200,16');
            cy.get('jems-partner-breakdown-cost-category').contains('Travel and accommodation').parent().should('contain', '210,16');
            cy.get('jems-partner-breakdown-cost-category').contains('Travel and accommodation').parent().should('contain', 'parked 200,16');
            cy.get('jems-partner-breakdown-cost-category').contains('Travel and accommodation').parent().should('contain', 're-included 210,16');
            cy.get('jems-partner-breakdown-cost-category').contains('External expertise and services').parent().should('contain', '187,33');
            cy.get('jems-partner-breakdown-cost-category').contains('External expertise and services').parent().should('contain', '0,00');
            cy.get('jems-partner-breakdown-cost-category').contains('External expertise and services').parent().should('contain', 'parked 0,00');
            cy.get('jems-partner-breakdown-cost-category').contains('External expertise and services').parent().should('contain', 're-included 0,00');
            cy.get('jems-partner-breakdown-cost-category').contains('Equipment').parent().should('contain', '555,55');
            cy.get('jems-partner-breakdown-cost-category').contains('Equipment').parent().should('contain', '0,00');
            cy.get('jems-partner-breakdown-cost-category').contains('Equipment').parent().should('contain', 'parked 0,00');
            cy.get('jems-partner-breakdown-cost-category').contains('Equipment').parent().should('contain', 're-included 0,00');
            cy.get('jems-partner-breakdown-cost-category').contains('Infrastructure and works').parent().should('contain', '1.234,80');
            cy.get('jems-partner-breakdown-cost-category').contains('Infrastructure and works').parent().should('contain', '0,00');
            cy.get('jems-partner-breakdown-cost-category').contains('Infrastructure and works').parent().should('contain', 'parked 0,00');
            cy.get('jems-partner-breakdown-cost-category').contains('Infrastructure and works').parent().should('contain', 're-included 0,00');
            cy.get('jems-partner-breakdown-cost-category').contains('Lump sum').parent().should('contain', '2.000,00');
            cy.get('jems-partner-breakdown-cost-category').contains('Lump sum').parent().should('contain', 'parked 2.000,00');
            cy.get('jems-partner-breakdown-cost-category').contains('Lump sum').parent().should('contain', 're-included 2.000,00');
            cy.get('jems-partner-breakdown-cost-category').contains('Unit Costs').parent().should('contain', '310,44');
            cy.get('jems-partner-breakdown-cost-category').contains('Unit Costs').parent().should('contain', '0,00');
            cy.get('jems-partner-breakdown-cost-category').contains('Unit Costs').parent().should('contain', 'parked 0,00');
            cy.get('jems-partner-breakdown-cost-category').contains('Unit Costs').parent().should('contain', 're-included 0,00');

            //check expenditure breakdown per lump sum
            cy.get('jems-partner-breakdown-lump-sum').contains('Implementation Lump sum DE').parent().parent().parent().should('contain', '2.000,00');
            cy.get('jems-partner-breakdown-lump-sum').contains('Implementation Lump sum DE').parent().parent().parent().should('contain', 'parked 2.000,00');
            cy.get('jems-partner-breakdown-lump-sum').contains('Implementation Lump sum DE').parent().parent().parent().should('contain', 're-included 2.000,00');

            //check expenditure breakdown per unit cost
            cy.get('jems-partner-breakdown-unit-cost').contains('Unit cost multi - all DE').parent().parent().should('contain', '310,44');
            cy.get('jems-partner-breakdown-unit-cost').contains('Unit cost multi - all DE').parent().parent().should('contain', '0,00');
            cy.get('jems-partner-breakdown-unit-cost').contains('Unit cost multi - all DE').parent().parent().should('contain', 'parked 0,00');
            cy.get('jems-partner-breakdown-unit-cost').contains('Unit cost multi - all DE').parent().parent().should('contain', 're-included 0,00');
          });
        });
      });
    });
  });

  it('TB-936 Control Expenditure verification', function () {
    cy.fixture('project/reporting/TB-936.json').then(testData => {
      cy.loginByRequest(user.admin.email);
      const typologyOfError = testData.typologyOfErrors.toPersist[0].description;
      cy.createTypologyOfErrors(testData.typologyOfErrors);

      cy.loginByRequest(user.programmeUser.email);
      cy.createCall(call).then(callId => {
        application.details.projectCallId = callId;
        cy.publishCall(callId);

        cy.loginByRequest(user.applicantUser.email);
        cy.createContractedApplication(application, user.programmeUser.email).then(applicationId => {
          const partnerId = this[application.partners[0].details.abbreviation];

          createControllerUser(testData, partnerId);

          cy.loginByRequest(user.applicantUser.email);
          cy.assignPartnerCollaborators(applicationId, partnerId, testData.partnerCollaborator);

          cy.addPartnerReport(partnerId).then(reportId => {
            addExpendituresAndSubmit(partnerId, reportId);

            // Set-up
            cy.loginByRequest(testData.controllerUser.email);
            cy.startControlWork(partnerId, reportId);
            cy.visit(`/app/project/detail/${applicationId}/reporting/${partnerId}/reports`, {failOnStatusCode: false});
            cy.contains('mat-row', 'Control ongoing')
              .contains('button', 'Open controller work')
              .click();

            // Group 1.
            parkExpenditureRowByIndex(1);
            verifyParkedExpenditureRowByIndex(1, true, false, 0);
            verifyParkedOverviewRowByIndex(partnerReportExpenditures[1].declaredAmountInEur);

            // Group 2.
            parkExpenditureRowByIndex(1);
            verifyParkedExpenditureRowByIndex(1, true, true, partnerReportExpenditures[1].declaredAmountInEur);

            // Group 3.
            const deductedAmount = 100;
            const certifiedAmount = partnerReportExpenditures[2].declaredAmountInEur - deductedAmount;
            deductExpenditureRowByIndex(2, deductedAmount, typologyOfError);
            verifyDeductedExpenditureRowByIndex(2, true, false, certifiedAmount);
            verifyDeductedOverviewRowByIndex(typologyOfError, deductedAmount);

            // Group 4.
            finalizeControlWork();
            verifyCannotEdit(applicationId, partnerId);
          });
        });
      });
      // });
    });
  });

  //region TB-744 METHODS
  function verifyIdentification(applicationId, partnerId1, reportId) {
    cy.visit(`app/project/detail/${applicationId}/reporting/${partnerId1}/reports/${reportId}/identification`, {failOnStatusCode: false});
    cy.get('input[name="startDate"]').should('be.disabled');
    cy.get('input[name="endDate"]').should('be.disabled');
    cy.get('mat-select').should('have.class', 'mat-select-disabled');
  }

  function verifyWorkplan(applicationId, partnerId1, reportId) {
    cy.visit(`app/project/detail/${applicationId}/reporting/${partnerId1}/reports/${reportId}/workplan`, {failOnStatusCode: false});
    cy.contains('mat-panel-title', 'Work package 1').click();

    cy.get('input[type="checkbox"]').each(cb => {
      cy.wrap(cb).should('be.disabled');
    });

    cy.contains('div', 'A 1.1').parent().within(() => {
      cy.get('jems-partner-actions-cell').within((e) => {
        cy.contains('mat-icon', 'file_download').should('exist');
        expect(e).to.not.contain('cancel');
      });
    });
  }

  function verifyProcurements(applicationId, partnerId1, reportId) {
    cy.visit(`app/project/detail/${applicationId}/reporting/${partnerId1}/reports/${reportId}/procurements`, {failOnStatusCode: false});

    cy.contains('PP - Rather important procurement').click();

    cy.get('jems-partner-procurement-identification input[name="reportNumber"]')
      .should('be.disabled');

    cy.get('jems-partner-procurement-identification input[name="contractName"]')
      .should('be.disabled');

    cy.get('jems-partner-procurement-identification input[name="referenceNumber"]')
      .should('be.disabled');

    cy.get('jems-partner-procurement-identification input[name="contractDate"]')
      .should('be.disabled');

    cy.get('jems-partner-procurement-identification input[name="contractType"]')
      .should('be.disabled');

    cy.get('jems-partner-procurement-identification input[name="contractAmount"]')
      .should('be.disabled');

    cy.get('mat-select[formcontrolname="currencyCode"]')
      .should('have.class', 'mat-select-disabled');

    cy.get('jems-partner-procurement-identification input[name="supplierName"]')
      .should('be.disabled');

    cy.get('jems-partner-procurement-identification input[name="vatNumber"]')
      .should('be.disabled');

    cy.get('[label="project.application.partner.report.procurements.table.comment"] textarea')
      .should('be.disabled');
  }

  function verifyExpendituresCheckbox(applicationId, partnerId, reportId) {
    cy.visit(`app/project/detail/${applicationId}/reporting/${partnerId}/reports/${reportId}/expenditures`, {failOnStatusCode: false});
    cy.get('input[type="checkbox"]').each(cb => {
      cy.wrap(cb).should('be.disabled');
    });
  }

  function verifyContributions(applicationId, partnerId, reportId) {
    cy.visit(`app/project/detail/${applicationId}/reporting/${partnerId}/reports/${reportId}/contribution`, {failOnStatusCode: false});
    cy.get('input[type="decimal"]').each(input => {
      cy.wrap(input).should('be.disabled');
    });
  }

  function verifyAnnexesEditable(applicationId, partnerId, reportId) {
    cy.visit(`app/project/detail/${applicationId}/reporting/${partnerId}/reports/${reportId}/annexes`, {failOnStatusCode: false});
    cy.contains('mat-card', 'Report annexes').within(() => {
      cy.contains('button', 'Upload file').should('not.exist');
    });
  }

  function verifyReportExport(applicationId, partnerId, reportId) {
    cy.visit(`app/project/detail/${applicationId}/reporting/${partnerId}/reports/${reportId}/export`, {failOnStatusCode: false});
    cy.contains('div', 'Export Plugin').find('mat-select').click();
    cy.contains('mat-option', 'Partner Report (Example) export').should('be.visible');
    cy.contains('mat-option', 'Partner Report budget (Example) export').click();
    cy.contains('button', 'Export').should('be.enabled');
  }

  // endregion

  //region TB-554 METHODS
  function verifyReport(reportInfo) {
    cy.contains('Partner report ID').next().should('contain.text', reportInfo.partnerReportId);
    cy.contains('Partner report status').next().should('contain.text', reportInfo.partnerReportStatus);
    cy.contains('Partner number').next().should('contain.text', reportInfo.partnerNumber);
    cy.contains('Name of the organisation in original language').next().should('contain.text', reportInfo.nameInOriginalLanguage);
    cy.contains('Name of the organisation in english').next().should('contain.text', reportInfo.nameInEnglish);
    cy.contains('Legal status').next().should('contain.text', reportInfo.legalStatus);
    cy.contains('Type of partner').next().should('contain.text', reportInfo.typeOfPartner);
    cy.contains('Partner organisation can recover VAT for project activities').next().should('contain.text', reportInfo.vatRecovery);
    cy.contains('Co-financing source and rate').next()
      .should('contain.text', reportInfo.coFinancingSourceAndRate1)
      .should('contain.text', reportInfo.coFinancingSourceAndRate2);
    cy.contains('Country').next().should('contain.text', reportInfo.country);
    cy.contains('Local currency (according to InforEuro)').next().should('contain.text', reportInfo.localCurrency);
  }

  //endregion

  //region TB-738 METHODS
  function createControllerUser(testData, partnerId1) {
    cy.loginByRequest(user.admin.email);
    testData.controllerRole.name = `controllerRole_${faker.string.alphanumeric(5)}`;
    testData.controllerUser.email = faker.internet.email();

    cy.createRole(testData.controllerRole)
      .then(roleId => {
        testData.controllerUser.userRoleId = roleId;

        cy.createUser(testData.controllerUser);

        testData.controllerInstitution.name = `${faker.word.adjective()} ${faker.word.noun()}`;
        testData.controllerInstitution.institutionUsers[0].userEmail = testData.controllerUser.email;

        cy.createInstitution(testData.controllerInstitution)
          .then(institutionId => {
            testData.controllerAssignment.assignmentsToAdd[0].partnerId = partnerId1;
            testData.controllerAssignment.assignmentsToAdd[0].institutionId = institutionId;
            testData.controllerInstitution.id = institutionId;
            cy.assignInstitution(testData.controllerAssignment);
          });
      });
  }


  function updatePartnerReportDetails(partnerId, reportId, partnerProcurement) {
    cy.addPublicProcurement(partnerId, reportId, partnerProcurement[0]);
    cy.updatePartnerReportIdentification(partnerId, reportId, partnerReportIdentification);
    cy.updatePartnerReportExpenditures(partnerId, reportId, partnerReportExpenditures)
      .then(response => {
        for (let i = 0; i < response.length; i++) {
          partnerParkedExpenditures[i].id = response[i].id;
        }
      });
    cy.runPreSubmissionPartnerReportCheck(partnerId, reportId);
    cy.submitPartnerReport(partnerId, reportId);
  }

  function performControlWork(testData, reportId, partnerId) {
    cy.loginByRequest(testData.controllerUser.email);
    cy.startControlWork(partnerId, reportId);

    controlReportIdentification.designatedController.controlInstitutionId = testData.controllerInstitution.id;
    controlReportIdentification.designatedController.controllingUserId = testData.controllerUser.id;
    controlReportIdentification.designatedController.controllerReviewerId = testData.controllerUser.id;
    cy.updateControlReportIdentification(partnerId, reportId, controlReportIdentification);
    cy.setExpenditureItemsAsParked(partnerId, reportId, partnerParkedExpenditures);
    cy.finalizeControl(partnerId, reportId);
  }

  function openListOfExpenditures(partnerId1, applicationId) {
    cy.loginByRequest(user.applicantUser.email);
    cy.addPartnerReport(partnerId1);
    // Open the list of expenditures tab for first partner.
    cy.visit(`/app/project/detail/${applicationId}/reporting/${partnerId1}/reports`, {failOnStatusCode: false});

    cy.contains('mat-row', 'Draft')
      .click();
    cy.contains('a', 'List of expenditures')
      .click();
  }

  function verifyRegularLumpSumsOnListAndAddOne(applicationId) {
    // this works for regular lump sum array with only one element in the array
    cy.getRegularLumpSums(applicationId).then(lumpSums => {
      lumpSums.forEach(lumpSum => {
        lumpSum.name.forEach(lumpSumName => {
          if (lumpSumName.language === 'DE') {
            cy.contains('mat-option', `${lumpSumName.translation}`)
              .should('be.visible')
              .click();
          }
        });
      });
    });
  }

  function verifyFastTrackLumpSumsNotOnList(applicationId) {
    cy.getFastTrackLumpSums(applicationId).then(lumpSums => {
      lumpSums.forEach(lumpSum => {
        lumpSum.name.forEach(lumpSumName => {
          if (lumpSumName.language === 'DE') {
            cy.contains('mat-option', `${lumpSumName.translation}`)
              .should('not.exist');
          }
        });
      });
    });
  }

  function clickAddExpenditure() {
    cy.contains('button', 'add expenditure')
      .scrollIntoView()
      .should('be.visible')
      .click();
  }

  function verifyCostOptions(applicationId, callId) {
    cy.get('#expenditure-costs-table mat-cell.mat-column-costOptions')
      .eq(1)
      .click();

    cy.getProgrammeUnitCostsEnabledInCall(callId)
      .then(unitCosts => {
        unitCosts.forEach(unitCost => {
          unitCost.name.forEach(unitCostName => {
            if (unitCostName.language === 'DE' &&
              !unitCostName.translation.toLowerCase().includes('staff')) {
              cy.contains('mat-option', `${unitCostName.translation}`)
                .scrollIntoView()
                .should('be.visible');
            }
          });
        });
      });

    cy.getProjectUnitCosts(applicationId)
      .then(unitCosts => {
        unitCosts.forEach(unitCost => {
          unitCost.name.forEach(unitCostName => {
            if (unitCostName.language === 'DE' &&
              !unitCostName.translation.toLowerCase().includes('staff')) {
              cy.contains('mat-option', `${unitCostName.translation}`)
                .scrollIntoView()
                .should('be.visible');
            }
          });
        });
      });

    cy.getProjectProposedUnitCosts(applicationId)
      .then(unitCosts => {
        unitCosts.forEach((unitCost, index) => {
          unitCost.name.forEach(unitCostName => {
            if (unitCostName.language === 'DE' &&
              !unitCostName.translation.toLowerCase().includes('staff')) {
              if (index === unitCosts.length - 1) { // we want to add the last available item
                cy.contains('mat-option', `${unitCostName.translation}`)
                  .scrollIntoView()
                  .should('be.visible')
                  .click();
              } else {
                cy.contains('mat-option', `${unitCostName.translation}`)
                  .scrollIntoView()
                  .should('be.visible');
              }
            }
          });
        });
      });

    cy.get('mat-option')
      .should('not.exist');
  }

  function addRegularExpendituresForEachCostCategory() {
    cy.contains('button', 'add expenditure')
      .scrollIntoView()
      .should('be.visible')
      .click();

    // Staff costs cannot be selected for cost category
    cy.contains('#expenditure-costs-table mat-select', 'Please select a cost category')
      .click()
      .contains('mat-option', 'Staff costs')
      .should('not.exist');

    // Investments cannot be linked to 'Travel and accommodation' cost category
    cy.contains('mat-option', 'Travel and accommodation')
      .click();
    // for efficiency reasons we have to firstly click the "disabled" field
    // to force the element to have mat-select child with aria-disabled attribute
    cy.get(`#expenditure-costs-table mat-cell.mat-column-investmentId`)
      .eq(2)
      .click();
    cy.get('#expenditure-costs-table mat-cell.mat-column-investmentId mat-select')
      .should('have.attr', 'aria-disabled', 'true');

    // Procurement can be selected only from the current partner report (not from the second partner report)
    cy.get(`#expenditure-costs-table mat-cell.mat-column-contractId`)
      .eq(2)
      .scrollIntoView()
      .click();
    cy.contains('mat-option', 'PP - Rather important procurement')
      .should('not.exist');
    cy.contains('mat-option', 'LP1 - Very important procurement')
      .should('be.visible')
      .type('{esc}'); // exit the opened dropdown menu

    // remove the unnecessary expenditure row
    cy.contains('Discard changes')
      .click();

    for (let i = 0; i < costCategories.length; i++) {
      const rowIndex = i + 2;

      cy.contains('button', 'add expenditure')
        .scrollIntoView()
        .should('be.visible')
        .click();

      cy.contains('mat-select', 'Please select a cost category')
        .click();

      cy.contains('mat-option', costCategories[i])
        .click();

      // Other cost categories can be linked to investments from the work package
      if (i !== 0) {
        cy.get(`#expenditure-costs-table mat-cell.mat-column-investmentId`)
          .eq(rowIndex)
          .click();

        cy.contains('.mat-option-text', `I1.${i}`)
          .click();
      }

      cy.get(`#expenditure-costs-table mat-cell.mat-column-internalReferenceNumber`)
        .eq(rowIndex)
        .scrollIntoView()
        .type(partnerReportExpenditures[i].internalReferenceNumber);

      cy.get(`#expenditure-costs-table mat-cell.mat-column-invoiceNumber`)
        .eq(rowIndex)
        .scrollIntoView()
        .type(partnerReportExpenditures[i].invoiceNumber);

      cy.get(`#expenditure-costs-table mat-cell.mat-column-invoiceDate`)
        .eq(rowIndex)
        .scrollIntoView()
        .type('2');

      cy.get(`#expenditure-costs-table mat-cell.mat-column-dateOfPayment`)
        .eq(rowIndex)
        .scrollIntoView()
        .type('3');

      cy.get(`#expenditure-costs-table mat-cell.mat-column-totalValueInvoice`)
        .eq(rowIndex)
        .scrollIntoView()
        .type(`${partnerReportExpenditures[i].totalValueInvoice}`);

      cy.get(`#expenditure-costs-table mat-cell.mat-column-vat`)
        .eq(rowIndex)
        .scrollIntoView()
        .type(`${partnerReportExpenditures[i].vat}`);

      cy.get(`#expenditure-costs-table mat-cell.mat-column-declaredAmount`)
        .eq(rowIndex)
        .scrollIntoView()
        .type(`${partnerReportExpenditures[i].declaredAmount}`.replace('.', ','));

      // Number of units and price per unit fields should be disabled/(non-existent?)
      cy.get(`#expenditure-costs-table mat-cell.mat-column-numberOfUnits`)
        .eq(rowIndex)
        .should('not.contain', 'input');

      cy.get(`#expenditure-costs-table mat-cell.mat-column-pricePerUnit`)
        .eq(rowIndex)
        .should('not.contain', 'input');

      saveExpenditure();
    }
  }

  function reincludeParkedExpenditures() {
    // add lump sum parked expenditure
    reincludeParkedExpenditureByRowIndex(3);

    // add unit cost based parked expenditure
    reincludeParkedExpenditureByRowIndex(2);

    // add 'normal' based parked expenditure
    reincludeParkedExpenditureByRowIndex(1);
  }

  function reincludeParkedExpenditureByRowIndex(rowIndex: number) {
    cy.get(`jems-partner-report-expenditures-parked mat-row`)
      .eq(rowIndex)
      .contains('mat-icon', 'sync')
      .click();

    confirmReinclusionOfParkedExpenditure(rowIndex);
  }

  function confirmReinclusionOfParkedExpenditure(rowIndex: number) {
    cy.contains('button', 'Confirm')
      .click();
    cy.contains('button', 'Confirm')
      .should('not.exist');

    cy.get(`jems-partner-report-expenditures-parked mat-row`)
      .eq(rowIndex)
      .should('not.exist');
  }

  function verifyExpendituresFieldsEditabilityPerRow(rowIndex: number, shouldNumberOfUnitsBeEnabled: boolean, shouldCurrencyCodeBeEnabled: boolean, isLumpSumOrUnitCost: boolean) {
    if (isLumpSumOrUnitCost) {
      // for efficiency reasons we have to firstly click the "disabled" field
      // to force the element to have mat-select child with aria-disabled attribute
      cy.get('#expenditure-costs-table mat-cell.mat-column-costCategory')
        .eq(rowIndex)
        .click()
        .then(currentSubject => {
          cy.wrap(currentSubject)
            .find('mat-select')
            .should('have.attr', 'aria-disabled', 'true');
        });
    } else {
      cy.get('#expenditure-costs-table mat-cell.mat-column-costCategory')
        .eq(rowIndex)
        .find('mat-select')
        .should('have.attr', 'aria-disabled', 'true');
    }

    cy.get('#expenditure-costs-table mat-cell.mat-column-investmentId')
      .eq(rowIndex)
      .find('mat-select')
      .should('have.attr', 'aria-disabled', 'true');

    cy.get('#expenditure-costs-table mat-cell.mat-column-contractId')
      .eq(rowIndex)
      .find('mat-select')
      .should('have.attr', 'aria-disabled', 'true');

    cy.get('#expenditure-costs-table mat-cell.mat-column-internalReferenceNumber')
      .eq(rowIndex)
      .find('input')
      .should('be.disabled');

    cy.get('#expenditure-costs-table mat-cell.mat-column-invoiceNumber')
      .eq(rowIndex)
      .find('input')
      .should('be.disabled');

    cy.get('#expenditure-costs-table mat-cell.mat-column-invoiceDate')
      .eq(rowIndex)
      .find('input')
      .should('be.disabled');

    cy.get('#expenditure-costs-table mat-cell.mat-column-dateOfPayment')
      .eq(rowIndex)
      .find('input')
      .should('be.disabled');

    cy.get('#expenditure-costs-table mat-cell.mat-column-description')
      .eq(rowIndex)
      .find('input')
      .should('be.enabled');

    cy.get('#expenditure-costs-table mat-cell.mat-column-comment')
      .eq(rowIndex)
      .find('input')
      .should('be.enabled');

    if (shouldNumberOfUnitsBeEnabled) {
      cy.get('#expenditure-costs-table mat-cell.mat-column-numberOfUnits')
        .eq(rowIndex)
        .find('input')
        .should('be.enabled');
    } else {
      cy.get('#expenditure-costs-table mat-cell.mat-column-numberOfUnits')
        .eq(rowIndex)
        .find('input')
        .should('be.disabled');
    }

    if (shouldCurrencyCodeBeEnabled) {
      cy.get('#expenditure-costs-table mat-cell.mat-column-currencyCode')
        .eq(rowIndex)
        .find('mat-select')
        .should('have.attr', 'aria-disabled', 'false');
    } else {
      cy.get('#expenditure-costs-table mat-cell.mat-column-currencyCode')
        .eq(rowIndex)
        .find('mat-select')
        .should('have.attr', 'aria-disabled', 'true');
    }

    cy.get('#expenditure-costs-table mat-cell.mat-column-pricePerUnit')
      .eq(rowIndex)
      .find('input')
      .should('be.disabled');

    cy.get('#expenditure-costs-table mat-cell.mat-column-totalValueInvoice')
      .eq(rowIndex)
      .find('input')
      .should('be.disabled');

    cy.get('#expenditure-costs-table mat-cell.mat-column-vat')
      .eq(rowIndex)
      .find('input')
      .should('be.disabled');

    cy.get('#expenditure-costs-table mat-cell.mat-column-declaredAmount')
      .eq(rowIndex)
      .find('input')
      .should('be.disabled');

    cy.get('#expenditure-costs-table mat-cell.mat-column-currencyConversionRate')
      .eq(rowIndex)
      .find('input')
      .should('be.disabled');

    cy.get('#expenditure-costs-table mat-cell.mat-column-declaredAmountInEur')
      .eq(rowIndex)
      .find('input')
      .should('be.disabled');
  }

  function uploadFiles() {
    // upload file to lump sum expenditure
    uploadFileToExpenditure(0, 0);
    replaceUploadedFile(0, 0);

    // upload file to unit cost expenditure
    uploadFileToExpenditure(1, 1);
    replaceUploadedFile(1, 1);

    // upload file to regular expenditure
    uploadFileToExpenditure(2, 2);
    replaceUploadedFile(2, 2);

    // upload file to reincluded lump sum expenditure
    uploadFileToExpenditure(6, 3);
    replaceUploadedFile(6, 3);

    // upload file to reincluded unit cost expenditure
    uploadFileToExpenditure(7, 4);
    replaceUploadedFile(7, 4);

    // upload file to reincluded regular expenditure
    uploadFileToExpenditure(8, 5);
    replaceUploadedFile(8, 5);
  }

  function uploadFileToExpenditure(rowIndex, attachmentIndex) {
    cy.get('#expenditure-costs-table mat-cell.mat-column-uploadFunction input')
      .eq(rowIndex)
      .scrollIntoView()
      .invoke('show')
      .selectFile('cypress/fixtures/project/reporting/fileToUpload.txt')
      .invoke('hide');

    cy.get('.mat-column-uploadFunction mat-chip-list span')
      .eq(attachmentIndex)
      .scrollIntoView()
      .should('be.visible')
      .should('have.text', 'fileToUpload.txt');
  }

  function removeUploadedFileByRowIndex(rowIndex) {
    cy.get('#expenditure-costs-table mat-cell.mat-column-uploadFunction')
      .eq(rowIndex)
      .find('mat-chip-list .mat-chip-remove')
      .click();

    cy.contains('button', 'Confirm')
      .click();

    cy.get('#expenditure-costs-table mat-cell.mat-column-uploadFunction')
      .eq(rowIndex)
      .find('mat-chip-list')
      .should('not.exist');
  }

  function replaceUploadedFile(rowIndex, attachmentIndex) {
    cy.get('#expenditure-costs-table mat-cell.mat-column-uploadFunction input')
      .eq(rowIndex)
      .scrollIntoView()
      .invoke('show')
      .selectFile('cypress/fixtures/project/reporting/fileForUpdate.txt')
      .invoke('hide');

    cy.contains('button', 'Confirm')
      .click();

    cy.get('.mat-column-uploadFunction mat-chip-list span')
      .eq(attachmentIndex)
      .scrollIntoView()
      .should('be.visible')
      .should('have.text', 'fileForUpdate.txt');
  }

  function removeExpenditureByRowIndex(rowIndex) {
    cy.get('#expenditure-costs-table mat-cell.mat-column-actions')
      .eq(rowIndex)
      .contains('mat-icon', 'delete')
      .click();
  }

  function verifyExpendituresTableSize(expectedSize: number) {
    cy.get(`#expenditure-costs-table mat-row`)
      .should(rowsCount => {
        expect(rowsCount).to.be.length(expectedSize);
      });
  }

  function removeParkedExpenditureByRowIndex(rowIndex) {
    cy.get('jems-partner-report-expenditures-parked')
      .eq(rowIndex)
      .contains('mat-icon', 'delete')
      .click();

    cy.contains('button', 'Confirm')
      .click();

    cy.get('jems-partner-report-expenditures-parked')
      .should('not.exist');
  }

  function saveExpenditure() {
    cy.contains('Save changes')
      .scrollIntoView()
      .should('be.visible')
      .click();

    cy.contains('Report expenditure costs were saved successfully')
      .should('not.exist');

    cy.contains('Save changes')
      .should('not.exist');
  }

  //endregion

  //region TB-740 METHODS
  function addSubcontractors(testData) {
    // Add 2 new subcontracts
    cy.contains('button', 'Add subcontractor')
      .scrollIntoView()
      .click();

    cy.get('[formarrayname="subcontracts"] [formcontrolname="contractName"]')
      .eq(0)
      .type(testData.subcontracts[0].contractName);

    cy.get('[formarrayname="subcontracts"] [formcontrolname="referenceNumber"]')
      .eq(0)
      .type(testData.subcontracts[0].referenceNumber);

    cy.get('[formarrayname="subcontracts"] [formcontrolname="contractDate"]')
      .eq(0)
      .type("03/09/2023"); // used hardcoded value due to date locale formatting

    cy.get('[formarrayname="subcontracts"] [formcontrolname="contractAmount"]')
      .eq(0)
      .type(testData.subcontracts[0].contractAmount);

    cy.get('[formarrayname="subcontracts"] [formcontrolname="currencyCode"]')
      .eq(0)
      .click();

    cy.get('.mat-option-text')
      .contains(testData.subcontracts[0].currencyCode)
      .click();

    cy.get('[formarrayname="subcontracts"] [formcontrolname="supplierName"]')
      .eq(0)
      .type(testData.subcontracts[0].supplierName);

    cy.get('[formarrayname="subcontracts"] [formcontrolname="vatNumber"]')
      .eq(0)
      .type(testData.subcontracts[0].vatNumber);

    cy.contains('button', 'Add subcontractor')
      .scrollIntoView()
      .click();

    cy.get('[formarrayname="subcontracts"] [formcontrolname="contractName"]')
      .eq(1)
      .type(testData.subcontracts[1].contractName);

    cy.get('[formarrayname="subcontracts"] [formcontrolname="referenceNumber"]')
      .eq(1)
      .type(testData.subcontracts[1].referenceNumber);

    cy.get('[formarrayname="subcontracts"] [formcontrolname="contractDate"]')
      .eq(1)
      .type("03/09/2023"); // used hardcoded value due to date locale formatting

    cy.get('[formarrayname="subcontracts"] [formcontrolname="contractAmount"]')
      .eq(1)
      .type(testData.subcontracts[1].contractAmount);

    cy.get('[formarrayname="subcontracts"] [formcontrolname="currencyCode"]')
      .eq(1)
      .click();

    cy.get('.mat-option-text')
      .contains(testData.subcontracts[1].currencyCode)
      .click();

    cy.get('[formarrayname="subcontracts"] [formcontrolname="supplierName"]')
      .eq(1)
      .type(testData.subcontracts[1].supplierName);

    cy.get('[formarrayname="subcontracts"] [formcontrolname="vatNumber"]')
      .eq(1)
      .type(testData.subcontracts[1].vatNumber);

    cy.contains('button', 'Save changes')
      .click();

    cy.contains('Subcontract(s) saved successfully')
      .should('be.visible');
  }

  //endregion

  //region TB-745 METHODS
  function prepareTestData(testData, application) {
    cy.loginByRequest(user.applicantUser.email);

    preparePartnersList(testData, application);

    application.lumpSums = [];
    application.description.workPlan[0].activities[0].cypressReferencePartner = application.partners[0].details.abbreviation;
    application.description.workPlan[0].activities[1].cypressReferencePartner = application.partners[1].details.abbreviation;
  }

  function preparePartnersList(testData, application) {
    application.partners = [];

    for (let i = 0; i < 49; i++) {
      const tempPartner = JSON.parse(JSON.stringify(testData.partner));

      if (i === 0) {
        application.partners.push(tempPartner);
        continue;
      }

      const index = i + 1;

      tempPartner.details.abbreviation = "PP" + index;
      tempPartner.details.role = "PARTNER";
      application.partners.push(tempPartner);
    }
  }

  function openModification(applicationId) {
    cy.loginByRequest(user.programmeUser.email);

    cy.visit(`app/project/detail/${applicationId}/modification`, {failOnStatusCode: false});
    cy.contains('Open new modification')
      .click();
    cy.get('jems-confirm-dialog')
      .should('be.visible');
    cy.get('jems-confirm-dialog')
      .find('.mat-dialog-actions')
      .contains('Confirm')
      .click();
    cy.contains('You have successfully opened a modification')
      .should('be.visible');
  }

  function disableSelectedPartners(application, applicationId, partnerIdsToDisable) {
    cy.loginByRequest(user.applicantUser.email);
    cy.visit(`app/project/detail/${applicationId}/applicationFormPartner`, {failOnStatusCode: false});
    disablePartnersByIds(application, partnerIdsToDisable);
    submitProjectApp(applicationId);
  }

  function verifyPartnerChangesBeforeApproving(application, applicationId, partnerIdsToDisable) {
    const disabledPartnerAbbreviation = application.partners[partnerIdsToDisable[0]].details.abbreviation;

    cy.loginByRequest(user.admin.email);

    cy.visit(`app/project/detail/${applicationId}/applicationFormPartner`, {failOnStatusCode: false})
      .then(() => {
        cy.get('mat-sidenav')
          .should('be.visible');

        partnerIdsToDisable.forEach(id => {
          cy.contains('mat-row', application.partners[id].details.abbreviation)
            .contains('Inactive')
            .scrollIntoView()
            .should('exist');

          cy.contains('mat-row', application.partners[id].details.abbreviation)
            .contains('mat-icon', 'person_off')
            .scrollIntoView()
            .should('exist');

          verifyIconsInPartnerDetails(application, id, false);
          verifyIconsInProjectPartners(application, id, true);
        });

        verifyIconsInProjectPrivileges(application, partnerIdsToDisable, false);
        verifyDeactivatedPartnerBannerDisplay("Partner details", disabledPartnerAbbreviation, false);
        verifyDeactivatedPartnerBannerDisplay("Project partners", disabledPartnerAbbreviation, true);
      });
  }

  function verifyIconsInPartnerDetails(application, id, shouldIconsBeDisplayed) {
    const displayFlag = shouldIconsBeDisplayed ? 'be.visible' : 'not.exist';

    cy.get('mat-expansion-panel-header:contains("Partner details")')
      .next('div')
      .find(`li:contains("${application.partners[id].details.abbreviation}")`)
      .then((foundElement) => {
        if (shouldIconsBeDisplayed) {
          cy.wrap(foundElement)
            .contains('mat-icon', 'person_off')
            .scrollIntoView()
            .should(displayFlag);
        } else {
          cy.wrap(foundElement)
            .contains('mat-icon', 'person_off')
            .should(displayFlag);
        }
      });
  }

  function verifyIconsInProjectPartners(application, id, shouldIconsBeDisplayed) {
    const displayFlag = shouldIconsBeDisplayed ? 'exist' : 'not.exist';

    cy.get('mat-expansion-panel-header:contains("Project partners")')
      .next('div')
      .find(`li:contains("${application.partners[id].details.abbreviation}")`)
      .contains('mat-icon', 'person_off')
      .then((foundElement) => {
        if (shouldIconsBeDisplayed) {
          cy.wrap(foundElement)
            .scrollIntoView()
            .should(displayFlag);
        } else {
          cy.wrap(foundElement)
            .should(displayFlag);
        }
      });
  }

  function verifyIconsInProjectPrivileges(application, partnerIdsToDisable, shouldIconsBeDisplayed) {
    const displayFlag = shouldIconsBeDisplayed ? 'exist' : 'not.exist';

    cy.contains('Project privileges')
      .click()
      .then(() => {
        partnerIdsToDisable.forEach(id => {
          cy.get(`mat-expansion-panel-header:contains("${application.partners[id].details.abbreviation}")`)
            .first()
            .scrollIntoView()
            .contains('mat-icon', 'person_off')
            .should(displayFlag);
        });
      });
  }

  function verifyDeactivatedPartnerBannerDisplay(headerTitle, disabledPartnerAbbreviation, shouldBannerBeDisplayed) {
    const displayFlag = shouldBannerBeDisplayed ? 'exist' : 'not.exist';

    cy.get(`mat-expansion-panel-header:contains(${headerTitle})`)
      .next('div')
      .find(`span:contains("${disabledPartnerAbbreviation}")`)
      .first()
      .click()
      .then(() => {
        cy.contains('div', "You are currently viewing a deactivated partner.")
          .should(displayFlag);
      });
  }

  function submitProjectApp(applicationId) {
    cy.runPreSubmissionCheck(applicationId);
    cy.submitProjectApplication(applicationId);
  }

  function verifyPartnerAvailability(applicationId, shouldPartnerBeDisplayed) {
    cy.visit(`app/project/detail/${applicationId}`, {failOnStatusCode: false});

    verifyPartnerInPartnerDetails(shouldPartnerBeDisplayed);
    verifyPartnerAvailabilityInProjectPrivileges(shouldPartnerBeDisplayed);
  }

  function verifyPartnerInPartnerDetails(shouldPartnerBeDisplayed) {

    cy.get('mat-expansion-panel:contains("Partner details") ul')
      .then((foundElement) => {
        if (shouldPartnerBeDisplayed) {
          cy.wrap(foundElement)
            .find(`li:contains("PP50")`)
            .scrollIntoView()
            .should('exist');
        } else {
          cy.wrap(foundElement)
            .find(`li:contains("PP50")`)
            .should('not.exist');
        }
      });
  }

  function verifyPartnerAvailabilityInProjectPrivileges(shouldPartnerBeDisplayed) {
    const displayFlag = shouldPartnerBeDisplayed ? 'exist' : 'not.exist';

    cy.contains('Project privileges')
      .click()
      .then((foundElement) => {
        if (shouldPartnerBeDisplayed) {
          cy.wrap(foundElement)
            .get(`mat-expansion-panel-header:contains("PP50")`)
            .scrollIntoView()
            .should(displayFlag);
        } else {
          cy.wrap(foundElement)
            .get(`mat-expansion-panel-header:contains("PP50")`)
            .should(displayFlag);
        }
      });
  }

  function disablePartnersByIds(application, partnerIdsToDisable) {
    partnerIdsToDisable.forEach(id => {
      cy.contains('mat-row', application.partners[id].details.abbreviation)
        .contains('button', 'Deactivate partner')
        .click();

      cy.contains('button', 'Confirm')
        .click();

      cy.contains('div', `Partner "${application.partners[id].details.abbreviation}" deactivated successfully`)
        .scrollIntoView()
        .should('be.visible');

      cy.contains('div', `Partner "${application.partners[id].details.abbreviation}" deactivated successfully`)
        .should('not.exist');

      cy.contains('mat-row', application.partners[id].details.abbreviation)
        .contains('button', 'Deactivate partner')
        .scrollIntoView()
        .should('be.disabled');

      cy.contains('mat-row', application.partners[id].details.abbreviation)
        .contains('Inactive')
        .scrollIntoView()
        .should('exist');

      cy.contains('mat-row', application.partners[id].details.abbreviation)
        .contains('mat-icon', 'person_off')
        .scrollIntoView()
        .should('exist');

      verifyIconsInProjectPartners(application, id, true);
    });
  }

  function verify50PartnersLimit(applicationId) {
    cy.loginByRequest(user.applicantUser.email).then(() => {
      cy.visit(`app/project/detail/${applicationId}/applicationFormPartner`, {failOnStatusCode: false});
      cy.contains('Add new partner')
        .should('not.exist');
    });
  }

  //endregion

  //region TB-840 METHODS
  function createCostAsExpenditure(declaredAmountFormatted: string) {
    cy.contains('List of expenditures').click({force: true});
    cy.contains('add expenditure').click();
    cy.contains('Please select a cost category').click();
    cy.contains('mat-option', 'Staff costs').click();
    cy.get('mat-row').last().find('.mat-column-declaredAmount').scrollIntoView().click();
    cy.get('mat-row').last().find('.mat-column-declaredAmount').find('input').scrollIntoView().type(declaredAmountFormatted);
    cy.intercept(/api\/project\/report\/partner\/identification\/byPartnerId\/[0-9]+\/byReportId\/[0-9]+/).as('getPartner');
    cy.contains('Save changes').click();
    cy.wait('@getPartner');
  }

  function createLumpSumAsExpenditure() {
    cy.contains('List of expenditures').click({force: true});
    cy.contains('add expenditure').click();
    cy.get('mat-row').last().find('.mat-column-costOptions').click();
    cy.contains('mat-option', 'Implementation Lump sum DE - Period 1').click();
    cy.intercept(/api\/project\/report\/partner\/identification\/byPartnerId\/[0-9]+\/byReportId\/[0-9]+/).as('getPartner');
    cy.contains('Save changes').click();
    cy.wait('@getPartner');
  }

  function createUnitCostsAsExpenditures() {
    cy.contains('List of expenditures').click({force: true});
    cy.contains('add expenditure').click();
    cy.get('mat-row').last().find('.mat-column-costOptions').click();
    cy.contains('mat-option', 'Unit cost multi - all DE').click();
    cy.contains('add expenditure').click();
    cy.get('mat-row').last().find('.mat-column-costOptions').click();
    cy.contains('mat-option', 'Unit cost single - External DE').click();
    cy.intercept(/api\/project\/report\/partner\/identification\/byPartnerId\/[0-9]+\/byReportId\/[0-9]+/).as('getPartner');
    cy.contains('Save changes').click();
    cy.wait('@getPartner');
  }

  function submitPartnerReport() {
    cy.contains('Submit').click({force: true});
    cy.contains('Run pre-submission check').click();
    cy.contains('Submit partner report').click();
    cy.contains('Confirm').should('be.visible').click();
    cy.get('mat-chip.status-Submitted').should('be.visible');
  }

  //endregion


  //region TB-742 METHODS
  function createReportByAttachingFiles(projectId: number, partnerId: number, reportId: number, isFirstReport: boolean) {
    const procurementIndex = isFirstReport ? 0 : 1;
    const uploadedFilePath = isFirstReport ? 'cypress/fixtures/project/reporting/partner-report-attachment01.txt' :
      'cypress/fixtures/project/reporting/fileToUpload.txt';
    cy.updatePartnerReportIdentification(partnerId, reportId, partnerReportIdentification);

    cy.visit(`/app/project/detail/${projectId}/reporting/${partnerId}/reports/${reportId}/workplan`, {failOnStatusCode: false});
    cy.contains('mat-panel-title', 'Work package 1')
      .click();

    cy.get('div.activity-container > jems-multi-language-container input').eq(0)
      .scrollIntoView()
      .invoke('show')
      .selectFile(uploadedFilePath)
      .invoke('hide');

    cy.visit(`/app/project/detail/${projectId}/reporting/${partnerId}/reports/${reportId}/expenditures`, {failOnStatusCode: false});
    clickAddExpenditure();
    cy.contains('#expenditure-costs-table mat-select', 'Please select a cost category')
      .click();
    cy.contains('mat-option', 'Travel and accommodation')
      .click();
    saveExpenditure();
    cy.get('#expenditure-costs-table mat-cell.mat-column-uploadFunction input').eq(0)
      .scrollIntoView()
      .invoke('show')
      .selectFile(uploadedFilePath)
      .invoke('hide');

    cy.fixture('api/partnerReport/partnerProcurement.json').then(partnerProcurements => {
      cy.addPublicProcurement(partnerId, reportId, partnerProcurements[procurementIndex]).then(procurement => {
        cy.visit(`/app/project/detail/${projectId}/reporting/${partnerId}/reports/${reportId}/procurements/${procurement.id}`, {failOnStatusCode: false});
        cy.contains('button', 'Upload file').click();
        cy.get('input[type=file]').selectFile(uploadedFilePath, {force: true});
      });
    });

    cy.visit(`/app/project/detail/${projectId}/reporting/${partnerId}/reports/${reportId}/contribution`, {failOnStatusCode: false});
    cy.get('#contributions-table mat-cell.mat-column-attachment input').eq(0)
      .scrollIntoView()
      .invoke('show')
      .selectFile(uploadedFilePath)
      .invoke('hide');

    if (isFirstReport) {
      cy.visit(`/app/project/detail/${projectId}/reporting/${partnerId}/reports/${reportId}/annexes`, {failOnStatusCode: false});
      cy.contains('button', 'Upload file').click();
      cy.get('input[type=file]').selectFile(uploadedFilePath, {force: true});
    }

    cy.wait(2000);
  }

  function addFileDescription(rowIndex: number) {
    const testInput = faker.word.noun();
    cy.get('mat-row').eq(rowIndex).contains('button', 'edit').scrollIntoView().click();
    cy.get('[label="file.table.column.name.description"] textarea').click().scrollIntoView().type(testInput);
    cy.contains('button', 'Save').scrollIntoView().click();

    cy.contains(new RegExp('File description for \'[a-zA-Z-.\\d]+\' has been updated')).should('be.visible');
    cy.contains(new RegExp('File description for \'[a-zA-Z-.\\d]+\' has been updated')).should('not.exist');
  }

  function validateDownloadFile(rowIndex: number, partnerId: number) {
    cy.get('mat-row').eq(rowIndex).contains('button', 'download').scrollIntoView().clickToDownload(`api/project/report/partner/byPartnerId/${partnerId}/?*`, 'txt').then(returnValue => {
      cy.wrap(returnValue.fileName === 'fileToUpload.txt').should('eq', true);
    });
  }

  //endregion

  //region TB-993 METHODS

  function validateBudgetReportExportFile(applicationId: number, partnerId: number, reportId: number, expenditures: any[], isEnglish: boolean) {
    cy.contains('button', 'Export').clickToDownload(`/api/project/report/partner/byPartnerId/${partnerId}/byReportId/${reportId}/export?*`, 'xlsx').then(exportFile => {
      expect(exportFile.content[0].data.length).to.equals(expenditures.length + 2); // 2 for extra rows
      expect(exportFile.content[0].data[0][0]).contains(`${applicationId} - Lead Partner`);
      const translationIndex = isEnglish ? 1 : 0;
      for (let i = 0; i < expenditures.length; i++) {
        expect(exportFile.content[0].data[i + 2][1]).to.equals(expenditures[i].costCategoryInFile);
        expect(exportFile.content[0].data[i + 2][2]).to.equals(expenditures[i].internalReferenceNumber);
        expect(exportFile.content[0].data[i + 2][3]).to.equals(expenditures[i].totalValueInvoice);
        expect(exportFile.content[0].data[i + 2][4]).to.equals(expenditures[i].description[translationIndex].translation);
        expect(exportFile.content[0].data[i + 2][5]).to.equals(expenditures[i].comment[translationIndex].translation);
      }
    });
  }

  function validatePartnerReportExportFile(applicationId: number, partnerId: number, reportId: number) {
    cy.contains('button', 'Export').clickToDownload(`/api/project/report/partner/byPartnerId/${partnerId}/byReportId/${reportId}/export?*`, 'pdf').then(fileName => {
      expect(fileName).contains(applicationId);
    });
  }

  //endregion

  //region TB-936 METHODS
  function addExpendituresAndSubmit(partnerId, reportId) {
    cy.updatePartnerReportIdentification(partnerId, reportId, partnerReportIdentification);
    cy.updatePartnerReportExpenditures(partnerId, reportId, partnerReportExpenditures)
      .then(response => {
        for (let i = 0; i < response.length; i++) {
          partnerParkedExpenditures[i].id = response[i].id;
        }
      });
    cy.runPreSubmissionPartnerReportCheck(partnerId, reportId);
    cy.submitPartnerReport(partnerId, reportId);
  }

  function parkExpenditureRowByIndex(rowIndex) {
    cy.contains('a', 'Expenditure verification').click();

    cy.get('#expenditure-costs-table mat-cell.mat-column-parked mat-slide-toggle')
      .eq(rowIndex)
      .click();

    cy.contains('button', 'Save changes')
      .click();
  }

  function verifyParkedExpenditureRowByIndex(rowIndex, partOfSample, partOfSampleEnabled, certifiedAmount) {
    cy.get('#expenditure-costs-table mat-cell.mat-column-partOfSample mat-slide-toggle input')
      .eq(rowIndex)
      .should(partOfSample ? 'be.checked' : 'not.checked')
      .should(partOfSampleEnabled ? 'be.enabled' : 'be.disabled');

    cy.get('#expenditure-costs-table mat-cell.mat-column-certifiedAmount input')
      .eq(rowIndex)
      .should('have.value', formatAmount(certifiedAmount));
  }

  function verifyParkedOverviewRowByIndex(certifiedAmount) {
    cy.get('.mat-tab-header-pagination-after').click();
    cy.wait(200);
    cy.contains('a', 'Overview and Finalize').click();

    cy.get("#overview-of-control-work-table mat-cell.mat-column-inControlSample")
      .should('be.visible')
      .should('have.text', `${formatAmount(certifiedAmount)}`);
  }

  function deductExpenditureRowByIndex(rowIndex, deductedAmount, typologyOfError) {
    cy.wait(300);
    cy.get('#expenditure-costs-table mat-cell.mat-column-deductedAmount input')
      .eq(rowIndex)
      .clear()
      .type(`${deductedAmount}`);

    cy.get('#expenditure-costs-table mat-cell.mat-column-typologyOfErrorId mat-select')
      .eq(rowIndex)
      .click();
    cy.get('mat-option').contains(typologyOfError).click();

    cy.contains('button', 'Save changes')
      .click();
  }

  function verifyDeductedExpenditureRowByIndex(rowIndex, partOfSample, partOfSampleEnabled, certifiedAmount) {
    cy.get('#expenditure-costs-table mat-cell.mat-column-partOfSample mat-slide-toggle input')
      .eq(rowIndex)
      .should(partOfSample ? 'be.checked' : 'not.checked')
      .should(partOfSampleEnabled ? 'be.enabled' : 'be.disabled');

    cy.get('#expenditure-costs-table mat-cell.mat-column-certifiedAmount input')
      .eq(rowIndex)
      .should('have.value', formatAmount(certifiedAmount));
  }

  function verifyDeductedOverviewRowByIndex(typologyOfError, certifiedAmount) {
    cy.get('.mat-tab-header-pagination-after').click();
    cy.wait(200);
    cy.contains('a', 'Overview and Finalize').click();

    cy.contains('#overview-of-deduction-table:last-of-type mat-row', typologyOfError)
      .find('mat-cell.mat-column-total')
      .should('have.text', formatAmount(certifiedAmount));
  }

  function finalizeControlWork() {
    // Set Controller
    cy.get('.mat-tab-header-pagination-before').click();
    cy.wait(200);
    cy.contains('Control Identification').click();
    cy.get('input[name="controlUser"]').eq(0).click();
    cy.get('mat-option:last-of-type').click();
    cy.contains('button', 'Save changes').should('be.visible').click();
    cy.contains('Successfully saved the control identification data').should('be.visible');

    // Finalize
    cy.get('.mat-tab-header-pagination-after').click();
    cy.contains('Overview and Finalize').should('be.visible').click();
    cy.contains('Run pre-submission check').click();
    cy.contains('button', 'Finalize control').should('be.enabled').click();
    cy.contains('Confirm').should('be.visible').click();
    cy.get('mat-chip.status-Certified').should('be.visible');
  }

  function verifyCannotEdit(applicationId, partnerId) {
    cy.visit(`/app/project/detail/${applicationId}/reporting/${partnerId}/reports`, {failOnStatusCode: false});
    cy.contains('mat-row', 'Certified')
      .contains('button', 'Open controller work')
      .click();

    cy.contains('a', 'Expenditure verification').click();
    cy.get('#expenditure-costs-table mat-cell.mat-column-partOfSample mat-slide-toggle input')
      .each((el) => cy.wrap(el).should('be.disabled'));
    cy.get('#expenditure-costs-table mat-cell.mat-column-parked mat-slide-toggle input')
      .each((el) => cy.wrap(el).should('be.disabled'));
  }

  //endregion

  function formatAmount(amount) {
    return new Intl.NumberFormat('de-DE', {minimumFractionDigits: 2}).format(amount);
  }
});
