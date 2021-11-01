import {Component, OnInit} from '@angular/core';
import {AbstractControl, FormBuilder, FormGroup, Validators} from '@angular/forms';
import {faTimesCircle} from '@fortawesome/free-solid-svg-icons';
import {KeyType} from '../../../shared/model/KeyType';
import {KeyLength} from '../../../shared/model/KeyLength';
import {NamedCurves} from '../../../shared/model/NamedCurves';
import {SignatureAlgorithms} from '../../../shared/model/SignatureAlgorithms';
import {TenantService} from '../../../shared/services/tenant.service';
import {MatDialogRef} from '@angular/material/dialog';
import {DN_REGEX} from '../../../shared/model/Constants';

@Component({
  selector: 'app-add-trust-chain',
  templateUrl: './add-trust-chain.component.html',
  styleUrls: ['./add-trust-chain.component.scss']
})
export class AddTrustChainComponent implements OnInit {

  faTimesCircle = faTimesCircle;
  formGroup: FormGroup;
  keyTypes = [{ displayName: KeyType.RSA, type: 'RSA' }, { displayName: KeyType.ELLIPTIC_CURVE, type: 'ELLIPTIC_CURVE'}];
  keyLengths = [
    { displayName: KeyLength.LENGTH_4096, keySize: 'LENGTH_4096' },
    { displayName: KeyLength.LENGTH_3072, keySize: 'LENGTH_3072' },
    { displayName: KeyLength.LENGTH_2048, keySize: 'LENGTH_2048' },
    { displayName: KeyLength.LENGTH_1024, keySize: 'LENGTH_1024' }
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

  showNamedCurveInput = false;
  showKeyLengthInput = false;
  showSignatureAlgorithms = false;

  signatureAlgorithms = [SignatureAlgorithms.SHA256withECDSA, SignatureAlgorithms.SHA256withRSA, SignatureAlgorithms.SHA1withRSA];

  tenants = [];

  constructor(
    private fb: FormBuilder,
    private tenantService: TenantService,
    private dialog: MatDialogRef<AddTrustChainComponent>
  ) { }

  ngOnInit(): void {
    this.formGroup = this.fb.group({
      name: ['', [Validators.required]],
      description: [''],
      subjectDistinguishedName: ['', [Validators.required, Validators.pattern(DN_REGEX)]],
      validFrom: ['', Validators.required],
      keyType: [null, Validators.required],
      keySize: [null],
      namedCurve: [null],
      validityInYears: ['1', [Validators.required, Validators.min(1)]],
      signatureAlgorithm: [null, Validators.required],
      tenantExtId: [null, Validators.required]
    });
    this.tenantService.list({ page: 0, size: 1000 })
      .subscribe(data => {
        this.tenants = data.content;
      });
  }

  createTrustChain(): void {
    if (this.formGroup.valid) {
      this.dialog.close(this.formGroup.value);
    }
  }

  keyTypeChanged(): void {
    const selectedKeyType = this.keyType.value;
    switch (selectedKeyType) {
      case 'RSA':
        this.showNamedCurveInput = false;
        this.showKeyLengthInput = true;
        this.showSignatureAlgorithms = true;
        this.signatureAlgorithms = [SignatureAlgorithms.SHA1withRSA, SignatureAlgorithms.SHA256withRSA];
        this.toggleEcInputValidators();
        break;
      case 'ELLIPTIC_CURVE':
        this.showNamedCurveInput = true;
        this.showKeyLengthInput = false;
        this.showSignatureAlgorithms = true;
        this.signatureAlgorithms = [SignatureAlgorithms.SHA256withECDSA];
        this.toggleEcInputValidators();
        break;
      default:
        this.showKeyLengthInput = false;
        this.showNamedCurveInput = false;
        this.showSignatureAlgorithms = false;
    }
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

  get name(): AbstractControl {
    return this.formGroup.get('name');
  }

  get description(): AbstractControl {
    return this.formGroup.get('description');
  }

  get subjectDistinguishedName(): AbstractControl {
    return this.formGroup.get('subjectDistinguishedName');
  }

  get validFrom(): AbstractControl {
    return this.formGroup.get('validFrom');
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

  get validityInYears(): AbstractControl {
    return this.formGroup.get('validityInYears');
  }

  get signatureAlgorithm(): AbstractControl {
    return this.formGroup.get('signatureAlgorithm');
  }

  get tenantExtId(): AbstractControl {
    return this.formGroup.get('tenantExtId');
  }
}
