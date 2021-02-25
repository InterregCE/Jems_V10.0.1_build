import {ChangeDetectionStrategy, Component} from '@angular/core';
import {BaseComponent} from '@common/components/base-component';
import {Permission} from '../../../../security/permissions/permission';
import {merge, Subject} from 'rxjs';
import {I18nValidationError} from '@common/validation/i18n-validation-error';
import {
  OutputProgrammeData,
  ProgrammeDataService,
  ProgrammeFundDTO,
  ProgrammeFundService,
  ProgrammePriorityService
} from '@cat/api';
import {catchError, mergeMap, map, tap} from 'rxjs/operators';
import {Log} from '../../../../common/utils/log';
import {HttpErrorResponse} from '@angular/common/http';
import {MatSort} from '@angular/material/sort';
import {ProgrammePageSidenavService} from '../../services/programme-page-sidenav.service';

@Component({
  selector: 'app-programme-page',
  templateUrl: './programme-page.component.html',
  styleUrls: ['./programme-page.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ProgrammePageComponent extends BaseComponent {
  Permission = Permission;

  programmeSaveError$ = new Subject<I18nValidationError | null>();
  programmeSaveSuccess$ = new Subject<boolean>();
  saveProgrammeData$ = new Subject<OutputProgrammeData>();

  fundsSaveError$ = new Subject<I18nValidationError | null>();
  fundsSaveSuccess$ = new Subject<boolean>();
  saveFunds$ = new Subject<ProgrammeFundDTO[]>();

  newPageSize$ = new Subject<number>();
  newPageIndex$ = new Subject<number>();
  newSort$ = new Subject<Partial<MatSort>>();

  private programmeById$ = this.programmeDataService.get()
    .pipe(
      tap(programmeData => Log.info('Fetched programme data:', this, programmeData))
    );

  private savedProgramme$ = this.saveProgrammeData$
    .pipe(
      mergeMap(programmeUpdate => this.programmeDataService.update(programmeUpdate)),
      tap(saved => Log.info('Updated programme:', this, saved)),
      tap(() => this.programmeSaveSuccess$.next(true)),
      tap(() => this.programmeSaveError$.next(null)),
      catchError((error: HttpErrorResponse) => {
        this.programmeSaveError$.next(error.error);
        throw error;
      })
    );

  programme$ = merge(this.programmeById$, this.savedProgramme$);

  private initialFunds$ = this.programmeFundService.getProgrammeFundList()
    .pipe(
      tap(funds => Log.info('Fetched programme funds:', this, funds))
    );

  private savedFunds$ = this.saveFunds$
    .pipe(
      mergeMap(funds => this.programmeFundService.updateProgrammeFundList(funds)),
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

  constructor(private programmeDataService: ProgrammeDataService,
              private programmeFundService: ProgrammeFundService,
              private programmePriorityService: ProgrammePriorityService,
              private programmePageSidenavService: ProgrammePageSidenavService) {
    super();
  }
}
