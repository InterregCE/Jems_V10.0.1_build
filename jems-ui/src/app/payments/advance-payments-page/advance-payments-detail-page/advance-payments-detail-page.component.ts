import {ChangeDetectionStrategy, ChangeDetectorRef, Component, OnInit} from '@angular/core';
import {combineLatest, Observable, of} from 'rxjs';
import {
  AdvancePaymentDetailDTO,
  AdvancePaymentUpdateDTO,
  OutputProjectSimple,
  OutputUser,
  ProjectPartnerPaymentSummaryDTO,
  ProjectPartnerSummaryDTO,
  UserDTO
} from '@cat/api';
import {ActivatedRoute} from '@angular/router';
import {AbstractControl, FormBuilder, FormGroup, Validators} from '@angular/forms';
import {catchError, debounceTime, map, take, tap} from 'rxjs/operators';
import {animate, state, style, transition, trigger} from '@angular/animations';
import {FormService} from '@common/components/section/form/form.service';
import {SecurityService} from '../../../security/security.service';
import {UntilDestroy, untilDestroyed} from '@ngneat/until-destroy';
import {LocaleDatePipe} from '@common/pipe/locale-date.pipe';
import {Alert} from '@common/components/forms/alert';
import {AdvancePaymentsPageStore} from '../advance-payments-page.store';
import {AdvancePaymentsDetailPageStoreStore} from './advance-payments-detail-page-store.store';
import {AdvancePaymentsDetailPageConstants} from './advance-payments-detail-page.constants';
import {RoutingService} from '@common/services/routing.service';
import {APIError} from '@common/models/APIError';
import {TranslateService} from '@ngx-translate/core';

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
  constants = AdvancePaymentsDetailPageConstants;
  tableData: AbstractControl[] = [];
  paymentId = this.activatedRoute.snapshot.params.advancePaymentId;
  columnsToDisplay = ['partner', 'partnerName', 'amountApproved', 'addInstallment'];
  projectCustomIdentifierSearchForm =  this.formBuilder.group(
    {
      projectCustomIdentifierSearch: this.formBuilder.control('')
    }
  );

  advancePaymentForm = this.formBuilder.group({
    id: '',
    projectCustomIdentifier: this.formBuilder.control(''),
    projectAcronym: this.formBuilder.control(''),
    partnerAbbreviation: this.formBuilder.control(''),
    selectedPartner: this.formBuilder.control(''),
    partnerType: this.formBuilder.control(''),
    partnerNumber: this.formBuilder.control(''),
    sourceOrFundName: this.formBuilder.control(''),
    programmeFundId: this.formBuilder.control(''),
    partnerContributionId: this.formBuilder.control(''),
    partnerContributionSpfId: this.formBuilder.control(''),
    amountAdvance: this.formBuilder.control(''),
    dateOfPayment: this.formBuilder.control(''),
    comment: this.formBuilder.control(''),
    paymentAuthorized: this.formBuilder.control(''),
    paymentAuthorizedUser: this.formBuilder.control(''),
    paymentAuthorizedDate: this.formBuilder.control(''),
    paymentConfirmed: this.formBuilder.control(''),
    paymentConfirmedUser: this.formBuilder.control(''),
    paymentConfirmedDate: this.formBuilder.control('')
  });
  initialAdvancePaymentDetail: AdvancePaymentDetailDTO;
  currentUserDetails: UserDTO;
  selectedProject$: Observable<OutputProjectSimple | undefined>;
  selectedPartner$: Observable<ProjectPartnerPaymentSummaryDTO | undefined>;
  data$: Observable<{
    paymentDetail: AdvancePaymentDetailDTO;
    currentUser: UserDTO;
  }>;
  contractedProjects$: Observable<OutputProjectSimple[]>;
  partnerData$: Observable<ProjectPartnerPaymentSummaryDTO[]>;
  fundsAndContributions: ProjectPartnerPaymentSummaryDTO | null;

  constructor(private activatedRoute: ActivatedRoute,
              private formBuilder: FormBuilder,
              private formService: FormService,
              private advancePaymentsDetailPageStore: AdvancePaymentsDetailPageStoreStore,
              private advancePaymentsStore: AdvancePaymentsPageStore,
              private securityService: SecurityService,
              private localeDatePipe: LocaleDatePipe,
              private router: RoutingService,
              private translateService: TranslateService,
              private changeDetectorRef: ChangeDetectorRef) {
    this.contractedProjects$ = this.advancePaymentsDetailPageStore.getContractedProjects();
    this.partnerData$ = this.advancePaymentsDetailPageStore.getPartnerData();
    this.projectCustomIdentifierSearchForm.get(this.constants.FORM_CONTROL_NAMES.projectCustomIdentifierSearch)?.valueChanges
    .pipe(
      debounceTime(150),
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
    ])
      .pipe(
        map(([paymentDetail, currentUser]: any) => ({
          paymentDetail,
          currentUser,
        })),
        tap((data) => this.initialAdvancePaymentDetail = data.paymentDetail),
        tap(data => this.currentUserDetails = data.currentUser),
        tap(data => this.loadData(data.paymentDetail)),
        tap(data => this.getSelectedProject(data.paymentDetail.projectCustomIdentifier)),
        tap(data => this.getSelectedPartner(data.paymentDetail.partnerAbbreviation)),
        tap(data => this.resetForm(data.paymentDetail))
      );
    this.formService.init(this.advancePaymentForm, of(true));
  }

  loadData(paymentDetail: AdvancePaymentDetailDTO) {
    if(paymentDetail?.id) {
      this.advancePaymentsDetailPageStore.getProjectPartnersByProjectId$.next(paymentDetail.projectId);
    }
  }

  getSelectedProject(identifier: string) {
    this.selectedProject$ = this.contractedProjects$.pipe(
      map((projects) => projects.find(item => item.customIdentifier === identifier)),
      untilDestroyed(this)
    );
  }

  getSelectedPartner(partnerName: string) {
    this.selectedPartner$ = this.partnerData$.pipe(
      map((partnerData) => partnerData.find(item => item.partnerSummary.abbreviation === partnerName)),
      untilDestroyed(this)
    );
  }

  resetSourceForAdvance() {
    this.advancePaymentForm.get(this.constants.FORM_CONTROL_NAMES.programmeFundId)?.setValue('');
    this.advancePaymentForm.get(this.constants.FORM_CONTROL_NAMES.partnerContributionId)?.setValue('');
  }

  setSourceForAdvance(selection: any) {
    if(selection) {
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

  resetForm(paymentDetail: AdvancePaymentDetailDTO) {
    this.advancePaymentForm.get(this.constants.FORM_CONTROL_NAMES.id)?.setValue(this.paymentId ? this.paymentId : null);
    this.selectedProject$.pipe(tap(project => {
        this.advancePaymentForm.get(this.constants.FORM_CONTROL_NAMES.projectCustomIdentifier)?.setValue(project);
      }
    )).subscribe();

    this.selectedPartner$.pipe(tap(partner => {
      if(partner) {
        this.fundsAndContributions = partner;
        this.advancePaymentForm.get(this.constants.FORM_CONTROL_NAMES.partnerAbbreviation)?.setValue(partner);
        this.advancePaymentForm.get(this.constants.FORM_CONTROL_NAMES.sourceOrFundName)?.setValue(this.getSourceValue(paymentDetail, partner));
      }
      }
    )).subscribe();

    this.setFoundOrContribution(paymentDetail);
    this.advancePaymentForm.get(this.constants.FORM_CONTROL_NAMES.projectAcronym)?.setValue(paymentDetail.projectAcronym);
    this.advancePaymentForm.get(this.constants.FORM_CONTROL_NAMES.partnerNumber)?.setValue(paymentDetail.partnerNumber);
    this.advancePaymentForm.get(this.constants.FORM_CONTROL_NAMES.partnerType)?.setValue(paymentDetail.partnerType);
    this.advancePaymentForm.get(this.constants.FORM_CONTROL_NAMES.programmeFundId)?.setValue(paymentDetail.programmeFund?.id);
    this.advancePaymentForm.get(this.constants.FORM_CONTROL_NAMES.partnerContributionId)?.setValue(paymentDetail.partnerContribution?.id);
    this.advancePaymentForm.get(this.constants.FORM_CONTROL_NAMES.partnerContributionSpfId)?.setValue(paymentDetail.partnerContributionSpf?.id);
    this.advancePaymentForm.get(this.constants.FORM_CONTROL_NAMES.amountAdvance)?.setValue(paymentDetail.amountAdvance);
    this.advancePaymentForm.get(this.constants.FORM_CONTROL_NAMES.dateOfPayment)?.setValue(paymentDetail.dateOfPayment);
    this.advancePaymentForm.get(this.constants.FORM_CONTROL_NAMES.comment)?.setValue(paymentDetail?.comment ? paymentDetail?.comment : '');
    this.advancePaymentForm.get(this.constants.FORM_CONTROL_NAMES.paymentAuthorized)?.setValue(paymentDetail.paymentAuthorized ? paymentDetail.paymentAuthorized : false);
    this.advancePaymentForm.get(this.constants.FORM_CONTROL_NAMES.paymentAuthorizedUser)?.setValue(paymentDetail?.paymentAuthorizedUser ? this.getOutputUserObject(paymentDetail?.paymentAuthorizedUser) : null);
    this.advancePaymentForm.get(this.constants.FORM_CONTROL_NAMES.paymentAuthorizedDate)?.setValue(paymentDetail?.paymentAuthorizedDate ? paymentDetail?.paymentAuthorizedDate : null);
    this.advancePaymentForm.get(this.constants.FORM_CONTROL_NAMES.paymentConfirmed)?.setValue(paymentDetail.paymentConfirmed ? paymentDetail.paymentConfirmed : false);
    this.advancePaymentForm.get(this.constants.FORM_CONTROL_NAMES.paymentConfirmedUser)?.setValue(paymentDetail?.paymentConfirmedUser ? this.getOutputUserObject(paymentDetail?.paymentConfirmedUser) : null);
    this.advancePaymentForm.get(this.constants.FORM_CONTROL_NAMES.paymentConfirmedDate)?.setValue(paymentDetail?.paymentConfirmedDate ? paymentDetail?.paymentConfirmedDate : null);

    this.setValidators();
    this.disableFieldsIfPaymentIsSaved();
    this.disableFieldsIfPaymentIsConfirmed();
    this.disableFieldsIfProjectNotSelected(paymentDetail);
    this.disableAuthorizationCheckbox(paymentDetail);
    this.disableConfirmationCheckbox();
  }

  setValidators() {
    this.advancePaymentForm.get(this.constants.FORM_CONTROL_NAMES.sourceOrFundName)?.setValidators([Validators.required]);
    this.advancePaymentForm.get(this.constants.FORM_CONTROL_NAMES.partnerAbbreviation)?.setValidators([Validators.required]);
    this.advancePaymentForm.get(this.constants.FORM_CONTROL_NAMES.projectCustomIdentifier)?.setValidators([Validators.required]);
    this.advancePaymentForm.get(this.constants.FORM_CONTROL_NAMES.comment)?.setValidators([Validators.maxLength(500)]);
    this.advancePaymentForm.get(this.constants.FORM_CONTROL_NAMES.amountAdvance)?.setValidators([Validators.required, Validators.min(0.01)]);
  }

  setFoundOrContribution(paymentDetail: AdvancePaymentDetailDTO) {
    if(paymentDetail.programmeFund?.id) {
      this.advancePaymentForm.get(this.constants.FORM_CONTROL_NAMES.sourceOrFundName)?.setValue({type: 'fund', data: paymentDetail.programmeFund });
    } else if(paymentDetail.partnerContribution?.id) {
      this.advancePaymentForm.get(this.constants.FORM_CONTROL_NAMES.sourceOrFundName)?.setValue({type: 'contribution', data:paymentDetail.partnerContribution});
    }
    else if(paymentDetail.partnerContributionSpf?.id) {
      this.advancePaymentForm.get(this.constants.FORM_CONTROL_NAMES.sourceOrFundName)?.setValue({type: 'spfContribution', data:{id: paymentDetail.partnerContributionSpf.id, name: paymentDetail.partnerContributionSpf.name}});
    }
  }

  disableAuthorizationCheckbox(paymentDetail: AdvancePaymentDetailDTO){
    if(this.isPaymentAuthorisationDisabled() || !paymentDetail.projectId) {
      this.advancePaymentForm.get(this.constants.FORM_CONTROL_NAMES.paymentAuthorized)?.disable();
    } else {
      this.advancePaymentForm.get(this.constants.FORM_CONTROL_NAMES.paymentAuthorized)?.enable();
    }
  }

  disableConfirmationCheckbox() {
    if(!this.isPaymentAuthorised()) {
      this.advancePaymentForm.get(this.constants.FORM_CONTROL_NAMES.paymentConfirmed)?.disable();
    } else {
      this.advancePaymentForm.get(this.constants.FORM_CONTROL_NAMES.paymentConfirmed)?.enable();
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

  updateAdvancePayment() {
    const dataToUpdate = this.prepareDataForSave(this.advancePaymentForm.getRawValue());
    this.advancePaymentsDetailPageStore.updateAdvancePayment(dataToUpdate).pipe(
      take(1),
      tap(() => this.formService.setSuccess('payments.detail.table.have.success')),
      tap(data =>  this.redirectToPartnerDetailAfterCreate(dataToUpdate.id === null, data.id)),
      catchError(error => {
        const apiError = error.error as APIError;
        if (apiError?.formErrors) {
          Object.keys(apiError.formErrors).forEach(field => {
            const control = this.advancePayment.get(field);
            control?.setErrors({required: this.translateService.instant(apiError.formErrors[field].i18nKey)});
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
    if(isCreate) {
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
      amountAdvance: data.amountAdvance,
      dateOfPayment: data.dateOfPayment,
      comment: data.comment,
      paymentAuthorized: data.paymentAuthorized,
      paymentConfirmed: data.paymentConfirmed,
    };
  }

  getProjectToDisplay(attribute1: any, attribute2: any) {
    if (attribute1?.id == attribute2?.id) {
      return attribute1;
    } else {
      return '';
    }
  }

  getPartnerToDisplay(attribute1: ProjectPartnerPaymentSummaryDTO, attribute2: ProjectPartnerPaymentSummaryDTO) {
    if (attribute1?.partnerSummary?.id == attribute2?.partnerSummary?.id) {
      return attribute1;
    } else {
      return '';
    }
  }

  getSourceToDisplay(attribute1: any, attribute2: any) {
    if (attribute1?.data?.id == attribute2?.data?.id && attribute1?.type === attribute2?.type) {
      return attribute1;
    } else {
      return '';
    }
  }


  setPaymentAuthorised(isChecked: boolean) {
    if (isChecked) {
      this.advancePayment.get(this.constants.FORM_CONTROL_NAMES.amountAdvance)?.disable();
      this.advancePayment.get(this.constants.FORM_CONTROL_NAMES.paymentConfirmed)?.enable();
      this.advancePayment.get(this.constants.FORM_CONTROL_NAMES.paymentAuthorizedDate)?.setValue(this.getFormattedCurrentLocaleDate());
      this.advancePayment.get(this.constants.FORM_CONTROL_NAMES.paymentAuthorizedUser)?.setValue(this.getOutputUserObject(this.currentUserDetails));
      this.advancePayment.get(this.constants.FORM_CONTROL_NAMES.dateOfPayment)?.setValidators([Validators.required]);
    } else {
      this.advancePayment.get(this.constants.FORM_CONTROL_NAMES.amountAdvance)?.enable();
      this.advancePayment.get(this.constants.FORM_CONTROL_NAMES.paymentConfirmed)?.disable();
      this.advancePayment.get(this.constants.FORM_CONTROL_NAMES.paymentAuthorizedDate)?.setValue(null);
      this.advancePayment.get(this.constants.FORM_CONTROL_NAMES.paymentAuthorizedUser)?.setValue(null);

      this.advancePayment.get(this.constants.FORM_CONTROL_NAMES.dateOfPayment)?.removeValidators([Validators.required]);
    }
  }

  disableFieldsIfPaymentIsSaved() {
    if (this.advancePayment.get(this.constants.FORM_CONTROL_NAMES.paymentAuthorized)?.value) {
      this.advancePayment.get(this.constants.FORM_CONTROL_NAMES.amountAdvance)?.disable();
    }
  }

  disableFieldsIfPaymentIsConfirmed() {
    if (this.advancePayment.get(this.constants.FORM_CONTROL_NAMES.paymentConfirmed)?.value) {
      this.advancePayment.get(this.constants.FORM_CONTROL_NAMES.projectCustomIdentifier)?.disable();
      this.advancePayment.get(this.constants.FORM_CONTROL_NAMES.partnerAbbreviation)?.disable();
      this.advancePayment.get(this.constants.FORM_CONTROL_NAMES.sourceOrFundName)?.disable();
      this.advancePayment.get(this.constants.FORM_CONTROL_NAMES.dateOfPayment)?.disable();
    }
  }

  disableFieldsIfProjectNotSelected(paymentDetail: AdvancePaymentDetailDTO) {
    if (!paymentDetail?.projectId) {
      this.advancePayment.get(this.constants.FORM_CONTROL_NAMES.partnerAbbreviation)?.disable();
      this.advancePayment.get(this.constants.FORM_CONTROL_NAMES.sourceOrFundName)?.disable();
    }
  }

  getOutputUserObject(userDetails: UserDTO | OutputUser): OutputUser {
    return {
      id: userDetails.id,
      email: userDetails.email,
      name: userDetails.name,
      surname: userDetails.surname
    } as OutputUser;
  }

  getFormattedCurrentLocaleDate() {
    const date = new Date();
    return date.toISOString().substring(0,10);
  }

  setConfirmPaymentDate(isChecked: boolean) {
    if (isChecked) {
      this.advancePayment.get(this.constants.FORM_CONTROL_NAMES.paymentConfirmedDate)?.setValue(this.getFormattedCurrentLocaleDate());
      this.advancePayment.get(this.constants.FORM_CONTROL_NAMES.paymentConfirmedUser)?.setValue(this.getOutputUserObject(this.currentUserDetails));
      this.advancePayment.get(this.constants.FORM_CONTROL_NAMES.projectCustomIdentifier)?.disable();
      this.advancePayment.get(this.constants.FORM_CONTROL_NAMES.partnerAbbreviation)?.disable();
      this.advancePayment.get(this.constants.FORM_CONTROL_NAMES.sourceOrFundName)?.disable();

      if(!this.isPaymentDateEmpty()) {
        this.advancePayment.get(this.constants.FORM_CONTROL_NAMES.dateOfPayment)?.disable();
      }
    } else {
      this.advancePayment.get(this.constants.FORM_CONTROL_NAMES.paymentConfirmedDate)?.setValue(null);
      this.advancePayment.get(this.constants.FORM_CONTROL_NAMES.paymentConfirmedUser)?.setValue(null);
      this.advancePayment.get(this.constants.FORM_CONTROL_NAMES.projectCustomIdentifier)?.enable();
      this.advancePayment.get(this.constants.FORM_CONTROL_NAMES.partnerAbbreviation)?.enable();
      this.advancePayment.get(this.constants.FORM_CONTROL_NAMES.sourceOrFundName)?.enable();
      this.advancePayment.get(this.constants.FORM_CONTROL_NAMES.dateOfPayment)?.enable();
    }
  }

  isPaymentAuthorisationDisabled(): boolean {
    return this.isPaymentConfirmed() ||
      this.isPaymentAlreadyConfirmed() || !this.advancePayment.get(this.constants.FORM_CONTROL_NAMES.amountAdvance)?.value;
  }

  isPaymentDateEmpty(): boolean {
    return !this.advancePayment.get(this.constants.FORM_CONTROL_NAMES.dateOfPayment)?.value;
  }

  isPaymentConfirmed(): boolean {
    return this.advancePayment
      .get(this.constants.FORM_CONTROL_NAMES.paymentConfirmed)?.value;
  }

  isPaymentAlreadyConfirmed(): boolean {
    return  this.initialAdvancePaymentDetail.paymentConfirmed || false;
  }

  getFormattedDate(value: string): any {
    return this.localeDatePipe.transform(value);
  }

  loadPartnerAndFundsData(project: OutputProjectSimple) {
    this.advancePaymentsDetailPageStore.getProjectPartnersByProjectId$.next(project.id);
    this.resetFundsAndContributionData();

    this.advancePayment.get(this.constants.FORM_CONTROL_NAMES.partnerAbbreviation)?.enable();
    this.advancePayment.get(this.constants.FORM_CONTROL_NAMES.sourceOrFundName)?.enable();

    this.advancePayment.get(this.constants.FORM_CONTROL_NAMES.projectAcronym)?.setValue(project.acronym);
  }

  setFundsAndContributionData(selection: any) {
    this.fundsAndContributions = selection;
  }

  resetFundsAndContributionData() {
    this.fundsAndContributions = null;
  }
}
