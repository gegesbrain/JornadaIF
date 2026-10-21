package io.github.some_example_name;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class TelaSobre implements Screen {
    private final Main jogo;
    private final Stage stage;
    private final SpriteBatch batch;
    private final BitmapFont fonte;
    private final Texture texturaBotao;
    private final ParticulasFundo particulasFundo;

    public TelaSobre(Main jogo) {
        this.jogo = jogo;
        stage = new Stage(new ScreenViewport());
        batch = new SpriteBatch();
        fonte = FonteUtil.carregar("pressStart2P.ttf", 32);
        texturaBotao = new Texture(Gdx.files.internal("s_botao_menu.png"));
        particulasFundo = new ParticulasFundo();
        criarTela();
    }

    private Stack criarBotao(
        String texto,
        float r,
        float g,
        float b,
        Runnable acao) {

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

        // Adiciona o efeito de pulinho ao botão.
        EfeitoPulinho.aplicar(botao, acao, () -> jogo.tocarSomClique());

        return botao;
    }


    private void criarTela() {
        Table tabela = new Table();
        tabela.setFillParent(true);
        tabela.center(); // Centraliza o conjunto na tela
        tabela.pad(20);

        stage.addActor(tabela);

        Label.LabelStyle estilo = new Label.LabelStyle(fonte, Color.WHITE);
        Label.LabelStyle estiloLink = new Label.LabelStyle(fonte, Color.PINK);

        Label label1 = new Label("SOBRE A APLICAÇÃO", estilo);
        label1.setAlignment(Align.center);

        Label label2 = new Label("=== Jornada IF ===", estilo);
        label2.setAlignment(Align.center);

        Label label7 = new Label("Projeto avaliativo para a dispciplina:\n\n" +
            "Tópicos Especiais em Informática;\n\n" +
            " 'Jornada IF' foi desenvolvido com JAVA e o programa LibGDX.", estilo);
        label7.setAlignment(Align.left);
        label7.setWrap(true);

        Stack voltar = criarBotao(
            "VOLTAR", 189, 0, 35, new Runnable() {
                @Override
                public void run() {
                    jogo.mudarTela(new TelaInicial(jogo));
                }
            }
        );

        Label labelGitHubAlexandre = new Label("Orientado pelo professor Alexandre Costa", estiloLink);

        labelGitHubAlexandre.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.net.openURI("https://github.com/alexandrecostapb");
            }
        });
        labelGitHubAlexandre.setAlignment(Align.center);

        Label labelGitHubJean = new Label("Feito pelo estudante Jean Walter", estiloLink);

        labelGitHubJean.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.net.openURI("https://github.com/gegesbrain");
            }
        });
        labelGitHubJean.setAlignment(Align.center);

        tabela.add(label1).width(400).height(46).center().padBottom(10);
        tabela.row();

        tabela.add(label2).width(400).height(46).center().padBottom(10);
        tabela.row();

        tabela.add(label7).width(1000).height(50).center().pad(225);
        tabela.row();

        tabela.add(labelGitHubAlexandre).width(400).height(50).padBottom(20);
        tabela.row();

        tabela.add(labelGitHubJean).width(400).height(50);
        tabela.row();

        tabela.add(voltar).width(220).height(64).center().padTop(70);
        tabela.row();
    }


    @Override public void show() { jogo.configurarEntrada(stage); }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.025f, 0.035f, 0.065f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        particulasFundo.atualizar(delta);
        batch.begin();
        particulasFundo.desenhar(batch);
        batch.end();
        stage.act(delta);
        stage.draw();
    }

    @Override public void resize(int width, int height) { stage.getViewport().update(width, height, true); }
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() { Gdx.input.setInputProcessor(null); }

    @Override
    public void dispose() {
        particulasFundo.dispose();
        stage.dispose();
        batch.dispose();
        fonte.dispose();
        texturaBotao.dispose();
    }
}
