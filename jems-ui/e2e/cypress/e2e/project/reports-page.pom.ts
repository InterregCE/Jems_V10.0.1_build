export const partnerReportPage = {
  verifyAmountsInTables(expectedResults) {
      verifyReportPageByTableAndRowAndColumn("jems-partner-breakdown", expectedResults)
  }
}

export const projectReportPage = {
    verifyAmountsInTables(expectedResults) {
        verifyReportPageByTableAndRowAndColumn("jems-project-breakdown", expectedResults)
    }
}


function verifyReportPageByTableAndRowAndColumn(tablePrefix: string, expectedResults: any) {
    for (let table in expectedResults) {
        for (let row in expectedResults[table]) {
            for (let column in expectedResults[table][row]) {

                const amount =  expectedResults[table][row][column];
                cy.get(`${tablePrefix}-${table} *[role="row"]:contains("${row}")`).find(`.mat-column-${column}`)
                    .then(element => { expect(element.text()).to.contain(amount) })
            }
        }
    }
}
