import { Status } from './status.model'

export class Task {
  constructor(
    private id: Number,
    private title: String,
    private description: String,
    private status: Status,
    private assignedTo: Number,
    private createdAt: Date,
    private updatedAt: Date,
  ) { }
}
