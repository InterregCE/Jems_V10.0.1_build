import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ProgrammeStateAidComponent } from './programme-state-aid.component';
import {TestModule} from '@common/test-module';
import {ProgrammeModule} from '../../../programme.module';

describe('ProgrammeStateAidComponent', () => {
  let component: ProgrammeStateAidComponent;
  let fixture: ComponentFixture<ProgrammeStateAidComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [
        TestModule,
        ProgrammeModule
      ],
      declarations: [ ProgrammeStateAidComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ProgrammeStateAidComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
