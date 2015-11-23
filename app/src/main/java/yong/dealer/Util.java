package yong.dealer;

import java.util.Comparator;
//https://wolfpaulus.com/journal/android-journal/android-and-ocr/

class Util {


    public class PositionComparator implements Comparator<FoursquareVenue> {

        public PositionComparator() {

        }

        @Override
        public int compare(FoursquareVenue lhs, FoursquareVenue rhs) {
            int lhsDist = lhs.getDistance();
            int rhsDist = rhs.getDistance();
            if (lhsDist < rhsDist)
                return -1;
            else if (lhsDist == rhsDist)
                return 0;
            else
                return 1;

        }

    }
    /*
    protected void ocr() {

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 2;
        Bitmap bitmap = BitmapFactory.decodeFile(IMAGE_PATH, options);

        try {
            ExifInterface exif = new ExifInterface(IMAGE_PATH);
            int exifOrientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

            Log.v(LOG_TAG, "Orient: " + exifOrientation);

            int rotate = 0;
            switch (exifOrientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    rotate = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    rotate = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    rotate = 270;
                    break;
            }

            Log.v(LOG_TAG, "Rotation: " + rotate);

            if (rotate != 0) {

                // Getting width & height of the given image.
                int w = bitmap.getWidth();
                int h = bitmap.getHeight();

                // Setting pre rotate
                Matrix mtx = new Matrix();
                mtx.preRotate(rotate);

                // Rotating Bitmap
                bitmap = Bitmap.createBitmap(bitmap, 0, 0, w, h, mtx, false);
                // tesseract req. ARGB_8888
                bitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
            }

        } catch (IOException e) {
            Log.e(LOG_TAG, "Rotate or coversion failed: " + e.toString());
        }

        ImageView iv = (ImageView) findViewById(R.id.image);
        iv.setImageBitmap(bitmap);
        iv.setVisibility(View.VISIBLE);

        Log.v(LOG_TAG, "Before baseApi");

        TessBaseAPI baseApi = new TessBaseAPI();
        baseApi.setDebug(true);
        baseApi.init(DATA_PATH, LANG);
        baseApi.setImage(bitmap);
        String recognizedText = baseApi.getUTF8Text();
        baseApi.end();

        Log.v(LOG_TAG, "OCR Result: " + recognizedText);

        // clean up and show
        if (LANG.equalsIgnoreCase("eng")) {
            recognizedText = recognizedText.replaceAll("[^a-zA-Z0-9]+", " ");
        }
        if (recognizedText.length() != 0) {
            ((TextView) findViewById(R.id.field)).setText(recognizedText.trim());
        }
    }
    */
}