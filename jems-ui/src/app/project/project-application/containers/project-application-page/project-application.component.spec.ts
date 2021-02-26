import {async, ComponentFixture, TestBed} from '@angular/core/testing';
import {ProjectApplicationComponent} from './project-application.component';
import {HttpTestingController} from '@angular/common/http/testing';
import {TestModule} from '../../../../common/test-module';
import {ProjectModule} from '../../../project.module';
import {CallModule} from '../../../../call/call.module';

describe('ProjectApplicationComponent', () => {

  let httpTestingController: HttpTestingController;
  let component: ProjectApplicationComponent;
  let fixture: ComponentFixture<ProjectApplicationComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ProjectApplicationComponent],
      imports: [
        CallModule,
        ProjectModule,
        TestModule
      ],
    })
      .compileComponents();
    httpTestingController = TestBed.inject(HttpTestingController);
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ProjectApplicationComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should be created', () => {
    expect(component).toBeTruthy();
  });

});
