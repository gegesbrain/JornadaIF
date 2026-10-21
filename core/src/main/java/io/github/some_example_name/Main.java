package io.github.some_example_name;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Preferences;

public class Main extends Game {

    private SpriteBatch efeitosBatch;
    private Texture pixelBranco;
    private EfeitoToque efeitoToque;
    private Transicao transicao;
    private InputProcessor entradaAtual;

    public Preferences preferencias;

    private Music musicaFundo;
    private Sound somClique;
    public boolean musicaLigada = true;
    public boolean somLigado = true;

    @Override
    public void create() {

        preferencias = Gdx.app.getPreferences("JornadaIF");

        somLigado = preferencias.getBoolean("somLigado", true);
        musicaLigada = preferencias.getBoolean("musicaLigada", true);


        // Carrega a música de fundo
        musicaFundo = Gdx.audio.newMusic(
            Gdx.files.internal("ost_musica1.wav")
        );

        // Configura a reprodução contínua
        musicaFundo.setLooping(true);

        // Define o volume (de 0.0 a 1.0)
        musicaFundo.setVolume(0.4f);

        // Inicia a música
        musicaFundo.play();

        somClique = Gdx.audio.newSound(
            Gdx.files.internal("ef_efeito1.wav")
        );

        efeitosBatch = new SpriteBatch();
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(1, 1, 1, 1);
        pixmap.fill();
        pixelBranco = new Texture(pixmap);
        pixmap.dispose();

        efeitoToque = new EfeitoToque();
        transicao = new Transicao(this);
        setScreen(new TelaSplash(this));
    }

    public void definirMusicaLigada(boolean ligada) {
        musicaLigada = ligada;

        if (musicaFundo != null) {
            if (ligada) {
                musicaFundo.play();
            } else {
                musicaFundo.pause();
            }
        }
    }

    public void tocarSomClique() {
        if (somLigado && somClique != null) {
            somClique.play(1.0f);
        }
    }

            /** Método padrão para trocar de tela com a transição reutilizável. */
    public void mudarTela(com.badlogic.gdx.Screen novaTela) {
        transicao.iniciar(novaTela);
    }

    public void setDuracaoTransicao(float segundos) {
        transicao.setDuracao(segundos);
    }

    /** Use nos métodos show() das telas para combinar partículas de toque e Stage. */
    public void configurarEntrada(InputProcessor entradaDaTela) {
        entradaAtual = new InputMultiplexer(efeitoToque, entradaDaTela);
        if (!transicao.estaAtiva()) {
            Gdx.input.setInputProcessor(entradaAtual);
        }
    }

    public void restaurarEntrada() {
        if (entradaAtual != null) Gdx.input.setInputProcessor(entradaAtual);
    }

    @Override
    public void render() {
        float delta = Gdx.graphics.getDeltaTime();
        transicao.atualizar(delta);
        super.render();
        efeitoToque.atualizar(delta);
        efeitoToque.desenhar(efeitosBatch, pixelBranco);
        transicao.desenhar(efeitosBatch, pixelBranco);
    }

    @Override
    public void dispose() {
        super.dispose();
        if (musicaFundo != null) {
            musicaFundo.stop();
            musicaFundo.dispose();
        }
        if (transicao != null) transicao.dispose();
        if (efeitosBatch != null) efeitosBatch.dispose();
        if (pixelBranco != null) pixelBranco.dispose();
        if (somClique != null) somClique.dispose();
    }
}
