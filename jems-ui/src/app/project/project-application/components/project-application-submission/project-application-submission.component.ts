import {ChangeDetectionStrategy, ChangeDetectorRef, Component, EventEmitter, Output} from '@angular/core';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {InputProject} from '@cat/api';
import {AbstractForm} from '@common/components/forms/abstract-form';
import {ActivatedRoute, Router} from '@angular/router';
import {Permission} from '../../../../security/permissions/permission';
import {TranslateService} from '@ngx-translate/core';

@Component({
  selector: 'app-project-application-submission',
  templateUrl: 'project-application-submission.component.html',
  styleUrls: ['project-application-submission.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})

export class ProjectApplicationSubmissionComponent extends AbstractForm {

  @Output()
  submitProjectApplication: EventEmitter<InputProject> = new EventEmitter<InputProject>();

  clearOnSuccess = true;
  Permission = Permission;

  submissionForm = this.formBuilder.group({
    acronym: ['', Validators.compose([
      Validators.required,
      Validators.maxLength(25)
    ])]
  });

  acronymErrors = {
    maxlength: 'project.acronym.size.too.long',
    required: 'project.acronym.should.not.be.empty'
  };

  constructor(private formBuilder: FormBuilder,
              private router: Router,
              protected changeDetectorRef: ChangeDetectorRef,
              protected translationService: TranslateService,
              protected activatedRoute: ActivatedRoute) {
    super(changeDetectorRef, translationService);
  }

  callId = this.activatedRoute.snapshot.params.callId;

  getForm(): FormGroup | null {
    return this.submissionForm;
  }

  onSubmit(): void {
    this.submitProjectApplication.emit({
      acronym: this.submissionForm?.controls?.acronym?.value,
      projectCallId: this.callId
    });
  }

  onCancel(): void {
    this.router.navigate(['/app/call']);
  }
}
