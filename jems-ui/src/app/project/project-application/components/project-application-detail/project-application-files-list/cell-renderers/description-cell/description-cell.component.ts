import {ChangeDetectorRef, Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {FormState} from '@common/components/forms/form-state';
import {AbstractForm} from '@common/components/forms/abstract-form';
import {OutputProjectFile} from '@cat/api';
import {TranslateService} from '@ngx-translate/core';

@Component({
  selector: 'app-description-cell',
  templateUrl: './description-cell.component.html',
  styleUrls: ['./description-cell.component.scss']
})
export class DescriptionCellComponent extends AbstractForm implements OnInit {
  FormState = FormState;

  @Input()
  formState: FormState = FormState.VIEW;
  @Input()
  file: OutputProjectFile;
  @Input()
  fileId: number;

  @Output()
  switchedFormState: EventEmitter<FormState> = new EventEmitter<FormState>();
  @Output()
  saveFile: EventEmitter<OutputProjectFile> = new EventEmitter<OutputProjectFile>();

  descriptionForm = this.formBuilder.group({
    description: ['', Validators.maxLength(100)]
  });

  constructor(private formBuilder: FormBuilder,
              protected changeDetectorRef: ChangeDetectorRef,  protected translationService: TranslateService) {
    super(changeDetectorRef, translationService);
  }

  ngOnInit(): void {
    super.ngOnInit();
    this.descriptionForm.patchValue({description: this.file?.description});
  }

  getSizedDescription(): string {
    if (this.file?.description && this.file?.description.length > 30) {
      return this.file?.description.substring(0, 30) + '...';
    }
    return this.file?.description;
  }

  getForm(): FormGroup | null {
    return this.descriptionForm;
  }

  onSubmit(): void {
    this.file.description = this.descriptionForm?.controls?.description?.value;
    this.saveFile.emit(this.file);
    this.switchedFormState.emit(FormState.VIEW);
  }

  onCancel(): void {
    this.descriptionForm.patchValue({description: this.file?.description});
    this.switchedFormState.emit(FormState.VIEW);
  }
}
