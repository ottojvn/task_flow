import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { Task } from '../models/task.model';

@Injectable({ providedIn: 'root' })
export class TaskService {
  constructor(private http: HttpClient) {
    this.http = inject(HttpClient);
  }

  getTasks(): Observable<Task[]> {
    return this.http.get<Task[]>('http://localhost:8080/api/tasks');
  }
}
