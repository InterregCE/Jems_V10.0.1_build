import {ChangeDetectionStrategy, Component, ElementRef, ViewChild} from '@angular/core';
import { Alert } from '@common/components/forms/alert';
import {FormService} from '@common/components/section/form/form.service';
import {BehaviorSubject, combineLatest, Observable, of} from 'rxjs';
import {
  AccountingYearDTO, PaymentAccountCorrectionExtensionDTO,
  PaymentAccountDTO,
  ProgrammePriorityDTO, ReconciledAmountPerPriorityDTO, ReconciledAmountUpdateDTO
} from '@cat/api';
import {MatTableDataSource} from '@angular/material/table';
import {AccountsPageStore} from '../../accounts-page.store';
import {catchError, map, take, tap} from 'rxjs/operators';
import {AbstractControl, FormArray, FormBuilder, Validators} from '@angular/forms';
import {AccountsReconciliationStoreService} from './accounts-reconciliation-store.service';
import {UntilDestroy, untilDestroyed} from '@ngneat/until-destroy';
import {APIError} from '@common/models/APIError';
import PaymentAccountStatusEnum = PaymentAccountCorrectionExtensionDTO.PaymentAccountStatusEnum;

@UntilDestroy()
@Component({
  selector: 'jems-accounts-reconciliation-tab',
  templateUrl: './accounts-reconciliation-tab.component.html',
  styleUrls: ['./accounts-reconciliation-tab.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [FormService, AccountsReconciliationStoreService]
})
export class AccountsReconciliationTabComponent {

  Alert = Alert;
  error$ = new BehaviorSubject<APIError | null>(null);
  success$ = new BehaviorSubject(false);
  data$: Observable<{
    accountDetail: PaymentAccountDTO;
    accountReconciliationList: ReconciledAmountPerPriorityDTO[];
    programmePriorities: ProgrammePriorityDTO[];
    isEditable: boolean;
  }>;

  form = this.formBuilder.group({
    comments: this.formBuilder.array([]),
  });
  @ViewChild('reconciliationTable', {read: ElementRef}) private reconciliationTable: ElementRef;

  columnsAvailable = [
    'priority',
    'g1',
    'g2',
    'g3',
    'g4',
    'reconciliation'
  ];
  displayedColumns = this.columnsAvailable;
  dataSource: MatTableDataSource<AbstractControl> = new MatTableDataSource([]);
  mappedData: AccountReconciliationLine[] = [];

  userCanEdit$: Observable<boolean>;
  currentYear: AccountingYearDTO;

  editedRowIndex: number | null = null;

  constructor(
    public accountsPageStore: AccountsPageStore,
    private formService: FormService,
    public pageStore: AccountsReconciliationStoreService,
    private formBuilder: FormBuilder,
  ) {
    this.userCanEdit$ = this.accountsPageStore.userCanEdit$;
    this.data$ = combineLatest([
      this.accountsPageStore.accountDetail$,
      this.pageStore.accountReconciliationOverview$,
      this.pageStore.programmePriorities$,
      this.accountsPageStore.userCanEdit$,
    ]).pipe(
      map(([accountDetail, accountReconciliationList, programmePriorities, isEditable]) => ({
          accountDetail,
          accountReconciliationList,
          programmePriorities,
          isEditable: isEditable && accountDetail.status === PaymentAccountStatusEnum.DRAFT
        })
      ),
      tap(data => this.populateDataSource(data.accountReconciliationList, data.programmePriorities)),
      tap(data => this.currentYear = data.accountDetail.accountingYear),
      tap(data => this.formService.init(this.form)),
    );
  }

  populateDataSource(overviewData: ReconciledAmountPerPriorityDTO[], programmePriorities: ProgrammePriorityDTO[]) {
    this.reconciliationComments.clear();
    programmePriorities.map(priority => {
      const perPriority = overviewData.filter(a => a.priorityAxis === priority.code)[0];
      if (perPriority) {
        const priorityAxisHeader: AccountReconciliationLine = {
          g1: perPriority.reconciledAmountTotal.scenario4Sum,
          g2: perPriority.reconciledAmountTotal.scenario3Sum,
          g3: perPriority.reconciledAmountTotal.clericalMistakesSum,
          comment: perPriority.reconciledAmountTotal.comment,
          translation: '',
          subComponentIndex: 0,
          priorityAxis: perPriority.priorityAxis,
          priorityAxisId: priority.id,
          editable: true
        };
        this.mappedData.push(priorityAxisHeader);
        this.reconciliationComments.push(
          this.formBuilder.group({
            comment: this.formBuilder.control(perPriority.reconciledAmountTotal.comment, Validators.maxLength(500))
          })
        );

        const ofAaRow = ({
          g1: perPriority.reconciledAmountOfAa.scenario4Sum,
          g2: perPriority.reconciledAmountOfAa.scenario3Sum,
          g3: perPriority.reconciledAmountOfAa.clericalMistakesSum,
          comment: perPriority.reconciledAmountOfAa.comment,
          translation: 'payments.accounts.reconciliation.table.row.of.which.aa.audits',
          subComponentIndex: 1,
          priorityAxis: perPriority.priorityAxis,
          priorityAxisId: priority.id,
          editable: true
        } as AccountReconciliationLine);
        this.mappedData.push(ofAaRow);
        this.reconciliationComments.push(
          this.formBuilder.group({
            comment: this.formBuilder.control(perPriority.reconciledAmountOfAa.comment, Validators.maxLength(500))
          })
        );

        const ofEcRow = ({
          g1: perPriority.reconciledAmountOfEc.scenario4Sum,
          g2: perPriority.reconciledAmountOfEc.scenario3Sum,
          g3: perPriority.reconciledAmountOfEc.clericalMistakesSum,
          comment: perPriority.reconciledAmountOfEc.comment,
          translation: 'payments.accounts.reconciliation.table.row.of.which.ec.audits',
          subComponentIndex: 2,
          priorityAxis: perPriority.priorityAxis,
          priorityAxisId: priority.id,
          editable: true
        } as AccountReconciliationLine);
        this.mappedData.push(ofEcRow);
        this.reconciliationComments.push(
          this.formBuilder.group({
            comment: this.formBuilder.control(perPriority.reconciledAmountOfEc.comment, Validators.maxLength(500))
          })
        );
      } else {
        const empty = ({
          g1: 0,
          g2: 0,
          g3: 0,
          comment: '',
          translation: '',
          subComponentIndex: 0,
          priorityAxis: priority.code,
          priorityAxisId: priority.id,
          editable: false
        } as AccountReconciliationLine);
        this.mappedData.push(empty);
        this.reconciliationComments.push(
          this.formBuilder.group({
            comment: this.formBuilder.control('', Validators.maxLength(500))
          })
        );

        const emptyOfAa = ({
          g1: 0,
          g2: 0,
          g3: 0,
          comment: '',
          translation: 'payments.accounts.reconciliation.table.row.of.which.aa.audits',
          subComponentIndex: 1,
          priorityAxis: priority.code,
          priorityAxisId: priority.id,
          editable: false
        } as AccountReconciliationLine);
        this.mappedData.push(emptyOfAa);
        this.reconciliationComments.push(
          this.formBuilder.group({
            comment: this.formBuilder.control('', Validators.maxLength(500))
          })
        );

        const emptyOfEc = ({
          g1: 0,
          g2: 0,
          g3: 0,
          comment: '',
          translation: 'payments.accounts.reconciliation.table.row.of.which.ec.audits',
          subComponentIndex: 2,
          priorityAxis: priority.code,
          priorityAxisId: priority.id,
          editable: false
        } as AccountReconciliationLine);
        this.mappedData.push(emptyOfEc);
        this.reconciliationComments.push(
          this.formBuilder.group({
            comment: this.formBuilder.control('', Validators.maxLength(500))
          })
        );
      }
    });
    this.dataSource.data = [...this.reconciliationComments.controls];
    this.formService.setEditable(true);
    this.formService.resetEditable();
  }

  editAmounts(rowIndex: number) {
    this.editedRowIndex = rowIndex;
    setTimeout(() => {
      this.scrollToRight();
    });
  }
  scrollToRight(): void {
    this.reconciliationTable.nativeElement.scrollLeft = this.reconciliationTable.nativeElement.scrollWidth;
  }

  get reconciliationComments(): FormArray {
    return this.form.get('comments') as FormArray;
  }

  submitAmountChanges(rowIndex: number, reconciliation: AccountReconciliationLine) {
    const updateDto = {
      priorityAxisId: reconciliation.priorityAxisId,
      type: this.getType(reconciliation),
      comment: this.reconciliationComments.at(rowIndex)?.get('comment')?.value,
    } as ReconciledAmountUpdateDTO;
    this.pageStore.updateReconciliation(updateDto).pipe(
      take(1),
      tap(_ => this.showSuccessMessageAfterUpdate()),
      catchError(err => this.showErrorMessage(err.error)),
      untilDestroyed(this)
    ).subscribe();
    this.editedRowIndex = null;
    this.formService.setDirty(false);
  }

  discardChanges(rowIndex: number, reconciliation: AccountReconciliationLine) {
    this.reconciliationComments.at(rowIndex).patchValue({
      comment: reconciliation.comment,
    });
    this.editedRowIndex = null;
    this.formService.setDirty(false);
  }

  getType(reconciliation: AccountReconciliationLine): ReconciledAmountUpdateDTO.TypeEnum {
    if (reconciliation.subComponentIndex == 0)
    {
      return ReconciledAmountUpdateDTO.TypeEnum.Total;
    }
    if (reconciliation.subComponentIndex == 1)
    {
      return ReconciledAmountUpdateDTO.TypeEnum.OfAa;
    }
    return ReconciledAmountUpdateDTO.TypeEnum.OfEc;
  }

  private showSuccessMessageAfterUpdate(): void {
    this.success$.next(true);
    setTimeout(() => this.success$.next(false), 4000);
  }

  private showErrorMessage(error: APIError): Observable<null> {
    this.error$.next(error);
    setTimeout(() => {
      if (this.error$.value?.id === error.id) {
        this.error$.next(null);
      }
    }, 10000);
    return of(null);
  }

  reconciliation(index: number): AccountReconciliationLine {
    return this.mappedData[index];
  }
}

interface AccountReconciliationLine {
  g1: number;
  g2: number;
  g3: number;
  comment: string;
  translation: string;
  subComponentIndex: number;
  priorityAxis: string;
  priorityAxisId: number;
  editable: boolean;
}
