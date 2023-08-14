import {UntilDestroy} from '@ngneat/until-destroy';
import {ChangeDetectionStrategy, Component, Input, OnChanges, SimpleChanges} from '@angular/core';
import {FormService} from '@common/components/section/form/form.service';
import {MatTableDataSource} from '@angular/material/table';
import {
  FinancingSourceBreakdownDTO,
  FinancingSourceBreakdownLineDTO, FinancingSourceFundDTO, InputTranslation
} from '@cat/api';

@UntilDestroy()
@Component({
  selector: 'jems-verification-report-total-eligible-per-sources',
  templateUrl: './verification-report-total-eligible-per-sources.component.html',
  styleUrls: ['./verification-report-total-eligible-per-sources.component.scss'],
  providers: [FormService],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class VerificationReportTotalEligiblePerSourcesComponent implements OnChanges {

  initialColumns = [
    'partnerReport',
    'partner',
  ];

  lastColumns = [
    'partnerContribution',
    'publicContribution',
    'automaticPublicContribution',
    'privateContribution',
    'total',
  ];

  displayedColumns = [...this.initialColumns, ...this.lastColumns];
  funds: FinancingSourceFundDTO[] = [];

  @Input()
  breakdown: FinancingSourceBreakdownDTO;

  ngOnChanges(changes: SimpleChanges): void {
    this.funds = this.breakdown.total.fundsSorted;
    const fundColumns = this.breakdown.total.fundsSorted.map(fund => fund.id.toString());
    this.displayedColumns = [...this.initialColumns, ...fundColumns, ...this.lastColumns];
    const financingSourceBreakdownLines: FinancingSourceBreakdownLine[] = [];
    this.breakdown.sources.forEach(breakdown => {
      financingSourceBreakdownLines.push({
        ...breakdown,
        fundValues: new Map(breakdown.fundsSorted.map(fund => [fund.id, fund.amount])),
        isSplit: false
      } as FinancingSourceBreakdownLine);
      if (breakdown.split.length > 1) {
        breakdown.split.forEach(split => {
          financingSourceBreakdownLines.push({
            isSplit: true,
            splitFundValue: split.value,
            splitFundId: split.fundId,
            splitFundAbbreviation: this.funds.find(fund => fund.id === split.fundId)?.abbreviation,
            partnerContribution: split.partnerContribution,
            publicContribution: split.publicContribution,
            automaticPublicContribution: split.automaticPublicContribution,
            privateContribution: split.privateContribution,
            total: split.total
          } as FinancingSourceBreakdownLine);
        });
      }
    });
    this.dataSource.data = financingSourceBreakdownLines;
  }

  dataSource: MatTableDataSource<FinancingSourceBreakdownLine> = new MatTableDataSource([]);

  shouldDisplayBorderBottom(index: number){
    return !(index == this.dataSource.data.length - 1 || (this.dataSource.data[index].isSplit && this.dataSource.data[index + 1].isSplit));
  }
}

interface FinancingSourceBreakdownLine extends FinancingSourceBreakdownLineDTO {
  isSplit: boolean;
  fundValues: Map<number, number>;
  splitFundValue: number | undefined;
  splitFundId: number | undefined;
  splitFundAbbreviation: InputTranslation[];
}
