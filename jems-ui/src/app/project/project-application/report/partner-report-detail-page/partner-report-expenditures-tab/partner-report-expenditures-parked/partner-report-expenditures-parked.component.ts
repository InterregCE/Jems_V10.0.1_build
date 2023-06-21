import { ChangeDetectionStrategy, Component, Input } from '@angular/core';
import { UntilDestroy, untilDestroyed } from '@ngneat/until-destroy';
import { Forms } from '@common/utils/forms';
import { MatDialog } from '@angular/material/dialog';
import {
  PartnerReportExpendituresStore
} from '@project/project-application/report/partner-report-detail-page/partner-report-expenditures-tab/partner-report-expenditures-store.service';
import { ProjectPartnerReportParkedExpenditureDTO } from '@cat/api';
import { Observable } from 'rxjs';
import { filter, switchMap, take } from 'rxjs/operators';

@UntilDestroy()
@Component({
  selector: 'jems-partner-report-expenditures-parked',
  templateUrl: './partner-report-expenditures-parked.component.html',
  styleUrls: ['./partner-report-expenditures-parked.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class PartnerReportExpendituresParkedComponent {

  @Input()
  listDirty$: Observable<boolean>;

  @Input()
  expenditures: ParkedExpenditure[] = [];

  @Input()
  columns: string[] = [];

  constructor(
    public pageStore: PartnerReportExpendituresStore,
    private dialog: MatDialog,
  ) {
  }

  reInclude(parked: ParkedExpenditure) {
    Forms.confirmDialog(
      this.dialog,
      'project.application.partner.report.expenditures.re.include',
      'project.application.partner.report.expenditures.re.include.notif',
      { reportNumber: parked.expenditure.parkingMetadata.reportOfOriginNumber },
    ).pipe(
      take(1),
      filter(yes => !!yes),
      switchMap(() => this.pageStore.reIncludeParkedExpenditure(parked.expenditure.id)),
      untilDestroyed(this),
    ).subscribe();
  }

  delete(parked: ParkedExpenditure) {
    Forms.confirmDialog(
      this.dialog,
      'common.delete.entry',
      'project.application.partner.report.expenditures.parked.delete.notif',
      { reportNumber: parked.expenditure.parkingMetadata.reportOfOriginNumber },
    ).pipe(
      take(1),
      filter(yes => !!yes),
      switchMap(() => this.pageStore.deleteParkedExpenditure(parked.expenditure.id)),
      untilDestroyed(this),
    ).subscribe();
  }

}

export interface ParkedExpenditure extends ProjectPartnerReportParkedExpenditureDTO {
  canBeReIncluded: boolean;
  contractName: string | undefined;
}
