package io.github.some_example_name;

import com.badlogic.gdx.math.Rectangle;

/** Caixa fisica simples, sem Box2D. */
public class BlocoPuzzle {
    public final Rectangle area;
    public final String texto;
    public float velocidadeY;
    public final float inicioX, inicioY;
    public BlocoPuzzle(String texto, float x, float y) {
        this.texto = texto;
        this.inicioX = x;
        this.inicioY = y;
        area = new Rectangle(x, y, 32, 32);
    }
    public void reiniciar() {
        area.setPosition(inicioX, inicioY);
        velocidadeY = 0;
    }
}
