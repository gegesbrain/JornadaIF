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

public class TelaMenu implements Screen {

    private final Main jogo;
    private final Stage stage;
    private final SpriteBatch batch;
    private final BitmapFont fonte;
    private final Texture texturaBotao;
    private final ParticulasFundo particulasFundo;

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

    public TelaMenu(Main jogo) {
        this.jogo = jogo;
        stage = new Stage(new ScreenViewport());
        batch = new SpriteBatch();
        fonte = FonteUtil.carregar("pressStart2P.ttf", 32);
        texturaBotao = new Texture(Gdx.files.internal("s_botao_menu.png"));
        particulasFundo = new ParticulasFundo();
        criarMenu();
    }

    private Stack criarNivel(final int numero) {
        Stack tile = new Stack();
        Image fundo = new Image(texturaBotao);
        float tom = (numero % 2 == 0) ? .35f : .23f;
        fundo.setColor(tom, .44f, .31f, 1f);
        Label texto = new Label(String.format("NÍVEL %02d", numero),
            new Label.LabelStyle(fonte, Color.WHITE));
        // Texto menor para caber na grade de 15 niveis.
        texto.setFontScale(.65f);
        texto.setAlignment(Align.center);
        texto.setFillParent(true);
        tile.add(fundo);
        tile.add(texto);
        EfeitoPulinho.aplicar(tile,
            () -> jogo.mudarTela(new TelaNivel(jogo, numero)),
            () -> jogo.tocarSomClique());
        return tile;
    }

    private void criarMenu() {
        Table raiz = new Table();
        raiz.setFillParent(true);
        raiz.pad(22);
        stage.addActor(raiz);

        Label titulo = new Label("SELECIONE O NÍVEL",
            new Label.LabelStyle(fonte, Color.WHITE));
        titulo.setAlignment(Align.center);
        raiz.add(titulo).padTop(18).padBottom(24).row();

        // Tres linhas de cinco niveis. Altere TAMANHO e ESPACAMENTO para personalizar.
        final float LARGURA_BOTAO = 300f;
        final float ALTURA_BOTAO = 120f;
        final float ESPACAMENTO = 12f;
        Table grade = new Table();
        for (int numero = 1; numero <= 15; numero++) {
            grade.add(criarNivel(numero)).width(LARGURA_BOTAO)
                .height(ALTURA_BOTAO).pad(ESPACAMENTO);
            if (numero % 5 == 0) grade.row();
        }
        raiz.add(grade).expand().center().row();

        Stack voltar = criarBotao("VOLTAR", 189f/255f, 0f, 35f/255f,
            () -> jogo.mudarTela(new TelaInicial(jogo)));
        raiz.add(voltar).width(260).height(75).padBottom(18);
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
