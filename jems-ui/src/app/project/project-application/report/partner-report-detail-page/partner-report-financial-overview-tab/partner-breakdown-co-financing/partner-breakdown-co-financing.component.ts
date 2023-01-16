import {ChangeDetectionStrategy, Component, Input, OnChanges, SimpleChanges} from '@angular/core';
import {UntilDestroy} from '@ngneat/until-destroy';
import {FormService} from '@common/components/section/form/form.service';
import {
  CallFundRateDTO,
  ExpenditureCoFinancingBreakdownDTO,
  ExpenditureCoFinancingBreakdownLineDTO,
  InputTranslation,
} from '@cat/api';
import {MatTableDataSource} from '@angular/material/table';

@UntilDestroy()
@Component({
  selector: 'jems-partner-breakdown-co-financing',
  templateUrl: './partner-breakdown-co-financing.component.html',
  styleUrls: ['./partner-breakdown-co-financing.component.scss'],
  providers: [FormService],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class PartnerBreakdownCoFinancingComponent implements OnChanges {

  certifiedColumns = ['totalEligibleAfterControl'];
  columnsAvailable = ['type', 'totalEligibleBudget', 'previouslyReported', 'currentReport', 'totalEligibleAfterControl', 'totalReportedSoFar', 'totalReportedSoFarPercentage', 'remainingBudget', 'previouslyPaid'];
  displayedColumns = this.columnsAvailable;

  @Input()
  breakdown: ExpenditureCoFinancingBreakdownDTO;
  @Input()
  funds: CallFundRateDTO[];
  @Input()
  isCertified = false;

  dataSource: MatTableDataSource<ExpenditureLine> = new MatTableDataSource([]);

  ngOnChanges(changes: SimpleChanges): void {
    this.dataSource.data = [
      ...PartnerBreakdownCoFinancingComponent.addTranslationObjects(this.breakdown.funds, this.funds),
      { ...this.breakdown.publicContribution, translation: 'project.application.partner.report.financial.contribution.public', isProgrammeLanguage: false, subcomponent: true },
      { ...this.breakdown.automaticPublicContribution, translation: 'project.application.partner.report.financial.contribution.auto.public', isProgrammeLanguage: false, subcomponent: true },
      { ...this.breakdown.privateContribution, translation: 'project.application.partner.report.financial.contribution.auto.private', isProgrammeLanguage: false, subcomponent: true },
    ];
    this.displayedColumns = [...this.columnsAvailable]
      .filter(column => this.isCertified || !this.certifiedColumns.includes(column));
  }

  private static addTranslationObjects(fundsCumulative: ExpenditureCoFinancingBreakdownLineDTO[], callFunds: CallFundRateDTO[]): ExpenditureLine[] {
    return fundsCumulative.map(fundCumulative => {
      const isPartnerContribution = !fundCumulative.fundId;
      const translation = isPartnerContribution
        ? 'project.partner.coFinancing.partnerContribution'
        : callFunds.find(x => x.programmeFund.id === fundCumulative.fundId)?.programmeFund?.abbreviation || [];

      return ({
        ...fundCumulative,
        translation,
        isProgrammeLanguage: !isPartnerContribution,
        subcomponent: false,
      });
    });
  }

}

interface ExpenditureLine extends ExpenditureCoFinancingBreakdownLineDTO {
  translation: string | InputTranslation[];
  isProgrammeLanguage: boolean;
  subcomponent: boolean;
}
