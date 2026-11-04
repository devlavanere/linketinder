<p align="center">
  <img src="https://images.unsplash.com/photo-1558494949-ef010cbdcc31?auto=format&fit=crop&w=1000&q=80" alt="Backend API REST Server" width="100%" style="border-radius: 10px;">
</p>

<p align="center">
  <img src="https://img.shields.io/badge/Groovy-4298B8?style=for-the-badge&logo=apachegroovy&logoColor=white" alt="Groovy">
  <img src="https://img.shields.io/badge/Apache_Tomcat-F8DC75?style=for-the-badge&logo=apachetomcat&logoColor=black" alt="Tomcat">
  <img src="https://img.shields.io/badge/Java_Servlets-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white" alt="Java Servlets">
  <img src="https://img.shields.io/badge/PostgreSQL-316192?style=for-the-badge&logo=postgresql&logoColor=white" alt="PostgreSQL">
</p>

# Linketinder API RESTful (Java Servlets & Apache Tomcat)

Esta branch contém a implementação do requisito **K2-T4 (ZG-HERO Project)**.
O sistema monolítico foi refatorado para operar como uma API RESTful, adotando a arquitetura Cliente-Servidor sem a utilização de frameworks (como Spring ou Micronaut).

## Tecnologias e Recursos Utilizados

- **Linguagem:** Groovy 4 (Rodando na JVM)
- **Servidor Web:** Apache Tomcat 9
- **Especificação:** Jakarta EE / Java Servlets (`javax.servlet-api:4.0.1`)
- **Automação:** Gradle com plugin **Gretty** (para rodar o Tomcat embarcado)
- **Banco de Dados:** PostgreSQL (JDBC) com "Bypass de ClassLoader"
- **Padrões:** MVC, Singleton, Factory e DIP.

## Estratégia de Arquitetura (Servlets Nativo)

A estratégia consistiu em substituir o `HttpServer` nativo da JDK pela especificação de mercado **Servlets**.
Para cada domínio (Candidato, Empresa, Vaga), foi criada uma classe estendendo `HttpServlet`, mapeada diretamente através de anotações.

### O Contrato da API (Endpoints RESTful mapeados via `@WebServlet`)

| Verbo | Endpoint | Payload Esperado (JSON) | Status de Sucesso |
| :--- | :--- | :--- | :--- |
| `POST` | `/api/candidatos` | Dados básicos + Idade/Data | `201 Created` |
| `POST` | `/api/empresas` | Dados básicos + CNPJ | `201 Created` |
| `POST` | `/api/vagas` | `idEmpresa`, `titulo`, `local` + Competências | `201 Created` |

*Nota: O tratamento de `CORS` (Cross-Origin Resource Sharing) foi implementado manualmente sobrescrevendo o método `doOptions()` em cada Servlet, permitindo que o Frontend consuma a API via navegador.*

## O Maior Desafio Técnico: ClassLoader Isolation do Tomcat

Ao migrar do console para o Tomcat, ocorreu o erro `No suitable driver found` ao conectar ao PostgreSQL, mesmo a dependência constando no Gradle.
Isso ocorre porque o gerenciador interno do Java (`DriverManager`) não enxergava os drivers carregados em ambientes Web isolados (WebappClassLoader).

**A Solução Definitiva:**
Aplicamos um *bypass* na Fábrica de Conexões (`PostgresConnection`), instanciando o Driver do Postgres fisicamente e conectando diretamente via `Properties`, ignorando a gerência do Java.

```groovy
Driver driver = new Driver()
Properties props = new Properties()
props.setProperty("user", DbConfig.USER)
props.setProperty("password", DbConfig.PASSWORD)
Connection conn = driver.connect(DbConfig.URL, props)
```

***Autor: Michel Lavanere Sampaio***