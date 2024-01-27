package se.liu.albhe576.project;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class SwingGameComponent extends JComponent {

  private ArrayList<Entity> entities;


  private Point convertSizeToScreenSpace(float [] size){
      float width = size[0];
      float height = size[1];
      int screenWidth = convertWidthToScreenSpace(width);
      int screenHeight = convertHeightToScreenSpace(height);

      return new Point(screenWidth, screenHeight);
  }

  private int convertWidthToScreenSpace(float size){
      return (int)(size * this.getWidth());
  }
    private int convertHeightToScreenSpace(float size){
	return (int)(size * this.getHeight());
    }

  private Point convertCoordinatesToScreenSpace(float entityX, float entityY, Point size){
      int x = (int) (entityX - size.x / 2 + Game.SCREEN_WIDTH / 2);
      int y = (int) (entityY - size.y / 2 + Game.SCREEN_HEIGHT / 2);
      return new Point(x, y);
  }

  private void drawEntity(Graphics2D g2d, Entity entity){
      // Swing treats the given coordinates as the top left corner
      // While we treat it as the middle of the texture
      Point size = convertSizeToScreenSpace(entity.getTextureSize());
      Point position = convertCoordinatesToScreenSpace(entity.x, entity.y, size);


      Texture texture = entity.getTexture();
      ImageIcon icon = new ImageIcon();
      icon.setImage(texture.getImage());
      g2d.drawImage(icon.getImage(), position.x, position.y, size.x, size.y, null);


  }
    private void drawBounds(Graphics2D g2d, Entity entity){
      	Point textureSize = convertSizeToScreenSpace(entity.getTextureSize());
	Bounds bounds = entity.getBounds();


	int w = convertWidthToScreenSpace(bounds.getWidth());
	int h = convertHeightToScreenSpace(bounds.getHeight());
	int xOffset = convertWidthToScreenSpace(bounds.getTextureOffsetX());
	int yOffset = convertHeightToScreenSpace(bounds.getTextureOffsetY());

	Point position = convertCoordinatesToScreenSpace(entity.x, entity.y, new Point(w, h));
	position.x += xOffset;
	position.y += yOffset;

	g2d.setColor(entity.getBounds().getColor());
	g2d.drawRect(position.x, position.y,w, h);
    }

  @Override
  protected void paintComponent(Graphics g) {
    super.paintComponent(g);
    final Graphics2D g2d = (Graphics2D) g;

    g2d.setColor(Color.BLACK);
    g2d.fillRect(0,0,this.getWidth(), this.getHeight());

    if(this.entities != null){
	for(int i = 0; i <this.entities.size(); i++){
	    Entity entity = this.entities.get(i);
	    drawEntity(g2d, entity);
	    drawBounds(g2d, entity);
	}
    }
  }

  public void setEntities(ArrayList<Entity> entities) {
    this.entities = entities;
  }

  public SwingGameComponent(int w, int h) {
    this.entities = null;
    this.setPreferredSize(new Dimension(w, h));
  }
}
