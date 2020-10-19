import {Injectable} from '@angular/core';
import {
  InputProjectPartnerCreate,
  InputProjectPartnerUpdate,
  OutputProjectPartnerDetail,
  OutputProjectStatus,
  ProjectPartnerService,
  InputProjectPartnerAddress
} from '@cat/api';
import {combineLatest, merge, Observable, of, ReplaySubject, Subject} from 'rxjs';
import {
  catchError,
  distinctUntilChanged,
  map,
  mergeMap,
  shareReplay,
  switchMap,
  tap,
  withLatestFrom
} from 'rxjs/operators';
import {Log} from '../../../../../common/utils/log';
import {ProjectStore} from '../../project-application-detail/services/project-store.service';
import {HttpErrorResponse} from '@angular/common/http';
import {I18nValidationError} from '@common/validation/i18n-validation-error';
import {ProjectApplicationFormSidenavService} from './project-application-form-sidenav.service';
import {Router} from '@angular/router';

@Injectable()
export class ProjectPartnerStore {

  private partnerId$ = new ReplaySubject<number | null>(1);
  private projectId$ = this.projectStore.getProject()
    .pipe(
      map(project => project.id),
      shareReplay(1)
    )

  savePartner$ = new Subject<InputProjectPartnerUpdate>();
  createPartner$ = new Subject<InputProjectPartnerCreate>();
  partnerSaveSuccess$ = new Subject<boolean>();
  partnerSaveError$ = new Subject<I18nValidationError | null>();

  savePartnerAddresses$ = new Subject<InputProjectPartnerAddress[]>();

  private partnerById$ = combineLatest([this.partnerId$, this.projectId$])
    .pipe(
      distinctUntilChanged(),
      mergeMap(([partnerId, projectId]) => partnerId
        ? this.partnerService.getProjectPartnerById(partnerId, projectId)
        : of({})
      ),
      tap(projectPartner => Log.info('Fetched project partner:', this, projectPartner)),
    );


  private savedPartner$ = this.savePartner$
    .pipe(
      withLatestFrom(this.projectId$),
      switchMap(([partnerUpdate, projectId]) =>
        this.partnerService.updateProjectPartner(projectId, partnerUpdate)
          .pipe(
            catchError((error: HttpErrorResponse) => {
              this.partnerSaveError$.next(error.error);
              return of();
            })
          )
      ),
      tap(() => this.partnerSaveError$.next(null)),
      tap(() => this.partnerSaveSuccess$.next(true)),
      tap(saved => Log.info('Updated partner:', this, saved))
    );

  private createdPartner$ = this.createPartner$
    .pipe(
      withLatestFrom(this.projectId$),
      switchMap(([partnerCreate, projectId]) =>
        this.partnerService.createProjectPartner(projectId, partnerCreate)
          .pipe(
            map(created => ({projectId, partner: created})),
            catchError((error: HttpErrorResponse) => {
              this.partnerSaveError$.next(error.error);
              return of();
            })
          )
      ),
      tap(saved => Log.info('Created partner:', this, saved)),
      tap((created: any) => this.router.navigate([
        'app', 'project', 'detail', created.projectId, 'applicationForm', 'partner', 'detail', created.partner.id
      ])),
    );

  private updatedPartnerAddresses$ = this.savePartnerAddresses$
    .pipe(
      withLatestFrom(this.partnerId$, this.projectId$),
      mergeMap(([addresses, partnerId, projectId]) =>
        this.partnerService.updateProjectPartnerAddress(partnerId as any, projectId, addresses)
      ),
      tap(() => this.partnerSaveError$.next(null)),
      tap(() => this.partnerSaveSuccess$.next(true)),
      tap(saved => Log.info('Updated partner addresses:', this, saved)),
      catchError((error: HttpErrorResponse) => {
        this.partnerSaveError$.next(error.error);
        return of();
      })
    );

  projectEditable$ = this.projectStore.getProject()
    .pipe(
      tap(project => this.projectApplicationFormSidenavService.setAcronym(project.acronym)),
      map(project => project.projectStatus.status === OutputProjectStatus.StatusEnum.DRAFT
        || project.projectStatus.status === OutputProjectStatus.StatusEnum.RETURNEDTOAPPLICANT
      ),
      shareReplay(1)
    );

  private partner$ = merge(
    this.partnerById$,
    this.savedPartner$,
    this.createdPartner$,
    this.updatedPartnerAddresses$
  )
    .pipe(
      shareReplay(1)
    )

  constructor(private partnerService: ProjectPartnerService,
              private projectStore: ProjectStore,
              private router: Router,
              private projectApplicationFormSidenavService: ProjectApplicationFormSidenavService) {
  }

  init(partnerId: number | string | null) {
    this.partnerId$.next(Number(partnerId));
  }

  getProjectPartner(): Observable<OutputProjectPartnerDetail | any> {
    return this.partner$;
  }
}
