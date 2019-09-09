/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.knoesis.url.extractions;

import de.l3s.boilerpipe.BoilerpipeExtractor;
import de.l3s.boilerpipe.BoilerpipeProcessingException;
import de.l3s.boilerpipe.document.Image;
import de.l3s.boilerpipe.extractors.CommonExtractors;
import de.l3s.boilerpipe.sax.ImageExtractor;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.xml.sax.SAXException;

/**
 *
 * @author reeder
 */
public class ImageURLExtractor implements Extractor<String> {

    @Override
    public String extract(Object text) {
        URL url = (URL) text;

//        try {
//            url = new URL("http://www.spiegel.de/wissenschaft/natur/0,1518,789176,00.html");
//        } catch (MalformedURLException ex) {
//            Logger.getLogger(ImageURLExtractor.class.getName()).log(Level.SEVERE, null, ex);
//        }

        // choose from a set of useful BoilerpipeExtractors...
        final BoilerpipeExtractor extractor = CommonExtractors.ARTICLE_EXTRACTOR;
//              final BoilerpipeExtractor extractor = CommonExtractors.DEFAULT_EXTRACTOR;
//              final BoilerpipeExtractor extractor = CommonExtractors.CANOLA_EXTRACTOR;
//              final BoilerpipeExtractor extractor = CommonExtractors.LARGEST_CONTENT_EXTRACTOR;

        final ImageExtractor ie = ImageExtractor.INSTANCE;

        List<Image> imgUrls;
        try {
            imgUrls = ie.process(url, extractor);
            Collections.sort(imgUrls);
            return imgUrls.get(0).getSrc();
            
        } catch (IOException ex) {
            Logger.getLogger(ImageURLExtractor.class.getName()).log(Level.SEVERE, null, ex);
        } catch (BoilerpipeProcessingException ex) {
            Logger.getLogger(ImageURLExtractor.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SAXException ex) {
            Logger.getLogger(ImageURLExtractor.class.getName()).log(Level.SEVERE, null, ex);
        }

        // automatically sorts them by decreasing area, i.e. most probable true positives come first
       


        return null;


    }

    @Override
    public void process(URLModel urlModel) {
        try {
            urlModel.setImageURL(new URL(this.extract(urlModel.getOriginalURL())));
        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
