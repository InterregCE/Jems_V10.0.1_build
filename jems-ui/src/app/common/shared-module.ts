import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {TranslateModule} from '@ngx-translate/core';
import {TableComponent} from './components/table/table.component';
import {NgxPermissionsModule} from 'ngx-permissions';
import {PaginatorComponent} from '@common/components/table/paginator/paginator.component';
import {RouterModule} from '@angular/router';
import {ProjectApplicationListComponent} from '../project/project-application/components/project-application-list/project-application-list.component';
import {MoneyPipe} from './pipe/money.pipe';
import {RowListTemplateComponent} from './templates/row-list-template/row-list-template.component';
import {MaterialModule} from './material/material-module';
import {BreadcrumbComponent} from '@common/components/breadcrumb/breadcrumb.component';
import {AlertComponent} from '@common/components/forms/form-validation/alert.component';
import {ExpandableTextareaComponent} from '@common/components/expandable-textarea/expandable-textarea.component';
import {TopBarComponent} from '@common/components/top-bar/top-bar.component';
import {MenuComponent} from '@common/components/menu/menu.component';
import {FormFieldErrorsComponent} from '@common/components/forms/form-field-errors/form-field-errors.component';
import {ConfirmDialogComponent} from '@common/components/modals/confirm-dialog/confirm-dialog.component';
import {HelpMenuComponent} from '@common/components/top-bar/help-menu/help-menu.component';
import {SideNavComponent} from '@common/components/side-nav/component/side-nav.component';
import {FormComponent} from '@common/components/section/form/form.component';
import {PercentagePipe} from './pipe/percentage';
import {MultiLanguageComponent} from '@common/components/forms/multi-language/multi-language.component';

const modules = [
  CommonModule,
  TranslateModule,
  FormsModule,
  ReactiveFormsModule,
  NgxPermissionsModule.forChild(),
  RouterModule,
  MaterialModule
];

const declarations = [
  ProjectApplicationListComponent,
  RowListTemplateComponent,
  TableComponent,
  PaginatorComponent,
  FormComponent,
  MoneyPipe,
  PercentagePipe,
  MultiLanguageComponent,
  BreadcrumbComponent,
  AlertComponent,
  ExpandableTextareaComponent,
  TopBarComponent,
  MenuComponent,
  FormFieldErrorsComponent,
  ConfirmDialogComponent,
  HelpMenuComponent,
  SideNavComponent
];

@NgModule({
  declarations: [
    declarations
  ],
  imports: [
    modules
  ],
  exports: [
    modules,
    declarations
  ]
})
export class SharedModule {
}
