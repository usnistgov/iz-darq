import { Component, OnInit, Inject } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { SelectItem } from 'primeng/api/selectitem';
import { ReportTemplateService } from '../../services/report-template.service';
import { IReportTemplateDescriptor } from '../../model/report-template.model';
import { IDescriptor } from 'src/app/modules/shared/model/descriptor.model';

@Component({
  selector: 'app-clone-rt-dialog',
  templateUrl: './clone-rt-dialog.component.html',
  styleUrls: ['./clone-rt-dialog.component.scss']
})
export class CloneRtDialogComponent implements OnInit {

  configurations: { value: string, label: IDescriptor }[];
  configurationId: string;

  template: IReportTemplateDescriptor;

  constructor(
    public reportTemplateService: ReportTemplateService,
    public dialogRef: MatDialogRef<CloneRtDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: any,
  ) {
    console.log(data)
    this.template = data.template
    if (data.template?.compatibilities.length > 0) {
      this.configurationId = data.template.compatibilities[0].id
      this.configurations = data.template.compatibilities.map((comp) => {
        return {
          value: comp.id,
          label: comp,
        };
      });
    }
    console.log(this.configurations)
    console.log(this.configurations[0])

  }

  valid() {
    return true;
  }

  clone() {
    this.dialogRef.close({
      configurationId: this.configurationId,
    });
  }


  cancel() {
    this.dialogRef.close();
  }

  ngOnInit(): void {
  }

}
