import { IFacilityDescriptor } from './../../../facility/model/facility.model';
import { ExternalVariableService } from './../../services/external-variables.service';
import { ExternalQueryVariableScope, IExternalQueryVariable, IIISExternalQueryVariable, IIISVariableValue } from './../../../shared/model/query-variable.model';
import { Component, Inject, OnInit } from '@angular/core';
import { FormGroup, FormControl, FormArray, AbstractControl } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';

@Component({
  selector: 'app-external-variable-dialog',
  templateUrl: './external-variable-dialog.component.html',
  styleUrls: ['./external-variable-dialog.component.scss']
})
export class ExternalVariableDialogComponent implements OnInit {
  VariableScope = ExternalQueryVariableScope;
  edit: boolean;
  value: IExternalQueryVariable;
  group: FormGroup;
  facilities: IFacilityDescriptor[];
  facilitiesOptions: { value: string, label: string }[];
  commonFormControls = {
    id: new FormControl(''),
    name: new FormControl(''),
    description: new FormControl(''),
    tags: new FormControl([]),
    type: new FormControl(''),
    scope: new FormControl(''),
  };
  facilityValue = new FormGroup({
    value: new FormControl(0),
    comment: new FormControl(''),
    facilityId: new FormControl(''),
  });
  facilityNameMap: Record<string, string> = {};
  facilityValueEdit = false;

  get values() {
    return (this.group.controls.values as FormArray).controls;
  }

  set values(values: AbstractControl[]) {
    (this.group.controls.values as FormArray).controls = [
      ...values
    ];
  }

  constructor(
    public dialogRef: MatDialogRef<ExternalVariableDialogComponent>,
    private externalVariableService: ExternalVariableService,
    @Inject(MAT_DIALOG_DATA) public data: any,
  ) {
    this.edit = data.edit;
    this.value = data.value;
    this.facilities = data.facilities;
    this.facilitiesOptions = [...this.facilities.map((f) => ({ value: f.id, label: f.name }))];
    this.facilityNameMap = this.facilitiesOptions.reduce((acc, v) => {
      return {
        ...acc,
        [v.value]: v.label,
      };
    }, {});

    if (!this.edit) {
      this.value = this.externalVariableService.getEmptyGlobalVariable();
    }

    this.group = this.getFormValue(this.value);
    this.group.patchValue(this.value);
    if (this.value.scope === ExternalQueryVariableScope.IIS) {
      ((this.value as IIISExternalQueryVariable).values || []).forEach((variable) => {
        this.addVariableValue(variable);
      });
    }
  }

  facilityChange(facilityId: string) {
    this.facilityValueEdit = facilityId && this.values &&
      this.values.findIndex((x) => (x as FormGroup).controls.facilityId.value === facilityId) !== -1;
  }

  addVariableValue(facilityValue: IIISVariableValue) {
    this.values = [
      ...(this.values || []).filter((x) => (x as FormGroup).controls.facilityId.value !== facilityValue.facilityId),
      new FormGroup({
        facilityId: new FormControl(facilityValue.facilityId),
        value: new FormControl(facilityValue.value),
        comment: new FormControl(facilityValue.comment),
      }),
    ];
    this.facilityChange(facilityValue.facilityId);
  }

  addValue() {
    if (this.facilityValue.valid) {
      const facilityValue = this.facilityValue.getRawValue();
      this.addVariableValue(facilityValue);
    }
  }

  editValue(value: FormGroup) {
    this.facilityValue.patchValue(value.getRawValue());
  }

  removeValue(facilityId: string) {
    this.values = [
      ...(this.values || []).filter((x) => (x as FormGroup).controls.facilityId.value !== facilityId),
    ];
  }

  getFormValue(value: IExternalQueryVariable): FormGroup {
    switch (value.scope) {
      case ExternalQueryVariableScope.GLOBAL:
        return new FormGroup({
          ...this.commonFormControls,
          value: new FormControl(0),
          comment: new FormControl(''),
        });
      case ExternalQueryVariableScope.IIS:
        return new FormGroup({
          ...this.commonFormControls,
          values: new FormArray([]),
        });
    }
  }

  done() {
    this.dialogRef.close(this.group.getRawValue());
  }

  changeType(scope: ExternalQueryVariableScope) {
    this.value = scope === ExternalQueryVariableScope.IIS ?
      this.externalVariableService.getEmptyIISVariable() :
      this.externalVariableService.getEmptyGlobalVariable();
    this.group = this.getFormValue(this.value);
    this.group.patchValue(this.value);
  }

  ngOnInit(): void {
  }

}
