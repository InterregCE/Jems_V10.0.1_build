import {HttpClient} from '@angular/common/http';
import {TranslateHttpLoader} from '@ngx-translate/http-loader';
import {ModuleWithProviders, NgModule} from '@angular/core';
import {TranslateLoader, TranslateModule} from '@ngx-translate/core';
import {CommonModule} from '@angular/common';

@NgModule({
  imports: [
    CommonModule,
    TranslateModule.forChild({
    loader: {
      provide: TranslateLoader,
      useFactory: HttpLoaderFactory,
      deps: [HttpClient]
    },
    isolate: true,
    extend: true
  })],
  exports: [TranslateModule]
})
export class AppI18nModule {
  static forRoot(): ModuleWithProviders {
    return {
      ngModule: AppI18nModule
    };
  }
}


export function HttpLoaderFactory(http: HttpClient): TranslateHttpLoader {
  return new TranslateHttpLoader(http, '/api/i18n/', '');
}
