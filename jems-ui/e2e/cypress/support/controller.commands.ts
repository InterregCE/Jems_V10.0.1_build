import {
  UpdateControllerInstitutionDTO
} from '../../../build/swagger-code-jems-api/model/updateControllerInstitutionDTO';
import {
  ControllerInstitutionAssignmentDTO
} from '../../../build/swagger-code-jems-api/model/controllerInstitutionAssignmentDTO';

declare global {

  namespace Cypress {
    interface Chainable {
      createInstitution(institution: UpdateControllerInstitutionDTO);

      assignInstitution(assignment: ControllerInstitutionAssignmentDTO);
    }
  }
}

Cypress.Commands.add('createInstitution', (institution: UpdateControllerInstitutionDTO) => {
  cy.request({
    method: 'POST',
    url: 'api/controller/institution/create',
    body: institution
  }).then(response => {
    return response.body.id
  });
});

Cypress.Commands.add('assignInstitution', (assignment: ControllerInstitutionAssignmentDTO) => {
  cy.request({
    method: 'POST',
    url: 'api/controller/institution/assign',
    body: assignment
  });
});

export {}
