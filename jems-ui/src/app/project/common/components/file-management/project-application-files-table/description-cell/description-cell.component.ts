import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {FormBuilder, Validators} from '@angular/forms';
import {ProjectFileMetadataDTO} from '@cat/api';

@Component({
  selector: 'jems-description-cell',
  templateUrl: './description-cell.component.html',
  styleUrls: ['./description-cell.component.scss']
})
export class DescriptionCellComponent implements OnInit {

  @Input()
  editable: boolean;
  @Input()
  file: ProjectFileMetadataDTO;

  @Output()
  editFinished = new EventEmitter<void>();
  @Output()
  saveFile: EventEmitter<ProjectFileMetadataDTO> = new EventEmitter<ProjectFileMetadataDTO>();

  descriptionForm = this.formBuilder.group({
    description: ['', Validators.maxLength(100)]
  });

  constructor(private formBuilder: FormBuilder) {
  }

  ngOnInit(): void {
    this.descriptionForm.patchValue({description: this.file?.description});
  }

  getSizedDescription(): string {
    if (this.file?.description && this.file?.description.length > 30) {
      return this.file?.description.substring(0, 30) + '...';
    }
    return this.file?.description;
  }

  onSubmit(): void {
    this.file.description = this.descriptionForm?.controls?.description?.value;
    this.saveFile.emit(this.file);
    this.editFinished.emit();
  }

  onCancel(): void {
    this.descriptionForm.patchValue({description: this.file?.description});
    this.editFinished.emit();
  }
}
