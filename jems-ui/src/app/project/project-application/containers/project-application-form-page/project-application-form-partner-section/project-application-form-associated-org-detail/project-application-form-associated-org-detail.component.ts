import {ChangeDetectionStrategy, Component, OnInit} from '@angular/core';
import {combineLatest} from 'rxjs';
import {NutsStore} from '../../../../../../common/services/nuts.store';
import {distinctUntilChanged, map, switchMap, takeUntil, tap} from 'rxjs/operators';
import {ActivatedRoute, Router} from '@angular/router';
import {ProjectPartnerService} from '@cat/api';
import {BaseComponent} from '@common/components/base-component';
import {ProjectAssociatedOrganizationStore} from '../../services/project-associated-organization-store.service';
import {ProjectStore} from '../../../project-application-detail/services/project-store.service';
import {ProjectVersionStore} from '../../../../../services/project-version-store.service';

@Component({
  selector: 'app-project-application-form-associated-org-detail',
  templateUrl: './project-application-form-associated-org-detail.component.html',
  styleUrls: ['./project-application-form-associated-org-detail.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ProjectApplicationFormAssociatedOrgDetailComponent extends BaseComponent implements OnInit {

  projectId = this.activatedRoute?.snapshot?.params?.projectId;
  associatedOrganizationId = this.activatedRoute?.snapshot?.params?.associatedOrganizationId;

  details$ = combineLatest([
    this.associatedOrganizationStore.getProjectAssociatedOrganization(),
    this.nutsStore.getNuts(),
    this.projectVersionStore.currentRouteVersion$
      .pipe(
        switchMap(version => this.partnerService.getProjectPartnersForDropdown(this.projectId, undefined, version))
      )
  ])
    .pipe(
      map(([organization, nuts, partners]) => ({organization, nuts, partners}))
    );

  constructor(public associatedOrganizationStore: ProjectAssociatedOrganizationStore,
              public projectStore: ProjectStore,
              private projectVersionStore: ProjectVersionStore,
              private partnerService: ProjectPartnerService,
              private nutsStore: NutsStore,
              private activatedRoute: ActivatedRoute,
              private router: Router) {
    super();
    this.activatedRoute.params.pipe(
      takeUntil(this.destroyed$),
      map(params => params.associatedOrganizationId),
      distinctUntilChanged(),
      tap(id => this.associatedOrganizationStore.init(id)),
    ).subscribe();
  }

  ngOnInit(): void {
    if (this.associatedOrganizationId) {
      return;
    }
    // creating a new associated organization
    this.associatedOrganizationStore.init(null);
  }

  redirectToAssociatedOrganizationOverview(): void {
    this.router.navigate(['app', 'project', 'detail', this.projectId, 'applicationFormAssociatedOrganization']);
  }
}
