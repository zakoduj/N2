import org.jfree.chart.annotations.AbstractXYAnnotation;
import org.jfree.chart.annotations.CategoryAnnotation;
import org.jfree.chart.annotations.XYAnnotation;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.block.RectangleConstraint;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.title.Title;
import org.jfree.chart.ui.RectangleAnchor;
import org.jfree.chart.ui.RectangleInsets;
import org.jfree.chart.ui.Size2D;
import org.jfree.chart.util.PublicCloneable;
import org.jfree.chart.util.UnitType;
import org.jfree.data.Range;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;


public class AreaTitleAnnotation extends AbstractXYAnnotation implements
        CategoryAnnotation, XYAnnotation, Cloneable, PublicCloneable,
        Serializable {

    /**
     * For serialization.
     */
    private static final long serialVersionUID = 1L;

    /**
     * The insets of the annotation.
     */
    private RectangleInsets insets;

    /**
     * The anchor of the annotation.
     */
    private RectangleAnchor anchor;

    /**
     * The position of the annotation.
     */
    private RectangleAnchor position;

    /**
     * The title.
     */
    private Title title;

    /**
     * The maximum width of the annotation, either relative or absolute.
     */
    private double maxWidth;

    /**
     * The maximum height of the annotation, either relative or absolute.
     */
    private double maxHeight;

    /**
     * The type of the maxWidth, either relative or absolute.
     */
    private UnitType maxWidthUnitType;

    /**
     * The type of the maxheight, either relative or absolute.
     */
    private UnitType maxHeightUnitType;

    /**
     * A flag indicating whether the supplied coordintaes should be used to 
     * calculate the anchor of the annotation.
     */
    private boolean useXYCoordinates;

    /**
     * The x coordinate of the annotation.
     */
    private double xCoordinate;

    /**
     * The y coordinate of the annotation.
     */
    private double yCoordinate;


    /**
     * Creates a new annotation.
     *
     * @param title  the title (<code>null</code>  permitted).
     */
    public AreaTitleAnnotation(Title title) {
        super();
        this.title = title;
        insets = RectangleInsets.ZERO_INSETS;
        anchor = RectangleAnchor.TOP_LEFT;
        position = RectangleAnchor.TOP_LEFT;
        maxWidth = 1.0;
        maxHeight = 1.0;
        maxWidthUnitType = UnitType.RELATIVE;
        maxHeightUnitType = UnitType.RELATIVE;
        xCoordinate = 0.0;
        yCoordinate = 0.0;
        useXYCoordinates = false;

    }

    /**
     * Returns the insets for the annotation.
     *
     * @return The insets.
     */
    public RectangleInsets getAnnotationInsets() {
        return this.insets;
    }

    /**
     * Sets the insets and notifies registered listeners.
     *
     * @param insets  the insets around the annotation.
     */
    public void setAnnotationInsets(RectangleInsets insets) {
        this.insets = insets;
        fireAnnotationChanged();
    }

    /**
     * Returns the anchor for the annotationn, i.e. the point with which it is
     * attached to its position..
     *
     * @return The anchor.
     */
    public RectangleAnchor getAnnotationAchor() {
        return this.anchor;
    }

    /**
     * Sets the anchor of the annotation, i.e. the point with which it is
     * attached to its position, and notifies registered listeners.
     *
     * @param anchor  The anchor.
     */
    public void setAnnotationAnchor(RectangleAnchor anchor) {
        this.anchor = anchor;
        fireAnnotationChanged();
    }

    /**
     * Returns the position for the annotationn, i.e. the place where it will
     * show up inside the data area.
     *
     * @return The position.
     */
    public RectangleAnchor getAnnotationPosition() {
        return this.position;
    }

    /**
     * Sets the position for the annotationn, i.e. the place where it will
     * show up inside the data area, and notifies registered listeners.
     *
     * @param anchor The anchor.
     */
    public void setAnnotationPosition(RectangleAnchor anchor) {
        this.position = anchor;
        fireAnnotationChanged();
    }

    /**
     * Returns the maximum width for the annotation, either absolute in pixels
     * or relative to the data area depending on the maxWidthUnitType.
     *
     * @return The width.
     */
    public double getMaxWidth() {
        return this.maxWidth;
    }

    /**
     * Sets the maximum width for the annotation, either absolute in pixels
     * or relative to the data area depending on the maxWidthUnitType, and
     * notifies registered listeners.
     *
     * @param w  The width.
     */
    public void setMaxWidth(double w) {
        this.maxWidth = w;
        fireAnnotationChanged();
    }

    /**
     * Returns the maximum height for the annotation, either absolute in pixels
     * or relative to the data area depending on the maxHeightUnitType.
     *
     * @return The height.
     */
    public double getMaxHeight() {
        return this.maxHeight;
    }

    /**
     * Sets the maximum height for the annotation, either absolute in pixels
     * or relative to the data area depending on the maxHeightUnitType, and
     * notifies registered listeners.
     *
     * @param h  The width.
     */
    public void setMaxHeight(double h) {
        this.maxHeight = h;
        fireAnnotationChanged();
    }

    /**
     * Returns a strategy how the maxWidth value will be interpreted: either 
     * absolute in pixels (UnitType.ABSOLUTE) or relative to the data area 
     * (UnitType.RELATIVE)
     *
     * @return The type.
     */
    public UnitType getMaxWidthUnitType() {
        return this.maxWidthUnitType;
    }

    /**
     * Sets a strategy how the maxWidth value will be interpreted: either 
     * absolute in pixels (UnitType.ABSOLUTE) or relative to the data area 
     * (UnitType.RELATIVE), and notifies registered listeners.
     *
     * @param ut The type.
     */
    public void setMaxWidthUnitType(UnitType ut) {
        this.maxWidthUnitType = ut;
        fireAnnotationChanged();
    }

    /**
     * Returns a strategy how the maxHeightvalue will be interpreted: either 
     * absolute in pixels (UnitType.ABSOLUTE) or relative to the data area 
     * (UnitType.RELATIVE)
     *
     * @return The type.
     */
    public UnitType getMaxHeightUnitType() {
        return this.maxHeightUnitType;
    }

    /**
     * Sets a strategy how the maxWidth value will be interpreted: either 
     * absolute in pixels (UnitType.ABSOLUTE) or relative to the data area 
     * (UnitType.RELATIVE), and notifies registered listeners.
     *
     * @param ut The type.
     */
    public void setMaxHeightUnitType(UnitType ut) {
        this.maxHeightUnitType = ut;
        fireAnnotationChanged();
    }

    /**
     * Returns the x coordinate for the anchor of the annotation, starting from
     * the top left of the data area after application of the rectangle 
     * constraint.
     *
     * @return The x coordinate.
     */
    public double getXCoordinate() {
        return this.xCoordinate;
    }

    /**
     * Sets the x coordinate for the anchor of the annotation, starting from the
     * top left of the data area after application of the rectangle constraint.
     * This value will only be used if the flag useXYCoordinates is set to true.
     * Values > 1.0 will be interpreted as absolute values, values <= 1.0 will
     * be interpreted as values relative to the width of the data area.
     * Registered listeners will be notifed about the change.
     *
     * @param x  The x  coordinate.
     */
    public void setXCoordinate(double x) {
        this.xCoordinate = x;
        fireAnnotationChanged();
    }

    /**
     * Returns the y coordinate for the anchor of the annotation, starting from
     * the top left of the data area after application of the rectangle 
     * constraint.
     *
     * @return The x coordinate.
     */
    public double getYCoordinate() {
        return this.yCoordinate;
    }

    /**
     * Sets the y coordinate for the anchor of the annotation, starting from the
     * top left of the data area after application of the rectangle constraint.
     * This value will only be used if the flag useXYCoordinates is set to true.
     * Values > 1.0 will be interpreted as absolute values, values <= 1.0 will
     * be interpreted as values relative to the height of the data area.
     * Registered listeners will be notifed about the change.
     *
     * @param y  The y coordinate.
     */
    public void setYCoordinate(double y) {
        this.yCoordinate = y;
        fireAnnotationChanged();
    }

    /**
     * Returns a flag that indicates wether the x and y coordinate will be used
     * to calculate the top left point of the annotation.
     *
     * @return The flag.
     */
    public boolean getUseXYCoordinates() {
        return this.useXYCoordinates;
    }

    /**
     * Sets a flag that indicates wether the x and y coordinate will be used
     * to calculate the top left point of the annotation, and notifies
     * registered listeners about the change.
     *
     * @param flag  The flag.
     */
    public void setUseXYCoordinates(boolean flag) {
        this.useXYCoordinates = flag;
        fireAnnotationChanged();
    }

    /**
     * Returns the title that will be rendered at the position and anchor of the
     * annotation.
     *
     * @return The title.
     */
    public Title getTitle() {
        return this.title;
    }

    /**
     * Sets the title that will be rendered at the position and anchor of the
     * annotation, and notifies registered listeners.
     *
     * @param t  THe title.
     */
    public void setTitle(Title t) {
        this.title = t;
        fireAnnotationChanged();
    }

    /**
     * Draws the annotation. This method is called by the drawing code in the
     * {@link XYPlot} class.
     *
     * @param g2  the graphics device.
     * @param plot  the plot.
     * @param dataArea  the data area.
     * @param domainAxis  the domain axis.
     * @param rangeAxis  the range axis.
     * @param rendererIndex  the renderer index.
     * @param info  if supplied, this info object will be populated with
     *              entity information.
     */
    public void draw(Graphics2D g2, XYPlot plot, Rectangle2D dataArea, ValueAxis domainAxis, ValueAxis rangeAxis, int rendererIndex, PlotRenderingInfo info) {
        draw(g2, dataArea, info, rendererIndex);

    }

    /**
     * Draws the annotation. This method is called by the drawing code in the
     * {@link CategoryPlot} class.
     *
     * @param g2  the graphics device.
     * @param plot  the plot.
     * @param dataArea  the data area.
     * @param domainAxis  the domain axis.
     * @param rangeAxis  the range axis.
     */
    public void draw(Graphics2D g2, CategoryPlot plot, Rectangle2D dataArea, CategoryAxis domainAxis, ValueAxis rangeAxis) {
        draw(g2, dataArea, null, 0);
    }

    /**
     * Draws the annotation.
     *
     * @param g2  the graphics device.
     * @param dataArea  the data area.
     * @param info  if supplied, this info object will be populated with
     *              entity information.
     * @param rendererIndex  the renderer index.
     */
    public void draw(Graphics2D g2, Rectangle2D dataArea, PlotRenderingInfo info, int rendererIndex) {
        if(title == null) return;
        Rectangle2D constrained = insets.createInsetRectangle(dataArea,true, true);
        double maxW = constrained.getWidth();
        double maxH = constrained.getHeight();
        if (maxWidthUnitType == UnitType.ABSOLUTE) {
            maxW = Math.min(maxW, maxWidth);
        } else if (maxWidthUnitType == UnitType.RELATIVE) {
            maxW = Math.min(maxW, maxW * maxWidth);
        }
        if (maxHeightUnitType == UnitType.ABSOLUTE) {
            maxH = Math.min(maxH, maxHeight);
        } else if (maxHeightUnitType == UnitType.RELATIVE) {
            maxH = Math.min(maxH, maxH * maxHeight);
        }
        RectangleConstraint rc = new RectangleConstraint(new Range(0, maxW), new Range(0, maxH));
        Size2D size = this.title.arrange(g2, rc);
        Point2D origin = null;
        double xOffset = (xCoordinate <= 1.0) ? xCoordinate * dataArea.getWidth() : xCoordinate;
        double yOffset = (yCoordinate <= 1.0) ? yCoordinate * dataArea.getHeight() : yCoordinate;
        origin = new Point2D.Double(constrained.getX() + xOffset, constrained.getY() + yOffset);

        xOffset = 0;
        yOffset = 0;
        //nothing to do for RectangleAnchor.TOP_LEFT
        if (this.anchor.equals(RectangleAnchor.TOP)) {
            xOffset = -size.width / 2.0;
        } else if (this.anchor.equals(RectangleAnchor.TOP_RIGHT)) {
            xOffset = -size.width;
        } else if (this.anchor.equals(RectangleAnchor.LEFT)) {
            yOffset = -size.height / 2.0;
        } else if (this.anchor.equals(RectangleAnchor.CENTER)) {
            xOffset = -size.width / 2.0;
            yOffset = -size.height / 2.0;
        } else if (this.anchor.equals(RectangleAnchor.RIGHT)) {
            xOffset = -size.width;
            yOffset = -size.height / 2.0;
        } else if (this.anchor.equals(RectangleAnchor.BOTTOM_LEFT)) {
            yOffset = -size.height;
        } else if (this.anchor.equals(RectangleAnchor.BOTTOM)) {
            xOffset = -size.width / 2.0;
            yOffset = -size.height;
        } else if (this.anchor.equals(RectangleAnchor.BOTTOM_RIGHT)) {
            xOffset = -size.width;
            yOffset = -size.height;
        }
        Rectangle2D titleRect = new Rectangle2D.Double(origin.getX() + xOffset, origin.getY() + yOffset, size.getWidth(), size.getHeight());
        this.title.draw(g2, titleRect);
        super.addEntity(info, titleRect, rendererIndex, getToolTipText(), getURL());
    }
}