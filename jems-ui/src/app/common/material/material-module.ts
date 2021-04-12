import {NgModule} from '@angular/core';
import {MatTabsModule} from '@angular/material/tabs';
import {MatDialogModule} from '@angular/material/dialog';
import {MatMenuModule} from '@angular/material/menu';
import {MatIconModule} from '@angular/material/icon';
import {MatSelectModule} from '@angular/material/select';
import {MatSortModule} from '@angular/material/sort';
import {MatSidenavModule} from '@angular/material/sidenav';
import {MatExpansionModule} from '@angular/material/expansion';
import {MatButtonToggleModule} from '@angular/material/button-toggle';
import {MatCardModule} from '@angular/material/card';
import {MatButtonModule} from '@angular/material/button';
import {MatInputModule} from '@angular/material/input';
import {MatTableModule} from '@angular/material/table';
import {MatPaginatorIntl, MatPaginatorModule} from '@angular/material/paginator';
import {MatDatepickerIntl, MatDatepickerModule} from '@angular/material/datepicker';
import {MatCheckboxModule} from '@angular/material/checkbox';
import {MatListModule} from '@angular/material/list';
import {MatRadioModule} from '@angular/material/radio';
import {MatTooltipModule} from '@angular/material/tooltip';
import {NgxMatDatetimePickerModule} from '@angular-material-components/datetime-picker';
import {MatAutocompleteModule} from '@angular/material/autocomplete';
import {MatTreeModule} from '@angular/material/tree';
import {MatFormFieldModule} from '@angular/material/form-field';
import {TranslatableMatPaginatorIntl} from './translatable-mat-paginator-intl.service';
import {TranslatableMatDatepickerIntl} from './translatable-mat-datepicker-intl.service';
import {MatChipsModule} from '@angular/material/chips';
import {MatProgressSpinnerModule} from '@angular/material/progress-spinner';

const modules = [
  MatTabsModule,
  MatMenuModule,
  MatIconModule,
  MatSidenavModule,
  MatSelectModule,
  MatExpansionModule,
  MatButtonToggleModule,
  MatCardModule,
  MatButtonModule,
  MatInputModule,
  MatTableModule,
  MatDialogModule,
  MatPaginatorModule,
  MatSortModule,
  MatDatepickerModule,
  MatCheckboxModule,
  MatListModule,
  MatRadioModule,
  MatTooltipModule,
  MatAutocompleteModule,
  NgxMatDatetimePickerModule,
  MatTreeModule,
  MatFormFieldModule,
  MatChipsModule,
  MatProgressSpinnerModule
];

@NgModule({
  imports: [
    modules
  ],
  providers: [
    {provide: MatPaginatorIntl, useExisting: TranslatableMatPaginatorIntl},
    {provide: MatDatepickerIntl, useExisting: TranslatableMatDatepickerIntl},
  ],
  exports: [
    modules
  ]
})
export class MaterialModule {
}
