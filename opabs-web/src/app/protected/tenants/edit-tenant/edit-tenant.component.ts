import {Component, Inject, OnInit} from '@angular/core';
import {AbstractControl, FormArray, FormBuilder, FormGroup, Validators} from '@angular/forms';
import {MAT_DIALOG_DATA, MatDialogRef} from '@angular/material/dialog';
import { faMinusCircle, faTimesCircle, faPlusCircle } from '@fortawesome/free-solid-svg-icons';
import {Tenant} from '../../../shared/model/Tenant';
import {Address} from '../../../shared/model/Address';

@Component({
  selector: 'app-edit-tenant',
  templateUrl: './edit-tenant.component.html',
  styleUrls: ['./edit-tenant.component.scss']
})
export class EditTenantComponent implements OnInit {

  faMinusCircle = faMinusCircle;

  faTimesCircle = faTimesCircle;

  faPlusCircle = faPlusCircle;

  formGroup: FormGroup;

  constructor(private fb: FormBuilder,
              public dialogRef: MatDialogRef<EditTenantComponent>,
              @Inject(MAT_DIALOG_DATA) public data: Tenant) { }

  ngOnInit(): void {
    this.formGroup = this.fb.group({
      name: [this.data.name, [Validators.required]],
      contactInfo: this.fb.group({
        phone: [this.data.contactInfo.phone, [Validators.required]],
        emailAddress: [this.data.contactInfo.emailAddress, [Validators.required]],
        addresses: this.fb.array([])
      })
    });
    this.data.contactInfo.addresses.forEach((value: Address) => {
      this.addresses.push(this.fb.group({
        addressLine1: [value.addressLine1, [Validators.required]],
        addressLine2: [value.addressLine2],
        city: [value.city, [Validators.required]],
        stateOrProvince: [value.stateOrProvince, [Validators.required]],
        country: [value.country, [Validators.required]],
        zipCode: [value.zipCode, Validators.required]
      }));
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

  addAddressForm(): void {
    const fg = this.createAddressForm();
    this.addresses.push(fg);
  }

  removeAddressForm(i): void {
    this.addresses.removeAt(i);
  }

  updateTenant(): void {
    if (this.formGroup.valid) {
      this.dialogRef.close({ ...this.formGroup.value, id: this.data.id });
    }
  }

}
