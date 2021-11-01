import {Component, Inject, OnInit} from '@angular/core';
import {faTimesCircle} from '@fortawesome/free-solid-svg-icons';
import {AbstractControl, FormBuilder, FormGroup, Validators} from '@angular/forms';
import {SignatureAlgorithms} from '../../../shared/model/SignatureAlgorithms';
import {KeyType} from '../../../shared/model/KeyType';
import {KeyLength} from '../../../shared/model/KeyLength';
import {NamedCurves} from '../../../shared/model/NamedCurves';
import {KeyUsage} from '../../../shared/model/KeyUsage';
import {MAT_DIALOG_DATA, MatDialogRef} from '@angular/material/dialog';

@Component({
  selector: 'app-add-certificate',
  templateUrl: './add-certificate.component.html',
  styleUrls: ['./add-certificate.component.scss']
})
export class AddCertificateComponent implements OnInit {

  faTimesCircle = faTimesCircle;

  formGroup: FormGroup;

  availableKeyTypes = [{displayName: KeyType.RSA, type: 'RSA'}, {displayName: KeyType.ELLIPTIC_CURVE, type: 'ELLIPTIC_CURVE'}];

  keyLengths = [
    {displayName: KeyLength.LENGTH_4096, keySize: 'LENGTH_4096'},
    {displayName: KeyLength.LENGTH_3072, keySize: 'LENGTH_3072'},
    {displayName: KeyLength.LENGTH_2048, keySize: 'LENGTH_2048'},
    {displayName: KeyLength.LENGTH_1024, keySize: 'LENGTH_1024'}
  ];
  namedCurves = [
    NamedCurves.curve25519,
    NamedCurves.secp128r1,
    NamedCurves.secp160k1,
    NamedCurves.secp160r1,
    NamedCurves.secp160r2,
    NamedCurves.secp192k1,
    NamedCurves.secp192r1,
    NamedCurves.secp224k1,
    NamedCurves.secp224r1,
    NamedCurves.secp256k1,
    NamedCurves.secp256r1,
    NamedCurves.secp384r1,
    NamedCurves.secp521r1
  ];

  keyUsageValues: Array<{ displayName: string, value: string }> = [];

  showNamedCurveInput = false;
  showKeyLengthInput = false;
  showKeyUsageInput = false;


  signatureAlgorithms = [SignatureAlgorithms.SHA256withECDSA, SignatureAlgorithms.SHA256withRSA, SignatureAlgorithms.SHA1withRSA];

  constructor(
    private fb: FormBuilder,
    private dialogRef: MatDialogRef<AddCertificateComponent>,
    @Inject(MAT_DIALOG_DATA) public data: { parentKeyType: KeyType }
  ) {
    this.setSignatureAlgoBasedOnParentKeyType(data.parentKeyType);
  }

  createCertificate(): void {
    if (this.formGroup.valid) {
      this.dialogRef.close(this.formGroup.value);
    }
  }

  ngOnInit(): void {
    this.formGroup = this.fb.group({
      subjectDistinguishedName: [null, [Validators.required]],
      keyUsages: [null, [Validators.required]],
      keyType: [null, [Validators.required]],
      namedCurve: [null],
      keySize: [null],
      validFrom: [null, Validators.required],
      validityInYears: ['1', [Validators.required, Validators.min(1)]],
      signatureAlgorithm: [null, [Validators.required]]
    });
  }

  setSignatureAlgoBasedOnParentKeyType(parentKeyType: KeyType): void {
    switch (parentKeyType.toString()) {
      case 'RSA':
        this.signatureAlgorithms = [SignatureAlgorithms.SHA1withRSA, SignatureAlgorithms.SHA256withRSA];
        break;
      case 'ELLIPTIC_CURVE':
        this.signatureAlgorithms = [SignatureAlgorithms.SHA256withECDSA];
    }
  }


  keyTypeChanged(): void {
    const selectedKeyType = this.keyType.value;
    switch (selectedKeyType) {
      case 'RSA':
        this.showNamedCurveInput = false;
        this.showKeyLengthInput = true;
        this.showKeyUsageInput = true;
        this.keyUsageValues = [
          {displayName: KeyUsage.KEY_CERT_SIGN, value: 'KEY_CERT_SIGN'},
          {displayName: KeyUsage.CRL_SIGN, value: 'CRL_SIGN'},
          {displayName: KeyUsage.KEY_ENCIPHERMENT, value: 'KEY_ENCIPHERMENT'},
          {displayName: KeyUsage.DATA_ENCIPHERMENT, value: 'DATA_ENCIPHERMENT'},
          {displayName: KeyUsage.DIGITAL_SIGNATURE, value: 'DIGITAL_SIGNATURE'},
          {displayName: KeyUsage.ENCIPHER_ONLY, value: 'ENCIPHER_ONLY'},
          {displayName: KeyUsage.DECIPHER_ONLY, value: 'DECIPHER_ONLY'},
          {displayName: KeyUsage.NON_REPUDIATION, value: 'NON_REPUDIATION'}
        ];
        this.toggleEcInputValidators();
        break;
      case 'ELLIPTIC_CURVE':
        this.showNamedCurveInput = true;
        this.showKeyLengthInput = false;
        this.showKeyUsageInput = true;
        this.keyUsageValues = [
          {displayName: KeyUsage.KEY_AGREEMENT, value: 'KEY_AGREEMENT'},
          {displayName: KeyUsage.NON_REPUDIATION, value: 'NON_REPUDIATION'},
          {displayName: KeyUsage.DIGITAL_SIGNATURE, value: 'DIGITAL_SIGNATURE'},
          {displayName: KeyUsage.KEY_CERT_SIGN, value: 'KEY_CERT_SIGN'},
          {displayName: KeyUsage.CRL_SIGN, value: 'CRL_SIGN'}
        ];
        this.toggleEcInputValidators();
        break;
      default:
        this.showKeyLengthInput = false;
        this.showNamedCurveInput = false;
    }
  }

  getKeyUsageValue(value: string): void {
    // tslint:disable-next-line:forin

  }

  toggleEcInputValidators(): void {
    if (this.keyType.value === KeyType.ELLIPTIC_CURVE) {
      this.keySize.clearValidators();
      this.namedCurve.setValidators(Validators.required);
    } else if (this.keyType.value === KeyType.RSA) {
      this.keySize.setValidators(Validators.required);
      this.namedCurve.clearValidators();
    } else {
      this.keySize.setValidators(Validators.required);
      this.namedCurve.setValidators(Validators.required);
    }
  }

  get subjectDistinguishedName(): AbstractControl {
    return this.formGroup.get('subjectDistinguishedName');
  }

  get keyUsages(): AbstractControl {
    return this.formGroup.get('keyUsages');
  }

  get keyType(): AbstractControl {
    return this.formGroup.get('keyType');
  }

  get keySize(): AbstractControl {
    return this.formGroup.get('keySize');
  }

  get namedCurve(): AbstractControl {
    return this.formGroup.get('namedCurve');
  }

  get validFrom(): AbstractControl {
    return this.formGroup.get('validFrom');
  }

  get validityInYears(): AbstractControl {
    return this.formGroup.get('validityInYears');
  }

  get signatureAlgorithm(): AbstractControl {
    return this.formGroup.get('signatureAlgorithm');
  }
}
