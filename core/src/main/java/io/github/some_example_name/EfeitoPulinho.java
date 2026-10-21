package io.github.some_example_name;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

public class EfeitoPulinho {

    public static void aplicar(Stack botao, Runnable acao, Runnable somClique) {

        botao.setTransform(true);
        botao.setOrigin(
            botao.getWidth() / 2f,
            botao.getHeight() / 2f
        );

        botao.addListener(new ClickListener() {
            @Override
            public void clicked(
                InputEvent event,
                float x,
                float y) {

                if (somClique != null) {
                    somClique.run();
                }

                botao.clearActions();

                botao.addAction(Actions.sequence(
                    Actions.scaleTo(1.08f, 0.92f, 0.06f),
                    Actions.scaleTo(0.94f, 1.08f, 0.08f),
                    Actions.scaleTo(1.03f, 0.97f, 0.07f),
                    Actions.scaleTo(1f, 1f, 0.08f),
                    Actions.run(acao)
                ));
            }
        });
    }
}
