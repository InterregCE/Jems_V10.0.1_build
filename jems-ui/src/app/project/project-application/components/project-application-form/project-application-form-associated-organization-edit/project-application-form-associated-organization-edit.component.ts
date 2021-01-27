import {
  ChangeDetectionStrategy,
  Component,
  EventEmitter,
  Input,
  OnChanges,
  OnInit,
  Output,
  SimpleChanges
} from '@angular/core';
import {AbstractControl, FormBuilder, FormGroup, Validators} from '@angular/forms';
import {
  InputProjectAssociatedOrganizationAddress,
  InputProjectAssociatedOrganizationCreate,
  InputProjectAssociatedOrganizationUpdate,
  InputProjectContact,
  OutputNuts,
  OutputProjectAssociatedOrganizationDetail,
  OutputProjectPartner,
  OutputProgrammeLanguage,
} from '@cat/api';
import {Permission} from '../../../../../security/permissions/permission';
import {BaseComponent} from '@common/components/base-component';
import {Observable} from 'rxjs';
import {HttpErrorResponse} from '@angular/common/http';
import {FormService} from '@common/components/section/form/form.service';
import {takeUntil, tap} from 'rxjs/operators';

@Component({
  selector: 'app-project-application-form-associated-organization-edit',
  templateUrl: './project-application-form-associated-organization-edit.component.html',
  styleUrls: ['./project-application-form-associated-organization-edit.component.scss'],
  providers: [FormService],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ProjectApplicationFormAssociatedOrganizationEditComponent extends BaseComponent implements OnInit, OnChanges {
  Permission = Permission;
  LANGUAGE = OutputProgrammeLanguage.CodeEnum;

  // TODO: remove these and adapt the component to save independently
  @Input()
  error$: Observable<HttpErrorResponse | null>;
  @Input()
  success$: Observable<any>;

  @Input()
  nuts: OutputNuts[];
  @Input()
  partners: OutputProjectPartner[];
  @Input()
  associatedOrganization: OutputProjectAssociatedOrganizationDetail;
  @Input()
  editable: boolean;

  @Output()
  create = new EventEmitter<InputProjectAssociatedOrganizationCreate>();
  @Output()
  update = new EventEmitter<InputProjectAssociatedOrganizationUpdate>();
  @Output()
  cancel = new EventEmitter<void>();

  associatedOrganizationForm: FormGroup = this.formBuilder.group({
    id: [],
    nameInOriginalLanguage: ['', Validators.compose([
      Validators.maxLength(100),
      Validators.required])
    ],
    nameInEnglish: ['', Validators.compose([
      Validators.maxLength(100),
      Validators.required])
    ],
    partnerId: [null, Validators.required],
    country: [''],
    region2: [''],
    region3: [''],
    street: ['', Validators.maxLength(50)],
    houseNumber: ['', Validators.maxLength(20)],
    postalCode: ['', Validators.maxLength(20)],
    city: ['', Validators.maxLength(50)],
    representativeTitle: ['', Validators.maxLength(25)],
    representativeFirstName: ['', Validators.maxLength(50)],
    representativeLastName: ['', Validators.maxLength(50)],
    contactTitle: ['', Validators.maxLength(25)],
    contactFirstName: ['', Validators.maxLength(50)],
    contactLastName: ['', Validators.maxLength(50)],
    contactEmail: ['', Validators.compose([
      Validators.maxLength(255),
      Validators.email
    ])],
    contactTelephone: ['', Validators.compose([
      Validators.maxLength(25),
      Validators.pattern('^[0-9+()/-]*$')
    ])],
    roleDescription: [],
  });

  nameInOriginalLanguageErrors = {
    maxlength: 'project.organization.original.name.size.too.long',
    required: 'project.organization.original.should.not.be.empty',
  };
  nameInEnglishErrors = {
    maxlength: 'project.organization.english.name.size.too.long',
    required: 'project.organization.english.should.not.be.empty',
  };
  partnerIdErrors = {
    required: 'project.organization.partner.should.not.be.empty',
  };
  representativeTitleErrors = {
    maxlength: 'project.contact.title.size.too.long'
  };
  representativeFirstNameErrors = {
    maxlength: 'project.contact.first.name.size.too.long'
  };
  representativeLastNameErrors = {
    maxlength: 'project.contact.last.name.size.too.long'
  };
  contactTitleErrors = {
    maxlength: 'project.contact.title.size.too.long'
  };
  contactFirstNameErrors = {
    maxlength: 'project.contact.first.name.size.too.long'
  };
  contactLastNameErrors = {
    maxlength: 'project.contact.last.name.size.too.long'
  };
  contactEmailErrors = {
    maxlength: 'project.contact.email.size.too.long',
    email: 'project.contact.email.wrong.format'
  };
  contactTelephoneErrors = {
    maxlength: 'project.contact.telephone.size.too.long',
    pattern: 'project.contact.telephone.wrong.format'
  };

  constructor(private formBuilder: FormBuilder,
              private formService: FormService) {
    super();
  }

  ngOnInit(): void {
    this.resetForm();
    this.formService.init(this.associatedOrganizationForm);
    this.formService.setCreation(!this.associatedOrganization.id);
    this.formService.setEditable(this.editable);
    this.error$
      .pipe(
        takeUntil(this.destroyed$),
        tap(err => this.formService.setError(err))
      )
      .subscribe();
    this.success$
      .pipe(
        takeUntil(this.destroyed$),
        tap(() => this.formService.setSuccess('project.partner.save.success'))
      )
      .subscribe();
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes.associatedOrganization) {
      this.resetForm();
    }
  }

  get controls(): { [key: string]: AbstractControl } | undefined {
    return this.associatedOrganizationForm.controls;
  }

  onSubmit(): void {
    const toSave = {
      partnerId: this.controls?.partnerId.value,
      nameInOriginalLanguage: this.controls?.nameInOriginalLanguage.value,
      nameInEnglish: this.controls?.nameInEnglish.value,
      address: {
        country: this.controls?.country.value,
        nutsRegion2: this.controls?.region2.value,
        nutsRegion3: this.controls?.region3.value,
        street: this.controls?.street.value,
        houseNumber: this.controls?.houseNumber.value,
        postalCode: this.controls?.postalCode.value,
        city: this.controls?.city.value,
      } as InputProjectAssociatedOrganizationAddress,
      contacts: this.getContacts(),
      roleDescription: this.controls?.roleDescription.value,
    };

    if (!this.controls?.id?.value) {
      this.create.emit({...toSave} as InputProjectAssociatedOrganizationCreate);
    } else {
      this.update.emit({id: this.controls.id.value, ...toSave} as InputProjectAssociatedOrganizationUpdate);
    }
  }

  private getContacts(): InputProjectContact[] {
    const contacts: InputProjectContact[] = [];

    const contactRepresentative = {
      type: InputProjectContact.TypeEnum.LegalRepresentative,
      title: this.controls?.representativeTitle.value,
      firstName: this.controls?.representativeFirstName.value,
      lastName: this.controls?.representativeLastName.value,
    } as InputProjectContact;

    if (contactRepresentative.title || contactRepresentative.firstName || contactRepresentative.lastName) {
      contacts.push(contactRepresentative);
    }

    const person = {
      type: InputProjectContact.TypeEnum.ContactPerson,
      title: this.controls?.contactTitle.value,
      firstName: this.controls?.contactFirstName.value,
      lastName: this.controls?.contactLastName.value,
      email: this.controls?.contactEmail.value,
      telephone: this.controls?.contactTelephone.value,
    } as InputProjectContact;

    if (person.title || person.firstName || person.lastName || person.email || person.telephone) {
      contacts.push(person);
    }

    return contacts;
  }

  onCancel(): void {
    if (!this.associatedOrganization?.id) {
      this.cancel.emit();
    }
    this.resetForm();
  }

  resetForm(): void {
    this.controls?.id.setValue(this.associatedOrganization?.id);
    this.controls?.nameInOriginalLanguage.setValue(this.associatedOrganization?.nameInOriginalLanguage);
    this.controls?.nameInEnglish.setValue(this.associatedOrganization?.nameInEnglish);
    this.controls?.partnerId.setValue(this.associatedOrganization?.partner?.id);
    this.controls?.country.setValue(this.associatedOrganization?.address?.country);
    this.controls?.region2.setValue(this.associatedOrganization?.address?.nutsRegion2);
    this.controls?.region3.setValue(this.associatedOrganization?.address?.nutsRegion3);
    this.controls?.street.setValue(this.associatedOrganization?.address?.street);
    this.controls?.houseNumber.setValue(this.associatedOrganization?.address?.houseNumber);
    this.controls?.postalCode.setValue(this.associatedOrganization?.address?.postalCode);
    this.controls?.city.setValue(this.associatedOrganization?.address?.city);
    this.initLegalRepresentative();
    this.initContactPerson();
    this.controls?.roleDescription.setValue(this.associatedOrganization?.roleDescription);
  }

  private initLegalRepresentative(): void {
    const legalRepresentative = this.associatedOrganization?.contacts?.find(person => person.type === InputProjectContact.TypeEnum.LegalRepresentative);
    this.associatedOrganizationForm.controls.representativeTitle.setValue(legalRepresentative?.title);
    this.associatedOrganizationForm.controls.representativeFirstName.setValue(legalRepresentative?.firstName);
    this.associatedOrganizationForm.controls.representativeLastName.setValue(legalRepresentative?.lastName);
  }

  private initContactPerson(): void {
    const contactPerson = this.associatedOrganization?.contacts?.find(person => person.type === InputProjectContact.TypeEnum.ContactPerson);
    this.associatedOrganizationForm.controls.contactTitle.setValue(contactPerson?.title);
    this.associatedOrganizationForm.controls.contactFirstName.setValue(contactPerson?.firstName);
    this.associatedOrganizationForm.controls.contactLastName.setValue(contactPerson?.lastName);
    this.associatedOrganizationForm.controls.contactEmail.setValue(contactPerson?.email);
    this.associatedOrganizationForm.controls.contactTelephone.setValue(contactPerson?.telephone);
  }

}
