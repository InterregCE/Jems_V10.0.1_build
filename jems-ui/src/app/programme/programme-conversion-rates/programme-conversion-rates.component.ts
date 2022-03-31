import {ChangeDetectionStrategy, Component} from '@angular/core';
import PermissionsEnum = UserRoleDTO.PermissionsEnum;
import {CurrencyDTO, UserRoleDTO} from '@cat/api';
import {ProgrammeConversionRateStore} from './programme-conversion-rate-store.service';
import { Alert } from '@common/components/forms/alert';
import {Observable} from 'rxjs';
import {ProgrammePageSidenavService} from '../programme-page/services/programme-page-sidenav.service';

@Component({
  selector: 'jems-programme-conversion-rates',
  templateUrl: './programme-conversion-rates.component.html',
  styleUrls: ['./programme-conversion-rates.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ProgrammeConversionRatesComponent {
  PermissionsEnum = PermissionsEnum;
  Alert = Alert;

  currencies$: Observable<CurrencyDTO[]>

  constructor(public store: ProgrammeConversionRateStore,
              private programmePageSidenavService: ProgrammePageSidenavService) {
    this.currencies$ = this.store.currencies$
  }

  onSubmit(): void {
    this.store.downloadConversionRates$.next();
  }
}
