package se.liu.albhe576.project;

public interface UI {
    UIState render(InputState inputState, Renderer renderer, long window, int score, int hp);
}
