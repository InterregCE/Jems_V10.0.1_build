import {ChangeDetectionStrategy, Component} from '@angular/core';
import {
  ContractingPartnerStateAidDeMinimisDTO,
  ContractingPartnerStateAidDeMinimisSectionDTO,
  MemberStateForGrantingDTO,
  OutputNuts,
  ProjectPartnerSummaryDTO
} from '@cat/api';
import {combineLatest, Observable} from 'rxjs';
import {AbstractControl, FormArray, FormBuilder, FormGroup, Validators} from '@angular/forms';
import {ContractPartnerStore} from '@project/project-application/contracting/contract-partner/contract-partner.store';
import {FormService} from '@common/components/section/form/form.service';
import {catchError, map, startWith, take, tap} from 'rxjs/operators';
import {NutsStore} from '@common/services/nuts.store';
import {ContractMonitoringStore} from '@project/project-application/contracting/contract-monitoring/contract-monitoring-store';

@Component({
  selector: 'jems-demimis-state-aid',
  templateUrl: './demimis-state-aid.component.html',
  styleUrls: ['./demimis-state-aid.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [FormService],
})
export class DemimisStateAidComponent {

  BaseForGrantingEnum = ContractingPartnerStateAidDeMinimisDTO.BaseForGrantingEnum;

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
    deMinimis: ContractingPartnerStateAidDeMinimisSectionDTO;
  }>;

  constructor(
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
      this.contractPartnerStore.deMinimis$,
    ]).pipe(
      map(([nuts, canEdit, partnerSummary, isPartnerLocked, deMinimis]) => ({
        nuts,
        canEdit,
        partnerSummary,
        isPartnerLocked,
        deMinimis
      })),
      tap(data =>
        this.nuts = data.deMinimis.memberStatesGranting.flatMap((state: MemberStateForGrantingDTO) => this.filterNuts(state.country, data.nuts))),
      tap(data => this.initForm(data)),
      tap(data => this.resetForm(data.canEdit, data.isPartnerLocked, data.deMinimis))
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

  resetForm(canEdit: boolean, isPartnerLocked: boolean, deMinimis: ContractingPartnerStateAidDeMinimisSectionDTO) {
    this.memberStates.clear();
    this.addMemberStates(deMinimis.memberStatesGranting);
    this.tableData = [...this.memberStates.controls];
    this.deMinimisForm.controls.dateOfGrantingAid.setValue(deMinimis.dateOfGrantingAid);
    this.deMinimisForm.controls.amountGrantingAid.setValue(deMinimis.amountGrantingAid);
    this.deMinimisForm.controls.selfDeclarationSubmissionDate.setValue(deMinimis.selfDeclarationSubmissionDate);
    this.deMinimisForm.controls.aidGrantedOnBasis.setValue(deMinimis.baseForGranting);
    this.deMinimisForm.controls.country.setValue(deMinimis.aidGrantedByCountry);
    this.deMinimisForm.controls.comment.setValue(deMinimis.comment);
    this.deMinimisForm.controls.dateOfGrantingAid.disable();
    this.memberStates.controls.forEach((memberState: any) => memberState.controls.memberCountry.disable());
    if (!canEdit || isPartnerLocked) {
      this.memberStates.disable();
    }
  }

  saveForm(partnerId: number) {
    const deMinimis = this.buildSaveEntity(partnerId);

    this.contractPartnerStore.updateDeMinimis(deMinimis).pipe(
      take(1),
      tap(() => this.formService.setSuccess('project.application.contract.monitoring.project.de.minimis.saved')),
      catchError(async (error) => this.formService.setError(error)),
    ).subscribe();
  }

  buildSaveEntity(partnerId: number): ContractingPartnerStateAidDeMinimisDTO {
    return {
      partnerId: partnerId,
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

  getTooltipWithLength(content: string, maxLength: number): string {
    return `${content} (${ content.length}/${maxLength})`;
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
    this.addMemberStates(data.deMinimis.memberStatesGranting);
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
