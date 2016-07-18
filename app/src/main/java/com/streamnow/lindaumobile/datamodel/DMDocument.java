package com.streamnow.lindaumobile.datamodel;

import com.streamnow.lindaumobile.interfaces.IMenuPrintable;
import com.streamnow.lindaumobile.lib.LDConnection;
import com.streamnow.lindaumobile.utils.Lindau;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/** !
 * Created by Miguel Estévez on 09/2/16.
 */
public class DMDocument extends DMElement implements IMenuPrintable
{
    public String id;
    public String name;
    public String description;
    public String lastUpdate;
    public String version;
    public String issuer;
    public String link;

    public DMDocument(JSONObject o)
    {
        this.elementType = DMElementType.DMElementTypeDocument;

        try
        {
            if(!o.isNull("id")) this.id = o.getString("id");
            if(!o.isNull("name")) this.name = o.getString("name");
            if(!o.isNull("description")) this.description = o.getString("description");
            if(!o.isNull("last_update")) this.lastUpdate = o.getString("lastUpdate");
            if(!o.isNull("issuer")) this.issuer = o.getString("issuer");
            if(!o.isNull("link")) this.link = o.getString("link");
        }
        catch(JSONException e)
        {
            e.printStackTrace();
        }
    }

    public static ArrayList<DMDocument> documentsWithArray(JSONArray array)
    {
        ArrayList<DMDocument> documents = new ArrayList<>();

        for( int i = 0; i < array.length(); i++ )
        {
            try
            {
                JSONObject object = array.getJSONObject(i);
                documents.add(new DMDocument(object));
            }
            catch( JSONException e )
            {
                e.printStackTrace();
            }
        }
        return documents;
    }

    @Override
    public String getIconUrlString()
    {
        return LDConnection.getAbsoluteUrl("getImage") + "?access_token=" + Lindau.getInstance().getCurrentSessionUser().accessToken + "&item_id=" + this.id;
    }

    @Override
    public String getRowTitleText()
    {
        return this.name;
    }
}
