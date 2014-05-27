/**
 * Sencha GXT 3.1.0-beta - Sencha for GWT
 * Copyright(c) 2007-2014, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.data.rebind;

import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.JMethod;
import com.google.gwt.dev.util.Name;
import com.google.gwt.editor.rebind.model.ModelUtils;
import com.google.gwt.thirdparty.guava.common.base.Joiner;
import com.google.gwt.user.rebind.SourceWriter;
import com.sencha.gxt.data.shared.LabelProvider;

public class LabelProviderCreator extends ValueProviderCreator {

  private final JClassType labelProviderInterface;
  public LabelProviderCreator(GeneratorContext ctx, TreeLogger l, JMethod labelProviderMethodDecl) {
    super(ctx, l, labelProviderMethodDecl);
    labelProviderInterface = ctx.getTypeOracle().findType(Name.getSourceNameForClass(LabelProvider.class));
    setReadability(RequiredReadability.GET);
  }

  @Override
  protected JClassType getObjectType() {
    JClassType[] params = ModelUtils.findParameterizationOf(labelProviderInterface, getSupertype());

    return params[0];
  }

  @Override
  protected void create(SourceWriter sw) throws UnableToCompleteException {
    sw.println("public static final %1$s INSTANCE = new %1$s();", getSimpleName());

    // @Override
    sw.println("public String getLabel(%1$s item) {", getObjectTypeName());

    appendGetterBody(sw, "item");

    sw.println("}");
  }

  @Override
  protected String getGetterExpression(String objectName) throws UnableToCompleteException {
    return "\"\" + " + super.getGetterExpression(objectName);
  }

  @Override
  protected String getSimpleName() {
    return getObjectType().getName().replace('.', '_') + "_" + Joiner.on("_").join(path) + "_ModelLabelProviderImpl";
  }

}
