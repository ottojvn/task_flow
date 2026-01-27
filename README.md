# Task Flow 

## Endpoints

### POST /api/tasks

Creates a new task

``` sh
curl \
-X POST \
-d '{ \
    "title": "Task title", \
    "description": "Task description", \
    "status": "PENDING", \
    "assignedTo": 1 \
}'
localhost:8080/api/tasks
```

* **Expected Status Code:** 201 if successful, 500 otherwise

* **Response Example:**

**Valid Task**

``` json
{
    "id": 9,
    "title": "Task Title",
    "description": "POST test",
    "status": "PENDING",
    "assignedTo": 3,
    "createdAt": "2026-01-27T17:57:31.927769",
    "updatedAt": "2026-01-27T17:57:31.927885"
}
```

**Invalid Task**

``` json
{
    "timestamp": "2026-01-27T17:59:08.328636386",
    "Status": 500,
    "error": "Internal Server Error",
    "message": "Error processing request",
    "path": "/api/tasks"
}
```

### GET /api/tasks

Gets a paginated list with all tasks

``` sh
curl localhost:8080/api/tasks
```

* **Expected Status Code:** 200 OK

* **Options:** page, size (default = 10, max = 100), sort (key and order)

``` sh
curl localhost:8080/api/tasks?page=2&size=15&sort=title,asc
```

* **Response Example:**

``` json
{
    "content": [
        {
            "id": 6,
            "title": "Task 5",
            "description": "POST test",
            "status": "PENDING",
            "assignedTo": 3,
            "createdAt": "2026-01-26T13:58:53.438395",
            "updatedAt": "2026-01-26T13:58:53.438691"
        },
        {
            "id": 8,
            "title": "Task INVALID_STATUS",
            "description": "POST test",
            "status": "PENDING",
            "assignedTo": 3,
            "createdAt": "2026-01-26T15:28:44.640006",
            "updatedAt": "2026-01-26T15:28:44.640201"
        }
    ],
    "empty": false,
    "first": false,
    "last": true,
    "number": 2,
    "numberOfElements": 2,
    "pageable": {
        "offset": 4,
        "pageNumber": 2,
        "pageSize": 2,
        "paged": true,
        "sort": {
            "empty": true,
            "sorted": false,
            "unsorted": true
        },
        "unpaged": false
    },
    "size": 2,
    "sort": {
        "empty": true,
        "sorted": false,
        "unsorted": true
    },
    "totalElements": 6,
    "totalPages": 3
}
```


### GET /api/tasks/{id}

Gets a single task or error

``` sh
curl localhost:8080/api/tasks/1
```

* **Expected Status Code:** 200 OK if Task is found or 404 Not Found if task is not found

* **Response Example:**

**Task Found:**

``` json
{
    "id": 1,
    "title": "Updated Task 1",
    "description": "PUT Test",
    "status": "IN_PROGRESS",
    "assignedTo": 2,
    "createdAt": "2026-01-22T15:50:25.054924",
    "updatedAt": "2026-01-26T13:48:31.035477"
}
```

**Task Not Found:**

``` json
{
    "timestamp": "2026-01-27T17:13:53.113806034",
    "Status": 404,
    "error": "Not found",
    "message": "Task not found",
    "path": "/api/tasks/999"
}
```

### GET /api/tasks/{status}

Gets a paginated list with tasks filtered by their status

``` sh
curl localhost:8080/api/tasks/IN_PROGRESS
```

* **Expected Status Code:** 200 OK if status is valid, 500 otherwise

* **Response Example:**

**Valid Status:**

``` json
{
    "content": [
        {
            "id": 2,
            "title": "Task 2",
            "description": "Implement feature",
            "status": "PENDING",
            "assignedTo": 3,
            "createdAt": "2026-01-22T15:50:25.082333",
            "updatedAt": "2026-01-22T15:50:25.082352"
        },
        {
            "id": 3,
            "title": "Test feature",
            "description": "Bug Fix",
            "status": "PENDING",
            "assignedTo": 5,
            "createdAt": "2026-01-22T15:50:25.088448",
            "updatedAt": "2026-01-22T15:50:25.088468"
        },
        {
            "id": 4,
            "title": "Task 4",
            "description": "Refactor function",
            "status": "PENDING",
            "assignedTo": 7,
            "createdAt": "2026-01-22T15:50:25.094202",
            "updatedAt": "2026-01-22T15:50:25.09424"
        },
        {
            "id": 6,
            "title": "Task 5",
            "description": "POST test",
            "status": "PENDING",
            "assignedTo": 3,
            "createdAt": "2026-01-26T13:58:53.438395",
            "updatedAt": "2026-01-26T13:58:53.438691"
        },
        {
            "id": 8,
            "title": "Task INVALID_STATUS",
            "description": "POST test",
            "status": "PENDING",
            "assignedTo": 3,
            "createdAt": "2026-01-26T15:28:44.640006",
            "updatedAt": "2026-01-26T15:28:44.640201"
        }
    ],
    "empty": false,
    "first": true,
    "last": true,
    "number": 0,
    "numberOfElements": 5,
    "pageable": {
        "offset": 0,
        "pageNumber": 0,
        "pageSize": 10,
        "paged": true,
        "sort": {
            "empty": true,
            "sorted": false,
            "unsorted": true
        },
        "unpaged": false
    },
    "size": 10,
    "sort": {
        "empty": true,
        "sorted": false,
        "unsorted": true
    },
    "totalElements": 5,
    "totalPages": 1
}
```

**Invalid Status:**

``` json
{
    "timestamp": "2026-01-27T17:22:22.823128557",
    "Status": 500,
    "error": "Internal Server Error",
    "message": "Error processing request",
    "path": "/api/tasks/status/LOREM"
}
```

### PUT /api/tasks/{id}

Updates an existing task with new info

``` sh
curl \
-X PUT \
-d '{ \
    "title": "Updated title", \
    "description": "Updated description", \
    "status": "IN_PROGRESS", \
    "assignedTo": 1 \
}'
localhost:8080/api/tasks/1
```

* **Expected Status Code:** 200 if successful, 404 if task id was not found, 500 if task info is invalid

* **Response Example:**

**Valid Task**

``` json
{
    "id": 1,
    "title": "Updated Task 1",
    "description": "PUT Test",
    "status": "IN_PROGRESS",
    "assignedTo": 3,
    "createdAt": "2026-01-22T15:50:25.054924",
    "updatedAt": "2026-01-27T18:02:22.678179"
}
```

**ID not found:**

``` json
{
    "timestamp": "2026-01-27T18:03:56.369165799",
    "Status": 404,
    "error": "Not found",
    "message": "Task not found",
    "path": "/api/tasks/999"
}
```

**Invalid Task Info**

``` json
{
    "timestamp": "2026-01-27T18:04:16.78184996",
    "Status": 500,
    "error": "Internal Server Error",
    "message": "Error processing request",
    "path": "/api/tasks/1"
}
```

### DELETE /api/tasks/{id}

Deletes a single task with a certain id

``` sh
curl -x DELETE localhost:8080/api/tasks/1
```

* **Expected Status Code:** 204 if successful, 404 if task id was not found

* **Response Example:**

**Valid Task**: Empty response

**ID not found:**

``` json
{
    "timestamp": "2026-01-27T18:06:31.704437849",
    "Status": 404,
    "error": "Not found",
    "message": "Task not found",
    "path": "/api/tasks/999"
}
```

**Invalid Task Info**

``` json
{
    "timestamp": "2026-01-27T18:04:16.78184996",
    "Status": 500,
    "error": "Internal Server Error",
    "message": "Error processing request",
    "path": "/api/tasks/1"
}
```
