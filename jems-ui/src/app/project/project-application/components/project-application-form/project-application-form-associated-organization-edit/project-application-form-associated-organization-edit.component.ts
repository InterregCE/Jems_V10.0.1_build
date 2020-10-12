import {
  ChangeDetectionStrategy,
  ChangeDetectorRef,
  Component,
  EventEmitter,
  Input,
  OnInit,
  Output
} from '@angular/core';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {AbstractForm} from '@common/components/forms/abstract-form';
import {
  OutputProjectPartner,
  InputProjectAssociatedOrganizationCreate,
  OutputProjectAssociatedOrganization,
  OutputProjectAssociatedOrganizationDetail
} from '@cat/api';

@Component({
  selector: 'app-project-application-form-associated-organization-edit',
  templateUrl: './project-application-form-associated-organization-edit.component.html',
  styleUrls: ['./project-application-form-associated-organization-edit.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ProjectApplicationFormAssociatedOrganizationEditComponent extends AbstractForm implements OnInit {

  @Input()
  nutsCountry: any;
  @Input()
  nutsRegion2: any;
  @Input()
  nutsRegion3: any[];
  @Input()
  partner: OutputProjectPartner[];
  @Input()
  associatedOrganization: OutputProjectAssociatedOrganizationDetail;

  @Output()
  changeCountry = new EventEmitter<any>();
  @Output()
  changeRegion = new EventEmitter<any>();
  @Output()
  changePartner = new EventEmitter<any>();
  @Output()
  update = new EventEmitter<InputProjectAssociatedOrganizationCreate[]>();
  @Output()
  cancel = new EventEmitter<void>();

  Object = Object

  associatedOrganizationForm: FormGroup = this.formBuilder.group({
    nameInOriginalLanguage: ['', Validators.maxLength(100)],
    nameInEnglish: ['', Validators.maxLength(100)],
    responsiblePartner: [],
    associatedOrganizationCountry: [''],
    associatedOrganizationRegion2: [''],
    associatedOrganizationRegion3: [''],
    associatedOrganizationStreet: ['', Validators.maxLength(50)],
    associatedOrganizationHouseNumber: ['', Validators.maxLength(20)],
    associatedOrganizationPostalCode: ['', Validators.maxLength(20)],
    associatedOrganizationCity: ['', Validators.maxLength(50)],
    associatedOrganizationRepresentativeTitle: ['', Validators.maxLength(25)],
    associatedOrganizationRepresentativeFirstName: ['', Validators.maxLength(50)],
    associatedOrganizationRepresentativeLastName: ['', Validators.maxLength(50)],
    associatedOrganizationContactTitle: ['', Validators.maxLength(25)],
    associatedOrganizationContactFirstName: ['', Validators.maxLength(50)],
    associatedOrganizationContactLastName: ['', Validators.maxLength(50)],
    associatedOrganizationContactEmail: ['', Validators.compose([
      Validators.maxLength(255),
      Validators.email
    ])],
    associatedOrganizationContactTelephone: ['', Validators.compose([
      Validators.maxLength(25),
      Validators.pattern('^[0-9+()/-]*$')
    ])],
    associatedOrganizationRole: [],
  })

  nameInOriginalLanguageErrors = {
    maxlength: 'partner.organization.original.name.size.too.long'
  };
  nameInEnglishErrors = {
    maxlength: 'partner.organization.english.name.size.too.long'
  };
  associatedOrganizationStreetErrors = {
    maxlength: 'partner.organization.english.name.size.too.long'
  };
  associatedOrganizationHouseNumberErrors = {
    maxlength: 'partner.organization.english.name.size.too.long'
  };
  associatedOrganizationPostalCodeErrors = {
    maxlength: 'partner.organization.english.name.size.too.long'
  };
  associatedOrganizationCityErrors = {
    maxlength: 'partner.organization.english.name.size.too.long'
  };
  associatedOrganizationRepresentativeTitleErrors = {
    maxlength: 'partner.contact.representative.title.size.too.long'
  };
  associatedOrganizationRepresentativeFirstNameErrors = {
    maxlength: 'partner.contact.representative.first.name.size.too.long'
  };
  associatedOrganizationRepresentativeLastNameErrors = {
    maxlength: 'partner.contact.representative.last.name.size.too.long'
  };
  associatedOrganizationContactTitleErrors = {
    maxlength: 'partner.contact.title.size.too.long'
  };
  associatedOrganizationContactFirstNameErrors = {
    maxlength: 'partner.contact.first.name.size.too.long'
  };
  associatedOrganizationContactLastNameErrors = {
    maxlength: 'partner.contact.last.name.size.too.long'
  };
  associatedOrganizationContactEmailErrors = {
    maxlength: 'partner.contact.email.size.too.long',
    email: 'partner.contact.email.wrong.format'
  };
  associatedOrganizationContactTelephoneErrors = {
    maxlength: 'partner.contact.telephone.size.too.long',
    pattern: 'partner.contact.telephone.wrong.format'
  };

  constructor(private formBuilder: FormBuilder,
              protected changeDetectorRef: ChangeDetectorRef) {
    super(changeDetectorRef);
  }

  ngOnInit(): void {
  }

  getForm(): FormGroup | null {
    return this.associatedOrganizationForm;
  }

  protected enterViewMode() {
    // this.sideNavService.setAlertStatus(false);
    this.initAssociatedOrganizationAddressFields();
    // this.initPartnerOrganizationDepartmentAddressFields();
  }

  private initAssociatedOrganizationAddressFields(): void {
    const associatedOrganizationAddress = this.associatedOrganization?.organizationAddress
    this.associatedOrganizationForm.controls.partnerCountry.setValue(associatedOrganizationAddress?.country);
    this.associatedOrganizationForm.controls.partnerRegion2.setValue(associatedOrganizationAddress?.nutsRegion2);
    this.associatedOrganizationForm.controls.partnerRegion3.setValue(associatedOrganizationAddress?.nutsRegion3);
    this.associatedOrganizationForm.controls.partnerStreet.setValue(associatedOrganizationAddress?.street);
    this.associatedOrganizationForm.controls.partnerHouseNumber.setValue(associatedOrganizationAddress?.houseNumber);
    this.associatedOrganizationForm.controls.partnerPostalCode.setValue(associatedOrganizationAddress?.postalCode);
    this.associatedOrganizationForm.controls.partnerCity.setValue(associatedOrganizationAddress?.city);
  }

  private initLegalRepresentative() {
    const legalRepresentative = this.associatedOrganization?.associatedOrganizationContacts?.find(person => person.type === InputProjectPartnerContact.TypeEnum.LegalRepresentative)
    this.partnerContactForm.controls.partnerRepresentativeTitle.setValue(legalRepresentative?.title);
    this.partnerContactForm.controls.partnerRepresentativeFirstName.setValue(legalRepresentative?.firstName);
    this.partnerContactForm.controls.partnerRepresentativeLastName.setValue(legalRepresentative?.lastName);
  }

  private initContactPerson() {
    const contactPerson = this.partner?.partnerContactPersons?.find(person => person.type === InputProjectPartnerContact.TypeEnum.ContactPerson)
    this.partnerContactForm.controls.partnerContactTitle.setValue(contactPerson?.title);
    this.partnerContactForm.controls.partnerContactFirstName.setValue(contactPerson?.firstName);
    this.partnerContactForm.controls.partnerContactLastName.setValue(contactPerson?.lastName);
    this.partnerContactForm.controls.partnerContactEmail.setValue(contactPerson?.email);
    this.partnerContactForm.controls.partnerContactTelephone.setValue(contactPerson?.telephone);
  }

  countryChanged(country: any): void {
    this.associatedOrganizationForm.controls.associatedOrganizationRegion2.reset();
    this.associatedOrganizationForm.controls.associatedOrganizationRegion3.reset();
    this.changeCountry.emit(country);
  }

  regionChanged(region: any): void {
    this.associatedOrganizationForm.controls.associatedOrganizationRegion3.reset();
    this.changeRegion.emit(region);
  }

  partnerChanged(partner: any): void {
    this.changePartner.emit(partner);
  }
}
