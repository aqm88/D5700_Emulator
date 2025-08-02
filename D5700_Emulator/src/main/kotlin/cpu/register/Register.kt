package org.example.cpu.register

class Register(
    sizeInBits: Int,
    data: Int = 0
) {
    private val sizeInBits: Int = sizeInBits
    var data: Int = data
        private set

    fun setData(newData: Int) {
        if (newData < 0 || newData >= (1 shl sizeInBits)) {
            throw IllegalArgumentException("Value $newData is out of bounds for a register of size $sizeInBits bits")
        }
        data = newData
    }

}