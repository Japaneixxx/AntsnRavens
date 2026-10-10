# AGENTS.md — Instruções e Contexto para IAs Assistentes

Este documento estabelece o contexto arquitetural, as convenções de código e as mecânicas principais do projeto **Ants 'n Ravens (AnR)** para orientar IAs de desenvolvimento (Copilot, Cursor, Gemini, Claude, etc.).

---

## 1. Visão Geral do Mod
* **Nome Oficial:** Ants 'n Ravens
* **Sigla / Acrônimo:** AnR
* **Mod ID (NeoForge):** `antsnravens`
* **Versão do Minecraft:** 26.3 (linha de desenvolvimento compatível com NeoForge 26.3.x)
* **Versão do NeoForge:** `26.3.0.64-beta`
* **Versão do Java:** Java 25 (LTS), usada pelo Gradle e pelo toolchain do projeto
* **Documento de Design de Referência:** `ANTS_N_RAVENS_GDD.md` (na raiz do repositório)

---

## 2. Arquitetura Multi-Módulo Gradle
O projeto é dividido rigorosamente em três submódulos:

1. **`anr-api`**:
    * Contém contratos públicos e estruturas de roteamento independentes da lógica de blocos do core.
    * Exemplos atuais: `NetworkNode` e `RoutingGraph`.
    * Não deve conter lógica de blocos, GUIs, entidades ou registros do mod.
2. **`anr-core`**:
    * Contém a implementação principal (`AntsNRavensCore`), registros, blocos, BlockEntities, itens, rede local e data generation.
    * Registros atuais: `ModBlocks`, `ModItems`, `ModBlockEntities`, `ModDataComponents` e a creative tab do mod.
    * Conteúdo implementado: `QueenChamberBlock`, `SubColonyNodeBlock`, `RavenTranslatorBlock`, as suas BlockEntities, `CompanionCrowEntity`, `RavenTerminalItem`, `PheromoneBrushItem`, `TrainingWhistleItem` e a GUI modular do terminal.
3. **`anr-compat`**:
    * Módulo opcional de integração com mods de terceiros (*AE2*, *Create* e *Mekanism*).
    * Não deve declarar uma segunda classe `@Mod`, registros próprios do namespace `antsnravens` ou metadata `neoforge.mods.toml`.

---

## 3. Principais Mecânicas e Regras do Código

1. **Arquitetura Hub & Spoke (Câmara da Rainha):**
    * A **Câmara da Rainha** (`QueenChamberBlockEntity`) atua como o **Master Controller** central.
    * A lista de nódulos conectados é persistida com `ValueInput`/`ValueOutput` e `BlockPos.CODEC`.
    * Cada **Sub-Colony Node** (`SubColonyNodeBlockEntity`) persiste a posição da sua câmara rainha mãe.
    * A Câmara executa um ticker server-side periódico para remover nódulos que deixaram de existir.
2. **Terminal Único & GUI Modular do Corvo:**
    * O `RavenTerminalItem` realiza a leitura inicial da rede e informa status, rainhas, nódulos e raio de varredura.
    * A GUI deve ser modular: ao registrar novas sub-colônias na rede, novas abas dinâmicas de controle surgem na tela principal do terminal.
    * O `RavenTranslatorBlockEntity` persiste a posição da Câmara da Rainha associada.
    * O `RavenTranslatorBlock` abre `RavenTerminalMenu` apenas no servidor e apenas quando a posição associada resolve para uma `QueenChamberBlockEntity` válida.
    * `RavenTerminalMenu` usa `IMenuTypeExtension`/`IContainerFactory` para transportar a posição da Rainha no `RegistryFriendlyByteBuf`; o `RavenTerminalScreen` renderiza slots de visualização e abas para os nódulos ligados.
3. **Pareamento por Feromônio:**
    * O `PheromoneBrushItem` seleciona primeiro um `SubColonyNodeBlock`.
    * A posição selecionada é armazenada no item pelo Data Component `antsnravens:selected_node_pos`.
    * Ao clicar em uma `QueenChamberBlock`, o pincel registra o nódulo na rainha e grava a posição da rainha no nódulo.
    * Pareamento, mensagens, som e partículas devem ser executados server-side.
    * `QueenChamberBlockEntity` emite `pheromone_trail` no cliente ao longo das ligações válidas; a emissão é limitada por intervalo, distância e chunks carregados.
4. **Corvo Companheiro e Fluxo Aéreo:**
    * `CompanionCrowEntity` é um papagaio domesticado especializado, registrado em `ModEntities`, com navegação aérea e voo Bézier server-side até ao `RavenTranslatorBlock`.
    * O Puleiro Tradutor procura um corvo do jogador num raio de 15 blocos; quando encontrado, o corvo faz a aproximação, reproduz som e abre o `RavenTerminalMenu`.
    * Ao fechar o menu aberto pelo corvo, `RavenTerminalMenu.removed` inicia o retorno aéreo ao dono.
    * `TrainingWhistleItem` invoca um corvo domesticado com Shift + clique direito para testes.
4. **Infiltração em Blocos (In-Block Routing):**
    * O transporte de curta distância ocorre por dentro da geometria sólida de blocos existentes através de um sistema de grafos de micro-frestas, sem necessidade de cabos aparentes.
5. **Wi-Fi Biológico Aéreo:**
    * Conexões de longa distância entre postos e a Rainha usam unidades de corvos e puleiros sem fios.

---

## 4. Convenções de Código e Registros (NeoForge 26.3)

* **Registros:** Utilizar `DeferredRegister.Items`, `DeferredRegister.Blocks` e `DeferredRegister` para `BLOCK_ENTITY_TYPE`/`DATA_COMPONENT_TYPE`.
* **Registro de BlockEntities:** manter os IDs `queen_chamber_be` e `sub_colony_node_be` em `ModBlockEntities.BLOCK_ENTITIES`.
* **Menus:** registrar `MenuType` em `ModMenuTypes` e transportar dados adicionais de abertura com `IPlayerExtension.openMenu`.
* **Separação de estoques:** `QueenChamberBlockEntity` mantém o inventário central persistente de 27 slots; cada `SubColonyNodeBlockEntity` expõe exclusivamente os inventários físicos vinculados em um raio esférico de até 5 blocos, sem buffer interno próprio.
* **Sincronização de terminal:** manter a topologia máxima fixa no menu cliente/servidor, marcar slots excedentes como inativos e sincronizar contagens com `ContainerData`; conteúdos de containers persistentes são sincronizados pelos próprios slots do `AbstractContainerMenu`.
* **Slots virtuais do terminal:** o preenchimento usado para manter a topologia fixa nunca pode aceitar itens; somente slots do buffer do Nódulo e dos inventários físicos vinculados podem receber ou remover stacks.
* **Lista de inventários:** a GUI usa uma janela de quatro linhas sobre uma lista única de até 441 slots da Sub-Colônia; a rolagem é feita por roda do mouse ou scrollbar, sincronizada pelo botão do menu, sem truncar os baús vinculados.
* **Layout do terminal:** a grade da Sub-Colônia começa abaixo do título, o scrollbar fica em uma coluna externa à direita e cada slot recebe um fundo visual individual antes da renderização dos itens.
* **Espaçamento vertical:** a grade rolável termina antes do indicador de capacidade e do inventário do jogador, com a interface compactada para impedir sobreposição da última linha.
* **Rolagem e baús duplos:** a roda do mouse rola em toda a área da lista da Sub-Colônia; ao vincular uma metade de um `ChestBlock`, a outra metade adjacente também é vinculada automaticamente quando válida.
* **Namespaces:** Usar a constante `AntsNRavensCore.MOD_ID` (`"antsnravens"`).
* **Data Components:** usar `DataComponentType` persistente para estado de itens; não introduzir NBT manual para novos dados de item.
* **Vinculação de inventários:** o `PheromoneBrushItem` seleciona um Nódulo e depois liga um inventário vanilla a até 5 blocos dele; as posições vinculadas são persistidas no Nódulo.
* **BlockEntity persistence:** na API 26.3, usar `ValueInput`/`ValueOutput` e codecs (`BlockPos.CODEC`) nos hooks `loadAdditional`/`saveAdditional`.
* **Inventários:** a linha NeoForge 26.3 não possui mais `IItemHandler`/`ItemStackHandler`; usar `Capabilities.Item.BLOCK`, `ResourceHandler<ItemResource>` e `VanillaContainerWrapper` para compatibilidade com containers vanilla e modded.
* **IDs explícitos:** toda `BlockBehaviour.Properties` e `Item.Properties` registrada deve usar `setId` com `ResourceKey`, exigido pelo runtime 26.3.
* **Data generation:** providers devem ser registrados no mod event bus e os modelos gerados devem ir para `src/generated/resources`.
* **Lado lógico:** lógica de mundo, vinculação, voo, invocação, partículas de mundo e alteração de estado deve validar `!level.isClientSide()`; a renderização e os providers de partículas são client-only.
* **Trilhas de conexão:** conexões de Nódulos com inventários vinculados emitem `ant_trail` no cliente, com partículas pretas pequenas e altura fixa próxima ao chão.
* **Formatos de arquivos:** manter Java limpo, desacoplado e documentar interfaces públicas da `anr-api` com Javadoc.

---

## 5. Instruções para IAs ao Gerar Código
1. Sempre consulte o arquivo `ANTS_N_RAVENS_GDD.md` para validar regras de design e balanceamento.
2. Não coloque lógica de blocos ou GUIs dentro da `anr-api`; a API deve conter apenas contratos e grafos puros.
3. Garanta que novas classes sigam o pacote base `com.japaneixxx.antsnravens`.
4. Antes de criar um registro, procure os registros existentes e reutilize `AntsNRavensCore.MOD_ID`.
5. Ao alterar uma BlockEntity, valide persistência, registro, ticker e associação no bloco.
6. Ao alterar itens com estado, use `ModDataComponents` e teste o fluxo no servidor.
7. Valide mudanças do core com `gradle :anr-core:compileJava`; para recursos, execute `gradle :anr-core:runData`.