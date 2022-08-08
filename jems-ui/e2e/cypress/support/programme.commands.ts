import {faker} from '@faker-js/faker';


declare global {

  interface Checklist {
    id:number,
    type:string,
    name:string,
    minScore:number,
    maxScore:number,
    allowsDecimalScore:false,
    lastModificationDate:string,
    locked:boolean,
    components?:ChecklistComponent[],
  }

  interface ChecklistComponent {
    id:number,
    type: string,
    position: number,
    metadata: ChecklistMetadata[]
  }

  interface ChecklistMetadata {
    type?:string,
    question?:string,
    firstOption?:string,
    secondOption?:string,
    thirdOption?:string,
    value?:string,
    explanationLabel?:string,
    explanationMaxLength?:string,
    weight?:string
  }

  namespace Cypress {
    interface Chainable {
      createChecklist(checklist);
    }
  }
}

Cypress.Commands.add('createChecklist', (checklist: Checklist) => {
  createChecklist(checklist).then(checklistId => {
    checklist.id = checklistId;
    cy.wrap(checklist.id).as('checklistId');
  });
});


function createChecklist(checklist: Checklist) {
  checklist.name = `${faker.hacker.adjective()} ${faker.hacker.noun()}`;
  return cy.request({
    method: 'POST',
    url: 'api/programme/checklist/create',
    body: checklist
  }).then(response => {
    return response.body.id
  });
}

export {}
