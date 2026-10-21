
package io.github.some_example_name;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class TelaConfiguracoes implements Screen {

    private final Main jogo;
    private final Stage stage;
    private final SpriteBatch batch;
    private final BitmapFont fonte;
    private final Texture texturaBotao;
    private final ParticulasFundo particulasFundo;

    private boolean efLigado;
    private boolean musicaLigada;

    private Label textoSom;
    private Label textoMusica;

    public TelaConfiguracoes(Main jogo) {
        this.jogo = jogo;

        // Recupera as configurações salvas.
        efLigado = jogo.preferencias.getBoolean("somLigado", true);
        musicaLigada = jogo.preferencias.getBoolean("musicaLigada", true);

        // Aplica as configurações recuperadas ao jogo.
        jogo.somLigado = efLigado;
        jogo.definirMusicaLigada(musicaLigada);

        stage = new Stage(new ScreenViewport());
        batch = new SpriteBatch();

        fonte = FonteUtil.carregar("pressStart2P.ttf", 32);

        texturaBotao = new Texture(
            Gdx.files.internal("s_botao_menu.png")
        );

        particulasFundo = new ParticulasFundo();

        criarMenu();
    }

    private Stack criarBotao(
        String texto,
        float r,
        float g,
        float b,
        Runnable acao
    ) {
        Stack botao = new Stack();

        Image fundo = new Image(texturaBotao);
        fundo.setColor(r, g, b, 1f);
        botao.add(fundo);

        Label label = new Label(
            texto,
            new Label.LabelStyle(fonte, Color.WHITE)
        );

        label.setAlignment(Align.center);
        label.setFillParent(true);

        botao.add(label);

        // Efeito visual e som de clique.
        EfeitoPulinho.aplicar(
            botao,
            acao,
            () -> jogo.tocarSomClique()
        );

        return botao;
    }

    private void criarMenu() {
        Table tabela = new Table();

        tabela.setFillParent(true);
        tabela.pad(16f);

        stage.addActor(tabela);

        Label.LabelStyle estilo =
            new Label.LabelStyle(fonte, Color.WHITE);

        tabela.add(
            new Label("CONFIGURACOES", estilo)
        ).padBottom(24f).row();

        // BOTÃO DE EFEITOS SONOROS

        textoSom = new Label("", estilo);
        textoSom.setAlignment(Align.center);

        Stack botaoSom = criarBotao(
            "ALTERAR SOM",
            0.13f, 0.32f, 0.22f,
            () -> {
                efLigado = !efLigado;

                jogo.somLigado = efLigado;

                jogo.preferencias.putBoolean(
                    "somLigado",
                    efLigado
                );

                jogo.preferencias.flush();

                atualizarTextos();
            }
        );

        tabela.add(botaoSom)
            .width(400f)
            .height(50f)
            .pad(20f)
            .row();

        tabela.add(textoSom)
            .width(400f)
            .padBottom(20f)
            .row();

        // BOTÃO DE MÚSICA

        textoMusica = new Label("", estilo);
        textoMusica.setAlignment(Align.center);

        Stack botaoMusica = criarBotao(
            "ALTERAR MUSICA",
            0.13f, 0.32f, 0.22f,
            () -> {
                musicaLigada = !musicaLigada;

                jogo.definirMusicaLigada(musicaLigada);

                jogo.preferencias.putBoolean(
                    "musicaLigada",
                    musicaLigada
                );

                jogo.preferencias.flush();

                atualizarTextos();
            }
        );

        tabela.add(botaoMusica)
            .width(400f)
            .height(50f)
            .pad(20f)
            .row();

        tabela.add(textoMusica)
            .width(400f)
            .padBottom(20f)
            .row();

        // BOTÃO VOLTAR

        Stack botaoVoltar = criarBotao(
            "VOLTAR",
            189f / 255f,
            0f,
            35f / 255f,
            () -> jogo.mudarTela(new TelaInicial(jogo))
        );

        tabela.add(botaoVoltar)
            .width(280f)
            .height(60f)
            .padTop(16f)
            .row();

        atualizarTextos();
    }

    private void atualizarTextos() {
        if (textoSom != null) {
            textoSom.setText(
                "Som: " + (efLigado ? "ligado" : "desligado")
            );
        }

        if (textoMusica != null) {
            textoMusica.setText(
                "Musica: " + (musicaLigada ? "ligada" : "desligada")
            );
        }
    }

    @Override
    public void show() {
        jogo.configurarEntrada(stage);
        atualizarTextos();
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(
            0.025f, 0.035f, 0.065f, 1f
        );

        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        particulasFundo.atualizar(delta);

        batch.begin();
        particulasFundo.desenhar(batch);
        batch.end();

        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void hide() {
        Gdx.input.setInputProcessor(null);
    }

    @Override
    public void dispose() {
        particulasFundo.dispose();
        stage.dispose();
        batch.dispose();
        fonte.dispose();
        texturaBotao.dispose();
    }
}
