package edu.arizona.biosemantics.matrixreview.client.matrix.field;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.resources.client.ImageResource.ImageOptions;
import com.google.gwt.resources.client.ImageResource.RepeatStyle;
import com.sencha.gxt.core.client.resources.StyleInjectorHelper;
import com.sencha.gxt.widget.core.client.button.IconButton.IconConfig;
import edu.arizona.biosemantics.matrixreview.client.matrix.field.DualTreeField.DualTreeFieldAppearance;

public class DualTreeFieldDefaultAppearance implements DualTreeFieldAppearance {

  public interface DualListFieldResources extends ClientBundle {
    @Source("DualListField.css")
    DualListFieldStyle css();
    
    @Source("up2.gif")
    @ImageOptions(repeatStyle = RepeatStyle.None)
    ImageResource up();

    @Source("doubleright2.gif")
    @ImageOptions(repeatStyle = RepeatStyle.None)
    ImageResource allRight();
    
    @Source("left2.gif")
    @ImageOptions(repeatStyle = RepeatStyle.None)
    ImageResource left();
    
    @Source("right2.gif")
    @ImageOptions(repeatStyle = RepeatStyle.None)
    ImageResource right();
    
    @Source("doubleleft2.gif")
    @ImageOptions(repeatStyle = RepeatStyle.None)
    ImageResource allLeft();
    
    @Source("down2.gif")
    @ImageOptions(repeatStyle = RepeatStyle.None)
    ImageResource down();
  }
  
  public interface DualListFieldStyle extends CssResource {
    String up();
    
    String allRight();
    
    String right();
    
    String left();
    
    String allLeft();
    
    String down();
  }
  
  private final DualListFieldResources resources;
  private final DualListFieldStyle style;
  
  public DualTreeFieldDefaultAppearance() {
    this(GWT.<DualListFieldResources>create(DualListFieldResources.class));
  }
  
  public DualTreeFieldDefaultAppearance(DualListFieldResources resources) {
    this.resources = resources;
    this.style = this.resources.css();
    
    StyleInjectorHelper.ensureInjected(style, true);
  }
  
  @Override
  public IconConfig up() {
    return new IconConfig(style.up());
  }

  @Override
  public IconConfig allRight() {
    return new IconConfig(style.allRight());
  }

  @Override
  public IconConfig right() {
    return new IconConfig(style.right());
  }

  @Override
  public IconConfig left() {
    return new IconConfig(style.left());
  }

  @Override
  public IconConfig allLeft() {
    return new IconConfig(style.allLeft());
  }

  @Override
  public IconConfig down() {
    return new IconConfig(style.down());
  }

}
