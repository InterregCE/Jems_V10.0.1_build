import {NgModule} from '@angular/core';
import {DatePipe} from '@angular/common';
import {ProjectRoutingModule} from './project-routing.module';
import {CoreModule} from '../common/core-module';
import {ProjectApplicationDetailComponent} from './project-application/containers/project-application-detail/project-application-detail.component';
import {ProjectApplicationSubmissionComponent} from './project-application/components/project-application-submission/project-application-submission.component';
import {ProjectApplicationListComponent} from './project-application/components/project-application-list/project-application-list.component';
import {ProjectApplicationComponent} from './project-application/containers/project-application-page/project-application.component';
import {DeleteDialogComponent} from './project-application/components/project-application-detail/delete-dialog/delete-dialog.component';
import {ProjectFileService} from './project-application/services/project-file.service';
import {DescriptionCellComponent} from '@common/components/table/cell-renderers/description-cell/description-cell.component';
import {SharedModule} from '../common/shared-module';
import {OverlayModule} from '@angular/cdk/overlay';
import {MatDialogModule} from '@angular/material/dialog';
import {MatSelectModule} from '@angular/material/select';
import {MatListModule} from '@angular/material/list';
import {BrowserAnimationsModule} from '@angular/platform-browser/animations';
import { ProjectApplicationDataComponent } from './project-application/components/project-application-detail/project-application-data/project-application-data.component';
import { ProjectApplicationFilesListComponent } from './project-application/components/project-application-detail/project-application-files-list/project-application-files-list.component';

@NgModule({
  declarations: [
    DeleteDialogComponent,
    DescriptionCellComponent,
    ProjectApplicationComponent,
    ProjectApplicationListComponent,
    ProjectApplicationSubmissionComponent,
    ProjectApplicationDetailComponent,
    ProjectApplicationDataComponent,
    ProjectApplicationFilesListComponent,
  ],
  imports: [
    SharedModule,
    CoreModule,
    ProjectRoutingModule,
    MatListModule,
    MatSelectModule,
    MatDialogModule,
    OverlayModule,
    BrowserAnimationsModule
  ],
  providers: [
    ProjectFileService,
    DatePipe
  ],
  entryComponents: [
    DescriptionCellComponent,
    DeleteDialogComponent,
  ]
})
export class ProjectModule {
}
