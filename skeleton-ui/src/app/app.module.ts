import {BrowserModule} from '@angular/platform-browser';
import {NgModule} from '@angular/core';
import {ApiModule, BASE_PATH} from '@cat/api';

import {AppRoutingModule} from './app-routing.module';
import {AppComponent} from './app.component';
import {HomeComponent} from './pages/home/home.component';
import {LoginComponent} from './pages/login/login.component';
import {HTTP_INTERCEPTORS, HttpClientModule} from '@angular/common/http';
import {AuthenticationInterceptor} from './security/authentication.interceptor';
import {ReactiveFormsModule} from '@angular/forms';
import {SecurityService} from './security/security.service';
import {AppI18nModule} from './app-i18n.module';

@NgModule({
  declarations: [
    AppComponent,
    HomeComponent,
    LoginComponent
  ],
  imports: [
    BrowserModule,
    HttpClientModule,
    ApiModule,
    ReactiveFormsModule,
    AppRoutingModule,
    AppI18nModule
  ],
  providers: [
    SecurityService,
    {
      provide: HTTP_INTERCEPTORS,
      useClass: AuthenticationInterceptor,
      multi: true
    },
    {
      provide: BASE_PATH,
      useValue: '.'
    }],
  entryComponents: [
    HomeComponent,
    LoginComponent
  ],
  bootstrap: [AppComponent]
})
export class AppModule {
}
