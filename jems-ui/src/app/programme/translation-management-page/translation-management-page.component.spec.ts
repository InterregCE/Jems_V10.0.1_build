import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { TranslationManagementPageComponent } from './translation-management-page.component';

describe('TranslationManagementPageComponent', () => {
  let component: TranslationManagementPageComponent;
  let fixture: ComponentFixture<TranslationManagementPageComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ TranslationManagementPageComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(TranslationManagementPageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
