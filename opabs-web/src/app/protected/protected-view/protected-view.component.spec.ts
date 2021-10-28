import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ProtectedViewComponent } from './protected-view.component';

describe('ProtectedViewComponent', () => {
  let component: ProtectedViewComponent;
  let fixture: ComponentFixture<ProtectedViewComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ProtectedViewComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ProtectedViewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
