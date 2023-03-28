package controller.commands;

import java.util.Map;
import model.IMEModel;

/**
 * This class is used to perform the operation of sharpening the image based on the kernel filter
 * value.
 */
public class Sharpen extends AbstractIMECommand {

  private final String sourceImageName;
  private final String destinationImageName;

  /**
   * This is a constructor used to instantiate the above class.
   *
   * @param sourceImageName      the name of the source image on which the operation is to be
   *                             performed
   * @param destinationImageName the name of the resultant image obtained after manipulation
   */
  public Sharpen(String sourceImageName, String destinationImageName) {
    this.sourceImageName = sourceImageName;
    this.destinationImageName = destinationImageName;
  }

  /**
   * This method sharpens the image based on the kernel filter value and puts the resultant image
   * data object to the map.
   *
   * @param objectMap the map to store the manipulated image and its image data object respectively
   */
  @Override
  public void execute(Map<String, IMEModel> objectMap) {
    IMEModel callingObject = getModelObject(objectMap, sourceImageName);
    IMEModel sharpenedImage = callingObject.sharpen();
    objectMap.put(destinationImageName, sharpenedImage);
  }
}