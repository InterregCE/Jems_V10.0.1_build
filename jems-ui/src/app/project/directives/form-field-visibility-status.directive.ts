import {Directive, Input, TemplateRef, ViewContainerRef} from '@angular/core';
import {UntilDestroy, untilDestroyed} from '@ngneat/until-destroy';
import {ApplicationFormModel} from '@project/application-form-model';
import {FormVisibilityStatusService} from '@project/services/form-visibility-status.service';

@UntilDestroy()
@Directive({
  selector: '[appFormFieldVisibilityStatus]'
})
export class FormFieldVisibilityStatusDirective {

  private hasView = false;

  @Input() set appFormFieldVisibilityStatus(fieldIds: string | ApplicationFormModel) {
    if (fieldIds === undefined || fieldIds === null || (typeof fieldIds === 'string' && fieldIds.length === 0) || (typeof fieldIds === 'object' && Object.keys(fieldIds).length === 0)) {
      return;
    }
    this.visibilityStatusService.isVisible$(fieldIds).pipe(untilDestroyed(this)).subscribe(shouldBeVisible => {
      this.handleElementVisibility(shouldBeVisible);
    });
  }

  constructor(private templateRef: TemplateRef<any>, private viewContainer: ViewContainerRef, private visibilityStatusService: FormVisibilityStatusService) {
  }

  private handleElementVisibility(shouldBeVisible: boolean): void {
    if (shouldBeVisible) {
      if (!this.hasView) {
        this.viewContainer.createEmbeddedView(this.templateRef);
        this.hasView = true;
      }
    } else if (this.hasView) {
      this.viewContainer.clear();
      this.hasView = false;
    }
  }

}
