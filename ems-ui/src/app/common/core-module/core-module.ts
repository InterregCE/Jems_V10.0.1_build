import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';
import {AppI18nModule} from '../../app-i18n.module';
import {HttpClientModule} from '@angular/common/http';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {BrowserModule} from '@angular/platform-browser';
import {ApiModule} from '@cat/api';
import {AppRoutingModule} from '../../app-routing.module';
import {TranslateModule} from '@ngx-translate/core';
import {MatInputModule} from '@angular/material/input';
import {MatSelectModule} from '@angular/material/select';
import {MatTableModule} from '@angular/material/table';
import {MatListModule} from '@angular/material/list';
import {MatButtonModule} from '@angular/material/button';
import {BrowserAnimationsModule} from '@angular/platform-browser/animations';
import {MatDialogModule} from '@angular/material/dialog';
import {OverlayModule} from '@angular/cdk/overlay';
import {TableComponent} from '../../components/general/table/table.component';

const modules = [
  CommonModule,
  BrowserModule,
  HttpClientModule,
  ApiModule,
  AppRoutingModule,
  AppI18nModule,
  TranslateModule,
  // TODO with the module splinting task: only keep the modules that are common to all other modules
  // some/most modules below probably are not
  FormsModule,
  ReactiveFormsModule,
  BrowserAnimationsModule,
  MatButtonModule,
  MatInputModule,
  MatListModule,
  MatSelectModule,
  MatTableModule,
  MatDialogModule,
  OverlayModule,
];

const declarations = [
  TableComponent,
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
export class CoreModule {
}
