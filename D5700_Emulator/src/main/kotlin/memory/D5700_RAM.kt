package org.example.memory

class D5700_RAM : Memory(4096) {
    override val data = Array<Int>(4096) { 0 }
    override fun read(address: Int): Int {
        return if (address in 0 until data.size) {
            data[address]
        } else {
            throw IndexOutOfBoundsException("Address $address is out of bounds for D5700_RAM")
        }
    }

    override fun write(address: Int, value: Int) {
        if (value !in 0..255) {
            throw IllegalArgumentException("Value $value is not able to be stored in one byte (expected 0x00 to 0xFF)")
        }
        if (address in 0 until data.size) {
            data[address] = value
        } else {
            throw IndexOutOfBoundsException("Address $address is out of bounds for D5700_RAM")
        }
    }
}