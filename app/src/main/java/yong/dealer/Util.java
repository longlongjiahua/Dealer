package yong.dealer;

import java.util.Comparator;

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
}