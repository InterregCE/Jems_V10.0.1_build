import {ChangeDetectionStrategy, Component} from '@angular/core';
import {AbstractControl, FormArray, FormBuilder, FormGroup, Validators} from '@angular/forms';
import {
  ContractingPartnerStateAidGberDTO,
  ContractingPartnerStateAidGberSectionDTO,
  PartnerBudgetPerFundDTO,
  ProgrammeFundDTO,
  ProjectPartnerSummaryDTO
} from '@cat/api';
import {combineLatest, Observable} from 'rxjs';
import {ContractPartnerStore} from '@project/project-application/contracting/contract-partner/contract-partner.store';
import {FormService} from '@common/components/section/form/form.service';
import {catchError, map, startWith, take, tap} from 'rxjs/operators';
import {ContractMonitoringStore} from '@project/project-application/contracting/contract-monitoring/contract-monitoring-store';

@Component({
  selector: 'jems-gber-state-aid',
  templateUrl: './gber-state-aid.component.html',
  styleUrls: ['./gber-state-aid.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [FormService],
})
export class GberStateAidComponent {

  tableData: AbstractControl[] = [];
  displayedColumns = ['fund', 'coFinancing'];

  LocationInAssistedArea = ContractingPartnerStateAidGberDTO.LocationInAssistedAreaEnum;

  gberForm: FormGroup;

  aidIntensityErrors = {
    min: 'project.application.contract.partner.section.gber.aid.intensity.under.minimum'
  };

  minimumPercentage = 0;

  data$: Observable<{
    canEdit: boolean;
    partnerSummary: ProjectPartnerSummaryDTO;
    isPartnerLocked: boolean;
    gber: ContractingPartnerStateAidGberSectionDTO,
    fundList: ProgrammeFundDTO[];
  }>;

  constructor(
    private contractPartnerStore: ContractPartnerStore,
    private contractMonitoringStore: ContractMonitoringStore,
    private formBuilder: FormBuilder,
    public formService: FormService
  ) {
    this.formService.init(this.gberForm);
    this.data$ = combineLatest([
      this.contractMonitoringStore.canSetToContracted$,
      this.contractPartnerStore.partnerSummary$,
      this.contractPartnerStore.isPartnerLocked$,
      this.contractPartnerStore.GBER$,
    ]).pipe(
      map(([canEdit, partnerSummary, isPartnerLocked, gber]) => ({
        canEdit,
        partnerSummary,
        isPartnerLocked,
        gber,
        fundList: gber.partnerFunds.map(partnerFund => partnerFund.fund),
      })),
      tap(data => this.initForm(data)),
      tap(data => this.resetForm(data.gber))
    );
  }

  get funds(): FormArray {
    return this.gberForm.get('funds') as FormArray;
  }

  addFunds(funds: PartnerBudgetPerFundDTO[] | null) {
    funds?.forEach((fund) => {
      this.funds.push(this.formBuilder.group({
        id: this.formBuilder.control(fund?.fund.id || 0),
        fund: this.formBuilder.control(fund?.fund || null),
        percentageOfTotal: this.formBuilder.control(fund?.percentageOfTotal || 0),
        percentage: this.formBuilder.control(fund?.percentage || 0),
        value: this.formBuilder.control(fund?.value || 0),
      }));
    });
  }

  resetForm(gber: ContractingPartnerStateAidGberSectionDTO) {
    this.funds.clear();
    this.addFunds(gber.partnerFunds);
    this.tableData = [...this.funds.controls];
    this.gberForm.controls.dateOfGrantingAid.setValue(gber.dateOfGrantingAid);
    this.gberForm.controls.amountGrantingAid.setValue(gber.amountGrantingAid);
    this.gberForm.controls.aidIntensity.setValue(gber.aidIntensity === 0 ? this.minimumPercentage : gber.aidIntensity);
    this.gberForm.controls.sector.setValue(gber.naceGroupLevel);
    this.gberForm.controls.locationInAssistedArea.setValue(gber.locationInAssistedArea);
    this.gberForm.controls.comment.setValue(gber.comment);
    this.gberForm.controls.dateOfGrantingAid.disable();
    this.gberForm.controls.sector.disable();
    this.funds.disable();
  }

  saveForm(partnerId: number) {
    const gber = this.buildSaveEntity(partnerId)

    this.contractPartnerStore.updateGber(gber).pipe(
      take(1),
      tap(() => this.formService.setSuccess('project.application.contract.partner.section.gber.saved')),
      catchError(async (error) => this.formService.setError(error)),
    ).subscribe();
  }

  buildSaveEntity(partnerId: number): ContractingPartnerStateAidGberDTO {
    return {
      partnerId: partnerId,
      aidIntensity: this.gberForm.controls.aidIntensity.value,
      locationInAssistedArea: this.gberForm.controls.locationInAssistedArea.value,
      comment: this.gberForm.controls.comment.value,
      amountGrantingAid: this.gberForm.controls.amountGrantingAid.value
    } as ContractingPartnerStateAidGberDTO;
  }

  private initForm(data: any): void {
    this.minimumPercentage = Math.min(...data.gber.partnerFunds.map((fund: PartnerBudgetPerFundDTO) => fund.percentage));
    this.gberForm = this.formBuilder.group({
      dateOfGrantingAid: [''],
      amountGrantingAid: [''],
      aidIntensity: [0, Validators.min(this.minimumPercentage)],
      sector: [''],
      locationInAssistedArea: [''],
      comment: ['', Validators.maxLength(2000)],
      funds: this.formBuilder.array([]),
    });
    this.addFunds(data.gber.partnerFunds);
    this.formService.init(this.gberForm, new Observable<boolean>().pipe(startWith(data.canEdit && !data.isPartnerLocked)));
    this.gberForm.controls.dateOfGrantingAid.disable();
    this.gberForm.controls.sector.disable();
    this.funds.disable();
  }
}
