import {
  ChangeDetectionStrategy,
  ChangeDetectorRef,
  Component,
  EventEmitter,
  Input,
  OnChanges,
  Output,
  SimpleChanges,
} from '@angular/core';
import {AbstractControl, FormBuilder, FormGroup, Validators} from '@angular/forms';
import {ViewEditForm} from '@common/components/forms/view-edit-form';
import {SideNavService} from '@common/components/side-nav/side-nav.service';
import {FormState} from '@common/components/forms/form-state';
import {InputProjectPartnerAddress, NutsImportService, OutputProjectPartnerAddress, OutputNuts} from '@cat/api';
import {Permission} from '../../../../../security/permissions/permission';

@Component({
  selector: 'app-project-application-form-partner-address',
  templateUrl: './project-application-form-partner-address.component.html',
  styleUrls: ['./project-application-form-partner-address.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ProjectApplicationFormPartnerAddressComponent extends ViewEditForm implements OnChanges {
  Permission = Permission;

  @Input()
  partnerId: number;
  @Input()
  nuts: OutputNuts[];
  @Input()
  organizationDetails: OutputProjectPartnerAddress[];
  @Input()
  editable: boolean;

  @Output()
  update = new EventEmitter<InputProjectPartnerAddress[]>();

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
  })

  private static isOrganizationDtoEmpty(partnerOrganizationDetails: InputProjectPartnerAddress): boolean {
    return !(partnerOrganizationDetails.country || partnerOrganizationDetails.nutsRegion2 || partnerOrganizationDetails.nutsRegion3 ||
      partnerOrganizationDetails.street || partnerOrganizationDetails.houseNumber || partnerOrganizationDetails.postalCode ||
      partnerOrganizationDetails.city);
  }

  private static getValidatedDataToEmit(partnerOrganizationMainAddress: InputProjectPartnerAddress,
                                        partnerOrganizationDepartmentAddress: InputProjectPartnerAddress): InputProjectPartnerAddress[]{
    const dataToEmit : InputProjectPartnerAddress[] = [];
    if (!ProjectApplicationFormPartnerAddressComponent.isOrganizationDtoEmpty(partnerOrganizationMainAddress)) {
      dataToEmit.push(partnerOrganizationMainAddress);
    }
    if (!ProjectApplicationFormPartnerAddressComponent.isOrganizationDtoEmpty(partnerOrganizationDepartmentAddress)) {
      dataToEmit.push(partnerOrganizationDepartmentAddress);
    }
    return dataToEmit;
  }

  constructor(private formBuilder: FormBuilder,
              protected changeDetectorRef: ChangeDetectorRef,
              private nutsService: NutsImportService,
              private sideNavService: SideNavService) {
    super(changeDetectorRef);
  }

  protected enterViewMode() {
    this.sideNavService.setAlertStatus(false);
    this.initPartnerOrganizationMainAddressFields();
    this.initPartnerOrganizationDepartmentAddressFields();
  }

  protected enterEditMode(): void {
    this.sideNavService.setAlertStatus(true);
  }

  ngOnChanges(changes: SimpleChanges) {
    if (changes.partnerId) {
      this.changeFormState$.next(FormState.VIEW);
    }
  }

  getForm(): FormGroup | null {
    return this.partnerAddressForm;
  }

  get organization(): {[key: string]: AbstractControl} {
    return (this.partnerAddressForm.controls?.organization as FormGroup).controls;
  }

  get department(): {[key: string]: AbstractControl} {
    return (this.partnerAddressForm.controls?.department as FormGroup).controls;
  }

  onSubmit(): void {
    const partnerOrganizationAddress = {
      type: InputProjectPartnerAddress.TypeEnum.Organization,
      country: this.organization.country.value,
      nutsRegion2: this.organization.region2.value,
      nutsRegion3: this.organization.region3.value,
      street: this.organization.street.value,
      houseNumber: this.organization.houseNumber.value,
      postalCode: this.organization.postalCode.value,
      city: this.organization.city.value,
      homepage: this.organization.homepage.value
    }
    const partnerDepartmentAddress = {
      type: InputProjectPartnerAddress.TypeEnum.Department,
      country: this.department.country.value,
      nutsRegion2: this.department.region2.value,
      nutsRegion3: this.department.region3.value,
      street: this.department.street.value,
      houseNumber: this.department.houseNumber.value,
      postalCode: this.department.postalCode.value,
      city: this.department.city.value,
      homepage: ''
    }
    this.update.emit(ProjectApplicationFormPartnerAddressComponent.getValidatedDataToEmit(
        partnerOrganizationAddress, partnerDepartmentAddress)
    )
  }

  onCancel(): void {
    this.changeFormState$.next(FormState.VIEW);
  }

  private initPartnerOrganizationMainAddressFields(): void {
    const partnerMainAddress = this.organizationDetails?.find(
        person => person.type === OutputProjectPartnerAddress.TypeEnum.Organization)
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
        person => person.type === OutputProjectPartnerAddress.TypeEnum.Department)
    this.department.country.setValue(partnerMainAddress?.country);
    this.department.region2.setValue(partnerMainAddress?.nutsRegion2);
    this.department.region3.setValue(partnerMainAddress?.nutsRegion3);
    this.department.street.setValue(partnerMainAddress?.street);
    this.department.houseNumber.setValue(partnerMainAddress?.houseNumber);
    this.department.postalCode.setValue(partnerMainAddress?.postalCode);
    this.department.city.setValue(partnerMainAddress?.city);
  }

}
