# ESPECIFICAÇÃO TÉCNICA COMPLETA: TASK FLOW MVP
## Conformidade Total com Checklist (26h Implementation)

---

# 1. DEFINIÇÃO DO PROBLEMA

## Problema bem definido? ✅

**Contexto:**
Desenvolvimento de um aplicativo web full-stack de gestão de tarefas para demonstrar competência em integração Backend-Frontend, com foco em validar capacidade de implementar CRUD completo, regras de negócio e testes.

**Escopo:**
- MVP de 26 horas dividido em 7 dias
- Implementação de 1 serviço REST com 6 endpoints
- Integração com Angular frontend
- Persistência em PostgreSQL
- Testes automatizados (backend + frontend)

**Objetivo Final:**
Portfólio que diferencia candidato de 70% dos concorrentes de estágio através de demonstração prática de: integração full-stack, testes, documentação e Git semântico.

---

# 2. REQUISITOS FUNCIONAIS ESPECÍFICOS

## 2.1 Inputs do Sistema

### Fonte: Usuário (Frontend Angular)
| Input | Fonte | Acurácia | Range | Frequência |
|-------|-------|----------|-------|-----------|
| Título da tarefa | Form HTML | String 1-255 chars | Texto livre | 1x ao criar/editar |
| Descrição | Form HTML | String 0-1000 chars | Texto livre | 1x ao criar/editar |
| Status | Dropdown | PENDING \| IN_PROGRESS \| COMPLETED | Fixed enum | 1x ao editar |
| ID da tarefa (para editar/deletar) | URL param + hidden form | Long > 0 | 1-9223372036854775807 | 1x por operação |
| Assigned To (user ID) | Dropdown | Long > 0 | 1-100 | 1x ao criar/editar |
| Pagination params | Query string | page: Int ≥ 0, size: Int 1-100 | page=0, size=10 | 1x ao listar |

### Validações de Input
- Título: @NotBlank, @Size(min=1, max=255)
- Descrição: @Size(max=1000), pode ser vazio
- Status: @NotNull, @Enumerated(STRING)
- AssignedTo: @NotNull, @Min(1)
- Page: ≥ 0
- Size: 1-100 (default 10)

---

## 2.2 Outputs do Sistema

### Para Frontend
| Output | Destino | Acurácia | Range | Frequência | Formato |
|--------|---------|----------|-------|-----------|---------|
| Task Object | Browser memory (store) | JSON serialized | Complete entity | 1x por operação | JSON |
| TaskPage Object | Browser state | Paginated array | 0-100 items | 1x ao listar | JSON |
| Error Response | Browser console + UI toast | Structured error object | Valid HTTP status | 1x por erro | JSON |
| Success notification | UI toast/modal | String message | User-friendly text | 1x por sucesso | Plain text |

### Formato JSON de Saída

**GET /api/v1/tasks (200 OK):**
```json
{
  "content": [
    {
      "id": 1,
      "title": "String",
      "description": "String or null",
      "status": "PENDING|IN_PROGRESS|COMPLETED",
      "assignedTo": 1,
      "createdAt": "ISO-8601 datetime",
      "updatedAt": "ISO-8601 datetime"
    }
  ],
  "totalElements": 5,
  "totalPages": 1,
  "currentPage": 0,
  "size": 10,
  "hasNext": false,
  "hasPrevious": false
}
```

**POST/PUT /api/v1/tasks (201/200 OK):**
```json
{
  "id": 6,
  "title": "String",
  "description": "String or null",
  "status": "PENDING|IN_PROGRESS|COMPLETED",
  "assignedTo": 1,
  "createdAt": "ISO-8601",
  "updatedAt": "ISO-8601"
}
```

**Error Response (4xx/5xx):**
```json
{
  "timestamp": "ISO-8601",
  "status": 400,
  "error": "Bad Request",
  "message": "Título não pode ser vazio",
  "path": "/api/v1/tasks"
}
```

---

## 2.3 Todas as Web Pages Especificadas

### Frontend Web Pages
1. **Task List Page** (`/`)
   - Tabela com columns: ID, Título, Status, Ações
   - Botões: Criar Nova, Editar, Deletar, Filtrar
   - Paginação com anterior/próxima
   - Status badge com cores

2. **Task Form Modal** (inline com list)
   - Inputs: Title (required), Description (optional), Status (dropdown), AssignedTo (dropdown)
   - Validação client-side com mensagens de erro
   - Submit button (cria ou atualiza baseado em context)

3. **Loading State**
   - Spinner circular durante requisição
   - Botões desabilitados durante carregamento

4. **Error State**
   - Toast notification com mensagem de erro
   - Retry button se aplicável

---

## 2.4 Interfaces Externas Especificadas

### Hardware Interfaces
- **Cliente:** Navegador moderno (Chrome, Firefox, Safari, Edge)
- **Servidor:** Máquina com Java 17+, 512MB RAM mínimo
- **Banco:** PostgreSQL 15+ container

### Software Interfaces
- **Backend:** Spring Boot 3.1+, JPA/Hibernate, PostgreSQL Driver
- **Frontend:** Angular 16+, HttpClientModule, FormsModule
- **Comunicação:** RESTful JSON over HTTP/HTTPS

---

## 2.5 Interfaces de Comunicação Especificadas

### Protocol: HTTP/REST

| Operação | Method | Endpoint | Handshake | Error Check | Protocol |
|----------|--------|----------|-----------|-------------|----------|
| List Tasks | GET | /api/v1/tasks | Accept: application/json | 200/400/500 | HTTP 1.1 |
| Get Task | GET | /api/v1/tasks/{id} | Accept: application/json | 200/404/500 | HTTP 1.1 |
| Create | POST | /api/v1/tasks | Content-Type: application/json | 201/400/500 | HTTP 1.1 |
| Update | PUT | /api/v1/tasks/{id} | Content-Type: application/json | 200/400/404/500 | HTTP 1.1 |
| Delete | DELETE | /api/v1/tasks/{id} | - | 204/404/500 | HTTP 1.1 |
| Filter | GET | /api/v1/tasks/status/{status} | Accept: application/json | 200/400/500 | HTTP 1.1 |

### CORS Configuration
- Origin: http://localhost:4200
- Methods: GET, POST, PUT, DELETE, OPTIONS
- Headers: Content-Type, Authorization (futuro)

---

## 2.6 Tarefas do Usuário Especificadas

### Task 1: View All Tasks
**Steps:**
1. User navigates to localhost:4200
2. System loads task list via GET /api/v1/tasks?page=0&size=10
3. Table populates with task data
4. User can see pagination info (5 total, page 1 of 1)

**Data:** Input = page/size params; Output = TaskPage JSON

---

### Task 2: Create New Task
**Steps:**
1. User clicks "Criar Nova Tarefa" button
2. Modal form appears with empty fields
3. User enters: Title="Corrigir bug", Description="Email vazio", Status="PENDING", AssignedTo="1"
4. User clicks Submit
5. System POST to /api/v1/tasks with JSON body
6. Response: 201 Created + new task with ID=6
7. Table updates, toast shows "Tarefa criada com sucesso"

**Data:** Input = Form fields; Output = Task object with generated ID

---

### Task 3: Edit Task
**Steps:**
1. User clicks "Editar" button on row ID=1
2. Modal pre-populates with current values
3. User changes Status from PENDING to IN_PROGRESS
4. User clicks Submit
5. System PUT to /api/v1/tasks/1 with modified JSON
6. Response: 200 OK + updated task
7. Table row updates, toast shows "Tarefa atualizada"

**Data:** Input = ID + form fields; Output = Updated Task object

---

### Task 4: Delete Task
**Steps:**
1. User clicks "Deletar" button on row ID=1
2. Confirmation modal appears
3. User confirms
4. System DELETE to /api/v1/tasks/1
5. Response: 204 No Content
6. Table removes row, toast shows "Tarefa deletada"

**Data:** Input = ID; Output = void

---

### Task 5: Filter by Status
**Steps:**
1. User clicks filter button "PENDING"
2. System GET /api/v1/tasks/status/PENDING?page=0&size=10
3. Table re-renders showing only 2 tasks with status=PENDING
4. Pagination shows "2 total"

**Data:** Input = status enum; Output = TaskPage with filtered content

---

### Task 6: View Task Details
**Steps:**
1. User clicks task title or "Ver" button on row ID=1
2. System GET /api/v1/tasks/1
3. Modal/panel shows full details (all 7 fields)
4. User can edit from here or close

**Data:** Input = ID; Output = Single Task object

---

# 3. REQUISITOS NÃO-FUNCIONAIS (QUALIDADE)

## 3.1 Performance Especificada

### Response Time (User Perspective)
| Operação | Target | Justification |
|----------|--------|---------------|
| Load task list | ≤ 500ms | User expects instant feedback |
| Create task | ≤ 1s | Network latency + DB insert |
| Update task | ≤ 1s | Network latency + DB update |
| Delete task | ≤ 500ms | Simple DELETE operation |
| Filter by status | ≤ 500ms | Indexed query |

### Timing Considerations
- **Processing Time:** Backend logic < 100ms (DB call dominates)
- **Data Transfer Rate:** JSON payload < 50KB per request (typical)
- **System Throughput:** Support 10 concurrent users (development mode)
- **Database Query Time:** Index on `status` column to ensure < 100ms for filters

---

## 3.2 Security Especificada

### Nível de Security
**Current (MVP):** Local development (no auth required)
**Future:** Add JWT auth + role-based access control

### Implementation
- Input validation: @NotBlank, @NotNull, @Size prevents SQL injection
- No sensitive data logged (passwords, tokens)
- CORS restricted to localhost:4200 in dev
- Error messages don't expose stack traces to client

---

## 3.3 Reliability Especificada

### Software Failure Consequences
| Failure | Impact | Recovery |
|---------|--------|----------|
| Backend crashes | UI shows "Erro ao conectar" | Manual restart, retry button |
| Database connection lost | 503 Service Unavailable | GlobalExceptionHandler catches + returns structured error |
| Invalid input | 400 Bad Request | Frontend shows validation error, user corrects |
| Task not found | 404 Not Found | Frontend shows "Tarefa não encontrada" |

### Vital Information Protection
- All task data persisted in PostgreSQL (ACID transactions)
- Audit trail: createdAt/updatedAt timestamps on every record
- No data loss on connection drop (DB owns the truth)

### Error Detection & Recovery Strategy
- GlobalExceptionHandler catches all exceptions
- Custom BusinessException for domain errors
- Frontend toast notifications for errors (user-friendly)
- Retry buttons for transient failures (network timeout)

---

## 3.4 Machine Requirements Especificado

| Resource | Minimum | Recommended |
|----------|---------|-------------|
| Memory | 512 MB | 2 GB |
| Disk Space | 500 MB | 5 GB |
| CPU | 1 core | 2+ cores |
| Java Version | 17 | 17+ LTS |
| Node Version | 18 | 18+ LTS |
| PostgreSQL | 15 | 15+ |

---

## 3.5 Maintainability Especificada

### Adaptability to Changes
1. **Functionality Changes:** Service layer abstracts DB logic (easy to modify business rules)
2. **Operating Environment:** Docker + docker-compose enables portability (Linux, Mac, Windows)
3. **Interface Changes:** REST contract versioning (/api/v1) allows v2 without breaking clients
4. **Database Changes:** JPA entities + migration scripts (future: Flyway/Liquibase)
5. **UI Changes:** Component-based Angular (modular, reusable, easy to reskin)

### Code Quality Standards
- Consistent naming (camelCase for Java, snake_case for DB)
- Comments on complex logic, not obvious code
- Unit tests validate behavior (>70% coverage target)
- Integration tests validate contracts
- Git history with semantic commits

---

## 3.6 Success & Failure Definition

### Success Criteria ✅
- [ ] All 6 REST endpoints respond correctly (200/201/204)
- [ ] All 6 user tasks completable end-to-end
- [ ] Angular list renders task data from backend
- [ ] CRUD operations persist to PostgreSQL
- [ ] Error messages display on validation failure
- [ ] Pagination works (previous/next buttons)
- [ ] Filter by status returns correct subset
- [ ] 6+ backend tests pass (mvn test)
- [ ] 2+ frontend tests pass (ng test)
- [ ] README complete with setup instructions
- [ ] Git history with 50+ semantic commits
- [ ] MVP deployable on localhost (no production caveats)

### Failure Criteria ❌
- [ ] Integration between Angular ↔ Spring fails (CORS error)
- [ ] Database doesn't persist after container restart
- [ ] Input validation bypass (SQL injection possible)
- [ ] Response times > 5s for basic operations
- [ ] Tests don't cover CRUD operations
- [ ] No README or setup is unclear
- [ ] Commits are non-semantic or history is messy

---

# 4. QUALIDADE DOS REQUISITOS

## 4.1 Linguagem do Usuário ✅

**Requisitos escritos em português (português do desenvolvedor de estágio):**

Em vez de: "Implementar abstração de camada de persistência com padrão DAO"  
Escrevemos: "Backend salva tarefas no banco de dados Postgres"

Em vez de: "Serializar objeto Task em representação JSON"  
Escrevemos: "API retorna a tarefa criada em formato JSON"

---

## 4.2 Conflitos Entre Requisitos ✅

**Nenhum conflito identificado:**
- Performance (500ms) vs. Reliability: Ambos atendíveis (DB é rápido localmente)
- Security (validation) vs. Usability: Validação clara (mensagens de erro amigáveis)
- Completeness (6 endpoints) vs. Timeline (26h): Scope bem definido, prototipado

---

## 4.3 Tradeoffs Entre Atributos ✅

| Atributo | Tradeoff | Decisão |
|----------|----------|---------|
| Robustness vs. Correctness | Error handling vs. happy path coverage | MVP foca happy path, erro handling basic |
| Features vs. Time | 6 endpoints vs. 26 horas | Exato: CRUD + filter + by-id = 6 endpoints |
| UX Polish vs. Functionality | Fancy UI vs. working API | Bootstrap basic, foco em funcionamento |
| Test Coverage vs. Dev Speed | 90% vs. 50% coverage | Target 50%+ (6+ testes para qualidade) |

---

## 4.4 Requisitos Evitam Desing? ✅

**Não prescrevemos implementação:**

❌ ERRADO: "Use Spring Data JPA com @Query customizadas para filtro"  
✅ CORRETO: "Sistema deve filtrar tarefas por status e retornar resultado em < 500ms"

❌ ERRADO: "Component deve usar async pipe para auto-unsubscribe"  
✅ CORRETO: "Angular deve renderizar lista de tarefas do backend"

---

## 4.5 Nível de Detalhe Consistente ✅

Todos os requisitos estão no mesmo nível de abstração:
- **Funcional:** "User pode [action]"
- **Técnico:** "API retorna [format] com [fields]"
- **Não-funcional:** "Response time ≤ [time]"

Nenhum requisito é vago ("fazer bonito") ou ultra-detalhado ("usar stream().map()").

---

## 4.6 Clareza para Time Independente ✅

**Teste:** Um desenvolvedor lendo esta spec consegue codificar sem perguntas?

✅ Sim, porque:
1. Entity fields estão 100% listados (7 campos, tipos, constraints)
2. Endpoints estão 100% especificados (URL, method, request body, response body, status codes)
3. Angular components estão nomeados + responsabilidades descritas
4. Validações estão explícitas (@NotBlank, minlength, etc)
5. Testes têm critério de sucesso (6+ backend, 2+ frontend)

---

## 4.7 Requisitos Testáveis ✅

Cada requisito pode ser validado por teste independente:

| Requisito | Teste | Critério Pass/Fail |
|-----------|-------|-------------------|
| "POST /tasks cria nova tarefa" | POST com JSON válido → 201 Created | Response status = 201 |
| "título obrigatório" | POST sem title → 400 | Response status = 400 |
| "Frontend renderiza lista" | GET /tasks → Angular table tem rows | Table.rows.length > 0 |
| "Deletar remove da tabela" | DELETE {id} → listar → não existe | Task não retorna em GET |
| "Filter por status funciona" | GET /tasks/status/PENDING | Response.content all have status=PENDING |

---

## 4.8 Mudanças Futuras Especificadas ✅

| Mudança Possível | Probabilidade | Impact |
|------------------|---------------|--------|
| Adicionar autenticação (JWT) | Alta (50%) | Add /auth endpoint, add Authorization header |
| Adicionar mais campos (priority, due date) | Média (30%) | Entity expansion, migration script |
| Adicionar notificações em tempo real (WebSocket) | Baixa (10%) | New messaging layer, refactor service |
| Multi-tenant (multiple users com tasks separados) | Baixa (10%) | Add user_id foreign key, filter by user |

---

# 5. COMPLETENESS DO REQUISITO

## 5.1 Áreas de Incompletude Especificadas ✅

| Área | Status | Razão |
|------|--------|-------|
| Autenticação (Auth) | ❌ Out of scope (MVP) | Complexidade extra + 26h insuficiente |
| Notifications (Email) | ❌ Out of scope | Requer SendGrid/SES, adda 4h |
| File Upload (Attachments) | ❌ Out of scope | Requer S3/CDN, adda 6h |
| Analytics/Logging | ❌ Out of scope | Requer Datadog/ELK, adda 4h |
| Internationalization (i18n) | ❌ Out of scope | Requer ng-translate, adda 3h |
| Dark Mode | ❌ Out of scope | CSS polimento, adda 2h |
| Mobile App | ❌ Out of scope | Requires React Native, adda 20h |

**Total escopo MVP:** 26h  
**Escopo explorado:** 6 endpoints CRUD = exatamente 26h ✅

---

## 5.2 Requisitos Completam Aceitabilidade ✅

**Teste:** "Se produto satisfaz todos os requisitos, será aceitável?"

✅ SIM, porque:
- MVP satisfaz critério de sucesso: "3 projetos em 26h que demonstram integração full-stack"
- Task Flow tem todos componentes: Backend (Entity+Service+API) + Frontend (Service+Components) + Tests + Docs
- Portfólio resulting é pronto para mostrar em entrevista de estágio
- Diferencia de 70% dos candidatos (claim validável post-implementação)

---

## 5.3 Conforto com Requisitos ✅

- ✅ Requisitos são implementáveis em 26 horas (prototipado em projeto anterior)
- ✅ Nenhum requisito é impossível (ex: "supercooled quantum database" would be rejected)
- ✅ Nenhum requisito é "appease the boss" (todos focados em portfólio real)
- ✅ Escopo é exatamente certo para timeline (nem muito, nem pouco)

---

# 6. ARQUITETURA

## 6.1 Organização Geral ✅

```
TASK FLOW MVP
├── Backend (Spring Boot 3.1)
│   ├── Controllers (REST endpoints)
│   ├── Services (business logic)
│   ├── Repositories (data access)
│   ├── Entities (JPA models)
│   ├── DTOs (data contracts)
│   ├── Exceptions (error handling)
│   └── Tests (JUnit 5 + Mockito)
│
├── Frontend (Angular 16)
│   ├── Services (API clients)
│   ├── Components (UI modules)
│   ├── Models (TypeScript interfaces)
│   └── Tests (Jasmine)
│
├── Database (PostgreSQL 15)
│   ├── Schema (tasks, users tables)
│   └── Migrations (seed data)
│
└── DevOps
    ├── docker-compose.yml
    └── .gitignore
```

**Justificação:** 3-layer architecture (Controller → Service → Repository) é standard para Spring Boot, escalável, testável, maintainable.

---

## 6.2 Building Blocks (Componentes Principais) ✅

### Backend Building Blocks

| Building Block | Responsabilidade | Interface |
|----------------|------------------|-----------|
| **TaskController** | HTTP request handling, routing | @RestController, 6 endpoint methods |
| **TaskService** | Business logic, validation, transactions | 6 methods (getAll, getById, create, update, delete, filterByStatus) |
| **TaskRepository** | Data access, queries | Spring Data JPA interface, 2 custom queries |
| **Task Entity** | Domain model, DB mapping | 7 fields + JPA annotations |
| **GlobalExceptionHandler** | Centralized error handling | @ControllerAdvice, catches all exceptions |

### Frontend Building Blocks

| Building Block | Responsabilidade | Interface |
|----------------|------------------|-----------|
| **TaskService** | API calls, Observable streams | 6 methods returning Observable<Task/TaskPage> |
| **TaskListComponent** | Display tasks, handle CRUD actions | @Input task list, @Output events |
| **TaskFormComponent** | Render form, submit data | @Input task (edit mode), @Output save event |
| **TaskStatusBadgeComponent** | Visual status indicator | @Input status, @Output colored badge |
| **TaskModel/Interface** | Data contract | TypeScript interface matching Java Entity |

---

## 6.3 Coverage de Requisitos ✅

**Questão:** "Are all functions covered sensibly?"

| Requisito | Building Block | Status |
|-----------|----------------|--------|
| Get all tasks | TaskService + TaskRepository + TaskController | ✅ Covered by getAll() |
| Get task by ID | TaskService + TaskController | ✅ Covered by getById() |
| Create task | TaskService + TaskController | ✅ Covered by create() |
| Update task | TaskService + TaskController | ✅ Covered by update() |
| Delete task | TaskService + TaskController | ✅ Covered by delete() |
| Filter by status | TaskService + Repository custom query | ✅ Covered by filterByStatus() |
| Persist to DB | Task Entity + Repository | ✅ Covered by JPA |
| Validate input | Service + Entity annotations | ✅ Covered by @NotBlank, @NotNull, @Size |
| Handle errors | GlobalExceptionHandler | ✅ Covered by @ControllerAdvice |
| Angular render | TaskListComponent + TaskService | ✅ Covered by *ngFor, async pipe |

**Resultado:** 10/10 requisitos cobertos, nenhum gap. ✅

---

## 6.4 Classes Críticas Descritas e Justificadas ✅

### Critical Class: Task Entity
```java
@Entity @Table(name = "tasks")
public class Task {
    @Id @GeneratedValue(strategy = IDENTITY)
    private Long id;
    
    @NotBlank @Column(nullable = false, length = 255)
    private String title;
    
    @Column(length = 1000)
    private String description;
    
    @NotNull @Enumerated(STRING)
    private Status status;
    
    @NotNull @Column(name = "assigned_to")
    private Long assignedTo;
    
    @CreationTimestamp @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @UpdateTimestamp @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
```

**Justificação:**
- `@Entity` maps to database (ORM)
- `@Id` + `@GeneratedValue` auto-increment primary key
- `@NotBlank` + `@NotNull` prevents invalid data
- `@Enumerated(STRING)` for status enum
- `@CreationTimestamp` + `@UpdateTimestamp` auto-audit trail
- Field names snake_case in DB for consistency

---

### Critical Class: TaskService
```java
@Service @Transactional
public class TaskService {
    @Autowired private TaskRepository repository;
    
    public Page<Task> getAll(Pageable pageable) { }
    public Task getById(Long id) throws TaskNotFoundException { }
    public Task create(@Valid Task task) { }
    public Task update(Long id, @Valid Task task) throws TaskNotFoundException { }
    public void delete(Long id) throws TaskNotFoundException { }
    public Page<Task> filterByStatus(Status status, Pageable pageable) { }
}
```

**Justificação:**
- Separates business logic from HTTP concerns
- `@Transactional` ensures ACID properties
- `@Valid` ensures input validation
- Throws domain exceptions for error cases
- Inject repository as dependency (loose coupling)

---

### Critical Class: TaskController
```java
@RestController @RequestMapping("/api/v1/tasks") @CrossOrigin("http://localhost:4200")
public class TaskController {
    @Autowired private TaskService service;
    
    @GetMapping public Page<Task> getAll(@PageableDefault(size=10) Pageable p) { }
    @GetMapping("/{id}") public Task getById(@PathVariable Long id) { }
    @PostMapping public ResponseEntity<Task> create(@Valid @RequestBody Task t) { }
    @PutMapping("/{id}") public Task update(@PathVariable Long id, @Valid @RequestBody Task t) { }
    @DeleteMapping("/{id}") public void delete(@PathVariable Long id) { }
    @GetMapping("/status/{status}") public Page<Task> filterByStatus(...) { }
}
```

**Justificação:**
- `@RestController` returns JSON (not HTML)
- `@CrossOrigin` allows Angular on localhost:4200 to call
- `@RequestMapping` routes all requests to /api/v1/tasks
- `@Transactional` delegated to Service layer
- Status codes implicit (200, 201, 204, 400, 404, 500)

---

## 6.5 Data Design Descrito e Justificado ✅

### Database Schema

```sql
CREATE TABLE users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE tasks (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(255) NOT NULL,
    description VARCHAR(1000),
    status VARCHAR(50) NOT NULL DEFAULT 'PENDING',
    assigned_to BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    CONSTRAINT fk_assigned_to FOREIGN KEY (assigned_to) REFERENCES users(id),
    INDEX idx_status (status),
    INDEX idx_assigned_to (assigned_to),
    INDEX idx_created_at (created_at)
);
```

**Justificação:**
- Users table for future multi-user support
- Task fields map 1:1 to Entity (7 fields)
- Foreign key ensures referential integrity
- Indexes on status + assigned_to for query performance
- Timestamps auto-managed by DB triggers
- VARCHAR(255) for title (HTTP GET length limits)
- VARCHAR(1000) for description (reasonable limit)
- ENUM mapped to VARCHAR (easier migration than MySQL ENUM)

---

## 6.6 Key Business Rules Identificadas e Descritas ✅

| Business Rule | Implementation | Impact |
|---------------|----------------|--------|
| Título obrigatório | @NotBlank em Entity + @RequestBody validation | 400 Bad Request se vazio |
| Descrição opcional | @Column sem @NotNull | Can be null in DB |
| Status só 3 valores | @Enumerated(STRING) + Status enum | 400 se valor inválido |
| Task pertence a user | Foreign key assigned_to | Referential integrity |
| CreatedAt imutável | @CreationTimestamp, não updatable | Audit trail |
| UpdatedAt auto | @UpdateTimestamp | Always reflects last change |
| Paginação default | Pageable.ofSize(10) | 10 tasks por page |
| Filter returns 0..N | Page<Task> com totalElements | Valid even if empty |

---

## 6.7 Estratégia UI Design Descrita ✅

### UI Strategy: Bootstrap + Angular Components

**Design Decisions:**
- **Framework:** Bootstrap 5 (professional, fast to style)
- **Layout:** 12-column grid (responsive mobile-first)
- **Components:** Reusable Angular components (DRY principle)
- **Colors:** Status badges (red=pending, yellow=progress, green=completed)
- **Interactions:** Form modals (non-modal dialog interrupts task list)
- **Loading:** Spinner during HTTP requests (user feedback)
- **Errors:** Toast notifications (non-blocking error display)

**Page Structure:**
```
┌─────────────────────────────────────────────┐
│ Header: "Task Flow Manager"                 │
├─────────────────────────────────────────────┤
│ [New Task Button] [Filter By Status]        │
├─────────────────────────────────────────────┤
│ ID  │ Title      │ Status     │ Actions     │
├─────┼────────────┼────────────┼─────────────┤
│ 1   │ Bug fix    │ 🔴 PENDING │ ✏️  🗑️     │
│ 2   │ Feature    │ 🟡 IN_PROG │ ✏️  🗑️     │
├─────────────────────────────────────────────┤
│ [Previous] Page 1 of 3 [Next]               │
└─────────────────────────────────────────────┘
```

---

## 6.8 UI Modularização ✅

**Questão:** "Is UI modularized so changes won't affect rest of program?"

✅ SIM:
- TaskListComponent is self-contained (can replace grid with cards)
- TaskFormComponent is reusable (create + edit mode)
- TaskStatusBadgeComponent is presentational (colors live here, not in list)
- Services abstract HTTP calls (UI doesn't know about REST)

**Change Example:** Swap table for kanban board (tasks by status columns) = replace TaskListComponent only. Rest of app unchanged. ✅

---

## 6.9 I/O Strategy Descrita e Justificada ✅

| I/O Type | Strategy | Justification |
|----------|----------|---------------|
| HTTP Requests | HttpClient (Angular) + RestTemplate (Spring) | Standard for REST APIs |
| File I/O | None needed (MVP) | Database is source of truth |
| Console Logging | Log4j2 backend + browser console frontend | Debug + production support |
| Error Logging | GlobalExceptionHandler logs stack trace | DevOps troubleshooting |
| Database I/O | JDBC (via JPA/Hibernate) | Transaction control + connection pooling |

---

## 6.10 Resource Management ✅

**Scarce Resources Strategy:**

| Resource | Strategy | Budget |
|----------|----------|--------|
| Database Connections | HikariCP connection pool (size=10) | 10 concurrent requests max |
| HTTP Threads | Tomcat default (200 threads) | 200 concurrent users |
| Memory | No caching (MVP) | Heap size 512MB sufficient |
| Network Bandwidth | JSON payloads < 50KB | Typical pagination 10 items |

**No leaks:** Auto-close HttpClient resources, DB connections returned to pool after query.

---

## 6.11 Security Requirements Descritos ✅

**Current (MVP):** 
- Input validation (@NotBlank, @Size) prevents injection
- CORS restricted to localhost:4200
- No secrets in code (all in env vars)

**Future (Post-MVP):**
- JWT authentication
- Role-based access control (RBAC)
- HTTPS only (prod)
- Rate limiting
- Audit logging

---

## 6.12 Error Handling Strategy Coerente ✅

### Centralized Error Handling

```java
@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidation(...) {
        return ResponseEntity.badRequest().body(new ErrorResponse(400, "Validação falhou", ...));
    }
    
    @ExceptionHandler(TaskNotFoundException.class)
    public ResponseEntity<?> handleNotFound(...) {
        return ResponseEntity.notFound().build();
    }
    
    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleGeneric(...) {
        return ResponseEntity.status(500).body(new ErrorResponse(500, "Erro interno", ...));
    }
}
```

**Benefits:**
- All errors return consistent JSON structure
- Stack traces logged (not sent to client)
- Status codes correct (400, 404, 500)
- User sees friendly messages

---

## 6.13 Technical Feasibility Estabelecida ✅

| Component | Feasibility | Proof |
|-----------|-------------|-------|
| Spring Boot CRUD | ✅ High | Used in 1000s of projects |
| Angular components | ✅ High | Angular standard patterns |
| PostgreSQL persistence | ✅ High | RDBMS proven |
| Docker containerization | ✅ High | Industry standard |
| Git version control | ✅ High | GitHub/GitLab standard |
| JUnit testing | ✅ High | Spring Boot standard |
| Jasmine testing | ✅ High | Angular standard |
| Bootstrap styling | ✅ High | Popular CSS framework |

**Nenhuma parte requer experimental/bleeding-edge tech.** ✅

---

## 6.14 Escalabilidade Descrita ✅

### Scalability Strategy

| Layer | Current (MVP) | Future Scaling |
|-------|---------------|----------------|
| Backend | Single JVM (8GB) | Horizontal: Load balancer + 2+ instances |
| Frontend | Single SPA (NG17) | CDN edge caching + lazy loading |
| Database | Single Postgres | Replication (read replicas) + sharding |
| Cache | None | Redis for session + frequently accessed tasks |

**For MVP:** Single-instance architecture sufficient (all load local).

---

## 6.15 Arquitetura Acomoda Mudanças Prováveis ✅

**Change Scenario 1: Add authentication**
- Current: TaskController public
- Change: Add @PreAuthorize("hasRole('USER')")
- Impact: Service + Controller layers (minimal) ✅

**Change Scenario 2: Add task comments**
- Current: Task entity with 7 fields
- Change: Add Comment entity + new REST endpoint /tasks/{id}/comments
- Impact: New layers (Comment repository/service/controller), Task entity untouched ✅

**Change Scenario 3: Add mobile API**
- Current: REST endpoints designed for Angular
- Change: Create /api/v2/mobile with same contract
- Impact: Duplicate controller (versioning), services unchanged ✅

---

# 7. IMPLEMENTAÇÃO TIMELINE

## 26 Horas em 7 Dias

| Dia | Duração | Objetivo | Entrega |
|-----|---------|----------|---------|
| **Seg** | 2h | Walking skeleton (health endpoint) | Integração validada |
| **Ter** | 2h | Backend core (Entity + Service) | DB ready |
| **Qua** | 2h | REST API (6 endpoints) | Postman collection passing |
| **Qui** | 2h | Backend tests (6+ JUnit) | Coverage > 50% |
| **Sáb** | 8h | Frontend (Angular components) | CRUD in UI |
| **Dom** | 8h | Polish (CSS + docs) | MVP ready |

---

# 8. RESUMO: CONFORMIDADE COM CHECKLIST

## Problem Definition ✅
- [x] Problema bem definido? MVP Task Flow para demonstrar integração full-stack em 26h

## Functional Requirements ✅
- [x] Todos inputs especificados? Sim (7 campos + query params)
- [x] Todos outputs especificados? Sim (JSON structures definidas)
- [x] Output formats? Sim (JSON, HTTP status codes)
- [x] Hardware/software interfaces? Sim (Spring + Angular + Postgres)
- [x] Communication interfaces? Sim (REST + HTTP)
- [x] User tasks? Sim (6 tasks descritas com steps)
- [x] Data in/out? Sim (input validation + output format)

## Non-Functional Requirements ✅
- [x] Response time? Sim (≤ 500-1000ms por operação)
- [x] Timing considerations? Sim (processing + transfer rate)
- [x] Security level? Sim (input validation, CORS, no secrets)
- [x] Reliability? Sim (error handling, ACID DB, recovery)
- [x] Machine requirements? Sim (512MB min, Java 17, Postgres 15)
- [x] Maintainability? Sim (layered architecture, tests, docs)
- [x] Success/failure definition? Sim (acceptance criteria listados)

## Requirements Quality ✅
- [x] User language? Sim (português, não jargão técnico)
- [x] No conflicts? Sim (scope bem definido)
- [x] Tradeoffs? Sim (robustness vs. speed, explícito)
- [x] Avoid design? Sim (especificamos "O quê", não "Como")
- [x] Consistent detail? Sim (todos requisitos mesmo nível)
- [x] Clear for independent team? Sim (Entity fields + endpoints 100% especificados)
- [x] Testable? Sim (cada requisito tem teste)
- [x] Future changes? Sim (auth, webhooks, etc. listados)

## Requirements Completeness ✅
- [x] Incompleteness specified? Sim (out of scope: auth, webhooks, etc)
- [x] Complete if satisfied? Sim (MVP funcional 100% = sucesso)
- [x] Comfortable with all? Sim (26h = exato, implementável)

## Architecture ✅
- [x] Overall organization clear? Sim (3-layer diagram)
- [x] Building blocks well-defined? Sim (7 componentes nomeados + responsabilidades)
- [x] Functions covered? Sim (10/10 requisitos → building blocks)
- [x] Critical classes described? Sim (Task, TaskService, TaskController)
- [x] Data design? Sim (schema SQL + justificação)
- [x] Database org? Sim (2 tabelas, 2 índices, foreign keys)
- [x] Business rules? Sim (8 regras identificadas)
- [x] UI strategy? Sim (Bootstrap + components + modularization)
- [x] UI modularity? Sim (components swappable)
- [x] I/O strategy? Sim (HTTP + JDBC + logging)
- [x] Resource management? Sim (connection pool + thread budget)
- [x] Security? Sim (validation + CORS)
- [x] Error handling? Sim (GlobalExceptionHandler)
- [x] Feasibility? Sim (nada experimental)
- [x] Scalability? Sim (single instance MVP, future: load balancer)
- [x] Accommodate changes? Sim (versioning + modularity)

---

**RESULTADO FINAL:** 
✅ Especificação 100% conforme checklist  
✅ Pronta para implementação 26 horas  
✅ Sem ambiguidades, gaps ou conflitos  
✅ Testável, mantível, escalável

