package model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import model.macro.IMacro;

import static model.helpers.PixelHelper.reverse;

/**
 * This class represents an implementation of the IMEModel interface which provides the
 * operations/manipulations that can be performed on a given image.
 */
public class IMEModelImpl implements IMEModel {

  /*
  Each Model object represents an Image loaded.
  An image is represented as a 2D array of IPixels where in each IPixel represent a particular
  type of Pixel implementation (RGB, PNG, etc..).
  Irrespective of the type of file imported the image will always be represented as an 2D array of
  IPixel objects.
   */
  private final IPixel[][] imageData;
  private final int height;
  private final int width;
  private final int maxValue;

  /**
   * This is a constructor used to instantiate the above class by taking in the image data, height
   * and width of the image.
   *
   * @param imageData the image data in the form of an array
   * @param height    the height of the image
   * @param width     the width of the image
   * @param maxValue  the max value of each component of a pixel
   */
  public IMEModelImpl(IPixel[][] imageData, int height, int width, int maxValue) {
    this.imageData = imageData;
    this.height = height;
    this.width = width;
    this.maxValue = maxValue;
  }

  /**
   * This is a method used to get the processed image data.
   *
   * @return the processed image data
   */
  @Override
  public IPixel[][] getImageData() {
    return imageData;
  }

  /**
   * This is a method used to get the height of the image.
   *
   * @return the height of the image
   */
  @Override
  public int getImageHeight() {
    return this.height;
  }

  @Override
  public int getMaxValue() {
    return this.maxValue;
  }

  /**
   * This is a method used to get the width of the image.
   *
   * @return the width of the image
   */
  @Override
  public int getImageWidth() {
    return this.width;
  }

  /**
   * This is a method used to convert the given image into a greyscale image of the given
   * component.
   *
   * @param func function which takes in the color component
   * @return the greyscale image data
   */
  @Override
  public IMEModel greyScaleImage(Function<IPixel, Integer> func) {
    IPixel[][] newImageData = new Pixel[height][width];
    for (int i = 0; i < height; i++) {
      for (int j = 0; j < width; j++) {
        int value = func.apply(imageData[i][j]);
        newImageData[i][j] = new Pixel(value, value, value);
      }
    }
    return new IMEModelImpl(newImageData, height, width, this.maxValue);
  }

  /**
   * This is a method used to flip the given image horizontally.
   *
   * @return the image data of the flipped image
   */
  @Override
  public IMEModel horizontalFlipImage() {
    IPixel[][] newImageData = new Pixel[height][width];
    for (int i = 0; i < imageData.length; i++) {
      newImageData[i] = reverse(imageData[i]);
    }

    return new IMEModelImpl(newImageData, height, width, maxValue);
  }

  /**
   * This is a method used to flip the given image vertically.
   *
   * @return the image data of the flipped image
   */
  @Override
  public IMEModel verticalFlipImage() {
    IPixel[][] newImageData = new Pixel[height][width];
    int n = imageData.length - 1;
    for (int i = n; i >= 0; i--) {
      newImageData[n - i] = imageData[i];
    }

    return new IMEModelImpl(newImageData, height, width, maxValue);
  }

  /**
   * This is a method used to brighten or darken the given image based on the increment or decrement
   * input value.
   *
   * @param delta the increment or decrement input value
   * @return the image data of the manipulated image
   */
  @Override
  public IMEModel alterBrightness(int delta) {

    IPixel[][] newImageData = new Pixel[height][width];

    if (delta < 0) {
      for (int i = 0; i < height; i++) {
        for (int j = 0; j < width; j++) {
          int redComponent = Math.max(this.imageData[i][j].getRedComponent() + delta, 0);
          int greenComponent = Math.max(this.imageData[i][j].getGreenComponent() + delta, 0);
          int blueComponent = Math.max(this.imageData[i][j].getBlueComponent() + delta, 0);

          newImageData[i][j] = new Pixel(redComponent, greenComponent, blueComponent);
        }
      }
    } else {
      for (int i = 0; i < height; i++) {
        for (int j = 0; j < width; j++) {
          int redComponent =
              Math.min(this.imageData[i][j].getRedComponent() + delta, this.maxValue);
          int greenComponent =
              Math.min(this.imageData[i][j].getGreenComponent() + delta, this.maxValue);
          int blueComponent =
              Math.min(this.imageData[i][j].getBlueComponent() + delta, this.maxValue);

          newImageData[i][j] = new Pixel(redComponent, greenComponent, blueComponent);
        }
      }
    }
    return new IMEModelImpl(newImageData, height, width, maxValue);
  }

  /**
   * This is a method used to split the given image into three greyscale images containing its red,
   * green and blue components respectively.
   *
   * @return three greyscale images containing the red, green and blue components
   */
  @Override
  public List<IMEModel> rgbSplit() {
    IMEModel redImage = this.greyScaleImage(IPixel::getRedComponent);
    IMEModel greenImage = this.greyScaleImage(IPixel::getGreenComponent);
    IMEModel blueImage = this.greyScaleImage(IPixel::getBlueComponent);
    return new ArrayList<>(Arrays.asList(redImage, greenImage, blueImage));
  }

  /**
   * This is a method used to combine the three greyscale images into a single image that gets its
   * red, green and blue components from the three images respectively.
   *
   * @param greenScaleImage the image from which the green component needs to be taken
   * @param blueScaleImage  the image from which the blue component needs to be taken
   * @return combined greyscale image with all the three components
   */
  @Override
  public IMEModel combineRGBImage(IMEModel greenScaleImage,
      IMEModel blueScaleImage) {

    if (!(this.height == greenScaleImage.getImageHeight()
        && this.height == blueScaleImage.getImageHeight())
        || !(this.width == greenScaleImage.getImageWidth()
        && this.width == blueScaleImage.getImageWidth())) {
      throw new IllegalStateException("The greyscale images are of different sizes!");
    }
    IPixel[][] newImageData = new Pixel[height][width];
    for (int i = 0; i < height; i++) {
      for (int j = 0; j < width; j++) {
        newImageData[i][j] = new Pixel(
            this.getImageData()[i][j].getRedComponent(),
            greenScaleImage.getImageData()[i][j].getGreenComponent(),
            blueScaleImage.getImageData()[i][j].getBlueComponent());
      }
    }
    return new IMEModelImpl(newImageData, height, width, maxValue);
  }

  /**
   * Function to execute a Macro manipulation on the image.
   *
   * @param macro the macro manipulation object
   * @return the manipulated image
   */
  public IMEModel executeMacro(IMacro macro) {
    return macro.execute(this);
  }
}