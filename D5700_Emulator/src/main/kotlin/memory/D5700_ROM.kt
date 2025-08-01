package org.example.memory

class D5700_ROM(data: Array<Int>): Memory(4096) {
    init {
        require(data.size == 4096) {
            "D5700_ROM must be initialized with exactly 4096 values, but got ${data.size}"
        }
    }
    override val data: Array<Int> = data.copyOf()

    override fun read(address: Int): Int {
        return if (address in 0 until data.size) {
            data[address]
        } else {
            throw IndexOutOfBoundsException("Address $address is out of bounds for D5700_ROM")
        }
    }

    override fun write(address: Int, value: Int) {
        throw UnsupportedOperationException("D5700_ROM is read-only and cannot be written to")
    }
}