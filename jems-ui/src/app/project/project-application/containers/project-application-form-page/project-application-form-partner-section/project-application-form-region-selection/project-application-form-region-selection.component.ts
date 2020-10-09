import {
  ChangeDetectionStrategy,
  Component,
  EventEmitter,
  Input,
  OnChanges,
  OnInit,
  Output,
  SimpleChanges
} from '@angular/core';
import {NutsStoreService} from '../../../../../../common/services/nuts-store.service';
import {Observable, of, Subject} from 'rxjs';
import {I18nValidationError} from '@common/validation/i18n-validation-error';
import {InputProjectPartnerOrganizationDetails, OutputProjectPartnerOrganizationDetails} from '@cat/api';
import {tap} from 'rxjs/operators';

@Component({
  selector: 'app-project-application-form-region-selection',
  templateUrl: './project-application-form-region-selection.component.html',
  styleUrls: ['./project-application-form-region-selection.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ProjectApplicationFormRegionSelectionComponent implements OnInit, OnChanges{

  @Input()
  organizationDetails: OutputProjectPartnerOrganizationDetails[];
  @Input()
  editable: boolean;
  @Input()
  success$: Observable<boolean>;
  @Input()
  error$: Observable<I18nValidationError | null>;
  @Input()
  showHomePage = false;

  @Output()
  update = new EventEmitter<InputProjectPartnerOrganizationDetails[]>();

  nuts$ = of();
  nuts2$ = new Subject<any>();
  nuts3$ = new Subject<any[]>();

  nutsDepartment$ = of();
  nuts2Department$ = new Subject<any>();
  nuts3Department$ = new Subject<any[]>();

  constructor(private nutsStore: NutsStoreService) {
  }

  ngOnInit(): void {
    this.nuts$ = this.nutsStore.getNuts()
        .pipe(
            tap(nuts => {
                const organizationDetailsMainAddress = this.organizationDetails?.find(org => org.type === InputProjectPartnerOrganizationDetails.TypeEnum.Organization);
                if (!organizationDetailsMainAddress?.country) {
                    this.nuts2$.next([])
                    this.nuts3$.next([])
                    return;
                }
                const country = nuts[organizationDetailsMainAddress.country];
                if (!country) return;

                const newNuts = new Map<string, any>();
                Object.values(country).forEach((nut: any) => {
                    Object.keys(nut).forEach(secondLayerNut => {
                        newNuts.set(secondLayerNut, nut[secondLayerNut]);
                    })
                })
                this.nuts2$.next(newNuts);

                if (organizationDetailsMainAddress.nutsRegion2)
                    this.nuts3$.next(newNuts
                        .get(organizationDetailsMainAddress.nutsRegion2)
                        .map( (region: any) => region.title));
            })
        );
      this.nutsDepartment$ = this.nutsStore.getNuts()
          .pipe(
              tap(nuts => {
                  const organizationDetailsMainAddress = this.organizationDetails?.find(org => org.type === InputProjectPartnerOrganizationDetails.TypeEnum.Department);
                  if (!organizationDetailsMainAddress?.country) {
                      this.nuts2Department$.next([])
                      this.nuts3Department$.next([])
                      return;
                  }
                  const country = nuts[organizationDetailsMainAddress.country];
                  if (!country) return;

                  const newNuts = new Map<string, any>();
                  Object.values(country).forEach((nut: any) => {
                      Object.keys(nut).forEach(secondLayerNut => {
                          newNuts.set(secondLayerNut, nut[secondLayerNut]);
                      })
                  })
                  this.nuts2Department$.next(newNuts);

                  if (organizationDetailsMainAddress.nutsRegion2)
                      this.nuts3Department$.next(newNuts
                          .get(organizationDetailsMainAddress.nutsRegion2)
                          .map( (region: any) => region.title));
              })
          );
  }

  ngOnChanges(changes: SimpleChanges) {
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

  changeDepartmentCountry(country: any) {
    const newNuts = new Map<string, any>();
    Object.values(country).forEach((nut: any) => {
        Object.keys(nut).forEach(secondLayerNut => {
            newNuts.set(secondLayerNut, nut[secondLayerNut]);
        })

    })
    this.nuts2Department$.next(newNuts)
    this.nuts3Department$.next([])
  }

  changeDepartmentRegion( regions: any[]) {
    this.nuts3Department$.next(regions.map( region => region.title));
  }
}


