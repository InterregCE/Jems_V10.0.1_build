import {
  ChangeDetectionStrategy,
  ChangeDetectorRef,
  Component,
  EventEmitter,
  Input,
  OnInit,
  Output
} from '@angular/core';
import {
  ContractingPartnerStateAidDeMinimisDTO,
  ContractingPartnerStateAidDeMinimisSectionDTO,
  MemberStateForGrantingDTO,
  OutputNuts,
  ProjectPartnerSummaryDTO
} from '@cat/api';
import {combineLatest, Observable} from 'rxjs';
import {AbstractControl, FormArray, FormBuilder, FormGroup, Validators} from '@angular/forms';
import {ActivatedRoute} from '@angular/router';
import {ContractPartnerStore} from '@project/project-application/contracting/contract-partner/contract-partner.store';
import {FormService} from '@common/components/section/form/form.service';
import {map, startWith, tap} from 'rxjs/operators';
import {NutsStore} from '@common/services/nuts.store';
import {
  ContractMonitoringStore
} from '@project/project-application/contracting/contract-monitoring/contract-monitoring-store';

@Component({
  selector: 'jems-demimis-state-aid',
  templateUrl: './demimis-state-aid.component.html',
  styleUrls: ['./demimis-state-aid.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [FormService],
})
export class DemimisStateAidComponent implements OnInit {

  @Input()
  deMinimis: ContractingPartnerStateAidDeMinimisSectionDTO;

  @Input()
  success$: Observable<any>;
  @Input()
  error$: Observable<any>;

  @Output()
  updateDeMinimis: EventEmitter<ContractingPartnerStateAidDeMinimisDTO> = new EventEmitter<ContractingPartnerStateAidDeMinimisDTO>();

  BaseForGrantingEnum = ContractingPartnerStateAidDeMinimisDTO.BaseForGrantingEnum;

  partnerId: number;
  projectId: number;
  tableData: AbstractControl[] = [];
  displayedColumns = ['selected', 'memberState', 'amount'];

  selectedCountry: OutputNuts | undefined;
  filteredCountry: Observable<string[]>;
  nuts: OutputNuts[];

  deMinimisForm: FormGroup;

  data$: Observable<{
    nuts: OutputNuts[];
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
    public formService: FormService,
    private nutsStore: NutsStore
  ) {
    this.data$ = combineLatest([
      this.nutsStore.getNuts(),
      this.contractMonitoringStore.canSetToContracted$,
      this.contractPartnerStore.partnerSummary$,
      this.contractPartnerStore.isPartnerLocked$,
    ]).pipe(
      map(([nuts, canEdit, partnerSummary, isPartnerLocked]) => ({
        nuts,
        canEdit,
        partnerSummary,
        isPartnerLocked
      })),
      tap(data =>
        this.nuts = this.deMinimis.memberStatesGranting.flatMap((state) => this.filterNuts(state.country, data.nuts))),
      tap(data => this.initForm(data)),
      tap(data => this.resetForm(data.canEdit, data.isPartnerLocked))
    );
  }

  get memberStates(): FormArray {
    return this.deMinimisForm.get('memberStates') as FormArray;
  }

  private static formatRegion(region: OutputNuts): string {
    return `${region.title} (${region.code})`;
  }

  private static selectOptionClicked(event: FocusEvent): boolean {
    return !!event.relatedTarget && (event.relatedTarget as any).tagName === 'MAT-OPTION';
  }

  ngOnInit(): void {
    this.success$
      .pipe(
        tap((data) => this.formService.setSuccess(data)),
      )
      .subscribe();

    this.error$
      .pipe(
        tap((data) => this.formService.setError(data)),
      )
      .subscribe();
  }

  addMemberStates(states: MemberStateForGrantingDTO[] | null) {
    states?.forEach((memberState) => {
      this.memberStates.push(this.formBuilder.group({
        partnerId: this.formBuilder.control(memberState?.partnerId || 0),
        memberCountry: this.formBuilder.control(memberState?.country || ''),
        countryCode: this.formBuilder.control(memberState?.countryCode || ''),
        amountInEur: this.formBuilder.control(memberState?.amountInEur || 0),
        selected: this.formBuilder.control(memberState?.selected || false),
      }));
    });
  }

  initializeFilters(): void {
    this.selectedCountry = this.findByName(this.deMinimisForm.controls.country.value, this.nuts);
    this.filteredCountry = this.deMinimisForm.controls.country.valueChanges
      .pipe(
        startWith(''),
        map(value => this.filter(value, this.nuts))
      );
  }

  resetForm(canEdit: boolean, isPartnerLocked: boolean) {
    this.memberStates.clear();
    this.addMemberStates(this.deMinimis.memberStatesGranting);
    this.tableData = [...this.memberStates.controls];
    this.deMinimisForm.controls.dateOfGrantingAid.setValue(this.deMinimis.dateOfGrantingAid);
    this.deMinimisForm.controls.amountGrantingAid.setValue(this.deMinimis.amountGrantingAid);
    this.deMinimisForm.controls.selfDeclarationSubmissionDate.setValue(this.deMinimis.selfDeclarationSubmissionDate);
    this.deMinimisForm.controls.aidGrantedOnBasis.setValue(this.deMinimis.baseForGranting);
    this.deMinimisForm.controls.country.setValue(this.deMinimis.aidGrantedByCountry);
    this.deMinimisForm.controls.comment.setValue(this.deMinimis.comment);
    this.deMinimisForm.controls.dateOfGrantingAid.disable();
    this.memberStates.controls.forEach((memberState: any) => memberState.controls.memberCountry.disable());
    if (!canEdit || isPartnerLocked) {
      this.memberStates.disable();
    }
    setTimeout(() => this.changeDetectorRef.detectChanges());
  }

  saveForm() {
    this.updateDeMinimis.emit(this.buildSaveEntity());
  }

  buildSaveEntity(): ContractingPartnerStateAidDeMinimisDTO {
    return {
      partnerId: this.deMinimis.partnerId,
      selfDeclarationSubmissionDate: this.deMinimisForm.controls.selfDeclarationSubmissionDate.value,
      baseForGranting: this.deMinimisForm.controls.aidGrantedOnBasis.value,
      aidGrantedByCountry: this.deMinimisForm.controls.country.value,
      comment: this.deMinimisForm.controls.comment.value,
      memberStatesGranting: this.buildMemberStatesSaveData(),
      amountGrantingAid: this.deMinimisForm.controls.amountGrantingAid.value
    } as ContractingPartnerStateAidDeMinimisDTO;
  }

  buildMemberStatesSaveData(): MemberStateForGrantingDTO[] {
    return this.deMinimisForm.getRawValue().memberStates.map((memberState: any) =>
      ({
        partnerId: memberState.partnerId,
        countryCode: memberState.countryCode,
        country: memberState.memberCountry,
        selected: memberState.selected,
        amountInEur: memberState.amountInEur
      } as MemberStateForGrantingDTO)
    );
  }

  setCheckedStatus(memberStateIndex: number, checked: boolean): void {
    this.memberStates.at(memberStateIndex).get('selected')?.patchValue(checked);
    this.deMinimisForm.updateValueAndValidity();
    this.formService.setDirty(true);
  }

  private initForm(data: any): void {
    this.deMinimisForm = this.formBuilder.group({
      dateOfGrantingAid: [''],
      amountGrantingAid: [''],
      selfDeclarationSubmissionDate: [''],
      aidGrantedOnBasis: [''],
      country: ['', Validators.maxLength(250)],
      comment: ['', Validators.maxLength(2000)],
      memberStates: this.formBuilder.array([]),
    });
    this.addMemberStates(this.deMinimis.memberStatesGranting);
    this.formService.init(this.deMinimisForm, new Observable<boolean>().pipe(startWith(data.canEdit && !data.isPartnerLocked)));
    this.deMinimisForm.controls.dateOfGrantingAid.disable();
    this.memberStates.controls.forEach((memberState: any) => memberState.controls.memberCountry.disable());
  }

  private findByName(value: string, nuts: OutputNuts[]): OutputNuts | undefined {
    return nuts.find(nut => value === DemimisStateAidComponent.formatRegion(nut));
  }

  private filter(value: string, nuts: OutputNuts[]): string[] {
    const filterValue = (value || '').toLowerCase();
    return nuts
      .filter(nut => DemimisStateAidComponent.formatRegion(nut).toLowerCase().includes(filterValue))
      .map(nut => DemimisStateAidComponent.formatRegion(nut));
  }

  private filterNuts(value: string, nuts: OutputNuts[]): OutputNuts[] {
    const filterValue = (value || '').toLowerCase();
    return nuts
      .filter(nut => DemimisStateAidComponent.formatRegion(nut).toLowerCase().includes(filterValue));
  }
}
