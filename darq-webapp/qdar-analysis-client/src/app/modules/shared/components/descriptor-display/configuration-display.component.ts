import {Component, Input, OnInit} from '@angular/core';
import {IDescriptor} from '../../model/descriptor.model';

@Component({
  selector: 'app-descriptor-display',
  templateUrl: './configuration-display.component.html',
  styleUrls: ['./configuration-display.component.scss']
})
export class DescriptorDisplayComponent implements OnInit {

  @Input()
  descriptor: IDescriptor;
  @Input()
  userId: string;

  constructor() { }

  ngOnInit(): void {
  }

}
