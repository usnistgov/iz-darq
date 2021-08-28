import { Component, OnInit } from '@angular/core';
import { DownloadService, FileDescriptor, JarInfo } from '../../services/download.service';
import { Observable } from 'rxjs';

@Component({
  selector: 'app-download',
  templateUrl: './download.component.html',
  styleUrls: ['./download.component.scss']
})
export class DownloadComponent implements OnInit {

  cliInfo$: Observable<JarInfo>;
  files$: Observable<FileDescriptor[]>;
  cliDownloadLink: string;

  constructor(private downloadService: DownloadService) {
    this.cliInfo$ = this.downloadService.getCLIInfo();
    this.files$ = this.downloadService.getFiles();
    this.cliDownloadLink = this.downloadService.getCLIDownloadLink();
  }

  getDownloadLink(id: string): string {
    return this.downloadService.getDownloadLink(id);
  }

  ngOnInit(): void {
  }

}
