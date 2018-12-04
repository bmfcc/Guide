package com.iscte.guide;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.iscte.guide.models.Exhibition;

import java.util.HashMap;
import java.util.List;

/**
 * Created by b.coitos on 11/14/2018.
 */

public class ExhibitionListAdapter extends BaseExpandableListAdapter {
    private Context context;
    private List<String> listExhibitionNames;
    private HashMap<String,Exhibition> exhibitionInfo;

    public ExhibitionListAdapter(Context context, List<String> listExhibitionNames, HashMap<String,Exhibition> exhibitionInfo) {

        this.context=context;
        this.listExhibitionNames=listExhibitionNames;
        this.exhibitionInfo=exhibitionInfo;

    }

    @Override
    public int getGroupCount() {
        return this.listExhibitionNames.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return 1;
    }

    @Override
    public Object getGroup(int groupPosition) {
        return this.listExhibitionNames.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return this.exhibitionInfo.get(this.listExhibitionNames.get(groupPosition));
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        String headerTitle = (String) getGroup(groupPosition);
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.exhibition_list_header, null);
        }

        TextView lblListHeader = (TextView) convertView
                .findViewById(R.id.exhibitionHeader);
        lblListHeader.setTypeface(null, Typeface.BOLD);
        lblListHeader.setText(headerTitle);

        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {

        final Exhibition exhibition = (Exhibition) getChild(groupPosition, childPosition);
        final int groupPositionFinal = groupPosition;

        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.exhibition_list_item, null);
        }

        TextView calendarEx = (TextView) convertView
                .findViewById(R.id.exhibitionCalendar);

        calendarEx.setText(exhibition.getCalendar());

        convertView.findViewById(R.id.plusImageButton).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Exhibition ex = exhibitionInfo.get(listExhibitionNames.get(groupPositionFinal));
                Intent intent = new Intent(context, ExhibitionInfo.class);
                intent.putExtra("exhibition",ex.getId());
                context.startActivity(intent);
            }
        });

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
