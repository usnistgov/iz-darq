import { Component, OnInit, OnDestroy, ElementRef } from '@angular/core';
import { Action, Store } from '@ngrx/store';
import { DamAbstractEditorComponent, EditorSave, EditorUpdate, IEditorMetadata, MessageService, UserMessage, MessageType } from 'ngx-dam-framework';
import { Observable, of, throwError, Subscription } from 'rxjs';
import { Actions } from '@ngrx/effects';
import { IWebContent } from '../../model/web-content.model';
import { map, flatMap, catchError, take, takeUntil, filter } from 'rxjs/operators';
import { FormGroup, FormControl, Validators, FormArray } from '@angular/forms';
import * as _ from 'lodash';
import { AdminService } from '../../services/admin.service';

export const WEB_CONTENT_EDITOR_METADATA: IEditorMetadata = {
  id: 'WEB_CONTENT_EDITOR_METADATA',
  title: 'Web Content'
};


@Component({
  selector: 'app-web-content-editor',
  templateUrl: './web-content-editor.component.html',
  styleUrls: ['./web-content-editor.component.scss']
})
export class WebContentEditorComponent extends DamAbstractEditorComponent implements OnInit, OnDestroy {

  patch: Subscription;
  form: FormGroup;

  constructor(
    actions$: Actions,
    store: Store<any>,
    private adminService: AdminService,
    private message: MessageService,
  ) {
    super(
      WEB_CONTENT_EDITOR_METADATA,
      actions$,
      store,
    );

    this.patch = this.currentSynchronized$.pipe(
      takeUntil(this.active$.pipe(
        filter(value => value.editor.id !== WEB_CONTENT_EDITOR_METADATA.id),
      )),
      flatMap((value) => {
        this.initForm();
        for (const section of value.homePage.sections) {
          this.sections.push(new FormGroup({
            title: new FormControl(section.title, [Validators.required]),
            content: new FormControl(section.content, []),
          }));
        }
        this.form.patchValue(value, { emitEvent: false });
        return this.form.valueChanges.pipe(map((change) => {
          this.editorChange(change, this.form.valid);
        }));
      }),
    ).subscribe();

  }

  get sections() {
    return ((this.form.controls.homePage as FormGroup).controls.sections as FormArray);
  }

  initForm() {
    this.form = new FormGroup({
      homePage: new FormGroup({
        title: new FormControl('', [Validators.required]),
        sections: new FormArray([]),
      }),
      registerTermsAndConditions: new FormControl(''),
      uploadTermsAndConditions: new FormControl(''),
    });
  }

  emptySection(): FormGroup {
    return new FormGroup({
      title: new FormControl('', [Validators.required]),
      content: new FormControl('', [Validators.required]),
    });
  }

  addSection() {
    this.sections.push(this.emptySection());
  }

  deleteSection(sections: FormArray, i: number) {
    sections.removeAt(i);
  }

  pullUpSection(sections: FormArray, i: number) {
    if (i > 0) {
      const upper = sections.at(i - 1);
      const curr = sections.at(i);
      sections.setControl(i - 1, curr);
      sections.setControl(i, upper);
    }
  }

  pullDownSection(sections: FormArray, i: number) {
    if (i < sections.length - 1) {
      const lower = sections.at(i + 1);
      const curr = sections.at(i);
      sections.setControl(i + 1, curr);
      sections.setControl(i, lower);
    }
  }

  onChange(webContent: IWebContent, invalid = false) {
    this.editorChange(_.cloneDeep(webContent), !(invalid || this.form.invalid));
  }

  onEditorSave(action: EditorSave): Observable<Action> {
    return this.current$.pipe(
      take(1),
      flatMap((current) => {
        if (current.valid) {
          return this.adminService.setWebContent(current.data).pipe(
            flatMap((value) => ([
              this.message.userMessageToAction(new UserMessage<any>(MessageType.SUCCESS, 'Web Content Save Success')),
              new EditorUpdate({ value, updateDate: true })
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
  onDeactivate(): void {
  }
  ngOnDestroy() {
    this.patch.unsubscribe();
  }
  ngOnInit(): void {
    (document.getElementsByClassName('container-content').item(0) as HTMLElement).scrollTop = 0;
  }

}
