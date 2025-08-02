package project

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.io.ByteArrayOutputStream
import java.io.PrintStream
import org.example.display.D5700_Display

class DisplayTest {

    @Test
    fun `test setPixel sets correct pixel and calls render`() {
        val display = D5700_Display(2, 3)

        // Capture output from render
        val output = ByteArrayOutputStream()
        val originalOut = System.out
        System.setOut(PrintStream(output))

        display.setPixel(1, 1, 'X')

        System.setOut(originalOut)
        val outputString = output.toString().trim().replace("\r", "")
        val lines = outputString.split("\n")

        assertEquals(4, lines.size)
        assertEquals("000", lines[1])
        assertEquals("0X0", lines[2])
    }

    @Test
    fun `test setPixel throws IndexOutOfBoundsException for invalid coordinates`() {
        val display = D5700_Display(2, 3)

        val exception = assertThrows(IndexOutOfBoundsException::class.java) {
            display.setPixel(3, 1, 'X') // x out of bounds
        }
        assertTrue(exception.message!!.contains("out of bounds"))

        val exception2 = assertThrows(IndexOutOfBoundsException::class.java) {
            display.setPixel(0, 2, 'X') // y out of bounds
        }
        assertTrue(exception2.message!!.contains("out of bounds"))

        val exception3 = assertThrows(IndexOutOfBoundsException::class.java) {
            display.setPixel(-1, 0, 'X') // x negative
        }
        assertTrue(exception3.message!!.contains("out of bounds"))

        val exception4 = assertThrows(IndexOutOfBoundsException::class.java) {
            display.setPixel(1, -1, 'X') // y negative
        }
        assertTrue(exception4.message!!.contains("out of bounds"))
    }

    @Test
    fun `test render outputs full frameBuffer`() {
        val display = D5700_Display(2, 2)
        display.setPixel(0, 0, 'A')
        display.setPixel(1, 0, 'B')
        display.setPixel(0, 1, 'C')
        display.setPixel(1, 1, 'D')

        val output = ByteArrayOutputStream()
        val originalOut = System.out
        System.setOut(PrintStream(output))

        display.render()

        System.setOut(originalOut)
        val expected = "==\nAC\nBD\n=="
        val actual = output.toString().trim().replace("\r", "")
        assertEquals(expected, actual)
    }

}
