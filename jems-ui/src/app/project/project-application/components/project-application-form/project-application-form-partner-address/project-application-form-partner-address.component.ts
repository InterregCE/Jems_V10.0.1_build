import {ChangeDetectionStrategy, Component, Input, OnChanges, OnInit, SimpleChanges} from '@angular/core';
import {AbstractControl, FormBuilder, FormGroup, Validators} from '@angular/forms';
import {OutputNuts, ProjectPartnerAddressDTO} from '@cat/api';
import {FormService} from '@common/components/section/form/form.service';
import {ProjectPartnerStore} from '../../../containers/project-application-form-page/services/project-partner-store.service';
import {catchError, filter, take, tap} from 'rxjs/operators';
import {APPLICATION_FORM} from '@project/common/application-form-model';
import {UntilDestroy, untilDestroyed} from '@ngneat/until-destroy';
import {ActivatedRoute} from '@angular/router';
import {RoutingService} from '@common/services/routing.service';
import {FormVisibilityStatusService} from '@project/common/services/form-visibility-status.service';
import {Alert} from '@common/components/forms/alert';

@UntilDestroy()
@Component({
  selector: 'jems-project-application-form-partner-address',
  templateUrl: './project-application-form-partner-address.component.html',
  styleUrls: ['./project-application-form-partner-address.component.scss'],
  providers: [FormService],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ProjectApplicationFormPartnerAddressComponent implements OnInit, OnChanges {
  Alert = Alert;

  @Input()
  partnerId: number;
  @Input()
  nuts: OutputNuts[];
  @Input()
  organizationDetails: ProjectPartnerAddressDTO[];
  APPLICATION_FORM = APPLICATION_FORM;

  partnerAddressForm: FormGroup = this.formBuilder.group({
    organization: this.formBuilder.group({
      country: [''],
      countryCode: [''],
      region2: [''],
      region2Code: [''],
      region3: [''],
      region3Code: [''],
      street: ['', Validators.maxLength(50)],
      houseNumber: ['', Validators.maxLength(20)],
      postalCode: ['', Validators.maxLength(20)],
      city: ['', Validators.maxLength(50)],
      homepage: ['', Validators.maxLength(250)],
    }),
    department: this.formBuilder.group({
      country: [''],
      countryCode: [''],
      region2: [''],
      region2Code: [''],
      region3: [''],
      region3Code: [''],
      street: ['', Validators.maxLength(50)],
      houseNumber: ['', Validators.maxLength(20)],
      postalCode: ['', Validators.maxLength(20)],
      city: ['', Validators.maxLength(50)],
    })
  });

  private static isOrganizationDtoEmpty(partnerOrganizationDetails: ProjectPartnerAddressDTO): boolean {
    return !(partnerOrganizationDetails.country || partnerOrganizationDetails.nutsRegion2 || partnerOrganizationDetails.nutsRegion3 ||
      partnerOrganizationDetails.street || partnerOrganizationDetails.houseNumber || partnerOrganizationDetails.postalCode ||
      partnerOrganizationDetails.city || partnerOrganizationDetails.homepage);
  }

  private static getValidatedDataToEmit(partnerOrganizationMainAddress: ProjectPartnerAddressDTO,
                                        partnerOrganizationDepartmentAddress: ProjectPartnerAddressDTO): ProjectPartnerAddressDTO[] {
    const dataToEmit: ProjectPartnerAddressDTO[] = [];
    if (!ProjectApplicationFormPartnerAddressComponent.isOrganizationDtoEmpty(partnerOrganizationMainAddress)) {
      dataToEmit.push(partnerOrganizationMainAddress);
    }
    if (!ProjectApplicationFormPartnerAddressComponent.isOrganizationDtoEmpty(partnerOrganizationDepartmentAddress)) {
      dataToEmit.push(partnerOrganizationDepartmentAddress);
    }
    return dataToEmit;
  }

  constructor(private formBuilder: FormBuilder,
              private formService: FormService,
              private partnerStore: ProjectPartnerStore,
              private activatedRoute: ActivatedRoute,
              private router: RoutingService,
              private visibilityStatusService: FormVisibilityStatusService
              ) {
    visibilityStatusService.isVisible$((APPLICATION_FORM.SECTION_B.ADDRESS)).pipe(
      untilDestroyed(this),
      filter(isVisible => !isVisible),
      tap(() => this.router.navigate(['../identity'], {relativeTo: this.activatedRoute, queryParamsHandling: 'merge'})),
    ).subscribe();
  }

  ngOnInit(): void {
    this.formService.init(this.partnerAddressForm, this.partnerStore.isProjectEditable$);
    this.resetForm();
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes.partnerId || changes.organizationDetails) {
      this.resetForm();
    }
  }

  get organization(): { [key: string]: AbstractControl } {
    return (this.partnerAddressForm.controls?.organization as FormGroup).controls;
  }

  get department(): { [key: string]: AbstractControl } {
    return (this.partnerAddressForm.controls?.department as FormGroup).controls;
  }

  resetForm(): void {
    this.initPartnerOrganizationMainAddressFields();
    this.initPartnerOrganizationDepartmentAddressFields();
  }

  onSubmit(): void {
    const partnerOrganizationAddress = {
      type: ProjectPartnerAddressDTO.TypeEnum.Organization,
      country: this.organization.country.value,
      countryCode: this.organization.countryCode.value,
      nutsRegion2: this.organization.region2.value,
      nutsRegion2Code: this.organization.region2Code.value,
      nutsRegion3: this.organization.region3.value,
      nutsRegion3Code: this.organization.region3Code.value,
      street: this.organization.street.value,
      houseNumber: this.organization.houseNumber.value,
      postalCode: this.organization.postalCode.value,
      city: this.organization.city.value,
      homepage: this.organization.homepage.value
    };
    const partnerDepartmentAddress = {
      type: ProjectPartnerAddressDTO.TypeEnum.Department,
      country: this.department.country.value,
      countryCode: this.department.countryCode.value,
      nutsRegion2: this.department.region2.value,
      nutsRegion2Code: this.department.region2Code.value,
      nutsRegion3: this.department.region3.value,
      nutsRegion3Code: this.department.region3Code.value,
      street: this.department.street.value,
      houseNumber: this.department.houseNumber.value,
      postalCode: this.department.postalCode.value,
      city: this.department.city.value,
      homepage: ''
    };

    this.partnerStore.updatePartnerAddress(
      ProjectApplicationFormPartnerAddressComponent.getValidatedDataToEmit(
        partnerOrganizationAddress, partnerDepartmentAddress
      )
    )
      .pipe(
        take(1),
        tap(() => this.formService.setSuccess('project.partner.main-address.save.success')),
        catchError(error => this.formService.setError(error))
      ).subscribe();
  }

  private initPartnerOrganizationMainAddressFields(): void {
    const partnerMainAddress = this.organizationDetails?.find(
      person => person.type === ProjectPartnerAddressDTO.TypeEnum.Organization);
    this.organization.country.setValue(partnerMainAddress?.country);
    this.organization.countryCode.setValue(partnerMainAddress?.countryCode);
    this.organization.region2.setValue(partnerMainAddress?.nutsRegion2);
    this.organization.region2Code.setValue(partnerMainAddress?.nutsRegion2Code);
    this.organization.region3.setValue(partnerMainAddress?.nutsRegion3);
    this.organization.region3Code.setValue(partnerMainAddress?.nutsRegion3Code);
    this.organization.street.setValue(partnerMainAddress?.street);
    this.organization.houseNumber.setValue(partnerMainAddress?.houseNumber);
    this.organization.postalCode.setValue(partnerMainAddress?.postalCode);
    this.organization.city.setValue(partnerMainAddress?.city);
    this.organization.homepage.setValue(partnerMainAddress?.homepage);
  }

  private initPartnerOrganizationDepartmentAddressFields(): void {
    const partnerMainAddress = this.organizationDetails?.find(
      person => person.type === ProjectPartnerAddressDTO.TypeEnum.Department);
    this.department.country.setValue(partnerMainAddress?.country);
    this.department.countryCode.setValue(partnerMainAddress?.countryCode);
    this.department.region2.setValue(partnerMainAddress?.nutsRegion2);
    this.department.region2Code.setValue(partnerMainAddress?.nutsRegion2Code);
    this.department.region3.setValue(partnerMainAddress?.nutsRegion3);
    this.department.region3Code.setValue(partnerMainAddress?.nutsRegion3Code);
    this.department.street.setValue(partnerMainAddress?.street);
    this.department.houseNumber.setValue(partnerMainAddress?.houseNumber);
    this.department.postalCode.setValue(partnerMainAddress?.postalCode);
    this.department.city.setValue(partnerMainAddress?.city);
  }
}
