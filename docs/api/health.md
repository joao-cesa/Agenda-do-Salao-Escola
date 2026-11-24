# GET /health

Verifica a aplicação e a conexão real com o banco (executa `SELECT 1` a cada chamada).

| Situação | HTTP | Corpo |
|---|---|---|
| Banco respondendo | `200` | `{"status":"UP","banco":"UP","iniciado_em":"2026-11-24T12:00:00.123Z"}` |
| Banco indisponível | `503` | `{"status":"DOWN","banco":"DOWN","iniciado_em":"...","mensagem":"Banco de dados indisponível no momento."}` |

- `iniciado_em` (UTC) é o instante em que **esta instância** da aplicação subiu. Ele muda a cada
  reinício, e é com isso que se prova a persistência (issue #10): registre o valor, reinicie, confira
  que mudou e que os agendamentos continuam listados.
- A resposta nunca traz `DATABASE_URL`, usuário, senha nem a mensagem interna do driver; detalhes
  técnicos vão só para o log do servidor (apenas o tipo da exceção).
- O tempo para responder `503` com o banco fora é limitado a ~5 s (`spring.datasource.hikari.connection-timeout`).

## Como testar à mão

```powershell
curl.exe -i http://localhost:8080/health
```

Para ver o `503`, a aplicação precisa estar **já no ar** com um banco de servidor (p.ex. PostgreSQL):
pare o banco e repita a chamada. Com o banco fora desde o início, a aplicação nem sobe (o schema não
consegue ser aplicado). Com o H2 embarcado não há como "derrubar" o banco sem derrubar a aplicação; esse
caso é coberto por `HealthServiceTest` e `HealthControllerTest`.
