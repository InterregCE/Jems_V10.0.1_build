import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';
import {AppI18nModule} from '../app-i18n.module';
import {HttpClientModule} from '@angular/common/http';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {BrowserModule} from '@angular/platform-browser';
import {ApiModule} from '@cat/api';
import {AppRoutingModule} from '../app-routing.module';
import {TranslateModule} from '@ngx-translate/core';
import {MatInputModule} from '@angular/material/input';
import {MatButtonModule} from '@angular/material/button';
import {TableComponent} from './components/table/table.component';
import {MatTableModule} from '@angular/material/table';
import {NgxPermissionsModule} from 'ngx-permissions';
import {MatPaginatorModule} from '@angular/material/paginator';
import {MatSortModule} from '@angular/material/sort';

const modules = [
  CommonModule,
  BrowserModule,
  HttpClientModule,
  ApiModule,
  AppRoutingModule,
  AppI18nModule,
  TranslateModule,
  FormsModule,
  ReactiveFormsModule,
  MatButtonModule,
  MatInputModule,
  MatTableModule,
  MatPaginatorModule,
  MatSortModule,
  NgxPermissionsModule.forChild()
];

const declarations = [
  TableComponent,
];

@NgModule({
  declarations: [
    declarations
  ],
  imports: [
    modules,
  ],
  exports: [
    modules,
    declarations
  ]
})
export class SharedModule {
}
