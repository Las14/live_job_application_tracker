import { Routes } from '@angular/router';
import { Register } from './features/auth/register/register.component';
import { Login } from './features/auth/login/login.component';

import { ApplicationList } from './features/applications/application-list/application-list';
import { authGuard } from './core/guards/authGuard';
import { ApplicationForm } from './features/applications/application-form/application-form';
import { Profile } from './features/profile/profile';

export const routes: Routes = [
  {
    path:'',
    redirectTo:'login',
    pathMatch:'full'
  },
  
  {
    path: 'register',
    component: Register
  },
  {
    path: 'login',
    component: Login
  },
   {
    path:'applicationList',
    component: ApplicationList,
    canActivate:[authGuard]
  },
  {
    path:'applicationForm',
    component:ApplicationForm,
    canActivate: [authGuard]

  }, 
  {
    path:'applicationForm/:id/edit',
    component:ApplicationForm,
    canActivate: [authGuard]

  }, 
   {
    path:'profile',
    component:Profile,
    canActivate:[authGuard]
  }
  
];
