package syao6_mychen5.ece420.uiuc.kapow.GPUImage;

;import syao6_mychen5.ece420.uiuc.kapow.GPUImage.GPUImageFilterGroup;

/**
 * Applies sobel edge detection on the image.
 */
public class GPUImageThresholdEdgeDetection extends GPUImageFilterGroup
{
    public GPUImageThresholdEdgeDetection() {
        super();
        addFilter(new GPUImageGrayscaleFilter());
        addFilter(new GPUImageSobelThresholdFilter());
    }

    public void setLineSize(final float size) {
        ((GPUImage3x3TextureSamplingFilter) getFilters().get(1)).setLineSize(size);
    }

    public void setThreshold(final float threshold) {
        ((GPUImageSobelThresholdFilter) getFilters().get(1)).setThreshold(threshold);
    }
}