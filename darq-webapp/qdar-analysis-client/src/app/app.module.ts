import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { AppComponent } from './app.component';
import { ToastrModule } from 'ngx-toastr';
import { BlockUIModule } from 'ng-block-ui';
import { CommonModule } from '@angular/common';
import { HttpClientModule } from '@angular/common/http';
import { AppRoutingModule } from './app-routing.module';
import { EffectsModule } from '@ngrx/effects';
import { StoreModule } from '@ngrx/store';
import { StoreDevtoolsModule } from '@ngrx/store-devtools';
import { environment } from '../environments/environment';
import { CoreModule } from './modules/core/core.module';
import {
  DamLoaderModule,
  DamAuthenticationModule,
  DamFrameworkModule,
  DamMessagesModule,
  DamRoutingModule,
} from 'ngx-dam-framework';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { ContextMenuModule } from 'ngx-contextmenu';
import { TreeModule } from 'angular-tree-component';

@NgModule({
  declarations: [
    AppComponent
  ],
  imports: [
    BrowserModule,
    BrowserAnimationsModule,
    AppRoutingModule,
    CommonModule,
    HttpClientModule,
    DamLoaderModule,
    DamFrameworkModule.forRoot(),
    DamMessagesModule.forRoot(),
    TreeModule.forRoot(),
    DamAuthenticationModule.forRootUsingUrl({
      api: {
        login: 'api/login',
        resetPassword: '',
        validateToken: '',
        updatePassword: '',
        checkAuthStatus: 'api/me',
        logout: 'api/logout',
      },
      loginPageRedirectUrl: '/login',
      unprotectedRedirectUrl: '/home',
      loginSuccessRedirectUrl: '/home',
    }),
    DamRoutingModule,
    EffectsModule.forRoot([]),
    StoreModule.forRoot({}),
    StoreDevtoolsModule.instrument({
      maxAge: 25,
      logOnly: environment.production,
    }),
    ToastrModule.forRoot(),
    BlockUIModule.forRoot(),
    ContextMenuModule.forRoot(),
    CoreModule,
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
