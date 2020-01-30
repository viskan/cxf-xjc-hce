/*
 * Copyright 2020 Viskan System AB
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.viskan.cxf.xjc.hce;

// CSOFF
import static com.sun.codemodel.JExpr.TRUE;
import static com.sun.codemodel.JExpr._null;
import static com.sun.codemodel.JExpr._this;
import static com.sun.codemodel.JExpr.cast;
import static com.sun.codemodel.JExpr.lit;
import static com.sun.codemodel.JExpr.ref;
import static com.sun.codemodel.JMod.PUBLIC;
// CSON

import java.util.Objects;

import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;

// CSOFF
import com.sun.codemodel.JBlock;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JExpression;
import com.sun.codemodel.JInvocation;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JVar;
import com.sun.tools.xjc.Options;
import com.sun.tools.xjc.Plugin;
import com.sun.tools.xjc.outline.ClassOutline;
import com.sun.tools.xjc.outline.FieldOutline;
import com.sun.tools.xjc.outline.Outline;
// CSON

/**
 * A utility plug-in that generates {@code hashCode} and {@code equals} methods for generated CXF classes, with zero runtime dependencies.
 */
public class UtilityPlugin extends Plugin
{
    private static final String JAVADOC = "Auto-generated by {@code com.viskan/cxf-xjc-hce}";

    @Override
    public String getOptionName()
    {
        return "Xhashcode-equals";
    }

    @Override
    public String getUsage()
    {
        return "  -Xhashcode-equals  :  Activate to add #hashCode and #equals implementations to generated classes";
    }

    @Override
    public boolean run(Outline outline, Options opt, ErrorHandler errorHandler) throws SAXException
    {
        for (ClassOutline classOutline : outline.getClasses())
        {
            addHashCode(outline, classOutline);
            addEquals(outline, classOutline);
        }
        return true;
    }

    private void addHashCode(Outline outline, ClassOutline classOutline)
    {
        JDefinedClass implementation = classOutline.getImplClass();
        JMethod hashCode = implementation.method(PUBLIC, int.class, "hashCode");
        hashCode.annotate(Override.class);
        hashCode.javadoc().append(JAVADOC);

        JClass objects = outline.getCodeModel().ref(Objects.class);
        JInvocation hash = objects.staticInvoke("hash");
        FieldOutline[] fields = classOutline.getDeclaredFields();
        for (FieldOutline field : fields)
        {
            String fieldName = field.getPropertyInfo().getName(false);
            hash.arg(ref(fieldName));
        }
        hashCode.body()._return(hash);
    }

    private void addEquals(Outline outline, ClassOutline classOutline)
    {
        JDefinedClass implementation = classOutline.getImplClass();
        JMethod equals = implementation.method(PUBLIC, boolean.class, "equals");
        equals.annotate(Override.class);
        equals.javadoc().append(JAVADOC);
        JVar obj = equals.param(Object.class, "obj");

        JBlock body = equals.body();
        body._if(obj.eq(_this()))._then()._return(lit(true));
        body._if(obj.eq(_null()).cor(obj.invoke("getClass").ne(_this().invoke("getClass"))))._then()._return(lit(false));

        JVar that = body.decl(implementation, "that");
        body.assign(that, cast(implementation, obj));

        JVar isEqual = body.decl(outline.getCodeModel().BOOLEAN, "isEqual");
        body.assign(isEqual, TRUE);

        JClass objects = outline.getCodeModel().ref(Objects.class);
        FieldOutline[] declaredFields = classOutline.getDeclaredFields();
        for (FieldOutline fieldOutline : declaredFields)
        {
            String fieldName = fieldOutline.getPropertyInfo().getName(false);
            boolean primitive = fieldOutline.getRawType().isPrimitive();

            if (primitive)
            {
                JExpression equalsOperator = _this().ref(fieldName).eq(ref("that").ref(fieldName));
                body.assign(isEqual, isEqual.cand(equalsOperator));
            }
            else
            {
                JInvocation equalsMethod = objects.staticInvoke("equals");
                equalsMethod.arg(_this().ref(fieldName));
                equalsMethod.arg(ref("that").ref(fieldName));
                body.assign(isEqual, isEqual.cand(equalsMethod));
            }
        }
        body._return(isEqual);
    }
}
