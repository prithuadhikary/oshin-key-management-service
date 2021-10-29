import { Component, OnInit } from '@angular/core';
import {AbstractControl, FormArray, FormBuilder, FormGroup, Validators} from '@angular/forms';
import { faMinusCircle, faTimesCircle, faPlusCircle } from '@fortawesome/free-solid-svg-icons';
import {MatDialogRef} from '@angular/material/dialog';

@Component({
  selector: 'app-add-tenant',
  templateUrl: './add-tenant.component.html',
  styleUrls: ['./add-tenant.component.scss']
})
export class AddTenantComponent implements OnInit {

  faMinusCircle = faMinusCircle;

  faTimesCircle = faTimesCircle;

  faPlusCircle = faPlusCircle;

  formGroup: FormGroup;

  constructor(
    private fb: FormBuilder,
    public dialogRef: MatDialogRef<AddTenantComponent>
  ) { }

  ngOnInit(): void {
    this.formGroup = this.fb.group({
      name: ['', [Validators.required]],
      contactInfo: this.fb.group({
        phone: ['', [Validators.required]],
        emailAddress: ['', [Validators.required]],
        addresses: this.fb.array([
            this.createAddressForm()
        ])
      })
    });
  }

  createAddressForm(): FormGroup {
    return this.fb.group({
      addressLine1: ['', [Validators.required]],
      addressLine2: [''],
      city: ['', [Validators.required]],
      stateOrProvince: ['', [Validators.required]],
      country: ['', [Validators.required]],
      zipCode: ['', Validators.required]
    });
  }

  createTenant(): void {
    if (this.formGroup.valid) {
      this.dialogRef.close(this.formGroup.value);
    }
  }

  addAddressForm(): void {
    const fg = this.createAddressForm();
    this.addresses.push(fg);
  }

  removeAddressForm(i): void {
    this.addresses.removeAt(i);
  }

  get name(): AbstractControl {
    return this.formGroup.get('name');
  }

  get contactInfo(): FormGroup {
    return this.formGroup.get('contactInfo') as FormGroup;
  }

  get phone(): AbstractControl {
    return this.contactInfo.get('phone');
  }

  get emailAddress(): AbstractControl {
    return this.contactInfo.get('emailAddress');
  }

  get addresses(): FormArray {
    return this.formGroup.get('contactInfo').get('addresses') as FormArray;
  }

}
