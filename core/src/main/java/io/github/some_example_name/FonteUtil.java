package io.github.some_example_name;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;

public class FonteUtil {

    public static BitmapFont carregar(String caminho, int tamanho) {
        FreeTypeFontGenerator gerador =
            new FreeTypeFontGenerator(Gdx.files.internal(caminho));

        FreeTypeFontGenerator.FreeTypeFontParameter parametros =
            new FreeTypeFontGenerator.FreeTypeFontParameter();

        parametros.size = tamanho;

        BitmapFont fonte = gerador.generateFont(parametros);
        gerador.dispose();

        return fonte;
    }
}
