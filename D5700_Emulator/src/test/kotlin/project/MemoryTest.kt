package org.example.project

import org.example.memory.D5700_RAM
import org.example.memory.D5700_ROM
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class MemoryTest {

    private val ram = D5700_RAM()

    @Test
    fun `write and read valid byte within range`() {
        ram.write(100, 42)
        val result = ram.read(100)
        Assertions.assertEquals(42, result)
    }

    @Test
    fun `write throws if value is greater than 0xFF`() {
        val ram = D5700_RAM()
        val exception = assertThrows<IllegalArgumentException> {
            ram.write(0, 256)
        }
        Assertions.assertEquals("Value 256 is not able to be stored in one byte (expected 0x00 to 0xFF)", exception.message)
    }

    @Test
    fun `write throws for value too low`() {
        val exception = assertThrows<IllegalArgumentException> {
            ram.write(0, -1)
        }
        Assertions.assertEquals("Value -1 is not able to be stored in one byte (expected 0x00 to 0xFF)", exception.message)
    }

    @Test
    fun `write throws for address out of bounds RAM`() {
        val exception1 = assertThrows<IndexOutOfBoundsException> {
            ram.write(4096, 0)
        }
        Assertions.assertEquals("Address 4096 is out of bounds for D5700_RAM", exception1.message)

        val exception2 = assertThrows<IndexOutOfBoundsException> {
            ram.write(-1, 0)
        }
        Assertions.assertEquals("Address -1 is out of bounds for D5700_RAM", exception2.message)
    }

    @Test
    fun `read throws for address out of bounds RAM`() {
        val exception1 = assertThrows<IndexOutOfBoundsException> {
            ram.read(4096)
        }
        Assertions.assertEquals("Address 4096 is out of bounds for D5700_RAM", exception1.message)
        val exception2 = assertThrows<IndexOutOfBoundsException> {
            ram.read(-1)
        }
        Assertions.assertEquals("Address -1 is out of bounds for D5700_RAM", exception2.message)
    }

    @Test
    fun `constructs with valid 4096-length data`() {
        val inputData = Array(4096) { it }
        val rom = D5700_ROM(inputData)
        Assertions.assertEquals(123, rom.read(123)) // sanity check
    }

    @Test
    fun `constructor throws if data size is not 4096`() {
        val badData = Array(100) { 0 }
        val exception = assertThrows<IllegalArgumentException> {
            D5700_ROM(badData)
        }
        Assertions.assertEquals(
            "D5700_ROM must be initialized with exactly 4096 values, but got 100",
            exception.message
        )
    }

    @Test
    fun `read throws if address is out of bounds ROM`() {
        val rom = D5700_ROM(Array(4096) { 0 })
        val exception = assertThrows<IndexOutOfBoundsException> {
            rom.read(4096)
        }
        Assertions.assertEquals(
            "Address 4096 is out of bounds for D5700_ROM",
            exception.message
        )
        val exception2 = assertThrows<IndexOutOfBoundsException> {
            rom.read(-1)
        }
        Assertions.assertEquals(
            "Address -1 is out of bounds for D5700_ROM",
            exception2.message
        )

    }

    @Test
    fun `write always throws UnsupportedOperationException`() {
        val rom = D5700_ROM(Array(4096) { 0 })
        val exception = assertThrows<UnsupportedOperationException> {
            rom.write(0, 1)
        }
        Assertions.assertEquals(
            "D5700_ROM is read-only and cannot be written to",
            exception.message
        )
    }
}