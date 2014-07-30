package org.uct.cs.hough.util;

/** SobelAngleClassifier
 * A helper class for precomputing values for the sobel filter.
 *
 * Premise: because atan2 and sqrt are rather complicated operations, this class provides a
 * look up for the appropriate result with constant time array index.
 *
 * 'RES' is the bucket resolution for angles and hypotenuse magnitues:
 * 0 = no scaling... array size is MAX*2 * MAX*2 (megabytes in size)
 * 1 = 2^1 values per bucket
 * 2 = 2^2 ...
 *
 * MAX is 1020 because 255 + 2*255 + 255 = 1020, so array must cover -1020 to 1020
 *
 */
public class SobelAngleClassifier {

    // MAX (and MIN)
    static final int MAX = 1020;
    // power of 2 resolution
    static final int RES = 2;
    // BUCKETS
    static final int BUCKET = (MAX<<1)>>RES;

    // backing arrays
    private static byte[] angles;
    private static byte[] hypclass;

    // Prepare. MUST BE CALLED BEFORE ANY ATTEMPT TO USE THIS CLASS!!!
    public static void prepare()
    {
        // create arrays
        angles = new byte[BUCKET * BUCKET];
        hypclass = new byte[BUCKET * BUCKET];

        // for each slot in the array, pre compute the required values
        for(int y=0;y<BUCKET;y++)
        {
            int offset = y*BUCKET;

            for(int x=0;x<BUCKET;x++)
            {
                int index = offset + x;

                // calculated corrosponding x and y
                int gx = (x<<RES)-MAX;
                int gy = (y<<RES)-MAX;

                // Atan2
                int degrees = (int)(Math.atan2(gy, gx)*180/Math.PI);
                // convert to [0-180] range
                degrees = (degrees + 180) % 180 + 23;

                // catergorise
                byte acat = (byte)0;
                if (degrees < 45) acat = (byte)0;
                else if (degrees < 90) acat = (byte)45;
                else if (degrees < 135) acat = (byte)90;
                else if (degrees < 180) acat = (byte)135;

                angles[index] = acat;

                // calculate magnitude or hypotenuse or whatever
                int gm = (int)Math.sqrt(gy*gy + gx*gx);
                if(gm > 255) gm = 255;

                hypclass[index] = (byte)gm;

            }
        }
    }

    public static byte atan2cat(int gy, int gx) {
        // calculate index slot
        gx+=MAX;        // move into range
        gx=gx>>RES;     // divide into correct bucket
        gy+=MAX;
        gy=gy>>RES;
        return angles[gy*BUCKET + gx];
    }

    public static byte mag(int gy, int gx) {
        // calculate index slot
        gx+=MAX;        // move into range
        gx=gx>>RES;     // divide into correct bucket
        gy+=MAX;
        gy=gy>>RES;
        return hypclass[gy*BUCKET + gx];
    }


}
