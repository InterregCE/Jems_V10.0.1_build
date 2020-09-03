import {ChangeDetectionStrategy, Component, OnInit} from '@angular/core';
import {combineLatest, merge, of, Subject} from 'rxjs';
import {I18nValidationError} from '@common/validation/i18n-validation-error';
import {HttpErrorResponse} from '@angular/common/http';
import {catchError, map, switchMap, tap} from 'rxjs/operators';
import {Log} from '../../../../../../common/utils/log';
import {ActivatedRoute, Router} from '@angular/router';
import {
  InputProjectPartnerCreate,
  InputProjectPartnerUpdate,
  OutputProjectStatus,
  ProjectPartnerService
} from '@cat/api';
import {BaseComponent} from '@common/components/base-component';
import {ProjectStore} from '../../../project-application-detail/services/project-store.service';
import {ProjectApplicationFormSidenavService} from '../../services/project-application-form-sidenav.service';

@Component({
  selector: 'app-project-application-form-partner-detail',
  templateUrl: './project-application-form-partner-detail.component.html',
  styleUrls: ['./project-application-form-partner-detail.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ProjectApplicationFormPartnerDetailComponent extends BaseComponent implements OnInit {

  projectId = this.activatedRoute?.snapshot?.params?.projectId
  partnerId = this.activatedRoute?.snapshot?.params?.partnerId;
  partnerSaveSuccess$ = new Subject<boolean>()
  partnerSaveError$ = new Subject<I18nValidationError | null>();
  savePartner$ = new Subject<InputProjectPartnerUpdate>();
  createPartner$ = new Subject<InputProjectPartnerCreate>();

  private partnerById$ = this.partnerId
    ? this.partnerService.getProjectPartnerById(this.partnerId, this.projectId)
      .pipe(
        tap(partner => Log.info('Fetched partner:', this, partner))
      )
    : of({});

  private savedPartner$ = this.savePartner$
    .pipe(
      switchMap(partnerUpdate =>
        this.partnerService.updateProjectPartner(this.projectId, partnerUpdate)
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
      switchMap(partnerCreate =>
        this.partnerService.createProjectPartner(this.projectId, partnerCreate)
          .pipe(
            catchError((error: HttpErrorResponse) => {
              this.partnerSaveError$.next(error.error);
              return of();
            })
          )
      ),
      tap(() => this.partnerSaveError$.next(null)),
      tap(() => this.partnerSaveSuccess$.next(true)),
      tap(saved => Log.info('Created partner:', this, saved)),
      tap(() => this.projectApplicationFormSidenavService.refreshPartners()),
    );

  public partner$ = merge(this.partnerById$, this.savedPartner$, this.createdPartner$);

  details$ = combineLatest([
    this.partner$,
    this.projectStore.getProject()
  ])
    .pipe(
      tap(([partner, project]) => this.projectApplicationFormSidenavService.setAcronym(project.acronym)),
      map(
        ([partner, project]) => ({
          partner,
          editable: project.projectStatus.status === OutputProjectStatus.StatusEnum.DRAFT
            || project.projectStatus.status === OutputProjectStatus.StatusEnum.RETURNEDTOAPPLICANT
        })
      )
    );

  constructor(private partnerService: ProjectPartnerService,
              private activatedRoute: ActivatedRoute,
              private projectStore: ProjectStore,
              private router: Router,
              private projectApplicationFormSidenavService: ProjectApplicationFormSidenavService) {
    super();
  }

  ngOnInit(): void {
    this.projectStore.init(this.projectId);
    this.projectApplicationFormSidenavService.init(this.destroyed$, this.projectId);
  }

  redirectToPartnerOverview(): void {
    this.router.navigate(['/project/' + this.projectId + '/applicationForm']);
  }
}
