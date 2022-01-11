import {ChangeDetectionStrategy, Component, Input, OnInit} from '@angular/core';
import {ProjectStatusDTO} from '@cat/api';
import {FormService} from '@common/components/section/form/form.service';
import {FormBuilder, Validators} from '@angular/forms';
import {of} from 'rxjs';
import {ModificationPageStore} from '@project/project-application/modification-page/modification-page-store.service';
import {catchError} from 'rxjs/operators';
import {UntilDestroy, untilDestroyed} from '@ngneat/until-destroy';
import {ProjectUtil} from '@project/common/project-util';

@UntilDestroy()
@Component({
  selector: 'app-modification-confirmation',
  templateUrl: './modification-confirmation.component.html',
  styleUrls: ['./modification-confirmation.component.scss'],
  providers: [FormService],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ModificationConfirmationComponent implements OnInit {
  ProjectStatus = ProjectStatusDTO.StatusEnum;

  @Input()
  index: number;
  @Input()
  decision: ProjectStatusDTO;
  @Input()
  projectStatus: ProjectStatusDTO;

  decisionForm = this.formBuilder.group({
    status: ['', Validators.required],
    decisionDate: ['', Validators.required],
    entryIntoForceDate: ['', Validators.required],
    note: ['', Validators.maxLength(10000)],
  });
  today = new Date();
  dateErrors = {
    matDatepickerMax: 'project.decision.date.must.be.in.the.past',
    matDatepickerParse: 'common.date.should.be.valid'
  };

  constructor(private formBuilder: FormBuilder,
              private formService: FormService,
              private pageStore: ModificationPageStore) {
  }

  ngOnInit(): void {
    this.formService.init(this.decisionForm, of(!this.decision));
    if (this.decision) {
      this.decisionForm.patchValue({
        ...this.decision,
        decisionDate: new Date(this.decision.decisionDate),
        entryIntoForceDate: this.decision.entryIntoForceDate && new Date(this.decision.entryIntoForceDate)
      });
    }
  }

  saveConfirmation(): void {
    const info = {
      note: this.decisionForm.get('note')?.value,
      date: this.decisionForm.get('decisionDate')?.value?.format('YYYY-MM-DD'),
      entryIntoForceDate: this.decisionForm.get('entryIntoForceDate')?.value?.format('YYYY-MM-DD'),
    };
    if (this.decisionForm.get('status')?.value === ProjectStatusDTO.StatusEnum.APPROVED) {
      this.pageStore.approveModification(info)
        .pipe(
          untilDestroyed(this),
          catchError(err => this.formService.setError(err)))
        .subscribe();
    } else if (this.decisionForm.get('status')?.value === ProjectStatusDTO.StatusEnum.MODIFICATIONREJECTED) {
      this.pageStore.rejectModification(info)
        .pipe(
          untilDestroyed(this),
          catchError(err => this.formService.setError(err)))
        .subscribe();
    }
  }

  getDecision() {
    console.log('decision', this.decision);
    if (this.decision) {
      if (this.decision.status === ProjectStatusDTO.StatusEnum.MODIFICATIONREJECTED) {
        return 'Rejected';
      } else return 'Approved';
    } else return 'Open';
  }

  hasOpenStatusColor(): boolean {
    return !this.decision
  }

  hasDeclinedStatusColor(): boolean {
    return this.decision?.status === ProjectStatusDTO.StatusEnum.MODIFICATIONREJECTED;
  }

  hasAcceptedStatusColor(): boolean {
    return this.decision?.status === ProjectStatusDTO.StatusEnum.APPROVED;
  }
}
