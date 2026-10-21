# Jornada IF — versão modificada

Projeto Java com LibGDX, organizado nos módulos `core`, `lwjgl3` e `android`.

## O que foi alterado

- Adicionada uma splash screen provisória de 5 segundos.
- Criada a classe `Transicao`, com fade para preto e entrada, usada por `Main.mudarTela(...)`.
- Criado o efeito global de partículas vermelhas e verdes ao clicar/tocar.
- Reorganizadas as telas Inicial, Menu, Configurações e Sobre para usar `Stage` e `InputMultiplexer` em conjunto com o efeito de toque.
- O menu agora contém uma grade de 15 níveis (5 colunas x 3 linhas).
- Cada nível abre uma cena provisória própria, com piso em pixels gerado por código e movimentação do personagem pelas setas.
- Ajustada a velocidade e os limites de movimento do personagem.
- Movidos os arquivos Java para uma pasta que corresponde ao pacote `io.github.some_example_name`.

## Recursos provisórios

- `TelaSplash.java`: substitua o símbolo pixelado pelo logo que preferir.
- `TelaNivel.java`: o piso é temporário e pode ser substituído por tilesets/mapas reais.
- `TelaConfiguracoes.java`: os controles de som e música alteram o estado exibido na interface, mas ainda precisam ser conectados a um sistema de áudio quando ele for adicionado.

## Abrir no Android Studio

Abra a pasta raiz `Jornada-IF` no Android Studio e aguarde a sincronização do Gradle. O ZIP de origem não continha `gradle/wrapper/gradle-wrapper.jar` nem `gradle-wrapper.properties`, então não foi possível validar uma compilação por linha de comando neste ambiente. Se o Android Studio informar que falta o Gradle Wrapper, será necessário regenerá-lo a partir de uma instalação Gradle compatível com a versão do projeto.

Os recursos gráficos originais foram mantidos. O piso e alguns botões são placeholders feitos com pixels/cores para facilitar a substituição posterior.
