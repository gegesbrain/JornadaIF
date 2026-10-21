package io.github.some_example_name;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/**
 * Transição reutilizável de fade para preto e fade de entrada.
 * Use Main.mudarTela(tela) para aplicá-la a qualquer navegação.
 */
public class Transicao {
    private final Main jogo;
    private Screen telaDestino;
    private float duracao = 0.45f;
    private float tempo;
    private boolean ativa;
    private boolean trocouTela;

    public Transicao(Main jogo) {
        this.jogo = jogo;
    }

    public void iniciar(Screen destino) {
        if (destino == null || ativa) {
            if (destino != null) destino.dispose();
            return;
        }
        if (jogo.getScreen() == null) {
            jogo.setScreen(destino);
            return;
        }
        telaDestino = destino;
        tempo = 0f;
        ativa = true;
        trocouTela = false;
        Gdx.input.setInputProcessor(null);
    }

    public void setDuracao(float segundos) {
        duracao = Math.max(0.1f, segundos);
    }

    public float getDuracao() {
        return duracao;
    }

    public void atualizar(float delta) {
        if (!ativa) return;
        tempo += Math.min(delta, 0.05f);
        if (!trocouTela && tempo >= duracao / 2f) {
            Screen anterior = jogo.getScreen();
            jogo.setScreen(telaDestino);
            telaDestino = null;
            if (anterior != null) anterior.dispose();
            trocouTela = true;
        }
        if (tempo >= duracao) {
            ativa = false;
            tempo = duracao;
            jogo.restaurarEntrada();
        }
    }

    public void desenhar(SpriteBatch batch, Texture pixel) {
        if (!ativa) return;
        float metade = duracao / 2f;
        float alpha = tempo < metade
                ? tempo / metade
                : 1f - ((tempo - metade) / metade);
        alpha = Math.max(0f, Math.min(1f, alpha));
        batch.begin();
        batch.setColor(0f, 0f, 0f, alpha);
        batch.draw(pixel, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        batch.setColor(1f, 1f, 1f, 1f);
        batch.end();
    }

    public boolean estaAtiva() {
        return ativa;
    }

    public void dispose() {
        if (telaDestino != null) {
            telaDestino.dispose();
            telaDestino = null;
        }
    }
}
