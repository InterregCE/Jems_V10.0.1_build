import {ChangeDetectionStrategy, ChangeDetectorRef, Component, OnInit} from '@angular/core';
import {combineLatest, Observable} from 'rxjs';
import {
  UserSimpleDTO,
  OutputNuts,
  ProjectDetailDTO, ProjectPartnerControlReportChangeDTO,
  ProjectPartnerControlReportDTO,
  ProjectPartnerDetailDTO, ReportOnTheSpotVerificationDTO,
  ReportVerificationDTO
} from '@cat/api';
import {FormArray, FormBuilder, FormGroup, Validators} from '@angular/forms';
import {
  PartnerReportDetailPageStore
} from '@project/project-application/report/partner-report-detail-page/partner-report-detail-page-store.service';
import {FormService} from '@common/components/section/form/form.service';
import {
  ProjectStore
} from '@project/project-application/containers/project-application-detail/services/project-store.service';
import {catchError, map, startWith, take, tap} from 'rxjs/operators';
import {SelectionModel} from '@angular/cdk/collections';
import {
  PartnerControlReportStore
} from '@project/project-application/report/partner-control-report/partner-control-report-store.service';
import {
  PartnerControlReportControlIdentificationConstants
} from '@project/project-application/report/partner-control-report/partner-control-report-identification-tab/partner-control-report-control-identification.constants';
import {NutsStore} from '@common/services/nuts.store';
import VerificationLocationsEnum = ReportOnTheSpotVerificationDTO.VerificationLocationsEnum;
import GeneralMethodologiesEnum = ReportVerificationDTO.GeneralMethodologiesEnum;
import {APIError} from '@common/models/APIError';
import {TranslateService} from '@ngx-translate/core';

@Component({
  selector: 'jems-partner-control-report-identification-tab',
  templateUrl: './partner-control-report-control-identification-tab.component.html',
  styleUrls: ['./partner-control-report-control-identification-tab.component.scss'],
  providers: [FormService],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class PartnerControlReportControlIdentificationTabComponent implements OnInit {

  ProjectPartnerControlReportDTO = ProjectPartnerControlReportDTO;
  constants = PartnerControlReportControlIdentificationConstants;
  reportEditable$: Observable<boolean>;
  nuts: OutputNuts[];
  controllerUsers: UserSimpleDTO[];
  selectedCountry: OutputNuts | undefined;
  selectedControllerUser: UserSimpleDTO | undefined;
  selectedReviewerUser: UserSimpleDTO | undefined;
  filteredCountry$: Observable<string[]>;
  filteredControllerUsers$: Observable<string[]>;
  filteredReviewerUsers$: Observable<string[]>;

  data$: Observable<{
    partnerControlReport: ProjectPartnerControlReportDTO;
    project: ProjectDetailDTO;
    partner: ProjectPartnerDetailDTO;
    nuts: OutputNuts[];
    controlInstitutionUsers: UserSimpleDTO[];
  }>;

  form = this.formBuilder.group({
    formats: [[]],
    partnerType: [],
    designatedController: this.formBuilder.group({
      controlInstitution: this.formBuilder.control(''),
      controlInstitutionId: this.formBuilder.control(''),
      controllingUserId: this.formBuilder.control(''),
      controllingUserName: this.formBuilder.control(''),
      jobTitle: this.formBuilder.control('', [Validators.maxLength(this.constants.JOB_TITLE_MAX_LENGTH)]),
      divisionUnit: this.formBuilder.control('', [Validators.maxLength(this.constants.DIVISION_MAX_LENGTH)]),
      address: this.formBuilder.control('', [Validators.maxLength(this.constants.ADDRESS_MAX_LENGTH)]),
      country: this.formBuilder.control(''),
      countryCode: this.formBuilder.control(''),
      telephone: this.formBuilder.control('', Validators.compose([
          Validators.pattern('^([\s]+[0-9+()/]+)|([0-9+()/]+)[ 0-9+()/-]*$'),
          Validators.maxLength(this.constants.PHONE_MAX_LENGTH)
        ])
      ),
      controllerReviewerId: this.formBuilder.control(''),
      controllerReviewerName: this.formBuilder.control(''),
      }
    ),
    reportVerification: this.formBuilder.group({
      generalMethodologies: this.formBuilder.control(''),
      administrativeVerification: this.formBuilder.control(''),
      onTheSpotVerification: this.formBuilder.control(''),
      riskBasedVerificationApplied: this.formBuilder.control(''),
      riskBasedVerificationDescription: this.formBuilder.control('', [Validators.maxLength(this.constants.SAMPLING_METHODOLOGY_MAX_LENGTH)]),
      verificationInstances: this.formBuilder.array([])
    })
  });

  selection = new SelectionModel<ProjectPartnerControlReportDTO.ControllerFormatsEnum>(true, []);

  formats = [
    ProjectPartnerControlReportDTO.ControllerFormatsEnum.Originals,
    ProjectPartnerControlReportDTO.ControllerFormatsEnum.Copy,
    ProjectPartnerControlReportDTO.ControllerFormatsEnum.Electronic
  ];

  constructor(
    public pageStore: PartnerReportDetailPageStore,
    public store: PartnerControlReportStore,
    public formService: FormService,
    private formBuilder: FormBuilder,
    private projectStore: ProjectStore,
    private nutsStore: NutsStore,
    private translateService: TranslateService,
    private changeDetectorRef: ChangeDetectorRef
  ) {
    this.reportEditable$ = this.store.controlReportEditable$;
    this.data$ = combineLatest([
      store.partnerControlReport$,
      projectStore.project$,
      store.partner$,
      this.nutsStore.getNuts(),
      this.store.controlInstitutionUsers$
    ]).pipe(
      map(([partnerControlReport, project, partner, nuts, controlInstitutionUsers]) => ({
        partnerControlReport,
        project,
        partner,
        nuts,
        controlInstitutionUsers
      })),
      tap(data => this.nuts = data.nuts),
      tap(data => this.controllerUsers = data.controlInstitutionUsers),
      tap((data) => this.resetForm(data.partnerControlReport)),
    );

  }

  ngOnInit() {
    this.initializeFilters();
    this.formService.init(this.form, this.store.controlReportEditable$);
  }

  private initializeFilters(): void {
    this.filteredCountry$ = this.designatedController.controls.country.valueChanges
      .pipe(
        startWith(''),
        map(value => this.filterCountry(value, this.nuts)),
      );

    this.filteredControllerUsers$ = this.designatedController.controls.controllingUserName.valueChanges
      .pipe(
        startWith(''),
        map(value => this.filterControllerUser(value, this.controllerUsers)),
      );

    this.filteredReviewerUsers$ = this.designatedController.controls.controllerReviewerName.valueChanges
      .pipe(
        startWith(''),
        map(value => this.filterControllerUser(value, this.controllerUsers)),
      );
  }

  private filterCountry(value: string, nuts: OutputNuts[]): string[] {
    const filterValue = (value || '').toLowerCase();
    return nuts
      .filter(nut => PartnerControlReportControlIdentificationTabComponent.formatRegion(nut).toLowerCase().includes(filterValue))
      .map(nut => PartnerControlReportControlIdentificationTabComponent.formatRegion(nut));
  }

  private filterControllerUser(value: string, controlUsers: UserSimpleDTO[]): string[] {
    const filterValue = (value || '').toLowerCase();
    return controlUsers
      .filter(user => PartnerControlReportControlIdentificationTabComponent.formatControLuser(user).toLowerCase().includes(filterValue))
      .map(user => PartnerControlReportControlIdentificationTabComponent.formatControLuser(user));
  }

  resetForm(data: ProjectPartnerControlReportDTO) {
    this.selection.clear();
    if (data.controllerFormats) {
      data.controllerFormats.forEach((select: ProjectPartnerControlReportDTO.ControllerFormatsEnum) => {
        this.selection.select(select);
      });
      this.form.controls.formats.patchValue(this.selection.selected);
    }
    if (data.type) {
      this.form.controls.partnerType.patchValue(data.type);
    }

    this.designatedController.get('controlInstitution')?.patchValue(this.returnValueOrNull(data.designatedController?.controlInstitution));
    this.designatedController.get('controlInstitutionId')?.patchValue(this.returnValueOrNull(data.designatedController?.controlInstitutionId));
    this.designatedController.get('controllingUserId')?.patchValue(this.returnValueOrNull(data.designatedController?.controllingUserId));
    this.designatedController.get('jobTitle')?.patchValue(this.returnValueOrNull(data.designatedController?.jobTitle));
    this.designatedController.get('divisionUnit')?.patchValue(this.returnValueOrNull(data.designatedController?.divisionUnit));
    this.designatedController.get('address')?.patchValue(this.returnValueOrNull(data.designatedController?.address));
    this.designatedController.get('country')?.patchValue(this.returnValueOrNull(data.designatedController?.country));
    this.designatedController.get('countryCode')?.patchValue(this.returnValueOrNull(data.designatedController?.countryCode));
    this.designatedController.get('telephone')?.patchValue(this.returnValueOrNull(data.designatedController?.telephone));
    this.designatedController.get('controllerReviewerId')?.patchValue(this.returnValueOrNull(data.designatedController?.controllerReviewerId));

    this.reportVerification.get('administrativeVerification')?.patchValue(data.reportVerification?.generalMethodologies.includes(ReportVerificationDTO.GeneralMethodologiesEnum.AdministrativeVerification));
    this.reportVerification.get('onTheSpotVerification')?.patchValue(data.reportVerification?.generalMethodologies.includes(ReportVerificationDTO.GeneralMethodologiesEnum.OnTheSpotVerification));
    this.reportVerification.get('generalMethodologies')?.patchValue(data.reportVerification?.generalMethodologies);
    this.reportVerification.get('riskBasedVerificationApplied')?.patchValue(data.reportVerification?.riskBasedVerificationApplied);
    this.reportVerification.get('riskBasedVerificationDescription')?.patchValue(data.reportVerification?.riskBasedVerificationDescription);

    this.setSelectedUsers(data);

    this.verificationInstances.clear();
    data.reportVerification?.verificationInstances.forEach((verificationRow: ReportOnTheSpotVerificationDTO) => this.addOnSpotVerification(verificationRow));

    this.formService.resetEditable();
    this.disableFields();
  }

  private setSelectedUsers(data: any) {
    this.designatedController.get(this.constants.FORM_CONTROL_NAMES.controllingUserName)?.patchValue(data.designatedController?.controllingUserId ? PartnerControlReportControlIdentificationTabComponent.formatControLuser(this.getUserById(data.designatedController.controllingUserId)) : '');
    this.designatedController.get(this.constants.FORM_CONTROL_NAMES.controllerReviewerName)?.patchValue(data.designatedController?.controllerReviewerId ? PartnerControlReportControlIdentificationTabComponent.formatControLuser(this.getUserById(data.designatedController.controllerReviewerId)) : '');

    this.selectedCountry = this.findByName(this.form.controls.designatedController.get(this.constants.FORM_CONTROL_NAMES.country)?.value, this.nuts);
    this.selectedControllerUser = this.getUserById(this.form.controls.designatedController.get(this.constants.FORM_CONTROL_NAMES.controllingUserId)?.value);
    this.selectedReviewerUser = this.getUserById(this.form.controls.designatedController.get(this.constants.FORM_CONTROL_NAMES.controllerReviewerId)?.value);
  }

  getUserById(id: number): UserSimpleDTO | undefined {
    return this.controllerUsers.find(user => user.id === id);
  }

  private disableFields() {
    this.designatedController.get(this.constants.FORM_CONTROL_NAMES.controlInstitution)?.disable();
  }

  returnValueOrNull(data: any) {
    return data ? data : null;
  }

  addOnSpotVerification(data?: ReportOnTheSpotVerificationDTO) {
    const group = this.formBuilder.group({
      verificationFrom: this.formBuilder.control(this.returnValueOrNull(data?.verificationFrom)),
      verificationTo: this.formBuilder.control(this.returnValueOrNull(data?.verificationTo)),
      premisesOfProjectPartner: this.formBuilder.control(data?.verificationLocations.includes(VerificationLocationsEnum.PremisesOfProjectPartner)),
      projectEvent: this.formBuilder.control(data?.verificationLocations.includes(VerificationLocationsEnum.ProjectEvent)),
      placeOfProjectOutput: this.formBuilder.control(data?.verificationLocations.includes(VerificationLocationsEnum.PlaceOfPhysicalProjectOutput)),
      virtual: this.formBuilder.control(data?.verificationLocations.includes(VerificationLocationsEnum.Virtual)),
      verificationFocus: this.formBuilder.control(this.returnValueOrNull(data?.verificationFocus), [Validators.maxLength(this.constants.FOCUS_MAX_LENGTH)])
    });
    this.verificationInstances?.push(group);
  }

  get verificationInstances(): FormArray {
    return this.form.get(this.constants.FORM_CONTROL_NAMES.reportVerification)?.get(this.constants.FORM_CONTROL_NAMES.verificationInstances) as FormArray;
  }

  get designatedController(): FormGroup {
    return this.form.get(this.constants.FORM_CONTROL_NAMES.designatedController) as FormGroup;
  }

  get reportVerification(): FormGroup {
    return this.form.get(this.constants.FORM_CONTROL_NAMES.reportVerification) as FormGroup;
  }

  checkSelection(element: ProjectPartnerControlReportDTO.ControllerFormatsEnum): void {
    this.selection.toggle(element);
    this.form.controls.formats.patchValue(this.selection.selected);
    this.formService.setDirty(true);
    this.formService.setValid(true);
  }

  saveIdentification(): void {
    const data = {
      controllerFormats: this.form.value.formats,
      type: this.form.value.partnerType,
      designatedController: {
        ...this.form.value.designatedController,
        controlInstitutionId: this.designatedController.get('controlInstitutionId')?.value,
        controlInstitution: this.designatedController.get('controlInstitution')?.value,
        countryCode: this.selectedCountry?.code,
        controllingUserId: this.selectedControllerUser?.id,
        controllerReviewerId: this.selectedReviewerUser?.id
      },
      reportVerification: {
        ...this.form.value.reportVerification,
        generalMethodologies: this.getListOfGeneralMethodologies(this.form.value.reportVerification),
        verificationInstances: this.form.controls.reportVerification?.get(this.constants.FORM_CONTROL_NAMES.onTheSpotVerification)?.value ? this.formatVerificationInstances(this.form.value.reportVerification.verificationInstances) : [],
      }
    } as ProjectPartnerControlReportChangeDTO;

    this.store.saveIdentification(data)
      .pipe(
        take(1),
        tap(() => this.formService.setSuccess('project.application.partner.report.tab.identification.saved')),
        catchError(error => {
          const apiError = error.error as APIError;
          if (apiError?.formErrors) {
            Object.keys(apiError.formErrors).forEach(field => {
              const control = this.designatedController.get(field);
              control?.setErrors({error: this.translateService.instant(apiError.formErrors[field].i18nKey)});
              control?.markAsDirty();
            });
            this.changeDetectorRef.detectChanges();
          }
          this.formService.setError(error);
          throw error;
        }
      )).subscribe();
  }

  formatVerificationInstances(verificationList: any): any {
    if (!verificationList) {
      return [];
    }
    return verificationList?.map((verificationEntry: any) => {
      return {
        ...verificationEntry,
        verificationLocations: this.getListOfLocations(verificationEntry)
      };
    });
  }

  getListOfGeneralMethodologies(verificationData: any): GeneralMethodologiesEnum[] {
    const generalMethodologies: GeneralMethodologiesEnum[] = [];

    if(this.form.controls.reportVerification.get('administrativeVerification')?.value) {
      generalMethodologies.push(GeneralMethodologiesEnum.AdministrativeVerification);
    }
    if(this.form.controls.reportVerification.get('onTheSpotVerification')?.value) {
      generalMethodologies.push(GeneralMethodologiesEnum.OnTheSpotVerification);
    }

    return generalMethodologies;
  }

  getListOfLocations(verification: any) {
    const verificationLocations = [];
    if(verification.premisesOfProjectPartner) {
      verificationLocations.push(VerificationLocationsEnum.PremisesOfProjectPartner);
    }
    if(verification.projectEvent) {
      verificationLocations.push(VerificationLocationsEnum.ProjectEvent);
    }
    if(verification.placeOfProjectOutput) {
      verificationLocations.push(VerificationLocationsEnum.PlaceOfPhysicalProjectOutput);
    }
    if(verification.virtual) {
      verificationLocations.push(VerificationLocationsEnum.Virtual);
    }
    return verificationLocations;
  }

  removeItem(index: number) {
    this.verificationInstances.removeAt(index);
    this.formService.setDirty(true);
  }

  countryChanged(countryTitle: string): void {
    this.selectedCountry = this.findByName(countryTitle, this.nuts);
  }

  controlUserChanged(selection: string): void {
    this.selectedControllerUser = this.controllerUsers.find(user => selection === PartnerControlReportControlIdentificationTabComponent.formatControLuser(user));
  }

  reviewerUserChanged(selection: string): void {
    this.selectedReviewerUser = this.controllerUsers.find(user => selection === PartnerControlReportControlIdentificationTabComponent.formatControLuser(user));
  }

  private findByName(value: string, nuts: OutputNuts[]): OutputNuts | undefined {
    return nuts.find(nut => value === PartnerControlReportControlIdentificationTabComponent.formatRegion(nut));
  }

  private static formatRegion(region: OutputNuts): string {
    return `${region.title} (${region.code})`;
  }

  private static formatControLuser(user?: UserSimpleDTO): string {
    if (!user) {
      return '';
    }
    return `${user.name} ${user.surname} - ${user.email}`;
  }
}
