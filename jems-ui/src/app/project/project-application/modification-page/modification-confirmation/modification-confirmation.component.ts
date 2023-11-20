import {ChangeDetectionStrategy, Component, Input, OnInit} from '@angular/core';
import {
  ApplicationActionInfoDTO,
  AuditControlCorrectionDTO,
  ProjectModificationCreateDTO,
  ProjectModificationDecisionDTO,
  ProjectStatusDTO,
  ProjectVersionDTO
} from '@cat/api';
import {FormService} from '@common/components/section/form/form.service';
import {FormBuilder, Validators} from '@angular/forms';
import {of} from 'rxjs';
import {ModificationPageStore} from '@project/project-application/modification-page/modification-page-store.service';
import {catchError} from 'rxjs/operators';
import {UntilDestroy, untilDestroyed} from '@ngneat/until-destroy';

@UntilDestroy()
@Component({
  selector: 'jems-modification-confirmation',
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
  projectStatus: ProjectStatusDTO;
  @Input()
  version: ProjectVersionDTO;
  @Input()
  currentStatus: ProjectStatusDTO.StatusEnum;
  @Input()
  corrections: AuditControlCorrectionDTO[];

  canEdit: boolean;
  decisionForm = this.formBuilder.group({
    status: ['', Validators.required],
    decisionDate: ['', Validators.required],
    entryIntoForceDate: ['', Validators.required],
    note: ['', Validators.maxLength(10000)],
    corrections: [],
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
    this.canEdit = !this.projectStatus && [ProjectStatusDTO.StatusEnum.MODIFICATIONPRECONTRACTINGSUBMITTED, ProjectStatusDTO.StatusEnum.MODIFICATIONSUBMITTED]
      .includes(this.currentStatus);
    this.formService.init(this.decisionForm, of(this.canEdit));
    if (this.projectStatus) {
      this.decisionForm.patchValue({
        status: this.projectStatus.status,
        decisionDate: new Date(this.projectStatus.decisionDate),
        entryIntoForceDate: this.projectStatus.entryIntoForceDate && new Date(this.projectStatus.entryIntoForceDate),
        note: this.projectStatus.note,
        corrections: this.corrections,
      });
    }
  }

  saveConfirmation(): void {
    const actionInfo = {
      note: this.decisionForm.get('note')?.value,
      date: this.decisionForm.get('decisionDate')?.value?.format('YYYY-MM-DD'),
      entryIntoForceDate: this.decisionForm.get('entryIntoForceDate')?.value?.format('YYYY-MM-DD'),
    } as ApplicationActionInfoDTO;
    const correctionIds = this.decisionForm.get('corrections')?.value?.map((correction: AuditControlCorrectionDTO) => correction.id);
    const modificationCreateDto = {actionInfo, correctionIds} as ProjectModificationCreateDTO;

    if (this.decisionForm.get('status')?.value === ProjectStatusDTO.StatusEnum.APPROVED) {
      this.pageStore.approveModification(modificationCreateDto)
        .pipe(
          untilDestroyed(this),
          catchError(err => this.formService.setError(err)))
        .subscribe();
    } else if (this.decisionForm.get('status')?.value === ProjectStatusDTO.StatusEnum.MODIFICATIONREJECTED) {
      this.pageStore.rejectModification(modificationCreateDto)
        .pipe(
          untilDestroyed(this),
          catchError(err => this.formService.setError(err)))
        .subscribe();
    }
  }

  getDecision() {
    if (this.projectStatus) {
      if (this.projectStatus.status === ProjectStatusDTO.StatusEnum.MODIFICATIONREJECTED) {
        return ProjectStatusDTO.StatusEnum.MODIFICATIONREJECTED;
      } else {
        return ProjectStatusDTO.StatusEnum.APPROVED;
      }
    } else {
      return 'MODIFICATION_OPEN';
    }
  }

  isStatusOpen(): boolean {
    return !this.projectStatus;
  }

  isStatusDeclined(): boolean {
    return this.projectStatus?.status === ProjectStatusDTO.StatusEnum.MODIFICATIONREJECTED;
  }

  isStatusAccepted(): boolean {
    return [ProjectStatusDTO.StatusEnum.APPROVED, ProjectStatusDTO.StatusEnum.CONTRACTED]
      .includes(this.projectStatus?.status);
  }

  getSwitchValue() {
    return this.projectStatus?.status ?? this.ProjectStatus.APPROVED;
  }

  removeCorrection(correction: AuditControlCorrectionDTO) {
    const corrections = this.decisionForm.get('corrections')?.value;

    const index = corrections.indexOf(correction);
    corrections.splice(index, 1);

    this.decisionForm.get('corrections')?.setValue(corrections);
  }

  isSelected(correctionId: number): boolean {
    return this.decisionForm.get('corrections')?.value?.find(
      (correction: AuditControlCorrectionDTO) => correction.id === correctionId
    );
  }
}
