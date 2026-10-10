# Ants 'n Ravens (AnR) — Game Design Document (GDD)

## 1. Visão Geral do Projeto
* **Nome do Mod:** Ants 'n Ravens
* **Sigla / Mod ID:** AnR (`antsnravens` / `anr`)
* **Plataforma / Versão:** Minecraft Java Edition 26.3+
* **Loader:** NeoForge (Versão 26.3.x, compatível com Java 25+)
* **Gênero:** Mod Técnico, Biotecnologia, Automação, Logística Espacial, Armazenamento Centralizado, Defesa e Companheiros
* **Público-Alvo:** Jogadores de Mods Técnicos (Mekanism, Create, AE2, Refined Storage, Tom's Simple Storage) que buscam logística estética, automação sem cabos ríspidos, sistema de armazenamento nativo autônomo com GUI modular, processamento industrial de minérios, defesa de base e ecossistema dinâmico.

---

## 2. Pilares de Engenharia & Diferenciais Competitivos

1. **Identidade Forte & Marcante (AnR):** Marca amigável e memorável combinando a micro-logística terrestre das formigas com o transporte aéreo e interface dos corvos.
2. **Terminal Único & GUI Modular do Corvo (Raven Terminal):** Interagir com o Corvo (ou Puleiro Tradutor) abre um terminal centralizador. A GUI é modular: ao adicionar sub-colônias (processamento, defesa, mineração), novas abas de controle específico surgem dinamicamente na tela.
3. **Arquitetura Core & Sub-Colônias (Hub & Spoke):** A Câmara da Rainha atua como o Core Central que gerencia, envia ordens e distribui tarefas/recursos para pequenas sub-colônias operacionais espalhadas pelo mapa.
4. **Multifuncionalidade Além do Armazenamento:** As sub-colônias operam refinarias de lixiviação de minérios, geradores de energia bio-térmica, unidades de defesa de território e mineradoras ecológicas.
5. **Armazenamento Centralizado Nativo (Corvo-Tradutor & Rainha):** Sistema de inventário completo nativo (estilo *Refined Storage* / *Tom's Simple Storage*), funcionando de forma 100% independente.
6. **Infiltração em Blocos (In-Block Routing):** As rotas de transporte local passam por *dentro* da geometria sólida dos blocos existentes através de micro-frestas, eliminando cabos visíveis.
7. **Wi-Fi Biológico Aéreo (Air-Link Network):** Conexão de longa distância sem cabos entre a Rainha e sub-colônias remotas usando bandos de corvos treinados.
8. **Mascotes Companheiros com Utilidade (Companion Pets):** Corvos de estimação de ombro com inventário portátil, terminal sem fio móvel, auto-loot e vigilância de minérios.
9. **Zero Lag / Renderização Leve (Graph-Based Routing):** Tráfego processado em grafo matemático no servidor e renderizado via partículas/instanced rendering na GPU do cliente.

---

## 3. A Interface Modular (Raven Terminal & GUIs)

* **Conversação com o Corvo:** Interagir com o Corvo Mascote de Ombro ou Puleiro Tradutor abre a interface centralizada.
* **Aba Principal (Inventário Central):** Tela de busca e retirada de itens/fluidos armazenados em toda a rede.
* **Abas Dinâmicas de Sub-Colônias:**
  * **[⚙️ Processamento]:** Ajuste de taxas de lixiviação, seleção de enzimas e controle de subprodutos de minérios.
  * **[⚔️ Defesa]:** Gestão de patrulhas de Formigas-Soldado, alarmes aéreos de corvos e perímetro de proteção.
  * **[⛏️ Escavação]:** Marcação de lotes de mineração e taxas de extração.
  * **[🧬 Genética]:** Leitura de operons, sequenciamento de DNA e mutações da Estação Biológica.

---

## 4. Arquitetura de Comando (Câmara da Rainha & Sub-Colônias)

* **Câmara da Rainha (Core Central):** Gerencia o inventário global, aloca populações de formigas e responde aos comandos transmitidos pelo Corvo Tradutor.
* **Nódulos de Sub-Colônia (Outposts Locais):** Posicionados em áreas operacionais para executar tarefas especializadas (lixiviação, mineração, defesa).

---

## 5. Arquitetura do Repositório (Multi-Module Gradle)

```text
com.antsnravens/
├── api/                   # Interfaces públicas, grafos e contratos (anr-api)
├── core/                  # Módulos principais, blocos, entidades e GUIs (anr-core)
│   ├── block/             # QueenChamberBlock (Core), SubColonyNodeBlock, RavenTranslatorBlock
│   ├── entity/            # CompanionCrowEntity, SoldierAntEntity
│   ├── item/              # PheromoneBrushItem, TrainingWhistleItem, DnaSyringeItem
│   ├── storage/           # FormicStorageNetwork, ItemGridHandler, FluidGridHandler
│   ├── processing/        # BioLeachingManager, BioEnergyGenerator
│   └── client/gui/        # RavenTerminalScreen, DynamicSubColonyTab, ItemGridWidget
└── compat/                # Módulos de integração (anr-compat)
    ├── ae2/               # MEFormicStorageGridLink
    ├── create/            # KineticBioReactorEngine
    └── mekanism/          # BioLeachingGasConverter