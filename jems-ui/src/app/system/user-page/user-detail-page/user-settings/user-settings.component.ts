import {
  ChangeDetectionStrategy,
  ChangeDetectorRef,
  Component,
  EventEmitter,
  Input,
  OnInit,
  Output
} from '@angular/core';
import {FormBuilder, FormGroup} from '@angular/forms';
import {take, tap} from 'rxjs/operators';
import {MatDialog} from '@angular/material/dialog';
import {TranslateService} from '@ngx-translate/core';
import {FormState} from '@common/components/forms/form-state';
import {UserDetailPageStore} from '../user-detail-page-store.service';
import {UserSettingsDTO} from '@cat/api';
import {AbstractFormComponent} from '@common/components/forms/abstract-form.component';

@Component({
  selector: 'jems-user-settings',
  templateUrl: './user-settings.component.html',
  styleUrls: ['./user-settings.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class UserSettingsComponent extends AbstractFormComponent implements OnInit {

  @Input()
  ownUser: boolean;
  @Input()
  userSettings: UserSettingsDTO;
  @Input()
  disabled: boolean;
  @Input()
  userId: number;
  @Output()
  settingsFormState: EventEmitter<FormState> = new EventEmitter<FormState>();

  clearOnSuccess = false;
  settingsForm: FormGroup = this.formBuilder.group({
    sendNotificationsToEmail: this.formBuilder.control(false)
  });

  constructor(private formBuilder: FormBuilder,
              private dialog: MatDialog,
              private userStore: UserDetailPageStore,
              protected changeDetectorRef: ChangeDetectorRef,
              protected translationService: TranslateService) {
    super(changeDetectorRef, translationService);
  }

  ngOnInit(): void {
    this.settingsFormState.next(FormState.EDIT);
    this.resetForm();
    super.ngOnInit();
  }

  getForm(): FormGroup | null {
    return this.settingsForm;
  }

  onSubmit(): void {
    this.submitted = true;
    this.userStore.updateSettings({id: this.userId, ...this.settingsForm.value})
      .pipe(
        take(1),
        tap(() => this.getForm()?.markAsPristine())
      ).subscribe();
  }

  resetForm() {
    this.settingsForm.reset();
    const controls: { [key: string]: any } = {
      sendNotificationsToEmail: this.formBuilder.control(this.userSettings.sendNotificationsToEmail)
    };
    this.settingsForm = this.formBuilder.group(controls);
  }
}
