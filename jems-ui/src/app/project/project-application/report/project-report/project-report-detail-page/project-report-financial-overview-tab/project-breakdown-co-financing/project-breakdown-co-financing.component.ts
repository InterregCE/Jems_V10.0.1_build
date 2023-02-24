import {ChangeDetectionStrategy, Component, Input, OnChanges, SimpleChanges} from '@angular/core';
import {FormService} from '@common/components/section/form/form.service';
import {
  CallFundRateDTO,
  CertificateCoFinancingBreakdownDTO, CertificateCoFinancingBreakdownLineDTO, InputTranslation
} from '@cat/api';
import {MatTableDataSource} from '@angular/material/table';

@Component({
  selector: 'jems-project-breakdown-co-financing',
  templateUrl: './project-breakdown-co-financing.component.html',
  styleUrls: ['./project-breakdown-co-financing.component.scss'],
  providers: [FormService],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ProjectBreakdownCoFinancingComponent implements OnChanges {

  columnsAvailable = ['type', 'totalEligibleBudget', 'previouslyReported', 'currentReport', 'totalReportedSoFar', 'totalReportedSoFarPercentage', 'remainingBudget', 'previouslyPaid'];
  displayedColumns = this.columnsAvailable;

  @Input()
  breakdown: CertificateCoFinancingBreakdownDTO;
  @Input()
  funds: CallFundRateDTO[];

  dataSource: MatTableDataSource<CertificateLine> = new MatTableDataSource([]);

  ngOnChanges(changes: SimpleChanges): void {
    this.dataSource.data = [
      ...ProjectBreakdownCoFinancingComponent.addTranslationObjects(this.breakdown.funds, this.funds),
      { ...this.breakdown.publicContribution, translation: 'project.application.partner.report.financial.contribution.public', isProgrammeLanguage: false, subcomponent: true },
      { ...this.breakdown.automaticPublicContribution, translation: 'project.application.partner.report.financial.contribution.auto.public', isProgrammeLanguage: false, subcomponent: true },
      { ...this.breakdown.privateContribution, translation: 'project.application.partner.report.financial.contribution.auto.private', isProgrammeLanguage: false, subcomponent: true },
    ];
    this.displayedColumns = [...this.columnsAvailable];
  }

  private static addTranslationObjects(fundsCumulative: CertificateCoFinancingBreakdownLineDTO[], callFunds: CallFundRateDTO[]): CertificateLine[] {
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

interface CertificateLine extends CertificateCoFinancingBreakdownLineDTO {
  translation: string | InputTranslation[];
  isProgrammeLanguage: boolean;
  subcomponent: boolean;
}
