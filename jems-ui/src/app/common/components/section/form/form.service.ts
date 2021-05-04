import {Injectable} from '@angular/core';
import {BehaviorSubject, Observable, of, ReplaySubject, Subject} from 'rxjs';
import {FormGroup} from '@angular/forms';
import {I18nLabel} from '../../../i18n/i18n-label';
import {Log} from '../../../utils/log';
import {filter, tap} from 'rxjs/operators';
import {RoutingService} from '../../../services/routing.service';
import {HttpErrorResponse} from '@angular/common/http';
import {UntilDestroy, untilDestroyed} from '@ngneat/until-destroy';
import {TranslateService} from '@ngx-translate/core';
import {APIError} from '../../../models/APIError';

@UntilDestroy()
@Injectable()
export class FormService {
  private form: FormGroup;
  private resetSubject = new Subject();
  private editable = true;

  saveLabel = 'common.save.label';
  valid$ = new ReplaySubject<boolean>(1);
  dirty$ = new ReplaySubject<boolean>(1);
  success$ = new Subject<I18nLabel | string | null>();
  pending$ = new BehaviorSubject<boolean>(false);
  error$ = new Subject<APIError | null>();
  reset$ = this.resetSubject.asObservable();

  constructor(private routingService: RoutingService, private translateService: TranslateService) {
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
        tap(editable => {
          this.editable = editable;
          this.setEditable(editable);
        }),
        untilDestroyed(this)
      ).subscribe();
  }

  setError(httpError: HttpErrorResponse | null): Observable<any> {
    if (!httpError) {
      this.error$.next(null);
      return of(null);
    }

    const apiError = httpError.error || {i18nMessage: {}};
    this.error$.next(apiError);
    this.pending$.next(false);
    this.success$.next(null);
    this.setValid(false);
    if (!apiError.i18nMessage?.i18nKey && !apiError?.message) {
      apiError.i18nMessage.i18nKey = 'incomplete.form';
    }
    this.setFieldErrors(apiError);

    throw httpError;
  }

  setSuccess(message: I18nLabel | string | null): void {
    this.success$.next(message);
    this.error$.next(null);
    this.pending$.next(false);
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
    this.valid$.next(valid);
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
    this.editable = editable;
  }

  resetEditable(): void {
    this.setEditable(this.editable);
    this.setDirty(false);
  }

  reset(): void {
    this.resetSubject.next();
  }

  private setFieldErrors(error: APIError): void {
    if (!this.form) {
      return;
    }
    Log.debug('Set form backend errors.', this, error);
    Object.keys(this.form?.controls).forEach(key => {
      if (!error?.formErrors || !error.formErrors[key]) {
        return;
      }
      this.form?.controls[key].setErrors({error: this.translateService.instant(error.formErrors[key].i18nKey, error.formErrors[key].i18nArguments)});
      this.form?.controls[key].markAsTouched();
    });
  }
}
