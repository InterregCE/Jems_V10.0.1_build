import user from '../../fixtures/users.json';
import basicData from '../../fixtures/programme/basic.data.json';
import languages from '../../fixtures/programme/languages.json';
import funds from '../../fixtures/programme/funds.json';
import priorities from '../../fixtures/programme/priorities.json';
import resultIndicators from '../../fixtures/programme/result.indicators.json';
import outputIndicators from '../../fixtures/programme/output.indicators.json';
import strategies from '../../fixtures/programme/strategies.json';
import lumpSums from '../../fixtures/programme/lump.sums.json';
import unitCosts from '../../fixtures/programme/unit.costs.json';
import stateAid from '../../fixtures/programme/state.aid.json';

context('Programme management tests', () => {

  beforeEach(() => {
    cy.viewport(1920, 1080);
    cy.loginByRequest(user.programmeUser.email);
  });

  it('TB-387 Programme Basic data can be configured', () => {

    cy.visit('/app/programme', {failOnStatusCode: false});

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

    cy.get('jems-alert p').should('contain.text', 'Programme data was successfully saved.');
  });

  it('Programme Languages can be configured', () => {

    cy.visit('/app/programme/languages', {failOnStatusCode: false});

    cy.contains('Edit').click();

    languages.forEach(language => {
      cy.contains('tr', language.language).then(el => {
        cy.wrap(el).find('input').check({force: true});
      });
    });

    cy.contains('Save changes').click();

    cy.get('jems-alert p').should('contain.text', 'Programme data was successfully saved.');
  });

  it('Programme NUTS can be downloaded', () => {

    cy.visit('/app/programme/areas', {failOnStatusCode: false});

    cy.contains('Download').click();
    cy.get('jems-alert p', {timeout: 20000}).should('contain.text', 'Up to date NUTS dataset was successfully downloaded.');

    cy.contains('DE Deutschland').click();
    cy.contains('RU Russian Federation').click();

    cy.contains('Save changes').click();
    cy.get('jems-alert p').should('contain.text', 'Programme regions saved successfully');
  });

  it('Programme Funds can be configured', () => {

    cy.visit('/app/programme/funds', {failOnStatusCode: false});

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

  it('Programme Priorities can be configured', () => {

    cy.visit('/app/programme/priorities', {failOnStatusCode: false});

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
        cy.contains('div.ng-pristine', specificObjective.programmeObjectivePolicy).then(el => {
          cy.wrap(el).find('input[type="checkbox"]').check({force: true});
          cy.wrap(el).contains('div', 'Programme specific objective code').find('input').type(specificObjective.code);
        });
      });

      cy.contains('Add priority').click();
      cy.contains('Confirm').should('be.visible').click();

      cy.contains('jems-programme-priority-list-page div', priority.code).should('exist');
    });
  });

  it('Programme result indicators can be configured', () => {

    cy.visit('/app/programme/indicators', {failOnStatusCode: false});

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

  it('Programme output indicators can be configured', () => {

    cy.visit('/app/programme/indicators', {failOnStatusCode: false});

    outputIndicators.forEach(indicator => {
      cy.contains('Create output indicator').click();

      cy.get('input[name="identifier"').type(indicator.identifier);
      cy.get('mat-select[name="indicatorCode"').click();
      cy.contains('mat-option', indicator.code).click();

      cy.get('mat-select[name="specificObjective"').click();
      cy.contains('mat-option', indicator.programmeObjectivePolicy).click();

      cy.get('mat-select[formcontrolname="resultIndicatorId"').click();
      cy.contains('mat-option', indicator.resultIndicator).click();

      cy.get('input[name="milestone"').type(indicator.milestone.toString());
      cy.get('input[name="finalTarget"').type(indicator.finalTarget);

      cy.contains('Save').click();
      cy.contains('Confirm').should('be.visible').click();

      cy.contains('jems-programme-output-indicators-list div', indicator.code).should('exist');
    });
  });

  it('Programme Strategies can be configured', () => {

    cy.visit('/app/programme/strategies', {failOnStatusCode: false});

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

  it('Programme Legal status can be configured', () => {

    cy.visit('/app/programme/legalStatus', {failOnStatusCode: false});

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

  it('Programme Lump Sums can be configured', () => {

    cy.visit('/app/programme/costs', {failOnStatusCode: false});

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

      cy.contains('button', lumpSum.splittingAllowed).click();
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

  it('Programme Unit Costs can be configured', () => {

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

  it('Programme State Aid can be configured', () => {

    cy.visit('/app/programme/stateAid', {failOnStatusCode: false});

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
})
