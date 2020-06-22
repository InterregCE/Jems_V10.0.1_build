import {NgModule} from '@angular/core';
import {BASE_PATH} from '@cat/api';
import {AppComponent} from './app.component';
import {HTTP_INTERCEPTORS} from '@angular/common/http';
import {AuthenticationInterceptor} from './security/authentication.interceptor';
import {SecurityService} from './security/security.service';
import {ProjectApplicationSubmissionComponent} from './components/project-application/project-application-submission/project-application-submission.component';
import {ProjectApplicationComponent} from './components/project-application/project-application.component';
import {ProjectApplicationListComponent} from './components/project-application/project-application-list/project-application-list.component';
import {ProjectFileService} from './services/project-file.service';
import {ProjectApplicationDetailComponent} from './components/project-application/project-application-detail/project-application-detail.component';
import {DatePipe} from '@angular/common';
import {DescriptionCellComponent} from './components/general/cell-renderers/description-cell/description-cell.component';
import {DeleteDialogComponent} from './components/project-application/project-application-detail/delete-dialog.component';
import {TopBarComponent} from './components/general/top-bar/top-bar.component';
import {HttpErrorInterceptor} from './common/http-error.interceptor';
import {MenuComponent} from './components/general/menu/menu.component';
import {UserModule} from './components/user/user.module';
import {CoreModule} from './common/core-module/core-module';
import {MatTabsModule} from '@angular/material/tabs';

@NgModule({
  declarations: [
    AppComponent,
    DeleteDialogComponent,
    ProjectApplicationComponent,
    ProjectApplicationListComponent,
    ProjectApplicationSubmissionComponent,
    ProjectApplicationDetailComponent,
    DescriptionCellComponent,
    TopBarComponent,
    MenuComponent
  ],
  imports: [
    CoreModule,
    MatTabsModule,
    UserModule,
  ],
  providers: [
    SecurityService,
    {
      provide: HTTP_INTERCEPTORS,
      useClass: AuthenticationInterceptor,
      multi: true
    },
    {
      provide: HTTP_INTERCEPTORS,
      useClass: HttpErrorInterceptor,
      multi: true
    },
    {
      provide: BASE_PATH,
      useValue: '.'
    },
    ProjectFileService,
    DatePipe
  ],
  entryComponents: [
    DescriptionCellComponent,
    DeleteDialogComponent,
  ],
  bootstrap: [AppComponent]
})
export class AppModule {
}
