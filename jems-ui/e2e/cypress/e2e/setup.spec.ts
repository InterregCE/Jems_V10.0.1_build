import user from '@fixtures/users.json';
import updatedDefaultProgrammeRole from '@fixtures/api/roles/updatedDefaultProgrammeRole.json';
import controllerRole from '@fixtures/api/roles/controllerRole.json';
import controllerInstitution from '@fixtures/api/control/institution.json';
import verificationRole from '@fixtures/api/roles/verificationRole.json';

context('Setup script', () => {

  it('Setup default roles, privileges, users, controller institutions', () => {
    cy.loginByRequest(user.admin.email);

    // prepare default programme role
    cy.updateRole(updatedDefaultProgrammeRole);
    cy.createUser(user.programmeUser);

    // prepare applicant user role
    cy.createUser(user.applicantUser);

    cy.downloadNUTS();

    // prepare controller role, user and institution
    cy.createRole(controllerRole);
    cy.createUser(user.controllerUser);
    cy.createInstitution(controllerInstitution);

    // prepare verification role and user
    cy.createRole(verificationRole);
    cy.createUser(user.verificationUser);
  });
});
