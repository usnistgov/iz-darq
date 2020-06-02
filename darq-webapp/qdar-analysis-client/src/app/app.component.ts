import { Component, OnInit } from '@angular/core';
import { Observable } from 'rxjs';
import { IServerInfo, ServerInfoService } from './modules/core/services/app-info.service';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})
export class AppComponent implements OnInit {

  title = 'qdar-analysis-client';
  info: Observable<IServerInfo>;

  constructor(private serverInfo: ServerInfoService) { }

  ngOnInit(): void {
    this.info = this.serverInfo.getServerInfo();
  }

}
