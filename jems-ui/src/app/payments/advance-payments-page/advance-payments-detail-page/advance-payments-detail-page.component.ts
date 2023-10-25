import {ChangeDetectionStrategy, ChangeDetectorRef, Component, OnInit} from '@angular/core';
import {combineLatest, Observable, of, Subject} from 'rxjs';
import {
  AdvancePaymentDetailDTO,
  AdvancePaymentSettlementDTO,
  AdvancePaymentStatusUpdateDTO,
  AdvancePaymentUpdateDTO,
  OutputProjectSimple,
  ProjectPartnerPaymentSummaryDTO,
  ProjectPartnerSummaryDTO,
  UserDTO
} from '@cat/api';
import {ActivatedRoute} from '@angular/router';
import {AbstractControl, FormArray, FormBuilder, FormGroup, Validators} from '@angular/forms';
import {catchError, debounceTime, filter, map, shareReplay, startWith, take, tap} from 'rxjs/operators';
import {animate, state, style, transition, trigger} from '@angular/animations';
import {FormService} from '@common/components/section/form/form.service';
import {SecurityService} from '../../../security/security.service';
import {UntilDestroy, untilDestroyed} from '@ngneat/until-destroy';
import {LocaleDatePipe} from '@common/pipe/locale-date.pipe';
import {Alert} from '@common/components/forms/alert';
import {AdvancePaymentsDetailPageStoreStore} from './advance-payments-detail-page-store.store';
import {AdvancePaymentsDetailPageConstants} from './advance-payments-detail-page.constants';
import {RoutingService} from '@common/services/routing.service';
import {APIError} from '@common/models/APIError';
import {TranslateService} from '@ngx-translate/core';
import {NumberService} from '@common/services/number.service';

@UntilDestroy()
@Component({
  selector: 'jems-advance-payments-detail-page',
  templateUrl: './advance-payments-detail-page.component.html',
  styleUrls: ['./advance-payments-detail-page.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush,
  animations: [
    trigger('detailExpand', [
      state('collapsed', style({height: '0px', minHeight: '0'})),
      state('expanded', style({height: '*'})),
      transition('expanded <=> collapsed', animate('225ms cubic-bezier(0.4, 0.0, 0.2, 1)')),
    ]),
  ],
  providers: [FormService]
})
export class AdvancePaymentsDetailPageComponent implements OnInit {
  Alert = Alert;

  settlementsExpanded = true;
  constants = AdvancePaymentsDetailPageConstants;
  tableData: AbstractControl[] = [];
  paymentId = this.activatedRoute.snapshot.params.advancePaymentId;
  columnsToDisplay = ['partner', 'partnerName', 'amountApproved', 'addInstallment'];

  advancePaymentForm = this.formBuilder.group({
    id: '',
    projectCustomIdentifier: this.formBuilder.control('', [Validators.required, AdvancePaymentsDetailPageComponent.requireMatch]),
    projectAcronym: this.formBuilder.control(''),
    partnerAbbreviation: this.formBuilder.control(''),
    selectedPartner: this.formBuilder.control(''),
    partnerType: this.formBuilder.control(''),
    partnerNumber: this.formBuilder.control(''),
    sourceOrFundName: this.formBuilder.control(''),
    programmeFundId: this.formBuilder.control(''),
    partnerContributionId: this.formBuilder.control(''),
    partnerContributionSpfId: this.formBuilder.control(''),
    amountPaid: this.formBuilder.control(''),
    paymentDate: this.formBuilder.control(''),
    comment: this.formBuilder.control(''),
    paymentAuthorized: this.formBuilder.control(''),
    paymentAuthorizedUser: this.formBuilder.control(''),
    paymentAuthorizedDate: this.formBuilder.control(''),
    paymentConfirmed: this.formBuilder.control(''),
    paymentConfirmedUser: this.formBuilder.control(''),
    paymentConfirmedDate: this.formBuilder.control(''),
    paymentSettlements: this.formBuilder.array([]),
  });
  initialAdvancePaymentDetail: AdvancePaymentDetailDTO;
  currentUserDetails: UserDTO;
  data$: Observable<{
    paymentDetail: AdvancePaymentDetailDTO;
    currentUser: UserDTO;
    userCanEdit: boolean;
  }>;
  contractedProjects$ = new Subject<OutputProjectSimple[]>();
  partnerData$: Observable<ProjectPartnerPaymentSummaryDTO[]>;
  fundsAndContributions: ProjectPartnerPaymentSummaryDTO | null;
  userCanEdit$: Observable<boolean>;

  constructor(private activatedRoute: ActivatedRoute,
              private formBuilder: FormBuilder,
              public formService: FormService,
              private advancePaymentsDetailPageStore: AdvancePaymentsDetailPageStoreStore,
              private securityService: SecurityService,
              private localeDatePipe: LocaleDatePipe,
              private router: RoutingService,
              private translateService: TranslateService,
              private changeDetectorRef: ChangeDetectorRef) {
    this.formService.init(this.advancePaymentForm, of(true));
    this.setContractedProjects();
    this.partnerData$ = this.advancePaymentsDetailPageStore.getPartnerData();
    this.advancePaymentForm.get(this.constants.FORM_CONTROL_NAMES.projectCustomIdentifier)?.valueChanges
      .pipe(
        debounceTime(150),
        map((searchTerm) => typeof searchTerm === 'string' ? searchTerm : searchTerm.customIdentifier),
        tap((searchedAcronym) => this.advancePaymentsDetailPageStore.searchProjectsByName$.next(searchedAcronym)),
        untilDestroyed(this)
      ).subscribe();

    this.advancePayment.get(this.constants.FORM_CONTROL_NAMES.sourceOrFundName)?.valueChanges
      .pipe(
        tap(selection => this.setSourceForAdvance(selection)),
        untilDestroyed(this)
      ).subscribe();
  }

  ngOnInit(): void {
    this.data$ = combineLatest([
      this.advancePaymentsDetailPageStore.advancePaymentDetail$,
      this.securityService.currentUserDetails,
      this.advancePaymentsDetailPageStore.userCanEdit$
    ])
      .pipe(
        map(([paymentDetail, currentUser, userCanEdit]: any) => ({
          paymentDetail,
          currentUser,
          userCanEdit
        })),
        tap((data) => this.initialAdvancePaymentDetail = data.paymentDetail),
        tap(data => this.currentUserDetails = data.currentUser),
        tap(data => this.resetForm(data.paymentDetail, data.userCanEdit)),
      );
  }

  resetSourceForAdvance() {
    this.advancePaymentForm.get(this.constants.FORM_CONTROL_NAMES.programmeFundId)?.setValue('');
    this.advancePaymentForm.get(this.constants.FORM_CONTROL_NAMES.partnerContributionId)?.setValue('');
  }

  setContractedProjects() {
    this.advancePaymentsDetailPageStore.getContractedProjects()
      .pipe(
        startWith([]),
        untilDestroyed(this)
      )
      .subscribe(this.contractedProjects$);
  }

  setSourceForAdvance(selection: any) {
    if (selection) {
      this.resetSourceForAdvance();
      switch (selection.type) {
        case 'fund':
          this.advancePaymentForm.get(this.constants.FORM_CONTROL_NAMES.programmeFundId)?.setValue(selection.data?.id);
          break;
        case 'contribution':
          this.advancePaymentForm.get(this.constants.FORM_CONTROL_NAMES.partnerContributionId)?.setValue(selection.data?.id);
          break;
        case 'spfContribution':
          this.advancePaymentForm.get(this.constants.FORM_CONTROL_NAMES.partnerContributionSpfId)?.setValue(selection.data?.id);
          break;
      }
    }
  }

  getSourceValue(paymentDetail: AdvancePaymentDetailDTO, partnerData: ProjectPartnerPaymentSummaryDTO): any | undefined {
    if (partnerData) {
      if (paymentDetail?.programmeFund?.id) {
        return {
          type: 'fund',
          data: partnerData.partnerCoFinancing.find(fund => fund.id === paymentDetail.programmeFund.id)
        };
      } else if (paymentDetail?.partnerContribution?.id) {
        return {
          type: 'contribution',
          data: partnerData.partnerContributions.find(contribution => contribution.id === paymentDetail.partnerContribution.id)
        };
      } else if (paymentDetail?.partnerContributionSpf?.id) {
        return {
          type: 'spfContribution',
          data: partnerData.partnerContributionsSpf.find(contribution => contribution.id === paymentDetail.partnerContributionSpf.id)
        };
      }
    }
    return undefined;
  }

  resetForm(paymentDetail: AdvancePaymentDetailDTO, userCanEdit: boolean) {
    this.advancePaymentForm.get(this.constants.FORM_CONTROL_NAMES.id)?.setValue(this.paymentId ? this.paymentId : null);
    this.advancePaymentForm.get(this.constants.FORM_CONTROL_NAMES.projectCustomIdentifier)?.setValue('');
    this.advancePaymentForm.get(this.constants.FORM_CONTROL_NAMES.partnerAbbreviation)?.setValue('');
    this.advancePaymentForm.get(this.constants.FORM_CONTROL_NAMES.sourceOrFundName)?.setValue('');

    this.setSelectedProject(paymentDetail.projectCustomIdentifier);
    this.setSelectedPartner(paymentDetail);
    this.setFoundOrContribution(paymentDetail);

    this.advancePaymentForm.get(this.constants.FORM_CONTROL_NAMES.projectAcronym)?.setValue(paymentDetail?.projectAcronym ?? '');
    this.advancePaymentForm.get(this.constants.FORM_CONTROL_NAMES.partnerNumber)?.setValue(paymentDetail?.partnerNumber ?? '');
    this.advancePaymentForm.get(this.constants.FORM_CONTROL_NAMES.partnerType)?.setValue(paymentDetail.partnerType ?? '');
    this.advancePaymentForm.get(this.constants.FORM_CONTROL_NAMES.programmeFundId)?.setValue(paymentDetail.programmeFund?.id ?? '');
    this.advancePaymentForm.get(this.constants.FORM_CONTROL_NAMES.partnerContributionId)?.setValue(paymentDetail.partnerContribution?.id ?? '');
    this.advancePaymentForm.get(this.constants.FORM_CONTROL_NAMES.partnerContributionSpfId)?.setValue(paymentDetail.partnerContributionSpf?.id ?? '');
    this.advancePaymentForm.get(this.constants.FORM_CONTROL_NAMES.amountPaid)?.setValue(paymentDetail.amountPaid ?? 0);
    this.advancePaymentForm.get(this.constants.FORM_CONTROL_NAMES.paymentDate)?.setValue(paymentDetail.paymentDate);
    this.advancePaymentForm.get(this.constants.FORM_CONTROL_NAMES.comment)?.setValue(paymentDetail?.comment ?? '');
    this.advancePaymentForm.get(this.constants.FORM_CONTROL_NAMES.paymentAuthorized)?.setValue(paymentDetail.paymentAuthorized);
    this.advancePaymentForm.get(this.constants.FORM_CONTROL_NAMES.paymentAuthorizedUser)?.setValue(paymentDetail?.paymentAuthorizedUser);
    this.advancePaymentForm.get(this.constants.FORM_CONTROL_NAMES.paymentAuthorizedDate)?.setValue(paymentDetail?.paymentAuthorizedDate);
    this.advancePaymentForm.get(this.constants.FORM_CONTROL_NAMES.paymentConfirmed)?.setValue(paymentDetail.paymentConfirmed);
    this.advancePaymentForm.get(this.constants.FORM_CONTROL_NAMES.paymentConfirmedUser)?.setValue(paymentDetail?.paymentConfirmedUser);
    this.advancePaymentForm.get(this.constants.FORM_CONTROL_NAMES.paymentConfirmedDate)?.setValue(paymentDetail?.paymentConfirmedDate);
    this.settlementsArray.clear();
    paymentDetail.paymentSettlements?.forEach(settlement => {
        this.settlementsArray.push(
          this.formBuilder.group({
              id: settlement.id,
              number: settlement.number,
              amountSettled: settlement.amountSettled,
              settlementDate: settlement.settlementDate,
              comment: settlement.comment
            }
          ));
      }
    );

    this.setValidators();
    this.disableFieldsIfPaymentIsAuthorized();
    this.disableFieldsIfPaymentIsConfirmed();
    this.disableFieldsIfProjectNotSelected(paymentDetail);
    this.disableAuthorizationCheckbox(paymentDetail);
    this.disableConfirmationCheckbox(paymentDetail.paymentSettlements);

    this.disableAllFields(userCanEdit);
  }

  disableAllFields(userCanEdit: boolean) {
    if (!userCanEdit) {
      this.advancePaymentForm.disable();
      this.settlementsArray.controls.forEach(control => control.disable());
    }
  }

  setValidators() {
    this.advancePaymentForm.get(this.constants.FORM_CONTROL_NAMES.sourceOrFundName)?.setValidators([Validators.required]);
    this.advancePaymentForm.get(this.constants.FORM_CONTROL_NAMES.partnerAbbreviation)?.setValidators([Validators.required]);
    this.advancePaymentForm.get(this.constants.FORM_CONTROL_NAMES.projectCustomIdentifier)?.setValidators([Validators.required, AdvancePaymentsDetailPageComponent.requireMatch]);
    this.advancePaymentForm.get(this.constants.FORM_CONTROL_NAMES.comment)?.setValidators([Validators.maxLength(500)]);
    this.advancePaymentForm.get(this.constants.FORM_CONTROL_NAMES.amountPaid)?.setValidators([Validators.required, Validators.min(0.01)]);
    this.settlementsArray.controls.forEach(control => {
        control.get(this.constants.FORM_CONTROL_NAMES.settlementDate)?.setValidators([Validators.required]);
        control.get(this.constants.FORM_CONTROL_NAMES.amountSettled)?.setValidators([Validators.required]);
        control.get(this.constants.FORM_CONTROL_NAMES.settlementComment)?.setValidators([Validators.maxLength(500)]);
      }
    );
  }

  setSelectedProject(identifier: string) {
    this.contractedProjects$.pipe(
      take(1),
      map(projects => projects.find(item => item.customIdentifier === identifier)),
      filter(Boolean),
      tap((project: OutputProjectSimple) => {
        this.advancePaymentsDetailPageStore.getProjectPartnersByProjectId$.next(project.id);
        this.advancePaymentForm.get(this.constants.FORM_CONTROL_NAMES.projectCustomIdentifier)?.setValue(project);
      }),
      shareReplay(1),
    ).subscribe();
  }

  setSelectedPartner(paymentDetail: AdvancePaymentDetailDTO) {
    this.partnerData$.pipe(
      map((partnerData) => partnerData.find(item => item.partnerSummary.id === paymentDetail.partnerId)),
      filter(Boolean),
      tap((partner: ProjectPartnerPaymentSummaryDTO) => {
        this.fundsAndContributions = partner;
        this.advancePaymentForm.get(this.constants.FORM_CONTROL_NAMES.partnerAbbreviation)?.setValue(partner);
        this.advancePaymentForm.get(this.constants.FORM_CONTROL_NAMES.sourceOrFundName)?.setValue(this.getSourceValue(paymentDetail, partner));
      }),
      untilDestroyed(this)
    ).subscribe();
  }

  setFoundOrContribution(paymentDetail: AdvancePaymentDetailDTO) {
    if (paymentDetail.programmeFund?.id) {
      this.advancePaymentForm.get(this.constants.FORM_CONTROL_NAMES.sourceOrFundName)?.setValue({type: 'fund', data: paymentDetail.programmeFund});
    } else if (paymentDetail.partnerContribution?.id) {
      this.advancePaymentForm.get(this.constants.FORM_CONTROL_NAMES.sourceOrFundName)?.setValue({
        type: 'contribution',
        data: paymentDetail.partnerContribution
      });
    } else if (paymentDetail.partnerContributionSpf?.id) {
      this.advancePaymentForm.get(this.constants.FORM_CONTROL_NAMES.sourceOrFundName)?.setValue({
        type: 'spfContribution',
        data: {id: paymentDetail.partnerContributionSpf.id, name: paymentDetail.partnerContributionSpf.name}
      });
    }
  }

  disableAuthorizationCheckbox(paymentDetail: AdvancePaymentDetailDTO) {
    if (this.isPaymentAuthorisationDisabled() || !paymentDetail.projectId) {
      this.advancePaymentForm.get(this.constants.FORM_CONTROL_NAMES.paymentAuthorized)?.disable();
    } else {
      this.advancePaymentForm.get(this.constants.FORM_CONTROL_NAMES.paymentAuthorized)?.enable();
    }
  }

  disableConfirmationCheckbox(settlements: AdvancePaymentSettlementDTO[]) {
    if (!this.isPaymentAuthorised()) {
      this.advancePaymentForm.get(this.constants.FORM_CONTROL_NAMES.paymentConfirmed)?.disable();
    } else {
      this.advancePaymentForm.get(this.constants.FORM_CONTROL_NAMES.paymentConfirmed)?.enable();
    }
    if (settlements?.length > 0) {
      this.advancePaymentForm.get(this.constants.FORM_CONTROL_NAMES.paymentConfirmed)?.disable();
    }
  }

  isPaymentAuthorised(): boolean {
    return this.advancePayment.get(this.constants.FORM_CONTROL_NAMES.paymentAuthorized)?.value;
  }

  getPartnerRole(partner: ProjectPartnerSummaryDTO): string {
    if (!partner) {
      return '';
    }
    return (partner.role === 'LEAD_PARTNER' ? 'LP' : 'PP') + partner.sortNumber;
  }

  get advancePayment(): FormGroup {
    return this.advancePaymentForm;
  }

  addSettlement() {
    this.settlementsArray.push(
      this.formBuilder.group({
          id: null,
          number: this.settlementsArray.length + 1,
          amountSettled: this.getProposedSettlementAmount(),
          settlementDate: ['', Validators.required],
          comment: ['', Validators.maxLength(500)]
        }
      ));
    this.formService.setDirty(true);
  }

  removeSettlement(settlementIndex: number) {
    this.settlementsArray.removeAt(settlementIndex);
    this.formService.setDirty(true);
  }

  get settlementsArray(): FormArray {
    return this.advancePaymentForm.get(this.constants.FORM_CONTROL_NAMES.settlements) as FormArray;
  }

  updateAdvancePayment() {
    const dataToUpdate = this.prepareDataForSave(this.advancePaymentForm.getRawValue());
    this.advancePaymentsDetailPageStore.updateAdvancePayment(dataToUpdate).pipe(
      take(1),
      tap(() => this.formService.setSuccess('payments.detail.table.have.success')),
      tap(data => this.redirectToPartnerDetailAfterCreate(dataToUpdate.id === null, data.id)),
      catchError(error => {
        const apiError = error.error as APIError;
        if (apiError?.formErrors) {
          Object.keys(apiError.formErrors).forEach(field => {
            const control = this.advancePayment.get(field);
            control?.setErrors({error: this.translateService.instant(apiError.formErrors[field].i18nKey)});
            control?.markAsDirty();
          });
          this.changeDetectorRef.detectChanges();
        }
        this.formService.setError(error);
        throw error;
      }),
      untilDestroyed(this)
    ).subscribe();
  }

  redirectToPartnerDetailAfterCreate(isCreate: boolean, paymentId: number) {
    if (isCreate) {
      this.router.navigate(
        ['..', paymentId],
        {relativeTo: this.activatedRoute}
      );
    }
  }

  prepareDataForSave(data: any): AdvancePaymentUpdateDTO {
    return {
      id: data.id,
      projectId: data.projectCustomIdentifier.id,
      partnerId: data.partnerAbbreviation.partnerSummary.id,
      programmeFundId: data.programmeFundId,
      partnerContributionId: data.partnerContributionId,
      partnerContributionSpfId: data.partnerContributionSpfId,
      amountPaid: data.amountPaid,
      paymentDate: data.paymentDate,
      comment: data.comment,
      paymentSettlements: data.paymentSettlements
    };
  }

  getPartnerToDisplay(attribute1: ProjectPartnerPaymentSummaryDTO, attribute2: ProjectPartnerPaymentSummaryDTO) {
    if (attribute1?.partnerSummary?.id === attribute2?.partnerSummary?.id) {
      return attribute1;
    } else {
      return '';
    }
  }

  getSourceToDisplay(attribute1: any, attribute2: any) {
    if (attribute1?.data?.id === attribute2?.data?.id && attribute1?.type === attribute2?.type) {
      return attribute1;
    } else {
      return '';
    }
  }

  setPaymentAuthorised(isChecked: boolean, paymentId: number) {
    this.advancePaymentsDetailPageStore.updateStatus(
      paymentId, isChecked ? AdvancePaymentStatusUpdateDTO.StatusEnum.AUTHORIZED : AdvancePaymentStatusUpdateDTO.StatusEnum.DRAFT
    ).subscribe();
  }

  disableFieldsIfPaymentIsAuthorized() {
    if (this.advancePayment.get(this.constants.FORM_CONTROL_NAMES.paymentAuthorized)?.value) {
      this.advancePayment.get(this.constants.FORM_CONTROL_NAMES.projectCustomIdentifier)?.disable();
      this.advancePayment.get(this.constants.FORM_CONTROL_NAMES.partnerAbbreviation)?.disable();
      this.advancePayment.get(this.constants.FORM_CONTROL_NAMES.sourceOrFundName)?.disable();
      this.advancePayment.get(this.constants.FORM_CONTROL_NAMES.amountPaid)?.disable();
    } else {
      this.advancePayment.get(this.constants.FORM_CONTROL_NAMES.projectCustomIdentifier)?.enable();
      this.advancePayment.get(this.constants.FORM_CONTROL_NAMES.partnerAbbreviation)?.enable();
      this.advancePayment.get(this.constants.FORM_CONTROL_NAMES.sourceOrFundName)?.enable();
      this.advancePayment.get(this.constants.FORM_CONTROL_NAMES.amountPaid)?.enable();
    }
  }

  disableFieldsIfPaymentIsConfirmed() {
    if (this.advancePayment.get(this.constants.FORM_CONTROL_NAMES.paymentConfirmed)?.value) {
      this.advancePayment.get(this.constants.FORM_CONTROL_NAMES.paymentDate)?.disable();
    } else {
      this.advancePayment.get(this.constants.FORM_CONTROL_NAMES.paymentDate)?.enable();
    }
  }

  disableFieldsIfProjectNotSelected(paymentDetail: AdvancePaymentDetailDTO) {
    if (!paymentDetail?.projectId) {
      this.advancePayment.get(this.constants.FORM_CONTROL_NAMES.partnerAbbreviation)?.disable();
      this.advancePayment.get(this.constants.FORM_CONTROL_NAMES.sourceOrFundName)?.disable();
    }
  }

  setConfirmPaymentDate(isChecked: boolean, paymentId: number) {
    this.advancePaymentsDetailPageStore.updateStatus(
      paymentId, isChecked ? AdvancePaymentStatusUpdateDTO.StatusEnum.CONFIRMED : AdvancePaymentStatusUpdateDTO.StatusEnum.AUTHORIZED
    ).subscribe();
  }

  isPaymentAuthorisationDisabled(): boolean {
    return this.isPaymentConfirmed() ||
      this.isPaymentAlreadyConfirmed()
      || !this.advancePayment.get(this.constants.FORM_CONTROL_NAMES.amountPaid)?.value
      || this.areRequiredFieldsEmpty();
  }

  areRequiredFieldsEmpty(): boolean {
    return this.advancePaymentForm.get(this.constants.FORM_CONTROL_NAMES.projectCustomIdentifier)?.errors !== null ||
      this.advancePaymentForm.get(this.constants.FORM_CONTROL_NAMES.partnerAbbreviation)?.errors !== null ||
      this.advancePaymentForm.get(this.constants.FORM_CONTROL_NAMES.sourceOrFundName)?.errors !== null ||
      this.advancePaymentForm.get(this.constants.FORM_CONTROL_NAMES.amountPaid)?.errors !== null;
  }

  isPaymentDateEmpty(): boolean {
    return !this.advancePayment.get(this.constants.FORM_CONTROL_NAMES.paymentDate)?.value;
  }

  isPaymentConfirmed(): boolean {
    return this.advancePayment
      .get(this.constants.FORM_CONTROL_NAMES.paymentConfirmed)?.value;
  }

  isPaymentAlreadyConfirmed(): boolean {
    return this.initialAdvancePaymentDetail.paymentConfirmed || false;
  }

  getFormattedDate(value: string): any {
    return this.localeDatePipe.transform(value);
  }

  projectSelected(project: OutputProjectSimple) {
    this.advancePaymentsDetailPageStore.getProjectPartnersByProjectId$.next(project.id);
    this.resetFundsAndContributionData();
    this.advancePayment.get(this.constants.FORM_CONTROL_NAMES.partnerAbbreviation)?.enable();
    this.advancePayment.get(this.constants.FORM_CONTROL_NAMES.sourceOrFundName)?.enable();

    this.advancePayment.get(this.constants.FORM_CONTROL_NAMES.sourceOrFundName)?.setValue('');
    this.advancePayment.get(this.constants.FORM_CONTROL_NAMES.partnerAbbreviation)?.setValue('');
    this.advancePayment.get(this.constants.FORM_CONTROL_NAMES.projectAcronym)?.setValue(project.acronym);
  }

  setFundsAndContributionData(selection: any) {
    this.fundsAndContributions = selection;
    this.advancePayment.get(this.constants.FORM_CONTROL_NAMES.sourceOrFundName)?.setValue('');
  }

  resetFundsAndContributionData() {
    this.fundsAndContributions = null;
  }

  isPaymentValueValid(value: string): boolean {
    return parseInt(value, 10) > 0;
  }

  toggleSettlements() {
    this.settlementsExpanded = !this.settlementsExpanded;
  }

  private getProposedSettlementAmount(): Number {
    const previouslySettledAmounts = this.settlementsArray.controls.map(control => control.get(this.constants.FORM_CONTROL_NAMES.amountSettled)?.value);
    const amountPaid = this.advancePaymentForm.get(this.constants.FORM_CONTROL_NAMES.amountPaid)?.value;
    return NumberService.minus(amountPaid, NumberService.sum(previouslySettledAmounts));
  }

  displayProjectIdentifier(project: any): string {
    return project?.customIdentifier ?? '';
  }

  static requireMatch(control: AbstractControl): { [key: string]: any } | null {
    const selection: any = control.value;
    if (typeof selection === 'string' && selection !== '') {
      return {incorrect: true};
    }
    return null;
  }

}

