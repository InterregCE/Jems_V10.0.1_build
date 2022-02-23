import user from '../fixtures/users.json';
import resultIndicators from '../fixtures/programme/result.indicators.json';
import outputIndicators from '../fixtures/programme/output.indicators.json';
import strategies from '../fixtures/programme/strategies.json';
import lumpSums from '../fixtures/programme/lump.sums.json';
import unitCosts from '../fixtures/programme/unit.costs.json';
import stateAid from '../fixtures/programme/state.aid.json';

context('Programme management tests', () => {

  beforeEach(() => {
    cy.viewport(1920, 1080);
    cy.loginByRequest(user.programmeUser);
  });

  it('Programme Basic data can be configured', () => {

    cy.visit('/app/programme');

    cy.contains('Edit').click();

    cy.get('input[name="cci"]').type('AUTO-PROGRAMME1');
    cy.get('input[name="title"]').type('Profile#1');
    cy.get('input[name="version"]').type('4');
    cy.get('input[name="firstYear"]').type('2020');
    cy.get('input[name="lastYear"]').type('2027');
    cy.get('input[name="eligibleFrom"]').type('01/01/2020');
    cy.get('input[name="eligibleUntil"]').type('12/31/2030');
    cy.get('input[name="commissionDecisionNumber"]').type('AUTO-PROGRAMME2');
    cy.get('input[name="commissionDecisionDate"]').type('12/01/2020');
    cy.get('input[name="programmeAmendingDecisionNumber"]').type('AUTO-PROGRAMME3');
    cy.get('input[name="programmeAmendingDecisionDate"]').type('12/01/2020');

    cy.contains('Save').click();
    cy.contains('Confirm').click();

    cy.get('jems-alert p').should('contain.text', 'Programme data was successfully saved.');
  });

  it('Programme Languages can be configured', () => {

    cy.visit('/app/programme/languages');

    cy.contains('Edit').click();

    cy.get('tr td').contains('Deutsch').parent().then(el => {
      cy.wrap(el).find('td').eq(0).find('input[type="checkbox"]').check({force: true});
      cy.wrap(el).find('td').eq(1).find('input[type="checkbox"]').check({force: true});
    });

    cy.contains('Save changes').click();

    cy.get('jems-alert p').should('contain.text', 'Programme data was successfully saved.');
  });

  it('Programme NUTS can be downloaded', () => {

    cy.visit('/app/programme/areas');

    cy.contains('Download').click();
    cy.get('jems-alert p', {timeout: 20000}).should('contain.text', 'Up to date NUTS dataset was successfully downloaded.');

    cy.contains('DE Deutschland').click();
    cy.contains('RU Russian Federation').click();

    cy.contains('Save changes').click();
    cy.get('jems-alert p').should('contain.text', 'Programme regions saved successfully');
  });

  it('Programme Funds can be configured', () => {

    cy.visit('/app/programme/funds');

    cy.contains('Edit').click();
    cy.contains('ERDF').parent().then(el => {
      cy.contains('button', 'EN').click();
      cy.wrap(el).find('input[name="multipleFundsAllowed"]').check({force: true});
      cy.contains('button', 'DE').click();
      cy.wrap(el).find('input[placeholder="ERDF"]').type('ERDF DE');
      cy.wrap(el).find('input[placeholder="Territorial cooperation Goal (Interreg)"]').type('Territorial cooperation Goal (Interreg) DE');
    });

    cy.contains('NEIGHBOURHOOD_CBC').parent().then(el => {
      cy.contains('button', 'EN').click();
      cy.wrap(el).find('input[name="multipleFundsAllowed"]').check({force: true});
      cy.contains('button', 'DE').click();
      cy.wrap(el).find('input[placeholder="Neighbourhood CBC"]').type('Neighbourhood CBC DE');
      cy.wrap(el).find('input[placeholder="Interreg A, external cross-border cooperation"]').type('Interreg A, external cross-border cooperation DE');
    });

    cy.contains('Save changes').click();
    cy.get('jems-alert p').should('contain.text', 'Programme funds saved successfully.');
  });

  it('Programme Priorities can be configured', () => {

    cy.visit('/app/programme/priorities');

    cy.contains('Add priority').click();
    cy.contains('button', 'EN').click();
    cy.get('input[ng-reflect-name="code"]').type('PO1');
    cy.get('input[ng-reflect-name="translation"]').type('PO1 Innovation EN');
    cy.contains('button', 'DE').click();
    cy.get('input[ng-reflect-name="translation"]').type('PO1 Innovation DE');

    const policyObjective1 = 'PO1 - A more competitive and smarter Europe';
    cy.get('mat-select[ng-reflect-name="objective"]').click();
    cy.contains(policyObjective1).click();

    const specificObjective1 = 'Developing and enhancing research and innovation capacities and the uptake of advanced technologies';
    cy.contains('div[ng-reflect-name="specificObjectives"]', specificObjective1).then(el => {
      cy.wrap(el).find('input[type="checkbox"]').check({force: true});
      cy.wrap(el).find('input[ng-reflect-name="code"]').type('SO1.1');
    });

    cy.contains('Add priority').click();
    cy.contains('Confirm').click();

    cy.contains('jems-programme-priority-list-page div', specificObjective1).should('exist');

    cy.contains('Add priority').click();
    cy.contains('button', 'EN').click();
    cy.get('input[ng-reflect-name="code"]').type('PO2');
    cy.get('input[ng-reflect-name="translation"]').type('PO2 Environment EN');
    cy.contains('button', 'DE').click();
    cy.get('input[ng-reflect-name="translation"]').type('PO2 Environment DE');

    cy.get('mat-select[ng-reflect-name="objective"]').click();
    const policyObjective2 = 'PO2 - A greener, low-carbon transitioning towards a net zero carbon economy and resilient Europe';
    cy.contains(policyObjective2).click();

    const specificObjective2 = 'Promoting energy efficiency and reducing greenhouse gas emissions';
    cy.contains('div[ng-reflect-name="specificObjectives"]', specificObjective2).then(el => {
      cy.wrap(el).find('input[type="checkbox"]').check({force: true});
      cy.wrap(el).find('input[ng-reflect-name="code"]').type('SO2.1');
    });

    cy.contains('Add priority').click();
    cy.contains('Confirm').click();

    cy.contains('jems-programme-priority-list-page div', specificObjective2).should('exist');
  });

  it('Programme result indicators can be configured', () => {

    cy.visit('/app/programme/indicators');

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
      cy.contains('Confirm').click();

      cy.contains('jems-programme-result-indicators-list div', indicator.code).should('exist');
    });
  });

  it('Programme output indicators can be configured', () => {

    cy.visit('/app/programme/indicators');

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
      cy.contains('Confirm').click();

      cy.contains('jems-programme-output-indicators-list div', indicator.code).should('exist');
    });
  });

  it('Programme Strategies can be configured', () => {

    cy.visit('/app/programme/strategies');

    cy.contains('button', 'Edit').click();

    strategies.forEach(strategy => {
      cy.contains('mat-checkbox', strategy.strategy).then(el => {
        cy.wrap(el).find('input[type="checkbox"]').check({force: true});
      });
    });

    cy.contains('Save').click();
    cy.contains('Confirm').click();

    cy.get('jems-alert p').should('contain.text', 'Programme strategies successfully updated');
  });

  it('Programme Legal status can be configured', () => {

    cy.visit('/app/programme/legalStatus');

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

    cy.visit('/app/programme/costs');

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
      cy.contains('Confirm').click();
      cy.contains('button', lumpSum.name[0].language).click();
      cy.contains('jems-programme-lump-sums-list div', lumpSum.name[0].translation).should('exist');
    });
  });

  it('Programme Unit Costs can be configured', () => {

    cy.visit('/app/programme/costs');

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
      cy.contains('Confirm').click();
      cy.contains('button', unitCost.name[0].language).click();
      cy.contains('jems-programme-unit-costs-list div', unitCost.name[0].translation).should('exist');
    });
  });

  it('Programme State Aid can be configured', () => {

    cy.visit('/app/programme/stateAid');

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
