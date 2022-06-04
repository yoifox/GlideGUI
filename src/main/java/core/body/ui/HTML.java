package core.body.ui;

import core.loader.ObjectLoader;
import core.util.Util;
import cz.vutbr.web.css.MediaSpec;
import cz.vutbr.web.css.MediaSpecAll;
import org.fit.cssbox.awt.GraphicsEngine;
import org.fit.cssbox.css.CSSNorm;
import org.fit.cssbox.css.DOMAnalyzer;
import org.fit.cssbox.io.*;
import org.fit.cssbox.layout.Dimension;
import org.lwjgl.opengl.GL11;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

public class HTML extends Component
{
    private String url;
    public HTML(float width, float height) {
        super(width, height);
    }

    public HTML() {

    }

    private Document w3cDoc;
    private DOMSource docSource;
    public void open(String url)
    {
        this.url = url;
        docSource = null;
        try {
            docSource = new DefaultDOMSource(new DefaultDocumentSource(url));
            w3cDoc = docSource.parse();
        } catch (IOException | SAXException e) {
            e.printStackTrace();
        }

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                BufferedImage img = null;
                try {
                    img = renderDocument(w3cDoc);
                    bc.texture = scene.objectLoader.loadTexture(img);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
            }
        };
        if(isCreated())
            runnable.run();
        else
            doOnCreate(runnable);
    }

    public void open(InputStream is)
    {
        docSource = null;
        try {
            docSource = new DefaultDOMSource(new StreamDocumentSource(is, new URL("http://nothing.com/"), "html/css"));
            w3cDoc = docSource.parse();
        } catch (IOException | SAXException e) {
            e.printStackTrace();
        }

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                BufferedImage img = null;
                try {
                    img = renderDocument(w3cDoc);
                    bc.texture = scene.objectLoader.loadTexture(img);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
            }
        };
        if(isCreated())
            runnable.run();
        else
            doOnCreate(runnable);
    }

    private BufferedImage renderDocument(org.w3c.dom.Document doc) throws MalformedURLException
    {
        MediaSpec media = new MediaSpecAll();
        media.setDimensions(getWidth(), getHeight());
        media.setDeviceDimensions(getWidth(), getHeight());

        DOMAnalyzer da = new DOMAnalyzer(doc, docSource.getDocumentSource().getURL());
        da.setMediaSpec(media);
        da.attributesToStyles();
        da.addStyleSheet(null, CSSNorm.stdStyleSheet(), DOMAnalyzer.Origin.AGENT);
        da.addStyleSheet(null, CSSNorm.userStyleSheet(), DOMAnalyzer.Origin.AGENT);
        da.addStyleSheet(null, CSSNorm.formsStyleSheet(), DOMAnalyzer.Origin.AGENT);
        da.getStyleSheets();

        GraphicsEngine engine = new GraphicsEngine(da.getRoot(), da, docSource.getDocumentSource().getURL())
        {
            @Override
            protected void setupGraphics(Graphics2D g)
            {
                g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
                g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
                g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
            }
        };
        engine.setUseKerning(false);
        engine.setAutoMediaUpdate(false);
        engine.getConfig().setClipViewport(true);
        engine.getConfig().setLoadImages(true);
        engine.getConfig().setLoadBackgroundImages(true);

        engine.createLayout(new Dimension(getWidth(), getHeight()));
        return engine.getImage();
    }

    public String getUrl() {
        return url;
    }
}
