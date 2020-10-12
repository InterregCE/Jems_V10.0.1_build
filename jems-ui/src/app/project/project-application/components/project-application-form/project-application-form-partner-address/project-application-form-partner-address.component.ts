import {
  ChangeDetectionStrategy,
  ChangeDetectorRef,
  Component,
  EventEmitter,
  Input,
  Output,
} from '@angular/core';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {ViewEditForm} from '@common/components/forms/view-edit-form';
import {SideNavService} from '@common/components/side-nav/side-nav.service';
import {FormState} from '@common/components/forms/form-state';
import {InputProjectPartnerOrganizationDetails, NutsImportService, OutputProjectPartnerOrganizationDetails} from '@cat/api';
import {Permission} from '../../../../../security/permissions/permission';

@Component({
  selector: 'app-project-application-form-partner-address',
  templateUrl: './project-application-form-partner-address.component.html',
  styleUrls: ['./project-application-form-partner-address.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ProjectApplicationFormPartnerAddressComponent extends ViewEditForm {

  @Input()
  nutsCountry: any;
  @Input()
  nutsRegion2: any;
  @Input()
  nutsRegion3: any[];
  @Input()
  nutsCountryDepartment: any;
  @Input()
  nutsRegion2Department: any;
  @Input()
  nutsRegion3Department: any[];
  @Input()
  organizationDetails: OutputProjectPartnerOrganizationDetails[];
  @Input()
  editable: boolean;
  @Input()
  showHomePage: boolean;

  @Output()
  changeCountry = new EventEmitter<any>();
  @Output()
  changeRegion = new EventEmitter<any>();
  @Output()
  changeDepartmentCountry = new EventEmitter<any>();
  @Output()
  changeDepartmentRegion = new EventEmitter<any>();
  @Output()
  update = new EventEmitter<InputProjectPartnerOrganizationDetails[]>();
  @Output()
  cancel = new EventEmitter<void>();

  Permission = Permission;
  Object = Object

  partnerAddressForm: FormGroup = this.formBuilder.group({
    partnerCountry: [''],
    partnerRegion2: [''],
    partnerRegion3: [''],
    partnerStreet: ['', Validators.maxLength(50)],
    partnerHouseNumber: ['', Validators.maxLength(20)],
    partnerPostalCode: ['', Validators.maxLength(20)],
    partnerCity: ['', Validators.maxLength(50)],
    partnerHomepage: ['', Validators.maxLength(250)],
    partnerDepartmentCountry: [''],
    partnerDepartmentRegion2: [''],
    partnerDepartmentRegion3: [''],
    partnerDepartmentStreet: ['', Validators.maxLength(50)],
    partnerDepartmentHouseNumber: ['', Validators.maxLength(20)],
    partnerDepartmentPostalCode: ['', Validators.maxLength(20)],
    partnerDepartmentCity: ['', Validators.maxLength(50)]
  })

  partnerStreetErrors = {
    maxlength: 'project.partner.address.street.size.too.long'
  };
  partnerHouseNumberErrors = {
    maxlength: 'project.partner.address.housenumber.size.too.long'
  };
  partnerPostalCodeErrors = {
    maxlength: 'project.partner.address.postalcode.size.too.long'
  };
  partnerCityErrors = {
    maxlength: 'project.partner.address.city.size.too.long'
  };
  partnerHomepageErrors = {
    maxlength: 'project.partner.address.homepage.size.too.long'
  };

  private static isOrganizationDtoEmpty(partnerOrganizationDetails: InputProjectPartnerOrganizationDetails): boolean {
    return !(partnerOrganizationDetails.country || partnerOrganizationDetails.nutsRegion2 || partnerOrganizationDetails.nutsRegion3 ||
        partnerOrganizationDetails.street || partnerOrganizationDetails.houseNumber || partnerOrganizationDetails.postalCode ||
        partnerOrganizationDetails.city );
  }

  private static getValidatedDataToEmit(partnerOrganizationMainAddress: InputProjectPartnerOrganizationDetails,
                                        partnerOrganizationDepartmentAddress: InputProjectPartnerOrganizationDetails): InputProjectPartnerOrganizationDetails[]{
    const dataToEmit : InputProjectPartnerOrganizationDetails[] = [];
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

  getForm(): FormGroup | null {
    return this.partnerAddressForm;
  }

  onSubmit(): void {
    const partnerOrganizationAddress = {
      type: InputProjectPartnerOrganizationDetails.TypeEnum.Organization,
      country: this.partnerAddressForm.controls.partnerCountry.value,
      nutsRegion2: this.partnerAddressForm.controls.partnerRegion2.value,
      nutsRegion3: this.partnerAddressForm.controls.partnerRegion3.value,
      street: this.partnerAddressForm.controls.partnerStreet.value,
      houseNumber: this.partnerAddressForm.controls.partnerHouseNumber.value,
      postalCode: this.partnerAddressForm.controls.partnerPostalCode.value,
      city: this.partnerAddressForm.controls.partnerCity.value,
      homepage: this.partnerAddressForm.controls.partnerHomepage.value
    }
    const partnerDepartmentAddress = {
      type: InputProjectPartnerOrganizationDetails.TypeEnum.Department,
      country: this.partnerAddressForm.controls.partnerDepartmentCountry.value,
      nutsRegion2: this.partnerAddressForm.controls.partnerDepartmentRegion2.value,
      nutsRegion3: this.partnerAddressForm.controls.partnerDepartmentRegion3.value,
      street: this.partnerAddressForm.controls.partnerDepartmentStreet.value,
      houseNumber: this.partnerAddressForm.controls.partnerDepartmentHouseNumber.value,
      postalCode: this.partnerAddressForm.controls.partnerDepartmentPostalCode.value,
      city: this.partnerAddressForm.controls.partnerDepartmentCity.value,
      homepage: '',
    }
    this.update.emit( ProjectApplicationFormPartnerAddressComponent.getValidatedDataToEmit(partnerOrganizationAddress, partnerDepartmentAddress))
  }

  onCancel(): void {
    this.changeFormState$.next(FormState.VIEW);
  }

  countryChanged(country: any): void {
    this.partnerAddressForm.controls.partnerRegion2.reset();
    this.partnerAddressForm.controls.partnerRegion3.reset();
    this.changeCountry.emit(country);
  }

  regionChanged(region: any): void {
    this.partnerAddressForm.controls.partnerRegion3.reset();
    this.changeRegion.emit(region);
  }

  countryDepartmentChanged(country: any): void {
    this.partnerAddressForm.controls.partnerDepartmentRegion2.reset();
    this.partnerAddressForm.controls.partnerDepartmentRegion3.reset();
    this.changeDepartmentCountry.emit(country);
  }

  regionDepartmentChanged(region: any): void {
    this.partnerAddressForm.controls.partnerDepartmentRegion3.reset();
    this.changeDepartmentRegion.emit(region);
  }

  private initPartnerOrganizationMainAddressFields(): void {
    const partnerMainAddress = this.organizationDetails?.find( person => person.type === OutputProjectPartnerOrganizationDetails.TypeEnum.Organization)
    this.partnerAddressForm.controls.partnerCountry.setValue(partnerMainAddress?.country);
    this.partnerAddressForm.controls.partnerRegion2.setValue(partnerMainAddress?.nutsRegion2);
    this.partnerAddressForm.controls.partnerRegion3.setValue(partnerMainAddress?.nutsRegion3);
    this.partnerAddressForm.controls.partnerStreet.setValue(partnerMainAddress?.street);
    this.partnerAddressForm.controls.partnerHouseNumber.setValue(partnerMainAddress?.houseNumber);
    this.partnerAddressForm.controls.partnerPostalCode.setValue(partnerMainAddress?.postalCode);
    this.partnerAddressForm.controls.partnerCity.setValue(partnerMainAddress?.city);
    this.partnerAddressForm.controls.partnerHomepage.setValue(partnerMainAddress?.homepage);
  }

  private initPartnerOrganizationDepartmentAddressFields(): void {
    const partnerMainAddress = this.organizationDetails?.find( person => person.type === OutputProjectPartnerOrganizationDetails.TypeEnum.Department)
    this.partnerAddressForm.controls.partnerDepartmentCountry.setValue(partnerMainAddress?.country);
    this.partnerAddressForm.controls.partnerDepartmentRegion2.setValue(partnerMainAddress?.nutsRegion2);
    this.partnerAddressForm.controls.partnerDepartmentRegion3.setValue(partnerMainAddress?.nutsRegion3);
    this.partnerAddressForm.controls.partnerDepartmentStreet.setValue(partnerMainAddress?.street);
    this.partnerAddressForm.controls.partnerDepartmentHouseNumber.setValue(partnerMainAddress?.houseNumber);
    this.partnerAddressForm.controls.partnerDepartmentPostalCode.setValue(partnerMainAddress?.postalCode);
    this.partnerAddressForm.controls.partnerDepartmentCity.setValue(partnerMainAddress?.city);
  }

}
