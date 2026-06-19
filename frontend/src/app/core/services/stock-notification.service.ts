import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable, of } from 'rxjs';
import { catchError, map } from 'rxjs/operators';
import { environment } from '../../../environments/environment';

@Injectable({ providedIn: 'root' })
export class StockNotificationService {
  private apiUrl = environment.apiUrl;

  constructor(private http: HttpClient) {}

  checkStockFromNotityApi(sku: string): Observable<number | null> {
    const params = new HttpParams().set('sku', sku);

    return this.http.get<unknown>(`${this.apiUrl}/notityapi`, { params }).pipe(
      map((res) => this.extractStock(res)),
      catchError(() => this.http.post<unknown>(`${this.apiUrl}/notityapi`, { sku }).pipe(
        map((res) => this.extractStock(res)),
        catchError(() => of(null))
      ))
    );
  }

  registerNotifyMe(sku: string, email: string): Observable<unknown> {
    return this.http.post<unknown>(`${this.apiUrl}/notify_me`, {
      sku,
      email,
      subscribeNow: true
    });
  }

  private extractStock(response: unknown): number | null {
    if (!response || typeof response !== 'object') return null;

    const payload = response as Record<string, unknown>;
    if (typeof payload['stock'] === 'number') return payload['stock'];
    if (typeof payload['stockQuantity'] === 'number') return payload['stockQuantity'];
    if (typeof payload['currentStock'] === 'number') return payload['currentStock'];
    if (typeof payload['inStock'] === 'boolean') return payload['inStock'] ? 1 : 0;

    return null;
  }
}
