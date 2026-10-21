package io.github.some_example_name;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import java.util.Random;

public class ParticulasFundo {

    private static final int QUANTIDADE = 300;

    private float[] x = new float[QUANTIDADE];
    private float[] y = new float[QUANTIDADE];
    private float[] velocidade = new float[QUANTIDADE];
    private float[] tamanho = new float[QUANTIDADE];

    private Texture pixel;

    private Random random = new Random();

    public ParticulasFundo() {

        Pixmap pixmap = new Pixmap(
            1,
            1,
            Pixmap.Format.RGBA8888
        );

        pixmap.setColor(1, 1, 1, 1);
        pixmap.fill();

        pixel = new Texture(pixmap);

        pixmap.dispose();

        for (int i = 0; i < QUANTIDADE; i++) {

            x[i] = random.nextFloat() * Gdx.graphics.getWidth();
            y[i] = random.nextFloat() * Gdx.graphics.getHeight();

            velocidade[i] = 100 + random.nextInt(300);

            tamanho[i] = 2 + random.nextInt(5);
        }
    }

    public void atualizar(float delta) {

        for (int i = 0; i < QUANTIDADE; i++) {

            x[i] += velocidade[i] * delta;

            if (x[i] > Gdx.graphics.getWidth()) {

                x[i] = 0;

                y[i] = random.nextFloat() * Gdx.graphics.getHeight();

                velocidade[i] = 100 + random.nextInt(300);
            }
        }
    }

    public void desenhar(SpriteBatch batch) {

        for (int i = 0; i < QUANTIDADE; i++) {

            batch.draw(
                pixel,
                x[i],
                y[i],
                tamanho[i],
                tamanho[i]
            );
        }
    }

    public void dispose() {
        pixel.dispose();
    }
}
