import {HttpClient} from '@angular/common/http';
import {TranslateHttpLoader} from '@ngx-translate/http-loader';
import {NgModule} from '@angular/core';
import {TranslateLoader, TranslateModule} from '@ngx-translate/core';



@NgModule({
  imports: [TranslateModule.forRoot({
    loader: {
      provide: TranslateLoader,
      useFactory: HttpLoaderFactory,
      deps: [HttpClient]
    }
  })],
  exports: [TranslateModule]
})
export class AppI18nModule {
}


export function HttpLoaderFactory(http: HttpClient) {
  return new TranslateHttpLoader(http, '/api/i18n/', '');
}
