package io.github.some_example_name;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
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

public class TelaInicial implements Screen {
    private final Main jogo;
    private final Stage stage;
    private final SpriteBatch batch;
    private final BitmapFont fonte;
    private final Texture logoTexture;
    private final Texture texturaBotao;
    private final ParticulasFundo particulasFundo;

    public TelaInicial(Main jogo) {
        this.jogo = jogo;
        stage = new Stage(new ScreenViewport());
        batch = new SpriteBatch();
        fonte = FonteUtil.carregar("pressStart2P.ttf", 24);
        logoTexture = new Texture(Gdx.files.internal("s_logo.png"));
        texturaBotao = new Texture(Gdx.files.internal("s_botao_menu.png"));
        particulasFundo = new ParticulasFundo();
        criarMenu();
    }

    private Stack criarBotao(String texto, float r, float g, float b, final Runnable acao) {
        Stack botao = new Stack();
        Image fundo = new Image(texturaBotao);
        Label label = new Label(
            texto,
            new Label.LabelStyle(fonte, com.badlogic.gdx.graphics.Color.WHITE)
        );

        EfeitoPulinho.aplicar(botao, acao, () -> jogo.tocarSomClique());

        label.setAlignment(Align.center);
        botao.add(fundo);
        botao.add(label);
        botao.addListener(new ClickListener() {
            @Override public void clicked(InputEvent event, float x, float y) {
                acao.run();
            }
        });
        return botao;
    }

    private void criarMenu() {
        Table tabela = new Table();
        tabela.setFillParent(true);
        tabela.pad(12);
        stage.addActor(tabela);

        Image logo = new Image(logoTexture);
        tabela.add(logo)
            .width(800).height(400)
            .padBottom(20).row();

        tabela.add(criarBotao("JOGAR", 45, 99, 161,  new Runnable() {
            @Override public void run() { jogo.mudarTela(new TelaMenu(jogo)); }
        })).width(400).height(120).pad(5).row();

        tabela.add(criarBotao("SOBRE", 45, 99, 161, new Runnable() {
            @Override public void run() { jogo.mudarTela(new TelaSobre(jogo)); }
        })).width(400).height(120).pad(5).row();

        tabela.add(criarBotao("CONFIGURAÇÕES", 45, 99, 161, new Runnable() {
            @Override public void run() { jogo.mudarTela(new TelaConfiguracoes(jogo)); }
        })).width(400).height(120).pad(5).row();

        tabela.add(criarBotao("SAIR", 189, 0, 35, new Runnable() {
            @Override public void run() { Gdx.app.exit(); }
        })).width(400).height(120).pad(5).row();
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
        logoTexture.dispose();
        texturaBotao.dispose();
    }
}
