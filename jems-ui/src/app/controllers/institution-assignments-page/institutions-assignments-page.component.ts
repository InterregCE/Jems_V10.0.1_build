import {ChangeDetectionStrategy, Component, TemplateRef, ViewChild} from '@angular/core';
import {combineLatest, Observable} from 'rxjs';
import {
  InstitutionPartnerAssignmentDTO,
  InstitutionPartnerDetailsDTO,
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
import PermissionsEnum = UserRoleCreateDTO.PermissionsEnum;

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


  institutionsAssignmentForm = this.formBuilder.group({
    institutionsPartnerAssignmentToRemove: this.formBuilder.array([]),
    institutionsPartnerAssignmentToAssign: this.formBuilder.array([]),
    institutionsPartnersAssignments: this.formBuilder.array([]),
});

  institutionsTobeAdded: {partnerId: number; institutionId: number}[] = [];
  institutionsTobeDeleted: {partnerId: number; institutionId: number}[] = [];

  constructor(public institutionAssignmentStore: InstitutionsAssignmentsStoreService,
              private permissionService: PermissionService,
              private controllerInstitutionStore: InstitutionsPageStore,
              private formService: FormService,
              private formBuilder: FormBuilder,

) {
    this.data$ = combineLatest([
      this.institutionAssignmentStore.controllerInstitutionAssignmentPage$,
      this.controllerInstitutionStore.controllerInstitutionPage$,
      this.permissionService.hasPermission(PermissionsEnum.InstitutionsAssignmentUpdate),
    ])
      .pipe(
        map(([page, institutionPage, institutionAssignmentUpdatePermission]) => ({
          rows: page.content.map((project, index) => ({
            ...project,
            index,
          })),
          totalElements: page.totalElements,
          tableConfiguration: this.getTableConfig(),
          institutions: institutionPage,
          editAssignmentPermission: institutionAssignmentUpdatePermission
          })
        ),
        tap(data => this.formService.setEditable(data.editAssignmentPermission)),
        tap(data => this.resetForm(data.rows)),
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
          columnWidth: ColumnWidth.IdColumn
        },
        {
          displayedColumn: 'controller.institutions.assignment.table.project.id.column.headline',
          elementProperty: 'projectCustomIdentifier',
          columnWidth: ColumnWidth.IdColumn
        },
        {
          displayedColumn: 'controller.institutions.assignment.table.acronym.column.headline',
          elementProperty: 'projectAcronym',
          columnType: ColumnType.StringColumn,
          columnWidth: ColumnWidth.WideColumn
        },
        {
          displayedColumn: 'controller.institutions.assignment.table.partner.no.column.headline',
          columnType: ColumnType.CustomComponent,
          customCellTemplate: this.partnerNumberCell,
          columnWidth: ColumnWidth.MediumColumn
        },
        {
          displayedColumn: 'controller.institutions.assignment.table.status.column.headline',
          columnType: ColumnType.CustomComponent,
          columnWidth: ColumnWidth.ChipColumn,
          customCellTemplate: this.statusCell
        },
        {
          displayedColumn: 'controller.institutions.assignment.table.partner.name.column.headline',
          elementProperty: 'partnerName',
          columnType: ColumnType.StringColumn,
          columnWidth: ColumnWidth.MediumColumn
        },
        {
          displayedColumn: 'controller.institutions.assignment.table.partner.nuts.column.headline',
          columnType: ColumnType.CustomComponent,
          customCellTemplate: this.institutionNutsCell,
          columnWidth: ColumnWidth.MediumColumn
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

  resetForm(institutionsPartnerAssignments: InstitutionPartnerAssignmentDTO[]): void {
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

}
