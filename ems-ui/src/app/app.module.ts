import {BrowserModule} from '@angular/platform-browser';
import {NgModule} from '@angular/core';
import {ApiModule, BASE_PATH} from '@cat/api';

import {AppRoutingModule} from './app-routing.module';
import {AppComponent} from './app.component';
import {HTTP_INTERCEPTORS, HttpClientModule} from '@angular/common/http';
import {AuthenticationInterceptor} from './security/authentication.interceptor';
import {ReactiveFormsModule, FormsModule} from '@angular/forms';
import {SecurityService} from './security/security.service';
import {AppI18nModule} from './app-i18n.module';
import {ProjectApplicationSubmissionComponent} from './components/project-application/project-application-submission/project-application-submission.component';
import {BrowserAnimationsModule} from '@angular/platform-browser/animations';
import {MatFormFieldModule} from '@angular/material/form-field';
import {MatButtonModule} from '@angular/material/button';
import {MatInputModule} from '@angular/material/input';
import {ProjectApplicationComponent} from './components/project-application/project-application.component';
import {ProjectApplicationListComponent} from './components/project-application/project-application-list/project-application-list.component';
import {MatListModule} from '@angular/material/list';
import {MatTableModule} from '@angular/material/table';
import {ProjectApplicationService} from './services/project-application.service';
import {ProjectFileService} from './services/project-file.service';
import { ProjectApplicationDetailComponent } from './components/project-application/project-application-detail/project-application-detail.component';
import { TableComponent } from './components/general/table/table.component';
import {DatePipe} from '@angular/common';

@NgModule({
  declarations: [
    AppComponent,
    ProjectApplicationComponent,
    ProjectApplicationListComponent,
    ProjectApplicationSubmissionComponent,
    ProjectApplicationDetailComponent,
    TableComponent,
  ],
  imports: [
    BrowserModule,
    HttpClientModule,
    ApiModule,
    FormsModule,
    ReactiveFormsModule,
    AppRoutingModule,
    AppI18nModule,
    BrowserAnimationsModule,
    MatFormFieldModule,
    MatButtonModule,
    MatInputModule,
    MatListModule,
    MatTableModule,
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
    },
    ProjectApplicationService,
    ProjectFileService,
    DatePipe
    ],
  entryComponents: [
  ],
  bootstrap: [AppComponent]
})
export class AppModule {
}
