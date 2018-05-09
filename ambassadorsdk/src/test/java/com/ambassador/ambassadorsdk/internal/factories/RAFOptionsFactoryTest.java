package com.ambassador.ambassadorsdk.internal.factories;

import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.Log;

import com.ambassador.ambassadorsdk.RAFOptions;
import com.ambassador.ambassadorsdk.internal.AmbSingleton;
import com.ambassador.ambassadorsdk.internal.utils.res.ColorResource;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Ignore;
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

@Ignore
@RunWith(PowerMockRunner.class)
@PrepareForTest({
        ResourceFactory.class,
        DocumentBuilderFactory.class,
        Color.class,
        AmbSingleton.class,
        Typeface.class,
        Log.class,
        ColorResource.class
})
public class RAFOptionsFactoryTest {

    private Context context;
    private InputStream inputStream;
    private NodeList nodeList;

    private Typeface font;

    @Before
    public void setUp() throws Exception {
        PowerMockito.mockStatic(
                ResourceFactory.class,
                DocumentBuilderFactory.class,
                Color.class,
                AmbSingleton.class,
                Typeface.class,
                Log.class
        );

        context = Mockito.mock(Context.class);
        inputStream = Mockito.mock(InputStream.class);

        ColorResource colorResource = PowerMockito.mock(ColorResource.class);
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

        Resources resources = Mockito.mock(Resources.class);
        Mockito.when(context.getResources()).thenReturn(resources);
        Mockito.when(resources.getIdentifier(Mockito.anyString(), Mockito.anyString(), Mockito.anyString())).thenReturn(12345);
        Mockito.when(resources.getColor(Mockito.eq(12345))).thenReturn(-1);

        PowerMockito.doReturn(-2).when(Color.class, "parseColor", Mockito.anyString());

        Mockito.when(AmbSingleton.getInstance().getContext()).thenReturn(context);
        AssetManager assets = Mockito.mock(AssetManager.class);
        Mockito.when(context.getAssets()).thenReturn(assets);
        font = Mockito.mock(Typeface.class);
        Mockito.when(Typeface.createFromAsset(Mockito.eq(assets), Mockito.anyString())).thenReturn(font);
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

    private void mockOption(String type, String key, String value) {
        Node node = Mockito.mock(Node.class);
        Mockito.when(nodeList.item(Mockito.eq(0))).thenReturn(node);

        NamedNodeMap attributes = Mockito.mock(NamedNodeMap.class);
        Mockito.when(node.getAttributes()).thenReturn(attributes);

        Mockito.when(node.getNodeName()).thenReturn(type);

        Node valueNode = Mockito.mock(Node.class);
        Mockito.when(attributes.getNamedItem(Mockito.eq("name"))).thenReturn(valueNode);

        Mockito.when(valueNode.getNodeValue()).thenReturn(key);

        Node firstChild = Mockito.mock(Node.class);
        Mockito.when(node.getFirstChild()).thenReturn(firstChild);
        Mockito.when(firstChild.getNodeValue()).thenReturn(value);

    }

    @Test
    public void decodeResourcesDefaultShareMessageTest() throws Exception {
        // ARRANGE
        String type = "string";
        String key = "rafdefaultsharemessage";
        String value = "default";
        mockOption(type, key, value);

        // ACT
        RAFOptions rafOptions = RAFOptionsFactory.decodeResources(inputStream, context);

        // ASSERT
        Assert.assertEquals(value, rafOptions.getDefaultShareMessage());
    }


    @Test
    public void decodeResourcesTitleTextTest() throws Exception {
        // ARRANGE
        String type = "string";
        String key = "raftitletext";
        String value = "title";
        mockOption(type, key, value);

        // ACT
        RAFOptions rafOptions = RAFOptionsFactory.decodeResources(inputStream, context);

        // ASSERT
        Assert.assertEquals(value, rafOptions.getTitleText());
    }


    @Test
    public void decodeResourcesDescriptionTextTest() throws Exception {
        // ARRANGE
        String type = "string";
        String key = "rafdescriptiontext";
        String value = "desc";
        mockOption(type, key, value);

        // ACT
        RAFOptions rafOptions = RAFOptionsFactory.decodeResources(inputStream, context);

        // ASSERT
        Assert.assertEquals(value, rafOptions.getDescriptionText());
    }


    @Test
    public void decodeResourcesToolbarTitleTest() throws Exception {
        // ARRANGE
        String type = "string";
        String key = "raftoolbartitle";
        String value = "toolbar";
        mockOption(type, key, value);

        // ACT
        RAFOptions rafOptions = RAFOptionsFactory.decodeResources(inputStream, context);

        // ASSERT
        Assert.assertEquals(value, rafOptions.getToolbarTitle());
    }

    @Test
    public void decodeResourcesLogoPositionTest() throws Exception {
        // ARRANGE
        String type = "string";
        String key = "raflogoposition";
        String value = "logopos";
        mockOption(type, key, value);

        // ACT
        RAFOptions rafOptions = RAFOptionsFactory.decodeResources(inputStream, context);

        // ASSERT
        Assert.assertEquals(value, rafOptions.getLogoPosition());
    }

    @Test
    public void decodeResourcesLogoTest() throws Exception {
        // ARRANGE
        String type = "string";
        String key = "raflogo";
        String value = "logo";
        mockOption(type, key, value);

        // ACT
        RAFOptions rafOptions = RAFOptionsFactory.decodeResources(inputStream, context);

        // ASSERT
        Assert.assertEquals(value, rafOptions.getLogo());
    }

    @Test
    public void decodeResourcesHomeBackgroundTest() throws Exception {
        // ARRANGE
        String type = "color";
        String key = "homebackground";
        String value = "#ffffff";
        mockOption(type, key, value);

        // ACT
        RAFOptions rafOptions = RAFOptionsFactory.decodeResources(inputStream, context);

        // ASSERT
        Assert.assertEquals(-2, rafOptions.getHomeBackgroundColor());
    }

    @Test
    public void decodeResourcesHomeWelcomeTitleColorTest() throws Exception {
        // ARRANGE
        String type = "color";
        String key = "homewelcometitle";
        String value = "@color/cats";
        mockOption(type, key, value);

        // ACT
        RAFOptions rafOptions = RAFOptionsFactory.decodeResources(inputStream, context);

        // ASSERT
        Assert.assertEquals(-1, rafOptions.getHomeWelcomeTitleColor());
    }

    @Test
    public void decodeResourcesHomeWelcomeTitleDimenTest() throws Exception {
        // ARRANGE
        String type = "dimen";
        String key = "homewelcometitle";
        String value = "12dp";
        mockOption(type, key, value);

        // ACT
        RAFOptions rafOptions = RAFOptionsFactory.decodeResources(inputStream, context);

        // ASSERT
        Assert.assertEquals(12f, rafOptions.getHomeWelcomeTitleSize());
    }

    @Test
    public void decodeResourcesHomeWelcomeTitleStringTest() throws Exception {
        // ARRANGE
        String type = "string";
        String key = "homewelcometitle";
        String value = "fonts/font1.ttf";
        mockOption(type, key, value);

        // ACT
        RAFOptions rafOptions = RAFOptionsFactory.decodeResources(inputStream, context);

        // ASSERT
        Assert.assertEquals(font, rafOptions.getHomeWelcomeTitleFont());
    }

    @Test
    public void decodeResourcesHomeWelcomeDescriptionColorTest() throws Exception {
        // ARRANGE
        String type = "color";
        String key = "homewelcomedesc";
        String value = "@color/cats";
        mockOption(type, key, value);

        // ACT
        RAFOptions rafOptions = RAFOptionsFactory.decodeResources(inputStream, context);

        // ASSERT
        Assert.assertEquals(-1, rafOptions.getHomeWelcomeDescriptionColor());
    }

    @Test
    public void decodeResourcesHomeWelcomeDescriptionDimenTest() throws Exception {
        // ARRANGE
        String type = "dimen";
        String key = "homewelcomedesc";
        String value = "12dp";
        mockOption(type, key, value);

        // ACT
        RAFOptions rafOptions = RAFOptionsFactory.decodeResources(inputStream, context);

        // ASSERT
        Assert.assertEquals(12f, rafOptions.getHomeWelcomeDescriptionSize());
    }

    @Test
    public void decodeResourcesHomeWelcomeDescriptionStringTest() throws Exception {
        // ARRANGE
        String type = "string";
        String key = "homewelcomedesc";
        String value = "fonts/font1.ttf";
        mockOption(type, key, value);

        // ACT
        RAFOptions rafOptions = RAFOptionsFactory.decodeResources(inputStream, context);

        // ASSERT
        Assert.assertEquals(font, rafOptions.getHomeWelcomeDescriptionFont());
    }

    @Test
    public void decodeResourcesHomeToolbarTest() throws Exception {
        // ARRANGE
        String type = "color";
        String key = "hometoolbar";
        String value = "@android:color/dogs";
        mockOption(type, key, value);

        // ACT
        RAFOptions rafOptions = RAFOptionsFactory.decodeResources(inputStream, context);

        // ASSERT
        Assert.assertEquals(-1, rafOptions.getHomeToolbarColor());
    }

    @Test
    public void decodeResourcesHomeToolbarTextColorTest() throws Exception {
        // ARRANGE
        String type = "color";
        String key = "hometoolbartext";
        String value = "#ff00ff";
        mockOption(type, key, value);

        // ACT
        RAFOptions rafOptions = RAFOptionsFactory.decodeResources(inputStream, context);

        // ASSERT
        Assert.assertEquals(-2, rafOptions.getHomeToolbarTextColor());
    }

    @Test
    public void decodeResourcesHomeToolbarTextStringTest() throws Exception {
        // ARRANGE
        String type = "string";
        String key = "hometoolbartext";
        String value = "font/font.ttf";
        mockOption(type, key, value);

        // ACT
        RAFOptions rafOptions = RAFOptionsFactory.decodeResources(inputStream, context);

        // ASSERT
        Assert.assertEquals(font, rafOptions.getHomeToolbarTextFont());
    }

    @Test
    public void decodeResourcesHomeToolbarArrowTest() throws Exception {
        // ARRANGE
        String type = "color";
        String key = "hometoolbararrow";
        String value = "#ffaaaa";
        mockOption(type, key, value);

        // ACT
        RAFOptions rafOptions = RAFOptionsFactory.decodeResources(inputStream, context);

        // ASSERT
        Assert.assertEquals(-2, rafOptions.getHomeToolbarArrowColor());
    }

    @Test
    public void decodeResourcesHomeShareTextBarColorTest() throws Exception {
        // ARRANGE
        String type = "color";
        String key = "homesharetextbar";
        String value = "@color/c";
        mockOption(type, key, value);

        // ACT
        RAFOptions rafOptions = RAFOptionsFactory.decodeResources(inputStream, context);

        // ASSERT
        Assert.assertEquals(-1, rafOptions.getHomeShareTextBar());
    }

    @Test
    public void decodeResourcesHomeShareTextColorTest() throws Exception {
        // ARRANGE
        String type = "color";
        String key = "homesharetext";
        String value = "@android:color/tst";
        mockOption(type, key, value);

        // ACT
        RAFOptions rafOptions = RAFOptionsFactory.decodeResources(inputStream, context);

        // ASSERT
        Assert.assertEquals(-1, rafOptions.getHomeShareTextColor());
    }

    @Test
    public void decodeResourcesHomeShareTextDimenTest() throws Exception {
        // ARRANGE
        String type = "dimen";
        String key = "homesharetext";
        String value = "-12sp";
        mockOption(type, key, value);

        // ACT
        RAFOptions rafOptions = RAFOptionsFactory.decodeResources(inputStream, context);

        // ASSERT
        Assert.assertEquals(-12f, rafOptions.getHomeShareTextSize());
    }

    @Test
    public void decodeResourcesHomeShareTextStringTest() throws Exception {
        // ARRANGE
        String type = "string";
        String key = "homesharetext";
        String value = "font/dogs.ttf";
        mockOption(type, key, value);

        // ACT
        RAFOptions rafOptions = RAFOptionsFactory.decodeResources(inputStream, context);

        // ASSERT
        Assert.assertEquals(font, rafOptions.getHomeShareTextFont());
    }

    @Test
    public void decodeResourcesSocialGridTextTest() throws Exception {
        // ARRANGE
        String type = "string";
        String key = "socialgridtext";
        String value = "font";
        mockOption(type, key, value);

        // ACT
        RAFOptions rafOptions = RAFOptionsFactory.decodeResources(inputStream, context);

        // ASSERT
        Assert.assertEquals(font, rafOptions.getSocialGridTextFont());
    }

    @Test
    public void decodeResourcesContactsListViewBackgroundTest() throws Exception {
        // ARRANGE
        String type = "color";
        String key = "contactslistviewbackground";
        String value = "#ff0000";
        mockOption(type, key, value);

        // ACT
        RAFOptions rafOptions = RAFOptionsFactory.decodeResources(inputStream, context);

        // ASSERT
        Assert.assertEquals(-2, rafOptions.getContactsListViewBackgroundColor());
    }

    @Test
    public void decodeResourcesContactsListNameDimenTest() throws Exception {
        // ARRANGE
        String type = "dimen";
        String key = "contactslistname";
        String value = "-1s1sas";
        mockOption(type, key, value);

        // ACT
        RAFOptions rafOptions = RAFOptionsFactory.decodeResources(inputStream, context);

        // ASSERT
        Assert.assertEquals(-11f, rafOptions.getContactsListNameSize());
    }

    @Test
    public void decodeResourcesContactsListNameStringTest() throws Exception {
        // ARRANGE
        String type = "string";
        String key = "contactslistname";
        String value = "ff#d";
        mockOption(type, key, value);

        // ACT
        RAFOptions rafOptions = RAFOptionsFactory.decodeResources(inputStream, context);

        // ASSERT
        Assert.assertEquals(font, rafOptions.getContactsListNameFont());
    }

    @Test
    public void decodeResourcesContactsListValueDimenTest() throws Exception {
        // ARRANGE
        String type = "dimen";
        String key = "contactslistvalue";
        String value = "-1s1sas";
        mockOption(type, key, value);

        // ACT
        RAFOptions rafOptions = RAFOptionsFactory.decodeResources(inputStream, context);

        // ASSERT
        Assert.assertEquals(-11f, rafOptions.getContactsListValueSize());
    }

    @Test
    public void decodeResourcesContactsListValueStringTest() throws Exception {
        // ARRANGE
        String type = "string";
        String key = "contactslistvalue";
        String value = "ff#d";
        mockOption(type, key, value);

        // ACT
        RAFOptions rafOptions = RAFOptionsFactory.decodeResources(inputStream, context);

        // ASSERT
        Assert.assertEquals(font, rafOptions.getContactsListValueFont());
    }

    @Test
    public void decodeResourcesContactsSendBackgroundTest() throws Exception {
        // ARRANGE
        String type = "color";
        String key = "contactssendbackground";
        String value = "@color/xx";
        mockOption(type, key, value);

        // ACT
        RAFOptions rafOptions = RAFOptionsFactory.decodeResources(inputStream, context);

        // ASSERT
        Assert.assertEquals(-1, rafOptions.getContactsSendBackground());
    }

    @Test
    public void decodeResourcesContactSendMessageTextTest() throws Exception {
        // ARRANGE
        String type = "string";
        String key = "contactsendmessagetext";
        String value = "font";
        mockOption(type, key, value);

        // ACT
        RAFOptions rafOptions = RAFOptionsFactory.decodeResources(inputStream, context);

        // ASSERT
        Assert.assertEquals(font, rafOptions.getContactSendMessageTextFont());
    }

    @Test
    public void decodeResourcesContactsToolbarTest() throws Exception {
        // ARRANGE
        String type = "color";
        String key = "contactstoolbar";
        String value = "#ff000f";
        mockOption(type, key, value);

        // ACT
        RAFOptions rafOptions = RAFOptionsFactory.decodeResources(inputStream, context);

        // ASSERT
        Assert.assertEquals(-2, rafOptions.getContactsToolbarColor());
    }

    @Test
    public void decodeResourcesContactsToolbarTextTest() throws Exception {
        // ARRANGE
        String type = "color";
        String key = "contactstoolbartext";
        String value = "#123456";
        mockOption(type, key, value);

        // ACT
        RAFOptions rafOptions = RAFOptionsFactory.decodeResources(inputStream, context);

        // ASSERT
        Assert.assertEquals(-2, rafOptions.getContactsToolbarTextColor());
    }

    @Test
    public void decodeResourcesContactsToolbarArrowTest() throws Exception {
        // ARRANGE
        String type = "color";
        String key = "contactstoolbararrow";
        String value = "@color/5";
        mockOption(type, key, value);

        // ACT
        RAFOptions rafOptions = RAFOptionsFactory.decodeResources(inputStream, context);

        // ASSERT
        Assert.assertEquals(-1, rafOptions.getContactsToolbarArrowColor());
    }

    @Test
    public void decodeResourcesContactsSendButtonTest() throws Exception {
        // ARRANGE
        String type = "color";
        String key = "contactssendbutton";
        String value = "@android:color/5";
        mockOption(type, key, value);

        // ACT
        RAFOptions rafOptions = RAFOptionsFactory.decodeResources(inputStream, context);

        // ASSERT
        Assert.assertEquals(-1, rafOptions.getContactsSendButtonColor());
    }

    @Test
    public void decodeResourcesContactsSendButtonTextTest() throws Exception {
        // ARRANGE
        String type = "color";
        String key = "contactssendbuttontext";
        String value = "#ffffff";
        mockOption(type, key, value);

        // ACT
        RAFOptions rafOptions = RAFOptionsFactory.decodeResources(inputStream, context);

        // ASSERT
        Assert.assertEquals(-2, rafOptions.getContactsSendButtonTextColor());
    }

    @Test
    public void decodeResourcesContactsDoneButtonTextTest() throws Exception {
        // ARRANGE
        String type = "color";
        String key = "contactsdonebuttontext";
        String value = "@color/xyz";
        mockOption(type, key, value);

        // ACT
        RAFOptions rafOptions = RAFOptionsFactory.decodeResources(inputStream, context);

        // ASSERT
        Assert.assertEquals(-1, rafOptions.getContactsDoneButtonTextColor());
    }

    @Test
    public void decodeResourcesContactsSearchBarTest() throws Exception {
        // ARRANGE
        String type = "color";
        String key = "contactssearchbar";
        String value = "#ffffff";
        mockOption(type, key, value);

        // ACT
        RAFOptions rafOptions = RAFOptionsFactory.decodeResources(inputStream, context);

        // ASSERT
        Assert.assertEquals(-2, rafOptions.getContactsSearchBarColor());
    }

    @Test
    public void decodeResourcesContactsSearchIconTest() throws Exception {
        // ARRANGE
        String type = "color";
        String key = "contactssearchicon";
        String value = "#ffffff";
        mockOption(type, key, value);

        // ACT
        RAFOptions rafOptions = RAFOptionsFactory.decodeResources(inputStream, context);

        // ASSERT
        Assert.assertEquals(-2, rafOptions.getContactsSearchIconColor());
    }

    @Test
    public void decodeResourcesContactNoPhotoAvailableBackgroundTest() throws Exception {
        // ARRANGE
        String type = "color";
        String key = "contactnophotoavailablebackground";
        String value = "#ffffff";
        mockOption(type, key, value);

        // ACT
        RAFOptions rafOptions = RAFOptionsFactory.decodeResources(inputStream, context);

        // ASSERT
        Assert.assertEquals(-2, rafOptions.getContactNoPhotoAvailableBackgroundColor());
    }

    @Test
    public void decodeResourcesChannelsTest() throws Exception {
        // ARRANGE
        String type = "array";
        String key = "channels";
        String value = "Facebook&Twitter&LinkedIn";

        Node node = Mockito.mock(Node.class);
        Mockito.when(nodeList.item(Mockito.eq(0))).thenReturn(node);

        NamedNodeMap attributes = Mockito.mock(NamedNodeMap.class);
        Mockito.when(node.getAttributes()).thenReturn(attributes);

        Mockito.when(node.getNodeName()).thenReturn(type);

        Node valueNode = Mockito.mock(Node.class);
        Mockito.when(attributes.getNamedItem(Mockito.eq("name"))).thenReturn(valueNode);
        Mockito.when(valueNode.getNodeValue()).thenReturn(key);


        NodeList nodes = Mockito.mock(NodeList.class);
        Mockito.when(node.getChildNodes()).thenReturn(nodes);
        Mockito.when(nodes.getLength()).thenReturn(3);

        Node node1 = Mockito.mock(Node.class);
        Mockito.when(nodes.item(Mockito.anyInt())).thenReturn(node1);

        NamedNodeMap attrs = Mockito.mock(NamedNodeMap.class);
        Mockito.when(node1.getAttributes()).thenReturn(attrs);

        Node innerChild = Mockito.mock(Node.class);
        Mockito.when(node1.getFirstChild()).thenReturn(innerChild);
        Mockito.when(innerChild.getNodeValue())
                .thenReturn("Facebook")
                .thenReturn("Twitter")
                .thenReturn("LinkedIn");

        // ACT
        RAFOptions rafOptions = RAFOptionsFactory.decodeResources(inputStream, context);

        // ASSERT
        org.junit.Assert.assertArrayEquals(new String[]{"Facebook", "Twitter", "LinkedIn"}, rafOptions.getChannels());
    }

    @Test
    public void decodeResourcesSocialOptionCornerRadiusTest() throws Exception {
        // ARRANGE
        String type = "dimen";
        String key = "socialoptioncornerradius";
        String value = "-8px";
        mockOption(type, key, value);

        // ACT
        RAFOptions rafOptions = RAFOptionsFactory.decodeResources(inputStream, context);

        // ASSERT
        Assert.assertEquals(-8f, rafOptions.getSocialOptionCornerRadius());
    }

    @Test
    public void decodeResourcesCaseSensitivityTest() throws Exception {
        // ARRANGE
        String type = "dimen";
        String key = "SOCIALoptioncornerradius";
        String value = "-8px";
        mockOption(type, key, value);

        // ACT
        RAFOptions rafOptions = RAFOptionsFactory.decodeResources(inputStream, context);

        // ASSERT
        Assert.assertEquals(-8f, rafOptions.getSocialOptionCornerRadius());
    }

}
