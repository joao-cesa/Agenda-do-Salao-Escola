# Agendamentos (persistência)

Rotas implementadas na issue #8. Os valores de `idServico`/`idProfissional` vêm de `GET /servicos` e
`GET /profissionais` (carregados por `banco/dados-iniciais.sql`). Datas no formato ISO local, sem fuso
(`2026-12-01T14:00:00`).

| Rota | O que faz | Sucesso | Erros |
|---|---|---|---|
| `POST /agendamentos` | Cria; calcula `dataHoraFim` = início + duração do serviço | `201` + `Location` | `400` (dados inválidos, serviço/profissional inexistente) |
| `GET /agendamentos` | Lista todos (ordem de início) | `200` | |
| `GET /agendamentos/{id}` | Consulta um | `200` | `404` |
| `PATCH /agendamentos/{id}/cancelar` | Muda `status` para `CANCELADO` (o registro **não** é apagado) | `200` | `404`; `409` se já estava cancelado |
| `GET /servicos` · `GET /profissionais` | Listam os dados iniciais | `200` | |

## Exemplo

```json
POST /agendamentos
{ "cliente": "Cliente Teste", "idServico": 1, "idProfissional": 1, "dataHoraInicio": "2026-12-01T14:00:00" }
```

```json
201 Created
{
  "id": 1, "cliente": "Cliente Teste",
  "idServico": 1, "servico": "Corte", "idProfissional": 1, "profissional": "Ana",
  "dataHoraInicio": "2026-12-01T14:00:00", "dataHoraFim": "2026-12-01T14:30:00",
  "status": "ATIVO"
}
```

## Formato de erro (sem detalhes internos)

```json
{ "status": 400, "erro": "Bad Request", "mensagem": "Dados inválidos.", "detalhes": ["cliente: O nome do cliente é obrigatório."] }
```

Falhas de banco retornam `503` (indisponível) ou `500` (erro de persistência) com mensagem genérica; o
detalhe técnico fica apenas no log do servidor.

## Fora do escopo desta issue (próximos passos)

- #12: validar dia (segunda a sábado) e expediente 08:00-20:00 → `400`.
- #13: impedir sobreposição para a mesma profissional → `409`.
- #14: agenda diária por profissional.

O ponto exato para encaixar #12 e #13 está comentado em `AgendamentoService.criar`.
