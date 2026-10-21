package io.github.some_example_name;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;

import java.util.Random;

/** Pequenos pixels coloridos que se espalham brevemente após um toque/clique. */
public class EfeitoToque extends InputAdapter {
    private static final int MAX_PARTICULAS = 90;
    private static final float DURACAO = 0.28f;

    private final float[] x = new float[MAX_PARTICULAS];
    private final float[] y = new float[MAX_PARTICULAS];
    private final float[] vx = new float[MAX_PARTICULAS];
    private final float[] vy = new float[MAX_PARTICULAS];
    private final float[] vida = new float[MAX_PARTICULAS];
    private final float[] tamanho = new float[MAX_PARTICULAS];
    private final int[] cor = new int[MAX_PARTICULAS];
    private final Random random = new Random();
    private int proxima;

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        criar(screenX, Gdx.graphics.getHeight() - screenY);
        return false; // Não impede que o Stage receba o toque.
    }

    private void criar(float px, float py) {
        for (int i = 0; i < 12; i++) {
            int indice = proxima;
            proxima = (proxima + 1) % MAX_PARTICULAS;
            x[indice] = px;
            y[indice] = py;
            float angulo = random.nextFloat() * MathUtils.PI2;
            float velocidade = 140f + random.nextFloat() * 380f;
            vx[indice] = MathUtils.cos(angulo) * velocidade;
            vy[indice] = MathUtils.sin(angulo) * velocidade;
            vida[indice] = DURACAO;
            tamanho[indice] = 8f + random.nextFloat() * 10f;
            cor[indice] = random.nextBoolean() ? 0 : 1; // verde ou vermelho
        }
    }

    public void atualizar(float delta) {
        for (int i = 0; i < MAX_PARTICULAS; i++) {
            if (vida[i] <= 0) continue;
            vida[i] -= delta;
            x[i] += vx[i] * delta;
            y[i] += vy[i] * delta;
            vx[i] *= 0.97f;
            vy[i] *= 0.97f;
        }
    }

    public void desenhar(SpriteBatch batch, Texture pixel) {
        batch.begin();
        for (int i = 0; i < MAX_PARTICULAS; i++) {
            if (vida[i] <= 0) continue;
            float alpha = MathUtils.clamp(vida[i] / DURACAO, 0f, 1f);
            if (cor[i] == 0) batch.setColor(1f, 0.12f, 0.18f, alpha);
            else batch.setColor(0.2f, 1f, 0.35f, alpha);
            batch.draw(pixel, x[i], y[i], tamanho[i], tamanho[i]);
        }
        batch.setColor(1f, 1f, 1f, 1f);
        batch.end();
    }
}
