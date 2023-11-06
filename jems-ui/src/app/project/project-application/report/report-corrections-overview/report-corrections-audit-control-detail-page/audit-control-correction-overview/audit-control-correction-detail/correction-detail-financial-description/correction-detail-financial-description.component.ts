import {ChangeDetectionStrategy, Component} from '@angular/core';
import {FormService} from '@common/components/section/form/form.service';
import {AbstractControl, FormArray, FormBuilder, FormGroup, Validators} from '@angular/forms';
import {
  AuditControlCorrectionDetailPageStore
} from '@project/project-application/report/report-corrections-overview/report-corrections-audit-control-detail-page/audit-control-correction-overview/audit-control-correction-detail/audit-control-correction-detail-page.store';
import {combineLatest, from, Observable, of} from 'rxjs';
import {tap, map, startWith, take, catchError} from 'rxjs/operators';
import {
  ProjectCorrectionFinancialDescriptionDTO, ProjectCorrectionFinancialDescriptionUpdateDTO
} from '@cat/api';
import {MatTableDataSource} from '@angular/material/table';
import CorrectionTypeEnum = ProjectCorrectionFinancialDescriptionDTO.CorrectionTypeEnum;
import {CustomTranslatePipe} from '@common/pipe/custom-translate-pipe';

@Component({
  selector: 'jems-correction-detail-financial-description',
  templateUrl: './correction-detail-financial-description.component.html',
  styleUrls: ['./correction-detail-financial-description.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [FormService]
})
export class CorrectionDetailFinancialDescriptionComponent {

  data$: Observable<{
    projectId: number;
    auditControlId: number;
    correctionId: number;
    financialDescription: ProjectCorrectionFinancialDescriptionDTO;
    canEdit: boolean;
    correctionTypeTranslations: Map<CorrectionTypeEnum, string>;
  }>;
  financialDescriptionForm: FormGroup;
  tableData: MatTableDataSource<AbstractControl> = new MatTableDataSource([]);
  displayedColumns: string[] = ['fundAmount', 'publicContribution', 'autoPublicContribution', 'privateContribution', 'total'];
  filteredCorrectionType: Observable<Map<CorrectionTypeEnum, string>>;
  correctionTypeTranslations$: Observable<Map<CorrectionTypeEnum, string>> = this.loadCorrectionTypeTranslations();
  correctionTypeTranslations = new Map<CorrectionTypeEnum, string>();

  constructor(
    private formBuilder: FormBuilder,
    private formService: FormService,
    private pageStore: AuditControlCorrectionDetailPageStore,
    private customTranslatePipe: CustomTranslatePipe
  ) {
    this.data$ = combineLatest([
      pageStore.projectId$,
      pageStore.auditControlId$,
      pageStore.correctionId$,
      pageStore.canEdit$,
      pageStore.financialDescription$,
      this.correctionTypeTranslations$,
    ]).pipe(
      map(([projectId, auditControlId, correctionId, canEdit, financialDescription, correctionTypeTranslations]) => ({
        projectId,
        auditControlId: Number(auditControlId),
        correctionId: Number(correctionId),
        canEdit,
        financialDescription,
        correctionTypeTranslations,
      })),
      tap(data => this.correctionTypeTranslations = data.correctionTypeTranslations),
      tap(data => this.initForm(data.canEdit)),
      tap(data => this.resetForm(data.financialDescription)),
    );
  }

  resetForm(financialDescription: ProjectCorrectionFinancialDescriptionDTO) {
    this.financialDescriptionForm.controls.deduction.setValue(financialDescription.deduction);
    this.amounts.at(0).get('fundAmount')?.setValue(financialDescription.fundAmount);
    this.amounts.at(0).get('publicContribution')?.setValue(financialDescription.publicContribution);
    this.amounts.at(0).get('autoPublicContribution')?.setValue(financialDescription.autoPublicContribution);
    this.amounts.at(0).get('privateContribution')?.setValue(financialDescription.privateContribution);
    this.financialDescriptionForm.controls.infoSentBeneficiaryDate.setValue(financialDescription.infoSentBeneficiaryDate);
    this.financialDescriptionForm.controls.infoSentBeneficiaryComment.setValue(financialDescription.infoSentBeneficiaryComment);
    this.financialDescriptionForm.controls.correctionTypeCode.setValue(financialDescription.correctionType);
    this.financialDescriptionForm.controls.correctionTypeText.setValue(this.correctionTypeTranslations.get(financialDescription.correctionType));
    this.financialDescriptionForm.controls.clericalTechnicalMistake.setValue(financialDescription.clericalTechnicalMistake);
    this.financialDescriptionForm.controls.goldPlating.setValue(financialDescription.goldPlating);
    this.financialDescriptionForm.controls.suspectedFraud.setValue(financialDescription.suspectedFraud);
    this.financialDescriptionForm.controls.correctionComment.setValue(financialDescription.correctionComment);
    this.tableData.data = this.amounts.controls;
  }

  private initForm(isEditable: boolean): void {
    this.financialDescriptionForm = this.formBuilder.group({
      deduction: [true, Validators.required],
      amounts: this.formBuilder.array([
        this.formBuilder.group({
          fundAmount: this.formBuilder.control( 0),
          publicContribution: this.formBuilder.control(0),
          autoPublicContribution: this.formBuilder.control(0),
          privateContribution: this.formBuilder.control(0),
        })
      ]),
      infoSentBeneficiaryDate: [''],
      infoSentBeneficiaryComment: ['', Validators.maxLength(2000)],
      correctionTypeCode: ['', Validators.required],
      correctionTypeText: ['', Validators.required],
      clericalTechnicalMistake: [false, Validators.required],
      goldPlating: [false, Validators.required],
      suspectedFraud: [false, Validators.required],
      correctionComment: ['', Validators.maxLength(2000)],
    });
    this.formService.init(this.financialDescriptionForm, of(isEditable));
  }

  getAmountSum(): number {
    return this.amounts.at(0).get('fundAmount')?.value +
      this.amounts.at(0).get('publicContribution')?.value +
      this.amounts.at(0).get('autoPublicContribution')?.value +
      this.amounts.at(0).get('privateContribution')?.value;
  }

  get amounts(): FormArray {
    return this.financialDescriptionForm.get('amounts') as FormArray;
  }

  initializeTypeDropdown() {
    this.filteredCorrectionType = this.financialDescriptionForm.controls.correctionTypeText.valueChanges
      .pipe(
        startWith(''),
        map(value => this.filter(value)),
      );
  }

  private filter(enteredValue: string): Map<CorrectionTypeEnum, string> {
    const filteredResults = new Map<CorrectionTypeEnum, string>();
    const filterValue = (enteredValue || '').toLowerCase();
    for (const [key, value] of this.correctionTypeTranslations.entries()) {
      if (value.toLowerCase().includes(filterValue)) {
        filteredResults.set(key, value);
      }
    }
    return filteredResults;
  }

  private static selectOptionClicked(event: FocusEvent): boolean {
    return !!event.relatedTarget && (event.relatedTarget as any).tagName === 'MAT-OPTION';
  }

  correctionTypeUnfocused(event: FocusEvent): void {
    if (CorrectionDetailFinancialDescriptionComponent.selectOptionClicked(event)) {
      return;
    }
    const validSelection = [...this.correctionTypeTranslations.values()].includes(this.financialDescriptionForm.controls.correctionTypeText.value);
    if (!validSelection) {
      this.financialDescriptionForm.controls.correctionTypeCode.patchValue('');
      this.financialDescriptionForm.controls.correctionTypeText.patchValue('');
    }
  }

  correctionTypeChanged(selectedCorrectionType: string): void {
    this.financialDescriptionForm.controls.correctionTypeCode.patchValue(selectedCorrectionType);
    this.financialDescriptionForm.controls.correctionTypeText.patchValue(this.correctionTypeTranslations.get(selectedCorrectionType as CorrectionTypeEnum));
  }

  loadCorrectionTypeTranslations(): Observable<Map<CorrectionTypeEnum, string>> {
    const correctionMap = new Map<CorrectionTypeEnum, string>();
    return from(Object.keys(CorrectionTypeEnum)).pipe(
      map(code => correctionMap.set(code as CorrectionTypeEnum, this.customTranslatePipe.transform('project.application.reporting.corrections.audit.control.detail.financial.description.type.' + code))),
    );
  }

  save(correctionId: number) {
    const data = {
      deduction: this.financialDescriptionForm.controls?.deduction.value,
      fundAmount: this.amounts.at(0).get('fundAmount')?.value,
      publicContribution: this.amounts.at(0).get('publicContribution')?.value,
      autoPublicContribution: this.amounts.at(0).get('autoPublicContribution')?.value,
      privateContribution: this.amounts.at(0).get('privateContribution')?.value,
      infoSentBeneficiaryDate: this.financialDescriptionForm.controls?.infoSentBeneficiaryDate.value,
      infoSentBeneficiaryComment: this.financialDescriptionForm.controls?.infoSentBeneficiaryComment.value,
      correctionType: this.financialDescriptionForm.controls?.correctionTypeCode.value,
      clericalTechnicalMistake: this.financialDescriptionForm.controls?.clericalTechnicalMistake.value,
      goldPlating: this.financialDescriptionForm.controls?.goldPlating.value,
      suspectedFraud: this.financialDescriptionForm.controls?.suspectedFraud.value,
      correctionComment: this.financialDescriptionForm.controls?.correctionComment.value,
    } as ProjectCorrectionFinancialDescriptionUpdateDTO;
    this.pageStore.saveFinancialDescription(correctionId, data)
      .pipe(
        take(1),
        tap(() => this.formService.setSuccess('project.application.reporting.corrections.update.correction.success')),
        catchError(err => this.formService.setError(err))
      ).subscribe();
  }

}
