/**
 * Sencha GXT 3.1.0-beta - Sencha for GWT
 * Copyright(c) 2007-2014, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.core.client.dom;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.resources.client.ImageResource.ImageOptions;
import com.google.gwt.resources.client.ImageResource.RepeatStyle;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.sencha.gxt.core.client.GXT;
import com.sencha.gxt.core.client.XTemplates;
import com.sencha.gxt.core.client.dom.Mask.MaskDefaultAppearance.MaskStyle;
import com.sencha.gxt.core.client.resources.StyleInjectorHelper;

/**
 * Masks a target element by showing a transparent "gray" overlay with support for a message.
 */
public class Mask {

  public interface MaskAppearance {
    void mask(XElement parent, String message);

    String masked();

    String positioned();

    void unmask(XElement parent);
  }

  public static class MaskDefaultAppearance implements MaskAppearance {

    public interface MaskResources extends ClientBundle {
      @ImageOptions(repeatStyle = RepeatStyle.Horizontal)
      ImageResource boxBackground();

      @Source("Mask.css")
      MaskStyle css();
    }

    public interface MaskStyle extends CssResource {
      String box();

      String mask();

      String masked();

      String positioned();

      String text();
    }

    private final MaskResources resources;
    private final MessageTemplates template;

    public MaskDefaultAppearance() {
      this(GWT.<MessageTemplates>create(MessageTemplates.class), GWT.<MaskResources>create(MaskResources.class));
    }

    public MaskDefaultAppearance(MessageTemplates template, MaskResources resources) {
      this.resources = resources;
      this.template = template;
      StyleInjectorHelper.ensureInjected(this.resources.css(), true);
    }

    @Override
    public void mask(XElement parent, String message) {
      XElement mask = XElement.createElement("div");
      mask.setClassName(resources.css().mask());
      parent.appendChild(mask);

      XElement box = null;
      if (message != null) {
        box = XDOM.create(template.template(resources.css(), SafeHtmlUtils.htmlEscape(message))).cast();
        parent.appendChild(box);
      }

      if (GXT.isIE() && !(GXT.isIE7()) && "auto".equals(parent.getStyle().getHeight())) {
        mask.setSize(parent.getOffsetWidth(), parent.getOffsetHeight());
      }

      if (box != null) {
        box.updateZIndex(0);
        box.center(parent);
      }

    }

    @Override
    public String masked() {
      return resources.css().masked();
    }

    @Override
    public String positioned() {
      return resources.css().positioned();
    }

    @Override
    public void unmask(XElement parent) {
      XElement mask = parent.selectNode("> ." + resources.css().mask());
      if (mask != null) {
        mask.removeFromParent();
      }
      XElement box = parent.selectNode("> ." + resources.css().box());
      if (box != null) {
        box.removeFromParent();
      }
    }

  }

  public interface MessageTemplates extends XTemplates {

    @XTemplate("<div class=\"{style.box}\"><div class=\"{style.text}\">{message}</div></div>")
    SafeHtml template(MaskStyle style, String message);

  }

  private static final Mask instance = GWT.create(Mask.class);

  /**
   * Masks the given element.
   * 
   * @param target the element to mask
   * @param message the message
   */
  public static void mask(XElement target, String message) {
    instance.maskInternal(target, message);
  }

  /**
   * Unmasks the given element.
   * 
   * @param target the target element
   */
  public static void unmask(XElement target) {
    instance.unmaskInternal(target);
  }

  private final MaskAppearance appearance;

  private Mask() {
    this.appearance = GWT.create(MaskAppearance.class);
  }

  public MaskAppearance getAppearance() {
    return appearance;
  }

  private void maskInternal(XElement parent, String message) {
    parent.addClassName(appearance.masked());
    if ("static".equals(parent.getComputedStyle("position"))) {
      parent.addClassName(appearance.positioned());
    }
    appearance.unmask(parent);
    appearance.mask(parent, message);

  }

  private void unmaskInternal(XElement parent) {
    parent.removeClassName(appearance.masked());
    appearance.unmask(parent);
  }

}
