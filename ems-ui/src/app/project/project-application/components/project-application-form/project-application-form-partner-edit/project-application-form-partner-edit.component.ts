import {
  ChangeDetectionStrategy,
  ChangeDetectorRef,
  Component,
  EventEmitter,
  Input,
  OnInit,
  Output
} from '@angular/core';
import {ViewEditForm} from '@common/components/forms/view-edit-form';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {InputProjectPartnerCreate, InputProjectPartnerUpdate, OutputProjectPartner} from '@cat/api';
import {FormState} from '@common/components/forms/form-state';
import {filter, take, takeUntil, tap} from 'rxjs/operators';
import {I18nValidationError} from '@common/validation/i18n-validation-error';
import {MatDialog} from '@angular/material/dialog';
import {Forms} from '../../../../../common/utils/forms';
import {SideNavService} from '@common/components/side-nav/side-nav.service';

@Component({
  selector: 'app-project-application-form-partner-edit',
  templateUrl: './project-application-form-partner-edit.component.html',
  styleUrls: ['./project-application-form-partner-edit.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ProjectApplicationFormPartnerEditComponent extends ViewEditForm implements OnInit {
  RoleEnum = OutputProjectPartner.RoleEnum;

  @Input()
  partner: OutputProjectPartner;
  @Input()
  editable: boolean;

  @Output()
  create = new EventEmitter<InputProjectPartnerCreate>();
  @Output()
  update = new EventEmitter<InputProjectPartnerUpdate>();
  @Output()
  cancel = new EventEmitter<void>();

  partnerForm: FormGroup = this.formBuilder.group({
    fakeRole: [], // needed for the fake role field in view mode
    id: [],
    name: ['', Validators.compose([
      Validators.maxLength(15),
      Validators.required])
    ],
    role: ['', Validators.required],
  });

  nameErrors = {
    maxlength: 'project.partner.name.size.too.long',
    required: 'project.partner.name.should.not.be.empty'
  };
  roleErrors = {
    required: 'project.partner.role.should.not.be.empty',
  };

  constructor(private formBuilder: FormBuilder,
              private dialog: MatDialog,
              protected changeDetectorRef: ChangeDetectorRef,
              private sideNavService: SideNavService) {
    super(changeDetectorRef);
  }

  ngOnInit(): void {
    super.ngOnInit();
    if (this.editable && !this.partner?.id) {
      this.changeFormState$.next(FormState.EDIT);
    }
    if (!this.error$) {
      return;
    }
    this.error$
      .pipe(
        takeUntil(this.destroyed$),
        filter(error => !!error && error.i18nKey === 'project.partner.role.lead.already.existing'),
        tap(error => this.handleLeadAlreadyExisting(this.controls, error as I18nValidationError))
      ).subscribe()
  }

  getForm(): FormGroup | null {
    return this.partnerForm;
  }

  onSubmit(controls: any, oldPartnerId?: number): void {
    if (!controls.id?.value) {
      this.create.emit({
        name: controls?.name.value,
        role: controls?.role.value,
        oldLeadPartnerId: oldPartnerId as any
      });
      return;
    }
    this.update.emit({
      id: controls?.id.value,
      name: controls?.name.value,
      role: controls?.role.value,
      oldLeadPartnerId: oldPartnerId as any
    })
  }

  onCancel(): void {
    if (!this.partner?.id) {
      this.cancel.emit();
    }
    this.changeFormState$.next(FormState.VIEW);
  }

  protected enterViewMode(): void {
    this.controls?.id.setValue(this.partner?.id);
    this.controls?.role.setValue(this.partner?.role);
    this.controls?.name.setValue(this.partner?.name);
    this.sideNavService.setAlertStatus(false);
  }

  protected enterEditMode(): void {
    this.controls?.id.setValue(this.partner?.id);
    this.controls?.role.setValue(this.partner?.role);
    this.controls?.name.setValue(this.partner?.name);
    this.sideNavService.setAlertStatus(true);
  }

  private handleLeadAlreadyExisting(controls: any, error: I18nValidationError): void {
    const partnerName = error.i18nArguments ? error.i18nArguments[1] : null;
    const partnerId = error.i18nArguments ? error.i18nArguments[0] : null;
    Forms.confirmDialog(
      this.dialog,
      'project.partner.role.lead.already.existing.title',
      'project.partner.role.lead.already.existing',
      {
        old_name: partnerName,
        new_name: controls.name.value
      }
    ).pipe(
      take(1),
      takeUntil(this.destroyed$),
      filter(change => !!change)
    ).subscribe(() => {
      this.onSubmit(controls, partnerId as any);
    });
  }
}
