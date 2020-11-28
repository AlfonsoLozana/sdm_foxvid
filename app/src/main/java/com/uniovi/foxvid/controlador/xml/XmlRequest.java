package com.uniovi.foxvid.controlador.xml;

import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;


public class XmlRequest<T> extends Request<T> {

    private static final String TAG = XmlRequest.class.getSimpleName();

    // Atributos
    private final Class<T> clazz;
    private final Map<String, String> headers;
    private final Response.Listener<T> listener;
    private final Serializer serializer = new Persister();

    /**
     * Se predefine para el uso de peticiones GET
     */
    public XmlRequest(String url, Class<T> clazz, Map<String, String> headers,
                      Response.Listener<T> listener, Response.ErrorListener errorListener) {
        super(Method.GET, url, errorListener);
        this.clazz = clazz;
        this.headers = headers;
        this.listener = listener;
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        return headers != null ? headers : super.getHeaders();
    }

    @Override
    protected void deliverResponse(T response) {
        listener.onResponse(response);
    }

    @Override
    protected Response<T> parseNetworkResponse(NetworkResponse response) {
        try {

            // Convirtiendo el flujo en cadena con formato UTF-8
            String xml = new String(response.data, "UTF-8");

            // Depurando...
            Log.d(TAG, xml);

            List<Item> result = parseXML(xml);
           return Response.success(
                    serializer.read(clazz, xml),
                    HttpHeaderParser.parseCacheHeaders(response));




        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        } catch (Exception e) {
            e.printStackTrace();
            return Response.error(new ParseError(e));
        }
    }




    private List<Item> parseXML(String xml){
        NodeList nl = null;
        List<Item> itemsList = new ArrayList<Item>();
        try {
            DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(new InputSource(new StringReader(xml)));
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            doc.getDocumentElement().normalize();
            NodeList nList = doc.getElementsByTagName("item");
            System.out.println("----------------------------");

            for (int temp = 0; temp < nList.getLength(); temp++) {
                Node nNode = nList.item(temp);
                if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element eElement = (Element) nNode;
                    String title = eElement.getElementsByTagName("title").item(0).getTextContent();
                    String URL = eElement.getElementsByTagName("link").item(0).getTextContent();
                    String content[] = eElement.getElementsByTagName("description").item(0).getChildNodes().item(0).getTextContent().split("src=\"")[1].split("\">");
                    String img = content[0];
                    String description = content[1];
                    itemsList.add(new Item(title,description,URL, new Content(img)));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return itemsList;
    }

}