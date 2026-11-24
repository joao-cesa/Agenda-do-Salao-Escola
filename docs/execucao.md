# Execução e configuração

Guia para preparar o ambiente, configurar a conexão com o banco **sem expor credenciais** e rodar a aplicação.

## Pré-requisitos

- JDK 17 ou superior (`java -version`).
- Maven 3.9+ (`mvn -v`).

## 1. Configuração segura (`DATABASE_URL`)

A aplicação lê a conexão do banco **somente por configuração do processo**. Nada de usuário, senha ou
string real fica no código ou no Git.

| Variável | Para quê | Padrão (se ausente) |
|---|---|---|
| `DATABASE_URL` | String de conexão JDBC | `jdbc:h2:file:./dados/agenda;DB_CLOSE_DELAY=-1` (H2 local em arquivo) |
| `DATABASE_USER` | Usuário do banco | `sa` |
| `DATABASE_PASSWORD` | Senha do banco | vazia |
| `H2_CONSOLE_ENABLED` | Liga `/h2-console` (só desenvolvimento) | `false` |
| `PORT` | Porta HTTP | `8080` |

### Local

1. Copie o modelo: `Copy-Item .env.example .env` (PowerShell) ou `cp .env.example .env`.
2. Ajuste os valores se necessário. O arquivo `.env` está no `.gitignore` e **nunca** deve ser commitado.
3. Ao subir, a aplicação importa o `.env` (`spring.config.import`). Variáveis de ambiente reais do sistema
   têm precedência sobre o `.env`.

Se preferir, defina as variáveis direto no terminal em vez de usar o `.env`:

```powershell
$env:DATABASE_URL = "jdbc:h2:file:./dados/agenda;DB_CLOSE_DELAY=-1"
```

### Azure App Service

Em **Configuration → Application settings**, cadastre `DATABASE_URL` (e `DATABASE_USER`/`DATABASE_PASSWORD`
se o banco exigir). O valor fica oculto no portal e **não** deve aparecer em prints, README ou commits.

## 2. Banco de dados (schema e dados iniciais)

Os scripts `banco/schema.sql` e `banco/dados-iniciais.sql` são executados **automaticamente** a cada
inicialização (`spring.sql.init.mode=always`). Ambos são idempotentes, então reiniciar não duplica dados.
O Hibernate **não** cria tabelas (`ddl-auto=none`): o schema pertence ao `banco/schema.sql`.

Para outro banco (PostgreSQL / Azure SQL), troque a dependência `h2` do `pom.xml` pelo driver do banco e
revise as notas de portabilidade no cabeçalho do `schema.sql`.

## 3. Rodar a aplicação

```powershell
mvn spring-boot:run
```

Console do H2 (com `H2_CONSOLE_ENABLED=true`): `http://localhost:8080/h2-console`, usando a mesma
`DATABASE_URL` do `.env`.

## 4. Testes

```powershell
mvn test
```

Os testes usam um H2 em memória próprio, então não tocam no banco local nem exigem `.env`.
