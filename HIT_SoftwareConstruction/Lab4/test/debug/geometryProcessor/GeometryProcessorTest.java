package geometryProcessor;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class GeometryProcessorTest {
	@Test
	public void testColor() {
		Shape a = new Circle(10, "Red", "Circle");
		Shape b = new Triangle(10, "Red", "Triangle");
		Shape c = new Square(10, "Yellow", "Square");
		assertEquals(a.getColour(), "Red");
		assertEquals(b.getColour(), "Red");
		assertEquals(c.getColour(), "Yellow");
		a.setColour("blue");
		b.setColour("blue");
		c.setColour("blue");
		assertEquals(a.getColour(), "blue");
		assertEquals(b.getColour(), "blue");
		assertEquals(c.getColour(), "blue");
	}

	@Test
	public void testArea() {
		Shape a = new Circle(10, "Red", "Circle");
		Shape b = new Triangle(10, "Red", "Triangle");
		Shape c = new Square(10, "Yellow", "Square");
		assertTrue(Math.abs(100 * Math.PI - a.getArea()) < 0.01);
		assertTrue(Math.abs(b.getArea() - 86.60254037844386) < 0.01);
		assertTrue(Math.abs(c.getArea() - 100) < 0.01);
		a.setLength(20);
		b.setLength(20);
		c.setLength(20);
		assertTrue(Math.abs(400 * Math.PI - a.getArea()) < 0.01);
		assertTrue(Math.abs(b.getArea() - 86.60254037844386*4) < 0.01);
		assertTrue(Math.abs(c.getArea() - 400) < 0.01);
	}
	
}
