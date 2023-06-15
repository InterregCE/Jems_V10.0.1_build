import {
  ChangeDetectionStrategy,
  ChangeDetectorRef,
  Component,
  EventEmitter,
  Input,
  OnInit,
  Output
} from '@angular/core';
import {AbstractControl, FormArray, FormBuilder, FormGroup, Validators} from '@angular/forms';
import {
  ContractingPartnerStateAidGberDTO,
  ContractingPartnerStateAidGberSectionDTO,
  PartnerBudgetPerFundDTO,
  ProgrammeFundDTO,
  ProjectPartnerSummaryDTO
} from '@cat/api';
import {combineLatest, Observable} from 'rxjs';
import {ActivatedRoute} from '@angular/router';
import {ContractPartnerStore} from '@project/project-application/contracting/contract-partner/contract-partner.store';
import {FormService} from '@common/components/section/form/form.service';
import {map, startWith, take, tap} from 'rxjs/operators';
import {
  ContractMonitoringStore
} from '@project/project-application/contracting/contract-monitoring/contract-monitoring-store';

@Component({
  selector: 'jems-gber-state-aid',
  templateUrl: './gber-state-aid.component.html',
  styleUrls: ['./gber-state-aid.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [FormService],
})
export class GberStateAidComponent implements OnInit {
  @Input()
  gber: ContractingPartnerStateAidGberSectionDTO;

  @Input()
  success$: Observable<any>;
  @Input()
  error$: Observable<any>;

  @Output()
  updateGber: EventEmitter<ContractingPartnerStateAidGberDTO> = new EventEmitter<ContractingPartnerStateAidGberDTO>();

  partnerId: number;
  projectId: number;
  tableData: AbstractControl[] = [];
  displayedColumns = ['fund', 'coFinancing'];

  LocationInAssistedArea = ContractingPartnerStateAidGberDTO.LocationInAssistedAreaEnum;

  gberForm: FormGroup;
  fundList: ProgrammeFundDTO[];

  aidIntensityErrors = {
    min: 'project.application.contract.partner.section.gber.aid.intensity.under.minimum'
  };

  minimumPercentage = 0;

  data$: Observable<{
    canEdit: boolean;
    partnerSummary: ProjectPartnerSummaryDTO;
    isPartnerLocked: boolean;
  }>;

  constructor(
    private activatedRoute: ActivatedRoute,
    protected changeDetectorRef: ChangeDetectorRef,
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
    ]).pipe(
      map(([canEdit, partnerSummary, isPartnerLocked]) => ({
        canEdit,
        partnerSummary,
        isPartnerLocked
      })),
      tap(data => this.fundList = this.gber.partnerFunds.map(fund => fund.fund)),
      tap(data => this.initForm(data)),
      tap(data => this.resetForm())
    );
  }

  get funds(): FormArray {
    return this.gberForm.get('funds') as FormArray;
  }

  ngOnInit(): void {
    this.success$
      .pipe(
        take(1),
        tap((data) => this.formService.setSuccess(data)),
      )
      .subscribe();

    this.error$
      .pipe(
        take(1),
        tap((data) => this.formService.setError(data)),
      )
      .subscribe();
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

  resetForm() {
    this.funds.clear();
    this.addFunds(this.gber.partnerFunds);
    this.tableData = [...this.funds.controls];
    this.gberForm.controls.dateOfGrantingAid.setValue(this.gber.dateOfGrantingAid);
    this.gberForm.controls.amountGrantedAid.setValue(this.gber.totalEligibleBudget);
    this.gberForm.controls.aidIntensity.setValue(this.gber.aidIntensity === 0 ? this.minimumPercentage : this.gber.aidIntensity);
    this.gberForm.controls.sector.setValue(this.gber.naceGroupLevel);
    this.gberForm.controls.locationInAssistedArea.setValue(this.gber.locationInAssistedArea);
    this.gberForm.controls.comment.setValue(this.gber.comment);
    this.gberForm.controls.dateOfGrantingAid.disable();
    this.gberForm.controls.amountGrantedAid.disable();
    this.gberForm.controls.sector.disable();
    this.funds.disable();
    setTimeout(() => this.changeDetectorRef.detectChanges());
  }

  saveForm() {
    this.updateGber.emit(this.buildSaveEntity());
  }

  buildSaveEntity(): ContractingPartnerStateAidGberDTO {
    return {
      partnerId: this.gber.partnerId,
      aidIntensity: this.gberForm.controls.aidIntensity.value,
      locationInAssistedArea: this.gberForm.controls.locationInAssistedArea.value,
      comment: this.gberForm.controls.comment.value,
    } as ContractingPartnerStateAidGberDTO;
  }

  private initForm(data: any): void {

    this.minimumPercentage = Math.min(...this.gber.partnerFunds.map(fund => fund.percentage));
    this.gberForm = this.formBuilder.group({
      dateOfGrantingAid: [''],
      amountGrantedAid: [''],
      aidIntensity: [0, Validators.min(this.minimumPercentage)],
      sector: [''],
      locationInAssistedArea: [''],
      comment: ['', Validators.maxLength(2000)],
      funds: this.formBuilder.array([]),
    });
    this.addFunds(this.gber.partnerFunds);
    this.formService.init(this.gberForm, new Observable<boolean>().pipe(startWith(data.canEdit && !data.isPartnerLocked)));
    this.gberForm.controls.dateOfGrantingAid.disable();
    this.gberForm.controls.amountGrantedAid.disable();
    this.gberForm.controls.sector.disable();
    this.funds.disable();
  }
}
