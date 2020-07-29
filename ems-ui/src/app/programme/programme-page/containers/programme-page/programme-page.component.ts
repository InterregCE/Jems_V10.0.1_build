import {ChangeDetectionStrategy, Component} from '@angular/core';
import {BaseComponent} from '@common/components/base-component';
import {Permission} from '../../../../security/permissions/permission';
import {combineLatest, merge, Observable, Subject} from 'rxjs';
import {I18nValidationError} from '@common/validation/i18n-validation-error';
import {InputProgrammeData, ProgrammeDataService} from '@cat/api';
import {catchError, flatMap, map, tap} from 'rxjs/operators';
import {Log} from '../../../../common/utils/log';
import {HttpErrorResponse} from '@angular/common/http';
import {ProgrammeNavigationStateManagementService} from '../../services/programme-navigation-state-management.service';

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
  saveProgrammeData$ = new Subject<InputProgrammeData>();
  activeTab$: Observable<number> = this.programmeNavigationStateManagementService.getTab();

  private programme$ = this.programmeDataService.get()
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

  details$ = combineLatest([
    merge(this.programme$, this.savedProgramme$)
  ])
    .pipe(
      map(([programme]) => ({programme}))
    );

  constructor(private programmeDataService: ProgrammeDataService,
              private programmeNavigationStateManagementService: ProgrammeNavigationStateManagementService) {
    super();
  }
}
