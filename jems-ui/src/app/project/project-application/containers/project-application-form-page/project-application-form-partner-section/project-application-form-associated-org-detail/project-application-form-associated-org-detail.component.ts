import {
  ChangeDetectionStrategy,
  Component,
  OnDestroy,
  OnInit,
} from '@angular/core';
import {combineLatest, Subject} from 'rxjs';
import {NutsStoreService} from '../../../../../../common/services/nuts-store.service';
import {distinctUntilChanged, map, startWith, takeUntil, tap} from 'rxjs/operators';
import {ActivatedRoute, Router} from '@angular/router';
import {
  ProjectPartnerService,
  OutputProjectAssociatedOrganizationDetail,
  OutputProjectAssociatedOrganizationAddress,
} from '@cat/api';
import {ProjectApplicationFormSidenavService} from '../../services/project-application-form-sidenav.service';
import {BaseComponent} from '@common/components/base-component';
import {ProjectAssociatedOrganizationStore} from '../../services/project-associated-organization-store.service';
import {ProjectStore} from '../../../project-application-detail/services/project-store.service';

@Component({
  selector: 'app-project-application-form-associated-org-detail',
  templateUrl: './project-application-form-associated-org-detail.component.html',
  styleUrls: ['./project-application-form-associated-org-detail.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ProjectApplicationFormAssociatedOrgDetailComponent extends BaseComponent implements OnInit, OnDestroy {

  projectId = this.activatedRoute?.snapshot?.params?.projectId
  associatedOrganizationId = this.activatedRoute?.snapshot?.params?.associatedOrganizationId;

  mainCountryChanged$ = new Subject<string>();
  mainRegion2Changed$ = new Subject<any[]>();

  mainAddress$ = combineLatest([
    this.associatedOrganizationStore.getProjectAssociatedOrganization(),
    this.nutsStore.getNuts(),
    this.mainCountryChanged$.pipe(startWith(null)),
    this.mainRegion2Changed$.pipe(startWith(null))
  ])
    .pipe(
      map(([organization, nuts, changedCountry, changedRegion2]) => ({
        organization,
        country: nuts,
        region2: this.getRegion2(organization, nuts, changedCountry),
        region3: this.getRegion3(organization, nuts, changedRegion2),
      }))
    );

  details$ = combineLatest([
    this.associatedOrganizationStore.getProjectAssociatedOrganization(),
    this.mainAddress$,
    this.partnerService.getProjectPartnersForDropdown(this.projectId, ['sortNumber,asc'])
  ]).pipe(
    takeUntil(this.destroyed$),
    map(([organization, main, partners]) => ({organization, main, partners})),
  )

  constructor(public associatedOrganizationStore: ProjectAssociatedOrganizationStore,
              private partnerService: ProjectPartnerService,
              private projectStore: ProjectStore,
              private nutsStore: NutsStoreService,
              private projectApplicationFormSidenavService: ProjectApplicationFormSidenavService,
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
    this.projectStore.init(this.projectId);
    this.projectApplicationFormSidenavService.init(this.destroyed$, this.projectId);

    if (this.associatedOrganizationId) {
      return;
    }
    // creating a new associated organization
    this.associatedOrganizationStore.init(null);
  }

  redirectToAssociatedOrganizationOverview(): void {
    this.router.navigate(['app', 'project', 'detail', this.projectId, 'applicationForm']);
  }

  ngOnDestroy(): void {
    super.ngOnDestroy();
  }

  private getPartnerCountry(nuts: any, address?: OutputProjectAssociatedOrganizationAddress) {
    if (!address?.country) {
      return null;
    }
    return nuts[address.country];
  }

  private getRegion2(partner: OutputProjectAssociatedOrganizationDetail,
                     nuts: any,
                     changedCountry: string | null): any {
    const country = changedCountry || this.getPartnerCountry(nuts, partner.address);
    if (!country) return [];

    const newNuts = new Map<string, any>();
    Object.values(country).forEach((nut: any) => {
      Object.keys(nut).forEach(secondLayerNut => {
        newNuts.set(secondLayerNut, nut[secondLayerNut]);
      })
    })
    return newNuts;
  }

  private getRegion3(partner: OutputProjectAssociatedOrganizationDetail,
                     nuts: any,
                     changedRegion2: any[] | null): any {
    if (changedRegion2) {
      return changedRegion2.map(region => region.title);
    }

    const address = partner.address;
    const country = this.getPartnerCountry(nuts, address);
    if (!country) return [];

    const newNuts = new Map<string, any>();
    Object.values(country).forEach((nut: any) => {
      Object.keys(nut).forEach(secondLayerNut => {
        newNuts.set(secondLayerNut, nut[secondLayerNut]);
      })
    })

    if (address?.nutsRegion2)
      return newNuts
        .get(address.nutsRegion2)
        .map((region: any) => region.title);
  }
}
