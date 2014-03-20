/**
 * Sencha GXT 3.1.0-beta - Sencha for GWT
 * Copyright(c) 2007-2014, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.theme.base.client.info;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.resources.client.ImageResource.ImageOptions;
import com.google.gwt.resources.client.ImageResource.RepeatStyle;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.sencha.gxt.core.client.XTemplates;
import com.sencha.gxt.core.client.dom.XElement;
import com.sencha.gxt.theme.base.client.frame.DivFrame;
import com.sencha.gxt.theme.base.client.frame.DivFrame.DivFrameResources;
import com.sencha.gxt.theme.base.client.frame.DivFrame.DivFrameStyle;
import com.sencha.gxt.theme.base.client.frame.Frame;
import com.sencha.gxt.widget.core.client.info.Info.InfoAppearance;

public class InfoDefaultAppearance implements InfoAppearance {

  public interface InfoResources extends ClientBundle, DivFrameResources {

    @Source("background.png")
    @ImageOptions(repeatStyle = RepeatStyle.Both)
    ImageResource background();

    @Source("bottomBorder.png")
    @ImageOptions(repeatStyle = RepeatStyle.Horizontal)
    ImageResource bottomBorder();

    @Source("bottomLeftBorder.png")
    ImageResource bottomLeftBorder();

    @Source("bottomRightBorder.png")
    ImageResource bottomRightBorder();

    @Source("leftBorder.png")
    @ImageOptions(repeatStyle = RepeatStyle.Vertical)
    ImageResource leftBorder();

    @Source("rightBorder.png")
    @ImageOptions(repeatStyle = RepeatStyle.Vertical)
    ImageResource rightBorder();

    @Source({"com/sencha/gxt/theme/base/client/frame/DivFrame.css", "Info.css"})
    InfoStyle style();

    @Source("topBorder.png")
    @ImageOptions(repeatStyle = RepeatStyle.Horizontal)
    ImageResource topBorder();

    @Source("topLeftBorder.png")
    ImageResource topLeftBorder();

    @Source("topRightBorder.png")
    ImageResource topRightBorder();
  }

  public interface InfoStyle extends DivFrameStyle {
    String info();
  }

  public interface Template extends XTemplates {
    @XTemplate(source = "InfoDefault.html")
    SafeHtml render(InfoStyle style);
  }

  private final Template template;
  private final Frame frame;
  private final InfoStyle style;

  public InfoDefaultAppearance() {
    this(GWT.<Template>create(Template.class), GWT.<InfoResources>create(InfoResources.class));
  }

  public InfoDefaultAppearance(Template template, InfoResources resources) {
    this.template = template;
    this.style = resources.style();
    this.style.ensureInjected();

    frame = new DivFrame(resources);
  }

  @Override
  public XElement getContentElement(XElement parent) {
    return parent.selectNode("." + style.info());
  }

  @Override
  public void render(SafeHtmlBuilder sb) {
    frame.render(sb, Frame.EMPTY_FRAME, template.render(style));
  }

}