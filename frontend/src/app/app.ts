import { Component, signal } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { OnInit } from '@angular/core';
import { TaskService } from './services/task.service';
import { Task } from './models/task.model';
import { CommonModule, JsonPipe } from '@angular/common';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, CommonModule, JsonPipe],
  templateUrl: './app.html',
  styleUrl: './app.css'
})
export class App implements OnInit {
  protected readonly title = signal('frontend');

  tasks = signal<Task[]>([]);

  constructor(private taskService: TaskService) { }

  ngOnInit(): void {
    this.taskService.getTasks().subscribe({
      next: (data: Task[]): void => {
        console.log("Tasks received: ", data);
        this.tasks.set(data);
      },
      error: (error: any): void => {
        console.error("Error: ", error);
      }
    })
  }
}
