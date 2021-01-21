export class ProjectPeriod {
  projectId: number;
  periodNumber: number;
  start: number;
  end: number;

  constructor(projectId: number, periodNumber: number, start: number, end: number) {
    this.projectId = projectId;
    this.periodNumber = periodNumber;
    this.start = start;
    this.end = end;
  }
}
