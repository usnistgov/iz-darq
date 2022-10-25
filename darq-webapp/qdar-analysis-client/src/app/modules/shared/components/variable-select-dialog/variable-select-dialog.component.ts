import { IQueryVariableDisplay } from './../variable-ref-display/variable-ref-display.component';
import {
  IQueryVariable,
  QueryVariableType,
  IExternalQueryVariable,
  ExternalQueryVariableScope
} from './../../model/query-variable.model';
import { Component, Inject, OnInit } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';

@Component({
  selector: 'app-variable-select-dialog',
  templateUrl: './variable-select-dialog.component.html',
  styleUrls: ['./variable-select-dialog.component.scss']
})
export class VariableSelectDialogComponent implements OnInit {
  hideAdf: boolean;
  hideFacility: boolean;
  hideGlobal: boolean;
  facilityId: string;
  adfVariables: IQueryVariableDisplay[];
  iisVariables: IQueryVariableDisplay[];
  globalVariables: IQueryVariableDisplay[];
  variableLists: { list: IQueryVariableDisplay[], title: string; id: string; }[];
  activeList: { list: IQueryVariableDisplay[], title: string; id: string; };
  constructor(
    public dialogRef: MatDialogRef<VariableSelectDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: any,
  ) {
    this.hideAdf = data.hideAdf;
    this.hideFacility = data.hideFacility;
    this.hideGlobal = data.hideGlobal;
    this.facilityId = data.facilityId;
    this.adfVariables = (data.variables || []).filter((v: IQueryVariableDisplay) => v.type === QueryVariableType.ADF);
    this.iisVariables = (data.variables || [])
      .filter((v: IQueryVariable) =>
        v.type === QueryVariableType.EXTERNAL &&
        (v as IExternalQueryVariable).scope === ExternalQueryVariableScope.IIS
      );
    this.globalVariables = (data.variables || [])
      .filter((v: IQueryVariable) =>
        v.type === QueryVariableType.EXTERNAL &&
        (v as IExternalQueryVariable).scope === ExternalQueryVariableScope.GLOBAL
      );
    this.variableLists = [
      ...this.hideAdf ? [] : [{
        title: 'ADF Variables',
        list: this.adfVariables,
        id: 'adf',
      }],
      ...this.hideGlobal ? [] : [{
        title: 'Global Variables',
        list: this.globalVariables,
        id: 'global',
      }],
      ...this.hideFacility ? [] : [{
        title: 'Facility Variables',
        list: this.iisVariables,
        id: 'facility',
      }],
    ];
    this.activeList = this.variableLists[0];
  }

  selectList(list) {
    this.activeList = list;
  }

  selectSnapshot(variable: IQueryVariableDisplay) {
    if (variable.snapshot) {
      this.dialogRef.close(variable.snapshot);
    }
  }

  selectDynamic(variable: IQueryVariableDisplay) {
    if (variable.dynamic) {
      this.dialogRef.close(variable.dynamic);
    }
  }

  ngOnInit(): void {
  }

}
