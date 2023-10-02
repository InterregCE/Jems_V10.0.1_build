export const partnerReportPage = {
  verifyAmountsInTables(expectedResults) {
    for (let table in expectedResults) {
      for (let row in expectedResults[table]) {
        for (let column in expectedResults[table][row]) {
          verifyByTableAndRowAndColumn(table, row, column, expectedResults[table][row][column])
        }
      }
    }
  }
}

function verifyByTableAndRowAndColumn(table, row, column, amount) {
  cy.get(`jems-partner-breakdown-${table} *[role="row"]:contains("${row}")`).find(`.mat-column-${column}`).then(element => {
    expect(element.text()).to.contain(amount)
  })
}