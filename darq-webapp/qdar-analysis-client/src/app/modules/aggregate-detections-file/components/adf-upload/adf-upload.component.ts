import { Component, OnInit, OnDestroy } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';
import { Store } from '@ngrx/store';
import { FileService, PRIVATE_FACILITY_ID } from '../../services/file.service';
import { RxjsStoreHelperService, MessageType } from 'ngx-dam-framework';
import { map } from 'rxjs/operators';
import { Router, ActivatedRoute } from '@angular/router';
import { Observable, combineLatest, Subscription, of } from 'rxjs';
import { SelectItem } from 'primeng/api/selectitem';
import { selectUserFacilitiesSorted } from '../../store/core.selectors';
import { WebContentService } from '../../../core/services/web-content.service';
import { ServerInfoService, IServerInfo } from '../../../core/services/app-info.service';

@Component({
  selector: 'app-adf-upload',
  templateUrl: './adf-upload.component.html',
  styleUrls: ['./adf-upload.component.scss']
})
export class AdfUploadComponent implements OnInit, OnDestroy {

  file: File;
  form: FormGroup;
  facilities$: Observable<SelectItem[]>;
  dashboardRoute$: Observable<string[]>;
  subs: Subscription;
  termsAndConditions: string;
  init = false;
  info$: Observable<IServerInfo>;

  constructor(
    private store: Store<any>,
    private fileService: FileService,
    private router: Router,
    private helper: RxjsStoreHelperService,
    private activatedRoute: ActivatedRoute,
    private webContentService: WebContentService,
    private serverInfo: ServerInfoService,
  ) {
    this.info$ = this.serverInfo.getServerInfo();
    this.form = new FormGroup({
      name: new FormControl('', [Validators.required]),
      facility: new FormControl(null, [Validators.required]),
      accept: new FormControl(false, [Validators.required]),
    });
    this.dashboardRoute$ = this.activatedRoute.queryParams.pipe(
      map((queryParams) => {
        return queryParams.facilityId ? ['/', 'adf', 'dashboard', queryParams.facilityId] : ['/', 'adf', 'dashboard'];
      })
    );
    this.facilities$ = store.select(selectUserFacilitiesSorted).pipe(
      map((list) => {
        return list.map((f) => {
          return {
            label: f.name,
            value: f.id,
          };
        });
      }),
    );

    this.subs = combineLatest([this.facilities$, this.activatedRoute.queryParams]).pipe(
      map(([facilities, params]) => {
        if (facilities && facilities.length > 0 && params.facilityId && facilities.find((f) => f.value === params.facilityId)) {
          this.form.get('facility').setValue(params.facilityId);
        }
      })
    ).subscribe();
  }

  submit() {
    const form = new FormData();
    const data = this.form.getRawValue();
    form.append('name', data.name);
    form.append('file', this.file);
    if (data.facility !== PRIVATE_FACILITY_ID) {
      form.append('facility', data.facility);
    }
    this.helper.getMessageAndHandle<any>(
      this.store,
      () => {
        return this.fileService.upload(form);
      },
      (message) => {
        if (message.status === MessageType.SUCCESS) {
          this.router.navigate(['/', 'adf', 'dashboard', data.facility]);
        }
        return of();
      }
    ).subscribe();
  }

  setFile(file: File) {
    this.file = file;
  }

  ngOnInit(): void {
    this.webContentService.getUploadTermsAndConditions().pipe(
      map((value) => {
        this.termsAndConditions = value;
        this.init = true;
      }),
    ).subscribe();
  }

  ngOnDestroy() {
    this.subs.unsubscribe();
  }

}
