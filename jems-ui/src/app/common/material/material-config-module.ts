import {NgModule} from '@angular/core';
import {DateAdapter, MAT_DATE_FORMATS, MAT_DATE_LOCALE} from '@angular/material/core';
import {
  MAT_MOMENT_DATE_ADAPTER_OPTIONS,
  MAT_MOMENT_DATE_FORMATS,
  MomentDateAdapter
} from '@angular/material-moment-adapter';
import {NGX_MAT_DATE_FORMATS, NgxMatDateAdapter} from '@angular-material-components/datetime-picker';
import {NgxMatMomentAdapter} from '@angular-material-components/moment-adapter';
import {LanguageStore} from '../services/language-store.service';
import {MAT_FORM_FIELD_DEFAULT_OPTIONS, MatFormFieldDefaultOptions} from '@angular/material/form-field';

const appearance: MatFormFieldDefaultOptions = {
  appearance: 'fill'
};

@NgModule({
  providers: [
    {
      provide: MAT_FORM_FIELD_DEFAULT_OPTIONS,
      useValue: appearance
    },
    {provide: MAT_DATE_LOCALE, useValue: 'en-GB'},
    {provide: MAT_DATE_FORMATS, useValue: MAT_MOMENT_DATE_FORMATS},
    {
      provide: DateAdapter,
      useClass: MomentDateAdapter,
      deps: [MAT_DATE_LOCALE, MAT_MOMENT_DATE_ADAPTER_OPTIONS]
    },
    {
      provide: NgxMatDateAdapter,
      useClass: NgxMatMomentAdapter,
      deps: [MAT_DATE_LOCALE, MAT_MOMENT_DATE_ADAPTER_OPTIONS]
    },
    {
      provide: NGX_MAT_DATE_FORMATS, useValue: {
        parse: {
          dateInput: ['YYYY-MM-DDTHH:MM:00Z', 'l, LT']
        },
        display: {
          dateInput: 'l, LT',
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
              private ngxDateAdapter: NgxMatDateAdapter<NgxMatMomentAdapter>,
              private languageStore: LanguageStore
  ) {
    this.languageStore.currentSystemLanguage$.subscribe(language => {
      // since moment.js is using different dialect than us for the Norway, we should map 'no' to 'nb' (we are using 'NO' for the Norway while moment.js is using 'nb' for that)
      const local = language.toLowerCase() === 'no' ? 'nb' : language;
      this.dateAdapter.setLocale(local);
      this.ngxDateAdapter.setLocale(local);
    });
  }
}
