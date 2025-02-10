import { Component, OnDestroy, OnInit } from '@angular/core';
import { DamAbstractEditorComponent, EditorSave, EditorUpdate, IEditorMetadata, MessageService, MessageType, UserMessage } from '@usnistgov/ngx-dam-framework-legacy';
import { Actions } from '@ngrx/effects';
import { Action, Store } from '@ngrx/store';
import { AdminService } from '../../services/admin.service';
import { Observable, of, Subscription, throwError } from 'rxjs';
import { FormGroup, FormControl, Validators, FormArray } from '@angular/forms';
import { IEmailTemplate } from '../../model/email-template.model';
import { flatMap, map, takeUntil, filter, catchError, take } from 'rxjs/operators';

export const EMTAIL_TEMPLATE_EDITOR_METADATA: IEditorMetadata = {
  id: 'EMTAIL_TEMPLATE_EDITOR_METADATA',
  title: 'Email Template'
};

@Component({
  selector: 'app-emails-template-editor',
  templateUrl: './emails-template-editor.component.html',
  styleUrls: ['./emails-template-editor.component.scss']
})
export class EmailsTemplateEditorComponent extends DamAbstractEditorComponent implements OnInit, OnDestroy {

  patch: Subscription;
  form: FormArray;
  visible: Record<string, boolean> = {};

  constructor(
    actions$: Actions,
    store: Store<any>,
    private adminService: AdminService,
    private message: MessageService,
  ) {
    super(
      EMTAIL_TEMPLATE_EDITOR_METADATA,
      actions$,
      store,
    );
    this.visible = {};
    this.patch = this.currentSynchronized$.pipe(
      takeUntil(this.active$.pipe(
        filter(value => value.editor.id !== EMTAIL_TEMPLATE_EDITOR_METADATA.id),
      )),
      flatMap((value) => {
        this.form = new FormArray([]);
        for (const template of value.templates) {
          this.form.push(this.emailTemplateForm(template));
        }

        return this.form.valueChanges.pipe(map((change) => {
          this.editorChange({
            templates: [...change]
          }, this.form.valid);
        }));
      }),
    ).subscribe();

  }

  toggle(type: string) {
    this.visible[type] = !this.visible[type];
  }

  emailTemplateForm(template: IEmailTemplate): FormGroup {
    return new FormGroup({
      type: new FormControl(template.type),
      enabled: new FormControl(template.enabled, [Validators.required]),
      subject: new FormControl(template.subject, [Validators.required]),
      template: new FormControl(template.template, [Validators.required]),
      params: new FormControl(template.params),
    });
  }

  onEditorSave(action: EditorSave): Observable<Action> {
    return this.current$.pipe(
      take(1),
      flatMap((current) => {
        if (current.valid) {
          return this.adminService.setEmailTemplates(current.data.templates).pipe(
            flatMap((value) => ([
              this.message.userMessageToAction(new UserMessage<any>(MessageType.SUCCESS, 'Email Templates Save Success')),
              new EditorUpdate({ value: { templates: value }, updateDate: true })
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
    return of();
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
