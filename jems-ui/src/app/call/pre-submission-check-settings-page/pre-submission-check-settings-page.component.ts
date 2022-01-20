import {ChangeDetectionStrategy, Component} from '@angular/core';
import {Alert} from '@common/components/forms/alert';
import {FormService} from '@common/components/section/form/form.service';
import {combineLatest, Observable} from 'rxjs';
import {FormBuilder, FormControl, Validators} from '@angular/forms';
import {PreSubmissionCheckSettingsPageStore} from './pre-submission-check-settings-page-store.service';
import {PluginInfoDTO} from '@cat/api';
import {catchError, map, tap} from 'rxjs/operators';
import {CallPageSidenavService} from '../services/call-page-sidenav.service';
import {UntilDestroy, untilDestroyed} from '@ngneat/until-destroy';

@UntilDestroy()
@Component({
  selector: 'jems-pre-submission-check-settings-page',
  templateUrl: './pre-submission-check-settings-page.component.html',
  styleUrls: ['./pre-submission-check-settings-page.component.scss'],
  providers: [FormService, PreSubmissionCheckSettingsPageStore],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class PreSubmissionCheckSettingsPageComponent {

  Alert = Alert;

  data$: Observable<{
    pluginKey: string;
    preSubmissionCheckPlugins: PluginInfoDTO[];
    callIsEditable: boolean;
  }>;

  form = this.formBuilder.group({
    plugin: ['', Validators.required],
  });

  constructor(private formService: FormService,
              private formBuilder: FormBuilder,
              private pageStore: PreSubmissionCheckSettingsPageStore,
              private callSidenavService: CallPageSidenavService) {
    this.formService.init(this.form);
    pageStore.callPreSubmissionCheckPluginKey$.pipe(tap(([pluginKey]) => this.resetForm(pluginKey)));
    this.data$ = combineLatest([pageStore.callIsEditable$, pageStore.preSubmissionCheckPlugins, pageStore.callPreSubmissionCheckPluginKey$]).pipe(
      map(([callIsEditable, preSubmissionCheckPlugins, pluginKey]) => {
        this.resetForm(pluginKey);
        return {
          pluginKey,
          preSubmissionCheckPlugins,
          callIsEditable
        };
      })
    );
  }

  resetForm(pluginKey: String): void {
    this.pluginKey.setValue(pluginKey);
  }

  save(): void {
    this.pageStore.save(this.pluginKey.value).pipe(
      tap(() => this.formService.setSuccess('call.detail.application.form.config.saved.success')),
      catchError(error => this.formService.setError(error)),
      untilDestroyed(this)
    ).subscribe();
  }

  get pluginKey(): FormControl {
    return this.form.get('plugin') as FormControl;
  }
}
