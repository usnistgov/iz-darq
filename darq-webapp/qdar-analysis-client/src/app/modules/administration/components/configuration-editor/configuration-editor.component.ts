import { Component, OnDestroy, OnInit } from '@angular/core';
import { DamAbstractEditorComponent, EditorSave, IEditorMetadata, Message, MessageService, MessageType, UserMessage } from 'ngx-dam-framework';
import { Actions } from '@ngrx/effects';
import { Action, Store } from '@ngrx/store';
import { AdminService } from '../../services/admin.service';
import { Observable, Subscription, throwError, Subject } from 'rxjs';
import { FormGroup, FormControl, Validators, FormArray } from '@angular/forms';
import { flatMap, map, takeUntil, filter, catchError, take, tap } from 'rxjs/operators';
import { IToolConfigurationKey } from '../../model/tool-config.model';

export const ADMIN_CONFIG_EDITOR_METADATA: IEditorMetadata = {
  id: 'ADMIN_CONFIG_EDITOR_METADATA',
  title: 'Configuration'
};

@Component({
  selector: 'app-configuration-editor',
  templateUrl: './configuration-editor.component.html',
  styleUrls: ['./configuration-editor.component.scss']
})
export class ConfigurationEditorComponent extends DamAbstractEditorComponent implements OnInit, OnDestroy {

  patch: Subscription;
  display$: Subject<{ status: Message<string>[] }>;
  statusCheck$: Observable<Message<string>[]>;
  form: FormArray;

  constructor(
    actions$: Actions,
    store: Store<any>,
    private adminService: AdminService,
    private message: MessageService,
  ) {
    super(
      ADMIN_CONFIG_EDITOR_METADATA,
      actions$,
      store,
    );

    this.display$ = new Subject();
    this.statusCheck$ = this.active$.pipe(
      map((active) => {
        return [
          ...active.display.status,
        ];
      })
    );
    this.patch = this.currentSynchronized$.pipe(
      takeUntil(this.active$.pipe(
        filter(value => value.editor.id !== ADMIN_CONFIG_EDITOR_METADATA.id),
      )),
      flatMap((value) => {
        this.form = new FormArray([]);
        for (const property of value.properties) {
          this.form.push(this.propertyForm(property));
        }

        return this.form.valueChanges.pipe(map((change) => {
          this.editorChange({
            properties: [...change]
          }, this.form.valid);
        }));
      }),
    ).subscribe();

  }

  propertyForm(conf: IToolConfigurationKey): FormGroup {
    return new FormGroup({
      key: new FormControl(conf.key),
      value: new FormControl(conf.value, conf.required ? [Validators.required] : []),
      required: new FormControl(conf.required),
    });
  }

  checkStatus() {
    this.adminService.checkStatus().pipe(
      tap((value) => {
        this.display$.next({ status: [...value] });
      }),
    ).subscribe();
  }

  onEditorSave(action: EditorSave): Observable<Action> {
    return this.current$.pipe(
      take(1),
      flatMap((current) => {
        if (current.valid) {
          return this.adminService.setToolConfKeys(current.data.properties).pipe(
            tap((value) => this.display$.next({ status: [...value.data] })),
            flatMap((value) => ([
              this.message.userMessageToAction(new UserMessage<any>(MessageType.SUCCESS, 'Properties Save Success'))
            ])),
            catchError((error) => {
              return throwError(this.message.actionFromError(error));
            })
          );
        } else {
          return throwError(this.message.userMessageToAction(new UserMessage<any>(MessageType.FAILED, 'Cannot save invalid data')));
        }
      })
    );
  }

  editorDisplayNode(): Observable<any> {
    return this.display$;
  }
  ngOnDestroy() {
    this.patch.unsubscribe();
  }
  onDeactivate(): void {
  }
  ngOnInit(): void {
    (document.getElementsByClassName('container-content').item(0) as HTMLElement).scrollTop = 0;
  }

}
