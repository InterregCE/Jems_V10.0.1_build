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
import {MatDialogModule} from '@angular/material/dialog';
import {ProjectFileService} from './services/project-file.service';
import { ProjectApplicationDetailComponent } from './components/project-application/project-application-detail/project-application-detail.component';
import { TableComponent } from './components/general/table/table.component';
import {DatePipe} from '@angular/common';
import { DescriptionCellComponent } from './components/general/cell-renderers/description-cell/description-cell.component';
import {DeleteDialogComponent} from './components/project-application/project-application-detail/delete-dialog.component';
import {MatSelectModule} from '@angular/material/select';
import {TopBarComponent} from './components/general/top-bar/top-bar.component';
import {MatCardModule} from '@angular/material/card';
import {HttpErrorInterceptor} from './common/http-error.interceptor';
import {LoginComponent} from './components/user/login/login.component';
import { OverlayModule } from '@angular/cdk/overlay';
import { MenuComponent } from './components/general/menu/menu.component';
import {MatTabsModule} from '@angular/material/tabs';

@NgModule({
  declarations: [
    AppComponent,
    DeleteDialogComponent,
    ProjectApplicationComponent,
    ProjectApplicationListComponent,
    ProjectApplicationSubmissionComponent,
    ProjectApplicationDetailComponent,
    TableComponent,
    DescriptionCellComponent,
    TopBarComponent,
    LoginComponent,
    MenuComponent
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
    MatSelectModule,
    MatCardModule,
    MatTableModule,
    MatDialogModule,
    OverlayModule,
    MatTabsModule,
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
