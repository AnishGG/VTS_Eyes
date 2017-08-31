package ssadteam5.vtsapp;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

/**
 * Created by ravsimar on 9/1/17.
 */

public class Vehicle implements ClusterItem
{
        private final LatLng mPosition;
        private String mTitle;
        private String mSnippet;
        public Vehicle(double lat, double lng)
        {
            mPosition = new LatLng(lat, lng);
            mTitle = null;
        }
        public Vehicle(double lat, double lng, String title,String snippet)
        {
            mPosition = new LatLng(lat, lng);
            mTitle = title;
            mSnippet = snippet;
        }
        @Override
        public LatLng getPosition()
        {
            return mPosition;
        }
        @Override
        public String getTitle()
        {
            return mTitle;
        }
        @Override
        public String getSnippet()
        {
            return mSnippet;
        }
        /**
         * Set the title of the marker
         * @param title string to be set as title
         */
        public void setTitle(String title)
        {
            mTitle = title;
        }

        /**
         * Set the description of the marker
         * @param snippet string to be set as snippet
         */
        public void setSnippet(String snippet)
        {
            mSnippet = snippet;
        }
}
