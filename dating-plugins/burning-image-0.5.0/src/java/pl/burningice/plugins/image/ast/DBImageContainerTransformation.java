/*
Copyright (c) 2009 Pawel Gdula <pawel.gdula@burningice.pl>

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in
all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
THE SOFTWARE.
*/
package pl.burningice.plugins.image.ast;

import groovy.lang.Closure;
import org.codehaus.groovy.ast.*;
import org.codehaus.groovy.ast.expr.*;
import org.codehaus.groovy.ast.stmt.BlockStatement;
import org.codehaus.groovy.ast.stmt.ExpressionStatement;
import org.codehaus.groovy.ast.stmt.Statement;
import org.codehaus.groovy.control.CompilePhase;
import org.codehaus.groovy.transform.GroovyASTTransformation;
import pl.burningice.plugins.image.ast.intarface.DBImageContainer;
import pl.burningice.plugins.image.container.DeleteDbImageCommand;

import java.lang.reflect.Modifier;
import java.util.Map;

/**
 * Class execute transformation of objects marked by DBImageContainer annotation
 *
 * @author pawel.gdula@burningice.pl
 */
@GroovyASTTransformation(phase = CompilePhase.CANONICALIZATION)
public class DBImageContainerTransformation extends AbstractImageContainerTransformation {

    @Override
    protected void transformSpecified(ClassNode node, String fieldName) {
        // implements interface
        node.addInterface(new ClassNode(DBImageContainer.class));
        // add relation with images table
        FieldNode imageBindField = new FieldNode("biImage", Modifier.PRIVATE, new ClassNode(Map.class), new ClassNode(node.getClass()), null);
        node.addField(imageBindField);
        addGetter(imageBindField, node);
        addSetter(imageBindField, node);
        addNullableConstraint(node, "biImage");
        // add hasMany relation
        FieldNode hasManyField = getHasManyField(node);
        MapExpression mapValues = (MapExpression)hasManyField.getInitialExpression();
        mapValues.addMapEntryExpression(new ConstantExpression("biImage"), new ClassExpression(new ClassNode(Image.class)));
        // add beforeDelete handler
        MethodNode beforeDeleteMethod = getBeforeDeleteMethod(node);
        ((BlockStatement)beforeDeleteMethod.getCode()).addStatement(createDeleteImageCommandCall());
        // add caching
        FieldNode mappingField = getMappingField(node);
        ((BlockStatement)((ClosureExpression)mappingField.getInitialExpression()).getCode()).addStatement(createBiImageFieldMapping());
    }

    private FieldNode getMappingField(ClassNode node){
        final String fieldName = "mapping";
        FieldNode mappingField = node.getDeclaredField(fieldName);

        if (mappingField == null){
            ClosureExpression mappingExpression = new ClosureExpression(new Parameter[0], new BlockStatement());
            // this is very important in ClosureExpression  - in other case NPE! 
            mappingExpression.setVariableScope(new VariableScope());

            mappingField = new FieldNode(
                fieldName,
                Modifier.STATIC | Modifier.PRIVATE,
                new ClassNode(Closure.class),
                new ClassNode(DBImageContainer.class),
                mappingExpression);

            addGetter(mappingField, node, Modifier.STATIC | Modifier.PUBLIC);
            addSetter(mappingField, node, Modifier.STATIC | Modifier.PUBLIC);
            node.addField(mappingField);
        }

        return mappingField;
    }

    private MethodNode getBeforeDeleteMethod(ClassNode node){
        final String methodName = "beforeDelete";
        MethodNode beforeDeleteMethod = node.getDeclaredMethod(methodName, new Parameter[0]);

        if (beforeDeleteMethod == null){
            beforeDeleteMethod = new MethodNode(methodName, Modifier.PUBLIC, new ClassNode(Object.class), new Parameter[0], new ClassNode[0], new BlockStatement());
            node.addMethod(beforeDeleteMethod);
        }

        return beforeDeleteMethod;
    }

    private Statement createDeleteImageCommandCall() {
        return new ExpressionStatement(
            new StaticMethodCallExpression(
                new ClassNode(DeleteDbImageCommand.class),
                "execute",
                new ArgumentListExpression(new VariableExpression("this"))
            )
        );
    }

    private Statement createBiImageFieldMapping() {
        NamedArgumentListExpression namedarg = new NamedArgumentListExpression();
        namedarg.addMapEntryExpression(new ConstantExpression("cache"), new BooleanExpression(new ConstantExpression(true)));
        namedarg.addMapEntryExpression(new ConstantExpression("lazy"), new BooleanExpression(new ConstantExpression(false)));

        MethodCallExpression constExpr = new MethodCallExpression(VariableExpression.THIS_EXPRESSION,
                                                                  new ConstantExpression("biImage"),
                                                                  namedarg);
        return new ExpressionStatement(constExpr);
    }
}