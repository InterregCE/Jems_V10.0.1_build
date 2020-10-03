import { ChangeDetectionStrategy, Component, Input } from '@angular/core';
import { OutputProject, OutputProjectStatus } from '@cat/api';

@Component({
    selector: 'app-project-application-assessments',
    templateUrl: './project-application-assessments.component.html',
    styleUrls: ['./project-application-assessments.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class ProjectApplicationAssessmentsComponent {
    OutputProjectStatus = OutputProjectStatus;

    @Input()
    project: OutputProject;
    @Input()
    projectStatus: OutputProjectStatus.StatusEnum;
}
