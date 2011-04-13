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

import org.codehaus.groovy.ast.*;
import org.codehaus.groovy.transform.*;
import org.codehaus.groovy.control.*;
import java.lang.reflect.Modifier;
import pl.burningice.plugins.image.ast.intarface.FileImageContainer;

/**
 * Object execute transformation of object marked by FileImageContainer annotation
 *
 * @author pawel.gdula@burningice.pl
 */
@GroovyASTTransformation(phase = CompilePhase.CANONICALIZATION)
public class FileImageContainerTransformation extends AbstractImageContainerTransformation {

    @Override
    protected void transformSpecified(ClassNode node, String fieldName) {
        // implements interface
        node.addInterface(new ClassNode(FileImageContainer.class));
        // imageExtension field
        FieldNode imageExtension = new FieldNode("imageExtension", Modifier.PRIVATE, new ClassNode(String.class), new ClassNode(node.getClass()), null);
        node.addField(imageExtension);
        addGetter(imageExtension, node);
        addSetter(imageExtension, node);
        addNullableConstraint(node, "imageExtension");
    }
}

