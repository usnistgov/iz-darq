import { Component, OnInit, Input } from '@angular/core';
import { IServerInfo } from '../../services/app-info.service';

@Component({
  selector: 'app-footer',
  templateUrl: './footer.component.html',
  styleUrls: ['./footer.component.scss']
})
export class FooterComponent implements OnInit {

  @Input()
  info: IServerInfo;

  constructor() { }

  ngOnInit(): void {
  }

}
