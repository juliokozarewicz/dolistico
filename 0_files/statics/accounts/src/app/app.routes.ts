import { Routes } from '@angular/router';

import { UpdatePasswordComponent } from './features/update-password/ts/component';
import { UpdateEmailComponent } from './features/update-email/ts/component';
import { DeleteAccountComponent } from './features/delete-account/ts/component';

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
  },
  {
    path: 'delete-account',
    component: DeleteAccountComponent,
    title: 'Delete Account'
  }
];
