import { ComponentFixture, fakeAsync, TestBed, tick, waitForAsync } from '@angular/core/testing';

import {ProgrammeDataDTO} from '@cat/api';
import {HttpTestingController} from '@angular/common/http/testing';
import {ProgrammeModule} from '../programme.module';
import {TestModule} from '@common/test-module';
import {ProgrammeBasicDataComponent} from './programme-basic-data.component';

describe('ProgrammeBasicDataComponent', () => {
  let httpTestingController: HttpTestingController;
  let component: ProgrammeBasicDataComponent;
  let fixture: ComponentFixture<ProgrammeBasicDataComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [ProgrammeBasicDataComponent],
      imports: [
        ProgrammeModule,
        TestModule
      ],
    })
      .compileComponents();
    httpTestingController = TestBed.inject(HttpTestingController);
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ProgrammeBasicDataComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should update a programme', fakeAsync(() => {
    const user = {cci: 'some cci'} as ProgrammeDataDTO;

    component.saveProgrammeData$.next(user);
    let success = false;
    component.programmeSaveSuccess$.subscribe(result => success = result);

    httpTestingController.expectOne({method: 'GET', url: `//api/auth/current`});

    httpTestingController.expectOne({
      method: 'GET',
      url: `//api/programmedata`
    }).flush(user);

    httpTestingController.expectOne({
      method: 'PUT',
      url: `//api/programmedata`
    }).flush(user);
    httpTestingController.verify();

    tick(4100);
    expect(success).toBeTruthy();
  }));
});
