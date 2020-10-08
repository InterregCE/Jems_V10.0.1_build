import {async, ComponentFixture, fakeAsync, TestBed, tick} from '@angular/core/testing';
import {TestModule} from '../../../../../common/test-module';
import {ProjectModule} from '../../../../project.module';
import {HttpTestingController} from '@angular/common/http/testing';
import {OutputWorkPackageSimple} from '@cat/api'
import {ProjectApplicationFormWorkPackageSectionComponent} from './project-application-form-work-package-section.component';

describe('ProjectApplicationFormWorkPackageSectionComponent', () => {
  let component: ProjectApplicationFormWorkPackageSectionComponent;
  let fixture: ComponentFixture<ProjectApplicationFormWorkPackageSectionComponent>;
  let httpTestingController: HttpTestingController;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [
        TestModule,
        ProjectModule
      ],
      declarations: [ProjectApplicationFormWorkPackageSectionComponent],
    })
      .compileComponents();
    httpTestingController = TestBed.inject(HttpTestingController);
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ProjectApplicationFormWorkPackageSectionComponent);
    component = fixture.componentInstance;
    component.projectId = 1;
    component.editable = true;
    fixture.detectChanges();
  });

  it('should be created', () => {
    expect(component).toBeTruthy();
  });

  it('should list work packages', fakeAsync(() => {
    let results: OutputWorkPackageSimple[] = [];
    component.currentWorkPackagePage$.subscribe(result => results = result.content);

    const workPackages = [
      {name: 'test1'} as OutputWorkPackageSimple,
      {name: 'test2'} as OutputWorkPackageSimple
    ];

    httpTestingController.match({method: 'GET', url: `//api/project/1/workpackage?page=0&size=25&sort=id,asc`})
      .forEach(req => req.flush({content: workPackages}));

    tick();
    expect(results).toEqual(workPackages);
  }));

  it('should delete a workPackage', fakeAsync(() => {
    component.deleteWorkPackage(1);

    httpTestingController.expectOne({
      method: 'DELETE',
      url: `//api/project/1/workpackage/1`
    })
    httpTestingController.expectOne({
      method: 'GET',
      url: `//api/project/1/workpackage?page=0&size=25&sort=id,asc`
    })
  }));

});
