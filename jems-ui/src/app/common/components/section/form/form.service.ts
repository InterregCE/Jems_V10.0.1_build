import {Injectable} from '@angular/core';
import {I18nValidationError} from '@common/validation/i18n-validation-error';
import {Observable, of, ReplaySubject, Subject} from 'rxjs';
import {FormGroup} from '@angular/forms';
import {I18nLabel} from '../../../i18n/i18n-label';
import {Log} from '../../../utils/log';
import {filter, tap} from 'rxjs/operators';
import {RoutingService} from '../../../services/routing.service';
import {HttpErrorResponse} from '@angular/common/http';
import {UntilDestroy, untilDestroyed} from '@ngneat/until-destroy';

@UntilDestroy()
@Injectable()
export class FormService {
  private form: FormGroup;
  private additionalValidators?: (() => boolean)[];
  private resetSubject = new Subject();

  saveLabel = 'common.save.label';
  valid$ = new ReplaySubject<boolean>(1);
  dirty$ = new ReplaySubject<boolean>(1);
  success$ = new Subject<I18nLabel | string | null>();
  error$ = new Subject<I18nValidationError | null>();
  reset$ = this.resetSubject.asObservable();

  constructor(private routingService: RoutingService) {
  }

  init(form: FormGroup, editable$?: Observable<boolean>): void {
    this.form = form;
    this.form?.valueChanges
      .pipe(
        filter(() => this.form.dirty),
        tap(() => this.setDirty(true)),
      ).subscribe();

    if (!editable$) {
      return;
    }
    editable$
      .pipe(
        tap(editable => this.setEditable(editable)),
        untilDestroyed(this)
      ).subscribe();
  }

  setError(error: HttpErrorResponse | null): Observable<any> {
    if (!error || !error.error) {
      this.error$.next(null);
      return of(null);
    }

    const i18nError = error.error;
    this.error$.next(i18nError);
    if (i18nError) {
      this.success$.next(null);
      if (!i18nError?.i18nKey) {
        i18nError.i18nKey = 'incomplete.form';
      }
      this.setFieldErrors(i18nError);
    }

    throw error;
  }

  setSuccess(message: I18nLabel | string | null): void {
    this.success$.next(message);
    this.error$.next(null);
    if (message) {
      setTimeout(() => this.success$.next(null), 3000);
    }
  }

  setDirty(dirty: boolean): void {
    if (!dirty) {
      // mark form as pristine in order to ignore 'dirty' statuses from formChanged$
      if (this.form) {
        this.form.markAsPristine();
        this.form.markAsUntouched();
      }
      this.routingService.confirmLeave = false;
    } else {
      this.success$.next(null);
      this.routingService.confirmLeave = true;
    }
    this.setValid(this.form?.valid);
    this.dirty$.next(dirty);
  }

  setValid(valid: boolean): void {
    this.valid$.next(
      valid && this.additionalValidators?.every(validator => validator())
    );
  }

  setCreation(isCreationForm: boolean): void {
    this.saveLabel = isCreationForm ? 'common.create.label' : 'common.save.label';
    this.setDirty(isCreationForm);
  }

  setEditable(editable: boolean): void {
    if (editable) {
      this.form?.enable();
    } else {
      this.form?.disable();
    }
  }

  setAdditionalValidators(additionalValidators?: (() => boolean)[]): void {
    this.additionalValidators = additionalValidators;
  }

  reset(): void {
    this.resetSubject.next();
  }

  private setFieldErrors(error: I18nValidationError): void {
    if (!this.form) {
      return;
    }
    Log.debug('Set form backend errors.', this, error);
    Object.keys(this.form?.controls).forEach(key => {
      if (!error?.i18nFieldErrors || !error.i18nFieldErrors[key]) {
        return;
      }
      this.form?.controls[key].setErrors({i18nError: error.i18nFieldErrors[key].i18nKey});
      this.form?.controls[key].markAsTouched();
    });
  }
}
