import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {TranslateModule} from '@ngx-translate/core';
import {TableComponent} from './components/table/table.component';
import {NgxPermissionsModule} from 'ngx-permissions';
import {PaginatorComponent} from '@common/components/table/paginator/paginator.component';
import {RouterModule} from '@angular/router';
import {MoneyPipe} from './pipe/money.pipe';
import {MaterialModule} from './material/material-module';
import {BreadcrumbComponent} from '@common/components/breadcrumb/breadcrumb.component';
import {AlertComponent} from '@common/components/forms/form-validation/alert.component';
import {ExpandableTextareaComponent} from '@common/components/expandable-textarea/expandable-textarea.component';
import {TopBarComponent} from '@common/components/top-bar/top-bar.component';
import {FormFieldErrorsComponent} from '@common/components/forms/form-field-errors/form-field-errors.component';
import {ConfirmDialogComponent} from '@common/components/modals/confirm-dialog/confirm-dialog.component';
import {HelpMenuComponent} from '@common/components/top-bar/help-menu/help-menu.component';
import {SideNavComponent} from '@common/components/side-nav/component/side-nav.component';
import {FormComponent} from '@common/components/section/form/form.component';
import {PercentagePipe} from './pipe/percentage';
import {
  MultiLanguageContainerComponent
} from '@common/components/forms/multi-language-container/multi-language-container.component';
import {ContextInfoComponent} from '@common/components/info-trigger/context-info.component';
import {FormFieldWidthDirective} from './directives/form-field-width/form-field-width.directive';
import {NgxCurrencyModule} from 'ngx-currency';
import {CurrencyDirective} from './directives/currency.directive';
import {TableConfigDirective} from './directives/table-config/table-config.directive';
import {
  MultiLanguageFormFieldComponent
} from '@common/components/forms/multi-language-form-field/multi-language-form-field.component';
import {FormLayoutDirective} from './directives/form-layout/form-layout.directive';
import {MultiColumnRowDirective} from './directives/multi-column-row/multi-column-row.directive';
import {NoWidthLimitDirective} from './directives/no-width-limit.directive';
import {LabelDirective} from './directives/form-layout/label.directive';
import {
  MainPageTemplateComponent
} from '@common/components/page-templates/main-page-template/main-page-template.component';
import {MatBadgeModule} from '@angular/material/badge';
import {ApiErrorContentComponent} from '@common/components/forms/api-error-content/api-error-content.component';
import {
  ProjectApplicationListComponent
} from '@common/components/project-application-list/project-application-list.component';
import {CallListComponent} from '@common/components/call-list/call-list.component';
import {InlineEditableFieldComponent} from '@common/components/inline-editable-field/inline-editable-field.component';
import {MatProgressSpinnerModule} from '@angular/material/progress-spinner';
import {TranslateBySystemLanguagePipe} from './pipe/translate-by-system-language.pipe';
import {TranslateByInputLanguagePipe} from './pipe/translate-by-input-language.pipe';
import {
  TranslateContainerLessInputsBySystemLanguagePipe
} from './pipe/translate-container-less-inputs-by-system-language.pipe';
import {LocaleDatePipe} from './pipe/locale-date.pipe';
import {DateFormatInfoPipe} from './pipe/date-format-info.pipe';
import {CustomTranslatePipe} from './pipe/custom-translate-pipe';
import {PendingButtonComponent} from '@common/components/section/form/pending-button/pending-button.component';
import {HasPermissionDirective} from './directives/has-permission.directive';
import {HintDirective} from './directives/hint.directive';
import {TextHintComponent} from '@common/components/forms/text-hint/text-hint.component';
import {IsMenuActivePipe} from '@common/components/side-nav/is-menu-active.pipe';
import {
  FilterAutocompleteInputComponent
} from '@common/components/filter/filter-autocomplete-input/filter-autocomplete-input.component';
import {FilterTemplateComponent} from '@common/components/filter/filter-template/filter-template.component';
import {FilterTextInputComponent} from '@common/components/filter/filter-text-input/filter-text-input.component';
import {FilterListInputComponent} from '@common/components/filter/filter-list-input/filter-list-input.component';
import {FilterDateInputComponent} from '@common/components/filter/filter-date-input/filter-date-input.component';
import {FilterOnlyDateInputComponent} from '@common/components/filter/filter-only-date-input/filter-only-date-input.component';
import {TextDirective} from '@common/directives/text.directive';
import {AdaptTranslationKeyByCallTypePipe} from '@common/pipe/adapt-translation-by-call-type.pipe';
import {SecondsToTimePipePipe} from '@common/pipe/seconds-to-time-pipe.pipe';
import {ChecklistAnswersComponent} from '@common/components/checklist/checklist-answers/checklist-answers.component';
import {
  PublicPageTemplateComponent
} from '@common/components/page-templates/public-page-template/public-page-template.component';
import {
  ChecklistInstanceListComponent
} from '@common/components/checklist/checklist-instance-list/checklist-instance-list.component';
import {
  ControlChecklistInstanceListComponent
} from '@common/components/checklist/control-checklist-instance-list/control-checklist-instance-list.component';
import {
  ChecklistConsolidatorOptionsComponent
} from '@common/components/checklist/checklist-consolidator-options/checklist-consolidator-options.component';
import {MatSliderModule} from '@angular/material/slider';
import {FileListComponent} from '@common/components/file-list/file-list.component';
import {JemsRegionsComponent} from '@common/components/jems-regions/jems-regions.component';
import {JemsNutsInfoComponent} from '@common/components/jems-nuts-info/jems-nuts-info.component';
import {JemsRegionsTreeComponent} from '@common/components/jems-regions-tree/jems-regions-tree.component';
import {JemsSelectedRegionsComponent} from '@common/components/jems-selected-regions/jems-selected-regions.component';
import {FileListTableComponent} from '@common/components/file-list/file-list-table/file-list-table.component';
import {
  ContractingChecklistInstanceListComponent
} from '@common/components/checklist/contracting-checklist-instance-list/contracting-checklist-instance-list.component';
import {ChecklistUtilsComponent} from '@common/components/checklist/checklist-utils/checklist-utils';
import {
  FileOperationsActionCellComponent
} from '@common/components/file-list/file-list-table/file-operations-action-cell/file-operations-action-cell.component';
import {
  FileListTableWithFileLinkingComponent
} from '@common/components/file-list/file-list-table-with-file-linking/file-list-table-with-file-linking.component';
import {NotificationListComponent} from '@common/components/notification-list/notification-list.component';
import {FilterNutsInputComponent} from '@common/components/filter/filter-nuts/filter-nuts-input.component';
import {PartnerReportListComponent} from '@common/components/partner-report-list/partner-report-list.component';
import {
  PartnerReportStatusComponent
} from '@project/project-application/report/partner-report-status/partner-report-status.component';
import {
  VerificationChecklistInstanceListComponent
} from '@common/components/checklist/verification-checklist-instance-list/verification-checklist-instance-list.component';
import {ProjectReportListComponent} from '@common/components/project-report-list/project-report-list.component';
import {
  ProjectReportStatusComponent
} from '@project/project-application/report/project-report/project-report-status/project-report-status.component';
import {AccountingYearPipe} from '@common/pipe/accounting-year.pipe';
import {AsPercentagePipe} from '@common/pipe/as-percentage.pipe';
import {
  ClosureChecklistInstanceListComponent
} from '@common/components/checklist/closure-checklist-instance-list/closure-checklist-instance-list.component';

const modules = [
  CommonModule,
  TranslateModule,
  FormsModule,
  ReactiveFormsModule,
  NgxPermissionsModule.forChild(),
  RouterModule,
  MaterialModule,
  NgxCurrencyModule,
  MatBadgeModule,
  MatProgressSpinnerModule,
  MatSliderModule,
];

const declarations = [
  ProjectApplicationListComponent,
  CallListComponent,
  NotificationListComponent,
  FileListComponent,
  FileListTableComponent,
  TableComponent,
  PaginatorComponent,
  FormComponent,
  PendingButtonComponent,
  TranslateBySystemLanguagePipe,
  TranslateByInputLanguagePipe,
  TranslateContainerLessInputsBySystemLanguagePipe,
  AdaptTranslationKeyByCallTypePipe,
  MoneyPipe,
  AsPercentagePipe,
  PercentagePipe,
  LocaleDatePipe,
  DateFormatInfoPipe,
  AccountingYearPipe,
  MultiLanguageContainerComponent,
  BreadcrumbComponent,
  AlertComponent,
  ExpandableTextareaComponent,
  TopBarComponent,
  FormFieldErrorsComponent,
  ConfirmDialogComponent,
  HelpMenuComponent,
  SideNavComponent,
  ContextInfoComponent,
  FormFieldWidthDirective,
  CurrencyDirective,
  HasPermissionDirective,
  TableConfigDirective,
  MultiLanguageFormFieldComponent,
  FormLayoutDirective,
  MultiColumnRowDirective,
  NoWidthLimitDirective,
  LabelDirective,
  TextDirective,
  MainPageTemplateComponent,
  ApiErrorContentComponent,
  InlineEditableFieldComponent,
  CustomTranslatePipe,
  HintDirective,
  TextHintComponent,
  IsMenuActivePipe,
  FilterTemplateComponent,
  FilterTextInputComponent,
  FilterDateInputComponent,
  FilterOnlyDateInputComponent,
  FilterListInputComponent,
  FilterAutocompleteInputComponent,
  FilterNutsInputComponent,
  SecondsToTimePipePipe,
  ChecklistAnswersComponent,
  ChecklistInstanceListComponent,
  ControlChecklistInstanceListComponent,
  ChecklistConsolidatorOptionsComponent,
  PublicPageTemplateComponent,
  JemsRegionsComponent,
  JemsNutsInfoComponent,
  JemsRegionsTreeComponent,
  JemsSelectedRegionsComponent,
  ContractingChecklistInstanceListComponent,
  ChecklistUtilsComponent,
  FileOperationsActionCellComponent,
  FileListTableWithFileLinkingComponent,
  PartnerReportListComponent,
  ProjectReportListComponent,
  PartnerReportStatusComponent,
  ProjectReportStatusComponent,
  VerificationChecklistInstanceListComponent,
  ClosureChecklistInstanceListComponent,
];

const providers = [
  MoneyPipe,
  LocaleDatePipe,
  DateFormatInfoPipe,
  CustomTranslatePipe,
  AccountingYearPipe,
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
  ],
  providers: [
    ...providers
  ]
})
export class SharedModule {
}
