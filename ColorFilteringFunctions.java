interface ColorChanger {
   int filter(int color);
}

class ColorInvert implements ColorChanger{

    @Override
    public int filter(int color) {

        //using exclusive or paint mode works as well

        return color ^ 0xffffff;
    }
}

class Grayify implements ColorChanger {

    @Override
    public int filter(int color) {
        int b = color & 0xff;
        color >>= 8;
        int g = color & 0xff;
        color >>= 8;
        int r = color & 0xff;
        int avg = averageInt(r, g, b);
        color = color<<16 & 0xff000000 | avg | avg<<8 | avg<<16;
        return color;
    }

    public int averageInt(int i1, int i2, int i3) {
        //time for averaging through division: 219 ms and 84ms
//        int i4 = (i1 + i2 + i3) / 3;
//        return i4;


        /**
        time for averaging through binary: 204 ms and 74ms
        Not as accurate as normal averaging
        I used this inaccurate, but fast way because it doesn't visual look different from a normal grayscale.
        It also makes it much faster on larger images and takes less long on slow computers.
         **/

        //extension cus time downs by 4 secs (78ms) (with three conditionals)
        //(~78-79ms) (with one extra)

        int i5 = i1;
        int i6 = i2;
        if(i1 < i2) {
            i5 = i2;
            i6 = i1;
        }

        if(i5 < i3) {
            i1 = i5;
            i5 = i3;
            i3 = i1;
        }

        if(i6 > i3) {
            i2 = i6;
            i6 = i3;
            i3 = i2;
        }


        int i4 = i5 + i6;
        // int length 32 bits
        i4 >>=1;
        i4 += i3;
        return i4>>1;
    }
}
