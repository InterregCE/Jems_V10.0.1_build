import {fakeAsync, TestBed, tick} from '@angular/core/testing';
import {OutputProjectSimple} from '@cat/api';
import {HttpTestingController} from '@angular/common/http/testing';
import {ProjectApplicationListStore} from '@common/components/project-application-list/project-application-list-store.service';
import {TestModule} from '../../test-module';

describe('ProjectApplicationListStore', () => {
  let service: ProjectApplicationListStore;
  let httpTestingController: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [TestModule],
      providers: [ProjectApplicationListStore]
    });
    service = TestBed.inject(ProjectApplicationListStore);
    httpTestingController = TestBed.inject(HttpTestingController);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should list projects', fakeAsync(() => {
    let results: OutputProjectSimple[] = [];
    service.page$.subscribe(result => results = result.content);

    const projects = [
      {acronym: '1'} as OutputProjectSimple,
      {acronym: '2'} as OutputProjectSimple
    ];

    httpTestingController.match({method: 'GET', url: `//api/project?page=0&size=25&sort=id,desc`})
      .forEach(req => req.flush({content: projects}));

    tick();
    expect(results).toEqual(projects);
  }));
});
