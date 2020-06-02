import { Component, OnInit } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';
import { Store } from '@ngrx/store';
import { FileService } from '../../services/file.service';
import { RxjsStoreHelperService, MessageType } from 'ngx-dam-framework';
import { finalize, map } from 'rxjs/operators';
import { Router } from '@angular/router';
import { Observable } from 'rxjs';
import { SelectItem } from 'primeng/api/selectitem';
import { selectFacilityList } from '../../store/core.selectors';

@Component({
  selector: 'app-adf-upload',
  templateUrl: './adf-upload.component.html',
  styleUrls: ['./adf-upload.component.scss']
})
export class AdfUploadComponent implements OnInit {

  file: File;
  form: FormGroup;
  facilities$: Observable<SelectItem[]>;

  constructor(
    private store: Store<any>,
    private fileService: FileService,
    private router: Router,
    private helper: RxjsStoreHelperService,
  ) {
    this.form = new FormGroup({
      name: new FormControl('', [Validators.required]),
      facility: new FormControl(null),
      accept: new FormControl(false, [Validators.required]),
    });
    this.facilities$ = store.select(selectFacilityList).pipe(
      map((list) => {
        return list.map((f) => {
          return {
            label: f.name,
            value: f.id,
          };
        });
      }),
    );
  }

  submit() {
    const form = new FormData();
    const data = this.form.getRawValue();
    form.append('name', data.name);
    form.append('file', this.file);
    if (data.facility) {
      form.append('facility', data.facility);
    }
    this.helper.getMessageAndHandle<any>(
      this.store,
      () => {
        return this.fileService.upload(form);
      },
      (message) => {
        if (message.status === MessageType.SUCCESS) {
          this.router.navigate(['/', 'adf', 'dashboard', data.facility ? data.facility : 'local']);
        }
        return [];
      }
    ).subscribe();
  }

  setFile(file: File) {
    this.file = file;
  }

  ngOnInit(): void {
  }

}
