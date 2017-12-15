webpackJsonp(["main"],{

/***/ "../../../../../src/$$_gendir lazy recursive":
/***/ (function(module, exports) {

function webpackEmptyAsyncContext(req) {
	// Here Promise.resolve().then() is used instead of new Promise() to prevent
	// uncatched exception popping up in devtools
	return Promise.resolve().then(function() {
		throw new Error("Cannot find module '" + req + "'.");
	});
}
webpackEmptyAsyncContext.keys = function() { return []; };
webpackEmptyAsyncContext.resolve = webpackEmptyAsyncContext;
module.exports = webpackEmptyAsyncContext;
webpackEmptyAsyncContext.id = "../../../../../src/$$_gendir lazy recursive";

/***/ }),

/***/ "../../../../../src/app/app.component.css":
/***/ (function(module, exports, __webpack_require__) {

exports = module.exports = __webpack_require__("../../../../css-loader/lib/css-base.js")(false);
// imports


// module
exports.push([module.i, "", ""]);

// exports


/*** EXPORTS FROM exports-loader ***/
module.exports = module.exports.toString();

/***/ }),

/***/ "../../../../../src/app/app.component.html":
/***/ (function(module, exports) {

module.exports = "<app-header></app-header>\n<router-outlet></router-outlet>\n<app-footer></app-footer>\n<ng2-toasty></ng2-toasty>\n"

/***/ }),

/***/ "../../../../../src/app/app.component.ts":
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "a", function() { return AppComponent; });
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_0__angular_core__ = __webpack_require__("../../../core/@angular/core.es5.js");
var __decorate = (this && this.__decorate) || function (decorators, target, key, desc) {
    var c = arguments.length, r = c < 3 ? target : desc === null ? desc = Object.getOwnPropertyDescriptor(target, key) : desc, d;
    if (typeof Reflect === "object" && typeof Reflect.decorate === "function") r = Reflect.decorate(decorators, target, key, desc);
    else for (var i = decorators.length - 1; i >= 0; i--) if (d = decorators[i]) r = (c < 3 ? d(r) : c > 3 ? d(target, key, r) : d(target, key)) || r;
    return c > 3 && r && Object.defineProperty(target, key, r), r;
};

var AppComponent = (function () {
    function AppComponent() {
        this.title = 'app';
    }
    return AppComponent;
}());
AppComponent = __decorate([
    Object(__WEBPACK_IMPORTED_MODULE_0__angular_core__["Component"])({
        selector: 'app-root',
        template: __webpack_require__("../../../../../src/app/app.component.html"),
        styles: [__webpack_require__("../../../../../src/app/app.component.css")]
    })
], AppComponent);

//# sourceMappingURL=app.component.js.map

/***/ }),

/***/ "../../../../../src/app/app.module.ts":
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "a", function() { return AppModule; });
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_0__angular_platform_browser__ = __webpack_require__("../../../platform-browser/@angular/platform-browser.es5.js");
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_1__angular_core__ = __webpack_require__("../../../core/@angular/core.es5.js");
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_2__angular_common_http__ = __webpack_require__("../../../common/@angular/common/http.es5.js");
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_3__app_component__ = __webpack_require__("../../../../../src/app/app.component.ts");
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_4__header_header_component__ = __webpack_require__("../../../../../src/app/header/header.component.ts");
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_5__footer_footer_component__ = __webpack_require__("../../../../../src/app/footer/footer.component.ts");
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_6__login_login_component__ = __webpack_require__("../../../../../src/app/login/login.component.ts");
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_7__angular_router__ = __webpack_require__("../../../router/@angular/router.es5.js");
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_8__home_home_component__ = __webpack_require__("../../../../../src/app/home/home.component.ts");
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_9__services_user_service__ = __webpack_require__("../../../../../src/app/services/user.service.ts");
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_10__angular_flex_layout__ = __webpack_require__("../../../flex-layout/esm5/flex-layout.es5.js");
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_11__services_server_info_service__ = __webpack_require__("../../../../../src/app/services/server-info.service.ts");
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_12__angular_http__ = __webpack_require__("../../../http/@angular/http.es5.js");
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_13__services_auth_interceptor__ = __webpack_require__("../../../../../src/app/services/auth.interceptor.ts");
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_14__angular_forms__ = __webpack_require__("../../../forms/@angular/forms.es5.js");
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_15__jobs_jobs_component__ = __webpack_require__("../../../../../src/app/jobs/jobs.component.ts");
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_16__guards_auth_guard__ = __webpack_require__("../../../../../src/app/guards/auth.guard.ts");
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_17_ngx_bootstrap__ = __webpack_require__("../../../../ngx-bootstrap/index.js");
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_18__services_jobs_service__ = __webpack_require__("../../../../../src/app/services/jobs.service.ts");
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_19__job_creator_job_creator_component__ = __webpack_require__("../../../../../src/app/job-creator/job-creator.component.ts");
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_20__file_component_file_component_component__ = __webpack_require__("../../../../../src/app/file-component/file-component.component.ts");
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_21__file_drop_directive__ = __webpack_require__("../../../../../src/app/file-drop.directive.ts");
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_22__external_drop_directive__ = __webpack_require__("../../../../../src/app/external-drop.directive.ts");
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_23_ng2_toasty__ = __webpack_require__("../../../../ng2-toasty/index.js");
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_24__job_result_job_result_component__ = __webpack_require__("../../../../../src/app/job-result/job-result.component.ts");
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_25_primeng_components_tree_tree__ = __webpack_require__("../../../../primeng/components/tree/tree.js");
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_25_primeng_components_tree_tree___default = __webpack_require__.n(__WEBPACK_IMPORTED_MODULE_25_primeng_components_tree_tree__);
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_26_ng_block_ui__ = __webpack_require__("../../../../ng-block-ui/dist/index.js");
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_26_ng_block_ui___default = __webpack_require__.n(__WEBPACK_IMPORTED_MODULE_26_ng_block_ui__);
var __decorate = (this && this.__decorate) || function (decorators, target, key, desc) {
    var c = arguments.length, r = c < 3 ? target : desc === null ? desc = Object.getOwnPropertyDescriptor(target, key) : desc, d;
    if (typeof Reflect === "object" && typeof Reflect.decorate === "function") r = Reflect.decorate(decorators, target, key, desc);
    else for (var i = decorators.length - 1; i >= 0; i--) if (d = decorators[i]) r = (c < 3 ? d(r) : c > 3 ? d(target, key, r) : d(target, key)) || r;
    return c > 3 && r && Object.defineProperty(target, key, r), r;
};



























var routes = [
    {
        path: "login",
        component: __WEBPACK_IMPORTED_MODULE_6__login_login_component__["a" /* LoginComponent */]
    },
    {
        path: "home",
        component: __WEBPACK_IMPORTED_MODULE_8__home_home_component__["a" /* HomeComponent */]
    },
    {
        path: "jobs",
        //canActivate : [ AuthGuard ],
        children: [
            {
                path: "",
                redirectTo: "dashboard",
                pathMatch: "prefix"
            },
            {
                path: "dashboard",
                component: __WEBPACK_IMPORTED_MODULE_15__jobs_jobs_component__["a" /* JobsComponent */]
            },
            {
                path: "create",
                component: __WEBPACK_IMPORTED_MODULE_19__job_creator_job_creator_component__["a" /* JobCreatorComponent */]
            },
            {
                path: "analysis",
                children: [
                    {
                        path: ":id",
                        component: __WEBPACK_IMPORTED_MODULE_24__job_result_job_result_component__["a" /* JobResultComponent */]
                    }
                ]
            }
        ]
    },
    {
        path: "",
        pathMatch: "full",
        redirectTo: "home"
    }
];
var AppModule = (function () {
    function AppModule() {
    }
    return AppModule;
}());
AppModule = __decorate([
    Object(__WEBPACK_IMPORTED_MODULE_1__angular_core__["NgModule"])({
        declarations: [
            __WEBPACK_IMPORTED_MODULE_3__app_component__["a" /* AppComponent */],
            __WEBPACK_IMPORTED_MODULE_4__header_header_component__["a" /* HeaderComponent */],
            __WEBPACK_IMPORTED_MODULE_5__footer_footer_component__["a" /* FooterComponent */],
            __WEBPACK_IMPORTED_MODULE_6__login_login_component__["a" /* LoginComponent */],
            __WEBPACK_IMPORTED_MODULE_8__home_home_component__["a" /* HomeComponent */],
            __WEBPACK_IMPORTED_MODULE_15__jobs_jobs_component__["a" /* JobsComponent */],
            __WEBPACK_IMPORTED_MODULE_19__job_creator_job_creator_component__["a" /* JobCreatorComponent */],
            __WEBPACK_IMPORTED_MODULE_20__file_component_file_component_component__["a" /* FileComponentComponent */],
            __WEBPACK_IMPORTED_MODULE_21__file_drop_directive__["a" /* FileDropDirective */],
            __WEBPACK_IMPORTED_MODULE_22__external_drop_directive__["a" /* ExternalDropDirective */],
            __WEBPACK_IMPORTED_MODULE_24__job_result_job_result_component__["a" /* JobResultComponent */]
        ],
        imports: [
            __WEBPACK_IMPORTED_MODULE_0__angular_platform_browser__["a" /* BrowserModule */],
            __WEBPACK_IMPORTED_MODULE_7__angular_router__["c" /* RouterModule */].forRoot(routes, { useHash: true }),
            __WEBPACK_IMPORTED_MODULE_10__angular_flex_layout__["a" /* FlexLayoutModule */],
            __WEBPACK_IMPORTED_MODULE_17_ngx_bootstrap__["a" /* BsDropdownModule */].forRoot(),
            __WEBPACK_IMPORTED_MODULE_12__angular_http__["b" /* HttpModule */],
            __WEBPACK_IMPORTED_MODULE_14__angular_forms__["a" /* FormsModule */],
            __WEBPACK_IMPORTED_MODULE_26_ng_block_ui__["BlockUIModule"],
            __WEBPACK_IMPORTED_MODULE_25_primeng_components_tree_tree__["TreeModule"],
            __WEBPACK_IMPORTED_MODULE_23_ng2_toasty__["b" /* ToastyModule */].forRoot(),
            __WEBPACK_IMPORTED_MODULE_2__angular_common_http__["c" /* HttpClientModule */]
        ],
        providers: [__WEBPACK_IMPORTED_MODULE_11__services_server_info_service__["b" /* ServerInfoService */], __WEBPACK_IMPORTED_MODULE_9__services_user_service__["a" /* UserService */], __WEBPACK_IMPORTED_MODULE_16__guards_auth_guard__["a" /* AuthGuard */], __WEBPACK_IMPORTED_MODULE_18__services_jobs_service__["a" /* JobsService */],
            {
                provide: __WEBPACK_IMPORTED_MODULE_2__angular_common_http__["a" /* HTTP_INTERCEPTORS */],
                useClass: __WEBPACK_IMPORTED_MODULE_13__services_auth_interceptor__["a" /* AuthInterceptor */],
                multi: true
            }],
        bootstrap: [__WEBPACK_IMPORTED_MODULE_3__app_component__["a" /* AppComponent */]]
    })
], AppModule);

//# sourceMappingURL=app.module.js.map

/***/ }),

/***/ "../../../../../src/app/external-drop.directive.ts":
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "a", function() { return ExternalDropDirective; });
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_0__angular_core__ = __webpack_require__("../../../core/@angular/core.es5.js");
var __decorate = (this && this.__decorate) || function (decorators, target, key, desc) {
    var c = arguments.length, r = c < 3 ? target : desc === null ? desc = Object.getOwnPropertyDescriptor(target, key) : desc, d;
    if (typeof Reflect === "object" && typeof Reflect.decorate === "function") r = Reflect.decorate(decorators, target, key, desc);
    else for (var i = decorators.length - 1; i >= 0; i--) if (d = decorators[i]) r = (c < 3 ? d(r) : c > 3 ? d(target, key, r) : d(target, key)) || r;
    return c > 3 && r && Object.defineProperty(target, key, r), r;
};
var __metadata = (this && this.__metadata) || function (k, v) {
    if (typeof Reflect === "object" && typeof Reflect.metadata === "function") return Reflect.metadata(k, v);
};

var ExternalDropDirective = (function () {
    function ExternalDropDirective() {
    }
    ExternalDropDirective.prototype.dragStart = function (event) {
        event.dataTransfer.setData('text', "XXX");
    };
    ExternalDropDirective.prototype.drag = function (event) {
        event.preventDefault();
        event.stopPropagation();
    };
    ExternalDropDirective.prototype.dragEnd = function (event) {
        event.preventDefault();
        event.stopPropagation();
        console.log("DRAG");
    };
    ExternalDropDirective.prototype.mouseover = function (event) {
        event.preventDefault();
        event.stopPropagation();
        console.log("DRAG");
    };
    ExternalDropDirective.prototype.mouseleave = function (event) {
        event.preventDefault();
        event.stopPropagation();
        console.log("DRAG");
    };
    ExternalDropDirective.prototype.dragstart = function (event) {
        event.preventDefault();
        event.stopPropagation();
        console.log("DRAG-START");
        event.dataTransfer = "ABCD";
    };
    return ExternalDropDirective;
}());
__decorate([
    Object(__WEBPACK_IMPORTED_MODULE_0__angular_core__["Input"])(),
    __metadata("design:type", String)
], ExternalDropDirective.prototype, "value", void 0);
__decorate([
    Object(__WEBPACK_IMPORTED_MODULE_0__angular_core__["HostListener"])('dragstart', ['$event']),
    __metadata("design:type", Function),
    __metadata("design:paramtypes", [Object]),
    __metadata("design:returntype", void 0)
], ExternalDropDirective.prototype, "dragStart", null);
__decorate([
    Object(__WEBPACK_IMPORTED_MODULE_0__angular_core__["HostListener"])('drag', ['$event']),
    __metadata("design:type", Function),
    __metadata("design:paramtypes", [Object]),
    __metadata("design:returntype", void 0)
], ExternalDropDirective.prototype, "drag", null);
__decorate([
    Object(__WEBPACK_IMPORTED_MODULE_0__angular_core__["HostListener"])('dragend', ['$event']),
    __metadata("design:type", Function),
    __metadata("design:paramtypes", [Object]),
    __metadata("design:returntype", void 0)
], ExternalDropDirective.prototype, "dragEnd", null);
__decorate([
    Object(__WEBPACK_IMPORTED_MODULE_0__angular_core__["HostListener"])('mouseover', ['$event']),
    __metadata("design:type", Function),
    __metadata("design:paramtypes", [Object]),
    __metadata("design:returntype", void 0)
], ExternalDropDirective.prototype, "mouseover", null);
__decorate([
    Object(__WEBPACK_IMPORTED_MODULE_0__angular_core__["HostListener"])('mouseleave', ['$event']),
    __metadata("design:type", Function),
    __metadata("design:paramtypes", [Object]),
    __metadata("design:returntype", void 0)
], ExternalDropDirective.prototype, "mouseleave", null);
__decorate([
    Object(__WEBPACK_IMPORTED_MODULE_0__angular_core__["HostListener"])('dragstart', ['$event']),
    __metadata("design:type", Function),
    __metadata("design:paramtypes", [Object]),
    __metadata("design:returntype", void 0)
], ExternalDropDirective.prototype, "dragstart", null);
ExternalDropDirective = __decorate([
    Object(__WEBPACK_IMPORTED_MODULE_0__angular_core__["Directive"])({
        selector: '[appExternalDrop]'
    }),
    __metadata("design:paramtypes", [])
], ExternalDropDirective);

//# sourceMappingURL=external-drop.directive.js.map

/***/ }),

/***/ "../../../../../src/app/file-component/file-component.component.css":
/***/ (function(module, exports, __webpack_require__) {

exports = module.exports = __webpack_require__("../../../../css-loader/lib/css-base.js")(false);
// imports


// module
exports.push([module.i, ".upload {\n\tposition: absolute;\n\twidth: 100%;\n}\n\n.file-upload {\n\tcursor: pointer;\n\theight: 100%;\n\topacity: 0;\n\tfilter: alpha(opacity=0);\n}\n\n.sleep {\n\tborder: 2px dashed lightgrey;\n}\n\n.wake {\n\tborder: 3px dashed darkblue;\n}\n\ndiv {\n\tposition: relative;\n\tvertical-align: middle;\n\ttext-align: center;\n}\n", ""]);

// exports


/*** EXPORTS FROM exports-loader ***/
module.exports = module.exports.toString();

/***/ }),

/***/ "../../../../../src/app/file-component/file-component.component.html":
/***/ (function(module, exports) {

module.exports = "<div fxLayoutAlign=\"center stretch\" fxLayout=\"column\" style=\"height: 100%; width: 100%;\">\n\t<div fileDrop (hover)=\"onHover($event)\" (dropped)=\"drop($event)\" fxFlex=\"100\" style=\"padding: 8px; background-color: lightgrey;\" fxLayoutAlign=\"center stretch\" fxLayout=\"column\">\n\t\t<div fxFlex=\"100\" style=\"position: relative; background-color: aliceblue;\" [class]=\"hover ? 'wake' : 'sleep'\" fxLayoutAlign=\"center stretch\" fxLayout=\"column\">\n\t\t\t<span *ngIf=\"!current\" style=\"color: darkblue; opacity: 0.5; font-weight: bold; font-size: 20px; text-align: center;\">{{label}}</span>\n\t\t\t<i (click)=\"clear()\" *ngIf=\"current\" class=\"fa fa-minus\" style=\"color: darkred; z-index : 100000000; position: absolute; top: 5px; right: 5px; cursor: pointer;\"></i>\n\t\t\t<i *ngIf=\"current\" class=\"fa fa-file\" style=\"font-size: 5em; color: darkgrey;\"></i>\n\t\t\t<span fxFlexOffset=\"5\" *ngIf=\"current\" style=\"text-align: center; word-wrap:break-word;font-weight: bold; color: darkgrey;\">{{current.name}}</span>\n\t\t\t<input #input name=\"up2\" type=\"file\" (change)=\"change($event)\" class=\"upload file-upload\" />\n\t\t</div>\n\t</div>\n</div>\n\n"

/***/ }),

/***/ "../../../../../src/app/file-component/file-component.component.ts":
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "a", function() { return FileComponentComponent; });
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_0__angular_core__ = __webpack_require__("../../../core/@angular/core.es5.js");
var __decorate = (this && this.__decorate) || function (decorators, target, key, desc) {
    var c = arguments.length, r = c < 3 ? target : desc === null ? desc = Object.getOwnPropertyDescriptor(target, key) : desc, d;
    if (typeof Reflect === "object" && typeof Reflect.decorate === "function") r = Reflect.decorate(decorators, target, key, desc);
    else for (var i = decorators.length - 1; i >= 0; i--) if (d = decorators[i]) r = (c < 3 ? d(r) : c > 3 ? d(target, key, r) : d(target, key)) || r;
    return c > 3 && r && Object.defineProperty(target, key, r), r;
};
var __metadata = (this && this.__metadata) || function (k, v) {
    if (typeof Reflect === "object" && typeof Reflect.metadata === "function") return Reflect.metadata(k, v);
};

var FileComponentComponent = (function () {
    function FileComponentComponent() {
        this.file = new __WEBPACK_IMPORTED_MODULE_0__angular_core__["EventEmitter"]();
    }
    FileComponentComponent.prototype.ngOnInit = function () {
        this.hover = false;
    };
    FileComponentComponent.prototype.clear = function () {
        this.current = null;
        this.input.nativeElement.value = "";
        this.file.emit(null);
        this.hover = false;
    };
    FileComponentComponent.prototype.change = function (event) {
        var files = event.srcElement.files;
        if (files.length == 1) {
            var file = files[0];
            this.current = file;
            this.input.nativeElement.value = "";
            this.file.emit(file);
            this.hover = false;
        }
    };
    FileComponentComponent.prototype.drop = function (file) {
        this.current = file;
        this.file.emit(file);
        this.hover = false;
    };
    FileComponentComponent.prototype.onHover = function (b) {
        this.hover = b;
    };
    return FileComponentComponent;
}());
__decorate([
    Object(__WEBPACK_IMPORTED_MODULE_0__angular_core__["Output"])(),
    __metadata("design:type", typeof (_a = typeof __WEBPACK_IMPORTED_MODULE_0__angular_core__["EventEmitter"] !== "undefined" && __WEBPACK_IMPORTED_MODULE_0__angular_core__["EventEmitter"]) === "function" && _a || Object)
], FileComponentComponent.prototype, "file", void 0);
__decorate([
    Object(__WEBPACK_IMPORTED_MODULE_0__angular_core__["ViewChild"])('input'),
    __metadata("design:type", typeof (_b = typeof __WEBPACK_IMPORTED_MODULE_0__angular_core__["ElementRef"] !== "undefined" && __WEBPACK_IMPORTED_MODULE_0__angular_core__["ElementRef"]) === "function" && _b || Object)
], FileComponentComponent.prototype, "input", void 0);
__decorate([
    Object(__WEBPACK_IMPORTED_MODULE_0__angular_core__["Input"])(),
    __metadata("design:type", String)
], FileComponentComponent.prototype, "label", void 0);
FileComponentComponent = __decorate([
    Object(__WEBPACK_IMPORTED_MODULE_0__angular_core__["Component"])({
        selector: 'app-file-component',
        template: __webpack_require__("../../../../../src/app/file-component/file-component.component.html"),
        styles: [__webpack_require__("../../../../../src/app/file-component/file-component.component.css")]
    }),
    __metadata("design:paramtypes", [])
], FileComponentComponent);

var _a, _b;
//# sourceMappingURL=file-component.component.js.map

/***/ }),

/***/ "../../../../../src/app/file-drop.directive.ts":
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "a", function() { return FileDropDirective; });
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_0__angular_core__ = __webpack_require__("../../../core/@angular/core.es5.js");
var __decorate = (this && this.__decorate) || function (decorators, target, key, desc) {
    var c = arguments.length, r = c < 3 ? target : desc === null ? desc = Object.getOwnPropertyDescriptor(target, key) : desc, d;
    if (typeof Reflect === "object" && typeof Reflect.decorate === "function") r = Reflect.decorate(decorators, target, key, desc);
    else for (var i = decorators.length - 1; i >= 0; i--) if (d = decorators[i]) r = (c < 3 ? d(r) : c > 3 ? d(target, key, r) : d(target, key)) || r;
    return c > 3 && r && Object.defineProperty(target, key, r), r;
};
var __metadata = (this && this.__metadata) || function (k, v) {
    if (typeof Reflect === "object" && typeof Reflect.metadata === "function") return Reflect.metadata(k, v);
};

var FileDropDirective = (function () {
    function FileDropDirective() {
        this.dropped = new __WEBPACK_IMPORTED_MODULE_0__angular_core__["EventEmitter"]();
        this.hover = new __WEBPACK_IMPORTED_MODULE_0__angular_core__["EventEmitter"]();
    }
    FileDropDirective.prototype.onMouseEnter = function () {
        console.log("MOUSE ENTER");
    };
    FileDropDirective.prototype.onDrop = function (event) {
        event.preventDefault();
        event.stopPropagation();
        var files = event.dataTransfer.files;
        if (files.length == 1) {
            this.dropped.emit(files[0]);
        }
    };
    FileDropDirective.prototype.dragover = function (event) {
        event.preventDefault();
        event.stopPropagation();
        this.hover.emit(true);
    };
    FileDropDirective.prototype.dragleave = function (event) {
        event.preventDefault();
        event.stopPropagation();
        this.hover.emit(false);
    };
    return FileDropDirective;
}());
__decorate([
    Object(__WEBPACK_IMPORTED_MODULE_0__angular_core__["Output"])(),
    __metadata("design:type", typeof (_a = typeof __WEBPACK_IMPORTED_MODULE_0__angular_core__["EventEmitter"] !== "undefined" && __WEBPACK_IMPORTED_MODULE_0__angular_core__["EventEmitter"]) === "function" && _a || Object)
], FileDropDirective.prototype, "dropped", void 0);
__decorate([
    Object(__WEBPACK_IMPORTED_MODULE_0__angular_core__["Output"])(),
    __metadata("design:type", typeof (_b = typeof __WEBPACK_IMPORTED_MODULE_0__angular_core__["EventEmitter"] !== "undefined" && __WEBPACK_IMPORTED_MODULE_0__angular_core__["EventEmitter"]) === "function" && _b || Object)
], FileDropDirective.prototype, "hover", void 0);
__decorate([
    Object(__WEBPACK_IMPORTED_MODULE_0__angular_core__["HostListener"])('mouseenter'),
    __metadata("design:type", Function),
    __metadata("design:paramtypes", []),
    __metadata("design:returntype", void 0)
], FileDropDirective.prototype, "onMouseEnter", null);
__decorate([
    Object(__WEBPACK_IMPORTED_MODULE_0__angular_core__["HostListener"])('drop', ['$event']),
    __metadata("design:type", Function),
    __metadata("design:paramtypes", [Object]),
    __metadata("design:returntype", void 0)
], FileDropDirective.prototype, "onDrop", null);
__decorate([
    Object(__WEBPACK_IMPORTED_MODULE_0__angular_core__["HostListener"])('dragover', ['$event']),
    __metadata("design:type", Function),
    __metadata("design:paramtypes", [Object]),
    __metadata("design:returntype", void 0)
], FileDropDirective.prototype, "dragover", null);
__decorate([
    Object(__WEBPACK_IMPORTED_MODULE_0__angular_core__["HostListener"])('dragleave', ['$event']),
    __metadata("design:type", Function),
    __metadata("design:paramtypes", [Object]),
    __metadata("design:returntype", void 0)
], FileDropDirective.prototype, "dragleave", null);
FileDropDirective = __decorate([
    Object(__WEBPACK_IMPORTED_MODULE_0__angular_core__["Directive"])({
        selector: '[fileDrop]'
    }),
    __metadata("design:paramtypes", [])
], FileDropDirective);

var _a, _b;
//# sourceMappingURL=file-drop.directive.js.map

/***/ }),

/***/ "../../../../../src/app/footer/footer.component.css":
/***/ (function(module, exports, __webpack_require__) {

exports = module.exports = __webpack_require__("../../../../css-loader/lib/css-base.js")(false);
// imports


// module
exports.push([module.i, "", ""]);

// exports


/*** EXPORTS FROM exports-loader ***/
module.exports = module.exports.toString();

/***/ }),

/***/ "../../../../../src/app/footer/footer.component.html":
/***/ (function(module, exports) {

module.exports = "<div style=\"width: 100%; padding : 10px; border-top: 1px solid darkgrey; background-color: #ecdc988a;\" fxLayout=\"row\" fxLayoutAlign=\"space-between center\">\n\t<img src=\"assets/images/nist-logo.png\" height=\"50px\">\n\t<div fxLayout=\"column\" fxLayoutAlign=\"start start\">\n\t\t<span style=\"font-weight: bold;\">Application Date</span>\n\t\t<span>{{sInfo().date | date}}</span>\n\t</div>\n\t<div fxLayout=\"column\" fxLayoutAlign=\"start start\">\n\t\t<span style=\"font-weight: bold;\">Supported Browsers</span>\n\t\t<span>\n\t\t\t<i class=\"fa fa-firefox\"></i> Firefox, <i class=\"fa fa-chrome\"></i> Chrome, <i class=\"fa fa-safari\"></i>Safari, <i class=\"fa fa-internet-explorer\"></i> IE 9+\n\t\t</span>\n\t</div>\n\t<div fxLayout=\"column\" fxLayoutAlign=\"start start\">\n\t\t<span style=\"font-weight: bold;\">External Links</span>\n\t\t<div fxLayout=\"row\" fxLayoutAlign=\"start start\">\n\t\t\t<a href=\"http://www.nist.gov/public_affairs/disclaimer.cfm\" target=\"_blank\" title=\"View Disclaimer\">Disclaimer</a>\n\t\t\t<a href=\"http://www.nist.gov/public_affairs/privacy.cfm#privpolicy\" target=\"_blank\" title=\"View Privacy Policy\">Privacy/Policy</a>\n\t\t\t<a ng-href=\"mailto:\" title=\"Email Website Administrator @: \" href=\"mailto:\">Website Administrator</a>\n\t\t</div>\n\t</div>\n</div>\n"

/***/ }),

/***/ "../../../../../src/app/footer/footer.component.ts":
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "a", function() { return FooterComponent; });
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_0__angular_core__ = __webpack_require__("../../../core/@angular/core.es5.js");
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_1__services_server_info_service__ = __webpack_require__("../../../../../src/app/services/server-info.service.ts");
var __decorate = (this && this.__decorate) || function (decorators, target, key, desc) {
    var c = arguments.length, r = c < 3 ? target : desc === null ? desc = Object.getOwnPropertyDescriptor(target, key) : desc, d;
    if (typeof Reflect === "object" && typeof Reflect.decorate === "function") r = Reflect.decorate(decorators, target, key, desc);
    else for (var i = decorators.length - 1; i >= 0; i--) if (d = decorators[i]) r = (c < 3 ? d(r) : c > 3 ? d(target, key, r) : d(target, key)) || r;
    return c > 3 && r && Object.defineProperty(target, key, r), r;
};
var __metadata = (this && this.__metadata) || function (k, v) {
    if (typeof Reflect === "object" && typeof Reflect.metadata === "function") return Reflect.metadata(k, v);
};
var __awaiter = (this && this.__awaiter) || function (thisArg, _arguments, P, generator) {
    return new (P || (P = Promise))(function (resolve, reject) {
        function fulfilled(value) { try { step(generator.next(value)); } catch (e) { reject(e); } }
        function rejected(value) { try { step(generator["throw"](value)); } catch (e) { reject(e); } }
        function step(result) { result.done ? resolve(result.value) : new P(function (resolve) { resolve(result.value); }).then(fulfilled, rejected); }
        step((generator = generator.apply(thisArg, _arguments || [])).next());
    });
};
var __generator = (this && this.__generator) || function (thisArg, body) {
    var _ = { label: 0, sent: function() { if (t[0] & 1) throw t[1]; return t[1]; }, trys: [], ops: [] }, f, y, t, g;
    return g = { next: verb(0), "throw": verb(1), "return": verb(2) }, typeof Symbol === "function" && (g[Symbol.iterator] = function() { return this; }), g;
    function verb(n) { return function (v) { return step([n, v]); }; }
    function step(op) {
        if (f) throw new TypeError("Generator is already executing.");
        while (_) try {
            if (f = 1, y && (t = y[op[0] & 2 ? "return" : op[0] ? "throw" : "next"]) && !(t = t.call(y, op[1])).done) return t;
            if (y = 0, t) op = [0, t.value];
            switch (op[0]) {
                case 0: case 1: t = op; break;
                case 4: _.label++; return { value: op[1], done: false };
                case 5: _.label++; y = op[1]; op = [0]; continue;
                case 7: op = _.ops.pop(); _.trys.pop(); continue;
                default:
                    if (!(t = _.trys, t = t.length > 0 && t[t.length - 1]) && (op[0] === 6 || op[0] === 2)) { _ = 0; continue; }
                    if (op[0] === 3 && (!t || (op[1] > t[0] && op[1] < t[3]))) { _.label = op[1]; break; }
                    if (op[0] === 6 && _.label < t[1]) { _.label = t[1]; t = op; break; }
                    if (t && _.label < t[2]) { _.label = t[2]; _.ops.push(op); break; }
                    if (t[2]) _.ops.pop();
                    _.trys.pop(); continue;
            }
            op = body.call(thisArg, _);
        } catch (e) { op = [6, e]; y = 0; } finally { f = t = 0; }
        if (op[0] & 5) throw op[1]; return { value: op[0] ? op[1] : void 0, done: true };
    }
};


var FooterComponent = (function () {
    function FooterComponent(sInfoService) {
        this.sInfoService = sInfoService;
        this.serverInfo = new __WEBPACK_IMPORTED_MODULE_1__services_server_info_service__["a" /* ServerInfo */]("", "", new Date());
    }
    FooterComponent.prototype.ngOnInit = function () {
        return __awaiter(this, void 0, void 0, function () {
            var _a;
            return __generator(this, function (_b) {
                switch (_b.label) {
                    case 0:
                        _a = this;
                        return [4 /*yield*/, this.sInfoService.get()];
                    case 1:
                        _a.serverInfo = _b.sent();
                        return [2 /*return*/];
                }
            });
        });
    };
    FooterComponent.prototype.sInfo = function () {
        return this.serverInfo;
    };
    return FooterComponent;
}());
FooterComponent = __decorate([
    Object(__WEBPACK_IMPORTED_MODULE_0__angular_core__["Component"])({
        selector: 'app-footer',
        template: __webpack_require__("../../../../../src/app/footer/footer.component.html"),
        styles: [__webpack_require__("../../../../../src/app/footer/footer.component.css")]
    }),
    __metadata("design:paramtypes", [typeof (_a = typeof __WEBPACK_IMPORTED_MODULE_1__services_server_info_service__["b" /* ServerInfoService */] !== "undefined" && __WEBPACK_IMPORTED_MODULE_1__services_server_info_service__["b" /* ServerInfoService */]) === "function" && _a || Object])
], FooterComponent);

var _a;
//# sourceMappingURL=footer.component.js.map

/***/ }),

/***/ "../../../../../src/app/guards/auth.guard.ts":
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "a", function() { return AuthGuard; });
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_0__angular_core__ = __webpack_require__("../../../core/@angular/core.es5.js");
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_1__angular_router__ = __webpack_require__("../../../router/@angular/router.es5.js");
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_2__services_user_service__ = __webpack_require__("../../../../../src/app/services/user.service.ts");
var __decorate = (this && this.__decorate) || function (decorators, target, key, desc) {
    var c = arguments.length, r = c < 3 ? target : desc === null ? desc = Object.getOwnPropertyDescriptor(target, key) : desc, d;
    if (typeof Reflect === "object" && typeof Reflect.decorate === "function") r = Reflect.decorate(decorators, target, key, desc);
    else for (var i = decorators.length - 1; i >= 0; i--) if (d = decorators[i]) r = (c < 3 ? d(r) : c > 3 ? d(target, key, r) : d(target, key)) || r;
    return c > 3 && r && Object.defineProperty(target, key, r), r;
};
var __metadata = (this && this.__metadata) || function (k, v) {
    if (typeof Reflect === "object" && typeof Reflect.metadata === "function") return Reflect.metadata(k, v);
};



var AuthGuard = (function () {
    function AuthGuard(router, $user) {
        this.router = router;
        this.$user = $user;
    }
    AuthGuard.prototype.canActivate = function (next, state) {
        var ctrl = this;
        return new Promise(function (resolve, reject) {
            ctrl.$user.me().then(function (x) {
                if (x && x.status)
                    resolve(true);
                else {
                    ctrl.router.navigate(['home']);
                    resolve(false);
                }
            }, function (err) {
                ctrl.router.navigate(['home']);
                resolve(false);
            });
        });
    };
    return AuthGuard;
}());
AuthGuard = __decorate([
    Object(__WEBPACK_IMPORTED_MODULE_0__angular_core__["Injectable"])(),
    __metadata("design:paramtypes", [typeof (_a = typeof __WEBPACK_IMPORTED_MODULE_1__angular_router__["b" /* Router */] !== "undefined" && __WEBPACK_IMPORTED_MODULE_1__angular_router__["b" /* Router */]) === "function" && _a || Object, typeof (_b = typeof __WEBPACK_IMPORTED_MODULE_2__services_user_service__["a" /* UserService */] !== "undefined" && __WEBPACK_IMPORTED_MODULE_2__services_user_service__["a" /* UserService */]) === "function" && _b || Object])
], AuthGuard);

var _a, _b;
//# sourceMappingURL=auth.guard.js.map

/***/ }),

/***/ "../../../../../src/app/header/header.component.css":
/***/ (function(module, exports, __webpack_require__) {

exports = module.exports = __webpack_require__("../../../../css-loader/lib/css-base.js")(false);
// imports


// module
exports.push([module.i, ".nav-back {\n\tbackground-color: #031a4c;\n\tbox-shadow:         0px 5px 5px 0px rgba(0, 0, 0, 0.5);\n\tborder-bottom: 1px solid black;\n}\n\n.nav-link:hover {\n\tcolor: #F3C60D !important;\n}\n\n.active {\n\ttext-shadow: 0px 0px 21px rgba(255, 255, 255, 0.6);\n\tcolor: #F3C60D !important;\n}\n", ""]);

// exports


/*** EXPORTS FROM exports-loader ***/
module.exports = module.exports.toString();

/***/ }),

/***/ "../../../../../src/app/header/header.component.html":
/***/ (function(module, exports) {

module.exports = "<nav fxLayout=\"row\" fxLayoutAlign=\"space-between center\" class=\"navbar fixed-top navbar-dark nav-back\">\n\t<div fxFlex=\"30\" fxLayout=\"row\" fxLayoutAlign=\"start center\">\n\t\t<a class=\"navbar-brand\" href=\"#\">\n\t\t\t<span style=\"color: #F3C60D; font-size: 25px; font-weight: bold;\">NIST</span>&nbsp;&nbsp;<span\n\t\t\tstyle=\"font-size: 17px;\"><strong>D</strong>ATA <strong>A</strong>T <strong>R</strong>EST <strong>QU</strong>ALITY <strong>A</strong>NALYSIS</span>\n\t\t\t&nbsp;<span class=\"badge\" style=\"background-color: orangered; font-size: 10px;\">{{sInfo().version}}-{{sInfo().qualifier}}</span>\n\t\t</a>\n\t</div>\n\t<div fxFlex=\"70\" style=\"padding-left : 50px; padding-right: 50px;\" fxLayout=\"row\" fxLayoutAlign=\"space-between center\">\n\t\t<div fxFlex=\"50\" fxLayoutAlign=\"space-between center\">\n\t\t\t<a class=\"nav-item nav-link\" [routerLink]=\"[ 'home' ]\"  routerLinkActive=\"active\">Home</a>\n\t\t\t<a *ngIf=\"logged()\" class=\"nav-item nav-link\" [routerLink]=\"[ 'jobs' ]\"  routerLinkActive=\"active\">Jobs</a>\n\t\t\t<a *ngIf=\"logged()\" class=\"nav-item nav-link\" [routerLink]=\"[ 'configuration' ]\"  routerLinkActive=\"active\">Configuration</a>\n\t\t\t<a class=\"nav-item nav-link\" [routerLink]=\"[ 'release-notes' ]\"  routerLinkActive=\"active\">Release Note</a>\n\t\t\t<a class=\"nav-item nav-link\" [routerLink]=\"[ 'issues' ]\"  routerLinkActive=\"active\">Issues</a>\n\t\t\t<a class=\"nav-item nav-link\" [routerLink]=\"[ 'about' ]\"  routerLinkActive=\"active\">About</a>\n\t\t</div>\n\t\t<div fxFlex=\"50\"  fxLayoutAlign=\"end center\">\n\t\t\t<div *ngIf=\"!logged()\">\n\t\t\t\t<a  class=\"nav-item nav-link\" [routerLink]=\"[ 'login' ]\" routerLinkActive=\"active\" >Login</a>\n\t\t\t</div>\n\t\t\t<div *ngIf=\"logged()\" fxLayoutAlign=\"end center\">\n\t\t\t\t<span style=\"color: white; margin-right : 10px;\">\n\t\t\t\t\tWelcome <span style=\"color: #F3C60D !important;\">{{user().username}} !</span>\n\t\t\t\t</span>\n\t\t\t\t<i class=\"fa fa-sign-out\" aria-hidden=\"true\" style=\"font-size: 2em; color: red; cursor: pointer;\" (click)=\"logout()\"></i>\n\t\t\t</div>\n\t\t</div>\n\t</div>\n</nav>\n\n"

/***/ }),

/***/ "../../../../../src/app/header/header.component.ts":
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "a", function() { return HeaderComponent; });
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_0__angular_core__ = __webpack_require__("../../../core/@angular/core.es5.js");
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_1__services_server_info_service__ = __webpack_require__("../../../../../src/app/services/server-info.service.ts");
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_2__services_user_service__ = __webpack_require__("../../../../../src/app/services/user.service.ts");
var __decorate = (this && this.__decorate) || function (decorators, target, key, desc) {
    var c = arguments.length, r = c < 3 ? target : desc === null ? desc = Object.getOwnPropertyDescriptor(target, key) : desc, d;
    if (typeof Reflect === "object" && typeof Reflect.decorate === "function") r = Reflect.decorate(decorators, target, key, desc);
    else for (var i = decorators.length - 1; i >= 0; i--) if (d = decorators[i]) r = (c < 3 ? d(r) : c > 3 ? d(target, key, r) : d(target, key)) || r;
    return c > 3 && r && Object.defineProperty(target, key, r), r;
};
var __metadata = (this && this.__metadata) || function (k, v) {
    if (typeof Reflect === "object" && typeof Reflect.metadata === "function") return Reflect.metadata(k, v);
};
var __awaiter = (this && this.__awaiter) || function (thisArg, _arguments, P, generator) {
    return new (P || (P = Promise))(function (resolve, reject) {
        function fulfilled(value) { try { step(generator.next(value)); } catch (e) { reject(e); } }
        function rejected(value) { try { step(generator["throw"](value)); } catch (e) { reject(e); } }
        function step(result) { result.done ? resolve(result.value) : new P(function (resolve) { resolve(result.value); }).then(fulfilled, rejected); }
        step((generator = generator.apply(thisArg, _arguments || [])).next());
    });
};
var __generator = (this && this.__generator) || function (thisArg, body) {
    var _ = { label: 0, sent: function() { if (t[0] & 1) throw t[1]; return t[1]; }, trys: [], ops: [] }, f, y, t, g;
    return g = { next: verb(0), "throw": verb(1), "return": verb(2) }, typeof Symbol === "function" && (g[Symbol.iterator] = function() { return this; }), g;
    function verb(n) { return function (v) { return step([n, v]); }; }
    function step(op) {
        if (f) throw new TypeError("Generator is already executing.");
        while (_) try {
            if (f = 1, y && (t = y[op[0] & 2 ? "return" : op[0] ? "throw" : "next"]) && !(t = t.call(y, op[1])).done) return t;
            if (y = 0, t) op = [0, t.value];
            switch (op[0]) {
                case 0: case 1: t = op; break;
                case 4: _.label++; return { value: op[1], done: false };
                case 5: _.label++; y = op[1]; op = [0]; continue;
                case 7: op = _.ops.pop(); _.trys.pop(); continue;
                default:
                    if (!(t = _.trys, t = t.length > 0 && t[t.length - 1]) && (op[0] === 6 || op[0] === 2)) { _ = 0; continue; }
                    if (op[0] === 3 && (!t || (op[1] > t[0] && op[1] < t[3]))) { _.label = op[1]; break; }
                    if (op[0] === 6 && _.label < t[1]) { _.label = t[1]; t = op; break; }
                    if (t && _.label < t[2]) { _.label = t[2]; _.ops.push(op); break; }
                    if (t[2]) _.ops.pop();
                    _.trys.pop(); continue;
            }
            op = body.call(thisArg, _);
        } catch (e) { op = [6, e]; y = 0; } finally { f = t = 0; }
        if (op[0] & 5) throw op[1]; return { value: op[0] ? op[1] : void 0, done: true };
    }
};



var HeaderComponent = (function () {
    function HeaderComponent(sInfoService, userService) {
        this.sInfoService = sInfoService;
        this.userService = userService;
        this.serverInfo = null;
        this.serverInfo = new __WEBPACK_IMPORTED_MODULE_1__services_server_info_service__["a" /* ServerInfo */]("", "", new Date());
    }
    HeaderComponent.prototype.ngOnInit = function () {
        return __awaiter(this, void 0, void 0, function () {
            var _a;
            return __generator(this, function (_b) {
                switch (_b.label) {
                    case 0:
                        _a = this;
                        return [4 /*yield*/, this.sInfoService.get()];
                    case 1:
                        _a.serverInfo = _b.sent();
                        return [2 /*return*/];
                }
            });
        });
    };
    HeaderComponent.prototype.sInfo = function () {
        return this.serverInfo;
    };
    HeaderComponent.prototype.logged = function () {
        return this.userService.loggedIn();
    };
    HeaderComponent.prototype.user = function () {
        return this.userService.cuser();
    };
    HeaderComponent.prototype.logout = function () {
        return this.userService.logout();
    };
    return HeaderComponent;
}());
HeaderComponent = __decorate([
    Object(__WEBPACK_IMPORTED_MODULE_0__angular_core__["Component"])({
        selector: 'app-header',
        template: __webpack_require__("../../../../../src/app/header/header.component.html"),
        styles: [__webpack_require__("../../../../../src/app/header/header.component.css")]
    }),
    __metadata("design:paramtypes", [typeof (_a = typeof __WEBPACK_IMPORTED_MODULE_1__services_server_info_service__["b" /* ServerInfoService */] !== "undefined" && __WEBPACK_IMPORTED_MODULE_1__services_server_info_service__["b" /* ServerInfoService */]) === "function" && _a || Object, typeof (_b = typeof __WEBPACK_IMPORTED_MODULE_2__services_user_service__["a" /* UserService */] !== "undefined" && __WEBPACK_IMPORTED_MODULE_2__services_user_service__["a" /* UserService */]) === "function" && _b || Object])
], HeaderComponent);

var _a, _b;
//# sourceMappingURL=header.component.js.map

/***/ }),

/***/ "../../../../../src/app/home/home.component.css":
/***/ (function(module, exports, __webpack_require__) {

exports = module.exports = __webpack_require__("../../../../css-loader/lib/css-base.js")(false);
// imports


// module
exports.push([module.i, "", ""]);

// exports


/*** EXPORTS FROM exports-loader ***/
module.exports = module.exports.toString();

/***/ }),

/***/ "../../../../../src/app/home/home.component.html":
/***/ (function(module, exports) {

module.exports = "<div fxFlexFill class=\"back\" fxLayout=\"column\" fxLayoutAlign=\"start stretch\" >\n\t<div style=\"height: 60px;\"></div>\n\t<div fxFlex fxLayout=\"column\" fxLayoutAlign=\"center stretch\" style=\"padding: 50px;  width: 100%; padding: 50px;\" >\n\t\t<div class=\"card\" fxFlex=\"40\" >\n\t\t\t<div class=\"card-block\">\n\t\t\t\t<h4 class=\"card-title\">Welcome !</h4>\n\t\t\t\t<h6 class=\"card-subtitle mb-2 text-muted\">Introduction</h6>\n\t\t\t\t<p class=\"card-text\"></p>\n\t\t\t</div>\n\t\t</div>\n\t\t<div fxFlexOffset=\"1\" class=\"card\" fxFlex=\"25\" >\n\t\t\t<div class=\"card-block\">\n\t\t\t\t<h4 class=\"card-title\">Have a Question ?</h4>\n\t\t\t\t<h6 class=\"card-subtitle mb-2 text-muted\">Help</h6>\n\t\t\t\t<p class=\"card-text\"></p>\n\t\t\t</div>\n\t\t</div>\n\t\t<div fxFlexOffset=\"1\" class=\"card\" fxFlex=\"25\" >\n\t\t\t<div class=\"card-block\">\n\t\t\t\t<h4 class=\"card-title\">Disclaimer</h4>\n\t\t\t\t<p class=\"card-text\"></p>\n\t\t\t</div>\n\t\t</div>\n\t</div>\n</div>\n"

/***/ }),

/***/ "../../../../../src/app/home/home.component.ts":
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "a", function() { return HomeComponent; });
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_0__angular_core__ = __webpack_require__("../../../core/@angular/core.es5.js");
var __decorate = (this && this.__decorate) || function (decorators, target, key, desc) {
    var c = arguments.length, r = c < 3 ? target : desc === null ? desc = Object.getOwnPropertyDescriptor(target, key) : desc, d;
    if (typeof Reflect === "object" && typeof Reflect.decorate === "function") r = Reflect.decorate(decorators, target, key, desc);
    else for (var i = decorators.length - 1; i >= 0; i--) if (d = decorators[i]) r = (c < 3 ? d(r) : c > 3 ? d(target, key, r) : d(target, key)) || r;
    return c > 3 && r && Object.defineProperty(target, key, r), r;
};
var __metadata = (this && this.__metadata) || function (k, v) {
    if (typeof Reflect === "object" && typeof Reflect.metadata === "function") return Reflect.metadata(k, v);
};

var HomeComponent = (function () {
    function HomeComponent() {
    }
    HomeComponent.prototype.ngOnInit = function () {
        console.log("INIT");
    };
    return HomeComponent;
}());
HomeComponent = __decorate([
    Object(__WEBPACK_IMPORTED_MODULE_0__angular_core__["Component"])({
        selector: 'app-home',
        template: __webpack_require__("../../../../../src/app/home/home.component.html"),
        styles: [__webpack_require__("../../../../../src/app/home/home.component.css")]
    }),
    __metadata("design:paramtypes", [])
], HomeComponent);

//# sourceMappingURL=home.component.js.map

/***/ }),

/***/ "../../../../../src/app/job-creator/job-creator.component.css":
/***/ (function(module, exports, __webpack_require__) {

exports = module.exports = __webpack_require__("../../../../css-loader/lib/css-base.js")(false);
// imports


// module
exports.push([module.i, "", ""]);

// exports


/*** EXPORTS FROM exports-loader ***/
module.exports = module.exports.toString();

/***/ }),

/***/ "../../../../../src/app/job-creator/job-creator.component.html":
/***/ (function(module, exports) {

module.exports = "<div fxFlexFill class=\"back\" fxLayout=\"column\" fxLayoutAlign=\"start stretch\" *blockUI=\"'job-create'\">\n\t<div style=\"height: 60px;\"></div>\n\t<div fxFlex fxLayout=\"column\" fxLayoutAlign=\"center center\" style=\"width: 100%;\">\n\t\t<div fxLayout=\"column\" fxLayoutAlign=\"start stretch\" fxFlex=\"60\" class=\"card\" style=\"padding: 50px; width: 60%;\">\n\t\t\t<div class=\"card-block\"  fxFlex=\"90\" fxLayout=\"column\" fxLayoutAlign=\"start stretch\">\n\t\t\t\t<h4 class=\"card-title\" fxLayout=\"row\" fxLayoutAlign=\"space-between center\">\n\t\t\t\t\t<span> Create Job </span>\n\t\t\t\t\t<div fxLayout=\"row\" fxLayoutAlign=\"end center\">\n\t\t\t\t\t\t<button routerLink=\"../dashboard\" fxFlexOffset=\"2\" type=\"button\" class=\"btn btn-warning\">\n\t\t\t\t\t\t\t<span>Dashboard</span>\n\t\t\t\t\t\t</button>\n\t\t\t\t\t\t<button (click)=\"create()\" *ngIf=\"config() && filesValid()\" fxFlexOffset=\"2\" type=\"button\" class=\"btn btn-success\">\n\t\t\t\t\t\t\t<span>Create</span>\n\t\t\t\t\t\t</button>\n\t\t\t\t\t</div>\n\t\t\t\t</h4>\n\t\t\t\t<div fxLayout=\"row\" fxLayoutAlign=\"center stretch\" >\n\t\t\t\t\t<table class=\"table table-sm\" fxFlex=\"100\" style=\"height: 100%;\">\n\t\t\t\t\t\t<thead style=\"background-color: lightgrey;\">\n\t\t\t\t\t\t<tr>\n\t\t\t\t\t\t\t<th style=\"padding-left: 5px;\" fxLayout=\"row\" fxLayoutAlign=\"space-between center\">\n\t\t\t\t\t\t\t\t<span>Configuration</span>\n\t\t\t\t\t\t\t\t<i *ngIf=\"config()\" style=\"font-size: 1.5em; color: green;\" class=\"fa fa-check-circle\" aria-hidden=\"true\"></i>\n\t\t\t\t\t\t\t\t<i *ngIf=\"!config()\" style=\"font-size: 1.5em; color: red;\" class=\"fa fa-times-circle\" aria-hidden=\"true\"></i>\n\t\t\t\t\t\t\t</th>\n\t\t\t\t\t\t</tr>\n\t\t\t\t\t\t</thead>\n\t\t\t\t\t\t<tbody>\n\t\t\t\t\t\t<tr>\n\t\t\t\t\t\t\t<td fxLayout=\"column\" style=\"height: 100%; width : 100%; padding: 0 !important;\" fxLayoutAlign=\"start stretch\">\n\t\t\t\t\t\t\t\t<div fxLayout=\"row\" fxLayoutAlign=\"start center\">\n\t\t\t\t\t\t\t\t\t<span  fxFlex=\"50\" style=\"text-align: center;\">\n\t\t\t\t\t\t\t\t\t\t<strong>Name</strong>\n\t\t\t\t\t\t\t\t\t</span>\n\t\t\t\t\t\t\t\t\t<input fxFlex=\"50\" required minlength=\"4\" [(ngModel)]=\"name\"  class=\"form-control\" placeholder=\"Job Name\" type=\"text\">\n\t\t\t\t\t\t\t\t</div>\n\t\t\t\t\t\t\t</td>\n\t\t\t\t\t\t</tr>\n\t\t\t\t\t\t<tr>\n\t\t\t\t\t\t\t<td fxLayout=\"column\" style=\"height: 100%; width : 100%; padding: 0 !important;\" fxLayoutAlign=\"start stretch\">\n\t\t\t\t\t\t\t\t<div fxLayout=\"row\" fxLayoutAlign=\"start center\">\n\t\t\t\t\t\t\t\t\t<span  fxFlex=\"50\" style=\"text-align: center;\">\n\t\t\t\t\t\t\t\t\t\t<strong>Configuation</strong>\n\t\t\t\t\t\t\t\t\t</span>\n\t\t\t\t\t\t\t\t\t<div fxFlex=\"50\"  fxLayout=\"column\" fxLayoutAlign=\"center center\" class=\"btn-group\">\n\t\t\t\t\t\t\t\t\t\t<button style=\"width: 100%; text-align: center;\" type=\"button\" class=\"btn btn-primary\" fxLayout=\"row\" fxLayoutAlign=\"space-between center\" (click)=\"toggleConfig()\">\n\t\t\t\t\t\t\t\t\t\t\t<span>Configurations</span>\n\t\t\t\t\t\t\t\t\t\t\t<span  *ngIf=\"configuration\" class=\"badge badge-default\"><i style=\"color: black;\" class=\"fa fa-cog\"></i> {{configuration?.name}}</span>\n\t\t\t\t\t\t\t\t\t\t\t<i *ngIf=\"confToggle\" style=\"color: black; font-size: 1.2em;\" class=\"fa fa-chevron-up\"></i>\n\t\t\t\t\t\t\t\t\t\t\t<i *ngIf=\"!confToggle\" style=\"color: black; font-size: 1.2em;\" class=\"fa fa-chevron-down\"></i>\n\t\t\t\t\t\t\t\t\t\t</button>\n\t\t\t\t\t\t\t\t\t</div>\n\t\t\t\t\t\t\t\t</div>\n\t\t\t\t\t\t\t</td>\n\t\t\t\t\t\t</tr>\n\t\t\t\t\t\t<tr *ngIf=\"confToggle\">\n\t\t\t\t\t\t\t<td fxLayout=\"column\" style=\"height: 100%; width : 100%; padding: 0 !important;\" fxLayoutAlign=\"start stretch\">\n\t\t\t\t\t\t\t\t<div fxLayout=\"row\" fxLayoutAlign=\"end center\">\n\t\t\t\t\t\t\t\t\t<div fxFlex=\"50\"  fxLayout=\"column\" fxLayoutAlign=\"center center\" class=\"btn-group\">\n\t\t\t\t\t\t\t\t\t\t<div class=\"list-group\" style=\"width: 100%;\">\n\t\t\t\t\t\t\t\t\t\t\t<a *ngFor=\"let conf of configurations\" (click)=\"selectConfig(conf)\" class=\"list-group-item list-group-item-action\" fxLayout=\"column\" fxLayoutAlign=\"center stretch\">\n\t\t\t\t\t\t\t\t\t\t\t\t{{conf?.name}}\n\t\t\t\t\t\t\t\t\t\t\t</a>\n\t\t\t\t\t\t\t\t\t\t</div>\n\t\t\t\t\t\t\t\t\t</div>\n\t\t\t\t\t\t\t\t</div>\n\t\t\t\t\t\t\t</td>\n\t\t\t\t\t\t</tr>\n\t\t\t\t\t\t</tbody>\n\t\t\t\t\t</table>\n\t\t\t\t</div>\n\t\t\t\t<div fxLayout=\"row\" fxLayoutAlign=\"center stretch\" fxFlex=\"60\" >\n\t\t\t\t\t<table class=\"table table-sm\" fxFlex=\"100\" style=\"height: 100%;\">\n\t\t\t\t\t\t<thead style=\"background-color: lightgrey;\">\n\t\t\t\t\t\t\t<tr>\n\t\t\t\t\t\t\t\t<th style=\"padding-left: 5px;\"  fxLayout=\"row\" fxLayoutAlign=\"space-between center\">\n\t\t\t\t\t\t\t\t\t<span>Files</span>\n\t\t\t\t\t\t\t\t\t<i *ngIf=\"filesValid()\" style=\"font-size: 1.5em; color: green;\" class=\"fa fa-check-circle\" aria-hidden=\"true\"></i>\n\t\t\t\t\t\t\t\t\t<i *ngIf=\"!filesValid()\" style=\"font-size: 1.5em; color: red;\" class=\"fa fa-times-circle\" aria-hidden=\"true\"></i>\n\t\t\t\t\t\t\t\t</th>\n\t\t\t\t\t\t\t</tr>\n\t\t\t\t\t\t</thead>\n\t\t\t\t\t\t<tbody>\n\t\t\t\t\t\t\t<tr>\n\t\t\t\t\t\t\t\t<td fxLayout=\"row\" style=\"height: 100%; padding: 0 !important;\" fxLayoutAlign=\"start stretch\">\n\t\t\t\t\t\t\t\t\t<app-file-component (file)=\"patientsUpdate($event)\" fxFlex=\"50\" label=\"Patient Records File\"></app-file-component>\n\t\t\t\t\t\t\t\t\t<app-file-component (file)=\"vaccinesUpdate($event)\" fxFlex=\"50\" label=\"Vaccination Records File\"></app-file-component>\n\t\t\t\t\t\t\t\t</td>\n\t\t\t\t\t\t\t</tr>\n\t\t\t\t\t\t</tbody>\n\t\t\t\t\t</table>\n\t\t\t\t</div>\n\t\t\t</div>\n\t\t</div>\n\t</div>\n\n</div>\n"

/***/ }),

/***/ "../../../../../src/app/job-creator/job-creator.component.ts":
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "a", function() { return JobCreatorComponent; });
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_0__angular_core__ = __webpack_require__("../../../core/@angular/core.es5.js");
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_1__angular_http__ = __webpack_require__("../../../http/@angular/http.es5.js");
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_2__angular_router__ = __webpack_require__("../../../router/@angular/router.es5.js");
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_3_ng2_toasty__ = __webpack_require__("../../../../ng2-toasty/index.js");
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_4_ng_block_ui__ = __webpack_require__("../../../../ng-block-ui/dist/index.js");
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_4_ng_block_ui___default = __webpack_require__.n(__WEBPACK_IMPORTED_MODULE_4_ng_block_ui__);
var __decorate = (this && this.__decorate) || function (decorators, target, key, desc) {
    var c = arguments.length, r = c < 3 ? target : desc === null ? desc = Object.getOwnPropertyDescriptor(target, key) : desc, d;
    if (typeof Reflect === "object" && typeof Reflect.decorate === "function") r = Reflect.decorate(decorators, target, key, desc);
    else for (var i = decorators.length - 1; i >= 0; i--) if (d = decorators[i]) r = (c < 3 ? d(r) : c > 3 ? d(target, key, r) : d(target, key)) || r;
    return c > 3 && r && Object.defineProperty(target, key, r), r;
};
var __metadata = (this && this.__metadata) || function (k, v) {
    if (typeof Reflect === "object" && typeof Reflect.metadata === "function") return Reflect.metadata(k, v);
};





var JobCreatorComponent = (function () {
    function JobCreatorComponent(http, router, toastyService, toastyConfig) {
        this.http = http;
        this.router = router;
        this.toastyService = toastyService;
        this.toastyConfig = toastyConfig;
        this.toastyConfig.theme = 'material';
    }
    JobCreatorComponent.prototype.ngOnInit = function () {
        this.configurations = [
            {
                id: "342ae98fcd",
                name: "default"
            }
        ];
        this.confToggle = false;
    };
    JobCreatorComponent.prototype.toggleConfig = function () {
        this.confToggle = !this.confToggle;
    };
    JobCreatorComponent.prototype.filesValid = function () {
        if (this.patients && this.vaccines)
            return true;
        else
            return false;
    };
    JobCreatorComponent.prototype.config = function () {
        if (this.name && this.configuration)
            return true;
        else
            return false;
    };
    JobCreatorComponent.prototype.create = function () {
        var ctrl = this;
        var form = new FormData();
        form.append('name', this.name);
        form.append('configuration', JSON.stringify(this.configuration));
        form.append('patient', this.patients);
        form.append('vaccination', this.vaccines);
        this.blockUIList.start('Loading...'); // Start blocking element only
        //
        return this.http.post('api/jobs/create', form).toPromise().then(function (x) {
            ctrl.blockUIList.stop();
            if (x && x.status) {
                ctrl.router.navigate(["/jobs", "dashboard"]).then(function (x) {
                    ctrl.growl("Job Added Successfully");
                });
            }
            else {
                ctrl.router.navigate(["/jobs", "dashboard"]).then(function (x) {
                    ctrl.growl("Failed to create job");
                });
            }
        }, function (x) {
            ctrl.blockUIList.stop();
            ctrl.router.navigate(["/jobs", "dashboard"]).then(function (x) {
                ctrl.growl("Failed to create job");
            });
        });
    };
    JobCreatorComponent.prototype.growl = function (message) {
        this.toastyService.info(message);
    };
    JobCreatorComponent.prototype.patientsUpdate = function (file) {
        this.patients = file;
    };
    JobCreatorComponent.prototype.vaccinesUpdate = function (file) {
        this.vaccines = file;
    };
    JobCreatorComponent.prototype.selectConfig = function (conf) {
        this.configuration = conf;
        this.toggleConfig();
    };
    return JobCreatorComponent;
}());
__decorate([
    Object(__WEBPACK_IMPORTED_MODULE_4_ng_block_ui__["BlockUI"])('job-create'),
    __metadata("design:type", typeof (_a = typeof __WEBPACK_IMPORTED_MODULE_4_ng_block_ui__["NgBlockUI"] !== "undefined" && __WEBPACK_IMPORTED_MODULE_4_ng_block_ui__["NgBlockUI"]) === "function" && _a || Object)
], JobCreatorComponent.prototype, "blockUIList", void 0);
JobCreatorComponent = __decorate([
    Object(__WEBPACK_IMPORTED_MODULE_0__angular_core__["Component"])({
        selector: 'app-job-creator',
        template: __webpack_require__("../../../../../src/app/job-creator/job-creator.component.html"),
        styles: [__webpack_require__("../../../../../src/app/job-creator/job-creator.component.css")]
    }),
    __metadata("design:paramtypes", [typeof (_b = typeof __WEBPACK_IMPORTED_MODULE_1__angular_http__["a" /* Http */] !== "undefined" && __WEBPACK_IMPORTED_MODULE_1__angular_http__["a" /* Http */]) === "function" && _b || Object, typeof (_c = typeof __WEBPACK_IMPORTED_MODULE_2__angular_router__["b" /* Router */] !== "undefined" && __WEBPACK_IMPORTED_MODULE_2__angular_router__["b" /* Router */]) === "function" && _c || Object, typeof (_d = typeof __WEBPACK_IMPORTED_MODULE_3_ng2_toasty__["c" /* ToastyService */] !== "undefined" && __WEBPACK_IMPORTED_MODULE_3_ng2_toasty__["c" /* ToastyService */]) === "function" && _d || Object, typeof (_e = typeof __WEBPACK_IMPORTED_MODULE_3_ng2_toasty__["a" /* ToastyConfig */] !== "undefined" && __WEBPACK_IMPORTED_MODULE_3_ng2_toasty__["a" /* ToastyConfig */]) === "function" && _e || Object])
], JobCreatorComponent);

var _a, _b, _c, _d, _e;
//# sourceMappingURL=job-creator.component.js.map

/***/ }),

/***/ "../../../../../src/app/job-result/job-result.component.css":
/***/ (function(module, exports, __webpack_require__) {

exports = module.exports = __webpack_require__("../../../../css-loader/lib/css-base.js")(false);
// imports


// module
exports.push([module.i, "", ""]);

// exports


/*** EXPORTS FROM exports-loader ***/
module.exports = module.exports.toString();

/***/ }),

/***/ "../../../../../src/app/job-result/job-result.component.html":
/***/ (function(module, exports) {

module.exports = "<div fxFlexFill class=\"back\" fxLayout=\"column\" fxLayoutAlign=\"start stretch\">\n\t<div style=\"height: 60px;\"></div>\n\t<div fxFlex fxLayout=\"column\" fxLayoutAlign=\"start stretch\" style=\"padding: 20px;  width: 100%; padding: 50px;\">\n\t\t<div fxLayout=\"column\" fxLayoutAlign=\"start stretch\" class=\"card\" style=\"padding: 50px; height: 90%;\">\n\t\t\t<div class=\"card-block\"  fxFlex=\"90\" fxLayout=\"column\" fxLayoutAlign=\"start stretch\">\n\t\t\t\t<h4 class=\"card-title\" fxLayout=\"row\" fxLayoutAlign=\"space-between center\">\n\t\t\t\t\t<span> Analysis Results </span>\n\t\t\t\t\t<div fxLayout=\"row\" fxLayoutAlign=\"end center\">\n\t\t\t\t\t\t<button routerLink=\"../../dashboard\" fxFlexOffset=\"2\" type=\"button\" class=\"btn btn-warning\">\n\t\t\t\t\t\t\t<span>Dashboard</span>\n\t\t\t\t\t\t</button>\n\t\t\t\t\t</div>\n\t\t\t\t</h4>\n\t\t\t\t<div fxFlex fxLayout=\"row\" fxLayoutAlign=\"start stretch\" >\n\t\t\t\t\t<div fxFlex=\"20\" fxLayout=\"column\" style=\"margin-top: 20px;\" fxLayoutAlign=\"start stretch\">\n\t\t\t\t\t\t<p-tree [(selection)]=\"selected\"  (onNodeSelect)=\"nodeSelect($event)\" selectionMode=\"single\"[value]=\"tree\" [styleClass]=\"'ftree'\" fxFlex=\"100\"></p-tree>\n\t\t\t\t\t</div>\n\t\t\t\t\t<div fxFlexOffset=\"2\" fxFlex=\"78\" fxLayout=\"column\" style=\"margin-top: 20px; height: 100%; background-color: lightgrey;\" fxLayoutAlign=\"start start\">\n\t\t\t\t\t\t<ul class=\"nav nav-pills nav-fill\" style=\"width: 100%;\">\n\t\t\t\t\t\t\t<li class=\"nav-item\">\n\t\t\t\t\t\t\t\t<a  [class]=\"tab === 'S' ? 'nav-link active' : 'nav-link'\" (click)=\"tab === 'S';\" >Statistics</a>\n\t\t\t\t\t\t\t</li>\n\t\t\t\t\t\t\t<li class=\"nav-item\">\n\t\t\t\t\t\t\t\t<a [class]=\"tab === 'D' ? 'nav-link active' : 'nav-link'\" (click)=\"tab === 'D';\"  >Detections</a>\n\t\t\t\t\t\t\t</li>\n\t\t\t\t\t\t</ul>\n\t\t\t\t\t\t<div style=\"text-align : center; background-color : white; border : 1px solid black; width: 100%;\" *ngIf=\"selected\">\n\t\t\t\t\t\t\t<span style=\"font-weight: bold; font-size: 18px;\">{{selected.data.fieldOfInterest}}</span>\n\t\t\t\t\t\t</div>\n\t\t\t\t\t\t<div fxLayout=\"column\" fxLayoutAlign=\"center center\" fxFlex style=\"width: 100%;\" *ngIf=\"selected && tab === 'S'\" >\n\n\t\t\t\t\t\t\t<div fxLayout=\"column\" fxLayoutAlign=\"start start\" style=\"width: 70%;\">\n\t\t\t\t\t\t\t\t<div style=\"width: 100%; height: 10px;\" fxLayout=\"row\" fxLayoutAlign=\"start center\">\n\t\t\t\t\t\t\t\t\t<div style=\"width: 20%; text-align: center;\">Base Line</div>\n\t\t\t\t\t\t\t\t\t<div style=\"width: 60%; height: 100%; background-color: black;\"></div>\n\t\t\t\t\t\t\t\t\t<div style=\"width: 20%; text-align: center;\">100% (out of {{result.totalRecords}})</div>\n\t\t\t\t\t\t\t\t</div>\n\t\t\t\t\t\t\t\t<div fxFlexOffset=\"1\" *ngFor=\"let stat of selected.data.statistics\" style=\"width: 100%; height: 10px;\" fxLayout=\"row\" fxLayoutAlign=\"start center\">\n\t\t\t\t\t\t\t\t\t<div style=\"width: 20%; text-align: center;\">{{stat.statisticId}}</div>\n\t\t\t\t\t\t\t\t\t<div style=\"width: 60%; height: 100%;\" fxLayout=\"row\" fxLayout=\"start stretch\">\n\t\t\t\t\t\t\t\t\t\t<div  style=\"background-color: blue;\" [fxFlex]=\"stat.count === 0 ? 0 :  (stat.count / stat.total) * 100\"></div>\n\t\t\t\t\t\t\t\t\t</div>\n\t\t\t\t\t\t\t\t\t<div style=\"width: 20%; text-align: center;\">{{(stat.count === 0 ? 0 :  (stat.count / stat.total) * 100) | number:'.1' }}% ({{stat.count}})</div>\n\t\t\t\t\t\t\t\t</div>\n\t\t\t\t\t\t\t</div>\n\n\t\t\t\t\t\t\t<!--<span style=\"color: lightgrey; font-weight: bold;\" *ngIf=\"!selected.data.statistics\"> No Statistic </span>-->\n\t\t\t\t\t\t\t<!--<div *ngFor=\"let stat of selected.data.statistics\" class=\"progress\" fxLayout=\"column\"-->\n\t\t\t\t\t\t\t\t <!--fxLayoutAlign=\"center start\"-->\n\t\t\t\t\t\t\t\t <!--style=\"position:relative; background-color: lightgrey; width: 80%; height: 30px; text-align: center; margin: auto;\">-->\n\t\t\t\t\t\t\t\t<!--<div style=\"height: 30px;\"-->\n\t\t\t\t\t\t\t\t\t <!--class=\"progress-bar progress-bar-striped progress-bar-animated\" role=\"progressbar\"-->\n\t\t\t\t\t\t\t\t\t <!--[ngStyle]=\"{ 'width' : (stat.count === 0 ? 0 :  stat.count / stat.total * 100) +'%' }\">-->\n\t\t\t\t\t\t\t\t<!--</div>-->\n\t\t\t\t\t\t\t\t<!--<span style=\"color : black; font-weight: bold; width : 100%; text-align : center; font-size : 15px; position: absolute;\">{{stat.statisticId}} {{(stat.count === 0 ? 0 :  stat.count / stat.total * 100) | number:'.1' }}%</span>-->\n\t\t\t\t\t\t\t<!--</div>-->\n\t\t\t\t\t\t</div>\n\t\t\t\t\t\t<div fxFlex style=\"width: 100%;\" *ngIf=\"selected && tab === 'D'\">\n\n\t\t\t\t\t\t</div>\n\t\t\t\t\t</div>\n\n\t\t\t\t<!--<table class=\"table table-sm table-striped\" style=\"width: 100%;\">-->\n\t\t\t\t\t<!--<thead>-->\n\t\t\t\t\t\t<!--<tr>-->\n\t\t\t\t\t\t\t<!--<th style=\"text-align:  center; width: 200px;\"> Field ID </th>-->\n\t\t\t\t\t\t\t<!--<th style=\"text-align:  center\"> Statistics </th>-->\n\t\t\t\t\t\t\t<!--<th style=\"text-align:  center\"> Detections </th>-->\n\t\t\t\t\t\t<!--</tr>-->\n\t\t\t\t\t<!--</thead>-->\n\t\t\t\t\t<!--<tbody>-->\n\t\t\t\t\t\t<!--<tr *ngFor=\"let subject of result.fields\">-->\n\t\t\t\t\t\t\t<!--<td style=\"vertical-align: middle;\">{{subject.fieldOfInterest}}</td>-->\n\t\t\t\t\t\t\t<!--<td fxLayout=\"column\" fxLayoutAlign=\"center center\">-->\n\t\t\t\t\t\t\t\t<!--<span style=\"color: lightgrey; font-weight: bold;\" *ngIf=\"!subject.statistics\"> No Statistic </span>-->\n\t\t\t\t\t\t\t\t<!--<div *ngFor=\"let stat of subject.statistics\" class=\"progress\" fxLayout=\"column\" fxLayoutAlign=\"center start\" style=\"position:relative; background-color: lightgrey; width: 80%; height: 30px; text-align: center; margin: auto;\" >-->\n\t\t\t\t\t\t\t\t\t<!--<div style=\"height: 30px;\" class=\"progress-bar progress-bar-striped progress-bar-animated\" role=\"progressbar\" [ngStyle]=\"{ 'width' : (stat.count === 0 ? 0 :  stat.count / stat.total * 100) +'%' }\">-->\n\t\t\t\t\t\t\t\t\t<!--</div>-->\n\t\t\t\t\t\t\t\t\t<!--<span style=\"color : black; font-weight: bold; width : 100%; text-align : center; font-size : 15px; position: absolute;\">{{stat.statisticId}} {{(stat.count === 0 ? 0 :  stat.count / stat.total * 100) | number:'.1' }}%</span>-->\n\t\t\t\t\t\t\t\t<!--</div>-->\n\t\t\t\t\t\t\t<!--</td>-->\n\t\t\t\t\t\t\t<!--<td style=\"text-align: center\">-->\n\t\t\t\t\t\t\t\t<!--<span style=\"color: lightgrey; font-weight: bold;\" *ngIf=\"!subject.detections\"> No Detection </span>-->\n\t\t\t\t\t\t\t<!--</td>-->\n\t\t\t\t\t\t<!--</tr>-->\n\t\t\t\t\t<!--</tbody>-->\n\t\t\t\t<!--</table>-->\n\t\t\t\t</div>\n\t\t\t</div>\n\t\t</div>\n\t</div>\n</div>\n"

/***/ }),

/***/ "../../../../../src/app/job-result/job-result.component.ts":
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "a", function() { return JobResultComponent; });
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_0__angular_core__ = __webpack_require__("../../../core/@angular/core.es5.js");
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_1__angular_router__ = __webpack_require__("../../../router/@angular/router.es5.js");
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_2__services_jobs_service__ = __webpack_require__("../../../../../src/app/services/jobs.service.ts");
var __decorate = (this && this.__decorate) || function (decorators, target, key, desc) {
    var c = arguments.length, r = c < 3 ? target : desc === null ? desc = Object.getOwnPropertyDescriptor(target, key) : desc, d;
    if (typeof Reflect === "object" && typeof Reflect.decorate === "function") r = Reflect.decorate(decorators, target, key, desc);
    else for (var i = decorators.length - 1; i >= 0; i--) if (d = decorators[i]) r = (c < 3 ? d(r) : c > 3 ? d(target, key, r) : d(target, key)) || r;
    return c > 3 && r && Object.defineProperty(target, key, r), r;
};
var __metadata = (this && this.__metadata) || function (k, v) {
    if (typeof Reflect === "object" && typeof Reflect.metadata === "function") return Reflect.metadata(k, v);
};
var __awaiter = (this && this.__awaiter) || function (thisArg, _arguments, P, generator) {
    return new (P || (P = Promise))(function (resolve, reject) {
        function fulfilled(value) { try { step(generator.next(value)); } catch (e) { reject(e); } }
        function rejected(value) { try { step(generator["throw"](value)); } catch (e) { reject(e); } }
        function step(result) { result.done ? resolve(result.value) : new P(function (resolve) { resolve(result.value); }).then(fulfilled, rejected); }
        step((generator = generator.apply(thisArg, _arguments || [])).next());
    });
};
var __generator = (this && this.__generator) || function (thisArg, body) {
    var _ = { label: 0, sent: function() { if (t[0] & 1) throw t[1]; return t[1]; }, trys: [], ops: [] }, f, y, t, g;
    return g = { next: verb(0), "throw": verb(1), "return": verb(2) }, typeof Symbol === "function" && (g[Symbol.iterator] = function() { return this; }), g;
    function verb(n) { return function (v) { return step([n, v]); }; }
    function step(op) {
        if (f) throw new TypeError("Generator is already executing.");
        while (_) try {
            if (f = 1, y && (t = y[op[0] & 2 ? "return" : op[0] ? "throw" : "next"]) && !(t = t.call(y, op[1])).done) return t;
            if (y = 0, t) op = [0, t.value];
            switch (op[0]) {
                case 0: case 1: t = op; break;
                case 4: _.label++; return { value: op[1], done: false };
                case 5: _.label++; y = op[1]; op = [0]; continue;
                case 7: op = _.ops.pop(); _.trys.pop(); continue;
                default:
                    if (!(t = _.trys, t = t.length > 0 && t[t.length - 1]) && (op[0] === 6 || op[0] === 2)) { _ = 0; continue; }
                    if (op[0] === 3 && (!t || (op[1] > t[0] && op[1] < t[3]))) { _.label = op[1]; break; }
                    if (op[0] === 6 && _.label < t[1]) { _.label = t[1]; t = op; break; }
                    if (t && _.label < t[2]) { _.label = t[2]; _.ops.push(op); break; }
                    if (t[2]) _.ops.pop();
                    _.trys.pop(); continue;
            }
            op = body.call(thisArg, _);
        } catch (e) { op = [6, e]; y = 0; } finally { f = t = 0; }
        if (op[0] & 5) throw op[1]; return { value: op[0] ? op[1] : void 0, done: true };
    }
};



var JobResultComponent = (function () {
    function JobResultComponent(route, $jobs) {
        this.route = route;
        this.$jobs = $jobs;
        this.tab = 'S';
    }
    JobResultComponent.prototype.nodeSelect = function ($event) {
        console.log(this.selected);
    };
    JobResultComponent.prototype.ngOnInit = function () {
        return __awaiter(this, void 0, void 0, function () {
            var _this = this;
            var ctrl, jobId;
            return __generator(this, function (_a) {
                ctrl = this;
                ctrl.tree = [];
                ctrl.result = null;
                ctrl.route.params.subscribe(function (params) {
                    jobId = params['id'];
                    _this.$jobs.getResult(jobId).then(function (x) {
                        ctrl.result = x;
                        console.log(x);
                        ctrl.tree = ctrl.$jobs.fieldsTree(x);
                        console.log(ctrl.tree);
                    });
                });
                return [2 /*return*/];
            });
        });
    };
    return JobResultComponent;
}());
JobResultComponent = __decorate([
    Object(__WEBPACK_IMPORTED_MODULE_0__angular_core__["Component"])({
        selector: 'app-job-result',
        template: __webpack_require__("../../../../../src/app/job-result/job-result.component.html"),
        styles: [__webpack_require__("../../../../../src/app/job-result/job-result.component.css")]
    }),
    __metadata("design:paramtypes", [typeof (_a = typeof __WEBPACK_IMPORTED_MODULE_1__angular_router__["a" /* ActivatedRoute */] !== "undefined" && __WEBPACK_IMPORTED_MODULE_1__angular_router__["a" /* ActivatedRoute */]) === "function" && _a || Object, typeof (_b = typeof __WEBPACK_IMPORTED_MODULE_2__services_jobs_service__["a" /* JobsService */] !== "undefined" && __WEBPACK_IMPORTED_MODULE_2__services_jobs_service__["a" /* JobsService */]) === "function" && _b || Object])
], JobResultComponent);

var _a, _b;
//# sourceMappingURL=job-result.component.js.map

/***/ }),

/***/ "../../../../../src/app/jobs/jobs.component.css":
/***/ (function(module, exports, __webpack_require__) {

exports = module.exports = __webpack_require__("../../../../css-loader/lib/css-base.js")(false);
// imports


// module
exports.push([module.i, "", ""]);

// exports


/*** EXPORTS FROM exports-loader ***/
module.exports = module.exports.toString();

/***/ }),

/***/ "../../../../../src/app/jobs/jobs.component.html":
/***/ (function(module, exports) {

module.exports = "<div fxFlexFill class=\"back\" fxLayout=\"column\" fxLayoutAlign=\"start stretch\">\n\t<div style=\"height: 60px;\"></div>\n\t<div fxFlex fxLayout=\"column\" fxLayoutAlign=\"start stretch\" style=\"padding: 50px;  width: 100%; padding: 50px;\">\n\t\t<div class=\"card\" style=\"padding: 50px;\">\n\t\t\t<div class=\"card-block\" fxLayout=\"column\" fxLayoutAlign=\"start stretch\">\n\t\t\t\t<h4 class=\"card-title\" fxLayout=\"row\" fxLayoutAlign=\"space-between center\">\n\t\t\t\t\t<span> My Jobs </span>\n\t\t\t\t\t<div fxLayout=\"row\" fxLayoutAlign=\"end center\">\n\t\t\t\t\t\t<div class=\"btn-group\" dropdown>\n\t\t\t\t\t\t\t<button dropdownToggle type=\"button\" class=\"btn btn-info dropdown-toggle\">\n\t\t\t\t\t\t\t\t<i class=\"fa fa-refresh\" aria-hidden=\"true\"></i> {{current == -1 ? \"Disabled\" : current+\" s\"}} <span class=\"caret\"></span>\n\t\t\t\t\t\t\t</button>\n\t\t\t\t\t\t\t<ul *dropdownMenu class=\"dropdown-menu\" role=\"menu\">\n\t\t\t\t\t\t\t\t<li *ngFor=\"let rate of rates\" role=\"menuitem\" style=\"cursor: pointer;\"><a\n\t\t\t\t\t\t\t\t\tclass=\"dropdown-item\" (click)=\"setRefreshRate(rate)\">{{rate == -1 ? \"Disable\" : rate+\" seconds\"}}</a></li>\n\t\t\t\t\t\t\t</ul>\n\t\t\t\t\t\t</div>\n\t\t\t\t\t\t<button routerLink=\"../create\" fxFlexOffset=\"2\" type=\"button\" class=\"btn btn-primary\">\n\t\t\t\t\t\t\t<i class=\"fa fa-plus\" aria-hidden=\"true\"></i>\n\t\t\t\t\t\t\t<span>New Job</span>\n\t\t\t\t\t\t</button>\n\t\t\t\t\t</div>\n\t\t\t\t</h4>\n\t\t\t\t<div fxFlex=\"90\" style=\"overflow-y: scroll;\" class=\"list-group\">\n\t\t\t\t\t<span *ngIf=\"jobs.length === 0\" style=\"width: 100%; font-size: 18px;  text-align : center; font-weight: bold; color: darkgrey;\">YOU HAVE NO ANALYSIS JOB - CREATE ONE BY CLICKING ON [+ NEW JOB] BUTTON</span>\n\t\t\t\t\t<div *ngFor=\"let job of jobs; let i = index\" fxLayout=\"column\" fxLayoutAlign=\"center stretch\">\n\t\t\t\t\t\t<a class=\"list-group-item list-group-item-action\" fxLayout=\"row\" fxLayoutAlign=\"start center\">\n\t\t\t\t\t\t\t<div fxFlex=\"10\" style=\"text-align: center;\">\n\t\t\t\t\t\t\t\t<span>{{job.name}}</span>\n\t\t\t\t\t\t\t</div>\n\t\t\t\t\t\t\t<div  fxFlex=\"10\" style=\"text-align: center;\">\n\t\t\t\t\t\t\t\t<a [routerLink]=\"'/configuration/'+job.configuration.id\"><i class=\"fa fa-cog\" aria-hidden=\"true\"></i> {{job.configuration.name}}</a>\n\t\t\t\t\t\t\t</div>\n\t\t\t\t\t\t\t<div fxFlex=\"10\" style=\"text-align: center;\">\n\t\t\t\t\t\t\t\t<span class=\"badge\" [ngStyle]=\"{'background-color' :\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\tjob.running() ? 'blue' :\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\tjob.finished() ? 'green' :\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\tjob.queued() ? 'gold' :\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\tjob.failed() ? 'red' :\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\tjob.stopped() ? 'red' :\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t'gray'}\">{{job.status}}</span>\n\t\t\t\t\t\t\t</div>\n\t\t\t\t\t\t\t<div fxFlex.gt-lg=\"35\" fxFlex.gt-md=\"30\" fxLayout=\"column\" fxLayoutAlign=\"center center\">\n\t\t\t\t\t\t\t\t<div class=\"progress\" fxLayout=\"column\" fxLayoutAlign=\"center start\" style=\"position:relative; background-color: lightgrey; width: 80%; height: 30px; text-align: center; margin: auto;\" >\n\t\t\t\t\t\t\t\t\t<div style=\"height: 30px;\" class=\"progress-bar progress-bar-striped progress-bar-animated\" role=\"progressbar\" [ngStyle]=\"{ 'width' : (job.progress*100) +'%' }\" [attr.aria-valuenow]=\"(job.progress * 100)\" aria-valuemin=\"0\" aria-valuemax=\"100\">\n\t\t\t\t\t\t\t\t\t</div>\n\t\t\t\t\t\t\t\t\t<span style=\"color : white; font-weight: bold; width : 100%; text-align : center; font-size : 15px; position: absolute;\">{{(job.progress * 100)+\"%\"}}</span>\n\t\t\t\t\t\t\t\t</div>\n\t\t\t\t\t\t\t</div>\n\t\t\t\t\t\t\t<div fxFlex.gt-lg=\"30\" fxLayout=\"row\" fxLayoutAlign=\"center center\">\n\t\t\t\t\t\t\t\t<div fxFlex.gt-lg=\"33\" *ngIf=\"job.dateCreated\" style=\"vertical-align: middle;\">\n\t\t\t\t\t\t\t\t\t<strong>Submited On</strong><br>\n\t\t\t\t\t\t\t\t\t{{job.dateCreated | date:'yyyy-MM-dd HH:mm:ss'}}\n\t\t\t\t\t\t\t\t</div>\n\t\t\t\t\t\t\t\t<div fxFlex.gt-lg=\"33\" *ngIf=\"job.dateStarted\"  style=\"vertical-align: middle;\">\n\t\t\t\t\t\t\t\t\t<strong>Started On</strong><br>\n\t\t\t\t\t\t\t\t\t{{job.dateStarted | date:'yyyy-MM-dd HH:mm:ss'}}\n\t\t\t\t\t\t\t\t</div>\n\t\t\t\t\t\t\t\t<div fxFlex.gt-lg=\"33\" *ngIf=\"job.dateEnded\"  style=\"vertical-align: middle;\">\n\t\t\t\t\t\t\t\t\t<strong>Ended On</strong><br>\n\t\t\t\t\t\t\t\t\t{{job.dateEnded | date:'yyyy-MM-dd HH:mm:ss'}}\n\t\t\t\t\t\t\t\t</div>\n\t\t\t\t\t\t\t</div>\n\t\t\t\t\t\t\t<div fxFlex.gt-lg=\"5\" fxFlex.gt-md=\"3\" style=\"vertical-align: middle; height: 100%;\" fxLayout=\"row\" fxLayoutAlign=\"center center\">\n\t\t\t\t\t\t\t\t<div fxLayout=\"column\" fxLayoutAlign=\"center stretch\" fxFlex=\"50\">\n\t\t\t\t\t\t\t\t\t<button *ngIf=\"!job.running()\" fxFlex=\"47\" class=\"btn btn-sm btn-danger\"><i class=\"fa fa-trash\" aria-hidden=\"true\"></i></button>\n\t\t\t\t\t\t\t\t\t<button *ngIf=\"job.finished()\"  fxFlexOffset=\"5\" fxFlex=\"48\" class=\"btn btn-sm btn-primary\" [routerLink]=\"['../analysis',job.id]\"><i class=\"fa fa-bar-chart\" aria-hidden=\"true\"></i></button>\n\t\t\t\t\t\t\t\t</div>\n\t\t\t\t\t\t\t\t<div *ngIf=\"job.running()\"  fxLayout=\"column\" fxLayoutAlign=\"center stretch\" fxFlexOffset=\"5\" fxFlex=\"50\">\n\t\t\t\t\t\t\t\t\t<button fxFlex=\"47\" class=\"btn btn-sm btn-danger\"><i class=\"fa fa-stop\" aria-hidden=\"true\"></i></button>\n\t\t\t\t\t\t\t\t\t<button fxFlexOffset=\"5\" fxFlex=\"48\" (click)=\"toggle(i)\" class=\"btn btn-sm btn-primary\"><i class=\"fa fa-plus\" aria-hidden=\"true\"></i></button>\n\t\t\t\t\t\t\t\t</div>\n\t\t\t\t\t\t\t</div>\n\t\t\t\t\t\t</a>\n\t\t\t\t\t\t<div *ngIf=\"toggled(i)\" class=\"list-group-item\" style=\"background-color: lightgrey;\"  fxLayout=\"row\" fxLayoutAlign=\"start start\">\n\t\t\t\t\t\t\t<div fxFlexOffset=\"3\">\n\t\t\t\t\t\t\t\t<strong>Files Status</strong><br>\n\t\t\t\t\t\t\t\t<div fxLayout=\"column\" fxLayoutAlign=\"center start\">\n\t\t\t\t\t\t\t\t\t<span><i [ngStyle]=\"{ color : job.files.patients ? 'green' : 'red'}\" class=\"fa fa-hdd-o\" aria-hidden=\"true\"></i> patients data</span>\n\t\t\t\t\t\t\t\t\t<span><i [ngStyle]=\"{ color : job.files.vaccines ? 'green' : 'red'}\" class=\"fa fa-hdd-o\" aria-hidden=\"true\"></i> vaccination data</span>\n\t\t\t\t\t\t\t\t</div>\n\t\t\t\t\t\t\t</div>\n\t\t\t\t\t\t\t<div fxFlexOffset=\"3\">\n\t\t\t\t\t\t\t\t<strong>Estimated Remaining Time</strong><br>\n\t\t\t\t\t\t\t\t10 minutes\n\t\t\t\t\t\t\t</div>\n\t\t\t\t\t\t\t<div fxFlexOffset=\"3\">\n\t\t\t\t\t\t\t\t<strong>Number of Records</strong><br>\n\t\t\t\t\t\t\t\t10000000\n\t\t\t\t\t\t\t</div>\n\t\t\t\t\t\t\t<div fxFlexOffset=\"3\">\n\t\t\t\t\t\t\t\t<strong>Processed records</strong><br>\n\t\t\t\t\t\t\t\t90767\n\t\t\t\t\t\t\t</div>\n\t\t\t\t\t\t</div>\n\t\t\t\t\t</div>\n\t\t\t\t</div>\n\t\t\t</div>\n\t\t</div>\n\t</div>\n</div>\n"

/***/ }),

/***/ "../../../../../src/app/jobs/jobs.component.ts":
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "a", function() { return JobsComponent; });
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_0__angular_core__ = __webpack_require__("../../../core/@angular/core.es5.js");
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_1__services_jobs_service__ = __webpack_require__("../../../../../src/app/services/jobs.service.ts");
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_2_rxjs__ = __webpack_require__("../../../../rxjs/Rx.js");
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_2_rxjs___default = __webpack_require__.n(__WEBPACK_IMPORTED_MODULE_2_rxjs__);
var __decorate = (this && this.__decorate) || function (decorators, target, key, desc) {
    var c = arguments.length, r = c < 3 ? target : desc === null ? desc = Object.getOwnPropertyDescriptor(target, key) : desc, d;
    if (typeof Reflect === "object" && typeof Reflect.decorate === "function") r = Reflect.decorate(decorators, target, key, desc);
    else for (var i = decorators.length - 1; i >= 0; i--) if (d = decorators[i]) r = (c < 3 ? d(r) : c > 3 ? d(target, key, r) : d(target, key)) || r;
    return c > 3 && r && Object.defineProperty(target, key, r), r;
};
var __metadata = (this && this.__metadata) || function (k, v) {
    if (typeof Reflect === "object" && typeof Reflect.metadata === "function") return Reflect.metadata(k, v);
};



var JobsComponent = (function () {
    function JobsComponent($jobs) {
        this.$jobs = $jobs;
        this.rate$ = new __WEBPACK_IMPORTED_MODULE_2_rxjs__["Subject"]();
    }
    JobsComponent.prototype.setRefreshRate = function (i) {
        this.rate$.next(i);
        this.current = i;
    };
    JobsComponent.prototype.toggle = function (i) {
        if (this.toggled(i)) {
            this.active = -1;
        }
        else
            this.active = i;
    };
    JobsComponent.prototype.toggled = function (i) {
        return i === this.active;
    };
    JobsComponent.prototype.ngOnInit = function () {
        var _this = this;
        this.rates = [
            -1, 5, 10, 15
        ];
        this.current = 5;
        this.jobs = [];
        this.$jobs.refreshRate(this.rate$);
        this.rate$.next(this.current);
        this.$jobs.startFlow$().subscribe(function (x) {
            _this.updateList(x);
        });
    };
    JobsComponent.prototype.find = function (x) {
        return this.jobs.find(function (z) {
            return z.id === x.id;
        });
    };
    JobsComponent.prototype.updateList = function (x) {
        while (x.length > 0) {
            var job = x.pop();
            var e_job = this.find(job);
            if (e_job) {
                Object.assign(e_job, job);
            }
            else {
                this.jobs.push(job);
            }
        }
    };
    return JobsComponent;
}());
JobsComponent = __decorate([
    Object(__WEBPACK_IMPORTED_MODULE_0__angular_core__["Component"])({
        selector: 'app-jobs',
        template: __webpack_require__("../../../../../src/app/jobs/jobs.component.html"),
        styles: [__webpack_require__("../../../../../src/app/jobs/jobs.component.css")]
    }),
    __metadata("design:paramtypes", [typeof (_a = typeof __WEBPACK_IMPORTED_MODULE_1__services_jobs_service__["a" /* JobsService */] !== "undefined" && __WEBPACK_IMPORTED_MODULE_1__services_jobs_service__["a" /* JobsService */]) === "function" && _a || Object])
], JobsComponent);

var _a;
//# sourceMappingURL=jobs.component.js.map

/***/ }),

/***/ "../../../../../src/app/login/login.component.css":
/***/ (function(module, exports, __webpack_require__) {

exports = module.exports = __webpack_require__("../../../../css-loader/lib/css-base.js")(false);
// imports


// module
exports.push([module.i, ".ng-invalid:not(form)  {\n\tborder-bottom: 2px solid #d82320; /* red */\n}\n\n.ng-valid:not(form)  {\n\tborder-bottom: 2px solid green; /* red */\n}\n", ""]);

// exports


/*** EXPORTS FROM exports-loader ***/
module.exports = module.exports.toString();

/***/ }),

/***/ "../../../../../src/app/login/login.component.html":
/***/ (function(module, exports) {

module.exports = "<div fxFlexFill class=\"back\" fxLayout=\"column\" fxLayoutAlign=\"start stretch\" >\n\t<div style=\"height: 60px;\"></div>\n\t<div fxFlex fxLayout=\"column\" fxLayoutAlign=\"start center\" style=\"padding: 50px;  width: 100%; padding: 50px;\" >\n\t\t<div fxFlexOffset=\"5\" class=\"card\" >\n\t\t\t<div class=\"card-block\">\n\t\t\t\t<h4 class=\"card-title\">Sign In</h4>\n\t\t\t</div>\n\t\t\t<div class=\"card-block\">\n\t\t\t\t<form style=\"width: 400px;\">\n\t\t\t\t\t<div *ngIf=\"error.on\" class=\"alert alert-danger\">\n\t\t\t\t\t\t{{error.message}}\n\t\t\t\t\t</div>\n\t\t\t\t\t<div class=\"form-group row\">\n\t\t\t\t\t\t<!--<label for=\"example-text-input\" class=\"col-4 col-form-label\">Username</label>-->\n\t\t\t\t\t\t<div class=\"col-12\">\n\t\t\t\t\t\t\t<input id=\"username\" name=\"username\" required minlength=\"4\" [(ngModel)]=\"username\"  class=\"form-control\" placeholder=\"Username\" type=\"text\" id=\"example-text-input\">\n\t\t\t\t\t\t</div>\n\t\t\t\t\t</div>\n\t\t\t\t\t<div class=\"form-group row\">\n\t\t\t\t\t\t<!--<label for=\"example-search-input\" class=\"col-4 col-form-label\">Password</label>-->\n\t\t\t\t\t\t<div class=\"col-12\">\n\t\t\t\t\t\t\t<input id=\"password\" name=\"password\"  required minlength=\"4\" [(ngModel)]=\"password\"  class=\"form-control\" placeholder=\"Password\" type=\"password\" id=\"example-search-input\">\n\t\t\t\t\t\t</div>\n\t\t\t\t\t</div>\n\t\t\t\t</form>\n\t\t\t\t<button class=\"btn btn-primary\" style=\"width: 100%;\" (click)=\"login()\">Log In</button>\n\t\t\t\t<div fxLayout=\"column\" fxLayoutAlign=\"end end\" style=\"margin-top: 10px;\">\n\n\t\t\t\t\t<a href=\"#\" class=\"card-link\">Sign Up</a>\n\t\t\t\t\t<a href=\"#\" class=\"card-link\">Forgot password</a>\n\t\t\t\t</div>\n\t\t\t</div>\n\t\t</div>\n\t</div>\n</div>\n"

/***/ }),

/***/ "../../../../../src/app/login/login.component.ts":
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "a", function() { return LoginComponent; });
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_0__angular_core__ = __webpack_require__("../../../core/@angular/core.es5.js");
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_1__services_user_service__ = __webpack_require__("../../../../../src/app/services/user.service.ts");
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_2__angular_router__ = __webpack_require__("../../../router/@angular/router.es5.js");
var __decorate = (this && this.__decorate) || function (decorators, target, key, desc) {
    var c = arguments.length, r = c < 3 ? target : desc === null ? desc = Object.getOwnPropertyDescriptor(target, key) : desc, d;
    if (typeof Reflect === "object" && typeof Reflect.decorate === "function") r = Reflect.decorate(decorators, target, key, desc);
    else for (var i = decorators.length - 1; i >= 0; i--) if (d = decorators[i]) r = (c < 3 ? d(r) : c > 3 ? d(target, key, r) : d(target, key)) || r;
    return c > 3 && r && Object.defineProperty(target, key, r), r;
};
var __metadata = (this && this.__metadata) || function (k, v) {
    if (typeof Reflect === "object" && typeof Reflect.metadata === "function") return Reflect.metadata(k, v);
};



var LoginComponent = (function () {
    function LoginComponent($user, router) {
        this.$user = $user;
        this.router = router;
        this.error = {
            on: false,
            message: ""
        };
    }
    LoginComponent.prototype.login = function () {
        var ctrl = this;
        this.$user.logIn(this.username, this.password).then(function (x) {
            if (!x) {
                ctrl.error.on = true;
                ctrl.error.message = "Unable to authenticate user, please try in a few moments";
                return;
            }
            if (x.status) {
                ctrl.router.navigate(["jobs"]).then(function (x) {
                    ctrl.error.on = false;
                    ctrl.error.message = "";
                });
            }
            else {
                ctrl.error.on = !x.status;
                ctrl.error.message = x.message;
            }
        });
    };
    LoginComponent.prototype.ngOnInit = function () {
        this.username = "";
        this.password = "";
    };
    return LoginComponent;
}());
LoginComponent = __decorate([
    Object(__WEBPACK_IMPORTED_MODULE_0__angular_core__["Component"])({
        selector: 'app-login',
        template: __webpack_require__("../../../../../src/app/login/login.component.html"),
        styles: [__webpack_require__("../../../../../src/app/login/login.component.css")]
    }),
    __metadata("design:paramtypes", [typeof (_a = typeof __WEBPACK_IMPORTED_MODULE_1__services_user_service__["a" /* UserService */] !== "undefined" && __WEBPACK_IMPORTED_MODULE_1__services_user_service__["a" /* UserService */]) === "function" && _a || Object, typeof (_b = typeof __WEBPACK_IMPORTED_MODULE_2__angular_router__["b" /* Router */] !== "undefined" && __WEBPACK_IMPORTED_MODULE_2__angular_router__["b" /* Router */]) === "function" && _b || Object])
], LoginComponent);

var _a, _b;
//# sourceMappingURL=login.component.js.map

/***/ }),

/***/ "../../../../../src/app/services/auth.interceptor.ts":
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "a", function() { return AuthInterceptor; });
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_0_rxjs_add_operator_do__ = __webpack_require__("../../../../rxjs/add/operator/do.js");
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_0_rxjs_add_operator_do___default = __webpack_require__.n(__WEBPACK_IMPORTED_MODULE_0_rxjs_add_operator_do__);
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_1__angular_common_http__ = __webpack_require__("../../../common/@angular/common/http.es5.js");
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_2__user_service__ = __webpack_require__("../../../../../src/app/services/user.service.ts");
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_3__angular_router__ = __webpack_require__("../../../router/@angular/router.es5.js");
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_4__angular_core__ = __webpack_require__("../../../core/@angular/core.es5.js");
var __decorate = (this && this.__decorate) || function (decorators, target, key, desc) {
    var c = arguments.length, r = c < 3 ? target : desc === null ? desc = Object.getOwnPropertyDescriptor(target, key) : desc, d;
    if (typeof Reflect === "object" && typeof Reflect.decorate === "function") r = Reflect.decorate(decorators, target, key, desc);
    else for (var i = decorators.length - 1; i >= 0; i--) if (d = decorators[i]) r = (c < 3 ? d(r) : c > 3 ? d(target, key, r) : d(target, key)) || r;
    return c > 3 && r && Object.defineProperty(target, key, r), r;
};
var __metadata = (this && this.__metadata) || function (k, v) {
    if (typeof Reflect === "object" && typeof Reflect.metadata === "function") return Reflect.metadata(k, v);
};
/**
 * Created by hnt5 on 12/3/17.
 */





var AuthInterceptor = (function () {
    function AuthInterceptor(auth, router) {
        this.auth = auth;
        this.router = router;
    }
    AuthInterceptor.prototype.intercept = function (request, next) {
        var _this = this;
        return next.handle(request).do(function (event) {
        }, function (err) {
            if (err instanceof __WEBPACK_IMPORTED_MODULE_1__angular_common_http__["d" /* HttpErrorResponse */]) {
                if (err.status === 401 || err.status === 403) {
                    console.log("UNAUTHORIZED");
                    _this.auth.clear();
                    _this.router.navigate(['/login']);
                }
            }
        });
    };
    return AuthInterceptor;
}());
AuthInterceptor = __decorate([
    Object(__WEBPACK_IMPORTED_MODULE_4__angular_core__["Injectable"])(),
    __metadata("design:paramtypes", [typeof (_a = typeof __WEBPACK_IMPORTED_MODULE_2__user_service__["a" /* UserService */] !== "undefined" && __WEBPACK_IMPORTED_MODULE_2__user_service__["a" /* UserService */]) === "function" && _a || Object, typeof (_b = typeof __WEBPACK_IMPORTED_MODULE_3__angular_router__["b" /* Router */] !== "undefined" && __WEBPACK_IMPORTED_MODULE_3__angular_router__["b" /* Router */]) === "function" && _b || Object])
], AuthInterceptor);

var _a, _b;
//# sourceMappingURL=auth.interceptor.js.map

/***/ }),

/***/ "../../../../../src/app/services/jobs.service.ts":
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
/* unused harmony export OpRes */
/* unused harmony export JobResult */
/* unused harmony export SubjectField */
/* unused harmony export Job */
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "a", function() { return JobsService; });
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_0__angular_core__ = __webpack_require__("../../../core/@angular/core.es5.js");
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_1_rxjs__ = __webpack_require__("../../../../rxjs/Rx.js");
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_1_rxjs___default = __webpack_require__.n(__WEBPACK_IMPORTED_MODULE_1_rxjs__);
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_2__angular_common_http__ = __webpack_require__("../../../common/@angular/common/http.es5.js");
var __decorate = (this && this.__decorate) || function (decorators, target, key, desc) {
    var c = arguments.length, r = c < 3 ? target : desc === null ? desc = Object.getOwnPropertyDescriptor(target, key) : desc, d;
    if (typeof Reflect === "object" && typeof Reflect.decorate === "function") r = Reflect.decorate(decorators, target, key, desc);
    else for (var i = decorators.length - 1; i >= 0; i--) if (d = decorators[i]) r = (c < 3 ? d(r) : c > 3 ? d(target, key, r) : d(target, key)) || r;
    return c > 3 && r && Object.defineProperty(target, key, r), r;
};
var __metadata = (this && this.__metadata) || function (k, v) {
    if (typeof Reflect === "object" && typeof Reflect.metadata === "function") return Reflect.metadata(k, v);
};
var __awaiter = (this && this.__awaiter) || function (thisArg, _arguments, P, generator) {
    return new (P || (P = Promise))(function (resolve, reject) {
        function fulfilled(value) { try { step(generator.next(value)); } catch (e) { reject(e); } }
        function rejected(value) { try { step(generator["throw"](value)); } catch (e) { reject(e); } }
        function step(result) { result.done ? resolve(result.value) : new P(function (resolve) { resolve(result.value); }).then(fulfilled, rejected); }
        step((generator = generator.apply(thisArg, _arguments || [])).next());
    });
};
var __generator = (this && this.__generator) || function (thisArg, body) {
    var _ = { label: 0, sent: function() { if (t[0] & 1) throw t[1]; return t[1]; }, trys: [], ops: [] }, f, y, t, g;
    return g = { next: verb(0), "throw": verb(1), "return": verb(2) }, typeof Symbol === "function" && (g[Symbol.iterator] = function() { return this; }), g;
    function verb(n) { return function (v) { return step([n, v]); }; }
    function step(op) {
        if (f) throw new TypeError("Generator is already executing.");
        while (_) try {
            if (f = 1, y && (t = y[op[0] & 2 ? "return" : op[0] ? "throw" : "next"]) && !(t = t.call(y, op[1])).done) return t;
            if (y = 0, t) op = [0, t.value];
            switch (op[0]) {
                case 0: case 1: t = op; break;
                case 4: _.label++; return { value: op[1], done: false };
                case 5: _.label++; y = op[1]; op = [0]; continue;
                case 7: op = _.ops.pop(); _.trys.pop(); continue;
                default:
                    if (!(t = _.trys, t = t.length > 0 && t[t.length - 1]) && (op[0] === 6 || op[0] === 2)) { _ = 0; continue; }
                    if (op[0] === 3 && (!t || (op[1] > t[0] && op[1] < t[3]))) { _.label = op[1]; break; }
                    if (op[0] === 6 && _.label < t[1]) { _.label = t[1]; t = op; break; }
                    if (t && _.label < t[2]) { _.label = t[2]; _.ops.push(op); break; }
                    if (t[2]) _.ops.pop();
                    _.trys.pop(); continue;
            }
            op = body.call(thisArg, _);
        } catch (e) { op = [6, e]; y = 0; } finally { f = t = 0; }
        if (op[0] & 5) throw op[1]; return { value: op[0] ? op[1] : void 0, done: true };
    }
};



var OpRes = (function () {
    function OpRes() {
    }
    return OpRes;
}());

var JobResult = (function () {
    function JobResult() {
    }
    return JobResult;
}());

var SubjectField = (function () {
    function SubjectField() {
    }
    return SubjectField;
}());

var Job = (function () {
    function Job(j) {
        this.id = j.id;
        this.name = j.name;
        this.configuration = j.configuration;
        this.files = j.files;
        this.dateEnded = j.dateEnded;
        this.dateStarted = j.dateStarted;
        this.dateCreated = j.dateCreated;
        this.progress = j.progress;
        this.status = j.status;
        this.stats = j.stats;
        this.resultId = j.resultId;
    }
    Job.prototype.running = function () {
        return this.status === "STARTED";
    };
    Job.prototype.queued = function () {
        return this.status === "QUEUED";
    };
    Job.prototype.finished = function () {
        return this.status === "FINISHED";
    };
    Job.prototype.stopped = function () {
        return this.status === "STOPED";
    };
    Job.prototype.failed = function () {
        return this.status === "FAILED";
    };
    return Job;
}());

var JobsService = (function () {
    function JobsService(http) {
        this.http = http;
        this.rate = 20;
    }
    JobsService.prototype.startFlow$ = function () {
        this.stop = false;
        this.jobs = new __WEBPACK_IMPORTED_MODULE_1_rxjs__["Subject"]();
        this.fetchJobs();
        this.loop();
        return this.jobs;
    };
    JobsService.prototype.fieldsTree = function (rs) {
        var tree = [];
        for (var _i = 0, _a = rs.fields; _i < _a.length; _i++) {
            var sub = _a[_i];
            this.addToTree(tree, sub, "", sub.fieldOfInterest);
        }
        return tree[0].children;
    };
    JobsService.prototype.addToTree = function (tree, sub, path_first, path_next) {
        if (path_next === "") {
            var node = {
                label: path_first,
                selectable: true,
                data: sub,
                children: []
            };
            tree.push(node);
            return;
        }
        else {
            var z = path_next.split(".");
            var n_path_first = z[0];
            var n_path_next = z.length > 1 ? path_next.substr(path_next.indexOf(".") + 1) : "";
            for (var _i = 0, tree_1 = tree; _i < tree_1.length; _i++) {
                var tnode = tree_1[_i];
                if (tnode.label === path_first) {
                    this.addToTree(tnode.children, sub, n_path_first, n_path_next);
                    return;
                }
            }
            var node = {
                label: path_first,
                selectable: false,
                children: []
            };
            tree.push(node);
            this.addToTree(node.children, sub, n_path_first, n_path_next);
        }
    };
    JobsService.prototype.loop = function () {
        var ctrl = this;
        setTimeout(function () {
            ctrl.fetchJobs();
            if (!ctrl.stop) {
                ctrl.loop();
            }
        }, ctrl.rate * 1000);
    };
    JobsService.prototype.refreshRate = function (obs) {
        var _this = this;
        obs.subscribe(function (x) {
            _this.rate = x;
        });
    };
    JobsService.prototype.deleteJob = function (j) {
        return __awaiter(this, void 0, void 0, function () {
            var r;
            return __generator(this, function (_a) {
                switch (_a.label) {
                    case 0: return [4 /*yield*/, this.http.delete("api/job/" + j.id).toPromise()];
                    case 1:
                        r = _a.sent();
                        if (r.status) {
                            //TODO OK
                        }
                        else {
                            //TODO Failure
                        }
                        return [2 /*return*/];
                }
            });
        });
    };
    JobsService.prototype.stopJob = function (j) {
        return __awaiter(this, void 0, void 0, function () {
            var r;
            return __generator(this, function (_a) {
                switch (_a.label) {
                    case 0: return [4 /*yield*/, this.http.get("api/job/" + j.id + "/stop").toPromise()];
                    case 1:
                        r = _a.sent();
                        if (r.status) {
                            //TODO OK
                        }
                        else {
                            //TODO Failure
                        }
                        return [2 /*return*/];
                }
            });
        });
    };
    JobsService.prototype.getResult = function (id) {
        return __awaiter(this, void 0, void 0, function () {
            var r;
            return __generator(this, function (_a) {
                switch (_a.label) {
                    case 0: return [4 /*yield*/, this.http.get("api/job/" + id + "/result").toPromise()];
                    case 1:
                        r = _a.sent();
                        return [2 /*return*/, r];
                }
            });
        });
    };
    JobsService.prototype.fetchJobs = function () {
        return __awaiter(this, void 0, void 0, function () {
            var jobs, result, _i, jobs_1, job, e_1;
            return __generator(this, function (_a) {
                switch (_a.label) {
                    case 0:
                        _a.trys.push([0, 2, , 3]);
                        return [4 /*yield*/, this.http.get("api/jobs").toPromise()];
                    case 1:
                        jobs = _a.sent();
                        if (this.jobs) {
                            result = [];
                            for (_i = 0, jobs_1 = jobs; _i < jobs_1.length; _i++) {
                                job = jobs_1[_i];
                                result.push(new Job(job));
                            }
                            this.jobs.next(result);
                        }
                        return [3 /*break*/, 3];
                    case 2:
                        e_1 = _a.sent();
                        this.halt();
                        return [3 /*break*/, 3];
                    case 3: return [2 /*return*/];
                }
            });
        });
    };
    JobsService.prototype.halt = function () {
        this.stop = true;
    };
    JobsService.prototype.myJobs$ = function () {
    };
    return JobsService;
}());
JobsService = __decorate([
    Object(__WEBPACK_IMPORTED_MODULE_0__angular_core__["Injectable"])(),
    __metadata("design:paramtypes", [typeof (_a = typeof __WEBPACK_IMPORTED_MODULE_2__angular_common_http__["b" /* HttpClient */] !== "undefined" && __WEBPACK_IMPORTED_MODULE_2__angular_common_http__["b" /* HttpClient */]) === "function" && _a || Object])
], JobsService);

var _a;
//# sourceMappingURL=jobs.service.js.map

/***/ }),

/***/ "../../../../../src/app/services/server-info.service.ts":
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "a", function() { return ServerInfo; });
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "b", function() { return ServerInfoService; });
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_0__angular_core__ = __webpack_require__("../../../core/@angular/core.es5.js");
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_1__angular_http__ = __webpack_require__("../../../http/@angular/http.es5.js");
var __decorate = (this && this.__decorate) || function (decorators, target, key, desc) {
    var c = arguments.length, r = c < 3 ? target : desc === null ? desc = Object.getOwnPropertyDescriptor(target, key) : desc, d;
    if (typeof Reflect === "object" && typeof Reflect.decorate === "function") r = Reflect.decorate(decorators, target, key, desc);
    else for (var i = decorators.length - 1; i >= 0; i--) if (d = decorators[i]) r = (c < 3 ? d(r) : c > 3 ? d(target, key, r) : d(target, key)) || r;
    return c > 3 && r && Object.defineProperty(target, key, r), r;
};
var __metadata = (this && this.__metadata) || function (k, v) {
    if (typeof Reflect === "object" && typeof Reflect.metadata === "function") return Reflect.metadata(k, v);
};
var __awaiter = (this && this.__awaiter) || function (thisArg, _arguments, P, generator) {
    return new (P || (P = Promise))(function (resolve, reject) {
        function fulfilled(value) { try { step(generator.next(value)); } catch (e) { reject(e); } }
        function rejected(value) { try { step(generator["throw"](value)); } catch (e) { reject(e); } }
        function step(result) { result.done ? resolve(result.value) : new P(function (resolve) { resolve(result.value); }).then(fulfilled, rejected); }
        step((generator = generator.apply(thisArg, _arguments || [])).next());
    });
};
var __generator = (this && this.__generator) || function (thisArg, body) {
    var _ = { label: 0, sent: function() { if (t[0] & 1) throw t[1]; return t[1]; }, trys: [], ops: [] }, f, y, t, g;
    return g = { next: verb(0), "throw": verb(1), "return": verb(2) }, typeof Symbol === "function" && (g[Symbol.iterator] = function() { return this; }), g;
    function verb(n) { return function (v) { return step([n, v]); }; }
    function step(op) {
        if (f) throw new TypeError("Generator is already executing.");
        while (_) try {
            if (f = 1, y && (t = y[op[0] & 2 ? "return" : op[0] ? "throw" : "next"]) && !(t = t.call(y, op[1])).done) return t;
            if (y = 0, t) op = [0, t.value];
            switch (op[0]) {
                case 0: case 1: t = op; break;
                case 4: _.label++; return { value: op[1], done: false };
                case 5: _.label++; y = op[1]; op = [0]; continue;
                case 7: op = _.ops.pop(); _.trys.pop(); continue;
                default:
                    if (!(t = _.trys, t = t.length > 0 && t[t.length - 1]) && (op[0] === 6 || op[0] === 2)) { _ = 0; continue; }
                    if (op[0] === 3 && (!t || (op[1] > t[0] && op[1] < t[3]))) { _.label = op[1]; break; }
                    if (op[0] === 6 && _.label < t[1]) { _.label = t[1]; t = op; break; }
                    if (t && _.label < t[2]) { _.label = t[2]; _.ops.push(op); break; }
                    if (t[2]) _.ops.pop();
                    _.trys.pop(); continue;
            }
            op = body.call(thisArg, _);
        } catch (e) { op = [6, e]; y = 0; } finally { f = t = 0; }
        if (op[0] & 5) throw op[1]; return { value: op[0] ? op[1] : void 0, done: true };
    }
};


var ServerInfo = (function () {
    function ServerInfo(v, q, d) {
        this.version = v;
        this.qualifier = q;
        this.date = d;
    }
    return ServerInfo;
}());

var ServerInfoService = (function () {
    function ServerInfoService(http) {
        this.http = http;
        this.serverInfo = null;
        this.fetch();
    }
    ServerInfoService.prototype.fetch = function () {
        return __awaiter(this, void 0, void 0, function () {
            var serverInf;
            return __generator(this, function (_a) {
                switch (_a.label) {
                    case 0: return [4 /*yield*/, this.http
                            .get("public/serverInfo")
                            .map(function (response) { return response.json(); })
                            .map(function (_a) {
                            var version = _a.version, qualifier = _a.qualifier, date = _a.date;
                            return new ServerInfo(version, qualifier, date);
                        })
                            .toPromise()];
                    case 1:
                        serverInf = _a.sent();
                        this.serverInfo = serverInf;
                        return [2 /*return*/, serverInf];
                }
            });
        });
    };
    ServerInfoService.prototype.get = function () {
        return __awaiter(this, void 0, void 0, function () {
            return __generator(this, function (_a) {
                if (this.serverInfo) {
                    return [2 /*return*/, this.serverInfo];
                }
                else {
                    return [2 /*return*/, this.fetch()];
                }
                return [2 /*return*/];
            });
        });
    };
    return ServerInfoService;
}());
ServerInfoService = __decorate([
    Object(__WEBPACK_IMPORTED_MODULE_0__angular_core__["Injectable"])(),
    __metadata("design:paramtypes", [typeof (_a = typeof __WEBPACK_IMPORTED_MODULE_1__angular_http__["a" /* Http */] !== "undefined" && __WEBPACK_IMPORTED_MODULE_1__angular_http__["a" /* Http */]) === "function" && _a || Object])
], ServerInfoService);

var _a;
//# sourceMappingURL=server-info.service.js.map

/***/ }),

/***/ "../../../../../src/app/services/user.service.ts":
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
/* unused harmony export User */
/* unused harmony export LoginResponse */
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "a", function() { return UserService; });
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_0__angular_core__ = __webpack_require__("../../../core/@angular/core.es5.js");
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_1__angular_http__ = __webpack_require__("../../../http/@angular/http.es5.js");
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_2_rxjs_add_operator_map__ = __webpack_require__("../../../../rxjs/add/operator/map.js");
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_2_rxjs_add_operator_map___default = __webpack_require__.n(__WEBPACK_IMPORTED_MODULE_2_rxjs_add_operator_map__);
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_3_rxjs__ = __webpack_require__("../../../../rxjs/Rx.js");
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_3_rxjs___default = __webpack_require__.n(__WEBPACK_IMPORTED_MODULE_3_rxjs__);
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_4__angular_router__ = __webpack_require__("../../../router/@angular/router.es5.js");
var __decorate = (this && this.__decorate) || function (decorators, target, key, desc) {
    var c = arguments.length, r = c < 3 ? target : desc === null ? desc = Object.getOwnPropertyDescriptor(target, key) : desc, d;
    if (typeof Reflect === "object" && typeof Reflect.decorate === "function") r = Reflect.decorate(decorators, target, key, desc);
    else for (var i = decorators.length - 1; i >= 0; i--) if (d = decorators[i]) r = (c < 3 ? d(r) : c > 3 ? d(target, key, r) : d(target, key)) || r;
    return c > 3 && r && Object.defineProperty(target, key, r), r;
};
var __metadata = (this && this.__metadata) || function (k, v) {
    if (typeof Reflect === "object" && typeof Reflect.metadata === "function") return Reflect.metadata(k, v);
};
var __awaiter = (this && this.__awaiter) || function (thisArg, _arguments, P, generator) {
    return new (P || (P = Promise))(function (resolve, reject) {
        function fulfilled(value) { try { step(generator.next(value)); } catch (e) { reject(e); } }
        function rejected(value) { try { step(generator["throw"](value)); } catch (e) { reject(e); } }
        function step(result) { result.done ? resolve(result.value) : new P(function (resolve) { resolve(result.value); }).then(fulfilled, rejected); }
        step((generator = generator.apply(thisArg, _arguments || [])).next());
    });
};
var __generator = (this && this.__generator) || function (thisArg, body) {
    var _ = { label: 0, sent: function() { if (t[0] & 1) throw t[1]; return t[1]; }, trys: [], ops: [] }, f, y, t, g;
    return g = { next: verb(0), "throw": verb(1), "return": verb(2) }, typeof Symbol === "function" && (g[Symbol.iterator] = function() { return this; }), g;
    function verb(n) { return function (v) { return step([n, v]); }; }
    function step(op) {
        if (f) throw new TypeError("Generator is already executing.");
        while (_) try {
            if (f = 1, y && (t = y[op[0] & 2 ? "return" : op[0] ? "throw" : "next"]) && !(t = t.call(y, op[1])).done) return t;
            if (y = 0, t) op = [0, t.value];
            switch (op[0]) {
                case 0: case 1: t = op; break;
                case 4: _.label++; return { value: op[1], done: false };
                case 5: _.label++; y = op[1]; op = [0]; continue;
                case 7: op = _.ops.pop(); _.trys.pop(); continue;
                default:
                    if (!(t = _.trys, t = t.length > 0 && t[t.length - 1]) && (op[0] === 6 || op[0] === 2)) { _ = 0; continue; }
                    if (op[0] === 3 && (!t || (op[1] > t[0] && op[1] < t[3]))) { _.label = op[1]; break; }
                    if (op[0] === 6 && _.label < t[1]) { _.label = t[1]; t = op; break; }
                    if (t && _.label < t[2]) { _.label = t[2]; _.ops.push(op); break; }
                    if (t[2]) _.ops.pop();
                    _.trys.pop(); continue;
            }
            op = body.call(thisArg, _);
        } catch (e) { op = [6, e]; y = 0; } finally { f = t = 0; }
        if (op[0] & 5) throw op[1]; return { value: op[0] ? op[1] : void 0, done: true };
    }
};





var User = (function () {
    function User(id, username, roles) {
        this.id = id;
        this.username = username;
        this.roles = roles;
    }
    return User;
}());

var LoginResponse = (function () {
    function LoginResponse(user, status, message) {
        this.user = user;
        this.status = status;
        this.message = message;
    }
    return LoginResponse;
}());

var UserService = (function () {
    function UserService(http, router) {
        this.http = http;
        this.router = router;
        this.currentUser = null;
        var ctrl = this;
        this.authentication = new __WEBPACK_IMPORTED_MODULE_3_rxjs__["Subject"]();
        this.authentication.subscribe(function (next) {
            ctrl.currentUser = next.user;
        });
        this.me().then(function (loginResponse) {
            ctrl.authentication.next(loginResponse);
        }, function (err) {
            ctrl.authentication.next({
                status: false,
                message: "no user",
                user: null
            });
        });
    }
    UserService.prototype.cuser = function () {
        return this.currentUser;
    };
    UserService.prototype.loggedIn = function () {
        return this.currentUser != null;
    };
    UserService.prototype.clear = function () {
        this.authentication.next(null);
    };
    UserService.prototype.logout = function () {
        var ctrl = this;
        this.http.get("api/logout").toPromise().then(function (x) {
            ctrl.router.navigate(['home']);
            ctrl.authentication.next(new LoginResponse(null, false, "logout"));
        }, function (y) {
            ctrl.router.navigate(['home']);
        });
    };
    UserService.prototype.auth$ = function () {
        return this.authentication;
    };
    UserService.prototype.me = function () {
        return __awaiter(this, void 0, void 0, function () {
            var response, e_1;
            return __generator(this, function (_a) {
                switch (_a.label) {
                    case 0:
                        _a.trys.push([0, 2, , 3]);
                        return [4 /*yield*/, this.http
                                .get("api/me")
                                .map(function (response) { return response.json(); })
                                .map(function (_a) {
                                var user = _a.user, status = _a.status, message = _a.message;
                                return new LoginResponse(user, status, message);
                            })
                                .toPromise()];
                    case 1:
                        response = _a.sent();
                        return [2 /*return*/, response];
                    case 2:
                        e_1 = _a.sent();
                        throw e_1;
                    case 3: return [2 /*return*/];
                }
            });
        });
    };
    UserService.prototype.logIn = function (username, password) {
        return __awaiter(this, void 0, void 0, function () {
            var response, e_2;
            return __generator(this, function (_a) {
                switch (_a.label) {
                    case 0:
                        _a.trys.push([0, 2, , 3]);
                        return [4 /*yield*/, this.http
                                .post("api/login", { username: username, password: password })
                                .map(function (response) { return response.json(); })
                                .map(function (_a) {
                                var user = _a.user, status = _a.status, message = _a.message;
                                return new LoginResponse(user, status, message);
                            })
                                .toPromise()];
                    case 1:
                        response = _a.sent();
                        this.authentication.next(response);
                        return [2 /*return*/, response];
                    case 2:
                        e_2 = _a.sent();
                        console.log(e_2);
                        return [2 /*return*/, null];
                    case 3: return [2 /*return*/];
                }
            });
        });
    };
    return UserService;
}());
UserService = __decorate([
    Object(__WEBPACK_IMPORTED_MODULE_0__angular_core__["Injectable"])(),
    __metadata("design:paramtypes", [typeof (_a = typeof __WEBPACK_IMPORTED_MODULE_1__angular_http__["a" /* Http */] !== "undefined" && __WEBPACK_IMPORTED_MODULE_1__angular_http__["a" /* Http */]) === "function" && _a || Object, typeof (_b = typeof __WEBPACK_IMPORTED_MODULE_4__angular_router__["b" /* Router */] !== "undefined" && __WEBPACK_IMPORTED_MODULE_4__angular_router__["b" /* Router */]) === "function" && _b || Object])
], UserService);

var _a, _b;
//# sourceMappingURL=user.service.js.map

/***/ }),

/***/ "../../../../../src/environments/environment.ts":
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "a", function() { return environment; });
// The file contents for the current environment will overwrite these during build.
// The build system defaults to the dev environment which uses `environment.ts`, but if you do
// `ng build --env=prod` then `environment.prod.ts` will be used instead.
// The list of which env maps to which file can be found in `.angular-cli.json`.
// The file contents for the current environment will overwrite these during build.
var environment = {
    production: false
};
//# sourceMappingURL=environment.js.map

/***/ }),

/***/ "../../../../../src/main.ts":
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
Object.defineProperty(__webpack_exports__, "__esModule", { value: true });
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_0__angular_core__ = __webpack_require__("../../../core/@angular/core.es5.js");
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_1__angular_platform_browser_dynamic__ = __webpack_require__("../../../platform-browser-dynamic/@angular/platform-browser-dynamic.es5.js");
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_2__app_app_module__ = __webpack_require__("../../../../../src/app/app.module.ts");
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_3__environments_environment__ = __webpack_require__("../../../../../src/environments/environment.ts");




if (__WEBPACK_IMPORTED_MODULE_3__environments_environment__["a" /* environment */].production) {
    Object(__WEBPACK_IMPORTED_MODULE_0__angular_core__["enableProdMode"])();
}
Object(__WEBPACK_IMPORTED_MODULE_1__angular_platform_browser_dynamic__["a" /* platformBrowserDynamic */])().bootstrapModule(__WEBPACK_IMPORTED_MODULE_2__app_app_module__["a" /* AppModule */])
    .catch(function (err) { return console.log(err); });
//# sourceMappingURL=main.js.map

/***/ }),

/***/ 0:
/***/ (function(module, exports, __webpack_require__) {

module.exports = __webpack_require__("../../../../../src/main.ts");


/***/ })

},[0]);
//# sourceMappingURL=main.bundle.js.map