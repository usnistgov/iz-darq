import { Component, OnInit } from '@angular/core';

export enum AdminTabs {
  USERS = 'USERS',
  EMAILS = 'EMAILS',
  NARRATIVES = 'NARRATIVES',
  CONFIG = 'CONFIG',
  VALUES = 'VALUES',
}

@Component({
  selector: 'app-admin-sidebar',
  templateUrl: './admin-sidebar.component.html',
  styleUrls: ['./admin-sidebar.component.scss']
})
export class AdminSidebarComponent implements OnInit {

  constructor() { }

  ngOnInit(): void {
  }

}
