package org.example.display

class D5700_Display(height:Int = 8, width:Int = 8 ): Display<Char>(height, width) {

    init {
        frameBuffer = Array<Char>(height * width) { '0' }
        for (i in frameBuffer.indices) {
            frameBuffer[i] = '0'
        }
    }

    override fun setPixel(x: Int, y: Int, newValue: Char) {
        if (x < 0 || x >= width || y < 0 || y >= height) {
            throw IndexOutOfBoundsException("Pixel coordinates ($x, $y) are out of bounds for display of size $width x $height")
        }
        val index = x * width + y
        frameBuffer[index] = newValue
        render()
    }

    override fun render() {
        println()
        for (i in 0 until width) {
            print("=")
        }
        println()
        for (y in 0 until height) {
            for (x in 0 until width) {
                val index = y * width + x
                print(frameBuffer[index])
            }
            println()
        }
        for (i in 0 until width) {
            print("=")
        }
        println()
    }
}