import {TestBed} from '@angular/core/testing';
import {TestModule} from '../../../common/test-module';
import {ProjectModule} from '../../project.module';
import {HttpTestingController} from '@angular/common/http/testing';
import {ProjectWorkPackagePageStore} from './project-work-package-page-store.service';

describe('ProjectWorkPackagePageStore', () => {
  let service: ProjectWorkPackagePageStore;
  let httpTestingController: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        TestModule,
        ProjectModule,
      ],
      providers: [ProjectWorkPackagePageStore]
    });
    service = TestBed.inject(ProjectWorkPackagePageStore);
    httpTestingController = TestBed.inject(HttpTestingController);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
