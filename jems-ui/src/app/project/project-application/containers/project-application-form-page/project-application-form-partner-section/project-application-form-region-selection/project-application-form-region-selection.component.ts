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
import {OutputProjectPartnerDetail, InputProjectPartnerOrganizationDetails} from '@cat/api';
import {tap} from 'rxjs/operators';

@Component({
  selector: 'app-project-application-form-region-selection',
  templateUrl: './project-application-form-region-selection.component.html',
  styleUrls: ['./project-application-form-region-selection.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ProjectApplicationFormRegionSelectionComponent implements OnInit, OnChanges{

  @Input()
  partner: OutputProjectPartnerDetail;
  @Input()
  editable: boolean;
  @Input()
  success$: Observable<boolean>;
  @Input()
  error$: Observable<I18nValidationError | null>;

  @Output()
  update = new EventEmitter<InputProjectPartnerOrganizationDetails>();

  nuts$ = of();
  nuts2$ = new Subject<any>();
  nuts3$ = new Subject<any[]>();

  // details$ = combineLatest([this.nuts$, this.nuts2$, this.nuts3$])
  //     .pipe(
  //       map(([nuts, nuts2, nuts3]) => ({ nuts, nuts2, nuts3 }))
  //     )

  constructor(private nutsStore: NutsStoreService) {
  }

  ngOnInit(): void {
    this.nuts$ = this.nutsStore.getNuts()
        .pipe(
            tap(nuts => {
              if (!this.partner.organization?.organizationDetails?.country) {
                  this.nuts2$.next([])
                  this.nuts3$.next([])
                  return;
              }
              const country = nuts[this.partner.organization.organizationDetails.country];
              if (!country) return;

                const newNuts = new Map<string, any>();
                Object.values(country).forEach((nut: any) => {
                    Object.keys(nut).forEach(secondLayerNut => {
                        newNuts.set(secondLayerNut, nut[secondLayerNut]);
                    })
                })
                this.nuts2$.next(newNuts);

                if (this.partner.organization.organizationDetails.nutsRegion2)
                    this.nuts3$.next(newNuts
                        .get(this.partner.organization.organizationDetails.nutsRegion2)
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
}


