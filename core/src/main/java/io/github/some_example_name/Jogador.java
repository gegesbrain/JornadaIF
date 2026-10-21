package io.github.some_example_name;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

/** Personagem de plataforma: horizontal + pulo, com colisao controlada pela TelaNivel. */
public class Jogador {
    public static final float LARGURA = 32, ALTURA = 32;
    private final Vector2 posicao = new Vector2();
    private final Rectangle limite = new Rectangle(0, 0, LARGURA, ALTURA);
    private Texture esquerdaTexture, direitaTexture;
    private Animation<TextureRegion> esquerdaAnimation, direitaAnimation;
    private float estadoDeTempo, velocidadeY;
    private boolean noChao, olhandoDireita = true;
    private float inicioX, inicioY;

    public static final float VELOCIDADE = 260f;

    private boolean moverEsquerdaTouch = false;
    private boolean moverDireitaTouch = false;
    private boolean pularTouch = false;
    private boolean puloAnteriorTouch = false;

    public Jogador() {
        esquerdaTexture = new Texture(Gdx.files.internal("s_jogadorESQ.png"));
        direitaTexture = new Texture(Gdx.files.internal("s_jogadorDIR.png"));
        esquerdaAnimation = new Animation<>(0.15f, TextureRegion.split(esquerdaTexture,32,32)[0]);
        direitaAnimation = new Animation<>(0.15f, TextureRegion.split(direitaTexture,32,32)[0]);
        definirInicio(100, 100);
    }
    public void definirInicio(float x, float y) {
        inicioX=x; inicioY=y; reiniciar();
    }
    public void reiniciar() {
        posicao.set(inicioX,inicioY); velocidadeY=0; noChao=false;
        limite.setPosition(posicao); estadoDeTempo=0;

        moverEsquerdaTouch = false;
        moverDireitaTouch = false;
        pularTouch = false;
        puloAnteriorTouch = false;
    }
    public Vector2 getPosicao() { return posicao; }
    public Rectangle getLimite() { return limite; }
    public float getVelocidadeY() { return velocidadeY; }
    public void setVelocidadeY(float v) { velocidadeY=v; }
    public boolean isNoChao() { return noChao; }
    public void setNoChao(boolean valor) { noChao=valor; }
    public void mover(float x, float y) { posicao.set(x,y); limite.setPosition(posicao); }

    public float lerHorizontal() {

        boolean esquerda =
            Gdx.input.isKeyPressed(Input.Keys.LEFT) ||
                Gdx.input.isKeyPressed(Input.Keys.A) ||
                moverEsquerdaTouch;

        boolean direita =
            Gdx.input.isKeyPressed(Input.Keys.RIGHT) ||
                Gdx.input.isKeyPressed(Input.Keys.D) ||
                moverDireitaTouch;

        int direcao = 0;

        if (esquerda) direcao--;
        if (direita) direcao++;

        if (direcao != 0) {
            olhandoDireita = direcao > 0;
            estadoDeTempo += Gdx.graphics.getDeltaTime();
        } else {
            estadoDeTempo = 0;
        }

        return direcao * VELOCIDADE;
    }

    public boolean pediuPulo() {

        boolean teclado =
            Gdx.input.isKeyJustPressed(Input.Keys.SPACE) ||
                Gdx.input.isKeyJustPressed(Input.Keys.UP) ||
                Gdx.input.isKeyJustPressed(Input.Keys.W);

        // Detecta apenas o início do toque de pulo.
        boolean novoToque = pularTouch && !puloAnteriorTouch;

        puloAnteriorTouch = pularTouch;

        return teclado || novoToque;
    }

    // Métodos usados pelos botões virtuais.
    public void setMoverEsquerda(boolean pressionado) {
        moverEsquerdaTouch = pressionado;
    }

    public void setMoverDireita(boolean pressionado) {
        moverDireitaTouch = pressionado;
    }

    public void setPular(boolean pressionado) {
        pularTouch = pressionado;
    }

    /** Mantido para compatibilidade com outras telas que chamem update(). */
    public void update(float delta) { /* Fisica executada pela TelaNivel. */ }
    public void render(SpriteBatch batch) {
        Animation<TextureRegion> a=olhandoDireita?direitaAnimation:esquerdaAnimation;
        batch.draw(a.getKeyFrame(estadoDeTempo,true),posicao.x,posicao.y,LARGURA,ALTURA);
    }
    public void dispose() { esquerdaTexture.dispose(); direitaTexture.dispose(); }
}
