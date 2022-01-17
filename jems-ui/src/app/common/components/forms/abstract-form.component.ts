import {ChangeDetectorRef, Component, Input, OnInit} from '@angular/core';
import {Observable, Subject} from 'rxjs';
import {FormGroup} from '@angular/forms';
import {filter, takeUntil} from 'rxjs/operators';
import {BaseComponent} from '@common/components/base-component';
import {Log} from '../../utils/log';
import {Alert} from './alert';
import {APIError} from '../../models/APIError';
import {TranslateService} from '@ngx-translate/core';

@Component({
  template: ''
})
export abstract class AbstractFormComponent extends BaseComponent implements OnInit {
  Alert = Alert;

  @Input()
  error$: Observable<APIError | null>;
  @Input()
  success$: Observable<any>;

  showSuccessMessage$ = new Subject<boolean>();
  submitted = false;
  clearOnSuccess = false;
  permanentSuccessAlert = false;

  protected constructor(protected changeDetectorRef: ChangeDetectorRef, protected translateService: TranslateService) {
    super();
  }

  abstract getForm(): FormGroup | null;

  ngOnInit(): void {
    if (this.error$) {
      this.handleError();
    }

    if (this.success$) {
      this.handleSuccess();
    }
  }

  private handleError(): void {
    this.error$
      .pipe(takeUntil(this.destroyed$))
      .subscribe((error: APIError) => {
        this.submitted = false;
        this.setFieldErrors(error, this.getForm());
      });
  }

  private setFieldErrors(error: APIError, form: FormGroup | null): void {
    if (!form) {
      return;
    }
    Log.debug('Set form backend errors.', this, error);
    Object.keys(form.controls).forEach(key => {
      if (!error?.formErrors || !error.formErrors[key]) {
        return;
      }
      form.controls[key].setErrors({error: this.translateService.instant(error.formErrors[key].i18nKey, error.formErrors[key].i18nArguments)});
      form.controls[key].markAsTouched();
      this.changeDetectorRef.markForCheck();
    });
  }

  private handleSuccess(): void {
    this.success$
      .pipe(
        takeUntil(this.destroyed$),
        filter(success => !!success)
      )
      .subscribe(() => {
        this.submitted = false;

        Log.debug('Show success message for 4 seconds.', this);
        this.showSuccessMessage$.next(true);
        if (!this.permanentSuccessAlert) {
          setTimeout(() => this.showSuccessMessage$.next(false), 4000);
        }

        this.handleClearFormOnSuccess();
      });
  }

  private handleClearFormOnSuccess(): void {
    if (!this.clearOnSuccess) {
      return;
    }
    const formGroup = this.getForm();
    if (!formGroup) {
      return;
    }
    Log.debug('Reset form and errors.', this);
    formGroup.reset();
    Object.keys(formGroup.controls).forEach(key => {
      formGroup.get(key)?.setErrors(null);
    });
    formGroup.setErrors([]); // marks the form as invalid without marking the fields with errors.
  }

}
