package com.healthymedium.arc.path_data;

import com.healthymedium.arc.api.tests.data.BaseData;
import com.healthymedium.arc.study.PathSegmentData;

public class SetupPathData extends PathSegmentData {

    public String id;

    public SetupPathData(){
        super();
    }

    @Override
    protected BaseData onProcess() {
        return null;
    }
}
