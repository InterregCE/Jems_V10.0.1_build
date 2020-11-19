import {
  ChangeDetectionStrategy,
  Component,
  Input,
  OnChanges, OnInit,
  SimpleChanges,
} from '@angular/core';
import {AbstractControl, FormBuilder, FormGroup, Validators} from '@angular/forms';
import {OutputNuts, ProjectPartnerAddressDTO} from '@cat/api';
import {FormService} from '@common/components/section/form/form.service';
import {ProjectPartnerStore} from '../../../containers/project-application-form-page/services/project-partner-store.service';
import {catchError, take, tap} from 'rxjs/operators';

@Component({
  selector: 'app-project-application-form-partner-address',
  templateUrl: './project-application-form-partner-address.component.html',
  styleUrls: ['./project-application-form-partner-address.component.scss'],
  providers: [FormService],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ProjectApplicationFormPartnerAddressComponent implements OnInit, OnChanges {
  @Input()
  partnerId: number;
  @Input()
  nuts: OutputNuts[];
  @Input()
  organizationDetails: ProjectPartnerAddressDTO[];
  @Input()
  editable: boolean;

  partnerAddressForm: FormGroup = this.formBuilder.group({
    organization: this.formBuilder.group({
      country: [''],
      region2: [''],
      region3: [''],
      street: ['', Validators.maxLength(50)],
      houseNumber: ['', Validators.maxLength(20)],
      postalCode: ['', Validators.maxLength(20)],
      city: ['', Validators.maxLength(50)],
      homepage: ['', Validators.maxLength(250)],
    }),
    department: this.formBuilder.group({
      country: [''],
      region2: [''],
      region3: [''],
      street: ['', Validators.maxLength(50)],
      houseNumber: ['', Validators.maxLength(20)],
      postalCode: ['', Validators.maxLength(20)],
      city: ['', Validators.maxLength(50)],
    })
  });

  private static isOrganizationDtoEmpty(partnerOrganizationDetails: ProjectPartnerAddressDTO): boolean {
    return !(partnerOrganizationDetails.country || partnerOrganizationDetails.nutsRegion2 || partnerOrganizationDetails.nutsRegion3 ||
      partnerOrganizationDetails.street || partnerOrganizationDetails.houseNumber || partnerOrganizationDetails.postalCode ||
      partnerOrganizationDetails.city);
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
              private partnerStore: ProjectPartnerStore) {
  }

  ngOnInit(): void {
    this.formService.init(this.partnerAddressForm);
    this.resetForm();
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes.partnerId) {
      this.resetForm();
      this.formService.setDirty(false);
    }
  }

  get organization(): { [key: string]: AbstractControl } {
    return (this.partnerAddressForm.controls?.organization as FormGroup).controls;
  }

  get department(): { [key: string]: AbstractControl } {
    return (this.partnerAddressForm.controls?.department as FormGroup).controls;
  }

  resetForm(): void {
    this.formService.setEditable(this.editable);
    this.initPartnerOrganizationMainAddressFields();
    this.initPartnerOrganizationDepartmentAddressFields();
  }

  onSubmit(): void {
    const partnerOrganizationAddress = {
      type: ProjectPartnerAddressDTO.TypeEnum.Organization,
      country: this.organization.country.value,
      nutsRegion2: this.organization.region2.value,
      nutsRegion3: this.organization.region3.value,
      street: this.organization.street.value,
      houseNumber: this.organization.houseNumber.value,
      postalCode: this.organization.postalCode.value,
      city: this.organization.city.value,
      homepage: this.organization.homepage.value
    };
    const partnerDepartmentAddress = {
      type: ProjectPartnerAddressDTO.TypeEnum.Department,
      country: this.department.country.value,
      nutsRegion2: this.department.region2.value,
      nutsRegion3: this.department.region3.value,
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
    this.organization.region2.setValue(partnerMainAddress?.nutsRegion2);
    this.organization.region3.setValue(partnerMainAddress?.nutsRegion3);
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
    this.department.region2.setValue(partnerMainAddress?.nutsRegion2);
    this.department.region3.setValue(partnerMainAddress?.nutsRegion3);
    this.department.street.setValue(partnerMainAddress?.street);
    this.department.houseNumber.setValue(partnerMainAddress?.houseNumber);
    this.department.postalCode.setValue(partnerMainAddress?.postalCode);
    this.department.city.setValue(partnerMainAddress?.city);
  }
}
