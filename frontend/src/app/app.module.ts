import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { ReactiveFormsModule, FormsModule } from '@angular/forms';
import { HttpClientModule, HTTP_INTERCEPTORS } from '@angular/common/http';
import { RouterModule } from '@angular/router';
import { CommonModule } from '@angular/common';

import { ToastrModule } from 'ngx-toastr';

import { AppComponent } from './app.component';
import { HomeComponent } from './components/home/home.component';
import { LoginComponent } from './components/login/login.component';
import { RegisterComponent } from './components/register/register.component';
import { DashboardComponent } from './components/dashboard/dashboard.component';
import { HeaderComponent } from './components/header/header.component';
import { VirtualMachinesComponent } from './components/virtual-machines/virtual-machines.component';
import { TermsModalComponent } from './components/terms-modal/terms-modal.component';
import { PricingComponent } from './components/pricing/pricing.component';
import { ResourceSummaryComponent } from './components/resource-summary/resource-summary.component';
import { CheckoutComponent } from './components/checkout/checkout.component';

import { AuthService } from './services/auth.service';
import { TermsConditionsService } from './services/terms-conditions.service';
import { AuthGuard } from './guards/auth.guard';
import { GuestGuard } from './guards/guest.guard';
import { AuthInterceptor } from './interceptors/auth.interceptor';

const routes = [
  { path: '', component: HomeComponent },
  { path: 'login', component: LoginComponent, canActivate: [GuestGuard] },
  { path: 'register', component: RegisterComponent, canActivate: [GuestGuard] },
  { path: 'dashboard', component: DashboardComponent, canActivate: [AuthGuard] },
  { path: 'virtual-machines', component: VirtualMachinesComponent, canActivate: [AuthGuard] },
  { path: 'pricing', component: PricingComponent },
  { path: 'checkout', component: CheckoutComponent, canActivate: [AuthGuard] },
  { path: 'verify-email', component: HomeComponent },
  { path: '**', redirectTo: '' }
];

@NgModule({
  declarations: [
    AppComponent,
    HomeComponent,
    LoginComponent,
    RegisterComponent,
    DashboardComponent,
    HeaderComponent,
    VirtualMachinesComponent,
    TermsModalComponent,
    PricingComponent,
    ResourceSummaryComponent,
    CheckoutComponent
  ],
  imports: [
    BrowserModule,
    CommonModule,
    BrowserAnimationsModule,
    ReactiveFormsModule,
    FormsModule,
    HttpClientModule,
    RouterModule.forRoot(routes),
    ToastrModule.forRoot({
      timeOut: 3000,
      positionClass: 'toast-top-right',
      preventDuplicates: true,
    })
  ],
  providers: [
    AuthService,
    TermsConditionsService,
    AuthGuard,
    GuestGuard,
    {
      provide: HTTP_INTERCEPTORS,
      useClass: AuthInterceptor,
      multi: true
    }
  ],
  bootstrap: [AppComponent]
})
export class AppModule { } 