import {ChangeDetectionStrategy, Component, Input, OnInit} from '@angular/core';
import {ProjectStatusDTO} from '@cat/api';
import {FormService} from '@common/components/section/form/form.service';
import {FormBuilder, Validators} from '@angular/forms';
import {of} from 'rxjs';
import {ModificationPageStore} from '@project/project-application/modification-page/modification-page-store.service';
import {catchError} from 'rxjs/operators';

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
              private pageStore: ModificationPageStore) { }

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
      note: this.decisionForm.get('note')?.value as any,
      date: this.decisionForm.get('decisionDate')?.value?.format('YYYY-MM-DD') as any,
      entryIntoForceDate: this.decisionForm.get('entryIntoForceDate')?.value?.format('YYYY-MM-DD') as any,
    };
    if (this.decisionForm.get('status')?.value === ProjectStatusDTO.StatusEnum.APPROVED) {
      this.pageStore.approveApplication(info)
        .pipe(catchError(err => this.formService.setError(err)))
        .subscribe();
    }
  }
}
