import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {InputProjectStatus, OutputProject} from '@cat/api';
import {Permission} from '../../../../../security/permissions/permission';
import { Alert } from '../../../../../common/components/forms/alert';
import {Observable, Subject} from 'rxjs';

@Component({
  selector: 'app-project-application-data',
  templateUrl: './project-application-data.component.html',
  styleUrls: ['./project-application-data.component.scss']
})
export class ProjectApplicationDataComponent implements OnInit {
  Alert = Alert;
  @Input()
  project: OutputProject;
  @Input()
  fileNumber: number;
  @Input()
  statusMessages: string[];
  @Input()
  Permission = Permission;
  @Input()
  projectSubmitted$: Observable<boolean>
  @Output()
  uploadFile: EventEmitter<File> = new EventEmitter<File>();
  @Output()
  submitProjectApplication: EventEmitter<InputProjectStatus> = new EventEmitter<InputProjectStatus>();

  projectSubmittedSuccess$ = new Subject<boolean>();

  ngOnInit() {
    this.projectSubmitted$.subscribe((value) => {
      this.projectSubmittedSuccess$.next(value);
    })
  }
}
