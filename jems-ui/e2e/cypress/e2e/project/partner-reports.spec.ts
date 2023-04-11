import {faker} from '@faker-js/faker';
import user from '../../fixtures/users.json';
import partner from '../../fixtures/api/application/partner/partner.json';
import call from "../../fixtures/api/call/1.step.call.json";
import approvalInfo from "../../fixtures/api/application/modification/approval.info.json";
import {loginByRequest} from "../../support/login.commands";
import application from "../../fixtures/api/application/application.json";
import partnerProcurement from "../../fixtures/api/partnerReport/partnerProcurement.json";
import partnerReportIdentification from "../../fixtures/api/partnerReport/partnerReportIdentification.json";
import partnerReportExpenditures from "../../fixtures/api/partnerReport/partnerReportExpenditures.json";
import partnerParkedExpenditures from "../../fixtures/api/partnerReport/partnerParkedExpenditures.json";

const costCategories = [
  'Travel and accommodation',
  'External expertise and services',
  'Equipment',
  'Infrastructure and works'
]

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
        const secondPartner = application.partners[1].details.abbreviation
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
              cy.addPublicProcurement(partnerId2, reportId, partnerProcurement[1])
            });

          cy.addPartnerReport(partnerId1)
            .then(reportId => {
              updatePartnerReportDetails(partnerId1, reportId);
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
              removeExpenditureByRowIndex(0)
              verifyExpendituresTableSize(8);

              // Delete another expenditure that has no attachments
              removeExpenditureByRowIndex(2)
              verifyExpendituresTableSize(7);

              // We have to save before going for parked expenditure table modification
              saveExpenditure();

              // Delete a parked expenditure
              removeParkedExpenditureByRowIndex(0)
            });
        });
      });
    });
  });

  it('TB-740 Partner user can report public procurements', function () {
    cy.fixture('project/reporting/TB-740.json').then(testData => {
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
                    cy.addSubcontractorToProcurement(partnerId1, reportId, procurement.id, testData.subcontracts[0])
                    cy.addAttachmentToProcurement('fileToUpload.txt', 'project/reporting/', partnerId1, reportId, procurement.id);
                    cy.runPreSubmissionPartnerReportCheck(partnerId1, reportId);
                    cy.submitPartnerReport(partnerId1, reportId);
                  })
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
                      .click()
                    cy.get('input[name="contractName"]')
                      .type(partnerProcurement[0].contractName);
                    cy.get('input[name="vatNumber"]')
                      .type(partnerProcurement[0].vatNumber);
                    cy.contains('button', 'Create')
                      .click();
                    cy.contains('use.case.create.project.partner.report.procurement.contractName.needs.to.be.unique (error code: S-CPPRP-003)')
                      .should('be.visible');

                    // Partners with EURO currency should be able to save only EUR as currency for their procurement
                    cy.get('#currency')
                      .click();
                    cy.contains('mat-option', 'AMD')
                      .click();
                    cy.contains('button', 'Create')
                      .click();
                    cy.contains('use.case.create.project.partner.report.procurement.invalid.currency (error code: S-CPPRP-004)')
                      .should('be.visible');
                    cy.contains('button', 'Discard changes')
                      .click();

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

                    // Upload an attachment, then changes it's description and delete the attachment
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
                      .type('Description test for the attachment');

                    cy.contains('button', 'Save')
                      .click();

                    cy.contains('File description for \'fileToUpload.txt\' has been updated.')
                      .should('be.visible');

                    cy.contains('File description for \'fileToUpload.txt\' has been updated.')
                      .should('not.exist');

                    // file can be deleted
                    cy.get('jems-file-list-table')
                      .contains('mat-icon', 'delete')
                      .scrollIntoView()
                      .click();

                    cy.contains('button', 'Confirm')
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

                    cy.contains('Save changes')
                      .click();

                    // Delete the procurement
                    cy.contains('Public procurements')
                      .click();

                    // Only the procurement created in the current partner report can be deleted
                    cy.get('mat-row')
                      .eq(1)
                      .contains('mat-icon', 'delete')
                      .should('not.exist');

                    cy.get('mat-row')
                      .eq(0)
                      .contains('mat-icon', 'delete')
                      .should('be.visible')
                      .click();

                    cy.contains('Confirm')
                      .click();

                    // links to procurement are deleted from LoE
                    cy.contains('List of expenditures')
                      .click();

                    cy.get(`#expenditure-costs-table mat-cell.mat-column-contractId`)
                      .scrollIntoView()
                      .should('contain.text', 'N/A');

                    // Open a procurement created in previous report
                    // Info, beneficial owner, subcontractor and attachment description for a procurement
                    // created in a previous report can't be edited
                    cy.contains('Public procurements')
                      .click();

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
                      .type('Description test for the attachment');

                    cy.contains('button', 'Save')
                      .click();

                    cy.contains('File description for \'fileToUpload.txt\' has been updated.')
                      .should('be.visible');

                    cy.contains('File description for \'fileToUpload.txt\' has been updated.')
                      .should('not.exist');

                    // file can be deleted
                    cy.get('jems-file-list-table')
                      .contains('mat-icon', 'delete')
                      .scrollIntoView()
                      .click();

                    cy.contains('button', 'Confirm')
                      .click();

                    cy.contains("File \'fileToUpload.txt\' has been deleted successfully.")
                      .should('be.visible');
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
            loginByRequest(user.applicantUser.email).then(() => {
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
            verify30PartnersLimit(applicationId);
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
          console.log(unitCostExternalFormatted)
          console.log(unitCostsFormatted)
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
    testData.controllerRole.name = `controllerRole_${faker.random.alphaNumeric(5)}`;
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

            cy.assignInstitution(testData.controllerAssignment);
          });
      });
  }

  function updatePartnerReportDetails(partnerId, reportId) {
    cy.addPublicProcurement(partnerId, reportId, partnerProcurement[0])
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

  function performControlWork(testData, reportId, partnerId1) {
    cy.loginByRequest(testData.controllerUser.email);
    cy.startControlWork(partnerId1, reportId);
    cy.setExpenditureItemsAsParked(partnerId1, reportId, partnerParkedExpenditures);
    cy.finalizeControl(partnerId1, reportId);
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
        })
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
        })
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
          })
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
          })
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
          })
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
        })
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

    for (let i = 0; i < 29; i++) {
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

    cy.visit(`app/project/detail/${applicationId}/modification`, {failOnStatusCode: false})
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
            .should('be.visible');

          cy.contains('mat-row', application.partners[id].details.abbreviation)
            .contains('mat-icon', 'person_off')
            .scrollIntoView()
            .should('be.visible');

          verifyIconsInPartnerDetails(application, id, false);
          verifyIconsInProjectPartners(application, id, true);
        });

        verifyIconsInProjectPrivileges(application, partnerIdsToDisable, false)
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
            .should(displayFlag)
        } else {
          cy.wrap(foundElement)
            .contains('mat-icon', 'person_off')
            .should(displayFlag)
        }
      })
  }

  function verifyIconsInProjectPartners(application, id, shouldIconsBeDisplayed) {
    const displayFlag = shouldIconsBeDisplayed ? 'be.visible' : 'not.exist';

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
      })
  }

  function verifyIconsInProjectPrivileges(application, partnerIdsToDisable, shouldIconsBeDisplayed) {
    const displayFlag = shouldIconsBeDisplayed ? 'be.visible' : 'not.exist';

    cy.contains('Project privileges')
      .click()
      .then(() => {
        partnerIdsToDisable.forEach(id => {
          cy.get(`mat-expansion-panel-header:contains("${application.partners[id].details.abbreviation}")`)
            .scrollIntoView()
            .contains('mat-icon', 'person_off')
            .should(displayFlag)
        });
      });
  }

  function verifyDeactivatedPartnerBannerDisplay(headerTitle, disabledPartnerAbbreviation, shouldBannerBeDisplayed) {
    const displayFlag = shouldBannerBeDisplayed ? 'be.visible' : 'not.exist';

    cy.get(`mat-expansion-panel-header:contains(${headerTitle})`)
      .next('div')
      .find(`span:contains("${disabledPartnerAbbreviation}")`)
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
    const displayFlag = shouldPartnerBeDisplayed ? 'be.visible' : 'not.exist';

    cy.get('mat-expansion-panel-header:contains("Partner details")')
      .next('div')
      .then((foundElement) => {
        if (shouldPartnerBeDisplayed) {
          cy.wrap(foundElement)
            .find(`li:contains("PP30")`)
            .scrollIntoView()
            .should(displayFlag)
        } else {
          cy.wrap(foundElement)
            .find(`li:contains("PP30")`)
            .should(displayFlag)
        }
      });
  }

  function verifyPartnerAvailabilityInProjectPrivileges(shouldPartnerBeDisplayed) {
    const displayFlag = shouldPartnerBeDisplayed ? 'be.visible' : 'not.exist';

    cy.contains('Project privileges')
      .click()
      .then((foundElement) => {
        if (shouldPartnerBeDisplayed) {
          cy.wrap(foundElement)
            .get(`mat-expansion-panel-header:contains("PP30")`)
            .scrollIntoView()
            .should(displayFlag)
        } else {
          cy.wrap(foundElement)
            .get(`mat-expansion-panel-header:contains("PP30")`)
            .should(displayFlag)
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
        .should('be.visible');

      cy.contains('mat-row', application.partners[id].details.abbreviation)
        .contains('mat-icon', 'person_off')
        .scrollIntoView()
        .should('be.visible');

      verifyIconsInProjectPartners(application, id, true);
    })
  }

  function verify30PartnersLimit(applicationId) {
    loginByRequest(user.applicantUser.email).then(() => {
      cy.visit(`app/project/detail/${applicationId}/applicationFormPartner`, {failOnStatusCode: false});
      cy.contains('Add new partner')
        .click()
      cy.contains('button', 'Partner')
        .click();
      cy.get(`[name='abbreviation']`)
        .type('PP31');
      cy.get(`[name='legalStatusId']`)
        .click()
      cy.contains('Public')
        .click();
      cy.contains('button', 'Create')
        .click();

      cy.contains('Failed to create the project partner (error code: S-CPP)')
        .should('be.visible');
      cy.contains('It is not possible to add more than "30" partner to the project application (error code: S-CPP-005)')
        .should('be.visible');
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
    cy.contains('Save changes').click();
  }

  function createLumpSumAsExpenditure() {
    cy.contains('List of expenditures').click({force: true});
    cy.contains('add expenditure').click();
    cy.get('mat-row').last().find('.mat-column-costOptions').click();
    cy.contains('mat-option', 'Implementation Lump sum DE - Period 1').click();
    cy.contains('Save changes').click();
  }

  function createUnitCostsAsExpenditures() {
    cy.contains('List of expenditures').click({force: true});
    cy.contains('add expenditure').click();
    cy.get('mat-row').last().find('.mat-column-costOptions').click();
    cy.contains('mat-option', 'Unit cost multi - all DE').click();
    cy.contains('add expenditure').click();
    cy.get('mat-row').last().find('.mat-column-costOptions').click();
    cy.contains('mat-option', 'Unit cost single - External DE').click();
    cy.contains('Save changes').click();
  }

  function submitPartnerReport() {
    cy.contains('Submit').click({force: true});
    cy.contains('Run pre-submission check').click();
    cy.contains('Submit partner report').click();
    cy.contains('Confirm').should('be.visible').click();
  }

  //endregion

});
