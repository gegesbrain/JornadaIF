package io.github.some_example_name;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.InputAdapter;

/** Splash provisória de cinco segundos. Substitua o símbolo pixelado pela sua logo quando quiser. */
public class TelaSplash implements Screen {
    private final Main jogo;
    private final SpriteBatch batch = new SpriteBatch();
    private final BitmapFont fonte = new BitmapFont();
    private final Texture pixel;
    private float tempo;
    private boolean iniciouTransicao;
    private static final float DURACAO = 5f;

    public TelaSplash(Main jogo) {
        this.jogo = jogo;
        Pixmap p = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        p.setColor(Color.WHITE);
        p.fill();
        pixel = new Texture(p);
        p.dispose();
        fonte.setColor(Color.WHITE);
        fonte.getData().setScale(2.2f);
    }

    @Override public void show() { jogo.configurarEntrada(new InputAdapter()); }

    @Override
    public void render(float delta) {
        tempo += delta;
        Gdx.gl.glClearColor(0.025f, 0.035f, 0.065f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        float w = Gdx.graphics.getWidth();
        float h = Gdx.graphics.getHeight();
        batch.begin();

        String titulo = "JORNADA IF";
        GlyphLayout layout = new GlyphLayout(fonte, titulo);
        fonte.draw(batch, layout, (w - layout.width) / 2f, h / 2f + 2);
        fonte.getData().setScale(1f);
        String subtitulo = "Preparando a jornada...";
        GlyphLayout sub = new GlyphLayout(fonte, subtitulo);
        fonte.draw(batch, subtitulo, (w - sub.width) / 2f, h / 2f - 35);
        fonte.getData().setScale(2.2f);
        batch.end();

        if (tempo >= DURACAO && !iniciouTransicao) {
            iniciouTransicao = true;
            jogo.mudarTela(new TelaInicial(jogo));
        }
    }

    @Override public void resize(int width, int height) {}
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
    @Override public void dispose() { batch.dispose(); fonte.dispose(); pixel.dispose(); }
}
