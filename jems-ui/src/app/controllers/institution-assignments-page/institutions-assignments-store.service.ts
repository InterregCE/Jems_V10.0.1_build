import {Injectable} from '@angular/core';
import {BehaviorSubject, combineLatest, Observable, of, Subject} from 'rxjs';
import {
  ControllerInstitutionAssignmentDTO,
  ControllerInstitutionDTO,
  ControllerInstitutionsApiService,
  InstitutionPartnerAssignmentDTO,
  PageInstitutionPartnerDetailsDTO
} from '@cat/api';
import {MatSort} from '@angular/material/sort';
import {map, startWith, switchMap, tap} from 'rxjs/operators';
import {Tables} from '@common/utils/tables';
import {Log} from '@common/utils/log';
import {RoutingService} from '@common/services/routing.service';
import {UntilDestroy, untilDestroyed} from "@ngneat/until-destroy";

@Injectable()
@UntilDestroy()
export class InstitutionsAssignmentsStoreService {

  controllerInstitutionAssignmentPage$: Observable<PageInstitutionPartnerDetailsDTO>;
  newPageSize$ = new Subject<number>();
  newPageIndex$ = new Subject<number>();
  newSort$ = new Subject<Partial<MatSort>>();
  updatedControllerInstitutionPartnerAssignment = new Subject<InstitutionPartnerAssignmentDTO[]>();
  private controllerInstitutionUpdateEvent$ = new BehaviorSubject(null);
  private initialSort = `${Tables.DEFAULT_INITIAL_SORT.active},${Tables.DEFAULT_INITIAL_SORT.direction}`;

  constructor(private controllerInstitutionsApiService: ControllerInstitutionsApiService) {
    this.controllerInstitutionAssignmentPage$ = this.controllerInstitutionAssignmentPage();
  }

  private controllerInstitutionAssignmentPage(): Observable<PageInstitutionPartnerDetailsDTO> {
    return combineLatest([
      this.newPageIndex$.pipe(startWith(Tables.DEFAULT_INITIAL_PAGE_INDEX)),
      this.newPageSize$.pipe(startWith(Tables.DEFAULT_INITIAL_PAGE_SIZE)),
    ])
      .pipe(
        switchMap(([pageIndex, pageSize]) =>
          this.controllerInstitutionsApiService.getInstitutionPartnerAssignments(pageIndex, pageSize, this.initialSort)),
        tap(page => Log.info('Fetched controllers institutions partners:', this, page.content)),
      );
  }

  updateControllerInstitutionAssignments(institutionPartnerAssignments: ControllerInstitutionAssignmentDTO) {
    return this.controllerInstitutionsApiService.assignInstitutionToPartner(institutionPartnerAssignments)
      .pipe(
        tap(updated => this.updatedControllerInstitutionPartnerAssignment.next(updated)),
        tap(() => this.controllerInstitutionUpdateEvent$.next(null)),
        tap(created => Log.info('Updated controller assignment:', this, created)),
        untilDestroyed(this)
      ).subscribe();
  }

}
