import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Order, OrderDetail, OrderInput } from '../models/api.models';

@Injectable({ providedIn: 'root' })
export class OrderService {
  private readonly baseUrl = `${environment.apiBaseUrl}/gateway/orders`;
  private readonly bffUrl = `${environment.apiBaseUrl}/api/bff/orders`;

  constructor(private readonly http: HttpClient) {}

  list(): Observable<Order[]> {
    return this.http.get<Order[]>(this.baseUrl);
  }

  detail(id: string): Observable<OrderDetail> {
    return this.http.get<OrderDetail>(`${this.bffUrl}/${id}/detail`);
  }

  create(order: OrderInput): Observable<Order> {
    return this.http.post<Order>(this.baseUrl, order);
  }
}
