import {Injectable} from '@angular/core';
import {BehaviorSubject, combineLatest, merge, Observable, of, Subject} from 'rxjs';
import {
  ControllerInstitutionDTO,
  ControllerInstitutionsApiService,
  PageControllerInstitutionListDTO,
  UpdateControllerInstitutionDTO
} from '@cat/api';
import {MatSort} from '@angular/material/sort';
import {catchError, map, startWith, switchMap, tap} from 'rxjs/operators';
import {Tables} from '@common/utils/tables';
import {Log} from '@common/utils/log';
import {RoutingService} from '@common/services/routing.service';

@Injectable()
export class InstitutionsPageStore {
  public static CONTROLLERS_DETAIL_PATH = '/app/controller/';
  controllerInstitutionPage$: Observable<PageControllerInstitutionListDTO>;
  controllerInstitutionDetail$: Observable<ControllerInstitutionDTO>;

  newPageSize$ = new Subject<number>();
  newPageIndex$ = new Subject<number>();
  newSort$ = new Subject<Partial<MatSort>>();
  updatedControllerInstitution = new Subject<ControllerInstitutionDTO>();
  private controllerInstitutionUpdateEvent$ = new BehaviorSubject(null);

  constructor(private controllerInstitutionsApiService: ControllerInstitutionsApiService,
              private router: RoutingService) {
    this.controllerInstitutionPage$ = this.controllerInstitutionPage();
    this.controllerInstitutionDetail$ = this.getControllerInstitutionById();
  }

  private controllerInstitutionPage(): Observable<PageControllerInstitutionListDTO> {
    return combineLatest([
      this.newPageIndex$.pipe(startWith(Tables.DEFAULT_INITIAL_PAGE_INDEX)),
      this.newPageSize$.pipe(startWith(Tables.DEFAULT_INITIAL_PAGE_SIZE)),
      this.newSort$.pipe(
        startWith(Tables.DEFAULT_INITIAL_SORT),
        map(sort => sort?.direction ? sort : Tables.DEFAULT_INITIAL_SORT),
        map(sort => `${sort.active},${sort.direction}`)
      )
    ])
      .pipe(
        switchMap(([pageIndex, pageSize, sort]) =>
          this.controllerInstitutionsApiService.getControllers(pageIndex, pageSize, sort)),
        tap(page => Log.info('Fetched controllers:', this, page.content)),
      );
  }

  private getControllerInstitutionById(): Observable<ControllerInstitutionDTO> {
    const initialControllerInstitution = this.router.routeParameterChanges(InstitutionsPageStore.CONTROLLERS_DETAIL_PATH, 'controllerInstitutionId')
      .pipe(
        switchMap(controllerInstitutionId => controllerInstitutionId ? this.controllerInstitutionsApiService.getControllerInstitutionById(Number(controllerInstitutionId))
          .pipe(
            catchError(() => {
              this.router.navigate(['app/controller']);
              return of({} as ControllerInstitutionDTO);
            })
          )
          : of({} as ControllerInstitutionDTO) ),
        tap(controllerInstitution => Log.info('Fetched controller institution:', this, controllerInstitution)),
      );

    return merge(initialControllerInstitution, this.updatedControllerInstitution);
  }

  createController(controllerInstitution: UpdateControllerInstitutionDTO): Observable<ControllerInstitutionDTO> {
    return this.controllerInstitutionsApiService.createController(controllerInstitution)
      .pipe(
        tap(created => this.updatedControllerInstitution.next(created)),
        tap(() => this.controllerInstitutionUpdateEvent$.next(null)),
        tap(created => Log.info('Created controller:', this, created))
      );
  }

  updateController(institutionId: number, controllerInstitution: UpdateControllerInstitutionDTO): Observable<ControllerInstitutionDTO> {
    return this.controllerInstitutionsApiService.updateController(institutionId, controllerInstitution)
      .pipe(
        tap(updated => this.updatedControllerInstitution.next(updated)),
        tap(() => this.controllerInstitutionUpdateEvent$.next(null)),
        tap(created => Log.info('Updated controller:', this, created))
      );
  }

}
