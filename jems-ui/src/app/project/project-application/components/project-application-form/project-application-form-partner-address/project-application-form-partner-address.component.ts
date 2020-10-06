import {
  ChangeDetectionStrategy,
  ChangeDetectorRef,
  Component,
  EventEmitter,
  Input,
  OnInit,
  Output,
} from '@angular/core';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {ViewEditForm} from '@common/components/forms/view-edit-form';
import {SideNavService} from '@common/components/side-nav/side-nav.service';
import {FormState} from '@common/components/forms/form-state';
import {InputProjectPartnerOrganizationDetails, NutsImportService, OutputProjectPartnerDetail} from '@cat/api';
import {Permission} from '../../../../../security/permissions/permission';

@Component({
  selector: 'app-project-application-form-partner-address',
  templateUrl: './project-application-form-partner-address.component.html',
  styleUrls: ['./project-application-form-partner-address.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ProjectApplicationFormPartnerAddressComponent extends ViewEditForm implements OnInit {

  @Input()
  nutsCountry: any;
  @Input()
  nutsRegion2: any;
  @Input()
  nutsRegion3: any[];
  @Input()
  partner: OutputProjectPartnerDetail;
  @Input()
  editable: boolean;

  @Output()
  changeCountry = new EventEmitter<any>();
  @Output()
  changeRegion = new EventEmitter<any>();
  @Output()
  update = new EventEmitter<InputProjectPartnerOrganizationDetails>();
  @Output()
  cancel = new EventEmitter<void>();

  constructor(private formBuilder: FormBuilder,
              protected changeDetectorRef: ChangeDetectorRef,
              private nutsService: NutsImportService,
              private sideNavService: SideNavService) {
    super(changeDetectorRef);
  }

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
  })

  partnerStreetErrors = {
    maxLength: 'project.partner.main-address.street.size.too.long'
  };
  partnerHouseNumberErrors = {
    maxLength: 'project.partner.main-address.housenumber.size.too.long'
  };
  partnerPostalCodeErrors = {
    maxLength: 'project.partner.main-address.postalcode.size.too.long'
  };
  partnerCityErrors = {
    maxLength: 'project.partner.main-address.city.size.too.long'
  };
  partnerHomepageErrors = {
    maxLength: 'project.partner.main-address.homepage.size.too.long'
  };

  ngOnInit() {
    super.ngOnInit();
    this.changeFormState$.next(FormState.EDIT);
  }

  protected enterViewMode() {
    this.sideNavService.setAlertStatus(false);
    this.initPartnerMainAddressFields();
  }

  protected enterEditMode(): void {
    this.sideNavService.setAlertStatus(true);
  }

  getForm(): FormGroup | null {
    return this.partnerAddressForm;
  }

  onSubmit(): void {
    this.update.emit( {
        country: this.partnerAddressForm.controls.partnerCountry.value,
        nutsRegion2: this.partnerAddressForm.controls.partnerRegion2.value,
        nutsRegion3: this.partnerAddressForm.controls.partnerRegion3.value,
        street: this.partnerAddressForm.controls.partnerStreet.value,
        houseNumber: this.partnerAddressForm.controls.partnerHouseNumber.value,
        postalCode: this.partnerAddressForm.controls.partnerPostalCode.value,
        city: this.partnerAddressForm.controls.partnerCity.value,
        homepage: this.partnerAddressForm.controls.partnerHomepage.value,
        }
    )
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

  private initPartnerMainAddressFields(): void {
    const partnerMainAddress = this.partner?.organization?.organizationDetails;
    this.partnerAddressForm.controls.partnerCountry.setValue(partnerMainAddress?.country);
    this.partnerAddressForm.controls.partnerRegion2.setValue(partnerMainAddress?.nutsRegion2);
    this.partnerAddressForm.controls.partnerRegion3.setValue(partnerMainAddress?.nutsRegion3);
    this.partnerAddressForm.controls.partnerStreet.setValue(partnerMainAddress?.street);
    this.partnerAddressForm.controls.partnerHouseNumber.setValue(partnerMainAddress?.houseNumber);
    this.partnerAddressForm.controls.partnerPostalCode.setValue(partnerMainAddress?.postalCode);
    this.partnerAddressForm.controls.partnerCity.setValue(partnerMainAddress?.city);
    this.partnerAddressForm.controls.partnerHomepage.setValue(partnerMainAddress?.homepage);
  }

}
