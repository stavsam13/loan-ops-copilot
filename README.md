# Loan Operations Copilot

An AI assistant for loan officers at a (fictional) bank. It answers questions about customers, loans, payments, agreements and internal policy, with **figures from the bank's own systems and citations to the exact document clause**.

> **Status: work in progress (Phase 1).** See the roadmap below. All data in this repo is synthetic.

## Example questions

| Question | Answered from |
| --- | --- |
| What is the outstanding balance of customer 10234? | Loan API (computed in Java) |
| Summarise the customer's payment history over the last 12 months. | Payments API |
| What are the early repayment conditions for this agreement? | Agreement documents (RAG) |
| Why is this loan classified as overdue? | Loan position API + credit policy + agreement |
| Which customers are at risk of missing their next payment? | Rule-based risk score, explained by the agent |

## Design principles

1. **The LLM never does the maths.** Balances, arrears and days past due come from deterministic Java services; the agent selects tools and explains results.
2. **Every claim has evidence.** Figures cite the API that produced them; contract statements cite document and clause.
3. **The AI explains, it doesn't decide.** Risk flags and delinquency buckets are rule-based.
4. **Access control lives in the services, not the prompt.**
5. **Sensitive actions need a human approval.**
6. **Everything is auditable.**
7. **Quality is measured** with an eval suite in CI.

## Architecture

```
React UI ──► Python agent (FastAPI + LangGraph) ──► LLM API
                 │                    │
                 ▼                    ▼
           MCP server            Retrieval
                 │                    │
                 ▼                    ▼
      Java 25 loan-service ──► PostgreSQL + pgvector ◄── Ingestion pipeline
```

| Layer | Technology |
| --- | --- |
| Domain services | Java 25, Spring Boot 4.1, JdbcClient, Flyway |
| Tool layer | Python MCP SDK 2.x (streamable HTTP) |
| Agent | LangGraph, FastAPI |
| Retrieval | PostgreSQL + pgvector |
| UI | React (Vite) |
| Evals | Python, GitHub Actions |
| Hosting | AWS (EC2 + docker-compose to start; ECS Fargate + RDS design as an ADR) |

## Repository layout

```
services/loan-service/   Java 25 / Spring Boot domain APIs
agent/                   LangGraph agent, MCP server, FastAPI
ingestion/               PDF parsing, chunking, pgvector loading
evals/                   golden set, runners, results
ui/                      React
data-generator/          synthetic customers, loans, payments, PDFs
infra/                   Terraform for AWS
docs/                    discovery note, ADRs, diagrams
```

## Roadmap

- [ ] **Phase 0:** Foundations: discovery note, data model, first ADRs
- [ ] **Phase 1:** Walking skeleton deployed live on AWS (balance question end to end)
- [ ] **Phase 2:** Documents and RAG with clause-level citations
- [ ] **Phase 3:** Hybrid reasoning: "why is this loan overdue?"
- [ ] **Phase 4:** Evaluation suite in CI
- [ ] **Phase 5:** RBAC, audit trail, tracing
- [ ] **Phase 6:** Human-in-the-loop approvals
- [ ] **Phase 7:** Rule-based portfolio risk
- [ ] **Phase 8:** Demo video and case-study write-up

## License

MIT. See [LICENSE](LICENSE).
