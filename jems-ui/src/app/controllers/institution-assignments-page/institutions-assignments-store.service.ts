import {Injectable} from '@angular/core';
import {BehaviorSubject, combineLatest, Observable, Subject} from 'rxjs';
import {
  ControllerInstitutionAssignmentDTO,
  ControllerInstitutionsApiService,
  InstitutionPartnerAssignmentDTO,
  PageInstitutionPartnerDetailsDTO
} from '@cat/api';
import {MatSort} from '@angular/material/sort';
import {map, startWith, switchMap, tap} from 'rxjs/operators';
import {Tables} from '@common/utils/tables';
import {Log} from '@common/utils/log';
import {UntilDestroy, untilDestroyed} from '@ngneat/until-destroy';
import {FormService} from '@common/components/section/form/form.service';

@Injectable()
@UntilDestroy()
export class InstitutionsAssignmentsStoreService {

  controllerInstitutionAssignmentPage$: Observable<PageInstitutionPartnerDetailsDTO>;
  newPageSize$ = new Subject<number>();
  newPageIndex$ = new Subject<number>();
  newSort$ = new Subject<Partial<MatSort>>();
  updatedControllerInstitutionPartnerAssignment$ = new Subject<InstitutionPartnerAssignmentDTO[]>();
  private controllerInstitutionUpdateEvent$ = new BehaviorSubject(null);
  private initialSort: Partial<MatSort> = {active: 'projectId', direction: 'asc'};

  constructor(private controllerInstitutionsApiService: ControllerInstitutionsApiService,
              private formService: FormService) {
    this.controllerInstitutionAssignmentPage$ = this.controllerInstitutionAssignmentPage();
  }

  private controllerInstitutionAssignmentPage(): Observable<PageInstitutionPartnerDetailsDTO> {
    return combineLatest([
      this.newPageIndex$.pipe(startWith(Tables.DEFAULT_INITIAL_PAGE_INDEX)),
      this.newPageSize$.pipe(startWith(Tables.DEFAULT_INITIAL_PAGE_SIZE)),
      this.newSort$.pipe(
        startWith(this.initialSort),
        map(sort => sort?.direction ? sort : this.initialSort)),
      this.updatedControllerInstitutionPartnerAssignment$.pipe(startWith(null))
    ])
      .pipe(
        switchMap(([pageIndex, pageSize, sort]) =>
          this.controllerInstitutionsApiService.getInstitutionPartnerAssignments(pageIndex, pageSize, `${sort.active},${sort.direction}`)),
        tap(page => Log.info('Fetched controllers institutions partners:', this, page.content)),
      );
  }

  updateControllerInstitutionAssignments(institutionPartnerAssignments: ControllerInstitutionAssignmentDTO) {
    return this.controllerInstitutionsApiService.assignInstitutionToPartner(institutionPartnerAssignments)
      .pipe(
        tap(updated => this.updatedControllerInstitutionPartnerAssignment$.next(updated)),
        tap(() => this.formService.setSuccess('controller.institutions.nuts.assignments.update.success')),
        tap(() => this.controllerInstitutionUpdateEvent$.next(null)),
        tap(updated => Log.info('Updated controller assignment:', this, updated)),
        untilDestroyed(this)
      ).subscribe();
  }

}
