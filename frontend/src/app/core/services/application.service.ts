import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import {Application} from '../models/application.model'
import { App } from '../../app';

@Injectable({
  providedIn: 'root',
})
export class ApplicationService {
  private http = inject(HttpClient);
  private apiUrl = 'http://localhost:8080/api/applications';

  getAll(): Observable<Application[]>{
    return this.http.get<Application[]>(this.apiUrl);
  }

  create(application:Application): Observable<Application>{
    return this.http.post<Application>(this.apiUrl,application);
  }

  update(id:number, application:Application):Observable<Application>{
    return this.http.put<Application>(
      `${this.apiUrl}/${id}`,
      application
    );
  }

  delete(id:number): Observable<void>{
    return this.http.delete<void>(
      `${this.apiUrl}/${id}`
    );

  }

  getById(id:number): Observable<Application>{
    return this.http.get<Application>(`${this.apiUrl}/${id}`);
  }

}

