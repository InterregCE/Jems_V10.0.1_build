import {loginByRequest} from './login.commands';

declare global {
    namespace Cypress {
        interface Chainable {
            addPartnerReport(partnerId: number);
            
            createCertifiedPartnerReport(partnerId: number, partnerReportDetails: any, controllerUserEmail): any;

            updatePartnerReportIdentification(partnerId: number, reportId: number, partnerReportIdentification);

            updatePartnerReportExpenditures(partnerId: number, reportId: number, partnerReportExpenditures);

            runPreSubmissionPartnerReportCheck(partnerId: number, reportId: number);

            submitPartnerReport(partnerId: number, reportId: number);

            addPublicProcurement(partnerId: number, reportId: number, procurementDetails);

            addBeneficialOwnerToProcurement(partnerId: number, reportId: number, procurementId: number, beneficialOwnerDetails: any);

            addSubcontractorToProcurement(partnerId: number, reportId: number, procurementId: number, subcontractorDetails: any);

            addAttachmentToProcurement(fileName: string, filePath: string, partnerId: number, reportId: number, procurementId: number);

            getRegularLumpSums(applicationId: number);

            getFastTrackLumpSums(applicationId: number);

            getUnitCostsByPartnerAndReportIds(partnerId: number, reportId: number);

            getLumpSumsByPartnerAndReportIds(partnerId: number, reportId: number);
        }
    }
}

Cypress.Commands.add('addPartnerReport', (partnerId: number) => {
    addPartnerReport(partnerId);
});

Cypress.Commands.add('createCertifiedPartnerReport', (partnerId: number, partnerReportDetails, controllerUserEmail) => {
    addPartnerReport(partnerId).then(partnerReportId => {
        cy.updatePartnerReportIdentification(partnerId, partnerReportId, partnerReportDetails.partnerReport.identification);
        cy.updatePartnerReportExpenditures(partnerId, partnerReportId, partnerReportDetails.partnerReport.expenditures).then(expenditureList => {
            expenditureList.forEach((expenditure, i) => {
                partnerReportDetails.controlWork.expenditureVerification[i].id = expenditure.id;
            });

            cy.runPreSubmissionPartnerReportCheck(partnerId, partnerReportId);
            cy.submitPartnerReport(partnerId, partnerReportId);

            loginByRequest(controllerUserEmail);
            cy.startControlWork(partnerId, partnerReportId);
            cy.updateControlReportIdentification(partnerId, partnerReportId, partnerReportDetails.controlWork.identification);
            cy.updateControlReportExpenditureVerification(partnerId, partnerReportId, partnerReportDetails.controlWork.expenditureVerification);
            cy.finalizeControl(partnerId, partnerReportId);
            cy.get('@currentUser').then((currentUser: any) => {
                loginByRequest(currentUser.name);
            });
        });
        cy.wrap(partnerReportId);
    });
});

Cypress.Commands.add('updatePartnerReportIdentification', (partnerId: number, reportId: number, partnerReportIdentification) => {
    updatePartnerReportIdentification(partnerId, reportId, partnerReportIdentification);
});

Cypress.Commands.add('updatePartnerReportExpenditures', (partnerId: number, reportId: number, partnerReportExpenditures) => {
    updatePartnerReportExpenditures(partnerId, reportId, partnerReportExpenditures);
});

Cypress.Commands.add('runPreSubmissionPartnerReportCheck', (partnerId: number, reportId: number) => {
    cy.request({
        method: 'POST',
        url: `api/project/report/partner/preCheck/${partnerId}/${reportId}`,
    });
});

Cypress.Commands.add('submitPartnerReport', (partnerId: number, reportId: number) => {
    cy.request({
        method: 'POST',
        url: `api/project/report/partner/submit/${partnerId}/${reportId}`,
    });
});

Cypress.Commands.add('addPublicProcurement', (partnerId: number, reportId: number, procurementDetails) => {
    cy.request({
        method: 'POST',
        url: `api/project/report/partner/procurement/byPartnerId/${partnerId}/byReportId/${reportId}`,
        body: procurementDetails
    }).then(response => response.body);
});

Cypress.Commands.add('getRegularLumpSums', (applicationId: number) => {
    cy.request({
        method: 'GET',
        url: `api/project/byId/${applicationId}`
    }).then((response) => {
        return response.body.callSettings.lumpSums.filter(lumpSum => !lumpSum.fastTrack);
    })
});

Cypress.Commands.add('getFastTrackLumpSums', (applicationId: number) => {
    cy.request({
        method: 'GET',
        url: `api/project/byId/${applicationId}`
    }).then((response) => {
        return response.body.callSettings.lumpSums.filter(lumpSum => lumpSum.fastTrack);
    })
});

Cypress.Commands.add('getLumpSumsByPartnerAndReportIds', (partnerId: number, reportId: number) => {
    cy.request({
        method: 'GET',
        url: `api/project/report/partner/expenditure/byPartnerId/${partnerId}/byReportId/${reportId}/lumpSums`
    }).then(response => response.body)
});

Cypress.Commands.add('getUnitCostsByPartnerAndReportIds', (partnerId: number, reportId: number) => {
    cy.request({
        method: 'GET',
        url: `api/project/report/partner/expenditure/byPartnerId/${partnerId}/byReportId/${reportId}/unitCosts`
    }).then(response => response.body);
});

Cypress.Commands.add('addBeneficialOwnerToProcurement', (partnerId: number, reportId: number, procurementId: number, beneficialOwnerDetails: any) => {
    cy.request({
        method: 'PUT',
        url: `api/project/report/partner/procurement/beneficialOwner/byPartnerId/${partnerId}/byReportId/${reportId}/byProcurementId/${procurementId}`,
        body: [beneficialOwnerDetails]
    }).then(response => response.body);
});

Cypress.Commands.add('addAttachmentToProcurement', (fileName: string, filePath: string, partnerId: number, reportId: number, procurementId: number) => {
    const filePathWithFileName = filePath + fileName;
    const fileType = "application/text";

    cy.fixture(filePathWithFileName, "binary")
        .then((txtBin) => Cypress.Blob.binaryStringToBlob(txtBin))
        .then((blob) => {
            const formData = new FormData();
            formData.append("file", blob, fileName);
            formData.append("file_type", fileType);

            cy.request({
                method: 'POST',
                url: `api/project/report/partner/procurement/attachment/byPartnerId/${partnerId}/byReportId/${reportId}/byProcurementId/${procurementId}`,
                body: formData
            })
        })
})

Cypress.Commands.add('addSubcontractorToProcurement', (partnerId: number, reportId: number, procurementId: number, subcontractorDetails: any) => {
    cy.request({
        method: 'PUT',
        url: `api/project/report/partner/procurement/subcontractor/byPartnerId/${partnerId}/byReportId/${reportId}/byProcurementId/${procurementId}`,
        body: [subcontractorDetails]
    }).then(response => response.body);
});

function addPartnerReport(partnerId: number) {
    return cy.request({
        method: 'POST',
        url: `api/project/report/partner/create/${partnerId}`,
    }).then(response => {
        return response.body.id
    });
}

function updatePartnerReportIdentification(partnerId: number, reportId: number, partnerReportIdentification: any) {
    cy.request({
        method: 'PUT',
        url: `api/project/report/partner/identification/byPartnerId/${partnerId}/byReportId/${reportId}`,
        body: partnerReportIdentification
    });
}

function updatePartnerReportExpenditures(partnerId: number, reportId: number, partnerReportExpenditures: any) {
    assignUnitCostIds(partnerId, reportId, partnerReportExpenditures);
    assignLumpSumIds(partnerId, reportId, partnerReportExpenditures);

    cy.request({
        method: 'PUT',
        url: `api/project/report/partner/expenditure/byPartnerId/${partnerId}/byReportId/${reportId}`,
        body: partnerReportExpenditures
    }).then(response => response.body);
}

function assignUnitCostIds(partnerId, reportId, partnerReportExpenditures) {
    matchProjectProposedUnitCostReferences(partnerReportExpenditures);
    cy.getUnitCostsByPartnerAndReportIds(partnerId, reportId).then(projectUnitCosts => {
        partnerReportExpenditures.forEach((expenditure, index) => {
            if (expenditure.cypressReference === 'shouldHaveUnitCost') {
                projectUnitCosts.forEach(unitCost => {
                    if (unitCost.unitCostProgrammeId === expenditure.unitCostId) {
                        partnerReportExpenditures[index].unitCostId = unitCost.id;
                    }
                });
            }
        });
    });
}

function matchProjectProposedUnitCostReferences(partnerReportExpenditures) {
    // match any project proposed unit cost reference to its id
    cy.then(function () {
        partnerReportExpenditures.forEach(expenditure => {
            if (expenditure.cypressReferenceUnit) {
                expenditure.unitCostId = this[expenditure.cypressReferenceUnit];
            }
        });
    });
}

function assignLumpSumIds(partnerId, reportId, partnerReportExpenditures) {
    cy.getLumpSumsByPartnerAndReportIds(partnerId, reportId).then(lumpSums => {
        partnerReportExpenditures.forEach(expenditure => {
            if (expenditure.cypressReference === 'shouldHaveLumpSum') {
                lumpSums.forEach(lumpSum => {
                    expenditure.lumpSumId = lumpSum.id
                });
            }
        });
    });
}

export {}
