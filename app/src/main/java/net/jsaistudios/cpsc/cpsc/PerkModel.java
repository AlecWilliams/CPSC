package net.jsaistudios.cpsc.cpsc;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;

/**
 * Created by Alec on 7/4/2018.
 */

public class PerkModel {
    private String name;
    private String info;
    private DataSnapshot perkDatabaseNode;


    public DataSnapshot getPerkDatabaseNode() {
        return perkDatabaseNode;
    }

    public void setPerkDatabaseNode(DataSnapshot perkDatabaseNode) {
        this.perkDatabaseNode = perkDatabaseNode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

}
