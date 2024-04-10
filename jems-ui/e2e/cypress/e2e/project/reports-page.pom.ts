export const partnerReportPage = {
  verifyAmountsInTables(expectedResults) {
      verifyReportPageByTableAndRowAndColumn('jems-partner-breakdown', expectedResults)
  }
}

export const projectReportPage = {
    verifyAmountsInTables(expectedResults, nestedTableSelector: string = '') {
        verifyReportPageByTableAndRowAndColumn('jems-project-breakdown', expectedResults, nestedTableSelector)
    },
    verifySpendingProfile(expectedResults, nestedTableSelector: string = '') {
        verifyReportPageByTableAndRowAndColumn(null, expectedResults, nestedTableSelector)
    }
}


function verifyReportPageByTableAndRowAndColumn(tablePrefix: string, expectedResults: any, nestedTableSelector: string = '') {
    for (let table in expectedResults) {
        for (let row in expectedResults[table]) {
            for (let column in expectedResults[table][row]) {

                const amount =  expectedResults[table][row][column];
                let tableName = table;
                if (tablePrefix)
                  tableName = `${tablePrefix}-${table}`;
                cy.get(`${tableName} ${nestedTableSelector} *[role="row"]:contains("${row}")`).find(`.mat-column-${column}`)
                    .then(element => { expect(element.text()).to.contain(amount) })
            }
        }
    }
}
