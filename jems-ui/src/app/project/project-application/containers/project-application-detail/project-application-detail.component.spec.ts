import {async, ComponentFixture, TestBed} from '@angular/core/testing';
import {CUSTOM_ELEMENTS_SCHEMA} from '@angular/core';
import {HttpTestingController} from '@angular/common/http/testing';
import {ProjectApplicationDetailComponent} from './project-application-detail.component';
import {ActivatedRoute} from '@angular/router';
import {TestModule} from '../../../../common/test-module';
import {ProjectModule} from '../../../project.module';

describe('ProjectApplicationDetailComponent', () => {

  let fixture: ComponentFixture<ProjectApplicationDetailComponent>;
  let projectApplicationDetailComponent: ProjectApplicationDetailComponent;
  let httpTestingController: HttpTestingController;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [
        TestModule,
        ProjectModule,
      ],
      declarations: [
        ProjectApplicationDetailComponent,
      ],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: {
            snapshot: {
              params: {
                projectId: 1
              }
            }
          }
        },
      ],
      schemas: [CUSTOM_ELEMENTS_SCHEMA],
    });
    TestBed.compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ProjectApplicationDetailComponent);
    projectApplicationDetailComponent = fixture.componentInstance;
    httpTestingController = TestBed.inject(HttpTestingController);
  });

  it('should create the project application', () => {
    expect(projectApplicationDetailComponent).toBeTruthy();
  });
});
