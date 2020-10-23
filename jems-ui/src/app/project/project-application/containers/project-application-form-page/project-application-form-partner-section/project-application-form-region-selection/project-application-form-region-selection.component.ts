import {
  ChangeDetectionStrategy,
  Component
} from '@angular/core';
import {NutsStoreService} from '../../../../../../common/services/nuts-store.service';
import {combineLatest, Subject} from 'rxjs';
import {InputProjectPartnerAddress, OutputProjectPartnerDetail, OutputProjectPartnerAddress} from '@cat/api';
import {map, startWith} from 'rxjs/operators';
import {ProjectPartnerStore} from '../../services/project-partner-store.service';

@Component({
  selector: 'app-project-application-form-region-selection',
  templateUrl: './project-application-form-region-selection.component.html',
  styleUrls: ['./project-application-form-region-selection.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ProjectApplicationFormRegionSelectionComponent {
  mainCountryChanged$ = new Subject<string>();
  mainRegion2Changed$ = new Subject<any[]>();

  mainAddress$ = combineLatest([
    this.partnerStore.getProjectPartner(),
    this.nutsStore.getNuts(),
    this.mainCountryChanged$.pipe(startWith(null)),
    this.mainRegion2Changed$.pipe(startWith(null))
  ])
    .pipe(
      map(([partner, nuts, changedCountry, changedRegion2]) => ({
        partner,
        country: nuts,
        region2: this.getRegion2(partner, nuts, changedCountry, InputProjectPartnerAddress.TypeEnum.Organization),
        region3: this.getRegion3(partner, nuts, changedRegion2, InputProjectPartnerAddress.TypeEnum.Organization),
      }))
    );

  departmentCountryChanged$ = new Subject<string>();
  departmentRegion2Changed$ = new Subject<any[]>();

  departmentAddress$ = combineLatest([
    this.partnerStore.getProjectPartner(),
    this.nutsStore.getNuts(),
    this.departmentCountryChanged$.pipe(startWith(null)),
    this.departmentRegion2Changed$.pipe(startWith(null))
  ])
    .pipe(
      map(([partner, nuts, changedCountry, changedRegion2]) => ({
        partner,
        country: nuts,
        region2: this.getRegion2(partner, nuts, changedCountry, InputProjectPartnerAddress.TypeEnum.Department),
        region3: this.getRegion3(partner, nuts, changedRegion2, InputProjectPartnerAddress.TypeEnum.Department),
      }))
    );

  details$ = combineLatest([
    this.partnerStore.getProjectPartner(),
    this.mainAddress$,
    this.departmentAddress$
  ])
    .pipe(
      map(([partner, main, department]) => ({partner, main, department}))
    );

  constructor(private nutsStore: NutsStoreService,
              public partnerStore: ProjectPartnerStore) {
  }

  private getPartnerAddress(partner: OutputProjectPartnerDetail,
                            addressType: InputProjectPartnerAddress.TypeEnum): OutputProjectPartnerAddress | undefined {
    return partner?.addresses?.find((addr: OutputProjectPartnerAddress) => addr.type === addressType);
  }

  private getPartnerCountry(nuts: any, address?: OutputProjectPartnerAddress) {
    if (!address?.country) {
      return null;
    }
    return nuts[address.country];
  }

  private getRegion2(partner: OutputProjectPartnerDetail,
                     nuts: any,
                     changedCountry: string | null,
                     addressType: InputProjectPartnerAddress.TypeEnum): any {
    const address = this.getPartnerAddress(partner, addressType);
    const country = changedCountry || this.getPartnerCountry(nuts, address);
    if (!country) return [];

    const newNuts = new Map<string, any>();
    Object.values(country).forEach((nut: any) => {
      Object.keys(nut).forEach(secondLayerNut => {
        newNuts.set(secondLayerNut, nut[secondLayerNut]);
      })
    })
    return newNuts;
  }

  private getRegion3(partner: OutputProjectPartnerDetail,
                     nuts: any,
                     changedRegion2: any[] | null,
                     addressType: InputProjectPartnerAddress.TypeEnum): any {
    if (changedRegion2) {
      return changedRegion2.map(region => region.title);
    }

    const address = this.getPartnerAddress(partner, addressType);
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
