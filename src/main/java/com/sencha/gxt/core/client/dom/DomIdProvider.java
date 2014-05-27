/**
 * Sencha GXT 3.1.0-beta - Sencha for GWT
 * Copyright(c) 2007-2014, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.core.client.dom;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.Widget;

/**
 * Replacable class that provides IDs for elements and widgets. By default, asks XDOM for a new unique ID, but can
 * be customized and replaced to (for example) provide an ID based on specific attributes about the widget or its
 * location in the application.
 * <p />
 * While not required, IDs are strongly encouraged to be legal to use in CSS expressions without any escaping, as
 * various tools such as HtmlLayoutContainer, DomQuery, and XElement will do no escaping when using them. According
 * to http://www.w3.org/TR/CSS21/grammar.html#scanner, these IDs should match this grammar:
 *
 * <code><pre>
 * %option case-insensitive
 *
 * h		[0-9a-f]
 * nonascii	[\240-\377]
 * unicode		\\{h}{1,6}(\r\n|[ \t\r\n\f])?
 * escape		{unicode}|\\[^\r\n\f0-9a-f]
 * nmstart		[_a-z]|{nonascii}|{escape}
 * nmchar		[_a-z0-9-]|{nonascii}|{escape}
 * ident		-?{nmstart}{nmchar}*
 * </pre></code>
 *
 * or roughly expressed as a regex, {@code -?[_a-zA-Z][_a-zA-Z0-9-]*}.
 *
 * <p/>
 *
 * You can replace the default implementation of this class with your own type that extends it through a replace-with
 * directive in your module file, something like this:
 * <code><pre>
 * &lt;replace-with class="path.to.my.CustomDomIdProvider">
 *   &lt;when-type-is class="com.sencha.gxt.core.client.dom.DomIdProvider" />
 * &lt;/replace-with>
 * </pre></code>
 * In that custom implementation, you can override the two non-static methods:
 * {@link #generateIdForElement(com.google.gwt.dom.client.Element)}, and
 * {@link #generateIdForWidget(com.google.gwt.user.client.ui.Widget)}.
 */
public class DomIdProvider {
  private static final DomIdProvider instance = GWT.create(DomIdProvider.class);
  public static String generateId(Widget widget) {
    return instance.generateIdForWidget(widget);
  }

  public static String generateId(Element element) {
    return instance.generateIdForElement(element);
  }

  public String generateIdForElement(Element element) {
    return XDOM.getUniqueId();
  }

  public String generateIdForWidget(Widget widget) {
    return XDOM.getUniqueId();
  }

}
