import {ChangeDetectionStrategy, Component, EventEmitter, Input, OnChanges, Output, SimpleChanges} from '@angular/core';
import {FormArray, FormBuilder, FormGroup, Validators} from '@angular/forms';
import {ProjectRelevanceSpfRecipientDTO} from '@cat/api';

@Component({
  selector: 'jems-spf-recipients-table',
  templateUrl: './spf-recipients-table.component.html',
  styleUrls: ['./spf-recipients-table.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class SpfRecipientsTableComponent implements OnChanges {

  @Input()
  formGroup: FormGroup;

  @Input()
  spfRecipients: ProjectRelevanceSpfRecipientDTO[];

  @Input()
  editable: boolean;

  @Output()
  changed = new EventEmitter<void>();

  recipientEnum = Object.keys(ProjectRelevanceSpfRecipientDTO.RecipientGroupEnum)
    .filter(recipientGroup => recipientGroup !== ProjectRelevanceSpfRecipientDTO.RecipientGroupEnum.GeneralPublic);

  recipientGroupErrors = {
    required: 'spf.project.application.form.relevance.recipient.group.not.empty',
  };

  constructor(private formBuilder: FormBuilder) {
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes.spfRecipients) {
      this.resetForm();
    }
  }

  get recipientsForm(): FormArray {
    return this.formGroup.get('spfRecipients') as FormArray;
  }

  addNewRecipient(): void {
    this.addControl();
    this.changed.emit();
  }

  private resetForm(): void {
    this.recipientsForm.clear();
    this.spfRecipients.forEach(recipient => this.addControl(recipient));
    if (!this.editable) {
      this.recipientsForm.disable();
    }
  }

  private addControl(recipient?: ProjectRelevanceSpfRecipientDTO): void {
    this.recipientsForm.push(this.formBuilder.group({
      recipientGroup: this.formBuilder.control(
        recipient ? recipient.recipientGroup : null, [Validators.required]
      ),
      specification: this.formBuilder.control(recipient?.specification || [], [Validators.maxLength(2000)]),
    }));
  }

  deleteEntry(elementIndex: number): void {
    this.recipientsForm.removeAt(elementIndex);
    this.changed.emit();
  }

}
