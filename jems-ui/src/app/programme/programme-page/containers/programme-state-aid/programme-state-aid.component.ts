import {ChangeDetectionStrategy, Component} from '@angular/core';
import {BaseComponent} from '@common/components/base-component';
import {merge, Subject} from 'rxjs';
import {I18nValidationError} from '@common/validation/i18n-validation-error';
import {
  ProgrammeStateAidService,
  ProgrammeStateAidUpdateDTO
} from '@cat/api';
import {catchError, mergeMap, tap} from 'rxjs/operators';
import {Log} from '@common/utils/log';
import {HttpErrorResponse} from '@angular/common/http';
import {ProgrammePageSidenavService} from '../../services/programme-page-sidenav.service';

@Component({
  selector: 'app-programme-state-aid',
  templateUrl: './programme-state-aid.component.html',
  styleUrls: ['./programme-state-aid.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ProgrammeStateAidComponent extends BaseComponent {

  error$ = new Subject<I18nValidationError | null>();
  success$ = new Subject<string>();
  saveStateAids$ = new Subject<ProgrammeStateAidUpdateDTO>();

  private initialStateAids$ = this.stateAidService.getProgrammeStateAidList()
    .pipe(
      tap(stateAid => Log.info('Fetched programme state aid list:', this, stateAid))
    );

  private savedStateAids$ = this.saveStateAids$
    .pipe(
      mergeMap(stateAid => this.stateAidService.updateProgrammeStateAids(stateAid)),
      tap(saved => Log.info('Updated programme state aids:', this, saved)),
      tap(() => this.success$.next('programme.state.aid.save.success')),
      tap(() => this.error$.next(null)),
      catchError((error: HttpErrorResponse) => {
        this.error$.next(error.error);
        throw error;
      })
    );

  stateAids$ = merge(this.initialStateAids$, this.savedStateAids$);

  constructor(private stateAidService: ProgrammeStateAidService,
              private programmePageSidenavService: ProgrammePageSidenavService) {
    super();
  }
}
