import { Component, Inject, OnInit } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { IADFDescriptor } from '../../model/adf.model';
import { SelectItem } from 'primeng/api/selectitem';
import { BehaviorSubject } from 'rxjs';

@Component({
  selector: 'app-adf-merge-dialog',
  templateUrl: './adf-merge-dialog.component.html',
  styleUrls: ['./adf-merge-dialog.component.scss']
})
export class AdfMergeDialogComponent implements OnInit {

  files: IADFDescriptor[];
  filtered$: BehaviorSubject<IADFDescriptor[]>;
  facilities: SelectItem[];
  facilityMap: Record<string, string> = {};
  fileSelection: IADFDescriptor[] = [];
  compatibilityVersions = [
    ['2.0.0', '2.0.0-SNAPSHOT', '2.0.1', '2.0.2', '2.0.3'],
    ['2.1.0', '2.1.1', '2.1.2', '2.1.3'],
    ['3.0.0-SNAPSHOT', '3.0.0-SNAPSHOT.rev1'],
    ['3.0.0', '3.0.1'],
  ];

  name: string;
  facilityId: string;
  tags: string[] = [];

  constructor(
    public dialogRef: MatDialogRef<AdfMergeDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: any,
  ) {
    this.files = data.files;
    this.filtered$ = new BehaviorSubject(this.files);
    this.facilities = data.facilities.map((f) => {
      this.facilityMap[f.id] = f.name;
      return {
        label: f.name,
        value: f.id,
      };
    });
    this.name = '';
    this.facilityId = '';
  }

  selectionChanged(selection: IADFDescriptor[]) {
    if (selection && selection.length > 0) {
      const version = selection[0].version;
      if (version) {
        this.filtered$.next(this.files.filter((file) => {
          return this.areCompatible(selection[0], file);
        }));
      } else {
        this.filtered$.next(selection);
      }
    } else {
      this.filtered$.next(this.files);
    }
  }

  onRowCheckboxClick(checked: boolean, row: IADFDescriptor) {
    if (checked) {
      this.mergeTags(this.tags, row.tags || []);
    }
  }

  areCompatible(a: IADFDescriptor, b: IADFDescriptor) {
    if (!a.cliVersion || !b.cliVersion) { return false; }

    const compatibleVersions = this.compatibilityVersions.find((l) => l.includes(a.cliVersion));
    if (compatibleVersions) {
      return a.mqeVersion === b.mqeVersion && compatibleVersions.includes(a.cliVersion) && compatibleVersions.includes(b.cliVersion);
    } else {
      return a.mqeVersion === b.mqeVersion && a.cliVersion === b.cliVersion;
    }
  }

  mergeTags(existing: string[], additional: string[]) {
    for (const tag of additional) {
      if (!existing.includes(tag)) {
        existing.push(tag);
      }
    }
  }

  valid() {
    return this.name && this.facilityId && this.fileSelection && this.fileSelection.length > 1;
  }

  merge() {
    this.dialogRef.close({
      name: this.name,
      facilityId: this.facilityId,
      tags: this.tags,
      ids: this.fileSelection.map((f) => f.id),
    });
  }

  ngOnInit(): void {
  }

}
