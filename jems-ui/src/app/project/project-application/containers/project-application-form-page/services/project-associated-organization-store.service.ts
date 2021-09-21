import {Injectable} from '@angular/core';
import {
  InputProjectAssociatedOrganization,
  OutputNuts,
  OutputProjectAssociatedOrganizationDetail,
  ProjectPartnerSummaryDTO,
  ProjectAssociatedOrganizationService,
} from '@cat/api';
import {combineLatest, merge, Observable, of, Subject} from 'rxjs';
import {catchError, switchMap, tap} from 'rxjs/operators';
import {Log} from '@common/utils/log';
import {ProjectStore} from '../../project-application-detail/services/project-store.service';
import {ProjectVersionStore} from '@project/common/services/project-version-store.service';
import {ProjectPartnerStore} from './project-partner-store.service';
import {RoutingService} from '@common/services/routing.service';
import {NutsStore} from '@common/services/nuts.store';
import {ProjectPaths} from '@project/common/project-util';

@Injectable()
export class ProjectAssociatedOrganizationStore {
  public static ORGANIZATION_DETAIL_PATH = '/applicationFormAssociatedOrganization/detail/';

  associatedOrganization$: Observable<OutputProjectAssociatedOrganizationDetail | {}>;
  nuts$: Observable<OutputNuts[]>;
  dropdownPartners$: Observable<ProjectPartnerSummaryDTO[]>;
  organizationEditable$: Observable<boolean>;
  projectTitle$: Observable<string>;

  private savedAssociatedOrganization$ = new Subject<OutputProjectAssociatedOrganizationDetail>();
  private projectId: number;

  constructor(private associatedOrganizationService: ProjectAssociatedOrganizationService,
              private projectStore: ProjectStore,
              private projectVersionStore: ProjectVersionStore,
              private partnerStore: ProjectPartnerStore,
              private router: RoutingService,
              private nutsStore: NutsStore) {
    this.associatedOrganization$ = this.associatedOrganization();
    this.nuts$ = this.nutsStore.getNuts();
    this.dropdownPartners$ = this.partnerStore.partnerSummaries$;
    this.organizationEditable$ = this.projectStore.projectEditable$;
    this.projectTitle$ = this.projectStore.projectTitle$;
  }

  createAssociatedOrganization(create: InputProjectAssociatedOrganization): Observable<void> {
    return this.associatedOrganizationService.createAssociatedOrganization(this.projectId, create)
      .pipe(
        tap(saved => this.savedAssociatedOrganization$.next(saved)),
        tap(saved => Log.info('Created associatedOrganization:', this, saved)),
        tap((created: any) => this.router.navigate([
          'app', 'project', 'detail', this.projectId, 'applicationFormAssociatedOrganization', 'detail', created.id
        ])),
      );
  }

  updateAssociatedOrganization(update: InputProjectAssociatedOrganization): Observable<OutputProjectAssociatedOrganizationDetail> {
    return this.associatedOrganizationService.updateAssociatedOrganization(this.projectId, update)
      .pipe(
        tap(saved => this.savedAssociatedOrganization$.next(saved)),
        tap(saved => Log.info('Updated associatedOrganization:', this, saved))
      );
  }

  private associatedOrganization(): Observable<OutputProjectAssociatedOrganizationDetail | {}> {
    const organizationById$ = combineLatest([
      this.router.routeParameterChanges(ProjectAssociatedOrganizationStore.ORGANIZATION_DETAIL_PATH, 'associatedOrganizationId'),
      this.projectStore.projectId$,
      this.projectVersionStore.currentRouteVersion$
    ]).pipe(
      tap(([organizationId, projectId]) => {
        this.projectId = Number(projectId);
      }),
      switchMap(([organizationId, projectId, version]) => organizationId
        ? this.associatedOrganizationService.getAssociatedOrganizationById(organizationId as number, projectId, version)
          .pipe(
            catchError(() => {
              this.router.navigate([ProjectPaths.PROJECT_DETAIL_PATH, projectId]);
              return of({});
            })
          )
        : of({})
      ),
      tap(partner => Log.info('Fetched project associatedOrganization:', this, partner)),
    );

    return merge(organizationById$, this.savedAssociatedOrganization$);
  }
}
