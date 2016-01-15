package com.ambassador.ambassadorsdk.internal.factories;

import android.content.Context;

import com.ambassador.ambassadorsdk.RAFOptions;
import com.ambassador.ambassadorsdk.utils.ColorResource;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

@RunWith(PowerMockRunner.class)
@PrepareForTest({
        ResourceFactory.class,
        DocumentBuilderFactory.class
})
public class RAFOptionsFactoryTest {

    private RAFOptions.Builder builder;

    private Context context;
    private InputStream inputStream;
    private NodeList nodeList;

    @Before
    public void setUp() throws Exception {
        PowerMockito.mockStatic(
                ResourceFactory.class,
                DocumentBuilderFactory.class
        );

        context = Mockito.mock(Context.class);
        inputStream = Mockito.mock(InputStream.class);

        ColorResource colorResource = Mockito.mock(ColorResource.class);
        Mockito.when(ResourceFactory.getColor(Mockito.anyInt())).thenReturn(colorResource);

        DocumentBuilderFactory documentBuilderFactory = Mockito.mock(DocumentBuilderFactory.class);
        Mockito.when(DocumentBuilderFactory.newInstance()).thenReturn(documentBuilderFactory);

        DocumentBuilder documentBuilder = Mockito.mock(DocumentBuilder.class);
        Mockito.when(documentBuilderFactory.newDocumentBuilder()).thenReturn(documentBuilder);

        Document document = Mockito.mock(Document.class);
        Mockito.when(documentBuilder.parse(Mockito.eq(inputStream))).thenReturn(document);

        Element element = Mockito.mock(Element.class);
        Mockito.when(document.getDocumentElement()).thenReturn(element);
        Mockito.doNothing().when(element).normalize();

        nodeList = Mockito.mock(NodeList.class);
        Node firstChild = Mockito.mock(Node.class);
        Mockito.when(document.getFirstChild()).thenReturn(firstChild);
        Mockito.when(firstChild.getChildNodes()).thenReturn(nodeList);

        Mockito.when(nodeList.getLength()).thenReturn(1);
    }

    @Test
    public void decodeResourcesNullAttributesTest() throws Exception {
        // ARRANGE
        Node node = Mockito.mock(Node.class);
        Mockito.when(nodeList.item(Mockito.eq(0))).thenReturn(node);
        Mockito.when(node.getAttributes()).thenReturn(null);

        // ACT
        RAFOptionsFactory.decodeResources(inputStream, context);

        // ASSERT
        Mockito.verify(node).getAttributes();
        Mockito.verifyNoMoreInteractions(node);
    }

    //@Test
    public void decodeResourcesNonArrayTest() throws Exception {
        // ARRANGE
        Node node = Mockito.mock(Node.class);
        Mockito.when(nodeList.item(Mockito.eq(0))).thenReturn(node);

        NamedNodeMap attributes = Mockito.mock(NamedNodeMap.class);
        Mockito.when(node.getAttributes()).thenReturn(attributes);

        String type = "color";
        Mockito.when(node.getNodeName()).thenReturn(type);

        Node value = Mockito.mock(Node.class);
        Mockito.when(attributes.getNamedItem(Mockito.eq("name"))).thenReturn(value);

        String key = "key";
        Mockito.when(value.getNodeValue()).thenReturn(key);

        String color = "#ff0000";
        Node firstChild = Mockito.mock(Node.class);
        Mockito.when(node.getFirstChild()).thenReturn(firstChild);
        Mockito.when(firstChild.getNodeValue()).thenReturn(color);

        // ACT
        RAFOptionsFactory.decodeResources(inputStream, context);

        // ASSERT
    }

}
