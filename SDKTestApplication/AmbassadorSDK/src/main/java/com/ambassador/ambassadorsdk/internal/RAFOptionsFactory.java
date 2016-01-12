package com.ambassador.ambassadorsdk.internal;

import com.ambassador.ambassadorsdk.RAFOptions;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public final class RAFOptionsFactory {

    public static RAFOptions decodeResources(InputStream inputStream) throws Exception {
        RAFOptions.Builder rafBuilder = new RAFOptions.Builder();

        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dbBuilder = dbFactory.newDocumentBuilder();
        Document document = dbBuilder.parse(inputStream);

        document.getDocumentElement().normalize();
        NodeList values = document.getFirstChild().getChildNodes();

        for (int i = 0; i < values.getLength(); i++) {
            Node entry = values.item(i);
            if (entry.getAttributes() != null) {
                String type = entry.getNodeName();
                String key = entry.getAttributes().getNamedItem("name").getNodeValue();
                String value = entry.getFirstChild().getNodeValue();

                new ResourceProcessor()
                        .withType(type)
                        .withKey(key)
                        .withValue(value)
                        .applyTo(rafBuilder);
            }
        }

        return rafBuilder.build();
    }

    private static final class ResourceProcessor {

        private static final String TYPE_STRING = "string";
        private static final String TYPE_COLOR = "color";
        private static final String TYPE_DIMEN = "dimen";
        private static final String TYPE_ARRAY = "array";

        private String type;
        private String key;
        private String value;

        public ResourceProcessor() {

        }

        public ResourceProcessor withType(String type) {
            this.type = type;
            return this;
        }

        public ResourceProcessor withKey(String key) {
            this.key = key;
            return this;
        }

        public ResourceProcessor withValue(String value) {
            this.value = value;
            return this;
        }

        public void applyTo(RAFOptions.Builder builder) {
            switch (type) {
                case TYPE_STRING:
                    new RAFOptionsMethod(key).invoke(builder, value);
                    break;
                case TYPE_COLOR:
                    break;
                case TYPE_DIMEN:
                    break;
                case TYPE_ARRAY:
                    break;
            }
        }

        private static final class RAFOptionsMethod {

            private String paramString;
            private int paramInt;

            private String key;

            private RAFOptionsMethod() {}

            public RAFOptionsMethod(String key) {
                this.key = key;
            }

            public void invoke(RAFOptions.Builder builder, String value) {
                paramString = value;
                execute(builder);
            }

            public void invoke(RAFOptions.Builder builder, int value) {
                paramInt = value;
                execute(builder);
            }

            private void execute(RAFOptions.Builder builder) {
                switch (key.toLowerCase()) {
                    case "rafdefaultsharemessage":
                        builder.setDefaultShareMessage(paramString);
                        break;

                    case "raftitletext":
                        builder.setTitleText(paramString);
                        break;

                    case "rafdescriptiontext":
                        builder.setDescriptionText(paramString);
                        break;

                    case "raftoolbartitle":
                        builder.setToolbarTitle(paramString);
                        break;

                    case "raflogoposition":
                        builder.setLogoPosition(paramString);
                        break;
                }
            }

        }

    }

}
