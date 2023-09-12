import {UntilDestroy, untilDestroyed} from '@ngneat/until-destroy';
import {ChangeDetectionStrategy, Component} from '@angular/core';
import {FormService} from '@common/components/section/form/form.service';
import {AbstractControl, FormArray, FormBuilder, Validators} from '@angular/forms';
import {BehaviorSubject, combineLatest, Observable, of} from 'rxjs';
import {ContractingPartnerBeneficialOwnerDTO,} from '@cat/api';
import {catchError, filter, map, switchMap, take, tap} from 'rxjs/operators';
import {ContractPartnerStore} from '@project/project-application/contracting/contract-partner/contract-partner.store';
import {ContractingSectionLockStore} from '@project/project-application/contracting/contracting-section-lock.store';
import {MatDialog} from '@angular/material/dialog';
import {Forms} from '@common/utils/forms';
import {APIError} from '@common/models/APIError';
import {Alert} from '@common/components/forms/alert';

@UntilDestroy()
@Component({
  selector: 'jems-contract-partner-beneficial-owner',
  templateUrl: './contract-partner-beneficial-owner.component.html',
  styleUrls: ['./contract-partner-beneficial-owner.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [FormService],
})
export class ContractPartnerBeneficialOwnerComponent {

  Alert = Alert;

  error$ = new BehaviorSubject<APIError | null>(null);

  private allColumns = ['firstName', 'lastName', 'birth', 'vatNumber', 'delete'];
  private readonlyColumns = this.allColumns.filter(col => col !== 'delete');
  displayedColumns: string[] = [];
  readonly MAX_NAME_LENGTH: number = 50;
  readonly MAX_VAT_LENGTH: number = 50;

  form = this.formBuilder.group({
    beneficialOwners: this.formBuilder.array([]),
  });

  tableData: AbstractControl[] = [];

  data$: Observable<{
    partnerId: number;
    beneficials: ContractingPartnerBeneficialOwnerDTO[];
    canEdit: boolean;
    canView: boolean;
    isPartnerLocked: boolean;
  }>;

  constructor(
    private contractPartnerStore: ContractPartnerStore,
    private formBuilder: FormBuilder,
    public formService: FormService,
    private contractingSectionLockStore: ContractingSectionLockStore,
    private dialog: MatDialog,
  ) {
    this.data$ = combineLatest([
      this.contractPartnerStore.partnerId$.pipe(filter(Boolean), map(Number)),
      this.contractPartnerStore.beneficialOwners$,
      this.contractPartnerStore.userCanEditContractPartner$,
      this.contractPartnerStore.userCanViewContractPartner$,
      this.contractPartnerStore.isPartnerLocked$,
    ]).pipe(
      map(([partnerId, beneficials, canEdit, canView, isPartnerLocked]) => ({
        partnerId, beneficials, canEdit, canView, isPartnerLocked
      })),
      tap(data => this.resetForm(data.beneficials, data.canEdit, data.isPartnerLocked)),
      tap(data => this.prepareVisibleColumns(data.canEdit, data.isPartnerLocked)),
    );
    this.formService.init(this.form);
  }

  private resetForm(beneficials: ContractingPartnerBeneficialOwnerDTO[], editable = false, isSectionLocked: boolean) {
    this.beneficials.clear();

    beneficials.forEach(b => this.addBeneficialOwner(b));
    this.formService.setEditable(editable && !isSectionLocked);
    this.formService.resetEditable();
    this.tableData = [...this.beneficials.controls];
  }

  private prepareVisibleColumns(isEditable: boolean, isPartnerLocked: boolean) {
    this.displayedColumns.splice(0);
    ((isEditable && !isPartnerLocked) ? this.allColumns : this.readonlyColumns).forEach(column => {
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
    this.tableData = [...this.beneficials.controls];
    this.formService.setDirty(true);
  }

  saveForm(partnerId: number) {
    this.contractPartnerStore.updateBeneficialOwners(this.convertFormToBeneficialOwnersDTOs(partnerId))
      .pipe(
        take(1),
        tap(() => this.formService.setSuccess('project.application.contract.partner.section.beneficial.owner.save.success')),
        catchError(error => this.formService.setError(error)),
      )
      .subscribe();
  }

  deleteBeneficialOwner(index: number) {
    this.beneficials.removeAt(index);
    this.tableData = [...this.beneficials.controls];
    this.formService.setDirty(true);
  }

  discardChanges(originalData: ContractingPartnerBeneficialOwnerDTO[], editable: boolean, isPartnerLocked: boolean) {
    this.resetForm(originalData, editable, isPartnerLocked);
  }

  get beneficials(): FormArray {
    return this.form.get('beneficialOwners') as FormArray;
  }

  lock(event: any) {
    Forms.confirm(
      this.dialog,
      {
        title: 'project.application.contract.partner.lock.dialog.header',
        message: {
          i18nKey: 'project.application.contract.partner.lock.dialog.message',
          i18nArguments: {name: ''}
        }
      }).pipe(
      take(1),
      filter(confirm => confirm),
      switchMap(() => this.contractingSectionLockStore.lockPartner()),
      catchError((error) => this.showErrorMessage(error.error)),
      untilDestroyed(this)
    ).subscribe();
  }

  unlock(event: any) {
    Forms.confirm(
      this.dialog,
      {
        title: 'project.application.contract.partner.unlock.dialog.header',
        message: {
          i18nKey: 'project.application.contract.partner.unlock.dialog.message',
          i18nArguments: {name: ''}
        }
      }).pipe(
      take(1),
      filter(confirm => confirm),
      switchMap(() => this.contractingSectionLockStore.unlockPartner()),
      catchError((error) => this.showErrorMessage(error.error)),
      untilDestroyed(this)
    ).subscribe();
  }

  private convertFormToBeneficialOwnersDTOs(partnerId: number): ContractingPartnerBeneficialOwnerDTO[] {
    const beneficialOwnerDTOs = [];
    for (const item of this.beneficials.controls) {
      beneficialOwnerDTOs.push({
        id: item.value.id,
        partnerId: partnerId,
        birth: item.value.birth,
        firstName: item.value.firstName,
        lastName: item.value.lastName,
        vatNumber: item.value.vatNumber
      } as ContractingPartnerBeneficialOwnerDTO);
    }
    return beneficialOwnerDTOs;
  }

  private showErrorMessage(error: APIError): Observable<null> {
    this.error$.next(error);
    setTimeout(() => {
      this.error$.next(null);
    }, 7000);
    return of(null);
  }
}
