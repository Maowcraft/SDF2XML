package maow.sdf2xml.util;

import maow.javasdf.attribute.*;
import org.apache.commons.text.WordUtils;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import java.util.Collection;

public class Converter {
    public static Document convertSDFToXML(maow.javasdf.document.Document document) {
        final Document xml = DocumentHelper.createDocument();
        final Element rootElement = xml.addElement(document.getName());
        for (InnerAttribute rootInnerAttribute : document.getRootInnerAttributes()) {
            rootElement.addAttribute(fixName(rootInnerAttribute.getName()), rootInnerAttribute.getValue());
        }
        for (Attribute attribute : document.getAttributes()) {
            if (attribute.getName().equals("")) continue;
            final Element element = rootElement.addElement(WordUtils.capitalize(fixName(attribute.getName())));
            if (attribute instanceof AbstractAttribute) {
                for (InnerAttribute innerAttribute : ((AbstractAttribute) attribute).getInnerAttributes()) {
                    element.addAttribute(fixName(innerAttribute.getName()), innerAttribute.getValue());
                }
                if (attribute instanceof CategoryAttribute) {
                    Collection<NestedAttribute> nestedAttributes = ((CategoryAttribute) attribute).getNestedAttributes();
                    if (nestedAttributes.size() == 0 && attribute.getValue().length() > 0) {
                        element.setText(attribute.getValue());
                    }
                    for (NestedAttribute nestedAttribute : nestedAttributes) {
                        addNestedElement(element, nestedAttribute);
                    }
                } else {
                    if (attribute.getValue().length() > 0) {
                        element.setText(attribute.getValue());
                    }
                }
            }
        }
        return xml;
    }

    private static void addNestedElement(Element currentElement, NestedAttribute nestedAttribute) {
        final Element element = currentElement.addElement(WordUtils.capitalize(fixName(nestedAttribute.getName())));
        for (InnerAttribute nestedInnerAttribute : nestedAttribute.getInnerAttributes()) {
            element.addAttribute(fixName(nestedInnerAttribute.getName()), nestedInnerAttribute.getValue());
        }
        if (nestedAttribute.getNestedAttributes().size() > 0) {
            for (NestedAttribute nestedAttribute1 : nestedAttribute.getNestedAttributes()) {
                addNestedElement(element, nestedAttribute1);
            }
        } else {
            if (nestedAttribute.getValue().length() > 0) {
                element.setText(nestedAttribute.getValue());
            }
        }
    }

    public static String fixName(String attributeName) {
        return attributeName.replaceAll("[!;.]+", "");
    }
}
