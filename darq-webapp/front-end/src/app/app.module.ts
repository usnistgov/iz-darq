import {BrowserModule} from '@angular/platform-browser';
import {NgModule} from '@angular/core';
import {HTTP_INTERCEPTORS, HttpClientModule} from '@angular/common/http';
import {AppComponent} from './app.component';
import {HeaderComponent} from './header/header.component';
import {FooterComponent} from './footer/footer.component';
import {LoginComponent} from './login/login.component';
import {RouterModule} from "@angular/router";
import {HomeComponent} from './home/home.component';
import {UserService} from "./services/user.service";
import {FlexLayoutModule} from "@angular/flex-layout";
import {ServerInfoService} from "./services/server-info.service";
import {HttpModule} from "@angular/http";
import {AuthInterceptor} from "./services/auth.interceptor";
import {FormsModule} from "@angular/forms";
import { JobsComponent } from './jobs/jobs.component';
import {AuthGuard} from "./guards/auth.guard";
import {BsDropdownModule} from "ngx-bootstrap";
import {JobsService} from "./services/jobs.service";
import { JobCreatorComponent } from './job-creator/job-creator.component';
import { FileComponentComponent } from './file-component/file-component.component';
import { FileDropDirective } from './file-drop.directive';
import { ExternalDropDirective } from './external-drop.directive';
import {ToastyModule} from "ng2-toasty";
import { JobResultComponent } from './job-result/job-result.component';
import {TreeModule} from "primeng/components/tree/tree";
import {BlockUIModule} from "ng-block-ui";

const routes = [
	{
		path: "login",
		component: LoginComponent
	},
	{
		path: "home",
		component: HomeComponent
	},
	{
		path: "jobs",
		//canActivate : [ AuthGuard ],
		children : [
			{
				path : "",
				redirectTo : "dashboard",
				pathMatch: "prefix"
			},
			{
				path : "dashboard",
				component: JobsComponent
			},
			{
				path : "create",
				component: JobCreatorComponent
			},
			{
				path : "analysis",
				children : [
					{
						path : ":id",
						component : JobResultComponent
					}
				]
			}
		]
	},
	{
		path : "",
		pathMatch : "full",
		redirectTo : "home"
	}
];

@NgModule({
	declarations: [
		AppComponent,
		HeaderComponent,
		FooterComponent,
		LoginComponent,
		HomeComponent,
		JobsComponent,
		JobCreatorComponent,
		FileComponentComponent,
		FileDropDirective,
		ExternalDropDirective,
		JobResultComponent
	],
	imports: [
		BrowserModule,
		RouterModule.forRoot(routes, { useHash: true }),
		FlexLayoutModule,
		BsDropdownModule.forRoot(),
		HttpModule,
		FormsModule,
		BlockUIModule,
		TreeModule,
		ToastyModule.forRoot(),
		HttpClientModule
	],
	providers: [ServerInfoService, UserService, AuthGuard, JobsService,
		{
			provide: HTTP_INTERCEPTORS,
			useClass: AuthInterceptor,
			multi: true
		}],
	bootstrap: [AppComponent]
})
export class AppModule {
}
