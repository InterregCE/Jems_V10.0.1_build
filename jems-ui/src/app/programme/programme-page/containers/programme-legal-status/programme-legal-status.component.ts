import {ChangeDetectionStrategy, Component} from '@angular/core';
import {merge, Subject} from 'rxjs';
import {I18nValidationError} from '@common/validation/i18n-validation-error';
import {InputProgrammeLegalStatusWrapper, ProgrammeLegalStatusService} from '@cat/api';
import {catchError, flatMap, map, tap} from 'rxjs/operators';
import {Log} from '../../../../common/utils/log';
import {HttpErrorResponse} from '@angular/common/http';
import {ProgrammePageSidenavService} from '../../services/programme-page-sidenav.service';
import {BaseComponent} from '@common/components/base-component';

@Component({
  selector: 'app-programme-legal-status',
  templateUrl: './programme-legal-status.component.html',
  styleUrls: ['./programme-legal-status.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ProgrammeLegalStatusComponent extends BaseComponent {

  error$ = new Subject<I18nValidationError | null>();
  success$ = new Subject<string>();
  saveStatuses$ = new Subject<InputProgrammeLegalStatusWrapper>();
  deleteStatus$ = new Subject<number>();

  private initialStatus$ = this.legalStatusService.getProgrammeLegalStatusList()
    .pipe(
      tap(legalStatus => Log.info('Fetched programme legal status list:', this, legalStatus))
    );

  private savedStatus$ = this.saveStatuses$
    .pipe(
      flatMap(legalStatuses => this.legalStatusService.addProgrammeLegalStatuses(legalStatuses)),
      tap(saved => Log.info('Updated programme legal statuses:', this, saved)),
      tap(() => this.success$.next('programme.legal.status.save.success')),
      tap(() => this.error$.next(null)),
      catchError((error: HttpErrorResponse) => {
        this.error$.next(error.error);
        throw error;
      })
    );

  private deletedStatus$ = this.deleteStatus$
    .pipe(
      flatMap(statusId => this.legalStatusService._delete(statusId)),
      tap(deleted => Log.info('Deleted programme legal status:', this, deleted)),
      tap(() => this.success$.next('programme.legal.status.deleted.success')),
      tap(() => this.error$.next(null)),
      catchError((error: HttpErrorResponse) => {
        this.error$.next(error.error);
        throw error;
      })
    )

  legalStatuses$ = merge(this.initialStatus$, this.savedStatus$, this.deletedStatus$)
    .pipe(
      map(legalStatuses => legalStatuses.map(status => ({
        id: status.id,
        description: status.description,
      })))
    );


  constructor(private legalStatusService: ProgrammeLegalStatusService,
              private programmePageSidenavService: ProgrammePageSidenavService) {
    super();
    this.programmePageSidenavService.init(this.destroyed$);
  }
}
