import {async, ComponentFixture, fakeAsync, TestBed, tick} from '@angular/core/testing';

import {ProgrammePageComponent} from './programme-page.component';
import {ProgrammeFundDTO, OutputProgrammeData} from '@cat/api';
import {HttpTestingController} from '@angular/common/http/testing';
import {TestModule} from '../../../../common/test-module';
import {ProgrammeModule} from '../../../programme.module';

describe('ProgrammePageComponent', () => {
  let httpTestingController: HttpTestingController;
  let component: ProgrammePageComponent;
  let fixture: ComponentFixture<ProgrammePageComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ProgrammePageComponent],
      imports: [
        ProgrammeModule,
        TestModule
      ],
    })
      .compileComponents();
    httpTestingController = TestBed.inject(HttpTestingController);
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ProgrammePageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should update a programme', fakeAsync(() => {
    const user = {cci: 'some cci'} as OutputProgrammeData;

    component.saveProgrammeData$.next(user);
    let success = false;
    component.programmeSaveSuccess$.subscribe(result => success = result);

    httpTestingController.expectOne({
      method: 'GET',
      url: `//api/programmedata`
    }).flush(user);

    httpTestingController.expectOne({
      method: 'PUT',
      url: `//api/programmedata`
    }).flush(user);
    httpTestingController.verify();

    tick();
    expect(success).toBeTruthy();
  }));

  it('should fetch initial funds', fakeAsync(() => {
    let result: ProgrammeFundDTO[] = [];
    component.funds$.subscribe(res => result = res);

    httpTestingController.expectOne({method: 'GET', url: `//api/programmedata`})
      .flush({cci: 'some cci'} as OutputProgrammeData);

    httpTestingController.expectOne({method: 'GET', url: `//api/programmeFund`})
      .flush([{id: 1}]);

    tick();
    expect(result.length).toBe(1);
    expect(result[0].id).toBe(1);
  }));

  it('should update funds', fakeAsync(() => {
    let result: ProgrammeFundDTO[] = [];
    component.funds$.subscribe(res => result = res);
    component.saveFunds$.next([]);

    httpTestingController.expectOne({method: 'PUT', url: `//api/programmeFund`})
      .flush([{id: 1}]);

    tick();
    expect(result.length).toBe(1);
    expect(result[0].id).toBe(1);
  }));
});
