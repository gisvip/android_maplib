/******************************************************************************
 * Project:  NextGIS mobile
 * Purpose:  Mobile GIS for Android.
 * Author:   Dmitry Baryshnikov (aka Bishop), polimax@mail.ru
 ******************************************************************************
 *   Copyright (C) 2014 NextGIS
 *
 *    This program is free software: you can redistribute it and/or modify
 *    it under the terms of the GNU General Public License as published by
 *    the Free Software Foundation, either version 2 of the License, or
 *    (at your option) any later version.
 *
 *    This program is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU General Public License for more details.
 *
 *    You should have received a copy of the GNU General Public License
 *    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 ****************************************************************************/
package com.nextgis.maplib.datasource;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import static com.nextgis.maplib.util.GeoConstants.GTPolygon;


public class GeoPolygon
        extends GeoGeometry
{

    protected GeoLinearRing mOuterRing;
    protected List<GeoLinearRing> mInnerRings;


    public GeoPolygon()
    {
        mOuterRing = new GeoLinearRing();
        mInnerRings = new ArrayList<>();
    }


    public void add(GeoPoint point)
    {
        mOuterRing.add(point);
    }


    public GeoPoint remove(int index)
    {
        return mOuterRing.remove(index);
    }


    @Override
    protected boolean rawProject(int toCrs)
    {
        return mOuterRing.rawProject(toCrs);
    }


    @Override
    public GeoEnvelope getEnvelope()
    {
        return mOuterRing.getEnvelope();
    }


    @Override
    public JSONArray coordinatesToJSON()
            throws JSONException
    {
        JSONArray coordinates = new JSONArray();
        coordinates.put(mOuterRing.coordinatesToJSON());
//TODO: add inner rings
        return coordinates;
    }


    @Override
    public int getType()
    {
        return GTPolygon;
    }


    @Override
    public void setCoordinatesFromJSON(JSONArray coordinates)
            throws JSONException
    {
        JSONArray outerRingCoordinates = coordinates.getJSONArray(0);

        if (outerRingCoordinates.length() < 4) {
            throw new JSONException(
                    "For type \"Polygon\", the \"coordinates\" member must be an array of LinearRing coordinate arrays. A LinearRing must be with 4 or more positions.");
        }

        mOuterRing.setCoordinatesFromJSON(outerRingCoordinates);

        if (!getOuterRing().isClosed()) {
            throw new JSONException(
                    "For type \"Polygon\", the \"coordinates\" member must be an array of LinearRing coordinate arrays. The first and last positions of LinearRing must be equivalent (they represent equivalent points).");
        }

        //TODO: inner rings
    }


    public GeoLinearRing getOuterRing()
    {
        return mOuterRing;
    }


    @Override
    public String toWKT(boolean full)
    {
        StringBuilder buf = new StringBuilder();
        if(full)
            buf.append("POLYGON ");
        if (mOuterRing.getPoints().size() == 0)
            buf.append(" EMPTY");
        else {
            buf.append("(");
            buf.append(mOuterRing.toWKT(false));
            if(mInnerRings.size() > 0) {
                buf.append(", ");
                for (int i = 0; i < mInnerRings.size(); i++) {
                    GeoLinearRing ring = mInnerRings.get(i);
                    buf.append(ring.toWKT(false));
                }
            }
            buf.append(")");
        }
        return buf.toString();
    }
}