import {NgModule} from '@angular/core';
import {DateAdapter, MAT_DATE_FORMATS, MAT_DATE_LOCALE} from '@angular/material/core';
import {
  MAT_MOMENT_DATE_ADAPTER_OPTIONS,
  MAT_MOMENT_DATE_FORMATS,
  MomentDateAdapter
} from '@angular/material-moment-adapter';
import {NGX_MAT_DATE_FORMATS, NgxMatDateAdapter} from '@angular-material-components/datetime-picker';
import {NGX_MAT_MOMENT_DATE_ADAPTER_OPTIONS, NgxMatMomentAdapter} from '@angular-material-components/moment-adapter';
import {MAT_FORM_FIELD_DEFAULT_OPTIONS, MatFormFieldDefaultOptions} from '@angular/material/form-field';
import {LocaleStore} from '../services/locale-store.service';

const appearance: MatFormFieldDefaultOptions = {
  appearance: 'fill'
};

@NgModule({
  providers: [
    {
      provide: MAT_FORM_FIELD_DEFAULT_OPTIONS,
      useValue: appearance
    },
    {
      provide: MAT_DATE_FORMATS, useValue: MAT_MOMENT_DATE_FORMATS
    },
    {
      provide: MAT_MOMENT_DATE_ADAPTER_OPTIONS, useValue: {useUtc: true}
    },
    {
      provide: NGX_MAT_MOMENT_DATE_ADAPTER_OPTIONS, useValue: {useUtc: false}
    },
    {
      provide: DateAdapter,
      useClass: MomentDateAdapter,
      deps: [MAT_DATE_LOCALE, MAT_MOMENT_DATE_ADAPTER_OPTIONS]
    },
    {
      provide: NgxMatDateAdapter,
      useClass: NgxMatMomentAdapter,
      deps: [MAT_DATE_LOCALE, NGX_MAT_MOMENT_DATE_ADAPTER_OPTIONS]
    },
    {
      provide: NGX_MAT_DATE_FORMATS, useValue: {
        parse: {
          dateInput: ['YYYY-MM-DDTHH:MM:00Z', 'l, LT']
        },
        display: {
          dateInput: 'L LT',
          monthYearLabel: 'MMM YYYY',
          dateA11yLabel: 'LL',
          monthYearA11yLabel: 'MMMM YYYY'
        },
        useUtc: true
      }
    }
  ]
})
export class MaterialConfigModule {
  constructor(private dateAdapter: DateAdapter<MomentDateAdapter>,
              private ngxDateAdapter: NgxMatDateAdapter<NgxMatMomentAdapter>) {
    // for date formats we currently use the browser locale
    this.dateAdapter.setLocale(LocaleStore.browserLocale());
    this.ngxDateAdapter.setLocale(LocaleStore.browserLocale());
  }
}
