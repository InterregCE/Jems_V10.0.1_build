import {loginByRequest} from './login.commands';

declare global {
    namespace Cypress {
        interface Chainable {
            addPartnerReport(partnerId: number);

            createCertifiedPartnerReport(partnerId: number, partnerReportDetails: any, controllerUserEmail): any;

            updatePartnerReportIdentification(partnerId: number, reportId: number, partnerReportIdentification);

            updatePartnerReportWorkPlans(partnerId: number, reportId: number, partnerReportWorkPlans: any): any;

            updatePartnerReportProcurements(partnerId: number, reportId: number, partnerReportProcurements: any): any;

            updatePartnerReportExpenditures(partnerId: number, reportId: number, partnerReportExpenditures);

            updatePartnerReportContributions(partnerId: number, reportId: number, partnerReportContributions: any): any;

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
        cy.updatePartnerReportWorkPlans(partnerId, partnerReportId, partnerReportDetails.partnerReport.workPlans);
        cy.updatePartnerReportProcurements(partnerId, partnerReportId, partnerReportDetails.partnerReport.procurements);
        cy.updatePartnerReportExpenditures(partnerId, partnerReportId, partnerReportDetails.partnerReport.expenditures);
        cy.updatePartnerReportContributions(partnerId, partnerReportId, partnerReportDetails.partnerReport.contributions);
        cy.runPreSubmissionPartnerReportCheck(partnerId, partnerReportId);
        cy.submitPartnerReport(partnerId, partnerReportId);

        loginByRequest(controllerUserEmail);
        cy.startControlWork(partnerId, partnerReportId);
        cy.updateControlReportIdentification(partnerId, partnerReportId, partnerReportDetails.controlWork.identification);
        cy.updateControlReportExpenditureVerification(partnerId, partnerReportId, partnerReportDetails.controlWork.expenditureVerification);
        cy.updateControlReportOverview(partnerId, partnerReportId, partnerReportDetails.controlWork.overview);
        cy.finalizeControl(partnerId, partnerReportId);
        cy.get('@currentUser').then((currentUser: any) => {
            loginByRequest(currentUser.name);
        });
        cy.wrap(partnerReportId);
    });
});

Cypress.Commands.add('updatePartnerReportIdentification', (partnerId: number, reportId: number, partnerReportIdentification) => {
    updatePartnerReportIdentification(partnerId, reportId, partnerReportIdentification);
});

Cypress.Commands.add('updatePartnerReportWorkPlans', (partnerId: number, partnerReportId: number, partnerReportWorkPlans) => {
    // match work package, activities, deliverables and output ids
    getApplicationWorkPlans(partnerId, partnerReportId).then(workPlans => {
        workPlans.forEach((workPlan, i) => {
            partnerReportWorkPlans[i].id = workPlan.id;
            workPlan.activities.forEach((activity, k) => {
                partnerReportWorkPlans[i].activities[k].id = activity.id;
                activity.deliverables.forEach((deliverable, j) => {
                    partnerReportWorkPlans[i].activities[k].deliverables[j].id = deliverable.id;
                });
            });
            workPlan.outputs.forEach((output, k) => {
                partnerReportWorkPlans[i].outputs[k].id = output.id;
            });
        });

        cy.request({
            method: 'PUT',
            url: `api/project/report/partner/workPlan/byPartnerId/${partnerId}/byReportId/${partnerReportId}`,
            body: partnerReportWorkPlans
        });
    });
});

Cypress.Commands.add('updatePartnerReportProcurements', (partnerId: number, partnerReportId: number, partnerReportProcurements) => {
    partnerReportProcurements.forEach(procurement => {
        cy.request({
            method: 'POST',
            url: `api/project/report/partner/procurement/byPartnerId/${partnerId}/byReportId/${partnerReportId}`,
            body: procurement.details
        }).then((response: any) => {
            const procurementId = response.body.id;
            cy.wrap(procurementId).as(procurement.details.contractName);
            cy.request({
                method: 'PUT',
                url: `api/project/report/partner/procurement/beneficialOwner/byPartnerId/${partnerId}/byReportId/${partnerReportId}/byProcurementId/${procurementId}`,
                body: procurement.beneficialOwners
            });
            cy.request({
                method: 'PUT',
                url: `api/project/report/partner/procurement/subcontractor/byPartnerId/${partnerId}/byReportId/${partnerReportId}/byProcurementId/${procurementId}`,
                body: procurement.subcontractors
            });
        });
    });
});

Cypress.Commands.add('updatePartnerReportExpenditures', (partnerId: number, reportId: number, partnerReportExpenditures) => {
    updatePartnerReportExpenditures(partnerId, reportId, partnerReportExpenditures);
});

Cypress.Commands.add('updatePartnerReportContributions', (partnerId: number, partnerReportId: number, partnerReportContributions) => {
    // match contribution ids
    getPartnerContributions(partnerId, partnerReportId).then(contributions => {
        contributions.forEach((contribution, i) => {
            partnerReportContributions.toBeUpdated[i].id = contribution.id
        });

        cy.request({
            method: 'PUT',
            url: `api/project/report/partner/contribution/byPartnerId/${partnerId}/byReportId/${partnerReportId}`,
            body: partnerReportContributions
        });
    });
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

function updatePartnerReportExpenditures(partnerId: number, partnerReportId: number, partnerReportExpenditures: any) {
    assignUnitCostIds(partnerId, partnerReportId, partnerReportExpenditures);
    assignLumpSumIds(partnerId, partnerReportId, partnerReportExpenditures);
    matchInvestmentIds(partnerId, partnerReportId, partnerReportExpenditures);
    matchProcurementIds(partnerReportExpenditures);

    cy.request({
        method: 'PUT',
        url: `api/project/report/partner/expenditure/byPartnerId/${partnerId}/byReportId/${partnerReportId}`,
        body: partnerReportExpenditures
    }).then(response => response.body);
}

function assignUnitCostIds(partnerId, partnerReportId, partnerReportExpenditures) {
    matchProjectProposedUnitCostReferences(partnerReportExpenditures);
    cy.getUnitCostsByPartnerAndReportIds(partnerId, partnerReportId).then(projectUnitCosts => {
        partnerReportExpenditures.forEach(expenditure => {
            if (expenditure.cypressReference === 'shouldHaveUnitCost') {
                projectUnitCosts.forEach(unitCost => {
                    if (unitCost.unitCostProgrammeId === expenditure.unitCostId) {
                        expenditure.unitCostId = unitCost.id;
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

function matchInvestmentIds(partnerId, partnerReportId, expenditures) {
    cy.request(`api/project/report/partner/expenditure/byPartnerId/${partnerId}/byReportId/${partnerReportId}/investments`).then(function(response) {
        const investments = response.body;
        expenditures.forEach(expenditure => {
            if (expenditure.cypressReferenceInvestment) {
                const matchedInvestment = investments.find(investment => investment.investmentId === this[expenditure.cypressReferenceInvestment]);
                expenditure.investmentId = matchedInvestment.id;
            }
        });
    });
}

function matchProcurementIds(procurements) {
    cy.then(function() {
        procurements.forEach((procurement, i) => {
            if (procurement.cypressReferenceProcurement) {
                procurements[i].contractId = this[procurement.cypressReferenceProcurement];
            }
        });
    });
}

function getApplicationWorkPlans(partnerId, partnerReportId) {
    return cy.request(`api/project/report/partner/workPlan/byPartnerId/${partnerId}/byReportId/${partnerReportId}`).then(response => {
        return response.body;
    })
}

function getPartnerContributions(partnerId, partnerReportId) {
    return cy.request(`api/project/report/partner/contribution/byPartnerId/${partnerId}/byReportId/${partnerReportId}`).then(response => {
        return response.body.contributions;
    })
}

export {}
