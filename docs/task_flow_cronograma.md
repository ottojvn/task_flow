# CRONOGRAMA TÉCNICO: TASK FLOW MVP

## 1 Semana -  26 Horas -  Walking Skeleton → MVP Completo


***

# MINDSET DO TECH LEAD

Antes de começar, entenda o princípio que guia cada dia:

```
SEMANA = PROGRESSÃO LINEAR COM GO/NO-GO GATES

✅ Segunda-feira = Validar que arquitetura funciona
   ├─ SE FALHAR: Para tudo, debug, não avança
   └─ SE PASSAR: Prossegue com segurança
   
✅ Terça-Quinta = Backend sólido (API + DB + Testes)
   ├─ Cada dia expande um aspecto
   └─ Sábado consome tudo com confiança
   
✅ Sábado = Frontend integra
   └─ Backend já pronto, só conectar
   
✅ Domingo = Polimento final
   └─ Nada quebra, só melhora
```

**Você vai aprender:**

1. Como estruturar backend para testabilidade
2. Como organizar frontend para reutilização
3. Como debug integração front-back (seu maior medo)
4. Como entregar sob pressão de tempo

***

# SEGUNDA-FEIRA: WALKING SKELETON

## "Arquitetura Funciona?"

**Duração:** 2 horas (19:00-21:00)
**Objetivo:** Um endpoint GET no Spring retornando dados que o Angular exibe no console
**GO/NO-GO Gate:** Se não funcionar, não avança segunda à noite

***

## 🎯 Por que segunda-feira é crítica?

Se integração básica falhar segunda-feira:

- ❌ CORS error? Semana inteira perdida debugando
- ❌ Port ocupada? Devops problem, não features
- ❌ Angular não chama backend? Dúvida arquitetural quebra tudo

**Se funcionar segunda-feira:**

- ✅ Terça-quinta você expande com confiança (backend cresce, frontend aguarda)
- ✅ Sábado você conecta tudo knowing integração já validada
- ✅ Domingo é polimento, não surpresas

***

## 📋 TAREFAS DA SEGUNDA-FEIRA

### Tarefa 1: Setup Backend (Spring Boot)

**Tempo:** 10 min
**Problema que resolve:** "Como iniciar um projeto Spring Boot do zero?"

#### O que fazer:

1. **Criar projeto Spring Boot** via Spring Initializr (start.spring.io)
    - Language: Java
    - Java version: 17 LTS
    - Project: Maven
    - Packaging: JAR
    - Dependencies: Web, JPA, PostgreSQL Driver, Lombok (opcional)
2. **Entender a estrutura** gerada:

```
src/main/java/com/taskflow/
├── TaskFlowApplication.java  ← Main que inicia tudo
├── controller/               ← Endpoints HTTP
├── service/                  ← Lógica de negócio
├── repository/               ← Acesso a dados
└── entity/                   ← Modelos JPA

src/main/resources/
├── application.properties    ← Configurações (porta, BD, etc)
└── templates/               ← HTML (não usado em REST)
```

3. **Testar que inicia**:
    - `mvn spring-boot:run` deve subir servidor na porta 8080
    - Ctrl+C para parar

#### Por que fazer isso:

- Você precisa entender como Spring Boot organiza código
- Cada classe tem responsabilidade clara (não mistura HTTP com lógica)
- Dependencies do Maven ficam prontos para imports


#### Como resolver problemas:

- **"Erro: cannot find symbol"?** → Talvez Java 8 selecionado, trocar para 17
- **"Port 8080 already in use"?** → Algo rodando na porta, mudar para 8081 em application.properties
- **"Dependency not found"?** → Maven cache corrompido, rodar `mvn clean install`

***

### Tarefa 2: Criar Entity Task (Modelo de Dados)

**Tempo:** 10 min
**Problema que resolve:** "Como definir que um 'Task' tem título, descrição, status?"

#### O que fazer:

1. **Criar classe Task em** `src/main/java/com/taskflow/entity/Task.java`
    - Anotação `@Entity` (JPA converte isso em tabela no BD)
    - Anotação `@Table(name = "tasks")`
    - 7 campos conforme spec:
        - `id` (Long) com `@Id` + `@GeneratedValue` (auto-increment)
        - `title` (String) com `@NotBlank`, `@Column(nullable=false, length=255)`
        - `description` (String) com `@Column(length=1000)` (opcional)
        - `status` (Enum) com `@Enumerated(STRING)`, `@NotNull`
        - `assignedTo` (Long) com `@NotNull`
        - `createdAt` (LocalDateTime) com `@CreationTimestamp` (auto)
        - `updatedAt` (LocalDateTime) com `@UpdateTimestamp` (auto)
2. **Entender anotações** JPA:
    - `@Entity` = "Essa classe representa uma tabela no BD"
    - `@Id` = "Essa coluna é chave primária"
    - `@GeneratedValue` = "BD gera ID automaticamente"
    - `@Column` = "Customizar coluna (tamanho, se pode ser null)"
    - `@NotBlank` / `@NotNull` = "Validação (Spring valida antes de salvar)"
3. **Criar Enum Status** em `src/main/java/com/taskflow/entity/Status.java`

```java
public enum Status {
    PENDING, IN_PROGRESS, COMPLETED
}
```


#### Por que fazer isso:

- Entity é o "contrato" com banco de dados
- Spring JPA usa Entity para gerar tabela automaticamente (não precisa escrever SQL)
- Anotações `@NotBlank` previnem dados inválidos no BD desde o início


#### Como resolver problemas:

- **"Enum conversion failed"?** → Status no DB deve ser VARCHAR, não ENUM (Spring mapeia automaticamente)
- **"Column title cannot be null"?** → `@NotBlank` está funcionando, validação está ok
- **"Unknown column in entity"?** → Talvez não rodou migration, iniciar Spring novamente (cria tabela)

***

### Tarefa 3: Criar Repository (Acesso a Dados)

**Tempo:** 5 min
**Problema que resolve:** "Como Spring busca dados do BD sem escrever SQL?"

#### O que fazer:

1. **Criar interface TaskRepository** em `src/main/java/com/taskflow/repository/TaskRepository.java`

```java
extends JpaRepository<Task, Long>
```

    - Herdando JpaRepository, Spring oferece grátis:
        - `.findAll()` retorna lista de tasks
        - `.findById(id)` retorna 1 task
        - `.save(task)` insere ou atualiza
        - `.delete(task)` deleta
        - `.count()` conta total
2. **Apenas herdar**:

```java
@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
}
```

    - Não escreva nenhum método (Spring gera por você)
    - Later (terça-feira) adicionará custom queries para filtro

#### Por que fazer isso:

- JpaRepository = Spring Data Magic
- Você não escreve SQL, Spring gera SQL baseado em método names
- `@Repository` injeta automaticamente em @Service


#### Como resolver problemas:

- **"No Spring Data repositories configured"?** → Checar se `@Entity` está em Task
- **"Generic error"?** → Provavelmente SQL syntax, mas hoje não escrevemos SQL, só herda

***

### Tarefa 4: Criar Service (Lógica de Negócio)

**Tempo:** 10 min
**Problema que resolve:** "Onde coloca validações, regras de negócio, transações?"

#### O que fazer:

1. **Criar classe TaskService** em `src/main/java/com/taskflow/service/TaskService.java`
    - Anotações: `@Service` + `@Transactional`
    - Injetar TaskRepository via `@Autowired`
2. **Implementar 1 método apenas para segunda-feira:**

```java
public List<Task> getAllTasks() {
    return repository.findAll();
}
```

    - Simples, mas mostra padrão: Service chama Repository
3. **Entender padrão**:
    - **Controller** recebe HTTP request
    - **Service** executa lógica (validações, cálculos, transações)
    - **Repository** fala com BD
    - **Entity** é o modelo de dados

#### Por que fazer isso:

- Separação de responsabilidades = fácil testar depois
- `@Transactional` garante que operação no BD é "tudo ou nada" (ACID)
- Service é reutilizável (pode chamar mesmo método em múltiplos Controllers)


#### Como resolver problemas:

- **"taskRepository is null"?** → Faltou `@Autowired` ou `@Repository` em TaskRepository
- **"No transactional manager"?** → Spring Auto-configure cuida disso, ignorar aviso

***

### Tarefa 5: Criar Controller com GET Endpoint

**Tempo:** 10 min
**Problema que resolve:** "Como expor Service como endpoint HTTP?"

#### O que fazer:

1. **Criar classe TaskController** em `src/main/java/com/taskflow/controller/TaskController.java`
    - Anotações: `@RestController` + `@RequestMapping("/api/v1/tasks")`
    - `@RestController` = retorna JSON, não HTML
    - `@RequestMapping` = todos endpoints começam com /api/v1/tasks
2. **Implementar 1 endpoint GET:**

```java
@GetMapping
public List<Task> getAllTasks() {
    return service.getAllTasks();
}
```

    - Acessível em: http://localhost:8080/api/v1/tasks
    - Retorna JSON automaticamente
3. **Configurar CORS** para Angular consumir:
    - `@CrossOrigin(origins = "http://localhost:4200")`
    - Isso permite Angular (na porta 4200) chamar backend (porta 8080)

#### Por que fazer isso:

- Controller = "HTTP interface"
- `@GetMapping` = responde a GET requests
- Retorna `List<Task>` que Spring serializa para JSON
- CORS é essencial: sem ele, navegador bloqueia requests cross-origin


#### Como resolver problemas:

- **"CORS policy blocked"?** → Falta `@CrossOrigin`, adicionar em Controller
- **"405 Method Not Allowed"?** → Errou anotação (`@PostMapping` em GET request)
- **"No qualifying bean of type TaskService"?** → Faltou `@Autowired TaskService service`

***

### Tarefa 6: Setup Frontend (Angular)

**Tempo:** 15 min
**Problema que resolve:** "Como criar projeto Angular que chama API?"

#### O que fazer:

1. **Criar projeto Angular** em terminal novo:

```bash
ng new task-flow-frontend --routing --style=css
cd task-flow-frontend
```

    - `--routing` = adiciona roteamento (opcional para MVP, mas bom ter)
    - `--style=css` = usar CSS simples (não SCSS)
2. **Entender estrutura**:

```
src/
├── app/
│   ├── app.component.ts       ← Componente raiz
│   ├── app.component.html     ← HTML raiz
│   ├── services/              ← Services (HTTP)
│   ├── components/            ← Componentes visuais
│   └── models/                ← Interfaces TypeScript
├── main.ts                    ← Inicia app
└── index.html                 ← HTML que carrega Angular
```

3. **Testar que inicia**:

```bash
ng serve
# Abre browser em http://localhost:4200
```


#### Por que fazer isso:

- Angular organiza código em modules, services, components
- Porta 4200 é padrão (você codificou em CORS com isso)
- `ng serve` oferece hot-reload (salva arquivo = atualiza browser)


#### Como resolver problemas:

- **"Cannot find module @angular/..."?** → `npm install` não rodou, executar
- **"Port 4200 in use"?** → `ng serve --port 4201`
- **"Error: Unknown or ambiguous command"?** → Você está em pasta errada, `cd task-flow-frontend`

***

### Tarefa 7: Criar Service Angular para chamar Backend

**Tempo:** 15 min
**Problema que resolve:** "Como Angular comunica com Spring backend?"

#### O que fazer:

1. **Criar TaskService** em `src/app/services/task.service.ts`:
    - Injetar `HttpClient` (Angular oferece grátis)
    - 1 método apenas: `getTasks()`
    - Fazer GET request para `http://localhost:8080/api/v1/tasks`
    - Retornar `Observable<Task[]>`
2. **Estrutura esperada**:

```typescript
@Injectable({ providedIn: 'root' })
export class TaskService {
    constructor(private http: HttpClient) {}
    
    getTasks(): Observable<Task[]> {
        return this.http.get<Task[]>('http://localhost:8080/api/v1/tasks');
    }
}
```

3. **Criar Task interface** em `src/app/models/task.model.ts`:
    - Espelha Entity Java: id, title, description, status, assignedTo, createdAt, updatedAt
    - TypeScript interface para type-safety
4. **Adicionar HttpClientModule** em `app.module.ts`:
    - Import: `import { HttpClientModule } from '@angular/common/http';`
    - Adicionar ao `imports: [ HttpClientModule ]`
    - Sem isso, HttpClient não funciona

#### Por que fazer isso:

- Service = reutilizável, testável, separado de UI
- HttpClient = Angular HTTP client (built-in)
- Observable = programação reativa (se dado muda no servidor, UI sabe)
- Interface TypeScript = evita erros de propriedades


#### Como resolver problemas:

- **"No provider for HttpClient"?** → Faltou `HttpClientModule` em app.module
- **"Cannot access properties on null"?** → Talvez backend não respondeu, checar console
- **"404 on GET request"?** → Endpoint errado, verificar URL (deve terminar sem `/`)

***

### Tarefa 8: Chamar Service em Component e Exibir no Console

**Tempo:** 15 min
**Problema que resolve:** "Como ligar tudo junto: Backend → Service → Component → Tela?"

#### O que fazer:

1. **Editar AppComponent** em `src/app/app.component.ts`:
    - Injetar TaskService
    - No `ngOnInit()`, chamar `taskService.getTasks()`
    - Subscribe no Observable, receber dados, exibir no console
2. **Estrutura esperada**:

```typescript
export class AppComponent implements OnInit {
    tasks: Task[] = [];
    
    constructor(private taskService: TaskService) {}
    
    ngOnInit() {
        this.taskService.getTasks().subscribe(
            (data) => {
                console.log('Tasks received:', data);
                this.tasks = data;
            },
            (error) => console.error('Error:', error)
        );
    }
}
```

3. **No template** `app.component.html`, adicionar:

```html
<div>{{ tasks | json }}</div>
```

    - `| json` = pipe que converte array para JSON string legível
    - Você vai ver tasks no HTML (não só console)

#### Por que fazer isso:

- Este é o teste de integração: dados fluem Backend → Frontend → Tela
- Subscribe = "quando resposta chegar, executa este código"
- console.log = ferramenta \#1 de debug
- Error handling = preparar para quando backend cair


#### Como resolver problemas:

- **"404 Not Found"?** → Backend não está rodando, `mvn spring-boot:run`
- **"CORS error in console"?** → Falta `@CrossOrigin` no Controller
- **"Empty array no console"?** → Backend foi-ao rodando, nenhum erro, mas sem dados. Checar se Entity está mapeada corretamente
- **"Subscribe never called"?** → Talvez erro de URL ou tipos TypeScript

***

### Tarefa 9: Seed Data (Dados para Testar)

**Tempo:** 10 min
**Problema que resolve:** "Como ter dados no BD para testes sem digitar no console?"

#### O que fazer:

1. **Criar CommandLineRunner** em `src/main/java/com/taskflow/config/DataLoader.java`
    - Componente que roda automaticamente quando Spring inicia
    - Insere 3-5 tasks de teste no BD
2. **Estrutura**:

```java
@Component
public class DataLoader implements CommandLineRunner {
    @Autowired private TaskRepository repository;
    
    @Override
    public void run(String... args) throws Exception {
        // Se BD vazio, insere dados de teste
        if (repository.count() == 0) {
            repository.save(new Task(null, "Bug fix", "Email", Status.PENDING, 1L, ...));
            // 3-4 mais tasks
        }
    }
}
```

3. **Resultado**:
    - Quando inicia Spring, BD ganha dados automaticamente
    - Próxima vez que acessa /api/v1/tasks, retorna tarefas reais

#### Por que fazer isso:

- Evita digitar dados via SQL ou Postman
- Garante dados consistentes para testes
- Descobrir bugs "sem dados" é frustração


#### Como resolver problemas:

- **"Task constructor error"?** → Talvez faltam fields, checar Entity
- **"Nenhum dado inserido"?** → DataLoader não executou, verificar `@Component`

***

## ✅ SEGUNDA-FEIRA: CHECKLIST DE CONCLUSÃO

**Antes de dormir, validar:**

```
BACKEND (Spring Boot)
 ☑ Projeto Maven criado com Java 17
 ☑ Task Entity com 7 campos + anotações JPA
 ☑ Status Enum (PENDING, IN_PROGRESS, COMPLETED)
 ☑ TaskRepository extends JpaRepository (sem código)
 ☑ TaskService com getAllTasks()
 ☑ TaskController GET /api/v1/tasks com @CrossOrigin
 ☑ DataLoader insere 3-5 tasks automaticamente
 ☑ mvn spring-boot:run inicia sem erros
 ☑ http://localhost:8080/api/v1/tasks retorna JSON

FRONTEND (Angular)
 ☑ Projeto criado com ng new
 ☑ HttpClientModule adicionado a app.module.ts
 ☑ TaskService criado com getTasks()
 ☑ Task interface espelhando Entity Java
 ☑ AppComponent injeta TaskService, chama getTasks()
 ☑ ng serve inicia sem erros
 ☑ http://localhost:4200 exibe tarefas no HTML + console

INTEGRAÇÃO
 ☑ Console do navegador mostra tasks (sem CORS error)
 ☑ Angular consegue chamar http://localhost:8080/api/v1/tasks
 ☑ Se backend cai, Angular mostra erro (não trava)
 ☑ Git commit: "feat: walking skeleton - GET tasks"
```


### 🚨 GO/NO-GO Gate Segunda-feira à Noite

**✅ SE PASSOU NO CHECKLIST ACIMA:**

```
Parabéns! Sua arquitetura funciona. Terça-feira expande com confiança.
Você pode agora:
- Adicionar POST/PUT/DELETE sem medo
- Sábado integra frontend sabendo backend é confiável
- Domingo é polimento, não surpresas
```

**❌ SE FALHOU EM QUALQUER PONTO:**

```
PARE. Não avance segunda à noite.
Debug antes de dormir:
1. Backend respondendo JSON?
   → Postman: GET http://localhost:8080/api/v1/tasks
   
2. Frontend consegue chamar?
   → Console: qualquer erro CORS?
   
3. Dados aparecem no HTML?
   → Verificar {{ tasks | json }} renderiza
   
Não durma sem responder essas 3 perguntas com SIM.
```


***

# TERÇA-FEIRA: BACKEND CORE - ENTITY + SERVICE

## "Dados no Banco Funcionam?"

**Duração:** 2 horas (19:00-21:00)
**Foco:** Entity completo, Service com 6 métodos, primeira validação
**Entrada:** Walking Skeleton de segunda (GET funciona)
**Saída:** Backend pronto para POST/PUT/DELETE

***

## 🎯 Por que Terça-feira é construção sólida?

Terça-feira você consolida o BD:

- ✅ CREATE (POST): Validações para não inserir lixo
- ✅ UPDATE (PUT): Lógica de negócio no Service
- ✅ DELETE: Tratamento de "tarefa não encontrada"
- ✅ FILTER: Queries customizadas no Repository
- Sábado, você só conecta isso ao Frontend (sem supresas)

***

## 📋 TAREFAS DA TERÇA-FEIRA

### Tarefa 1: Revisar + Completar Entity Task

**Tempo:** 10 min
**Problema que resolve:** "Entity tem tudo que spec exige?"

#### O que fazer:

1. **Revisar os 7 campos** conforme spec:
    - ✅ `id` (Long, @Id, @GeneratedValue)
    - ✅ `title` (String, @NotBlank, @Size(min=1, max=255))
    - ✅ `description` (String, @Size(max=1000), nullable)
    - ✅ `status` (Status enum, @NotNull, @Enumerated(STRING))
    - ✅ `assignedTo` (Long, @NotNull)
    - ✅ `createdAt` (LocalDateTime, @CreationTimestamp)
    - ✅ `updatedAt` (LocalDateTime, @UpdateTimestamp)
2. **Adicionar getters/setters e toString()**:
    - Usar Lombok: `@Data` (gera tudo automaticamente)
    - Ou gerar manualmente em IDE (mouse direito → Generate getters/setters)
3. **Adicionar construtor**:
    - Construtor vazio `public Task()` (JPA precisa)
    - Construtor com campos `public Task(Long id, String title, ...)`
4. **Validar importações**:

```java
import javax.persistence.*; // JPA
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.LocalDateTime;
import lombok.Data; // opcional
```


#### Por que fazer isso:

- Entity é o "contrato" entre aplicação e BD
- Falta um campo? Spec não satisfeita
- Sem construtor vazio, JPA queima
- toString() ajuda debug (ver task inteira em logs)


#### Como resolver problemas:

- **"CreationTimestamp not found"?** → Usar Hibernate annotations, não JPA padrão
- **"@Data não funciona"?** → Lombok dependency faltando em pom.xml
- **"Enum não serializa"?** → `@Enumerated(STRING)` garante que salva texto, não número

***

### Tarefa 2: Criar Repository com 2 Custom Queries

**Tempo:** 10 min
**Problema que resolve:** "Como buscar tasks por status? Como buscar por ID com tratamento?"

#### O que fazer:

1. **Adicionar 2 métodos ao TaskRepository**:

```java
Page<Task> findByStatus(Status status, Pageable pageable);
Optional<Task> findById(Long id); // JpaRepository já oferece, mas declarar explícito
```

2. **Entender cada um**:
    - `findByStatus` = Spring Data Magic: por nome do método gera SQL `WHERE status = ?`
    - `Optional<Task>` = retorna container que pode estar vazio (safer que null)
    - `Page<Task>` = resultado paginado (totalElements, hasNext, etc)
3. **Por que esses 2?**:
    - Filter by status = requisito da spec (Task 5)
    - Safe getById com Optional = evita NullPointerException

#### Por que fazer isso:

- Repository oferece queries prontas (JpaRepository)
- Naming convention (find + By + fieldName) = Spring gera SQL
- Optional = pattern Java moderno (evita null checks tediosos)


#### Como resolver problemas:

- **"Ambiguous method name"?** → Talvez sobrecarregou, deletar método redundante
- **"Page not found"?** → Import: `import org.springframework.data.domain.Page;`
- **"Optional error"?** → Import: `import java.util.Optional;`

***

### Tarefa 3: Expandir TaskService com 6 Métodos CRUD

**Tempo:** 25 min
**Problema que resolve:** "Como encapsular validações e regras de negócio?"

#### O que fazer:

1. **Implementar 6 métodos** no TaskService:

**Método 1: getAll (paginado)**

```java
public Page<Task> getAll(Pageable pageable) {
    return repository.findAll(pageable);
}
```

    - Input: Pageable (page=0, size=10 do Controller)
    - Output: Page<Task> com totalElements, hasNext, etc
    - Lógica: Só delega ao repository

**Método 2: getById (com erro se não encontra)**

```java
public Task getById(Long id) {
    return repository.findById(id)
        .orElseThrow(() -> new TaskNotFoundException("Task not found"));
}
```

    - Input: id
    - Output: Task
    - Lógica: Se não encontra, throws exception (Controller pega e retorna 404)

**Método 3: create (com validação)**

```java
public Task create(@Valid Task task) {
    // @Valid garante @NotBlank, @NotNull já passaram
    task.setId(null); // Garante que não tenta criar com ID
    return repository.save(task);
}
```

    - Input: Task com título, descrição, status, assignedTo
    - Output: Task com ID gerado
    - Lógica: Limpa ID (não deixa usuário forçar ID), salva

**Método 4: update (com erro se não encontra)**

```java
public Task update(Long id, @Valid Task task) {
    Task existing = getById(id); // Reutiliza getById (que throws se não encontra)
    existing.setTitle(task.getTitle());
    existing.setDescription(task.getDescription());
    existing.setStatus(task.getStatus());
    existing.setAssignedTo(task.getAssignedTo());
    // createdAt + updatedAt geradas automaticamente
    return repository.save(existing);
}
```

    - Input: id + Task atualizado
    - Output: Task merged
    - Lógica: Encontra existente, atualiza campos, salva (updatedAt automático)

**Método 5: delete (sem retorno)**

```java
public void delete(Long id) {
    Task task = getById(id); // Valida que existe
    repository.delete(task);
}
```

    - Input: id
    - Output: void
    - Lógica: Valida que existe antes de deletar

**Método 6: filterByStatus (paginado)**

```java
public Page<Task> filterByStatus(Status status, Pageable pageable) {
    return repository.findByStatus(status, pageable);
}
```

    - Input: status enum, pageable
    - Output: Page<Task> filtrado
    - Lógica: Delega ao repository
2. **Exceção customizada**:
    - Criar `TaskNotFoundException extends RuntimeException`
    - Usado em getById
    - Controller vai pegar e virar 404
3. **Importações necessárias**:

```java
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import javax.validation.Valid;
```


#### Por que fazer isso:

- Service = camada de lógica
- Cada método tem responsabilidade clara
- Validações centralizadas (um lugar, fácil testar)
- Exception handling robusta (não deixa BD em estado inconsistente)
- `@Valid` força Controller a validar antes de chegar aqui


#### Como resolver problemas:

- **"orElseThrow not found"?** → Versão Java < 10, usar `.orElse(null)` + null check
- **"Page not found in import"?** → Mesma do Repository acima
- **"Exception class error"?** → TaskNotFoundException precisa `extends RuntimeException`
- **"@Valid não funciona"?** → Controller precisa de `@Valid @RequestBody` também

***

### Tarefa 4: Criar Controller com 6 Endpoints (sem DELETE ainda)

**Tempo:** 25 min
**Problema que resolve:** "Como mapear cada método Service para endpoint HTTP?"

#### O que fazer:

1. **Expandir TaskController com 5 endpoints**:

**Endpoint 1: GET /api/v1/tasks (listar com paginação)**

```java
@GetMapping
public Page<Task> getAllTasks(@PageableDefault(size = 10) Pageable pageable) {
    return service.getAll(pageable);
}
```

    - Input: Query params `?page=0&size=10` (auto-parsed por Pageable)
    - Output: 200 OK + Page JSON
    - Sem validação (usuário malandro coloca `?page=-1` = spring normaliza)

**Endpoint 2: GET /api/v1/tasks/{id} (detalhes de 1 tarefa)**

```java
@GetMapping("/{id}")
public Task getTaskById(@PathVariable Long id) {
    return service.getById(id);
}
```

    - Input: Path param {id}
    - Output: 200 OK + Task JSON
    - Error: 404 se Task não encontrada (GlobalExceptionHandler pega)

**Endpoint 3: POST /api/v1/tasks (criar nova)**

```java
@PostMapping
public ResponseEntity<Task> createTask(@Valid @RequestBody Task task) {
    Task created = service.create(task);
    return ResponseEntity.status(201).body(created);
}
```

    - Input: JSON body (title, description, status, assignedTo)
    - Output: 201 Created + Task JSON com ID
    - Validação: @Valid falha se título vazio = 400 Bad Request

**Endpoint 4: PUT /api/v1/tasks/{id} (atualizar)**

```java
@PutMapping("/{id}")
public Task updateTask(@PathVariable Long id, @Valid @RequestBody Task task) {
    return service.update(id, task);
}
```

    - Input: Path param {id} + JSON body
    - Output: 200 OK + Task atualizado
    - Error: 404 se Task não encontrada

**Endpoint 5: GET /api/v1/tasks/status/{status} (filtrar por status)**

```java
@GetMapping("/status/{status}")
public Page<Task> filterByStatus(
    @PathVariable Status status,
    @PageableDefault(size = 10) Pageable pageable
) {
    return service.filterByStatus(status, pageable);
}
```

    - Input: Path param {status} + pageable
    - Output: 200 OK + Page JSON com tasks filtradas
    - Note: Endpoint específico ANTES de genérico (Spring matcheia ordem)
2. **Classe no topo do Controller**:

```java
@RestController
@RequestMapping("/api/v1/tasks")
@CrossOrigin(origins = "http://localhost:4200")
public class TaskController {
    @Autowired
    private TaskService service;
    // ... endpoints acima
}
```

3. **Importações**:

```java
import org.springframework.http.ResponseEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import javax.validation.Valid;
import org.springframework.web.bind.annotation.*;
```


#### Por que fazer isso:

- Controller = HTTP interface
- `@Valid` integra com validação JPA (400 se inválido)
- `ResponseEntity.status(201)` explícito (boa prática)
- Path vars vs body params = diferença crítica (URL vs JSON)
- Ordem dos endpoints = `/status/{status}` ANTES de `/{id}` (senão `/status/PENDING` vira getById com id="status")


#### Como resolver problemas:

- **"No enum constant"?** → Status na URL errado, deve ser PENDING não Pending
- **"400 Bad Request no POST"?** → Falta campo obrigatório no JSON, checar @NotBlank
- **"404 on PUT"?** → Task não encontrada, ID errado
- **"Multiple path variables"?** → Ordem importa, endpoint específico `/status/{status}` antes do genérico `/{id}`

***

### Tarefa 5: Criar GlobalExceptionHandler

**Tempo:** 15 min
**Problema que resolve:** "Como retornar erros estruturados ao invés de stack traces?"

#### O que fazer:

1. **Criar classe ErrorResponse** em `src/main/java/com/taskflow/dto/ErrorResponse.java`:

```java
public class ErrorResponse {
    private LocalDateTime timestamp;
    private int status;
    private String error;
    private String message;
    private String path;
    
    // getters/setters ou @Data
}
```

2. **Criar GlobalExceptionHandler** em `src/main/java/com/taskflow/exception/GlobalExceptionHandler.java`:

```java
@ControllerAdvice
public class GlobalExceptionHandler {
    
    // Validação @Valid falha (400)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidation(MethodArgumentNotValidException e, HttpServletRequest req) {
        ErrorResponse error = new ErrorResponse(
            LocalDateTime.now(),
            400,
            "Validation Error",
            "Um ou mais campos são inválidos",
            req.getRequestURI()
        );
        return ResponseEntity.badRequest().body(error);
    }
    
    // Task não encontrada (404)
    @ExceptionHandler(TaskNotFoundException.class)
    public ResponseEntity<?> handleNotFound(TaskNotFoundException e, HttpServletRequest req) {
        ErrorResponse error = new ErrorResponse(
            LocalDateTime.now(),
            404,
            "Not Found",
            "Task não encontrada",
            req.getRequestURI()
        );
        return ResponseEntity.status(404).body(error);
    }
    
    // Qualquer outra exceção (500)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleGeneric(Exception e, HttpServletRequest req) {
        ErrorResponse error = new ErrorResponse(
            LocalDateTime.now(),
            500,
            "Internal Server Error",
            "Erro ao processar requisição",
            req.getRequestURI()
        );
        e.printStackTrace(); // Log para DevOps debug
        return ResponseEntity.status(500).body(error);
    }
}
```

3. **Importações**:

```java
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import javax.servlet.http.HttpServletRequest;
```


#### Por que fazer isso:

- `@ControllerAdvice` = captura exceções de TODO o projeto
- Erro estruturado = Frontend consegue parsear (não stack trace confuso)
- Stack trace no servidor (log) = DevOps consegue debug, Frontend não vê
- 400 vs 404 vs 500 = cliente sabe diferença


#### Como resolver problemas:

- **"ControllerAdvice not invoked"?** → Talvez exception está sendo capturada em outro lugar, remover
- **"ErrorResponse fields null"?** → Faltam getters/setters ou @Data
- **"404 retornando 500"?** → TaskNotFoundException não extends RuntimeException?

***

### Tarefa 6: Testar Tudo via Postman (ou Insomnia)

**Tempo:** 15 min
**Problema que resolve:** "API funciona sem Frontend?"

#### O que fazer:

1. **Baixar Postman** (postman.com) ou Insomnia (insomnia.rest)
2. **Criar 6 requests** (uma por endpoint):

**Request 1: GET /api/v1/tasks**
    - Method: GET
    - URL: http://localhost:8080/api/v1/tasks
    - Headers: Accept: application/json
    - Expected: 200 OK + Page JSON com tasks

**Request 2: GET /api/v1/tasks/1**
    - Method: GET
    - URL: http://localhost:8080/api/v1/tasks/1
    - Expected: 200 OK + Task JSON

**Request 3: POST /api/v1/tasks**
    - Method: POST
    - URL: http://localhost:8080/api/v1/tasks
    - Headers: Content-Type: application/json
    - Body (raw JSON):

```json
{
  "title": "Nova tarefa",
  "description": "Descrição aqui",
  "status": "PENDING",
  "assignedTo": 1
}
```

    - Expected: 201 Created + Task JSON com ID gerado

**Request 4: PUT /api/v1/tasks/1**
    - Method: PUT
    - URL: http://localhost:8080/api/v1/tasks/1
    - Headers: Content-Type: application/json
    - Body (raw JSON):

```json
{
  "title": "Tarefa atualizada",
  "description": "Nova descrição",
  "status": "IN_PROGRESS",
  "assignedTo": 1
}
```

    - Expected: 200 OK + Task atualizado

**Request 5: GET /api/v1/tasks/status/PENDING**
    - Method: GET
    - URL: http://localhost:8080/api/v1/tasks/status/PENDING
    - Expected: 200 OK + Page JSON com tasks PENDING apenas

**Request 6: DELETE /api/v1/tasks/1**
    - Method: DELETE
    - URL: http://localhost:8080/api/v1/tasks/1
    - Expected: 204 No Content (vazio, sem body)
3. **Testar error scenarios**:
    - POST com título vazio → 400 Bad Request + ErrorResponse
    - GET /api/v1/tasks/999 → 404 Not Found + ErrorResponse
    - PUT /api/v1/tasks/999 → 404 Not Found + ErrorResponse

#### Por que fazer isso:

- Postman = validar API antes de conectar Frontend
- Evita gastar tempo debugando integração se API está quebrada
- Descobrir bugs agora = simples, descobrir sábado integrado = complexo


#### Como resolver problemas:

- **"CORS error"?** → Postman não sofre CORS, erro deve ser outro (verificar backend log)
- **"400 Bad Request"?** → JSON inválido, verificar sintaxe (aspas, vírgulas)
- **"404 not found"?** → ID errado ou endpoint desapareceu
- **"Connection refused"?** → Backend não rodando, `mvn spring-boot:run`

***

### Tarefa 7: Implementar Paginação Básica

**Tempo:** 10 min
**Problema que resolve:** "Como retornar 10 tasks por página sem sobrecarregar?"

#### O que fazer:

1. **Adicionar Spring Data Web config** em `application.properties`:

```properties
spring.data.web.pageable.default-page-size=10
spring.data.web.pageable.max-page-size=100
spring.data.web.pageable.one-indexed-parameters=false
```

2. **Entender**:
    - `?page=0` = primeira página (0-indexed)
    - `?size=10` = 10 items por página
    - `?sort=createdAt,desc` = ordena por criação descendente
    - Spring auto-parse em Pageable
3. **Testar**:
    - Postman: GET http://localhost:8080/api/v1/tasks?page=0\&size=5
    - Response inclui: content, totalElements, totalPages, number (current page), size

#### Por que fazer isso:

- Paginação = critério de sucesso na spec
- Sem paginação, 1000 tasks = trasnferência de 5MB (lento)
- Page object oferece hasNext, hasPrevious (útil no Frontend)


#### Como resolver problemas:

- **"Pageable not recognized"?** → Faltou `spring-boot-starter-web` dependency
- **"0-indexed vs 1-indexed confuso"?** → Padrão REST é 0, não mude

***

## ✅ TERÇA-FEIRA: CHECKLIST DE CONCLUSÃO

```
ENTITY + VALIDAÇÃO
 ☑ Task Entity com 7 campos + anotações
 ☑ Status Enum criado
 ☑ @NotBlank, @NotNull, @Size em Task
 ☑ Constructores: vazio + com campos
 ☑ Getters/setters ou @Data
 ☑ toString() para debug

REPOSITORY
 ☑ TaskRepository extends JpaRepository<Task, Long>
 ☑ findByStatus(Status, Pageable) declarado
 ☑ findById(Long) returns Optional

SERVICE - 6 MÉTODOS
 ☑ getAll(Pageable) com paginação
 ☑ getById(Long) com TaskNotFoundException
 ☑ create(@Valid Task) com setId(null)
 ☑ update(Long, @Valid Task) merge fields
 ☑ delete(Long) valida antes
 ☑ filterByStatus(Status, Pageable)
 ☑ TaskNotFoundException extends RuntimeException

CONTROLLER - 5 ENDPOINTS (DELETE dia seguinte)
 ☑ GET /api/v1/tasks com @PageableDefault
 ☑ GET /api/v1/tasks/{id}
 ☑ POST /api/v1/tasks com @Valid
 ☑ PUT /api/v1/tasks/{id} com @Valid
 ☑ GET /api/v1/tasks/status/{status}
 ☑ @CrossOrigin("http://localhost:4200") em Controller
 ☑ Ordem: /status/{status} ANTES de /{id}

ERROR HANDLING
 ☑ ErrorResponse DTO criado
 ☑ GlobalExceptionHandler com @ControllerAdvice
 ☑ Handle MethodArgumentNotValidException (400)
 ☑ Handle TaskNotFoundException (404)
 ☑ Handle generic Exception (500)
 ☑ Stack trace logged (não enviado ao cliente)

POSTMAN TESTS
 ☑ GET /api/v1/tasks → 200 + Page JSON
 ☑ GET /api/v1/tasks/1 → 200 + Task JSON
 ☑ POST /api/v1/tasks com body válido → 201 + Task
 ☑ POST sem title → 400 + ErrorResponse
 ☑ PUT /api/v1/tasks/1 → 200 + Task atualizado
 ☑ PUT /api/v1/tasks/999 → 404 + ErrorResponse
 ☑ GET /api/v1/tasks/status/PENDING → 200 + filtered Page
 ☑ Paginação ?page=0&size=5 → funciona

GIT
 ☑ Git commit: "feat: backend CRUD + validation"
```


***

# QUARTA-FEIRA: REST API - ENDPOINTS COMPLETOS

## "Todos os 6 Endpoints Funcionam?"

**Duração:** 2 horas (19:00-21:00)
**Foco:** Completar DELETE endpoint, testes manuais, documentação Postman
**Entrada:** Backend 5/6 endpoints de terça
**Saída:** API 100% pronta para sábado (integração)

***

## 📋 TAREFAS DA QUARTA-FEIRA

### Tarefa 1: Implementar DELETE Endpoint

**Tempo:** 5 min
**Problema que resolve:** "Como deletar task com segurança?"

#### O que fazer:

```java
@DeleteMapping("/{id}")
public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
    service.delete(id);
    return ResponseEntity.noContent().build(); // 204 No Content
}
```

- Input: Path param {id}
- Output: 204 No Content (sem body)
- Error: 404 se Task não encontrada (GlobalExceptionHandler pega)
- Significado: "Deletado com sucesso, nada para retornar"


#### Por que fazer isso:

- 204 = HTTP standard para operação bem-sucedida sem response body
- DELETE /tasks/1 → task sumiu do BD
- Frontend precisa disso para remover da tabela

***

### Tarefa 2: Testar Todos os 6 Endpoints no Postman

**Tempo:** 25 min
**Problema que resolve:** "API está 100% funcional?"

#### O que fazer:

1. **Criar Postman collection** com 6 requests:
    - GET /api/v1/tasks (listar)
    - GET /api/v1/tasks/1 (detalhe)
    - POST /api/v1/tasks (criar)
    - PUT /api/v1/tasks/1 (atualizar)
    - GET /api/v1/tasks/status/PENDING (filtrar)
    - DELETE /api/v1/tasks/2 (deletar)
2. **Executar sequência de testes**:

```
Step 1: GET /api/v1/tasks → Deve ter 3-5 tarefas da seed
Step 2: POST → Cria nova (ID=6 provavelmente)
Step 3: GET /api/v1/tasks → Deve ter 6 tarefas agora
Step 4: GET /api/v1/tasks/6 → Retorna a criada
Step 5: PUT /api/v1/tasks/6 → Atualiza algo
Step 6: GET /api/v1/tasks/6 → Confirma update
Step 7: GET /api/v1/tasks/status/IN_PROGRESS → Filtra
Step 8: DELETE /api/v1/tasks/2 → Deleta
Step 9: GET /api/v1/tasks → Deve ter 5 tarefas (uma foi deletada)
```

3. **Testar error scenarios**:
    - POST sem title → 400
    - GET /tasks/999 → 404
    - PUT /tasks/999 → 404
    - POST com status="INVALID_STATUS" → 400

#### Por que fazer isso:

- Validar que cada endpoint responde correto
- Checar status codes (200, 201, 204, 400, 404)
- Confirmar JSON structure está correto
- Garantir que BD persiste (reload tasks continua)

***

### Tarefa 3: Adicionar Logging (Debug)

**Tempo:** 10 min
**Problema que resolve:** "Como debugar quando integração quebra sábado?"

#### O que fazer:

1. **Adicionar logging em TaskService**:

```java
private static final Logger logger = LoggerFactory.getLogger(TaskService.class);

public Task create(@Valid Task task) {
    logger.info("Creating task: {}", task.getTitle());
    task.setId(null);
    Task saved = repository.save(task);
    logger.info("Task created with ID: {}", saved.getId());
    return saved;
}
```

2. **Adicionar logging em GlobalExceptionHandler**:

```java
@ExceptionHandler(Exception.class)
public ResponseEntity<?> handleGeneric(Exception e) {
    logger.error("Unexpected error: ", e); // Full stack trace
    // retorna ErrorResponse
}
```

3. **Configurar log level em application.properties**:

```properties
logging.level.com.taskflow=DEBUG
logging.level.org.springframework.web=WARN
```


#### Por que fazer isso:

- Logs = primeira ferramenta quando integração quebra
- Você vê "Task created with ID: 1" = sinal de vida
- Stack traces salvam tempo de debug

***

### Tarefa 4: Documentar API com Comentários (Javadoc opcional, mas bom ter)

**Tempo:** 10 min
**Problema que resolve:** "Como outros devs entendem a API?"

#### O que fazer:

1. **Adicionar comentários em Controller**:

```java
/**
 * GET /api/v1/tasks
 * Retorna lista paginada de todas as tarefas
 * @param pageable page (0-indexed), size (default 10, max 100)
 * @return Page<Task> com tarefas
 */
@GetMapping
public Page<Task> getAllTasks(@PageableDefault(size = 10) Pageable pageable) {
    return service.getAll(pageable);
}
```

2. **Criar Postman Collection README**:
    - Arquivo README.md na pasta do projeto
    - Listar 6 endpoints com exemplos de curl
    - Explicar status codes esperados
    - Exemplo de request/response

#### Por que fazer isso:

- Documentação salva tempo de integração
- Sábado, você não lembra o que cada endpoint faz
- Frontend dev consegue ler e entender

***

### Tarefa 5: Testar Paginação Avançada

**Tempo:** 5 min
**Problema que resolve:** "Paginação funciona com sorting?"

#### O que fazer:

1. **Testar em Postman**:
    - `GET /api/v1/tasks?page=0&size=3` → primeira página, 3 items
    - `GET /api/v1/tasks?page=1&size=3` → segunda página
    - `GET /api/v1/tasks?sort=createdAt,desc` → ordena por data descending
    - `GET /api/v1/tasks?sort=title,asc&page=0&size=5` → combinado
2. **Validar response**:

```json
{
  "content": [...],
  "totalElements": 12,
  "totalPages": 4,
  "number": 0,
  "size": 3,
  "hasNext": true,
  "hasPrevious": false
}
```


#### Por que fazer isso:

- Paginação = requisito spec
- Sábado, Frontend precisa disso para mostrar "Page 1 of 4"

***

## ✅ QUARTA-FEIRA: CHECKLIST DE CONCLUSÃO

```
COMPLETAR API
 ☑ DELETE /api/v1/tasks/{id} implementado
 ☑ 6 endpoints completos (GET list, GET by-id, POST, PUT, DELETE, GET filter)
 ☑ Todos retornam status corretos (200, 201, 204, 400, 404)

POSTMAN TESTS
 ☑ Collection com 6 requests
 ☑ Teste sequencial: criar → listar → atualizar → deletar
 ☑ Error tests: 400, 404, validação
 ☑ Paginação: ?page=0&size=5
 ☑ Sorting: ?sort=createdAt,desc

LOGGING + DOCS
 ☑ Logger configurado em TaskService
 ☑ GlobalExceptionHandler loga stack traces
 ☑ Javadoc em endpoints (ou comentários claros)
 ☑ README.md com exemplos curl para cada endpoint

GIT
 ☑ Git commit: "feat: complete REST API with DELETE + logging"
```


***

# QUINTA-FEIRA: TESTES BACKEND

## "6+ Testes Passam?"

**Duração:** 2 horas (19:00-21:00)
**Foco:** JUnit + Mockito, validar regras de negócio
**Entrada:** API completa de quarta
**Saída:** Backend validado por testes, confiante para sábado

***

## 📋 TAREFAS DA QUINTA-FEIRA

### Tarefa 1: Setup JUnit 5 + Mockito

**Tempo:** 5 min
**Problema que resolve:** "Como escrever testes Java?"

#### O que fazer:

1. **Verificar pom.xml** (Spring Boot inclui por padrão):

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-test</artifactId>
    <scope>test</scope>
</dependency>
```

    - Inclui JUnit 5, Mockito, AssertJ, etc
2. **Criar pasta** `src/test/java/com/taskflow/`
    - Mirror da estrutura src/main

#### Por que fazer isso:

- JUnit 5 = framework de testes Java moderno
- Mockito = simula dependências (faz teste rápido, sem BD real)
- @Test = anotação que marca teste

***

### Tarefa 2: Escrever 3 Testes do TaskService

**Tempo:** 30 min
**Problema que resolve:** "Como validar Service funciona sem BD real?"

#### O que fazer:

1. **Criar TaskServiceTest** em `src/test/java/com/taskflow/service/TaskServiceTest.java`:

**Teste 1: getAll() retorna Page**

```java
@ExtendWith(MockitoExtension.class)
public class TaskServiceTest {
    
    @Mock
    private TaskRepository repository;
    
    @InjectMocks
    private TaskService service;
    
    @Test
    public void testGetAllTasks() {
        // Arrange: Prepara dados fake
        Task task1 = new Task(1L, "Bug fix", "Email", Status.PENDING, 1L, ...);
        Task task2 = new Task(2L, "Feature", "Modal", Status.IN_PROGRESS, 1L, ...);
        Page<Task> page = new PageImpl<>(Arrays.asList(task1, task2));
        
        when(repository.findAll(any(Pageable.class))).thenReturn(page);
        
        // Act: Executa
        Page<Task> result = service.getAll(Pageable.ofSize(10));
        
        // Assert: Valida
        assertEquals(2, result.getContent().size());
        assertEquals("Bug fix", result.getContent().get(0).getTitle());
        
        // Verify: Checar se repository foi chamado
        verify(repository, times(1)).findAll(any(Pageable.class));
    }
}
```

**Teste 2: getById() throws se não encontra**

```java
@Test
public void testGetByIdNotFound() {
    // Arrange
    when(repository.findById(999L)).thenReturn(Optional.empty());
    
    // Act & Assert
    assertThrows(TaskNotFoundException.class, () -> {
        service.getById(999L);
    });
}
```

**Teste 3: create() valida e salva**

```java
@Test
public void testCreateTask() {
    // Arrange
    Task newTask = new Task(null, "Test task", "Desc", Status.PENDING, 1L, ...);
    Task saved = new Task(5L, "Test task", "Desc", Status.PENDING, 1L, ...);
    
    when(repository.save(any(Task.class))).thenReturn(saved);
    
    // Act
    Task result = service.create(newTask);
    
    // Assert
    assertEquals(5L, result.getId());
    assertNull(result.getId()); // Antes de save, ID é null
    verify(repository, times(1)).save(any(Task.class));
}
```

2. **Entender estrutura Arrange-Act-Assert**:
    - **Arrange:** Prepara dados fake com `new Task(...)` ou MockData
    - **Act:** Executa método que testa `service.getAll(...)`
    - **Assert:** Valida resultado `assertEquals(...)`
    - **Verify:** Checar se mock foi chamado correto `verify(repository).findAll(...)`
3. **Executar testes**:

```bash
mvn test
# Ou em IDE: click direito em classe → Run Tests
```


#### Por que fazer isso:

- Testes validam lógica sem BD real (rápido: ms vs segundos)
- Mock simula repository (você controla retorno)
- Encontra bugs antes de sábado
- Documenta comportamento esperado

***

### Tarefa 3: Escrever 2 Testes do TaskController

**Tempo:** 20 min
**Problema que resolve:** "Como testar HTTP endpoints?"

#### O que fazer:

1. **Criar TaskControllerTest** em `src/test/java/com/taskflow/controller/TaskControllerTest.java`:

**Teste 1: GET /api/v1/tasks retorna 200**

```java
@WebMvcTest(TaskController.class)
public class TaskControllerTest {
    
    @Autowired
    private MockMvc mockMvc; // Simula HTTP requests
    
    @MockBean
    private TaskService service;
    
    @Test
    public void testGetAllTasks() throws Exception {
        // Arrange
        Task task1 = new Task(1L, "Bug", "Email", Status.PENDING, 1L, ...);
        Page<Task> page = new PageImpl<>(Arrays.asList(task1));
        
        when(service.getAll(any(Pageable.class))).thenReturn(page);
        
        // Act & Assert
        mockMvc.perform(get("/api/v1/tasks"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content.title").value("Bug"));
    }
}
```

**Teste 2: POST /api/v1/tasks sem título retorna 400**

```java
@Test
public void testCreateTaskValidationError() throws Exception {
    // Arrange
    String invalidJson = "{ \"description\": \"No title\" }";
    
    // Act & Assert
    mockMvc.perform(post("/api/v1/tasks")
            .contentType(MediaType.APPLICATION_JSON)
            .content(invalidJson))
        .andExpect(status().isBadRequest());
}
```

2. **Importações necessárias**:

```java
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
```


#### Por que fazer isso:

- `MockMvc` = testa HTTP sem servidor real (rápido)
- Valida status codes (200, 400, 404)
- JSON parsing = garante API retorna JSON correto

***

### Tarefa 4: Testar GlobalExceptionHandler

**Tempo:** 15 min
**Problema que resolve:** "Erros retornam JSON estruturado?"

#### O que fazer:

1. **Teste: TaskNotFoundException retorna 404**

```java
@Test
public void testTaskNotFoundError() throws Exception {
    // Arrange
    when(service.getById(999L)).thenThrow(new TaskNotFoundException("Task not found"));
    
    // Act & Assert
    mockMvc.perform(get("/api/v1/tasks/999"))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.status").value(404))
        .andExpect(jsonPath("$.message").value("Task not found"));
}
```

2. **Teste: Validation error retorna 400**

```java
@Test
public void testValidationErrorResponse() throws Exception {
    String invalidJson = "{}"; // Vazio, sem title
    
    mockMvc.perform(post("/api/v1/tasks")
            .contentType(MediaType.APPLICATION_JSON)
            .content(invalidJson))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.status").value(400));
}
```


#### Por que fazer isso:

- Garante que errors retornam JSON (não HTML)
- Frontend consegue parsear error (não fica confuso)

***

### Tarefa 5: Rodar `mvn test` e Confirmar Tudo Passa

**Tempo:** 10 min
**Problema que resolve:** "Testes funciona mesmo?"

#### O que fazer:

1. **Executar**:

```bash
mvn test
```

    - Deve exibir: "BUILD SUCCESS" + "6 tests passed"
2. **Tentar fazer test falhar propositalmente**:
    - Mude `assertEquals(2, result.getContent().size())` para `assertEquals(3, ...)`
    - Rodar novamente: deve falhar (FAIL)
    - Voltar ao normal
3. **Coverage (opcional)**:

```bash
mvn jacoco:report
```

    - Gera relatório em `target/site/jacoco/index.html`
    - Mostra quanto % do código está coberto por testes

#### Por que fazer isso:

- Validar que testes realmente funcionam
- Coverage < 50% = risco, gaps de lógica não testada

***

## ✅ QUINTA-FEIRA: CHECKLIST DE CONCLUSÃO

```
SETUP
 ☑ spring-boot-starter-test em pom.xml
 ☑ Pasta src/test/java criada

TASKSERVICE TESTS (3+)
 ☑ Test getAll() → verifica Page retornado
 ☑ Test getById() with ID not found → throws TaskNotFoundException
 ☑ Test create() → validates and saves
 ☑ Test update() com merge de fields
 ☑ Test delete() com validação

TASKCONTROLLER TESTS (2+)
 ☑ Test GET /api/v1/tasks → 200 + JSON
 ☑ Test POST sem title → 400 + ErrorResponse
 ☑ Test GET /tasks/999 → 404 + ErrorResponse

ERROR HANDLING
 ☑ Test GlobalExceptionHandler captura TaskNotFoundException
 ☑ Test validation errors retornam 400 + ErrorResponse

EXECUTION
 ☑ mvn test → BUILD SUCCESS
 ☑ 6+ testes passando
 ☑ Coverage > 50% (opcional)

GIT
 ☑ Git commit: "test: add unit + integration tests"
```


***

# SÁBADO: FRONTEND INTEGRAÇÃO

## "Todos os 6 Endpoints Funcionam no Angular?"

**Duração:** 8 horas (09:00-17:00)
**Foco:** 3 componentes, consumir 6 endpoints do backend
**Entrada:** Backend 100% pronto de segunda a sexta
**Saída:** CRUD completo na UI (sem CSS bonito ainda)

***

## 🎯 Sábado = Integração, não Estilo

Você tem backend 100% testado. Sábado é **CONEXÃO**, não CSS:

- ✅ Listar tarefas (GET)
- ✅ Criar tarefa (POST)
- ✅ Editar tarefa (PUT)
- ✅ Deletar tarefa (DELETE)
- ✅ Filtrar por status
- ✅ Paginação
- ❌ Não gaste tempo com CSS (domingo é isso)

***

## 📋 TAREFAS DO SÁBADO (8 HORAS)

### Parte 1: Setup (9:00-10:00, 1h)

#### Tarefa 1: Estruturar Componentes e Services

**Tempo:** 20 min

**O que fazer:**

1. **Gerar componentes** (via CLI):

```bash
ng generate component components/task-list
ng generate component components/task-form
ng generate component components/task-status-badge
ng generate service services/task
```

2. **Estrutura resultante**:

```
src/app/
├── models/
│   └── task.model.ts         ← Interface Task
├── services/
│   └── task.service.ts       ← HTTP calls (6 métodos)
├── components/
│   ├── task-list/            ← Exibe tabela
│   ├── task-form/            ← Modal criar/editar
│   └── task-status-badge/    ← Cor do status
└── app.component.ts          ← Orquestra tudo
```

3. **Adicionar HttpClientModule** em app.module.ts (já fez segunda, mas confirmar):

```typescript
imports: [ BrowserModule, HttpClientModule, FormsModule ]
```


#### Por que fazer isso:

- Componentes separados = reúsa código
- Service centraliza HTTP (sem duplicação)
- Estrutura clara = fácil manter

***

#### Tarefa 2: Completar TaskService com 6 Métodos

**Tempo:** 15 min

**O que fazer:**

1. **Em `src/app/services/task.service.ts`, implementar**:

```typescript
import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Task, TaskPage } from '../models/task.model';

@Injectable({ providedIn: 'root' })
export class TaskService {
    private apiUrl = 'http://localhost:8080/api/v1/tasks';

    constructor(private http: HttpClient) {}

    // 1. List all (paginado)
    getTasks(page: number = 0, size: number = 10): Observable<TaskPage> {
        const params = new HttpParams()
            .set('page', page)
            .set('size', size);
        return this.http.get<TaskPage>(this.apiUrl, { params });
    }

    // 2. Get one by ID
    getTaskById(id: number): Observable<Task> {
        return this.http.get<Task>(`${this.apiUrl}/${id}`);
    }

    // 3. Create new
    createTask(task: Task): Observable<Task> {
        return this.http.post<Task>(this.apiUrl, task);
    }

    // 4. Update
    updateTask(id: number, task: Task): Observable<Task> {
        return this.http.put<Task>(`${this.apiUrl}/${id}`, task);
    }

    // 5. Delete
    deleteTask(id: number): Observable<void> {
        return this.http.delete<void>(`${this.apiUrl}/${id}`);
    }

    // 6. Filter by status
    getTasksByStatus(status: string, page: number = 0, size: number = 10): Observable<TaskPage> {
        const params = new HttpParams()
            .set('page', page)
            .set('size', size);
        return this.http.get<TaskPage>(`${this.apiUrl}/status/${status}`, { params });
    }
}
```

2. **Criar models** em `src/app/models/task.model.ts`:

```typescript
export interface Task {
    id?: number;
    title: string;
    description?: string;
    status: Status;
    assignedTo: number;
    createdAt?: string;
    updatedAt?: string;
}

export type Status = 'PENDING' | 'IN_PROGRESS' | 'COMPLETED';

export interface TaskPage {
    content: Task[];
    totalElements: number;
    totalPages: number;
    number: number; // current page
    size: number;
    hasNext: boolean;
    hasPrevious: boolean;
}
```


#### Por que fazer isso:

- 6 métodos = 6 endpoints que testes validaram
- HttpParams = query string automático (?page=0\&size=10)
- Interfaces TypeScript = segurança de tipos

***

#### Tarefa 3: Implementar TaskStatusBadgeComponent (Presentational)

**Tempo:** 10 min

**O que fazer:**

1. **`task-status-badge.component.ts`**:

```typescript
import { Component, Input } from '@angular/core';

@Component({
    selector: 'app-task-status-badge',
    templateUrl: './task-status-badge.component.html',
    styleUrls: ['./task-status-badge.component.css']
})
export class TaskStatusBadgeComponent {
    @Input() status: string = 'PENDING';

    getClass(): string {
        switch (this.status) {
            case 'PENDING': return 'badge-warning';
            case 'IN_PROGRESS': return 'badge-info';
            case 'COMPLETED': return 'badge-success';
            default: return 'badge-secondary';
        }
    }

    getLabel(): string {
        switch (this.status) {
            case 'PENDING': return '🔴 Pendente';
            case 'IN_PROGRESS': return '🟡 Em Progresso';
            case 'COMPLETED': return '🟢 Completo';
            default: return status;
        }
    }
}
```

2. **`task-status-badge.component.html`**:

```html
<span [ngClass]="'badge ' + getClass()">
    {{ getLabel() }}
</span>
```

3. **`task-status-badge.component.css`**:

```css
.badge {
    padding: 4px 8px;
    border-radius: 4px;
    font-weight: bold;
}
.badge-warning { background-color: #ffc107; color: black; }
.badge-info { background-color: #17a2b8; color: white; }
.badge-success { background-color: #28a745; color: white; }
.badge-secondary { background-color: #6c757d; color: white; }
```


#### Por que fazer isso:

- Componente presentational = só exibe dados
- Reutilizável em qualquer tabela (não acoplado)
- Cores tornam UI legível (sem CSS complexo)

***

### Parte 2: Listar Tarefas (10:00-12:00, 2h)

#### Tarefa 4: Implementar TaskListComponent (Read Only)

**Tempo:** 30 min

**O que fazer:**

1. **`task-list.component.ts`**:

```typescript
import { Component, OnInit } from '@angular/core';
import { TaskService } from '../../services/task.service';
import { Task, TaskPage } from '../../models/task.model';

@Component({
    selector: 'app-task-list',
    templateUrl: './task-list.component.html',
    styleUrls: ['./task-list.component.css']
})
export class TaskListComponent implements OnInit {
    tasks: Task[] = [];
    taskPage: TaskPage | null = null;
    loading = false;
    error = '';
    currentPage = 0;
    pageSize = 10;
    selectedStatus: string = '';

    constructor(private taskService: TaskService) {}

    ngOnInit() {
        this.loadTasks();
    }

    loadTasks() {
        this.loading = true;
        this.error = '';

        let request: Observable<TaskPage>;
        if (this.selectedStatus) {
            request = this.taskService.getTasksByStatus(this.selectedStatus, this.currentPage, this.pageSize);
        } else {
            request = this.taskService.getTasks(this.currentPage, this.pageSize);
        }

        request.subscribe({
            next: (page) => {
                this.taskPage = page;
                this.tasks = page.content;
                this.loading = false;
            },
            error: (err) => {
                this.error = 'Erro ao carregar tarefas: ' + err.message;
                this.loading = false;
            }
        });
    }

    filterByStatus(status: string) {
        this.selectedStatus = status;
        this.currentPage = 0; // Reset ao filtro
        this.loadTasks();
    }

    nextPage() {
        if (this.taskPage?.hasNext) {
            this.currentPage++;
            this.loadTasks();
        }
    }

    previousPage() {
        if (this.taskPage?.hasPrevious) {
            this.currentPage--;
            this.loadTasks();
        }
    }

    clearFilter() {
        this.selectedStatus = '';
        this.currentPage = 0;
        this.loadTasks();
    }
}
```

2. **`task-list.component.html`**:

```html
<div class="container">
    <h2>Task Flow Manager</h2>

    <!-- Filter buttons -->
    <div class="filter-buttons">
        <button (click)="filterByStatus('PENDING')" [class.active]="selectedStatus === 'PENDING'">
            🔴 Pendentes
        </button>
        <button (click)="filterByStatus('IN_PROGRESS')" [class.active]="selectedStatus === 'IN_PROGRESS'">
            🟡 Em Progresso
        </button>
        <button (click)="filterByStatus('COMPLETED')" [class.active]="selectedStatus === 'COMPLETED'">
            🟢 Completos
        </button>
        <button (click)="clearFilter()" *ngIf="selectedStatus">
            ✖ Limpar Filtro
        </button>
    </div>

    <!-- Error message -->
    <div *ngIf="error" class="error-message">
        {{ error }}
    </div>

    <!-- Loading spinner -->
    <div *ngIf="loading" class="loading">
        ⏳ Carregando...
    </div>

    <!-- Table -->
    <table *ngIf="!loading && tasks.length > 0">
        <thead>
            <tr>
                <th>ID</th>
                <th>Título</th>
                <th>Status</th>
                <th>Ações</th>
            </tr>
        </thead>
        <tbody>
            <tr *ngFor="let task of tasks">
                <td>{{ task.id }}</td>
                <td>{{ task.title }}</td>
                <td>
                    <app-task-status-badge [status]="task.status"></app-task-status-badge>
                </td>
                <td>
                    <button (click)="onEdit(task)">✏️ Editar</button>
                    <button (click)="onDelete(task.id)">🗑️ Deletar</button>
                </td>
            </tr>
        </tbody>
    </table>

    <!-- Empty state -->
    <div *ngIf="!loading && tasks.length === 0">
        Nenhuma tarefa encontrada.
    </div>

    <!-- Pagination -->
    <div *ngIf="taskPage" class="pagination">
        <button (click)="previousPage()" [disabled]="!taskPage.hasPrevious">← Anterior</button>
        <span>Página {{ taskPage.number + 1 }} de {{ taskPage.totalPages }}</span>
        <button (click)="nextPage()" [disabled]="!taskPage.hasNext">Próxima →</button>
    </div>
</div>
```

3. **`task-list.component.css` (básico, sem Bootstrap)*:

```css
.container {
    padding: 20px;
    max-width: 1000px;
    margin: 0 auto;
}

.filter-buttons {
    margin: 20px 0;
    display: flex;
    gap: 10px;
}

.filter-buttons button {
    padding: 8px 12px;
    border: 1px solid #ccc;
    background: white;
    cursor: pointer;
    border-radius: 4px;
}

.filter-buttons button.active {
    background: #007bff;
    color: white;
    border-color: #007bff;
}

table {
    width: 100%;
    border-collapse: collapse;
    margin: 20px 0;
}

table th, table td {
    padding: 10px;
    text-align: left;
    border-bottom: 1px solid #ddd;
}

table th {
    background-color: #f5f5f5;
    font-weight: bold;
}

table tr:hover {
    background-color: #f9f9f9;
}

table button {
    padding: 4px 8px;
    margin-right: 5px;
    cursor: pointer;
    border: none;
    background: white;
    border: 1px solid #ddd;
    border-radius: 3px;
}

table button:hover {
    background: #f0f0f0;
}

.error-message {
    background-color: #f8d7da;
    color: #721c24;
    padding: 10px;
    border-radius: 4px;
    margin: 10px 0;
}

.loading {
    text-align: center;
    padding: 20px;
    color: #666;
}

.pagination {
    display: flex;
    justify-content: center;
    gap: 10px;
    align-items: center;
    margin: 20px 0;
}

.pagination button:disabled {
    opacity: 0.5;
    cursor: not-allowed;
}
```


#### Por que fazer isso:

- Componente lê dados e exibe em tabela
- Subscribe ao Observable = quando dados chegam, table atualiza
- Paginação + filtro = requisitos spec
- Error handling = se backend cai, mostra mensagem (não trava)

***

#### Tarefa 5: Implementar TaskFormComponent (Create + Edit)

**Tempo:** 45 min

**O que fazer:**

1. **`task-form.component.ts`**:

```typescript
import { Component, Input, Output, EventEmitter, OnInit } from '@angular/core';
import { Task } from '../../models/task.model';

@Component({
    selector: 'app-task-form',
    templateUrl: './task-form.component.html',
    styleUrls: ['./task-form.component.css']
})
export class TaskFormComponent implements OnInit {
    @Input() task: Task | null = null;
    @Input() isVisible = false;
    @Output() save = new EventEmitter<Task>();
    @Output() cancel = new EventEmitter<void>();

    formData: Task = { title: '', description: '', status: 'PENDING', assignedTo: 1 };
    errors: string[] = [];
    isSubmitting = false;

    ngOnInit() {
        if (this.task) {
            this.formData = { ...this.task };
        } else {
            this.formData = { title: '', description: '', status: 'PENDING', assignedTo: 1 };
        }
    }

    ngOnChanges() {
        this.ngOnInit();
    }

    onSubmit() {
        this.errors = [];

        // Validação client-side
        if (!this.formData.title || this.formData.title.trim() === '') {
            this.errors.push('Título é obrigatório');
        }
        if (this.formData.title.length > 255) {
            this.errors.push('Título máximo 255 caracteres');
        }
        if (this.formData.description && this.formData.description.length > 1000) {
            this.errors.push('Descrição máximo 1000 caracteres');
        }

        if (this.errors.length > 0) {
            return; // Não envia se inválido
        }

        this.isSubmitting = true;
        this.save.emit(this.formData);
        this.isSubmitting = false;
        this.onCancel();
    }

    onCancel() {
        this.errors = [];
        this.formData = { title: '', description: '', status: 'PENDING', assignedTo: 1 };
        this.cancel.emit();
    }
}
```

2. **`task-form.component.html`**:

```html
<div *ngIf="isVisible" class="modal-overlay">
    <div class="modal">
        <h3>{{ task?.id ? 'Editar Tarefa' : 'Nova Tarefa' }}</h3>

        <!-- Errors -->
        <div *ngIf="errors.length > 0" class="errors">
            <div *ngFor="let error of errors" class="error-item">
                ❌ {{ error }}
            </div>
        </div>

        <!-- Form -->
        <form (ngSubmit)="onSubmit()">
            <div class="form-group">
                <label>Título *</label>
                <input
                    type="text"
                    [(ngModel)]="formData.title"
                    name="title"
                    placeholder="Título da tarefa"
                    maxlength="255"
                />
            </div>

            <div class="form-group">
                <label>Descrição</label>
                <textarea
                    [(ngModel)]="formData.description"
                    name="description"
                    placeholder="Descrição (opcional)"
                    maxlength="1000"
                    rows="3"
                ></textarea>
            </div>

            <div class="form-group">
                <label>Status</label>
                <select [(ngModel)]="formData.status" name="status">
                    <option value="PENDING">🔴 Pendente</option>
                    <option value="IN_PROGRESS">🟡 Em Progresso</option>
                    <option value="COMPLETED">🟢 Completo</option>
                </select>
            </div>

            <div class="form-group">
                <label>Atribuído a (User ID)</label>
                <input
                    type="number"
                    [(ngModel)]="formData.assignedTo"
                    name="assignedTo"
                    min="1"
                />
            </div>

            <div class="buttons">
                <button type="submit" [disabled]="isSubmitting">
                    {{ isSubmitting ? '⏳ Salvando...' : '💾 Salvar' }}
                </button>
                <button type="button" (click)="onCancel()" [disabled]="isSubmitting">
                    ✖ Cancelar
                </button>
            </div>
        </form>
    </div>
</div>
```

3. **`task-form.component.css`**:

```css
.modal-overlay {
    position: fixed;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background: rgba(0, 0, 0, 0.5);
    display: flex;
    justify-content: center;
    align-items: center;
    z-index: 1000;
}

.modal {
    background: white;
    padding: 20px;
    border-radius: 8px;
    width: 90%;
    max-width: 500px;
    box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
}

.form-group {
    margin-bottom: 15px;
}

.form-group label {
    display: block;
    margin-bottom: 5px;
    font-weight: bold;
}

.form-group input,
.form-group textarea,
.form-group select {
    width: 100%;
    padding: 8px;
    border: 1px solid #ddd;
    border-radius: 4px;
    font-size: 14px;
}

.errors {
    background-color: #f8d7da;
    padding: 10px;
    border-radius: 4px;
    margin-bottom: 15px;
}

.error-item {
    color: #721c24;
    margin-bottom: 5px;
}

.buttons {
    display: flex;
    gap: 10px;
    justify-content: flex-end;
}

.buttons button {
    padding: 10px 15px;
    border: none;
    border-radius: 4px;
    cursor: pointer;
    font-weight: bold;
}

.buttons button[type="submit"] {
    background-color: #28a745;
    color: white;
}

.buttons button[type="submit"]:hover {
    background-color: #218838;
}

.buttons button[type="button"] {
    background-color: #6c757d;
    color: white;
}

.buttons button[type="button"]:hover {
    background-color: #5a6268;
}

.buttons button:disabled {
    opacity: 0.6;
    cursor: not-allowed;
}
```


#### Por que fazer isso:

- Form modal = criar + editar (reutiliza)
- Validação client-side = feedback imediato
- Error messages = usuário sabe o que errou
- isSubmitting = button fica disabled durante requisição (UX melhor)

***

### Parte 3: CRUD Completo (12:00-15:00, 3h)

#### Tarefa 6: Integrar TaskFormComponent ao AppComponent

**Tempo:** 30 min

**O que fazer:**

1. **`app.component.ts` atualizado**:

```typescript
import { Component, OnInit } from '@angular/core';
import { TaskService } from './services/task.service';
import { Task } from './models/task.model';

@Component({
    selector: 'app-root',
    templateUrl: './app.component.html',
    styleUrls: ['./app.component.css']
})
export class AppComponent implements OnInit {
    showForm = false;
    taskToEdit: Task | null = null;
    toastMessage = '';
    toastType: 'success' | 'error' = 'success';

    constructor(private taskService: TaskService) {}

    ngOnInit() {
        // Componente exibe automaticamente lista
    }

    onNewTask() {
        this.taskToEdit = null;
        this.showForm = true;
    }

    onEditTask(task: Task) {
        this.taskToEdit = task;
        this.showForm = true;
    }

    onDeleteTask(id: number | undefined) {
        if (!id) return;
        if (!confirm('Tem certeza que quer deletar?')) return;

        this.taskService.deleteTask(id).subscribe({
            next: () => {
                this.showToast('Tarefa deletada com sucesso', 'success');
                this.listComponent?.loadTasks(); // Reload lista
            },
            error: (err) => {
                this.showToast('Erro ao deletar: ' + err.message, 'error');
            }
        });
    }

    onSaveTask(task: Task) {
        if (task.id) {
            // Update
            this.taskService.updateTask(task.id, task).subscribe({
                next: () => {
                    this.showToast('Tarefa atualizada com sucesso', 'success');
                    this.showForm = false;
                    this.listComponent?.loadTasks();
                },
                error: (err) => {
                    this.showToast('Erro ao atualizar: ' + err.message, 'error');
                }
            });
        } else {
            // Create
            this.taskService.createTask(task).subscribe({
                next: () => {
                    this.showToast('Tarefa criada com sucesso', 'success');
                    this.showForm = false;
                    this.listComponent?.loadTasks();
                },
                error: (err) => {
                    this.showToast('Erro ao criar: ' + err.message, 'error');
                }
            });
        }
    }

    onCancelForm() {
        this.showForm = false;
        this.taskToEdit = null;
    }

    showToast(message: string, type: 'success' | 'error') {
        this.toastMessage = message;
        this.toastType = type;
        setTimeout(() => {
            this.toastMessage = '';
        }, 3000);
    }

    @ViewChild(TaskListComponent) listComponent!: TaskListComponent;
}
```

2. **`app.component.html`**:

```html
<div class="app">
    <!-- Toast notification -->
    <div *ngIf="toastMessage" [ngClass]="'toast ' + toastType">
        {{ toastMessage }}
    </div>

    <!-- Header -->
    <header>
        <h1>📋 Task Flow Manager</h1>
        <button (click)="onNewTask()" class="btn-new-task">
            ➕ Nova Tarefa
        </button>
    </header>

    <!-- Task list -->
    <app-task-list
        #listComponent
        (onEdit)="onEditTask($event)"
        (onDelete)="onDeleteTask($event)"
    ></app-task-list>

    <!-- Form modal -->
    <app-task-form
        [task]="taskToEdit"
        [isVisible]="showForm"
        (save)="onSaveTask($event)"
        (cancel)="onCancelForm()"
    ></app-task-form>
</div>
```

3. **`app.component.css`**:

```css
.app {
    font-family: Arial, sans-serif;
    background-color: #f5f5f5;
    min-height: 100vh;
}

header {
    background-color: #007bff;
    color: white;
    padding: 20px;
    display: flex;
    justify-content: space-between;
    align-items: center;
    box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

header h1 {
    margin: 0;
    font-size: 28px;
}

.btn-new-task {
    padding: 10px 15px;
    background-color: white;
    color: #007bff;
    border: none;
    border-radius: 4px;
    cursor: pointer;
    font-weight: bold;
    font-size: 14px;
}

.btn-new-task:hover {
    background-color: #f0f0f0;
}

.toast {
    position: fixed;
    top: 20px;
    right: 20px;
    padding: 15px 20px;
    border-radius: 4px;
    color: white;
    font-weight: bold;
    z-index: 2000;
    animation: slideIn 0.3s ease;
}

.toast.success {
    background-color: #28a745;
}

.toast.error {
    background-color: #dc3545;
}

@keyframes slideIn {
    from {
        transform: translateX(400px);
        opacity: 0;
    }
    to {
        transform: translateX(0);
        opacity: 1;
    }
}
```


#### Por que fazer isso:

- AppComponent orquestra tudo
- Toast = feedback visual (usuário sabe que operação funcionou)
- Calls a loadTasks() após CRUD = tabela atualiza
- Modal padrão = UX familiar

***

#### Tarefa 7: Testar Integração Full-Stack

**Tempo:** 1h 30 min

**O que fazer:**

1. **Iniciar ambos**:

```bash
# Terminal 1
mvn spring-boot:run

# Terminal 2
ng serve
```

2. **Testar cada operação**:
    - ✅ Listar: Carrega 5 tasks da seed
    - ✅ Criar: Clica botão, abre modal, preenche, clica salvar → task aparece na lista
    - ✅ Editar: Clica editar, muda status, salva → lista atualiza
    - ✅ Deletar: Clica deletar, confirma → task some
    - ✅ Filtrar: Clica "PENDING" → mostra apenas pendentes
    - ✅ Paginação: Se > 10 tasks, mostra "Próxima"
    - ✅ Erro: Muda backend porta, tenta criar → toast "Erro ao conectar"
3. **Console do navegador**:
    - Não deve ter erros vermelhos
    - Network tab deve mostrar GET/POST/PUT/DELETE para http://localhost:8080/api/v1/tasks

#### Por que fazer isso:

- Validar que tudo funciona end-to-end
- Descobrir bugs agora = simples, descobrir domingo = tarde

***

## ✅ SÁBADO: CHECKLIST DE CONCLUSÃO

```
SETUP (1h)
 ☑ Componentes gerados (task-list, task-form, task-status-badge)
 ☑ TaskService com 6 métodos (getTasks, getTaskById, createTask, updateTask, deleteTask, getTasksByStatus)
 ☑ Task + TaskPage interfaces criadas
 ☑ HttpClientModule + FormsModule em app.module

TASK STATUS BADGE (10min)
 ☑ Componente exibe status com cores (🔴 🟡 🟢)
 ☑ Reutilizável

TASK LIST COMPONENT (1h 30min)
 ☑ Tabela com ID, Título, Status, Ações
 ☑ GET /api/v1/tasks carrega tasks
 ☑ Subscribe no Observable, exibe na tabela
 ☑ Filtro por status (PENDING, IN_PROGRESS, COMPLETED)
 ☑ Paginação (anterior/próxima)
 ☑ Loading spinner durante requisição
 ☑ Error message se backend cai
 ☑ Botões Editar + Deletar

TASK FORM COMPONENT (1h)
 ☑ Modal com campos: title, description, status, assignedTo
 ☑ Validação client-side (title obrigatório, max lengths)
 ☑ Modo criar (sem ID inicial)
 ☑ Modo editar (pre-fill com task atual)
 ☑ Submit emite para parent (AppComponent)
 ☑ Cancel fecha modal

APP COMPONENT (1h)
 ☑ Orquestra TaskList + TaskForm
 ☑ "Nova Tarefa" button abre form
 ☑ onEditTask(task) abre form com task
 ☑ onSaveTask(task) chama create ou update
 ☑ onDeleteTask(id) chama delete + reload
 ☑ Toast notification (sucesso/erro)

INTEGRAÇÃO (1h 30min)
 ☑ List: GET /api/v1/tasks → tabela exibe tarefas
 ☑ Create: POST → nova task aparece na lista
 ☑ Edit: PUT → lista atualiza
 ☑ Delete: DELETE → task desaparece
 ☑ Filter: GET /status/PENDING → tabela filtra
 ☑ Pagination: Next/Previous funciona
 ☑ Error: Backend down → toast mostra erro (não trava)
 ☑ Console: Sem erros vermelhos
 ☑ Network: Requisições para /api/v1/tasks aparecem

GIT
 ☑ Git commit: "feat: frontend CRUD integration"
```


***

# DOMINGO: POLIMENTO + DOCS

## "MVP Pronto para Portfólio?"

**Duração:** 8 horas (09:00-17:00)
**Foco:** CSS Bootstrap, tratamento de erros, README profissional
**Entrada:** CRUD 100% funcional de sábado
**Saída:** MVP pronto para mostrar em entrevista

***

## 📋 TAREFAS DO DOMINGO (8 HORAS)

### Parte 1: Estilização Básica (09:00-12:00, 3h)

#### Tarefa 1: Adicionar Bootstrap 5

**Tempo:** 15 min

**O que fazer:**

1. **Instalar Bootstrap**:

```bash
npm install bootstrap
```

2. **Importar em `styles.css` (raiz do app)**:

```css
@import '~bootstrap/dist/css/bootstrap.min.css';
```

3. **Usar classes Bootstrap em componentes**:

```
- `<button class="btn btn-primary">` em vez de `<button>`
```

```
- `<table class="table table-hover">` em vez de `<table>`
```

```
- `<div class="alert alert-danger">` em vez de `<div class="error">`
```


#### Por que fazer isso:

- Bootstrap = CSS profissional pronto
- Sem Bootstrap, UI fica amadora (cor ruim, spacing horrível)
- 15min = resultado 1000% melhor

***

#### Tarefa 2: Aplicar Bootstrap a Components

**Tempo:** 1h 30min

**O que fazer:**

1. **TaskListComponent - Usar Bootstrap classes**:

```html
<div class="container mt-4">
    <h2 class="mb-4">📋 Task Flow Manager</h2>

    <!-- Filters -->
    <div class="btn-group mb-3" role="group">
        <button type="button" class="btn btn-outline-warning" (click)="filterByStatus('PENDING')">
            🔴 Pendentes
        </button>
        <button type="button" class="btn btn-outline-info" (click)="filterByStatus('IN_PROGRESS')">
            🟡 Em Progresso
        </button>
        <button type="button" class="btn btn-outline-success" (click)="filterByStatus('COMPLETED')">
            🟢 Completos
        </button>
        <button type="button" class="btn btn-outline-secondary" (click)="clearFilter()" *ngIf="selectedStatus">
            ✖ Limpar
        </button>
    </div>

    <!-- Errors -->
    <div *ngIf="error" class="alert alert-danger alert-dismissible fade show" role="alert">
        {{ error }}
    </div>

    <!-- Loading -->
    <div *ngIf="loading" class="text-center">
        <div class="spinner-border" role="status">
            <span class="visually-hidden">Carregando...</span>
        </div>
    </div>

    <!-- Table -->
    <table *ngIf="!loading && tasks.length > 0" class="table table-hover table-striped">
        <thead class="table-dark">
            <tr>
                <th>ID</th>
                <th>Título</th>
                <th>Status</th>
                <th>Ações</th>
            </tr>
        </thead>
        <tbody>
            <tr *ngFor="let task of tasks">
                <td>{{ task.id }}</td>
                <td>{{ task.title }}</td>
                <td>
                    <app-task-status-badge [status]="task.status"></app-task-status-badge>
                </td>
                <td>
                    <button class="btn btn-sm btn-primary" (click)="onEdit(task)">✏️</button>
                    <button class="btn btn-sm btn-danger" (click)="onDelete(task.id)">🗑️</button>
                </td>
            </tr>
        </tbody>
    </table>

    <!-- Pagination -->
    <nav *ngIf="taskPage && tasks.length > 0">
        <ul class="pagination justify-content-center">
            <li class="page-item" [class.disabled]="!taskPage.hasPrevious">
                <button class="page-link" (click)="previousPage()" [disabled]="!taskPage.hasPrevious">
                    Anterior
                </button>
            </li>
            <li class="page-item active">
                <span class="page-link">{{ taskPage.number + 1 }} de {{ taskPage.totalPages }}</span>
            </li>
            <li class="page-item" [class.disabled]="!taskPage.hasNext">
                <button class="page-link" (click)="nextPage()" [disabled]="!taskPage.hasNext">
                    Próxima
                </button>
            </li>
        </ul>
    </nav>
</div>
```

2. **TaskFormComponent - Usar Bootstrap**:

```html
<div *ngIf="isVisible" class="modal fade show" style="display: block; background-color: rgba(0,0,0,0.5);">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title">
                    {{ task?.id ? 'Editar Tarefa' : 'Nova Tarefa' }}
                </h5>
                <button type="button" class="btn-close" (click)="onCancel()"></button>
            </div>
            <div class="modal-body">
                <!-- Errors -->
                <div *ngIf="errors.length > 0" class="alert alert-danger">
                    <div *ngFor="let error of errors">❌ {{ error }}</div>
                </div>

                <!-- Form -->
                <form (ngSubmit)="onSubmit()">
                    <div class="mb-3">
                        <label class="form-label">Título *</label>
                        <input
                            type="text"
                            class="form-control"
                            [(ngModel)]="formData.title"
                            name="title"
                            maxlength="255"
                        />
                    </div>

                    <div class="mb-3">
                        <label class="form-label">Descrição</label>
                        <textarea
                            class="form-control"
                            [(ngModel)]="formData.description"
                            name="description"
                            maxlength="1000"
                            rows="3"
                        ></textarea>
                    </div>

                    <div class="mb-3">
                        <label class="form-label">Status</label>
                        <select class="form-select" [(ngModel)]="formData.status" name="status">
                            <option value="PENDING">🔴 Pendente</option>
                            <option value="IN_PROGRESS">🟡 Em Progresso</option>
                            <option value="COMPLETED">🟢 Completo</option>
                        </select>
                    </div>

                    <div class="mb-3">
                        <label class="form-label">Atribuído a (User ID)</label>
                        <input
                            type="number"
                            class="form-control"
                            [(ngModel)]="formData.assignedTo"
                            name="assignedTo"
                            min="1"
                        />
                    </div>
                </form>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-secondary" (click)="onCancel()">
                    ✖ Cancelar
                </button>
                <button
                    type="button"
                    class="btn btn-success"
                    (click)="onSubmit()"
                    [disabled]="isSubmitting"
                >
                    {{ isSubmitting ? '⏳ Salvando...' : '💾 Salvar' }}
                </button>
            </div>
        </div>
    </div>
</div>
```

3. **AppComponent header - Usar Bootstrap**:

```html
<nav class="navbar navbar-dark bg-primary">
    <div class="container-fluid">
        <span class="navbar-brand mb-0 h1">📋 Task Flow Manager</span>
        <button class="btn btn-light" (click)="onNewTask()">
            ➕ Nova Tarefa
        </button>
    </div>
</nav>

<!-- Toast -->
<div class="position-fixed top-0 end-0 p-3" style="z-index: 11">
    <div *ngIf="toastMessage" [class]="'toast show alert ' + (toastType === 'success' ? 'alert-success' : 'alert-danger')" role="alert">
        {{ toastMessage }}
    </div>
</div>

<!-- Components -->
<div class="container mt-4">
    <app-task-list ...></app-task-list>
    <app-task-form ...></app-task-form>
</div>
```


#### Por que fazer isso:

- Bootstrap classes = UI profissional em minutos
- Cores, spacing, borders automáticos
- Responsivo (mobile-friendly)

***

#### Tarefa 3: Melhorar TaskStatusBadgeComponent com Bootstrap Badges

**Tempo:** 15 min

**O que fazer:**

```typescript
// task-status-badge.component.ts
getClass(): string {
    switch (this.status) {
        case 'PENDING': return 'badge bg-warning text-dark';
        case 'IN_PROGRESS': return 'badge bg-info';
        case 'COMPLETED': return 'badge bg-success';
        default: return 'badge bg-secondary';
    }
}
```

```html
<!-- task-status-badge.component.html -->
<span [ngClass]="getClass()">
    {{ getLabel() }}
</span>
```


***

### Parte 2: Error Handling + Validação (12:00-14:00, 2h)

#### Tarefa 4: Melhorar Tratamento de Erros Backend

**Tempo:** 30 min

**No frontend:**

1. **Mostrar erro específico do backend**:

```typescript
// task-list.component.ts
error: string => {
    if (error.status === 404) {
        this.error = 'Tarefa não encontrada';
    } else if (error.status === 400) {
        this.error = 'Dados inválidos. Verifique os campos.';
    } else if (error.status === 500) {
        this.error = 'Erro no servidor. Tente novamente.';
    } else if (error.status === 0) {
        this.error = 'Erro de conexão. Backend não respondeu.';
    } else {
        this.error = `Erro: ${error.error?.message || error.statusText}`;
    }
}
```

2. **Validação client-side robusta**:

```typescript
// task-form.component.ts
onSubmit() {
    this.errors = [];

    if (!this.formData.title?.trim()) {
        this.errors.push('Título é obrigatório');
    } else if (this.formData.title.length > 255) {
        this.errors.push('Título máximo 255 caracteres');
    }

    if (this.formData.description && this.formData.description.length > 1000) {
        this.errors.push('Descrição máximo 1000 caracteres');
    }

    if (!this.formData.assignedTo || this.formData.assignedTo < 1) {
        this.errors.push('User ID deve ser > 0');
    }

    if (this.errors.length > 0) {
        return;
    }

    // Envia apenas se válido
    this.save.emit(this.formData);
}
```


#### Por que fazer isso:

- Usuário sabe exatamente o que errou
- Não manda requisição inválida pro backend
- UX melhor (feedback claro)

***

#### Tarefa 5: Testar Cenários de Erro

**Tempo:** 30 min

**O que fazer:**

1. **Backend down**:
    - Parar `mvn spring-boot:run`
    - Tentar criar tarefa → deve mostrar toast "Erro de conexão"
    - Frontend não trava
2. **Entrada inválida**:
    - Deixar título vazio, clicar salvar → mostra "Título é obrigatório"
    - Não envia request
3. **Tarefa não encontrada**:
    - Editar URL em Postman: GET http://localhost:8080/api/v1/tasks/999
    - Verifica que retorna 404 + ErrorResponse
    - Frontend mostra mensagem amigável

#### Por que fazer isso:

- MVP robusto = aguenta mau comportamento do usuário
- Em entrevista, mostrar error handling = profissionalismo

***

### Parte 3: Documentação (14:00-16:00, 2h)

#### Tarefa 6: Criar README.md Profissional

**Tempo:** 45 min

**O que fazer:**

1. **Criar `README.md`** na raiz do projeto:
```markdown
# Task Flow MVP

Sistema de gestão de tarefas full-stack desenvolvido em uma semana com Spring Boot + Angular.

## 🎯 Objetivo

Demonstrar competência em:
- ✅ Integração backend-frontend
- ✅ REST API com validação
- ✅ Testes automatizados (JUnit + Jasmine)
- ✅ Documentação profissional
- ✅ Git semântico

## 🚀 Quick Start

### Pré-requisitos

- Java 17+
- Node.js 18+
- PostgreSQL 15+
- Maven 3.8+
- Angular CLI 16+

### Instalação

#### 1. Backend (Spring Boot)

```bash
# Clone o repositório
git clone https://github.com/seu-usuario/task-flow.git
cd task-flow/backend

# Rode o banco (docker-compose)
docker-compose up -d

# Build e run
mvn clean install
mvn spring-boot:run

# API estará em http://localhost:8080
```


#### 2. Frontend (Angular)

```bash
cd ../frontend

# Instale dependências
npm install

# Rode o servidor
ng serve

# Acesse em http://localhost:4200
```


## 📊 API Endpoints

### GET /api/v1/tasks

Retorna lista paginada de tarefas.

**Query Params:**

- `page` (default 0)
- `size` (default 10, max 100)
- `sort` (ex: `createdAt,desc`)

**Response (200):**

```json
{
  "content": [
    {
      "id": 1,
      "title": "Bug fix",
      "description": "Email field",
      "status": "PENDING",
      "assignedTo": 1,
      "createdAt": "2026-01-20T15:00:00",
      "updatedAt": "2026-01-20T15:00:00"
    }
  ],
  "totalElements": 5,
  "totalPages": 1,
  "number": 0,
  "size": 10,
  "hasNext": false,
  "hasPrevious": false
}
```


### GET /api/v1/tasks/{id}

Retorna uma tarefa específica.

**Response (200):** Task object (vide exemplo acima)
**Response (404):** Not Found

### POST /api/v1/tasks

Cria nova tarefa.

**Body:**

```json
{
  "title": "Nova tarefa",
  "description": "Descrição opcional",
  "status": "PENDING",
  "assignedTo": 1
}
```

**Response (201):** Task com ID gerado
**Response (400):** Validation error

### PUT /api/v1/tasks/{id}

Atualiza tarefa existente.

**Body:** Idem POST
**Response (200):** Task atualizado
**Response (404):** Not Found

### DELETE /api/v1/tasks/{id}

Deleta tarefa.

**Response (204):** No Content
**Response (404):** Not Found

### GET /api/v1/tasks/status/{status}

Filtra tarefas por status.

**Path Params:**

- `status`: PENDING | IN_PROGRESS | COMPLETED

**Response (200):** Page<Task>

## 🗄️ Banco de Dados

### Schema

```sql
-- Usuários
CREATE TABLE users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Tarefas
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


## 🧪 Testes

### Backend

```bash
cd backend
mvn test
```

Cobre:

- TaskService CRUD (3+ testes)
- TaskController endpoints (2+ testes)
- GlobalExceptionHandler (2+ testes)


### Frontend

```bash
cd frontend
ng test
```

Cobre:

- TaskService (HTTP calls)
- Components (rendering, events)


## 📁 Estrutura do Projeto

```
task-flow/
├── backend/                 # Spring Boot
│   ├── src/
│   │   ├── main/
│   │   │   └── java/com/taskflow/
│   │   │       ├── entity/
│   │   │       ├── repository/
│   │   │       ├── service/
│   │   │       ├── controller/
│   │   │       ├── exception/
│   │   │       └── config/
│   │   └── test/
│   ├── pom.xml
│   └── docker-compose.yml
│
└── frontend/                # Angular
    ├── src/
    │   ├── app/
    │   │   ├── models/
    │   │   ├── services/
    │   │   ├── components/
    │   │   │   ├── task-list/
    │   │   │   ├── task-form/
    │   │   │   └── task-status-badge/
    │   │   └── app.component.*
    │   └── styles.css
    └── package.json
```


## 🔧 Tecnologias

**Backend:**

- Spring Boot 3.1
- JPA/Hibernate
- PostgreSQL
- Maven
- JUnit 5 + Mockito

**Frontend:**

- Angular 16
- Bootstrap 5
- TypeScript
- Jasmine (testes)

**DevOps:**

- Docker + Docker Compose
- Git (semântico)


## 📝 Git Workflow

Commits seguem padrão semântico:

```bash
git commit -m "feat: add CRUD endpoints"
git commit -m "fix: validate task title on create"
git commit -m "test: add TaskService tests"
git commit -m "docs: update API documentation"
```

Total de commits: 50+

## 🎓 Aprendizados

- ✅ Arquitetura 3-layer (Controller → Service → Repository)
- ✅ Integração front-back com CORS
- ✅ Testes unitários + integração
- ✅ Tratamento de erros estruturado
- ✅ Gestão de estado em Angular (Observables)
- ✅ Versionamento de API (/api/v1)
- ✅ Docker para deploy
- ✅ Documentação técnica


## 🚀 Próximos Passos

- [ ] Adicionar autenticação JWT
- [ ] Implementar WebSocket para atualizações em tempo real
- [ ] Testes de carga (k6 ou JMeter)
- [ ] Deploy em Kubernetes
- [ ] UI em Material Design
- [ ] Internacionalização (i18n)


## 📧 Contato

[Seu nome] - [seu-email@exemplo.com]
LinkedIn: [seu-linkedin]
GitHub: [seu-github]

---

**Desenvolvido em 1 semana (26h) como MVP para portfólio de estágio.**

```

#### Por que fazer isso:
- README = primeira coisa que recrutador lê
- Mostra estrutura, tecnologias, como rodar
- Credibilidade profissional

***

#### Tarefa 7: Criar POSTMAN Collection JSON
**Tempo:** 30 min

**O que fazer:**
1. **Em Postman**: File → Export → Save como `TaskFlow-API.postman_collection.json`
2. **Incluir no repo**: `backend/docs/postman-collection.json`
3. **Instruções no README**: "Import this collection in Postman → File → Import"

#### Por que fazer isso:
- Outros devs conseguem testar API sem configurar nada
- Documentação executável

***

#### Tarefa 8: Adicionar CONTRIBUTING.md (opcional)
**Tempo:** 15 min

**O que fazer:**
```markdown
# Contribuindo

## Setup Local

1. Clone repo
2. Backend: `cd backend && mvn install`
3. Frontend: `cd frontend && npm install`
4. Docker: `docker-compose up -d`
5. Backend: `mvn spring-boot:run`
6. Frontend: `ng serve`

## Regras de Código

- Commits semânticos: `feat:`, `fix:`, `test:`, `docs:`
- Sem console.log em produção
- Testes para novas features
- SonarQube coverage > 50%

## Pull Requests

1. Branch: `feature/task-name`
2. Commit: semântico
3. Tests: passando
4. PR description: claro e conciso

---

**Obrigado pela contribuição!**
```


***

### Parte 4: Validação Final (16:00-17:00, 1h)

#### Tarefa 9: Checklist Final de MVP

**Tempo:** 30 min

**Validar:**

✅ Backend:

- 6 endpoints funcionam (GET list, GET by-id, POST, PUT, DELETE, GET filter)
- Validações working (@NotBlank, @NotNull, @Size)
- Error handling com GlobalExceptionHandler (400, 404, 500)
- 6+ testes passam (mvn test)
- Logs no server
- docker-compose up funciona

✅ Frontend:

- Angular carrega sem erro
- TaskService com 6 métodos
- TaskListComponent exibe tabela
- TaskFormComponent abre/fecha modal
- Create/Edit/Delete funcionam
- Pagination funciona
- Filter por status funciona
- Toast notifications exibem
- Bootstrap CSS aplicado
- Error handling (backend down = mensagem)

✅ Integração:

- GET http://localhost:4200 exibe lista do backend
- POST → nova tarefa aparece
- PUT → atualiza
- DELETE → remove
- Paginação navega corretamente
- Filter retorna dados corretos
- Console: zero erros vermelhos
- Network: requisições para /api/v1/tasks aparecem

✅ Documentação:

- README.md completo
- API endpoints documentados
- Setup instructions claros
- Tecnologias listadas
- Estrutura do projeto diagramada

✅ Git:

- 50+ commits semânticos
- Histórico limpo
- .gitignore completo
- Repo público no GitHub

***

#### Tarefa 10: Git Final + Versioning

**Tempo:** 30 min

**O que fazer:**

1. **Último commit**:

```bash
git add .
git commit -m "docs: add README, API docs, finalize MVP"
```

2. **Tag de release** (opcional, mas profissional):

```bash
git tag -a v1.0.0 -m "Task Flow MVP v1.0.0 - Complete"
git push origin v1.0.0
```

3. **GitHub: Adicionar description do repo**:
    - Description: "Full-stack task management MVP (Spring Boot + Angular)"
    - Topics: `spring-boot`, `angular`, `rest-api`, `full-stack`, `portfolio`
    - Link para README

***

## ✅ DOMINGO: CHECKLIST DE CONCLUSÃO

```
ESTILIZAÇÃO
 ☑ Bootstrap 5 instalado
 ☑ Classes Bootstrap aplicadas (btn, table, modal, badges, alerts)
 ☑ Toast notifications estilizadas
 ☑ Layout responsivo
 ☑ Cores + spacing profissional

ERROR HANDLING
 ☑ Frontend trata 404, 400, 500 com mensagens amigáveis
 ☑ Validação client-side (título obrigatório, max lengths)
 ☑ Backend down → toast "Erro de conexão" (não trava)
 ☑ Todos cenários testados

DOCUMENTAÇÃO
 ☑ README.md com Quick Start
 ☑ 6 endpoints documentados com exemplos JSON
 ☑ Schema do banco descrito
 ☑ Tecnologias listadas
 ☑ Estrutura do projeto diagramada
 ☑ Aprendizados mencionados
 ☑ Próximos passos listados

POSTMAN
 ☑ Collection exportada
 ☑ Incluída no repo

GIT FINAL
 ☑ 50+ commits semânticos
 ☑ Tag v1.0.0
 ☑ GitHub com description + topics
 ☑ README renderiza corretamente no GitHub

SANIDADE FINAL
 ☑ mvn test → BUILD SUCCESS (6+ tests)
 ☑ ng serve → zero console errors
 ☑ CRUD completo funciona
 ☑ Sem console.log em código
 ☑ Sem hardcoded URLs (localhost em dev ok)
 ☑ Docker compose funciona
 ☑ README claro para setup

RESULTADO
 ✅ MVP 100% pronto para portfólio
 ✅ Profissional + documentado
 ✅ Diferencia 70% dos candidatos
 ✅ Ready para entrevista demo
```


***

# RESUMO EXECUTIVO DA SEMANA

## Segunda-feira: Walking Skeleton ✅

**Ganho:** Backend GET + Angular consume + console exibe = Arquitetura validada
**Risco:** Se falhar, semana perdida
**Resultado:** ✅ Integração funciona

## Terça-feira: Backend Core ✅

**Ganho:** 6 métodos Service + validações + error handling
**Foco:** Solidez (não velocidade)
**Resultado:** ✅ Backend pronto para produção

## Quarta-feira: Testes ✅

**Ganho:** 6+ testes JUnit = confiança
**Foco:** Validar regras de negócio
**Resultado:** ✅ Bugs encontrados antes de integração

## Quinta-feira: (Reservado para Debug/Buffer)

**Nota:** Se tudo correr bem segunda a quarta, quinta é polimento backend

## Sábado: Frontend Integração ✅

**Ganho:** CRUD completo na UI
**Foco:** Conectar, não embelezar
**Resultado:** ✅ Full-stack funcional

## Domingo: Polimento ✅

**Ganho:** UI profissional + docs
**Foco:** Portfólio
**Resultado:** ✅ MVP pronto para mostrar

***

# DICAS DO TECH LEAD

1. **Segunda = Gate crítico**. Se não passar, debug imediatamente. Não durma sem solução.
2. **Terça-Sexta = Backend sacred**. Não toque no HTML/CSS. Backend sólido = tranquilidade sábado.
3. **Sábado = Maratona**. Troque o horário (9:00-17:00). Deixe notebook ligado. Não distraia.
4. **Domingo = Polimento rápido**. Bootstrap minutos. Docs metade do tempo. Focar em README + postman.
5. **Console do navegador = primeira ferramenta de debug**. Sempre verificar antes de culpar backend.
6. **Postman = seu amigo**. Se API funciona no Postman mas não em Angular, problema é frontend (CORS, tipos, etc).
7. **Git commits frequentes**. Não chegue no domingo sem 30+ commits. Risco de perder trabalho.
8. **Se travar, pule**. Não gaste 2h debugando. Siga adiante, volte depois.
9. **Sábado à noite, valide integração**. Se quebrou, domingo conserta rápido.
10. **domingo 17:00 = fim**. Se faltar algo, é post-MVP. Enviou o que promete = sucesso.

***

# BOM TRABALHO! 🚀

Você tem uma semana sólida de trabalho pela frente. Foco em arquitetura, testes, documentação.

Resultado: MVP que diferencia você de 70% dos candidatos.

**Domingo à noite, você terá 1 projeto full-stack pronto para entrevista.**