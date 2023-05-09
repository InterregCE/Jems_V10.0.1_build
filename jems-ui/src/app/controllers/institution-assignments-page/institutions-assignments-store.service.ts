import {Injectable} from '@angular/core';
import {BehaviorSubject, combineLatest, Observable, Subject} from 'rxjs';
import {
  ControllerInstitutionAssignmentDTO,
  ControllerInstitutionsApiService,
  InstitutionPartnerAssignmentDTO,
  InstitutionPartnerSearchRequestDTO,
  OutputNuts,
  PageInstitutionPartnerDetailsDTO
} from '@cat/api';
import {MatSort} from '@angular/material/sort';
import {map, shareReplay, startWith, switchMap, tap} from 'rxjs/operators';
import {Tables} from '@common/utils/tables';
import {Log} from '@common/utils/log';
import {UntilDestroy, untilDestroyed} from '@ngneat/until-destroy';
import {FormService} from '@common/components/section/form/form.service';
import {InstitutionsPageStore} from '../institutions-page/institutions-page-store.service';

@Injectable()
@UntilDestroy()
export class InstitutionsAssignmentsStoreService {

  controllerInstitutionAssignmentPage$: Observable<PageInstitutionPartnerDetailsDTO>;
  newPageSize$ = new Subject<number>();
  newPageIndex$ = new Subject<number>();
  newSort$ = new Subject<Partial<MatSort>>();
  updatedControllerInstitutionPartnerAssignment$ = new Subject<InstitutionPartnerAssignmentDTO[]>();
  private controllerInstitutionUpdateEvent$ = new BehaviorSubject(null);
  private initialSort: Partial<MatSort> = {active: 'partner.project.id', direction: 'asc'};
  filter$ = new BehaviorSubject<InstitutionPartnerSearchRequestDTO>(null as any);
  nutsDefinedForCurrentUser$: Observable<OutputNuts[]>;

  constructor(private controllerInstitutionsApiService: ControllerInstitutionsApiService,
              private formService: FormService,
              private controllerInstitutionStore: InstitutionsPageStore
) {
    this.nutsDefinedForCurrentUser$ = this.controllerInstitutionStore.getNutsDefinedForCurrentUser().pipe(shareReplay(1));
    this.controllerInstitutionAssignmentPage$ = this.controllerInstitutionAssignmentPage();
  }

  private controllerInstitutionAssignmentPage(): Observable<PageInstitutionPartnerDetailsDTO> {
    return combineLatest([
      this.filter$,
      this.newPageIndex$.pipe(startWith(Tables.DEFAULT_INITIAL_PAGE_INDEX)),
      this.newPageSize$.pipe(startWith(Tables.DEFAULT_INITIAL_PAGE_SIZE)),
      this.newSort$.pipe(
        startWith(this.initialSort),
        map(sort => sort?.direction ? sort : this.initialSort)),
      this.updatedControllerInstitutionPartnerAssignment$.pipe(startWith(null))
    ])
      .pipe(
        switchMap(([filter, pageIndex, pageSize, sort]: any) =>
          this.controllerInstitutionsApiService.getInstitutionPartnerAssignments(this.setupFilter(filter), pageIndex, pageSize, `${sort.active},${sort.direction}`)),
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

  private setupFilter(filter: InstitutionPartnerSearchRequestDTO): InstitutionPartnerSearchRequestDTO {
      if (filter == null)
        {
          return {
          callId: NaN,
          projectId: '',
          acronym: '',
          partnerName: '',
          partnerNuts: [] = []
          } as InstitutionPartnerSearchRequestDTO;
        }

    return filter;
  }
}
