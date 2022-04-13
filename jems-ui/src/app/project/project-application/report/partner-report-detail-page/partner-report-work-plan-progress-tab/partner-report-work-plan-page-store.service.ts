import {Injectable} from '@angular/core';
import {
  ProjectPartnerReportService,
  ProjectPartnerReportWorkPackageDTO,
  ProjectPartnerReportWorkPlanService,
  ProjectReportFileMetadataDTO,
  UpdateProjectPartnerReportWorkPackageDTO
} from '@cat/api';
import {combineLatest, Observable} from 'rxjs';
import {switchMap, take, tap} from 'rxjs/operators';
import {RoutingService} from '@common/services/routing.service';
import {Log} from '@common/utils/log';
import {ProjectStore} from '@project/project-application/containers/project-application-detail/services/project-store.service';
import {PartnerReportDetailPageStore} from '@project/project-application/report/partner-report-detail-page/partner-report-detail-page-store.service';

@Injectable({providedIn: 'root'})
export class PartnerReportWorkPlanPageStore {

  partnerId$: Observable<string | number | null>;
  partnerWorkPackages$: Observable<ProjectPartnerReportWorkPackageDTO[]>;

  constructor(private routingService: RoutingService,
              private partnerReportPageStore: PartnerReportDetailPageStore,
              private projectPartnerReportService: ProjectPartnerReportService,
              private projectStore: ProjectStore,
              private projectPartnerReportWorkPlanService: ProjectPartnerReportWorkPlanService) {
    this.partnerId$ = this.partnerReportPageStore.partnerId$;
    this.partnerWorkPackages$ = this.getWorkPackages();
  }

  getWorkPackages(): Observable<ProjectPartnerReportWorkPackageDTO[]> {
    return combineLatest([
      this.partnerId$,
      this.partnerReportPageStore.partnerReportId$,
    ]).pipe(
      switchMap(([partnerId, reportId]) => this.projectPartnerReportWorkPlanService.getWorkPlan(Number(partnerId), Number(reportId))),
      tap(data => Log.info('Fetched project work packages for report', this, data))
    );
  }

  saveWorkPackages(workPackages: UpdateProjectPartnerReportWorkPackageDTO[]): Observable<UpdateProjectPartnerReportWorkPackageDTO[]> {
     return combineLatest([
      this.partnerId$,
       this.partnerReportPageStore.partnerReportId$,
    ]).pipe(
      switchMap(([partnerId, reportId]) =>
        this.projectPartnerReportWorkPlanService.updateWorkPlan(Number(partnerId), Number(reportId), workPackages)),
      tap(data => Log.info('Updated work packages for report', this, data))
    );
  }

  uploadActivityFile(file: File, activityId: number, workPackageId: number): Observable<ProjectReportFileMetadataDTO> {
    return  combineLatest([
      this.partnerId$,
      this.partnerReportPageStore.partnerReportId$,
    ]).pipe(
      take(1),
      switchMap(([partnerId, reportId]) => this.projectPartnerReportWorkPlanService.uploadFileToActivityForm(
          file, activityId, partnerId as number, reportId, workPackageId)
      ));
  }

  uploadDeliverableFile(file: File, activityId: number, deliverableId: number, workPackageId: number): Observable<ProjectReportFileMetadataDTO> {
    return  combineLatest([
      this.partnerId$,
      this.partnerReportPageStore.partnerReportId$,
    ]).pipe(
      take(1),
      switchMap(([partnerId, reportId]) => this.projectPartnerReportWorkPlanService.uploadFileToDeliverableForm(
        file, activityId, deliverableId, partnerId as number, reportId, workPackageId)
      ));
  }

  uploadOutputFile(file: File, outputId: number, workPackageId: number): Observable<ProjectReportFileMetadataDTO> {
    return  combineLatest([
      this.partnerId$,
      this.partnerReportPageStore.partnerReportId$,
    ]).pipe(
      take(1),
      switchMap(([partnerId, reportId]) => this.projectPartnerReportWorkPlanService.uploadFileToOutputForm(
        file, outputId, partnerId as number, reportId, workPackageId)
      ));
  }
}
