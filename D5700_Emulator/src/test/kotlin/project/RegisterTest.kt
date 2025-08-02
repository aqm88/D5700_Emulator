package project

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.example.cpu.register.Register

class RegisterTest {

    @Test
    fun `constructor sets initial data correctly`() {
        val register = Register(8, 42)
        assertEquals(42, register.data)
    }

    @Test
    fun `constructor defaults to zero if no data provided`() {
        val register = Register(8)
        assertEquals(0, register.data)
    }

    @Test
    fun `setData sets value within valid range`() {
        val register = Register(4)
        register.setData(0)
        assertEquals(0, register.data)

        register.setData(15) // 2^4 - 1
        assertEquals(15, register.data)
    }

    @Test
    fun `setData throws for value greater than allowed`() {
        val register = Register(4)
        val exception = assertThrows<IllegalArgumentException> {
            register.setData(16) // 2^4
        }
        assertEquals("Value 16 is out of bounds for a register of size 4 bits", exception.message)
    }

    @Test
    fun `setData throws for negative values`() {
        val register = Register(4)
        val exception = assertThrows<IllegalArgumentException> {
            register.setData(-1)
        }
        assertEquals("Value -1 is out of bounds for a register of size 4 bits", exception.message)
    }

    @Test
    fun `data cannot be modified directly`() {
        val register = Register(8, 12)
        // This line won't compile if uncommented, which is the goal:
        // register.data = 42
        assertEquals(12, register.data)
    }
}