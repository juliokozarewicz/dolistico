import { Routes } from '@angular/router';

import { UpdatePasswordComponent } from './features/update-password/update-password';
import { UpdateEmailComponent } from './features/update-email/update-email';

export const routes: Routes = [
  {
    path: 'update-password',
    component: UpdatePasswordComponent,
    title: 'Update Password'
  },
  {
    path: 'update-email',
    component: UpdateEmailComponent,
    title: 'Update Email'
  }
];