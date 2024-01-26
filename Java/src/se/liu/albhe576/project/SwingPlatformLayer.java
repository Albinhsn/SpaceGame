package se.liu.albhe576.project;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

public class SwingPlatformLayer extends PlatformLayer
{
    private static enum Key
    {
        K_W, K_A, K_S, K_D, K_SPACE;
    }

    private static class ReleaseAction extends AbstractAction{
        private final InputState inputState;
        private final Key key;
        private ReleaseAction(InputState inputState, Key key){
            this.inputState = inputState;
            this.key = key;
        }

        @Override
        public void actionPerformed(ActionEvent e){
            switch(this.key){
                case K_W:{
                    this.inputState.setW(false);
                    break;
                }
                case K_A:{
                    this.inputState.setA(false);
                    break;
                }
                case K_S:{
                    this.inputState.setS(false);
                    break;
                }
                case K_D:{
                    this.inputState.setD(false);
                    break;
                }
                case K_SPACE:{
                    this.inputState.setSpace(false);
                    break;
                }
            }

        }
    }
    private static class PressAction extends AbstractAction{
        private final InputState inputState;
        private final Key key;
        private PressAction(InputState inputState, Key key){
            this.inputState = inputState;
            this.key = key;
        }

        @Override
        public void actionPerformed(ActionEvent e){
            switch(this.key){
                case K_W:{
                    this.inputState.setW(true);
                    break;
                }
                case K_A:{
                    this.inputState.setA(true);
                    break;
                }
                case K_S:{
                    this.inputState.setS(true);
                    break;
                }
                case K_D:{
                    this.inputState.setD(true);
                    break;
                }
                case K_SPACE:{
                    this.inputState.setSpace(true);
                    break;
                }
            }
        }
    }
    private void setInputMap(){
        JComponent pane = this.frame.getRootPane();

        final InputMap in= pane.getInputMap();
        // Pressed
        in.put(KeyStroke.getKeyStroke(KeyEvent.VK_A, 0, false), "left");
        in.put(KeyStroke.getKeyStroke(KeyEvent.VK_D, 0, false), "right");
        in.put(KeyStroke.getKeyStroke(KeyEvent.VK_W, 0, false), "up");
        in.put(KeyStroke.getKeyStroke(KeyEvent.VK_S, 0, false), "down");
        in.put(KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, 0, false), "space");

        // Release
        in.put(KeyStroke.getKeyStroke(KeyEvent.VK_A, 0, true), "rleft");
        in.put(KeyStroke.getKeyStroke(KeyEvent.VK_D, 0, true), "rright");
        in.put(KeyStroke.getKeyStroke(KeyEvent.VK_W, 0, true), "rup");
        in.put(KeyStroke.getKeyStroke(KeyEvent.VK_S, 0, true), "rdown");
        in.put(KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, 0, true), "rspace");

        final ActionMap actionMap = pane.getActionMap();
        actionMap.put("up", new PressAction(inputState, Key.K_W));
        actionMap.put("down", new PressAction(inputState, Key.K_S));
        actionMap.put("rup", new ReleaseAction(inputState, Key.K_W));
        actionMap.put("rdown", new ReleaseAction(inputState, Key.K_S));
        actionMap.put("left", new PressAction(inputState, Key.K_A));
        actionMap.put("rleft", new ReleaseAction(inputState, Key.K_A));
        actionMap.put("right", new PressAction(inputState, Key.K_D));
        actionMap.put("rright", new ReleaseAction(inputState, Key.K_D));
    }

    private JFrame frame;
    private final SwingGameComponent gameComponent;

    protected SwingPlatformLayer(final int width, final int height) {
        super(width, height);
        this.frame = new JFrame();
        this.gameComponent = new SwingGameComponent(width, height);
        this.lastDrawCall = 0;
    }

    private long lastDrawCall;
    @Override public void drawEntities(final ArrayList<Entity> entities) {
        // ToDO do more testing to see what fps we actually can manage
        if(this.lastDrawCall + 16 <= System.currentTimeMillis()){
            entities.sort(new ZComparator());
            this.gameComponent.setEntities(entities);
            this.gameComponent.repaint();
            this.lastDrawCall = System.currentTimeMillis();
        }
    }

    private final String[] YES_NO_OPTIONS = new String[]{"Yes!", "No!"};

    private JMenuBar createMenuBars(){
      final Action quitMenuAction = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
          int optionChosen = JOptionPane.showOptionDialog(
              frame,
              "U sure you want to exit?",
              "Exiting?",
              JOptionPane.YES_NO_OPTION,
              JOptionPane.QUESTION_MESSAGE,
              null,
              YES_NO_OPTIONS,
              YES_NO_OPTIONS[1]);

          if (optionChosen == 0) {
            System.exit(0);
          }
        }
      };

      // Create the bar
      final JMenuBar menuBar = new JMenuBar();
      final JMenu menu = new JMenu("Menu");
      final JMenuItem exitItem = new JMenuItem("Avsluta", 'A');
      menu.add(exitItem);
      exitItem.addActionListener(quitMenuAction);
      menuBar.add(menu);

      return menuBar;

    }

    private void initJFrame(){
      JMenuBar menuBar = this.createMenuBars();
      this.frame = new JFrame();
      this.frame.setJMenuBar(menuBar);
      this.frame.setResizable(true);
      this.frame.setLayout(new BorderLayout());
      this.frame.add(this.gameComponent);
      this.frame.pack();
      this.frame.setVisible(true);
      this.frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
      this.setInputMap();
    }

    @Override public void run() {
        this.initJFrame();
        while(true){
            System.out.printf("");
        }
    }
}
