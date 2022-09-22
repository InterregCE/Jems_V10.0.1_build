import {faker} from '@faker-js/faker';
import user from '../../fixtures/users.json';
import call from '../../fixtures/api/call/1.step.call.json';
import spfCall from '../../fixtures/api/call/spf.1.step.call.json';
import application from '../../fixtures/api/application/application.json';
import partner from '../../fixtures/api/application/partner/partner.json';

context('Programme management tests', () => {

  beforeEach(() => {
    cy.loginByRequest(user.programmeUser.email);
  });

  it('TB-387 Programme Basic data can be configured', () => {
    cy.fixture('programme/TB-387.json').then((basicData) => {
      cy.visit('/');

      cy.contains('Programme').click();
      cy.contains('Edit').click();

      cy.contains('div', 'CCI').find('input').type(basicData.cci);
      cy.contains('div', 'Title').find('input').type(basicData.title);
      cy.contains('div', 'Version').find('input').type(basicData.version);
      cy.contains('div', 'First year').find('input').type(basicData.firstYear);
      cy.contains('div', 'Last year').find('input').type(basicData.lastYear);
      cy.contains('div', 'Eligible from').find('input').type(basicData.eligibleFrom);
      cy.contains('div', 'Eligible until').find('input').type(basicData.eligibleUntil);
      cy.contains('div', 'Commission decision number').find('input').type(basicData.commissionDecisionNumber);
      cy.contains('div', 'Commission decision date').find('input').type(basicData.commissionDecisionDate);
      cy.contains('div', 'Programme amending decision number').find('input').type(basicData.programmeAmendingDecisionNumber);
      cy.contains('div', 'Programme amending decision entry').find('input').type(basicData.programmeAmendingDecisionDate);

      cy.contains('Save').click();
      cy.contains('Confirm').should('be.visible').click();

      cy.contains('Programme data was successfully saved.').should('be.visible');
    });
  });

  it('TB-525 Programme Languages can be configured', () => {
    cy.fixture('programme/TB-525.json').then((languages) => {
      cy.visit('/app/programme', {failOnStatusCode: false});

      cy.contains('Languages').click();
      cy.contains('Edit').click();

      languages.forEach(language => {
        cy.contains('tr', language.language).then(el => {
          cy.wrap(el).find('input').check({force: true});
        });
      });

      cy.contains('Save changes').click();

      cy.get('jems-alert p').should('contain.text', 'Programme data was successfully saved.');
    });
  });

  it('TB-523 Programme NUTS can be downloaded and areas selected', () => {

    cy.visit('/app/programme', {failOnStatusCode: false});

    cy.contains('Geographical coverage').click();
    cy.contains('Download').click();
    cy.get('jems-alert p', {timeout: 40000}).should('contain.text', 'Up to date NUTS dataset was successfully downloaded.');

    cy.contains('DE Deutschland').click();
    cy.contains('AT Österreich').click();

    cy.contains('Save changes').click();
    cy.get('jems-alert p').should('contain.text', 'Programme regions saved successfully');
  });

  it('TB-524 Programme Funds can be configured', () => {
    cy.fixture('programme/TB-524.json').then((funds) => {
      cy.visit('/app/programme', {failOnStatusCode: false});

      cy.contains('Funds').click();
      cy.contains('Edit').click();
      cy.contains('button', 'DE').click();

      funds.forEach(fund => {
        cy.contains(fund.type).parent().then(el => {
          cy.wrap(el).find('input[name="multipleFundsAllowed"]').check({force: true});

          fund.abbreviation.forEach(abbreviation => {
            cy.wrap(el).find('input:not([type="checkbox"])').eq(0).type(abbreviation.translation);
          });

          fund.description.forEach(description => {
            cy.wrap(el).find('input:not([type="checkbox"])').eq(1).type(description.translation);
          });
        });
      });

      cy.contains('Save changes').click();
      cy.get('jems-alert p').should('contain.text', 'Programme funds saved successfully.');
    });
  });

  it('TB-526 Programme Priorities can be configured', () => {
    cy.fixture('programme/TB-526.json').then((priorities) => {
      cy.visit('/app/programme', {failOnStatusCode: false});

      cy.contains('Priorities').click();
      priorities.forEach(priority => {

        cy.contains('Add priority').click();
        cy.contains('div', 'Priority code').find('input').type(priority.code);

        priority.title.forEach(title => {
          cy.contains('jems-multi-language-container', 'Priority title').then(el => {
            cy.wrap(el).contains('button', title.language).click();
            cy.wrap(el).find('input').type(title.translation);
          });
        });

        cy.contains('div', 'Select policy objective').find('mat-select').click();
        cy.contains(priority.objective).click();

        priority.specificObjectives.forEach(specificObjective => {
          cy.contains('tr', specificObjective.programmeObjectivePolicy).within(() => {
            cy.get('mat-checkbox').click();
            cy.contains('div', 'Programme specific objective code').find('input').type(specificObjective.code);
            cy.contains('expand_more').click();
          }).next().within(() => {
            specificObjective.dimensionCodes.TypesOfIntervention.forEach(code => {
              cy.contains('Types of intervention').next().find('input').type(code);
              cy.root().closest('body').find('mat-option').contains(code).click();
            });
            specificObjective.dimensionCodes.FormOfSupport.forEach(code => {
              cy.contains('Form of support').next().find('input').type(code);
              cy.root().closest('body').find('mat-option').contains(code).click();
            });
            specificObjective.dimensionCodes.TerritorialDeliveryMechanism.forEach(code => {
              cy.contains('Territorial delivery mechanism').next().find('input').type(code);
              cy.root().closest('body').find('mat-option').contains(code).click();
            });
            specificObjective.dimensionCodes.EconomicActivity.forEach(code => {
              cy.contains('Economic activity').next().find('input').type(code);
              cy.root().closest('body').find('mat-option').contains(code).click();
            });
            specificObjective.dimensionCodes.GenderEquality.forEach(code => {
              cy.contains('Gender Equality').next().find('input').type(code);
              cy.root().closest('body').find('mat-option').contains(code).click();
            });
            specificObjective.dimensionCodes.RegionalAndSeaBasinStrategy.forEach(code => {
              cy.contains('Regional and Sea Basin Strategy').next().find('input').type(code);
              cy.root().closest('body').find('mat-option').contains(code).click();
            });
          });
        });

        cy.contains('Add priority').should('be.visible').click();
        cy.contains('Confirm').should('be.visible').click();

        cy.contains('jems-programme-priority-list-page div', priority.code).should('exist');
      });
    });
  });

  it('TB-527 Programme result indicators can be configured', () => {
    cy.fixture('programme/TB-527.json').then((resultIndicators) => {
      resultIndicators.forEach(current => {
        current.identifier = current.identifier + faker.random.numeric(6);
      })
      cy.visit('/app/programme', {failOnStatusCode: false});
      cy.contains('Indicators').click();

      resultIndicators.forEach(indicator => {
        cy.contains('Create result indicator').click();

        cy.get('input[name="identifier"').type(indicator.identifier);
        cy.get('mat-select[name="indicatorCode"').click();
        cy.contains('mat-option', indicator.code).click();

        cy.get('mat-select[name="specificObjective"').click();
        cy.contains('mat-option', indicator.programmeObjectivePolicy).click();

        cy.get('input[name="baseline"').type(indicator.baseline.toString());
        cy.get('input[name="referenceYear"').type(indicator.referenceYear);
        cy.get('input[name="finalTarget"').type(indicator.finalTarget);

        indicator.sourceOfData.forEach(source => {
          cy.contains('jems-multi-language-container', 'Source of data').then(el => {
            cy.wrap(el).contains('button', source.language).click();
            cy.wrap(el).find('textarea').type(source.translation);
          });
        });

        cy.contains('div', 'Comments').find('textarea').type(indicator.comment);

        cy.contains('Save').click();
        cy.contains('Confirm').should('be.visible').click();

        cy.contains('jems-programme-result-indicators-list div', indicator.code).should('exist');
      });
    });
  });

  it('TB-528 Programme output indicators can be configured', () => {
    cy.fixture('programme/TB-528.json').then((outputIndicators) => {
      outputIndicators.forEach(current => {
        current.identifier = current.identifier + faker.random.numeric(6);
      })
      cy.visit('/app/programme/indicators', {failOnStatusCode: false});

      outputIndicators.forEach(indicator => {
        cy.contains('Create output indicator').click();

        cy.get('input[name="identifier"').type(indicator.identifier);
        cy.get('mat-select[name="indicatorCode"').click();
        cy.contains('mat-option', indicator.code).click();

        cy.get('mat-select[name="specificObjective"').click();
        cy.contains('mat-option', indicator.programmeObjectivePolicy).click();

        if (indicator.resultIndicator) {
          cy.get('mat-select[formcontrolname="resultIndicatorId"').click();
          cy.contains('mat-option', indicator.resultIndicator).click();
        }

        cy.get('input[name="milestone"').type(indicator.milestone.toString());
        cy.get('input[name="finalTarget"').type(indicator.finalTarget);

        cy.contains('Save').click();
        cy.contains('Confirm').should('be.visible').click();

        cy.contains('jems-programme-output-indicators-list div', indicator.code, {timeout: 8000}).should('exist');
      });
    });
  });

  it('TB-529 Programme Strategies can be configured', () => {
    cy.fixture('programme/TB-529.json').then((strategies) => {
      cy.visit('/app/programme', {failOnStatusCode: false});

      cy.contains('Strategies').click();
      cy.contains('button', 'Edit').click();

      strategies.forEach(strategy => {
        cy.contains('mat-checkbox', strategy.strategy).then(el => {
          cy.wrap(el).find('input[type="checkbox"]').check({force: true});
        });
      });

      cy.contains('Save').click();
      cy.contains('Confirm').should('be.visible').click();

      cy.get('jems-alert p').should('contain.text', 'Programme strategies successfully updated');
    });
  });

  it('TB-530 Programme Legal status can be configured', () => {

    cy.visit('/app/programme', {failOnStatusCode: false});

    cy.contains('Legal status').click();
    cy.contains('button', 'Edit').click();

    cy.contains('button', 'DE').click();
    cy.get('input[placeholder="Public"]').type('Public DE');
    cy.get('input[placeholder="Private"]').type('Private DE');

    cy.contains('mat-icon', 'add').click();
    cy.contains('OTHER').parent().find('input').type('Internal DE');
    cy.contains('button', 'EN').click();
    cy.contains('OTHER').parent().find('input').type('Internal EN');

    cy.contains('Save').click();

    cy.get('jems-alert p').should('contain.text', 'Legal status saved successfully');
  });

  it('TB-531 Programme Lump Sums can be configured', () => {
    cy.fixture('programme/TB-531.json').then((lumpSums) => {
      cy.visit('/app/programme', {failOnStatusCode: false});

      cy.contains('Simplified Cost Options').click();
      lumpSums.forEach(lumpSum => {
        cy.contains('button', 'Add Lump Sum').click();

        lumpSum.name.forEach(name => {
          cy.contains('jems-multi-language-container', 'Name of the Lump Sum').then(el => {
            cy.wrap(el).contains('button', name.language).click();
            cy.wrap(el).find('input').type(name.translation);
          });
        });

        lumpSum.description.forEach(description => {
          cy.contains('jems-multi-language-container', 'Description of the Lump Sum').then(el => {
            cy.wrap(el).contains('button', description.language).click();
            cy.wrap(el).find('input').type(description.translation);
          });
        });

        cy.contains('mat-form-field', 'Lump Sum cost').find('input').type(lumpSum.cost);

        cy.contains('Allow splitting of the lump sum between partners:').next().contains(lumpSum.splittingAllowed).click();
        cy.contains('This is a Fast Track Lump Sum').next().contains(lumpSum.fastTrack).click();
        cy.contains('button', lumpSum.phase).click();

        lumpSum.categories.forEach(category => {
          cy.contains('mat-checkbox', category).find('input').check({force: true});
        });

        cy.contains('Save').click();
        cy.contains('Confirm').should('be.visible').click();
        cy.contains('button', lumpSum.name[0].language).click();
        cy.contains('jems-programme-lump-sums-list div', lumpSum.name[0].translation).should('exist');
      });
    });
  });

  it('TB-532 Programme Unit Costs can be configured', () => {
    cy.fixture('programme/TB-532.json').then((unitCosts) => {
      cy.visit('/app/programme/costs', {failOnStatusCode: false});

      unitCosts.forEach(unitCost => {
        cy.contains('button', 'Add Unit Cost').click();

        unitCost.name.forEach(name => {
          cy.contains('jems-multi-language-container', 'Name of the Unit Cost').then(el => {
            cy.wrap(el).contains('button', name.language).click();
            cy.wrap(el).find('input').type(name.translation);
          });
        });

        unitCost.description.forEach(description => {
          cy.contains('jems-multi-language-container', 'Description of the Unit Cost').then(el => {
            cy.wrap(el).contains('button', description.language).click();
            cy.wrap(el).find('input').type(description.translation);
          });
        });

        unitCost.type.forEach(type => {
          cy.contains('jems-multi-language-container', 'Unit type').then(el => {
            cy.wrap(el).contains('button', type.language).click();
            cy.wrap(el).find('input').type(type.translation);
          });
        });

        cy.contains('mat-form-field', 'Cost per unit').find('input').type(unitCost.costPerUnit);

        cy.contains('button', unitCost.categoryType).click();

        if (unitCost.categoryType === 'Multiple cost categories') {
          unitCost.categories.forEach(category => {
            cy.contains('mat-checkbox', category).find('input').check({force: true});
          });
        } else {
          cy.contains('mat-radio-button', unitCost.categories[0]).find('input').click({force: true});
        }

        cy.contains('Save').click();
        cy.contains('Confirm').should('be.visible').click();
        cy.contains('button', unitCost.name[0].language).click();
        cy.contains('jems-programme-unit-costs-list div', unitCost.name[0].translation).should('exist');
      });
    });
  });

  it('TB-533 Programme State Aid can be configured', () => {
    cy.fixture('programme/TB-533.json').then((stateAid) => {
      cy.visit('/app/programme', {failOnStatusCode: false});

      cy.contains('State Aid').click();
      cy.contains('button', 'Add State Aid').click();

      stateAid.forEach(aid => {

        cy.get('div.jems-table-config > div').last().prev().then(el => {
          cy.wrap(el).contains('mat-form-field', 'Measure').find('input').click();
          cy.contains('mat-option', aid.measure).click();

          cy.contains('button', aid.name[0].language).click();
          cy.wrap(el).find('textarea').eq(0).type(aid.name[0].translation);
          cy.contains('button', aid.abbreviatedName[0].language).click();
          cy.wrap(el).find('input').eq(1).type(aid.abbreviatedName[0].translation);
          aid.comments[0] && cy.wrap(el).find('textarea').eq(1).type(aid.comments[0].translation);

          aid.schemeNumber && cy.wrap(el).find('input[name="schemeNumber"]').type(aid.schemeNumber);
          aid.maxIntensity && cy.wrap(el).find('input[name="maxIntensity"]').type(aid.maxIntensity);
          aid.threshold && cy.wrap(el).find('input[name="threshold"]').type(aid.threshold);

          cy.contains('Save changes').click();

          cy.get('jems-alert p').should('contain.text', 'State aid was successfully saved.');
          cy.get('div.jems-table-config > div').last().find('input[name="measure"]').should('have.value', aid.measure);

          // prepare for another entry
          cy.contains('Edit').click();
          cy.contains('mat-icon', 'add').click();
        });
      });
    });
  });

  it('TB-579 User can create, edit, preview and delete checklists', () => {
    cy.fixture('programme/TB-579.json').then((testData) => {
      cy.visit('app/programme', {failOnStatusCode: false});

      testData.checklists.forEach(checklist => {
        createChecklist(checklist);

        cy.wait(1000);
        cy.contains('div', 'Preview checklist').click();
        cy.contains('div', 'Score table').should('be.visible');
        checklist.Score.forEach(component => cy.contains('div', component.question).scrollIntoView().should('be.visible'))
        checklist.Headline.forEach(component => cy.contains('div', component.headlineText).should('be.visible'))
        checklist.OptionsToggle.forEach(component => {
          cy.contains('p', component.question).should('be.visible');
          cy.get('mat-button-toggle-group').should('be.visible');
          cy.get('mat-button-toggle-group').find('button').find('span').contains(component.option1).should('be.visible');
          cy.get('mat-button-toggle-group').find('button').find('span').contains(component.option2).should('be.visible');
          cy.get('mat-button-toggle-group').find('button').find('span').contains(component.option3).should('be.visible');
        })
        cy.contains('div', 'Justification').should('be.visible');
        checklist.TextInput.forEach(component => {
          cy.contains('p', component.question).scrollIntoView().should('be.visible');
          cy.contains('div', component.inputLabel).scrollIntoView().should('be.visible');
        })

        cy.contains('div', 'Justification').scrollIntoView().should('be.visible');
        cy.get('.score-calculation').scrollIntoView().should('be.visible');

        cy.visit('/app/programme/checklists', {failOnStatusCode: false});
        cy.get('.mat-row').first().click();

        cy.contains('mat-form-field', 'Headline name').find('input').clear().type(testData.newHeadlineName);
        cy.contains('mat-form-field', 'Question text').find('textarea').clear().type(testData.newOptionsQuestionText);
        cy.contains('mat-form-field', 'Option 1').find('input').clear().type(testData.newOption1);
        cy.contains('mat-form-field', 'Option2').find('input').clear().type(testData.newOption2);
        cy.contains('mat-form-field', 'Option3 (optional)').find('input').clear().type(testData.newOption3);
        cy.contains('mat-form-field', 'Input label').find('input').clear().type(testData.newTextInputLabel);
        cy.contains('mat-form-field', 'Characters').find('input').clear().type(testData.newCharacters);
        cy.contains('mat-form-field', 'Weight (multiplier)').find('input').type(testData.newWeight);
        cy.contains('button', 'Save changes').click();

        cy.contains('div', 'Preview checklist').click();
        cy.contains('div', testData.newHeadlineName).should('be.visible');
        cy.contains('p', testData.newOptionsQuestionText).should('be.visible');
        cy.get('mat-button-toggle-group').scrollIntoView().should('be.visible');
        cy.get('mat-button-toggle-group').find('button').find('span').contains(testData.newOption1).should('be.visible');
        cy.get('mat-button-toggle-group').find('button').find('span').contains(testData.newOption2).should('be.visible');
        cy.get('mat-button-toggle-group').find('button').find('span').contains(testData.newOption3).should('be.visible');
        cy.contains('div', 'Justification').should('be.visible');
        cy.contains('p', testData.textInputQuestionText).should('be.visible');
        cy.contains('div', testData.newTextInputLabel).scrollIntoView().should('be.visible');
        cy.contains('div', 'Justification').should('be.visible');
        cy.get('.score-calculation').scrollIntoView().should('be.visible');

        cy.visit('/app/programme/checklists', {failOnStatusCode: false});
        cy.get('.mat-row').first().find('button').click();
        cy.contains('button', 'Confirm').click();
        cy.contains('div', checklist.checklistName).should('not.exist');
      });
    });
  });

  it('TB-747 Data export in programme setup shall take the correct data version', () => {
    cy.fixture('programme/TB-747').then(testData => {

      call.generalCallSettings.name = '';
      cy.createCall(call).then(callId => {
        cy.publishCall(callId);

        cy.createCall(spfCall).then(spfCallId => {
          cy.publishCall(spfCallId);

          application.details.projectCallId = callId;
          const spfApplication = JSON.parse(JSON.stringify(application));
          spfApplication.partners[0].budget.infrastructure = null; // SPF projects don't allow infrastructure costs
          spfApplication.partners[0].budget.spf = testData.spfCostItem;
          spfApplication.partners[0].cofinancing.partnerContributions[0].amount = testData.cofinancingAmount;
          spfApplication.partners[0].spfCofinancing = testData.spfCofinancing;
          spfApplication.description.relevanceAndContext.projectSpfRecipients = testData.projectSpfRecipients;
          spfApplication.details.projectCallId = spfCallId;

          cy.loginByRequest(user.applicantUser.email);
          application.partners.push(partner);

          for (let i = 0; i < 3; i++) {
            cy.createApplication(application);
            cy.createApplication(spfApplication);
            cy.createApprovedApplication(application, user.programmeUser.email);
            cy.createApprovedApplication(spfApplication, user.programmeUser.email);
          }
        });
      });

      cy.loginByRequest(user.admin.email);
      testData.dataExportRole.name = `dataExportRole_${faker.random.alphaNumeric(5)}`;
      testData.dataExportUser.email = faker.internet.email();
      cy.createRole(testData.dataExportRole).then(roleId => {
        testData.dataExportUser.userRoleId = roleId;
        cy.createUser(testData.dataExportUser);
      });
      cy.loginByRequest(testData.dataExportUser.email);
      cy.loginByRequest(user.admin.email);
      cy.visit('app/programme', {failOnStatusCode: false});
      cy.contains('Data export').click();
      
      cy.contains('div', 'Programme data export plugin').find('mat-select').click();
      cy.contains('mat-option', 'Standard programme partner data export').click();
      cy.contains('button', 'Generate export file').click();
      cy.get('mat-spinner').should('exist');
      cy.get('mat-spinner').should('not.exist');

      let requestToIntercept = '/api/programme/export/download?pluginKey=standard-programme-partner-data-export-plugin';
      cy.contains('div', 'Standard programme partner data export').find('button').clickToDownload(requestToIntercept, 'xlsx').then(exportFile => {
        cy.fixture('programme/TB-747-partner-export-en-en.xlsx', null).parseXLSX().then(testDataFile => {
          preparePartnerExportData(exportFile);
          const assertionMessage = 'Verify downloaded partner EN xlsx file';
          expect(exportFile.content[0].data, assertionMessage).to.deep.equal(testDataFile[0].data);
        });
      });

      cy.contains('form div', 'Export language').find('mat-select').click();
      cy.contains('mat-option', 'Deutsch').click();
      cy.contains('form div', 'Input language').find('mat-select').click();
      cy.contains('mat-option', 'Deutsch').click();
      cy.contains('button', 'Generate export file').click();
      cy.get('mat-spinner').should('exist');
      cy.get('mat-spinner').should('not.exist');

      requestToIntercept = '/api/programme/export/download?pluginKey=standard-programme-partner-data-export-plugin';
      cy.contains('div', 'Standard programme partner data export').find('button').clickToDownload(requestToIntercept, 'xlsx').then(exportFile => {
        cy.fixture('programme/TB-747-partner-export-de-de.xlsx', null).parseXLSX().then(testDataFile => {
          preparePartnerExportData(exportFile);
          const assertionMessage = 'Verify downloaded partner DE xlsx file';
          expect(exportFile.content[0].data, assertionMessage).to.deep.equal(testDataFile[0].data);
        });
      });

      cy.contains('div', 'Programme data export plugin').find('mat-select').click();
      cy.contains('mat-option', 'Standard programme project data export').click();
      cy.contains('button', 'Generate export file').click();
      cy.get('mat-spinner').should('exist');
      cy.get('mat-spinner').should('not.exist');

      requestToIntercept = '/api/programme/export/download?pluginKey=standard-programme-project-data-export-plugin';
      cy.contains('div', 'Standard programme project data export').find('button').clickToDownload(requestToIntercept, 'xlsx').then(exportFile => {
        cy.fixture('programme/TB-747-project-export-de-de.xlsx', null).parseXLSX().then(testDataFile => {
          prepareProjectExportData(exportFile);
          const assertionMessage = 'Verify downloaded project DE xlsx file';
          expect(exportFile.content[0].data, assertionMessage).to.deep.equal(testDataFile[0].data);
        });
      });

      cy.contains('form div', 'Export language').find('mat-select').click();
      cy.contains('mat-option', 'English').click();
      cy.contains('form div', 'Input language').find('mat-select').click();
      cy.contains('mat-option', 'English').click();
      cy.contains('button', 'Generate export file').click();
      cy.get('mat-spinner').should('exist');
      cy.get('mat-spinner').should('not.exist');

      requestToIntercept = '/api/programme/export/download?pluginKey=standard-programme-project-data-export-plugin';
      cy.contains('div', 'Standard programme project data export').find('button').clickToDownload(requestToIntercept, 'xlsx').then(exportFile => {
        cy.fixture('programme/TB-747-project-export-en-en.xlsx', null).parseXLSX().then(testDataFile => {
          prepareProjectExportData(exportFile);
          const assertionMessage = 'Verify downloaded project EN xlsx file';
          expect(exportFile.content[0].data, assertionMessage).to.deep.equal(testDataFile[0].data);
        });
      });
    })
  });

  it('TB-746 Programme conversion rates can be loaded in the system', () => {
    cy.visit('/app/progr amme', {failOnStatusCode: false});
    cy.get('div#navbarSupportedContent').contains('span', 'Programme').click();
    cy.contains('div', 'Conversion rates').click();
    cy.contains('button', 'Load conversion rates').click();
    cy.contains('div', 'Loaded conversion rates successfully').should('be.visible');
    cy.contains('div', /Conversion rates in the system: Date: [0-9]{1,2} - [0-9]{4}/).should('be.visible');
  });
});

function createChecklist(checklist) {
  cy.contains('Checklists').click();

  cy.contains('button', 'Create new checklist').click();
  cy.contains('div', 'Checklist type').find('mat-select').click();
  cy.contains('mat-option', checklist.type).click();
  cy.contains('div', 'Checklist name (on the system)').find('input').type(checklist.name);

  checklist.Score.forEach(scoreComponent => {
    cy.contains('button', 'Add component').click();
    cy.get('.jems-table-config').children().last().within(() => {
      cy.contains('mat-form-field', 'Component type').click();
      cy.root().closest('body').find('mat-option').contains('Score').click();
      cy.contains('mat-form-field', 'Weight').find('input').type(scoreComponent.weight);
      cy.contains('mat-form-field', 'Question text').find('textarea').type(scoreComponent.question);
    });
  });

  checklist.Headline.forEach(headlineComponent => {
    cy.contains('button', 'Add component').click();
    cy.get('.jems-table-config').children().last().within(() => {
      cy.contains('mat-form-field', 'Component type').click();
      cy.root().closest('body').find('mat-option').contains('Headline').click();
      cy.contains('mat-form-field', 'Headline name').find('input').type(headlineComponent.headlineText);
    });
  });

  checklist.OptionsToggle.forEach(optionsToggleComponent => {
    cy.contains('button', 'Add component').click();
    cy.get('.jems-table-config').children().last().within(() => {
      cy.contains('mat-form-field', 'Component type').click();
      cy.root().closest('body').find('mat-option').contains('Options toggle').click();
      cy.contains('mat-form-field', 'Question text').find('textarea').type(optionsToggleComponent.question);
      cy.contains('mat-form-field', 'Option 1').find('input').type(optionsToggleComponent.option1);
      cy.contains('mat-form-field', 'Option2').find('input').type(optionsToggleComponent.option2);
      if (optionsToggleComponent.hasOwnProperty('option3')) cy.contains('mat-form-field', 'Option3 (optional)').find('input').type(optionsToggleComponent.option3);
    });
  });

  checklist.TextInput.forEach(textInputComponent => {
    cy.contains('button', 'Add component').click();
    cy.get('.jems-table-config').children().last().within(() => {
      cy.contains('mat-form-field', 'Component type').click();
      cy.root().closest('body').find('mat-option').contains('Text input').click();
      cy.contains('mat-form-field', 'Question text').find('textarea').type(textInputComponent.question);
      cy.contains('mat-form-field', 'Input label').find('input').type(textInputComponent.inputLabel);
      if (textInputComponent.hasOwnProperty('characters')) cy.contains('mat-form-field', 'Characters').find('input').clear().type(textInputComponent.characters);
    })
  });
  cy.contains('button', 'Create').click();
}

function preparePartnerExportData(exportFile) {
  exportFile.content[0].data = exportFile.content[0].data.slice(-9); // limit data to only relevant rows
  exportFile.content[0].data.forEach(data => {
    data[0] = null; // remove call id
    data[1] = null; // remove call name
    data[2] = null; // remove project id
    data[3] = null; // remove project acronym
  });
}

function prepareProjectExportData(exportFile) {
  exportFile.content[0].data = exportFile.content[0].data.slice(-6); // limit data to only relevant rows
  exportFile.content[0].data.forEach(data => {
    data[0] = null; // remove call id
    data[1] = null; // remove call name
    data[2] = null; // remove call start date
    data[3] = null; // remove call end date step 1
    data[4] = null; // remove call end date
    data[6] = null; // remove call project id
    data[7] = null; // remove project acronym
    data[79] = null; // remove first submission date
  });
}
