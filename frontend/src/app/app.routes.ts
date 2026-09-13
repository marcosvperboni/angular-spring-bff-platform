import { Routes } from '@angular/router';
import { authGuard } from './core/guards/auth.guard';

export const routes: Routes = [
  {
    path: '',
    loadComponent: () => import('./features/landing/landing').then((m) => m.Landing),
  },
  {
    path: 'login',
    loadComponent: () => import('./features/login/login').then((m) => m.Login),
  },
  {
    path: '',
    canActivate: [authGuard],
    loadComponent: () => import('./layout/shell/shell').then((m) => m.Shell),
    children: [
      { path: 'dashboard', loadComponent: () => import('./features/dashboard/dashboard').then((m) => m.Dashboard) },
      { path: 'customers', loadComponent: () => import('./features/customers/customers').then((m) => m.Customers) },
      { path: 'products', loadComponent: () => import('./features/products/products').then((m) => m.Products) },
      { path: 'orders', loadComponent: () => import('./features/orders/orders').then((m) => m.Orders) },
      { path: 'payments', loadComponent: () => import('./features/payments/payments').then((m) => m.Payments) },
      { path: '', redirectTo: 'dashboard', pathMatch: 'full' },
    ],
  },
  { path: '**', redirectTo: '' },
];
