import {ChangeDetectionStrategy, Component, TemplateRef, ViewChild} from '@angular/core';
import {OutputNuts, PageControllerInstitutionListDTO, UserRoleCreateDTO, UserRoleDTO} from '@cat/api';
import {InstitutionsPageStore} from './institutions-page-store.service';
import {ControllersPageSidenavService} from '../controllers-page-sidenav.service';
import {combineLatest, Observable} from 'rxjs';
import {TableConfiguration} from '@common/components/table/model/table.configuration';
import {ColumnType} from '@common/components/table/model/column-type.enum';
import {map} from 'rxjs/operators';
import {JemsRegionCheckbox} from '@common/models/jems-region-checkbox';
import {NutsStore} from '@common/services/nuts.store';
import {ColumnWidth} from '@common/components/table/model/column-width';
import Permissions = UserRoleDTO.PermissionsEnum;
import {PermissionService} from '../../security/permissions/permission.service';
import PermissionsEnum = UserRoleCreateDTO.PermissionsEnum;

@Component({
  selector: 'jems-institutions-page',
  templateUrl: './institutions-page.component.html',
  styleUrls: ['./institutions-page.component.scss'],
  providers: [InstitutionsPageStore],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class InstitutionsPageComponent {
  Permissions = Permissions;

  @ViewChild('institutionNutsColumn', {static: true})
  institutionNutsColumn: TemplateRef<any>;

  institutionNuts: OutputNuts[];
  data$: Observable<{
    page: PageControllerInstitutionListDTO;
    tableConfiguration: TableConfiguration;
    createInstitutionPermission: boolean;
  }>;
  allNuts$: Observable<OutputNuts[]>;

  constructor(public institutionsPageStore: InstitutionsPageStore,
              private controllersPageSidenav: ControllersPageSidenavService,
              private nutsStore: NutsStore,
              private permissionService: PermissionService) {

    this.data$ = combineLatest([
      this.institutionsPageStore.controllerInstitutionPage$,
      this.permissionService.hasPermission(PermissionsEnum.InstitutionsUpdate),
      this.permissionService.hasPermission(PermissionsEnum.InstitutionsUnlimited)
    ])
      .pipe(
        map(([page, institutionUpdatePermission, institutionUnlimitedPermission]) => ({
          page,
          tableConfiguration: this.getTableConfig(),
          createInstitutionPermission: institutionUpdatePermission && institutionUnlimitedPermission
        })
        )
      );

    this.allNuts$ = this.nutsStore.getNuts();
  }

  private getTableConfig(): TableConfiguration {
    return new TableConfiguration({
      routerLink: '/app/controller/',
      isTableClickable: true,
      columns: [
        {
          displayedColumn: 'controllers.table.id',
          elementProperty: 'id',
          sortProperty: 'id',
          columnWidth: ColumnWidth.IdColumn
        },
        {
          displayedColumn: 'controllers.table.name',
          elementProperty: 'name',
          sortProperty: 'name',
          columnType: ColumnType.StringColumn,
          columnWidth: ColumnWidth.WideColumn
        },
        {
          displayedColumn: 'controllers.table.nuts',
          columnType: ColumnType.CustomComponent,
          customCellTemplate: this.institutionNutsColumn
        },
        {
          displayedColumn: 'controllers.table.create.date',
          elementProperty: 'createdAt',
          sortProperty: 'createdAt',
          columnType: ColumnType.DateColumn,
          columnWidth: ColumnWidth.DateColumn
        }
      ]
    });
  }

  getSelected(checkboxes: JemsRegionCheckbox[]): Map<string, JemsRegionCheckbox[]> {
    const selected = new Map<string, JemsRegionCheckbox[]>();
    checkboxes
      .filter(checkbox => checkbox.someChecked)
      .forEach(checkbox => {

        if (checkbox.checked) {
          selected.set(checkbox.code, []);
          return;
        }
        const children: JemsRegionCheckbox[] = [];
        this.collectSelectedGrouped(checkbox, children);
        selected.set(checkbox.code, children);
      });
    return selected;
  }

  private collectSelectedGrouped(checkbox: JemsRegionCheckbox, results: JemsRegionCheckbox[]): void {
    if (checkbox.allChildrenChecked() || (checkbox.code && checkbox.checked)) {
      results.push(checkbox);
      return;
    }
    checkbox.children.forEach(child => {
      this.collectSelectedGrouped(child, results);
    });
  }

  getSelectedRegionsForInstitution(selectedRegionsMap: Map<string, JemsRegionCheckbox[]>): string {
    const regionString: string[] = [];
    selectedRegionsMap.forEach((children, key) => {
          regionString.push(key);
          children.forEach(child => {
            regionString.push(child.code);
          });
    });

    return regionString.join(', ');
  }

  convertToJemsCheckBox(allNuts: OutputNuts[] | null, institutionNuts: OutputNuts[]) {
    return allNuts ?
       JemsRegionCheckbox.fromSelected(
        JemsRegionCheckbox.fromNuts(allNuts), JemsRegionCheckbox.fromNuts(institutionNuts)
      ) : [];
  }
}
