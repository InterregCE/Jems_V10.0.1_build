import {ChangeDetectionStrategy, Component, OnDestroy} from '@angular/core';
import {BaseComponent} from '@common/components/base-component';
import {Permission} from '../../../../security/permissions/permission';
import {combineLatest, merge, Subject} from 'rxjs';
import {I18nValidationError} from '@common/validation/i18n-validation-error';
import {
  InputProgrammeFundWrapper,
  OutputProgrammeData,
  ProgrammeDataService,
  ProgrammeFundService,
  ProgrammePriorityService
} from '@cat/api';
import {catchError, flatMap, map, startWith, tap} from 'rxjs/operators';
import {Log} from '../../../../common/utils/log';
import {HttpErrorResponse} from '@angular/common/http';
import {TabService} from '../../../../common/services/tab.service';
import {Tables} from '../../../../common/utils/tables';
import {MatSort} from '@angular/material/sort';

@Component({
  selector: 'app-programme-page',
  templateUrl: './programme-page.component.html',
  styleUrls: ['./programme-page.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ProgrammePageComponent extends BaseComponent implements OnDestroy {
  Permission = Permission;

  programmeSaveError$ = new Subject<I18nValidationError | null>();
  programmeSaveSuccess$ = new Subject<boolean>();
  saveProgrammeData$ = new Subject<OutputProgrammeData>();

  fundsSaveError$ = new Subject<I18nValidationError | null>();
  fundsSaveSuccess$ = new Subject<boolean>();
  saveFunds$ = new Subject<InputProgrammeFundWrapper>();

  activeTab$ = this.tabService.currentTab(ProgrammePageComponent.name);

  newPageSize$ = new Subject<number>();
  newPageIndex$ = new Subject<number>();
  newSort$ = new Subject<Partial<MatSort>>();

  private programmeById$ = this.programmeDataService.get()
    .pipe(
      tap(programmeData => Log.info('Fetched programme data:', this, programmeData))
    );

  private savedProgramme$ = this.saveProgrammeData$
    .pipe(
      flatMap(programmeUpdate => this.programmeDataService.update(programmeUpdate)),
      tap(saved => Log.info('Updated programme:', this, saved)),
      tap(() => this.programmeSaveSuccess$.next(true)),
      tap(() => this.programmeSaveError$.next(null)),
      catchError((error: HttpErrorResponse) => {
        this.programmeSaveError$.next(error.error);
        throw error;
      })
    );

  programme$ = merge(this.programmeById$, this.savedProgramme$)

  private initialFunds$ = this.programmeFundService.getProgrammeFundList()
    .pipe(
      tap(funds => Log.info('Fetched programme funds:', this, funds))
    );

  private savedFunds$ = this.saveFunds$
    .pipe(
      flatMap(funds => this.programmeFundService.updateProgrammeFundList(funds)),
      tap(saved => Log.info('Updated programme funds:', this, saved)),
      tap(() => this.fundsSaveSuccess$.next(true)),
      tap(() => this.fundsSaveError$.next(null)),
      catchError((error: HttpErrorResponse) => {
        this.fundsSaveError$.next(error.error);
        throw error;
      })
    );

  funds$ = merge(this.initialFunds$, this.savedFunds$)
    .pipe(
      map(funds => funds.map(fund => ({
        id: fund.id,
        selected: fund.selected,
        abbreviation: fund.abbreviation,
        description: fund.description,
        creation: false
      })))
    );

  priorities$ =
    combineLatest([
      this.newPageIndex$.pipe(startWith(Tables.DEFAULT_INITIAL_PAGE_INDEX)),
      this.newPageSize$.pipe(startWith(100)),
      this.newSort$.pipe(
        startWith({active: 'code', direction: 'asc'}),
        map(sort => sort?.direction ? sort : {active: 'code', direction: 'asc'}),
        map(sort => `${sort.active},${sort.direction}`)
      ),
    ])
      .pipe(
        flatMap(([pageIndex, pageSize, sort]) =>
          this.programmePriorityService.get(pageIndex, pageSize, sort)),
        tap(page => Log.info('Fetched the priorities:', this, page.content)),
      );

  constructor(private programmeDataService: ProgrammeDataService,
              private programmeFundService: ProgrammeFundService,
              private programmePriorityService: ProgrammePriorityService,
              private tabService: TabService) {
    super();
  }

  ngOnDestroy(): void {
    super.ngOnDestroy();
    this.tabService.cleanupTab(ProgrammePageComponent.name);
  }

  changeTab(tabIndex: number): void {
    this.tabService.changeTab(ProgrammePageComponent.name, tabIndex);
  }
}
