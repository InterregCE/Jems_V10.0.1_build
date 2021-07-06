import {Directive, Input, TemplateRef, ViewContainerRef} from '@angular/core';
import {ProjectStore} from '@project/project-application/containers/project-application-detail/services/project-store.service';
import {map} from 'rxjs/operators';
import {combineLatest, Observable} from 'rxjs';
import {UntilDestroy, untilDestroyed} from '@ngneat/until-destroy';
import {ApplicationFormFieldConfigurationDTO} from '@cat/api';
import AvailableInStepEnum = ApplicationFormFieldConfigurationDTO.AvailableInStepEnum;

@UntilDestroy()
@Directive({
  selector: '[appFormFieldVisibilityStatus]'
})
export class FormFieldVisibilityStatusDirective {

  private hasView = false;

  @Input() set appFormFieldVisibilityStatus(fieldId: string) {
    this.shouldTheFieldBeVisible(fieldId).pipe(untilDestroyed(this)).subscribe(shouldBeVisible => {
      if (shouldBeVisible) {
        if (!this.hasView) {
          this.viewContainer.createEmbeddedView(this.templateRef);
          this.hasView = true;
        }
      } else if (this.hasView) {
        this.viewContainer.clear();
        this.hasView = false;
      }
    });
  }

  constructor(private templateRef: TemplateRef<any>, private viewContainer: ViewContainerRef, private projectStore: ProjectStore) {
  }

  shouldTheFieldBeVisible(fieldId: string): Observable<boolean> {
    return combineLatest([this.projectStore.project$, this.projectStore.projectCall$, this.projectStore.callHasTwoSteps$]).pipe(
      map(([project, callSetting, callHasTwoSteps]) => {
        const fieldConfiguration = callSetting.applicationFormFieldConfigurations.find(it => it.id === fieldId);

        if (fieldConfiguration === undefined || !fieldConfiguration.isVisible) {
          return false;
        }

        return !callHasTwoSteps ||
        (callHasTwoSteps && this.shouldBeVisibleForTwoStepCall(fieldConfiguration, project.step2Active));

      }),
    );
  }

  private shouldBeVisibleForTwoStepCall(fieldConfiguration: ApplicationFormFieldConfigurationDTO, isApplicationInStep2: boolean): boolean {
    return fieldConfiguration.availableInStep === AvailableInStepEnum.STEPONEANDTWO ||
      (fieldConfiguration.availableInStep === AvailableInStepEnum.STEPTWOONLY && isApplicationInStep2);
  }

}
