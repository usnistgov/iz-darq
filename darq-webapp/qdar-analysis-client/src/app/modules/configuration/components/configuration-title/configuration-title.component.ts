import { Component, OnInit, Input, TemplateRef } from '@angular/core';
import { IConfigurationDescriptor } from '../../model/configuration.model';

@Component({
  selector: 'app-configuration-title',
  templateUrl: './configuration-title.component.html',
  styleUrls: ['./configuration-title.component.scss']
})
export class ConfigurationTitleComponent implements OnInit {

  @Input()
  configuration: IConfigurationDescriptor;

  constructor() { }

  ngOnInit(): void {
  }

}
