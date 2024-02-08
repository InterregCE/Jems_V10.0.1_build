import {ChangeDetectionStrategy, Component} from '@angular/core';
import {combineLatest, Observable} from 'rxjs';
import {
  AccountingYearDTO,
  AmountWithdrawnPerPriorityDTO,
  InputTranslation,
  PaymentAccountDTO,
  ProgrammePriorityDTO,
} from '@cat/api';
import {AccountsPageStore} from '../../accounts-page.store';
import {map, tap} from 'rxjs/operators';
import {UntilDestroy} from '@ngneat/until-destroy';
import {MatTableDataSource} from '@angular/material/table';
import {AccountsWithdrawnStoreService} from './accounts-withdrawn-store.service';

@UntilDestroy()
@Component({
  selector: 'jems-accounts-withdrawn-tab',
  templateUrl: './accounts-withdrawn-tab.component.html',
  styleUrls: ['./accounts-withdrawn-tab.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [AccountsWithdrawnStoreService]
})
export class AccountsWithdrawnTabComponent {

  data$: Observable<{
    accountDetail: PaymentAccountDTO;
    accountWithdrawalList: AmountWithdrawnPerPriorityDTO[];
    programmePriorities: ProgrammePriorityDTO[];
  }>;

  columnsAvailable = [
    'priority',
    'withdrawals',
    'correspondingPublicContribution',
  ];
  displayedColumns = this.columnsAvailable;
  dataSource: MatTableDataSource<AccountWithdrawalLine> = new MatTableDataSource([]);

  userCanEdit$: Observable<boolean>;

  constructor(
    public accountsPageStore: AccountsPageStore,
    public listStore: AccountsWithdrawnStoreService,
  ) {
    this.userCanEdit$ = this.accountsPageStore.userCanEdit$;
    this.data$ = combineLatest([
      this.accountsPageStore.accountDetail$,
      this.listStore.accountWithdrawalOverview$,
      this.listStore.programmePriorities$
    ]).pipe(
      map(([accountDetail, accountWithdrawalList, programmePriorities]) => ({
          accountDetail,
          accountWithdrawalList,
          programmePriorities
        })
      ),
      tap(data => this.populateDataSource(data.accountWithdrawalList, data.programmePriorities))
    );
  }

  populateDataSource(overviewData: AmountWithdrawnPerPriorityDTO[], programmePriorities: ProgrammePriorityDTO[]) {

    const data = programmePriorities.map(priority => {
      const perPriority = overviewData.filter(a => a.priorityAxis === priority.code)[0];

      if (perPriority) {

        const priorityAxisHeader: AccountWithdrawalLine = {
          total: perPriority.withdrawalTotal,
          public: perPriority.withdrawalPublic,
          translation: '',
          subComponentIndex: 0,
          priorityAxis: perPriority.priorityAxis,
          year: null
        };

        const years = perPriority.perYear.map(perYear => {

          if (perYear.withdrawalTotal) {
            const totalPerYearRow = ({
              total: perYear.withdrawalTotal,
              public: perYear.withdrawalPublic,
              translation: 'payments.accounts.withdrawn.table.row.of.which.accounting.year.total',
              subComponentIndex: 1,
              priorityAxis: perPriority.priorityAxis,
              year: perYear.year
            } as AccountWithdrawalLine);

            const ofAaRow = ({
              total: perYear.withdrawalTotalOfWhichAa,
              public: perYear.withdrawalPublicOfWhichAa,
              translation: 'payments.accounts.withdrawn.table.row.of.which.aa.audits',
              subComponentIndex: 2,
              priorityAxis: perPriority.priorityAxis,
              year: null
            } as AccountWithdrawalLine);

            const ofEcRow = ({
              total: perYear.withdrawalTotalOfWhichEc,
              public: perYear.withdrawalPublicOfWhichEc,
              translation: 'payments.accounts.withdrawn.table.row.of.which.ec.audits',
              subComponentIndex: 3,
              priorityAxis: perPriority.priorityAxis,
              year: null
            } as AccountWithdrawalLine);

            return ([
              totalPerYearRow,
              ofAaRow,
              ofEcRow
            ] as AccountWithdrawalLine[]);
          } else {
            return ([]);
          }

        }).flat();

        years.splice(0, 0, priorityAxisHeader);

        return years;

      } else {
        return {
          total: 0,
          public: 0,
          translation: '',
          subComponentIndex: 0,
          priorityAxis: priority.code,
          year: null
        };
      }
    });

    this.dataSource.data = data.flat();
  }
}

interface AccountWithdrawalLine {
  total: number;
  public: number;
  translation: string | InputTranslation[];
  subComponentIndex: number;
  priorityAxis: string;
  year: AccountingYearDTO | null;
}

