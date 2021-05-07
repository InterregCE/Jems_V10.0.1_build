import {
  ChangeDetectionStrategy,
  ChangeDetectorRef,
  Component,
  EventEmitter,
  OnInit,
  Output
} from '@angular/core';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {CallDetailDTO, InputProject, ProjectCallSettingsDTO} from '@cat/api';
import {AbstractForm} from '@common/components/forms/abstract-form';
import {ActivatedRoute, Router} from '@angular/router';
import {Permission} from '../../../../security/permissions/permission';
import {TranslateService} from '@ngx-translate/core';
import {CallStore} from '../../../../call/services/call-store.service';
import moment from 'moment';
import {LocaleDatePipe} from '../../../../common/pipe/locale-date.pipe';

@Component({
  selector: 'app-project-application-submission',
  templateUrl: 'project-application-submission.component.html',
  styleUrls: ['project-application-submission.component.scss'],
  providers: [CallStore],
  changeDetection: ChangeDetectionStrategy.OnPush
})

export class ProjectApplicationSubmissionComponent extends AbstractForm implements OnInit {

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

  callId = this.activatedRoute.snapshot.params.callId;
  call: CallDetailDTO;

  constructor(public callStore: CallStore,
              private formBuilder: FormBuilder,
              private router: Router,
              private localeDatePipe: LocaleDatePipe,
              protected changeDetectorRef: ChangeDetectorRef,
              protected translationService: TranslateService,
              protected activatedRoute: ActivatedRoute) {
    super(changeDetectorRef, translationService);
  }

  ngOnInit(): void {
    super.ngOnInit();

    this.callStore.init(this.callId);
    this.callStore.call$.subscribe(call => {
        this.call = call;
      }
    );
  }

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

  navigateToCallDetails(): void {
    this.router.navigate(['app/call/detail/' + this.callId]);
  }

  getFormattedDateTime(date: Date): string {
    return this.localeDatePipe.transform(date);
  }

  getFormattedTimeLeft(dateToFormat: Date): { [key: string]: string | number } {
    const endDate = moment(dateToFormat);
    const now = moment(new Date());
    const diff = moment.duration(endDate.diff(now));
    const daysLeft = Math.floor(diff.asDays());

    return {
      date: this.localeDatePipe.transform(dateToFormat),
      days: daysLeft > 0 ? daysLeft : 0,
      hours: diff.hours() > 0 ? diff.hours() : 0,
      minutes: diff.minutes() > 0 ? diff.minutes() : 0
    };
  }

}
