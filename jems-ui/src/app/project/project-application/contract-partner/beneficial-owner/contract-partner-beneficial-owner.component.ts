import {UntilDestroy} from '@ngneat/until-destroy';
import {ChangeDetectionStrategy, Component, Input} from '@angular/core';
import {FormService} from '@common/components/section/form/form.service';
import {MatTableDataSource} from '@angular/material/table';
import {AbstractControl, FormArray, FormBuilder, Validators} from '@angular/forms';
import {combineLatest, Observable} from 'rxjs';
import {
  ContractingPartnerBeneficialOwnerDTO,
} from '@cat/api';
import {ActivatedRoute} from '@angular/router';
import {catchError, map, take, tap} from 'rxjs/operators';
import {ContractPartnerStore} from '@project/project-application/contract-partner/contract-partner.store';

@UntilDestroy()
@Component({
  selector: 'jems-contract-partner-beneficial-owner',
  templateUrl: './contract-partner-beneficial-owner.component.html',
  styleUrls: ['./contract-partner-beneficial-owner.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [FormService],
})
export class ContractPartnerBeneficialOwnerComponent {

  @Input()
  private partnerId: number;
  private allColumns = ['firstName', 'lastName', 'birth', 'vatNumber', 'delete'];
  private readonlyColumns = this.allColumns.filter(col => col !== 'delete');
  displayedColumns: string[] = [];
  readonly MAX_NAME_LENGTH: number = 50;
  readonly MAX_VAT_LENGTH: number = 30;

  form = this.formBuilder.group({
    beneficialOwners: this.formBuilder.array([]),
  });

  dataSource: MatTableDataSource<AbstractControl> = new MatTableDataSource([]);

  data$: Observable<{
    beneficials: ContractingPartnerBeneficialOwnerDTO[];
    canEdit: boolean;
    canView: boolean;
  }>;

  constructor(
    private activatedRoute: ActivatedRoute,
    private contractPartnerStore: ContractPartnerStore,
    private formBuilder: FormBuilder,
    public formService: FormService,
  ) {
    this.data$ = combineLatest([
      this.contractPartnerStore.beneficialOwners$,
      this.contractPartnerStore.userCanEditContractPartner$,
      this.contractPartnerStore.userCanViewContractPartner$,
    ]).pipe(
      map(([beneficials, canEdit, canView]) => ({ beneficials, canEdit, canView })),
      tap(data => this.resetForm(data.beneficials, data.canEdit)),
      tap(data => this.prepareVisibleColumns(data.canEdit)),

    );
    this.formService.init(this.form);
  }

  private resetForm(beneficials: ContractingPartnerBeneficialOwnerDTO[], editable = false) {
    this.beneficials.clear();

    beneficials.forEach(b => this.addBeneficialOwner(b));
    this.formService.setEditable(editable);
    this.formService.resetEditable();
    this.dataSource.data = this.beneficials.controls;
  }

  private prepareVisibleColumns(isEditable: boolean) {
    this.displayedColumns.splice(0);
    (isEditable ? this.allColumns : this.readonlyColumns).forEach(column => {
      this.displayedColumns.push(column);
    });
  }

  addBeneficialOwner(beneficial: ContractingPartnerBeneficialOwnerDTO | null = null) {
    const item = this.formBuilder.group({
      id: this.formBuilder.control(beneficial?.id || 0),
      firstName: this.formBuilder.control(beneficial?.firstName || '', Validators.maxLength(this.MAX_NAME_LENGTH)),
      lastName: this.formBuilder.control(beneficial?.lastName || '', Validators.maxLength(this.MAX_NAME_LENGTH)),
      birth: this.formBuilder.control(beneficial?.birth || null),
      vatNumber: this.formBuilder.control(beneficial?.vatNumber || '',
        Validators.compose([Validators.required, Validators.maxLength(this.MAX_VAT_LENGTH)])
      ),
    });

    this.beneficials.push(item);
    this.dataSource.data = this.beneficials.controls;
    this.formService.setDirty(true);
  }

  saveForm() {
    this.contractPartnerStore.updateBeneficialOwners(this.convertFormToBeneficialOwnersDTOs())
      .pipe(
        take(1),
        tap(() => this.formService.setSuccess('project.application.contract.partner.section.beneficial.owner.save.success')),
        catchError(error => this.formService.setError(error)),
      )
      .subscribe();
  }

  deleteBeneficialOwner(index: number) {
    this.beneficials.removeAt(index);
    this.dataSource.data = this.beneficials.controls;
    this.formService.setDirty(true);
  }

  discardChanges(originalData: ContractingPartnerBeneficialOwnerDTO[]) {
    this.resetForm(originalData);
  }

  get beneficials(): FormArray {
    return this.form.get('beneficialOwners') as FormArray;
  }

  private convertFormToBeneficialOwnersDTOs(): ContractingPartnerBeneficialOwnerDTO[] {
    const beneficialOwnerDTOs = [];
    for (const item of this.beneficials.controls) {
        beneficialOwnerDTOs.push({
          id: item.value.id,
          partnerId: this.partnerId,
          birth: item.value.birth,
          firstName: item.value.firstName,
          lastName: item.value.lastName,
          vatNumber: item.value.vatNumber
        } as ContractingPartnerBeneficialOwnerDTO);
    }
    return beneficialOwnerDTOs;
  }
}
