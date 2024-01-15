import {ChangeDetectionStrategy, Component, QueryList, TemplateRef, ViewChild, ViewChildren} from '@angular/core';
import {combineLatest, Observable} from 'rxjs';
import {
  InstitutionPartnerAssignmentDTO,
  InstitutionPartnerDetailsDTO,
  OutputNuts,
  PageControllerInstitutionListDTO,
  UserRoleCreateDTO
} from '@cat/api';
import {TableConfiguration} from '@common/components/table/model/table.configuration';
import {ColumnWidth} from '@common/components/table/model/column-width';
import {ColumnType} from '@common/components/table/model/column-type.enum';
import {InstitutionsAssignmentsStoreService} from './institutions-assignments-store.service';
import {map, shareReplay, tap} from 'rxjs/operators';
import {PermissionService} from '../../security/permissions/permission.service';
import {InstitutionsPageStore} from '../institutions-page/institutions-page-store.service';
import {FormService} from '@common/components/section/form/form.service';
import {FormArray, FormBuilder} from '@angular/forms';
import {UntilDestroy, untilDestroyed} from '@ngneat/until-destroy';
import {MatSelect} from '@angular/material/select';
import PermissionsEnum = UserRoleCreateDTO.PermissionsEnum;
import {ControllersPageSidenavService} from "../controllers-page-sidenav.service";

@UntilDestroy()
@Component({
  selector: 'jems-assignment-page',
  templateUrl: './institutions-assignments-page.component.html',
  styleUrls: ['./institutions-assignments-page.component.scss'],
  providers: [FormService, InstitutionsAssignmentsStoreService, InstitutionsPageStore],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class InstitutionsAssignmentsPageComponent{
  Permissions = Permissions;

  @ViewChild('partnerNumberCell', {static: true})
  partnerNumberCell: TemplateRef<any>;

  @ViewChild('institutionNutsCell', {static: true})
  institutionNutsCell: TemplateRef<any>;

  @ViewChild('statusCell', {static: true})
  statusCell: TemplateRef<any>;

  @ViewChild('institutionDropdownCell', {static: true})
  institutionDropdownCell: TemplateRef<any>;

  @ViewChildren(MatSelect) institutionDropdowns: QueryList<MatSelect>;

  data$: Observable<{
    rows: InstitutionPartnerDetailsDTO[];
    totalElements: number;
    tableConfiguration: TableConfiguration;
    institutions: PageControllerInstitutionListDTO;
    editAssignmentPermission: boolean;
  }>;
  institutionAssignmentsChanges: {
    assignmentsToAdd: InstitutionPartnerAssignmentDTO[];
    assignmentsToRemove: InstitutionPartnerAssignmentDTO[];
  } = {assignmentsToAdd: [], assignmentsToRemove: []};

  filterData$: Observable<{
    partnerNuts: OutputNuts[];
  }>;

  filterForm = this.formBuilder.group({
    callId: '',
    projectId: '',
    acronym: '',
    partnerName: '',
    partnerNuts: [[]],
  });

  institutionsAssignmentForm = this.formBuilder.group({
    institutionsPartnerAssignmentToRemove: this.formBuilder.array([]),
    institutionsPartnerAssignmentToAssign: this.formBuilder.array([]),
    institutionsPartnersAssignments: this.formBuilder.array([]),
});

  institutionsTobeAdded: {partnerId: number; institutionId: number}[] = [];
  institutionsTobeDeleted: {partnerId: number; institutionId: number}[] = [];

  static fromNuts(nuts: OutputNuts[]): OutputNuts[] {
    if (!nuts || nuts.length == 0) {
      return [];
    }

    return nuts.map(area => this.fromRegion(null, area));
  }

  static fromRegion(parent: OutputNuts | null, region: OutputNuts): OutputNuts {
    const outputNuts = {} as OutputNuts;
    if (!region) {
      return outputNuts;
    }
    outputNuts.code = region.code;
    outputNuts.title = region.title;
    if (region.areas?.length) {
      outputNuts.areas = region.areas.map(area => this.fromRegion(outputNuts, area));
    }

    return outputNuts;
  }

  constructor(public institutionAssignmentStore: InstitutionsAssignmentsStoreService,
              private permissionService: PermissionService,
              private controllerInstitutionStore: InstitutionsPageStore,
              private formService: FormService,
              private formBuilder: FormBuilder,
              private controllersPageSidenavService: ControllersPageSidenavService
) {
    this.filterData$ = combineLatest([
      this.institutionAssignmentStore.nutsDefinedForCurrentUser$
    ]).pipe(
        map(([outputNuts]) => InstitutionsAssignmentsPageComponent.fromNuts(outputNuts)),
        map(outputNuts => ({ partnerNuts: outputNuts }))
    );

    this.filterForm.valueChanges.pipe(
        tap(filters => this.institutionAssignmentStore.filter$.next(filters)),
        untilDestroyed(this)
    ).subscribe();

    this.data$ = combineLatest([
      this.institutionAssignmentStore.controllerInstitutionAssignmentPage$,
      this.controllerInstitutionStore.controllerInstitutionPage$,
      this.permissionService.hasPermission(PermissionsEnum.InstitutionsAssignmentUpdate),
    ])
      .pipe(
        map(([page, institutionPage, institutionAssignmentUpdatePermission]) => ({
          rows: page.content.map((assignment, index) => ({
            ...assignment, index,
          })),
          totalElements: page.totalElements,
          tableConfiguration: this.getTableConfig(),
          institutions: institutionPage,
          editAssignmentPermission: institutionAssignmentUpdatePermission
          })
        ),
        tap(data => this.formService.setEditable(data.editAssignmentPermission)),
        tap(data => this.resetForm(data.rows, false)),
        shareReplay(1)
      );

    this.institutionsAssignmentForm.valueChanges.pipe(
      map(() =>   ( {
          assignmentsToAdd: this.institutionsPartnerAssignmentToAssign().value,
          assignmentsToRemove:  this.institutionsPartnerAssignmentToRemove().value
        })
      ),
      tap((changes: {
        assignmentsToAdd: InstitutionPartnerAssignmentDTO[];
        assignmentsToRemove: InstitutionPartnerAssignmentDTO[];
      }) => this.institutionAssignmentsChanges = changes),
      tap(changes => this.formService.setDirty(!!(changes.assignmentsToAdd.length || changes.assignmentsToRemove.length))),
      untilDestroyed(this),
    ).subscribe();

    this.formService.init(this.institutionsAssignmentForm);
  }

  private getTableConfig(): TableConfiguration {
    return new TableConfiguration({
      isTableClickable: false,
      columns: [
        {
          displayedColumn: 'controller.institutions.assignment.table.call.id.column.headline',
          elementProperty: 'callId',
          columnWidth: ColumnWidth.IdColumn,
          sortProperty: 'partner.project.call.id',
        },
        {
          displayedColumn: 'controller.institutions.assignment.table.project.id.column.headline',
          elementProperty: 'projectCustomIdentifier',
          columnWidth: ColumnWidth.ChipColumn,
          sortProperty: 'partner.project.id',
        },
        {
          displayedColumn: 'controller.institutions.assignment.table.acronym.column.headline',
          elementProperty: 'projectAcronym',
          columnType: ColumnType.StringColumn,
          columnWidth: ColumnWidth.WideColumn,
          sortProperty: 'projectAcronym',
        },
        {
          displayedColumn: 'controller.institutions.assignment.table.partner.no.column.headline',
          columnType: ColumnType.CustomComponent,
          customCellTemplate: this.partnerNumberCell,
          columnWidth: ColumnWidth.IdColumn
        },
        {
          displayedColumn: 'controller.institutions.assignment.table.status.column.headline',
          columnType: ColumnType.CustomComponent,
          columnWidth: ColumnWidth.ChipColumn,
          customCellTemplate: this.statusCell,
          sortProperty: 'partnerActive'
        },
        {
          displayedColumn: 'controller.institutions.assignment.table.partner.name.column.headline',
          elementProperty: 'partnerName',
          columnType: ColumnType.StringColumn,
          columnWidth: ColumnWidth.MediumColumn,
          sortProperty: 'partnerAbbreviation',
        },
        {
          displayedColumn: 'controller.institutions.assignment.table.partner.nuts.column.headline',
          columnType: ColumnType.CustomComponent,
          customCellTemplate: this.institutionNutsCell,
          columnWidth: ColumnWidth.MediumColumn,
          sortProperty: 'addressNuts3'
        },
        {
          displayedColumn: 'controller.institutions.assignment.table.institution.column.headline',
          columnType: ColumnType.CustomComponent,
          customCellTemplate: this.institutionDropdownCell,
          columnWidth: ColumnWidth.ChipColumn
        }
      ]
    });
  }

  resetForm(institutionsPartnerAssignments: InstitutionPartnerAssignmentDTO[], isDiscarded: boolean): void {
    if (isDiscarded) {
      this.institutionDropdowns.forEach((select, index) =>
        select.writeValue(institutionsPartnerAssignments[index].institutionId));
    }
    this.institutionsPartnerAssignmentToRemove().clear();
    this.institutionsPartnerAssignmentToAssign().clear();
    this.institutionsPartnersAssignmentsArray().clear();

    this.institutionsAssignmentForm.reset();
    this.institutionsTobeDeleted = [];
    this.institutionsTobeAdded = [];
    institutionsPartnerAssignments.forEach(institution => {
      this.institutionsPartnersAssignmentsArray().push(this.formBuilder.group({
        partnerId: this.formBuilder.control(institution.partnerId),
        institutionId: this.formBuilder.control(institution.institutionId),
        institutionsPartnerAssignmentToRemove: this.formBuilder.array([]),
        institutionsPartnerAssignmentToAssign: this.formBuilder.array([]),
      }));
    });
  }

  onInstitutionChange(index: number, newInstitution: any) {
    const initialValue = this.institutionsPartnersAssignmentsValue(index).institutionId;
    const newInstitutionId = newInstitution.value;

    if(initialValue !== newInstitutionId) {
      if(newInstitutionId !== null) {
        this.institutionsTobeAdded.push({partnerId: this.institutionsPartnersAssignmentsValue(index).partnerId, institutionId: newInstitutionId});
        this.institutionsPartnerAssignmentToAssign().push(this.formBuilder.control({
          partnerId: this.institutionsPartnersAssignmentsValue(index).partnerId,
          institutionId: newInstitution.value
        }));
      } else {
        this.institutionsTobeDeleted.push({partnerId: this.institutionsPartnersAssignmentsValue(index).partnerId, institutionId: newInstitutionId});

        this.institutionsPartnerAssignmentToRemove().push(this.formBuilder.control({
          partnerId: this.institutionsPartnersAssignmentsValue(index).partnerId,
          institutionId: initialValue
        }));
      }
    }
  }

  saveForm(): void {
    this.institutionAssignmentStore.updateControllerInstitutionAssignments(this.institutionAssignmentsChanges);
  }

  private institutionsPartnerAssignmentToRemove(): FormArray {
    return this.institutionsAssignmentForm.get('institutionsPartnerAssignmentToRemove') as FormArray;
  }

  private institutionsPartnerAssignmentToAssign(): FormArray {
    return this.institutionsAssignmentForm.get('institutionsPartnerAssignmentToAssign') as FormArray;
  }

  private institutionsPartnersAssignmentsArray(): FormArray {
    return this.institutionsAssignmentForm.get('institutionsPartnersAssignments') as FormArray;
  }

  private institutionsPartnersAssignmentsValue(index: number) {
    return this.institutionsAssignmentForm.get('institutionsPartnersAssignments')?.value.at(index);
  }

  isThereAnyActiveFilter(): boolean {
    return this.filterForm.value.callId?.length > 0 ||
        this.filterForm.value.projectId?.length > 0 ||
        this.filterForm.value.acronym?.length > 0 ||
        this.filterForm.value.partnerName?.length > 0 ||
        this.filterForm.value.partnerNuts?.length;
  }
}
