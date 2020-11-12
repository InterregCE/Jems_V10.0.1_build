import {
  ChangeDetectionStrategy,
  ChangeDetectorRef,
  Component,
  EventEmitter,
  Input, OnChanges,
  OnInit,
  Output, SimpleChanges
} from '@angular/core';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {ActivatedRoute} from '@angular/router';
import {SideNavService} from '@common/components/side-nav/side-nav.service';
import {FormState} from '@common/components/forms/form-state';
import {ViewEditForm} from '@common/components/forms/view-edit-form';
import {OutputProjectPartnerDetail, InputProjectContact} from '@cat/api';
import {Permission} from '../../../../../security/permissions/permission';

@Component({
  selector: 'app-project-application-form-partner-contact',
  templateUrl: './project-application-form-partner-contact.component.html',
  styleUrls: ['./project-application-form-partner-contact.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ProjectApplicationFormPartnerContactComponent extends ViewEditForm implements OnInit, OnChanges {
  Permission = Permission;

  @Input()
  partner: OutputProjectPartnerDetail;
  @Input()
  editable: boolean;

  @Output()
  update = new EventEmitter<InputProjectContact[]>();

  partnerContactForm: FormGroup = this.formBuilder.group({
    partnerRepresentativeTitle: ['', Validators.maxLength(25)],
    partnerRepresentativeFirstName: ['', Validators.maxLength(50)],
    partnerRepresentativeLastName: ['', Validators.maxLength(50)],
    partnerContactTitle: ['', Validators.maxLength(25)],
    partnerContactFirstName: ['', Validators.maxLength(50)],
    partnerContactLastName: ['', Validators.maxLength(50)],
    partnerContactEmail: ['', Validators.compose([
      Validators.maxLength(255),
      Validators.email
    ])],
    partnerContactTelephone: ['', Validators.compose([
      Validators.maxLength(25),
      Validators.pattern('^[0-9+()/-]*$')
    ])]
  });

  partnerRepresentativeTitleErrors = {
    maxlength: 'partner.contact.representative.title.size.too.long'
  };
  partnerRepresentativeFirstNameErrors = {
    maxlength: 'partner.contact.representative.first.name.size.too.long'
  };
  partnerRepresentativeLastNameErrors = {
    maxlength: 'partner.contact.representative.last.name.size.too.long'
  };
  partnerContactTitleErrors = {
    maxlength: 'project.contact.title.size.too.long'
  };
  partnerContactFirstNameErrors = {
    maxlength: 'project.contact.first.name.size.too.long'
  };
  partnerContactLastNameErrors = {
    maxlength: 'project.contact.last.name.size.too.long'
  };
  partnerContactEmailErrors = {
    maxlength: 'project.contact.email.size.too.long',
    email: 'project.contact.email.wrong.format'
  };
  partnerContactTelephoneErrors = {
    maxlength: 'project.contact.telephone.size.too.long',
    pattern: 'project.contact.telephone.wrong.format'
  };

  private static isContactDtoEmpty(contactDto: InputProjectContact): boolean {
    return !(contactDto.title || contactDto.firstName || contactDto.lastName ||
      contactDto.email || contactDto.telephone);
  }

  private static getValidatedDataToEmit(legalRepresentative: InputProjectContact,
                                        contactPerson: InputProjectContact): InputProjectContact[] {
    const dataToEmit: InputProjectContact[] = [];
    if (!ProjectApplicationFormPartnerContactComponent.isContactDtoEmpty(legalRepresentative)) {
      dataToEmit.push(legalRepresentative);
    }
    if (!ProjectApplicationFormPartnerContactComponent.isContactDtoEmpty(contactPerson)) {
      dataToEmit.push(contactPerson);
    }
    return dataToEmit;
  }

  constructor(private formBuilder: FormBuilder,
              protected changeDetectorRef: ChangeDetectorRef,
              private activatedRoute: ActivatedRoute,
              private sideNavService: SideNavService) {
    super(changeDetectorRef);
  }

  ngOnInit(): void {
    super.ngOnInit();
    this.enterViewMode();
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes.partner) {
      this.changeFormState$.next(FormState.VIEW);
    }
  }

  protected enterViewMode(): void {
    this.sideNavService.setAlertStatus(false);
    this.initLegalRepresentative();
    this.initContactPerson();
  }

  protected enterEditMode(): void {
    this.sideNavService.setAlertStatus(true);
  }

  getForm(): FormGroup | null {
    return this.partnerContactForm;
  }

  onSubmit(): void {
    const legalRepresentative = {
      type: InputProjectContact.TypeEnum.LegalRepresentative,
      title: this.partnerContactForm.controls.partnerRepresentativeTitle.value,
      firstName: this.partnerContactForm.controls.partnerRepresentativeFirstName.value,
      lastName: this.partnerContactForm.controls.partnerRepresentativeLastName.value,
      email: '',
      telephone: ''
    };
    const contactPerson = {
      type: InputProjectContact.TypeEnum.ContactPerson,
      title: this.partnerContactForm.controls.partnerContactTitle.value,
      firstName: this.partnerContactForm.controls.partnerContactFirstName.value,
      lastName: this.partnerContactForm.controls.partnerContactLastName.value,
      email: this.partnerContactForm.controls.partnerContactEmail.value,
      telephone: this.partnerContactForm.controls.partnerContactTelephone.value
    };
    this.update.emit(ProjectApplicationFormPartnerContactComponent.getValidatedDataToEmit(legalRepresentative, contactPerson));
  }

  onCancel(): void {
    this.changeFormState$.next(FormState.VIEW);
  }

  private initLegalRepresentative(): void {
    const legalRepresentative = this.partner?.contacts?.find(person => person.type === InputProjectContact.TypeEnum.LegalRepresentative);
    this.partnerContactForm.controls.partnerRepresentativeTitle.setValue(legalRepresentative?.title);
    this.partnerContactForm.controls.partnerRepresentativeFirstName.setValue(legalRepresentative?.firstName);
    this.partnerContactForm.controls.partnerRepresentativeLastName.setValue(legalRepresentative?.lastName);
  }

  private initContactPerson(): void {
    const contactPerson = this.partner?.contacts?.find(person => person.type === InputProjectContact.TypeEnum.ContactPerson);
    this.partnerContactForm.controls.partnerContactTitle.setValue(contactPerson?.title);
    this.partnerContactForm.controls.partnerContactFirstName.setValue(contactPerson?.firstName);
    this.partnerContactForm.controls.partnerContactLastName.setValue(contactPerson?.lastName);
    this.partnerContactForm.controls.partnerContactEmail.setValue(contactPerson?.email);
    this.partnerContactForm.controls.partnerContactTelephone.setValue(contactPerson?.telephone);
  }

}
