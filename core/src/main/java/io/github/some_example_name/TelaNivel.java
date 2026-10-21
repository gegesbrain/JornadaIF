package io.github.some_example_name;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import java.util.ArrayList;
import java.util.List;

/** Jornada IF: quinze desafios de programacao de um robo, sem Tiled ou fisica. */
public class TelaNivel implements Screen {
    // PERSONALIZACAO: altere estes valores para ajustar o tamanho da interface.
    private static final float ESPACO = 8f;
    private static final float INTERVALO_MOVIMENTO = 0.32f;
    private static final int MAX_COMANDOS = 24;
    private static final int COLUNAS = 6, LINHAS = 4;
    private static final int TOTAL_NIVEIS = 15;

    // 0 = piso; 1 = obstaculo. A primeira linha representa o TOPO do mapa.
    // Os mapas sao propositalmente pequenos para facilitar o uso no celular.
    private static final String[][] MAPAS = {
        {"......", "......", "......", "......"},
        {"......", ".#####", "......", "......"},
        {"......", ".###..", "..###.", "......"},
        {"......", "..##..", "..###.", "..####"},
        {".#....", ".##.#.", "....#.", ".##..."},
        {".#....", ".##...", "....##", "....#."},

        {"..#...", "..#...", ".A#B..", "..#..."},
        {"..#...", ".A#B..", "..#...", "..#..."},
        {"...X..", "...#..", "..S#..", "...#.."},
        {"...X..", ".S.#..", "...#..", "...#.."},
        {"..#...", ".A#B..", "..#...", "..#..."},
        {"...X..", "..S#..", "...#..", "...#.."},
        {"...X..", ".AS#B.", "...#..", "...#.."},
        {"..###.", ".A##B.", "..##..", "..##.."},
        {"...XX.", ".AS#B.", "..##..", "..##.."}
    };
    // Coordenadas em celulas: x cresce para a direita; y cresce para BAIXO.
    private static final int[][] INICIOS = {
        {0,3},{0,3},{0,3},{0,3},{0,3},{0,3},
        {0,3},{0,3},{0,3},{0,3},{0,3},{0,3},{0,3},{0,3},{0,3}
    };
    private static final int[][] DESTINOS = {
        {5,3},{5,0},{5,0},{5,0},{5,0},{5,0},
        {5,3},{5,0},{5,0},{5,3},{5,0},{5,0},{5,0},{5,0},{5,0}
    };
    private static final String[] CONCEITOS = {
        "SEQUÊNCIA", "DESVIO DE OBSTÁCULOS", "PLANEJAMENTO",
        "REPETIÇÃO", "REPETIÇÃO E DESVIO", "DESAFIO FINAL",
        "PRIMEIRO TELETRANSPORTE", "DOIS PORTAIS", "ABRA A PORTA",
        "ENCONTRE O INTERRUPTOR", "ATALHO ESPACIAL", "PORTA E DESVIO",
        "PORTAL E INTERRUPTOR", "CAMINHO ALTERNATIVO", "DESAFIO DOS PORTAIS"
    };

    private final Main jogo;
    private final int numeroNivel;
    private final Stage stage;
    private final SpriteBatch batch;
    private final BitmapFont fonte;
    private final Texture pixel;
    private final Texture texturaBotao;
    private final Texture spritePiso, spriteParede, spriteRobo, spriteDestino;

    private final Texture spritePortalA, spritePortalB, spriteInterruptor;
    private final Texture spriteInterruptorAtivo, spritePortaFechada, spritePortaAberta;
    private final ParticulasFundo particulasFundo;
    private final List<String> comandos = new ArrayList<>();
    private final List<String> passos = new ArrayList<>();

    private Label titulo, descricao, fila, aviso;
    private Stack esquerda, direita, cima, baixo, repetir, executar, limpar, reiniciar, voltar;
    private float largura, altura, celula, origemX, origemY, escala;
    private int roboX, roboY, passoAtual;
    private float relogio;
    private boolean executando, venceu, portaAberta;

    public TelaNivel(Main jogo, int numeroNivel) {
        this.jogo = jogo;
        this.numeroNivel = MathUtils.clamp(numeroNivel, 1, TOTAL_NIVEIS);
        stage = new Stage(new ScreenViewport());
        batch = new SpriteBatch();
        fonte = FonteUtil.carregar("pressStart2P.ttf", 32);
        fonte.getData().setScale(.55f);
        Pixmap p = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        p.setColor(Color.WHITE); p.fill();
        pixel = new Texture(p); p.dispose();
        // Imagem de botao ja utilizada pelo projeto: troque o PNG quando quiser.
        texturaBotao = carregarSprite("s_botao_menu.png");
        spritePiso = carregarSprite("chaof.png");
        spriteParede = carregarSprite("s_parede.png");
        spriteRobo = carregarSprite("jogadorf.png");
        spriteDestino = carregarSprite("finalf.png");
        spritePortalA = carregarSprite("s_portal.png");
        spritePortalB = carregarSprite("s_portal.png");
        spriteInterruptor = carregarSprite("s_interruptor_off.png");
        spriteInterruptorAtivo = carregarSprite("s_interruptor_on.png");
        spritePortaFechada = carregarSprite("s_porta_fechada.png");
        spritePortaAberta = carregarSprite("s_porta_abert.png");
        particulasFundo = new ParticulasFundo();
        criarInterface();
        reiniciarTudo();
        resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    }

    // SPRITES: arquivos PNG opcionais. Nao e necessario criar todos agora.
    private Texture carregarSprite(String arquivo) {
        return Gdx.files.internal(arquivo).exists()
            ? new Texture(Gdx.files.internal(arquivo)) : null;
    }

    // TODOS os controles usam a MESMA animacao fofinha do restante do jogo.
    // Para mudar a imagem, substitua s_botao_menu.png em assets/.
    private Stack criarBotao(String texto, final Runnable acao) {
        Stack botao = new Stack();
        Image imagem = new Image(texturaBotao != null ? texturaBotao : pixel);
        imagem.setColor(.18f, .31f, .49f, .96f);
        botao.add(imagem);
        Label legenda = new Label(texto, new Label.LabelStyle(fonte, Color.WHITE));
        legenda.setAlignment(Align.center);
        legenda.setFillParent(true);
        botao.add(legenda);
        EfeitoPulinho.aplicar(botao, acao, () -> jogo.tocarSomClique());
        stage.addActor(botao);
        return botao;
    }

    private void criarInterface() {
        titulo = new Label("", new Label.LabelStyle(fonte, Color.WHITE));
        descricao = new Label("", new Label.LabelStyle(fonte, Color.CYAN));
        fila = new Label("", new Label.LabelStyle(fonte, Color.WHITE));
        aviso = new Label("", new Label.LabelStyle(fonte, Color.YELLOW));
        for (Label l : new Label[]{titulo, descricao, fila, aviso}) {
            l.setAlignment(Align.center);
            stage.addActor(l);
        }
        esquerda = criarBotao("Esquerda", () -> adicionar("E"));
        direita = criarBotao("Direita", () -> adicionar("D"));
        cima = criarBotao("Cima", () -> adicionar("C"));
        baixo = criarBotao("Baixo", () -> adicionar("B"));
        repetir = criarBotao("Repetir", () -> adicionar("2X"));
        executar = criarBotao("EXECUTAR", this::executar);
        limpar = criarBotao("LIMPAR", this::reiniciarTudo);
        reiniciar = criarBotao("REINICIAR", this::reiniciarTudo);
        voltar = criarBotao("MENU", () -> jogo.mudarTela(new TelaMenu(jogo)));
        titulo.setText("NIVEL " + String.format("%02d", numeroNivel));
        descricao.setText(CONCEITOS[numeroNivel - 1]);
    }

    private void adicionar(String comando) {
        if (executando || venceu || comandos.size() >= MAX_COMANDOS) return;
        if ("2X".equals(comando) && (comandos.isEmpty() || "2X".equals(comandos.get(comandos.size()-1)))) {
            aviso.setText("2X REPETE O ULTIMO MOVIMENTO");
            return;
        }
        comandos.add(comando);
        aviso.setText("");
        atualizarFila();
    }

    private void atualizarFila() {
        StringBuilder texto = new StringBuilder("COMANDOS: ");
        for (String c : comandos) {
            if (texto.length() > 65) { texto.append(" ..."); break; }
            texto.append(c).append(" ");
        }
        fila.setText(texto.toString());
    }

    private void reiniciarRobo() {
        roboX = INICIOS[numeroNivel-1][0];
        roboY = INICIOS[numeroNivel-1][1];
        executando = false;
        venceu = false;
        passoAtual = 0;
        relogio = 0;
        passos.clear();
        portaAberta = false; // Interruptores e portas voltam ao estado inicial.
    }

    private void reiniciarTudo() {
        reiniciarRobo();
        comandos.clear();
        aviso.setText("LEVE O ESTUDANTE ATÉ O IF");
        atualizarFila();
    }

    private void executar() {
        if (executando || comandos.isEmpty()) return;
        reiniciarRobo();
        String anterior = null;
        for (String c : comandos) {
            if ("2X".equals(c)) {
                if (anterior != null) passos.add(anterior); // executa MAIS UMA vez o ultimo passo
            } else {
                passos.add(c);
                anterior = c;
            }
        }
        executando = true;
        aviso.setText("EXECUTANDO...");
    }

    private void proximoPasso() {
        if (passoAtual >= passos.size()) {
            executando = false;
            venceu = roboX == DESTINOS[numeroNivel-1][0] && roboY == DESTINOS[numeroNivel-1][1];
            aviso.setText(venceu ? ":) MISSÃO CONCLUIDA!" : ":( TENTE OUTRA SEQUÊNCIA");
            return;
        }
        String c = passos.get(passoAtual++);
        int x = roboX, y = roboY;
        if ("E".equals(c)) x--;
        if ("D".equals(c)) x++;
        if ("C".equals(c)) y--;
        if ("B".equals(c)) y++;
        // A porta X bloqueia a passagem ate o robo pisar em S.
        char casa = (x >= 0 && x < COLUNAS && y >= 0 && y < LINHAS)
            ? MAPAS[numeroNivel-1][y].charAt(x) : '#';
        if (casa == '#' || (casa == 'X' && !portaAberta)) {
            executando = false;
            aviso.setText(":( CAMINHO BLOQUEADO! REINICIE");
            return;
        }
        roboX = x;
        roboY = y;

        // INTERRUPTOR: basta passar por S uma vez para abrir todas as portas X.
        if (casa == 'S') {
            portaAberta = true;
            aviso.setText("PORTA ABERTA!");
        }

        // TELETRANSPORTE: A leva ate B; B leva ate A.
        // O destino do teleporte nao dispara outro teleporte automaticamente.
        if (casa == 'A' || casa == 'B') {
            char destinoPortal = casa == 'A' ? 'B' : 'A';
            for (int linha = 0; linha < LINHAS; linha++) {
                int coluna = MAPAS[numeroNivel-1][linha].indexOf(destinoPortal);
                if (coluna >= 0) {
                    roboX = coluna;
                    roboY = linha;
                    aviso.setText("TELETRANSPORTE!");
                    break;
                }
            }
        }
    }

    private void retangulo(float x, float y, float w, float h, Color cor) {
        batch.setColor(cor);
        batch.draw(pixel, x, y, w, h);
        batch.setColor(Color.WHITE);
    }

    // Desenha o PNG quando disponivel; senao usa uma cor temporaria.
    // Para usar sprites, basta colocar os arquivos na pasta assets/.
    private void desenharSprite(Texture sprite, float x, float y, float w, float h, Color cor) {
        if (sprite != null) {
            batch.setColor(Color.WHITE);
            batch.draw(sprite, x, y, w, h);
        } else {
            retangulo(x, y, w, h, cor);
        }
    }

    private void desenharTabuleiro() {
        for (int y = 0; y < LINHAS; y++) for (int x = 0; x < COLUNAS; x++) {
            float px = origemX + x * (celula + ESPACO * escala);
            float py = origemY + (LINHAS - 1 - y) * (celula + ESPACO * escala);
            boolean parede = MAPAS[numeroNivel - 1][y].charAt(x) == '#';

            // SPRITE DO CHAO: assets/chaof.png
            desenharSprite(spritePiso, px, py, celula, celula, new Color(.15f,.28f,.40f,1));
            if (parede) {
                // SPRITE DO OBSTACULO: assets/robo_parede.png
                desenharSprite(spriteParede, px, py, celula, celula, Color.DARK_GRAY);
            }
            // SPRITES DE MECANICAS: cada letra representa um elemento especial.
            // Altere os nomes dos arquivos no construtor para trocar as imagens.
            char tipo = MAPAS[numeroNivel - 1][y].charAt(x);
            if (tipo == 'A') {
                desenharSprite(spritePortalA, px, py, celula, celula, Color.PURPLE);
            } else if (tipo == 'B') {
                desenharSprite(spritePortalB, px, py, celula, celula, Color.CYAN);
            } else if (tipo == 'S') {
                desenharSprite(portaAberta ? spriteInterruptorAtivo : spriteInterruptor,
                    px, py, celula, celula, portaAberta ? Color.GREEN : Color.ORANGE);
            } else if (tipo == 'X') {
                desenharSprite(portaAberta ? spritePortaAberta : spritePortaFechada,
                    px, py, celula, celula, portaAberta ? Color.GREEN : Color.RED);
            }

            if (x == DESTINOS[numeroNivel - 1][0] && y == DESTINOS[numeroNivel - 1][1]) {
                // SPRITE DA BANDEIRA: assets/finalf.png
                desenharSprite(spriteDestino, px, py, celula, celula, Color.GOLD);
            }
            if (x == roboX && y == roboY) {
                // SPRITE DO ROBO: assets/jogadorf.png
                // Futuramente voce pode substituir por Animation<TextureRegion>.
                desenharSprite(spriteRobo, px, py, celula, celula, Color.SKY);
            }
        }
    }

    private void posicionar(Label l, float x, float y, float w, float h) {
        l.setBounds(x, y, w, h);
        l.setFontScale(Math.max(.5f, escala));
    }

    private void posicionar(Stack botao, float x, float y, float w, float h) {
        botao.setBounds(x, y, w, h);
        botao.setOrigin(w / 2f, h / 2f); // escala do EfeitoPulinho a partir do centro
        botao.invalidate();
    }

    @Override public void resize(int w, int h) {
        if (w <= 0 || h <= 0) return;
        stage.getViewport().update(w, h, true);
        largura = w; altura = h;
        escala = Math.min(w / 960f, h / 650f);
        // Limites de escala podem ser alterados para ampliar ou reduzir o layout.
        escala = MathUtils.clamp(escala, .55f, 1.6f);
        float gap = ESPACO * escala;
        float espacoMapaW = w - 30f * escala;
        float espacoMapaH = h * .44f;
        celula = Math.min((espacoMapaW - (COLUNAS-1)*gap)/COLUNAS,
            (espacoMapaH - (LINHAS-1)*gap)/LINHAS);
        celula = Math.max(12f, celula);
        float tabW = COLUNAS*celula + (COLUNAS-1)*gap;
        float tabH = LINHAS*celula + (LINHAS-1)*gap;
        origemX = (w - tabW)/2f;
        origemY = h*.35f + Math.max(0, (espacoMapaH-tabH)/2f);
        posicionar(titulo, 0,h-48*escala,w,36*escala);
        posicionar(descricao,0,h-80*escala,w,28*escala);
        posicionar(reiniciar,w-160*escala,h-120*escala,150*escala,30*escala);
        posicionar(fila,10*escala,h*.29f,w-20*escala,38*escala);
        posicionar(aviso,0,h*.225f,w,40*escala);
        float bw = Math.min(115*escala,(w-60*escala)/5f);
        float by = h*.125f;
        Stack[] direcoes = {esquerda, cima, baixo, direita, repetir};
        float total = direcoes.length*bw + (direcoes.length-1)*gap;
        for (int i=0;i<direcoes.length;i++) posicionar(direcoes[i],(w-total)/2+i*(bw+gap),by,bw,48*escala);
        float acaoW = Math.min(180*escala,(w-35*escala)/3f);
        float acoesX = (w-(3*acaoW+2*gap))/2f;
        posicionar(limpar,acoesX,18*escala,acaoW,44*escala);
        posicionar(executar,acoesX+acaoW+gap,18*escala,acaoW,44*escala);
        posicionar(voltar,acoesX+2*(acaoW+gap),18*escala,acaoW,44*escala);
    }

    @Override public void show() { jogo.configurarEntrada(stage); }
    @Override public void render(float delta) {
        float dt = Math.min(delta, .05f);
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) { jogo.mudarTela(new TelaMenu(jogo)); return; }
        if (executando) {
            relogio += dt;
            if (relogio >= INTERVALO_MOVIMENTO) { relogio = 0; proximoPasso(); }
        }
        Gdx.gl.glClearColor(.025f,.035f,.065f,1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.getViewport().apply();
        batch.setProjectionMatrix(stage.getCamera().combined);
        particulasFundo.atualizar(dt);
        batch.begin();
        particulasFundo.desenhar(batch);
        desenharTabuleiro();
        batch.end();
        stage.act(dt);
        stage.draw();
    }
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() { Gdx.input.setInputProcessor(null); }
    @Override public void dispose() {
        particulasFundo.dispose(); stage.dispose(); batch.dispose(); fonte.dispose(); pixel.dispose();
        if (texturaBotao != null) texturaBotao.dispose();
        if (spritePiso != null) spritePiso.dispose();
        if (spriteParede != null) spriteParede.dispose();
        if (spriteRobo != null) spriteRobo.dispose();
        if (spriteDestino != null) spriteDestino.dispose();
        if (spritePortalA != null) spritePortalA.dispose();
        if (spritePortalB != null) spritePortalB.dispose();
        if (spriteInterruptor != null) spriteInterruptor.dispose();
        if (spriteInterruptorAtivo != null) spriteInterruptorAtivo.dispose();
        if (spritePortaFechada != null) spritePortaFechada.dispose();
        if (spritePortaAberta != null) spritePortaAberta.dispose();
    }
}
