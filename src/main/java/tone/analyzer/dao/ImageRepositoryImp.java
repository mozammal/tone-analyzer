package tone.analyzer.dao;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import javax.annotation.PostConstruct;

import javax.imageio.ImageIO;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import tone.analyzer.domain.model.Document;
import tone.analyzer.service.amazon.AmazonFileUploaderClient;

/** Created by Dell on 1/17/2018. */
@Component
public class ImageRepositoryImp implements ImageRepository {

  private static final Logger LOG = Logger.getLogger(ImageRepositoryImp.class);

  @Autowired private AmazonFileUploaderClient amazonFileUploaderClient;

  @PostConstruct
  public void init() {}

  @Override
  public void add(Document document, boolean isBAse64Image) throws IOException {

    File outputImageFile = null;
    try {
      if (org.springframework.util.StringUtils.isEmpty(document.getName())) {
        LOG.info("file name cant be null");
        throw new IOException();
      }

      if (!isBAse64Image) {
        // save profile photo using amazon s3
      } else {
        BufferedImage img = ImageIO.read(new ByteArrayInputStream(document.getContent()));
        outputImageFile = new File(document.getName());
        ImageIO.write(img, "png", outputImageFile);
        amazonFileUploaderClient.uploadFileTos3bucket(document.getName(), outputImageFile);
        outputImageFile.delete();
      }
    } catch (Exception exception) {
      if (outputImageFile != null) outputImageFile.delete();
    }
  }
}
