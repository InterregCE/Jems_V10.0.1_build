import {
  ChangeDetectionStrategy,
  ChangeDetectorRef,
  Component,
  EventEmitter,
  Input, OnDestroy,
  OnInit,
  Output
} from '@angular/core';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {AbstractForm} from '@common/components/forms/abstract-form';
import {of, Subject} from 'rxjs';
import {NutsStoreService} from '../../../../../../common/services/nuts-store.service';
import {catchError, flatMap, switchMap, take, tap} from 'rxjs/operators';
import {ActivatedRoute, Router} from '@angular/router';
import {
  InputProjectAssociatedOrganizationAddressDetails,
  ProjectAssociatedOrganizationService,
  OutputProjectAssociatedOrganizationDetail,
  InputProjectPartnerOrganizationDetails,
  OutputProjectAssociatedOrganization,
  InputProjectAssociatedOrganizationCreate
} from '@cat/api';
import {Log} from '../../../../../../common/utils/log';
import {HttpErrorResponse} from '@angular/common/http';
import {I18nValidationError} from '@common/validation/i18n-validation-error';
import {ProjectApplicationFormSidenavService} from '../../services/project-application-form-sidenav.service';
import {BaseComponent} from '@common/components/base-component';

@Component({
  selector: 'app-project-application-form-associated-org-detail',
  templateUrl: './project-application-form-associated-org-detail.component.html',
  styleUrls: ['./project-application-form-associated-org-detail.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ProjectApplicationFormAssociatedOrgDetailComponent extends BaseComponent implements OnInit, OnDestroy {

  projectId = this.activatedRoute?.snapshot?.params?.projectId
  associatedOrganizationId = this.activatedRoute?.snapshot?.params?.associatedOrganizationId;

  associatedOrganization:  OutputProjectAssociatedOrganization;
  partnerSaveSuccess$ = new Subject<boolean>();
  partnerSaveError$ = new Subject<I18nValidationError | null>();
  saveAssociatedOrganization$ = new Subject<OutputProjectAssociatedOrganization>();
  createPartner$ = new Subject<OutputProjectAssociatedOrganization>();

  nuts$ = of();
  nuts2$ = new Subject<any>();
  nuts3$ = new Subject<any[]>();

  partner$ = new Subject<any[]>();

  // private associatedOrganizationById$ = this.associatedOrganizationId
  //   ? this.projectAssociatedOrganizationService.getAssociatedOrganizationById(this.associatedOrganizationId, this.projectId)
  //     .pipe(
  //       tap(partner => Log.info('Fetched associated organization:', this, partner))
  //     )
  //   : of({});
  //
  // private assocOrg = this.projectAssociatedOrganizationService.getAssociatedOrganizationById(this.associatedOrganizationId, this.projectId)
  //     .pipe(
  //       take(1),
  //       tap(partner => this.associatedOrganization = partner),
  //       tap(partner => Log.info('Fetched associated organization:', this, partner))
  //     );
  //

  // private savedPartner$ = this.savePartner$
  //   .pipe(
  //     switchMap(partnerUpdate =>
  //       this.partnerService.updateProjectPartner(this.projectId, partnerUpdate)
  //         .pipe(
  //           catchError((error: HttpErrorResponse) => {
  //             this.partnerSaveError$.next(error.error);
  //             return of();
  //           })
  //         )
  //     ),
  //     tap(() => this.partnerSaveError$.next(null)),
  //     tap(() => this.partnerSaveSuccess$.next(true)),
  //     tap(saved => Log.info('Updated partner:', this, saved))
  //   );

  constructor(private nutsStore: NutsStoreService,
              private activatedRoute: ActivatedRoute,
              private projectAssociatedOrganizationService: ProjectAssociatedOrganizationService,
              private projectApplicationFormSidenavService: ProjectApplicationFormSidenavService,
              private router: Router) {
    super();
  }

  ngOnInit(): void {
    this.nuts$ = this.nutsStore.getNuts()
      .pipe(
        tap(nuts => {
          const associatedOrganizationAddress = this.associatedOrganization?.organizationAddress;
          if (!associatedOrganizationAddress?.country) {
            this.nuts2$.next([])
            this.nuts3$.next([])
            return;
          }
          const country = nuts[associatedOrganizationAddress.country];
          if (!country) return;

          const newNuts = new Map<string, any>();
          Object.values(country).forEach((nut: any) => {
            Object.keys(nut).forEach(secondLayerNut => {
              newNuts.set(secondLayerNut, nut[secondLayerNut]);
            })
          })
          this.nuts2$.next(newNuts);

          if (associatedOrganizationAddress.nutsRegion2)
            this.nuts3$.next(newNuts
              .get(associatedOrganizationAddress.nutsRegion2)
              .map((region: any) => region.title));
        })
      );

    this.projectApplicationFormSidenavService.init(this.destroyed$, this.projectId);
  }

  changeCountry(country: any) {
    const newNuts = new Map<string, any>();
    Object.values(country).forEach((nut: any) => {
      Object.keys(nut).forEach(secondLayerNut => {
        newNuts.set(secondLayerNut, nut[secondLayerNut]);
      })

    })
    this.nuts2$.next(newNuts)
    this.nuts3$.next([])
  }

  changeRegion( regions: any[]) {
    this.nuts3$.next(regions.map( region => region.title));
  }

  changePartner(partner : any) {
    this.partner$.next(partner)
  }

  redirectToAOOverview(): void {
    this.router.navigate(['app', 'project', 'detail', this.projectId, 'applicationForm']);
  }

  ngOnDestroy(): void {
    super.ngOnDestroy();
  }
}
