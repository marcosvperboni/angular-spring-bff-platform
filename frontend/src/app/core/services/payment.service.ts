import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Payment } from '../models/api.models';

@Injectable({ providedIn: 'root' })
export class PaymentService {
  private readonly baseUrl = `${environment.apiBaseUrl}/gateway/payments`;

  constructor(private readonly http: HttpClient) {}

  list(): Observable<Payment[]> {
    return this.http.get<Payment[]>(this.baseUrl);
  }

  byOrder(orderId: string): Observable<Payment[]> {
    return this.http.get<Payment[]>(`${this.baseUrl}/order/${orderId}`);
  }
}
