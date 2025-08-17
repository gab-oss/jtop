# **JTOP - slim "top" REST API - get information on processes and kill them**

## Getting Started

---

### 1. Prerequisites
- **Java 24+** 
- **Maven 3.8+**

### 2. Build the project
```bash
git clone https://github.com/gab-oss/jtop.git
cd jtop
mvn clean install
```
Find the file _src/main/resources/user.properties.example_, set a username and password and rename it to "user.properties".

### 3. Run
Run with Maven:
```bash
mvn spring-boot:run
```
or run the packaged jar (look for its name in the _target_ directory):
```bash
java -jar target/<current-build>.jar
```

## Usage

---

### 4.  Processes API
The application runs at http://localhost:8080.

#### Process's JSON

For each process, the response body provides you with the following:
- ID in the system (PID),
- command,
- owner user's name,
- state, 
- Resident Set Size (RSS) - how much memory is allocated to that process in RAM, in bytes,
- the fraction of CPU time used by the process since it started, cumulative. The value is between 0.0 and 1.0 per logical CPU. Multiply by 100 to get a value consistent with what's presented in _top_ (%CPU).

Example:
```
{"pid":174069,<br>
"command":"firefox",<br>
"owner":"user1",<br>
"state":"SLEEPING",<br>
"residentSetSize":70332416,<br>
"cumulativeCpu":0.0028041276759389823}
```
#### API Endpoints

| Method | Endpoint                                 | Description                                                           | Response body (200 OK) | Expected errors                                                                                                                                                         | Example call                                                     |
|--------|------------------------------------------|-----------------------------------------------------------------------|-----------------|-------------------------------------------------------------------------------------------------------------------------------------------------------------------------|------------------------------------------------------------------|
| GET    | `/processes`                             | List all running processes                                            | JSON array of process objects |                                                                                                                                                                         | curl --user user:password http://localhost:8080/processes        
| 
| GET    | `/processes/{pid}`                       | Get details of a process with the provided PID                        | JSON object                | 404 - Not Found - no running process with this PID                                                                                                                      | curl --user user:password http://localhost:8080/processes/165895 
| 
| POST   | `/processes/{id}/terminate[?force=true]` | Kill a process by PID - SIGKILL with _?force=true_, otherwise SIGTERM | Empty                | 404 - Not Found - no running process with this PID <br> <br> 403 - Forbidden - no permission to kill this process <br> <br> 409 - Conflict - process couldn't be killed | curl --user user:password http://localhost:8080/processes/165895/terminate                                       |

### Logs
To make troubleshooting and finding mistakes easier, each attempt to kill a process is logged in a H2 database, in ACTION_LOG table.

#### ACTION_LOG
- PID 
- LOGTIME - timestamp for the attempt,
- COMMAND,
- OWNER,
- ACTION - the signal you tried to send (SIGKILL ot SIGTERM),
- COMMENT - the result of the attempt, one of: SUCCESS, FAILED, NO_PERMISSION, CURRENT_PROCESS (tried to kill JTOP), NOT_FOUND.

#### Access
Go to http://localhost:8080/h2-console and log in with:
- JDBC URL: jdbc:h2:mem:testdb,
- username: sa,
- empty password.

You can change the default parameters in _src/main/resources/application.properties_.
H2 is an in-memory database, so once you stop the application, it will lose all data.
